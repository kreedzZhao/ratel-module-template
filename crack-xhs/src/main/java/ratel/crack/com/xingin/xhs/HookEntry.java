package ratel.crack.com.xingin.xhs;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "XHS_HOOK";

    public static String copyFromPlugin(String fileName){
        // 只有加载特定的 lib 才执行
        Context sContext = RatelToolKit.sContext;
        // 通过 content provider 获取 so 文件
        if (sContext == null) {
            Log.e(TAG, "context is null.");
            return null;
        }

        try {

            File injectSo = new File(sContext.getFilesDir(), fileName);
            Uri uri = Uri.parse("content://ratel.crack.com.xingin.xhs/assets/sodir/arm64-v8a/"+fileName);

            ContentResolver contentResolver = sContext.getContentResolver();
            AssetFileDescriptor afd = contentResolver.openAssetFileDescriptor(uri, "r", null);
            // 其实对于正常运行不会有影响，为了健壮性
            if (afd == null) {
                Log.e(TAG, "afterHookedMethod: invalid afd");
                return null;
            }
            if (afd.getLength() > Integer.MAX_VALUE) {
                Log.e(TAG, "afterHookedMethod: file too large");
                return null;
            }
            int aLength = (int) afd.getLength();
            FileInputStream is = afd.createInputStream();
            //  复制到 injectSo 位置
            byte[] bytes = new byte[aLength];
            is.read(bytes, 0, aLength);
            FileOutputStream os = new FileOutputStream(injectSo);
            Log.i(TAG, "afterHookedMethod: load ratel so plugin finished. path: " + injectSo.getAbsolutePath());
            os.write(bytes);
            is.close();
            os.close();
            afd.close();
            return injectSo.getAbsolutePath();
        } catch (Exception e){
            Log.e(TAG, "afterHookedMethod: failed load so plugin");
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
//        System.loadLibrary(); 不能 hook
        RposedHelpers.findAndHookMethod(
                Runtime.getRuntime().getClass(),
                "loadLibrary0",
                Class.class, String.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "beforeHookedMethod: " + param.args[1]);
                        if ("xylog".equals(param.args[1])) {
//                        if ("xyass".equals(param.args[1])) {
                            String soPath = copyFromPlugin("libcrack-xhs.so");
                            copyFromPlugin("libgadget.config.so");
//                            copyFromPlugin("trace.js");
                            String gadgetPath = copyFromPlugin("libgadget.so");
                            if (soPath != null) {
                                System.load(soPath);
                                System.load(gadgetPath);
                            }
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "afterHookedMethod: " + param.args[1]);
                    }
                }
        );


//        RposedBridge.hookAllConstructors(URL.class, new RC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String url = param.thisObject + "";
//                Log.i(tag, "access url:" + url);
//                if (url.contains("homefeed")) {
//                    Log.i(tag, "hint url:", new Throwable());
//                }
//            }
//        });

//        addFloatingButtonForActivity(lpparam);
        Log.i(TAG, "hook end");
    }


    private static void addFloatingButtonForActivity(final RC_LoadPackage.LoadPackageParam lpparam) {
        RposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new RC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                new Handler(Looper.getMainLooper())
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                createAndAttachFloatingButtonOnActivity((Activity) param.thisObject);
                            }
                        }, 1000);
            }

            private void createAndAttachFloatingButtonOnActivity(Activity activity) {
                Context context = RatelToolKit.ratelResourceInterface.createContext(lpparam.modulePath, HookEntry.class.getClassLoader(), RatelToolKit.sContext);

                FrameLayout frameLayout = (FrameLayout) activity.getWindow().getDecorView();
                LayoutInflater.from(context).cloneInContext(context)
                        .inflate(R.layout.float_button, frameLayout);

            }
        });
    }
}
