package ratel.crack.com.autonavi.minimap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;

import java.net.URL;

import cn.iinti.sekiro3.business.api.SekiroClient;
import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.HandlerRegistry;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequestInitializer;
import ratel.crack.com.autonavi.minimap.handlers.KeywordSearchHandler;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "GD_HOOK";
    private static final String clientId = Build.BRAND + "_" + Build.MODEL.replace(" ", "");

    public static void loge(String tag, String msg) {
        if (tag == null || tag.length() == 0 || msg == null || msg.length() == 0) {
            return;
        }
        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            Log.e(tag, msg);
        } else {
            while (msg.length() > segmentSize) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                Log.i(tag, logContent);
            }
            Log.i(tag, msg);// 打印剩余日志
        }

    }


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {


        startSekiro(lpparam);


        addFloatingButtonForActivity(lpparam);
        Log.i(TAG, "hook end");
    }

    public static void startSekiro(RC_LoadPackage.LoadPackageParam lpparam) {
       if (!lpparam.packageName.equals(lpparam.processName)){
           return;
       }
        SekiroClient client = new SekiroClient("gdmap", clientId, "172.16.12.40", 5612);
       client.setupSekiroRequestInitializer(
               new SekiroRequestInitializer() {
                   @Override
                   public void onSekiroRequest(SekiroRequest sekiroRequest, HandlerRegistry handlerRegistry) {
                     handlerRegistry.registerSekiroHandler(new KeywordSearchHandler());
                   }
               }
       );
       client.start();

    }

    private void backup(RC_LoadPackage.LoadPackageParam lpparam){
        // com.amap.network.http.HttpService#sendAos
        RposedBridge.hookAllMethods(
                RposedHelpers.findClass("com.amap.network.http.HttpService", lpparam.classLoader),
                "sendAos",
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        loge(TAG, "sendAos: " + JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])));
                        Object getOption = RposedHelpers.callMethod(param.args[0], "getOption");
                        Log.d(TAG, "sendAos.getOption: " + JSONObject.toJSONString(getOption));
                        Object getBody = RposedHelpers.callMethod(param.args[0], "getBody");
                        Object params = RposedHelpers.callMethod(getBody, "getParams");
                        Log.i(TAG, "getBody.getParams: "+JSONObject.toJSONString(params));
                    }
                }
        );
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
