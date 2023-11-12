package ratel.com.yuanrenxue.challenge;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import cn.iinti.sekiro3.business.api.SekiroClient;
import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.HandlerRegistry;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequestInitializer;
import ratel.com.yuanrenxue.challenge.handlers.AppOneHandler;
import ratel.com.yuanrenxue.challenge.handlers.SighHandler;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "yrx";
    private static final String clientId = Build.BRAND + "_"+Build.MODEL.replace(" ", "");
    private String soName = "libratel-so.so";


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(TAG, "hook end");

//        RposedBridge.hookAllConstructors(URL.class, new RC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                String url = param.thisObject + "";
//                Log.i(TAG, "access url:" + url);
//                if (url.contains("app")) {
//                    Log.i(TAG, "hint url:", new Throwable());
//                }
//            }
//        });
            hook_native(lpparam);





//        if (lpparam.packageName != "com.yuanrenxue.challenge") {
//            return;
//        }

        SekiroClient sekiroClient = new SekiroClient("yrx", clientId, "192.168.0.105", 5612);
        sekiroClient.setupSekiroRequestInitializer(new SekiroRequestInitializer() {
            @Override
            public void onSekiroRequest(SekiroRequest sekiroRequest, HandlerRegistry handlerRegistry) {
                handlerRegistry.registerSekiroHandler(new SighHandler(lpparam.classLoader));
                handlerRegistry.registerSekiroHandler(new AppOneHandler());
            }
        });
        sekiroClient.start();
    }

    // android 10 nativeLoad
    public void hook_native(RC_LoadPackage.LoadPackageParam lpparam){
//        RposedHelpers.findAndHookMethod(
//                "java.lang.Runtime",
//                lpparam.classLoader,
//                "nativeLoad",
//                String.class, ClassLoader.class, Class.class,
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "nativeLoad before hook arg0: "+param.args[0]);
//                    }
//                }
//        );
        // hook loadLibrary0
        RposedHelpers.findAndHookMethod(
                "java.lang.Runtime",
                lpparam.classLoader,
                "loadLibrary0",
                Class.class, String.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "loadLibrary0 before hook ["+param.args[1]+"]");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.d(TAG, "loadLibrary0 after hook ["+param.args[1]+"]");
                        if ("three".equals(param.args[1])){
                            // 目标 so 才进行加载
                            String libName = param.args[1].toString();
                            Context appContext = RatelToolKit.sContext;
                            try {
                                // 获取到插件中的 so 文件
                                if (appContext == null){
                                    Log.d(TAG, "context is null after hook loadLibrary0");
                                    return;
                                }
                                // /data/user/0/com.yuanrenxue.challenge/files
                                File injectSoFile = new File(appContext.getFilesDir(), soName);
                                Log.d(TAG, "Target app dir: " + appContext.getFilesDir());
                                Uri uri = Uri.parse("content://ratel.com.yuanrenxue.challenge/assets/arm64-v8a/"+soName);
                                // 获取成功，写入
                                ContentResolver contentResolver = appContext.getContentResolver();
                                AssetFileDescriptor afd = contentResolver.openAssetFileDescriptor(uri, "r", null);
                                if (afd == null){
                                    Log.d(TAG, "Get so from ratel plugin failed ["+soName+"]");
                                    return;
                                }
                                if (afd.getLength() > Integer.MAX_VALUE){
                                    Log.d(TAG, "Get so from ratel plugin too large ["+soName+"]");
                                    return;
                                }
                                int fLen = (int) afd.getLength();
                                FileInputStream is = afd.createInputStream();
                                byte[] cache = new byte[fLen];
                                is.read(cache, 0, fLen);
                                FileOutputStream os = new FileOutputStream(injectSoFile);
                                os.write(cache);
                                is.close();
                                os.close();
                                afd.close();
                                Log.i(TAG, "Load so success ["+soName+"]");

                                System.load(injectSoFile.getAbsolutePath());
                                // loadLibrary 不行
//                                System.loadLibrary(soName);

                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e(TAG, "receive file error: "+ e.getMessage());
                            }
                        }
                    }
                }
        );
    }

    public void hook_29(RC_LoadPackage.LoadPackageParam lpparam){
        // app 29
        // com.yuanrenxue.challenge.fragment.challenge.ChallengeTwentyNineFragment.OooO0O0#apply

        // o0O00O0.OooO00o#OooO0O0
        RposedHelpers.findAndHookMethod(
                "o0O00O0.OooO00o",
                lpparam.classLoader,
                "OooO0O0",
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "app 29 finger: "+ JSONObject.toJSONString(ForceFiledViewer.toView(param.getResult())), new Throwable());
                    }
                }
        );

//        RposedHelpers.findAndHookConstructor(
//                "com.yuanrenxue.challenge.core.http.entity.C29CBean",
//                lpparam.classLoader,
//                new RC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        Log.i(TAG, "C29CBean: "+JSONObject.toJSONString(ForceFiledViewer.toView(param.getResult())), new Throwable());
//                    }
//                }
//        );

        // com.yuanrenxue.challenge.fragment.challenge.ChallengeTwentyNineFragment.OooO00o#onNext
        RposedHelpers.findAndHookMethod(
                "com.yuanrenxue.challenge.fragment.challenge.ChallengeTwentyNineFragment.OooO00o",
                lpparam.classLoader,
                "onNext",
                RposedHelpers.findClass("com.yuanrenxue.challenge.core.http.entity.ChallengeOneResultBean", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "ChallengeOneResultBean: "+JSONObject.toJSONString(param.args[0]));
                    }
                }
        );

        // com.yuanrenxue.challenge.fragment.challenge.ChallengeTwentyNineFragment.OooO0OO.OooO00o#OooO00o(com.yuanrenxue.challenge.core.http.entity.C29CBean)
        RposedHelpers.findAndHookMethod(
                "com.yuanrenxue.challenge.fragment.challenge.ChallengeTwentyNineFragment.OooO0OO.OooO00o",
                lpparam.classLoader,
                "OooO00o",
                RposedHelpers.findClass("com.yuanrenxue.challenge.core.http.entity.C29CBean", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "C29CBean: "+ JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])));
                    }
                }
        );
    }

    public void hook_01(RC_LoadPackage.LoadPackageParam lpparam){
        // app 01 page=11699684192629
        // o00o0oO0.o00oO0o#OooO
        RposedHelpers.findAndHookMethod(
                "o00o0oO0.o00oO0o",
                lpparam.classLoader,
                "OooO",
                byte[].class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        byte[] input = (byte[]) param.args[0];
                        Log.i(TAG, "app 01 : " + new String(input));
                    }
                }
        );
    }

}
