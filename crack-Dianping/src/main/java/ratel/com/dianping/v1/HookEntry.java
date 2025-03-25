package ratel.com.dianping.v1;

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
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;

import external.com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import cn.iinti.sekiro3.business.api.SekiroClient;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "DP_HOOK";

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
            Uri uri = Uri.parse("content://ratel.com.dianping.v1/assets/sodir/arm64-v8a/"+fileName);

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

        addFloatingButtonForActivity(lpparam);
        Log.i(TAG, "hook end");

        RposedHelpers.findAndHookMethod(
                Runtime.getRuntime().getClass(),
                "loadLibrary0",
                Class.class, String.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "beforeHookedMethod: " + param.args[1]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "afterHookedMethod: " + param.args[1]);
//                        if ("mtguard".equals(param.args[1])) {
                        if ("cips".equals(param.args[1])) {
                            String soPath = copyFromPlugin("libcrack-dp.so");
                            copyFromPlugin("libgadget.config.so");
                            copyFromPlugin("trace.js");
                            String gadgetPath = copyFromPlugin("libgadget.so");
                            if (soPath != null) {
                                System.load(soPath);
                                System.load(gadgetPath);
                            }
                        }
                    }
                }
        );

//        startSekiro(lpparam);

//        RposedBridge.hookAllConstructors(URL.class, new RC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String url = param.thisObject + "";
//                Log.i(tag, "access url:" + url);
//                if (url.contains("shop")) {
//                    Log.i(tag, "hint url:", new Throwable());
//                }
//            }
//        });
//         com.dianping.nvnetwork.Request
//        RposedBridge.hookAllConstructors(
//                RposedHelpers.findClass("com.dianping.nvnetwork.Request", lpparam.classLoader),
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.d(tag, "request constructor: " + JSONObject.toJSONString(param.args), new Throwable());
//                    }
//                }
//        );
////         com.dianping.nvnetwork.NVDefaultNetworkService#exec(com.dianping.nvnetwork.Request, com.dianping.nvnetwork.o)
//        RposedBridge.hookAllMethods(
//                RposedHelpers.findClass("com.dianping.nvnetwork.NVDefaultNetworkService", lpparam.classLoader),
//                "exec",
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.d(tag, "NVDefaltNetworkService exec arg1: "+ JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])));
//                        Log.d(tag, "NVDefaltNetworkService exec arg2: "+ JSONObject.toJSONString(param.args[1]));
//                        Log.d(tag, "arg2 type: "+ param.args[1].getClass());
//                    }
//                }
//        );
//        RposedHelpers.findAndHookMethod(
//                "com.dianping.dataservice.mapi.impl.DefaultMApiService.a",
//                lpparam.classLoader,
//                "onRequestFinish",
//                RposedHelpers.findClass("com.dianping.nvnetwork.Request", lpparam.classLoader),
//                RposedHelpers.findClass("com.dianping.nvnetwork.q", lpparam.classLoader),
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.d(tag, "resposne: " + JSONObject.toJSONString(ForceFiledViewer.toView(param.args[1])));
//                    }
//                }
//        );

        RposedBridge.hookAllConstructors(
                RposedHelpers.findClass("com.dianping.dataservice.mapi.impl.DefaultMApiService.a", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "enter DefaultMapi");
                        if (param.args[3] != null){
                            Log.d(TAG, "decrypt class: "+ param.args[3].getClass());
                        }
                    }
                }
        );

        // com.dianping.picasso.commonbridge.MapiModule#resolveData(com.dianping.archive.DPObject, boolean, int)
        RposedHelpers.findAndHookMethod(
                "com.dianping.picasso.commonbridge.MapiModule",
                lpparam.classLoader,
                "resolveData",
                RposedHelpers.findClass("com.dianping.archive.DPObject", lpparam.classLoader),
                boolean.class,
                int.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "resolveData arg0: "+ JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])));
                        Log.d(TAG, "resolveData arg1: "+ param.args[1]);
                        Log.d(TAG, "resolveData arg2: "+ param.args[2]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.d(TAG, "resolveData: " + param.getResult());
                    }
                }
        );

    }

    private static void startSekiro(RC_LoadPackage.LoadPackageParam lpparam) {
        new SekiroClient("DP", "pixel4", "192.168.0.106", 5620)
                .setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) ->
                        // 注册一个接口，名为testAction
                        handlerRegistry.registerSekiroHandler(new ActionHandler() {
                            @Override
                            public String action() {
                                return "testAction";
                            }

                            @Override
                            public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
                                // 接口处理逻辑，我们不做任何处理，直接返回字符串：ok
                                Log.d(TAG, "sekiro received. ");
                                sekiroResponse.success("ok");
                            }
                        })
                ).start();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
