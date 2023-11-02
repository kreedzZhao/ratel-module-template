package ratel.com.yuanrenxue.challenge;

import android.os.Build;
import android.util.Log;

import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;


import cn.iinti.sekiro3.business.api.SekiroClient;
import cn.iinti.sekiro3.business.api.interfaze.HandlerRegistry;
import cn.iinti.sekiro3.business.api.interfaze.RequestHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequestInitializer;
import ratel.com.yuanrenxue.challenge.handlers.SighHandler;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String tag = "Kreedz";
    private static final String clientId = Build.BRAND + "_"+Build.MODEL.replace(" ", "");


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
        Log.i(tag, "hook end");

//        if (lpparam.packageName != "com.yuanrenxue.challenge") {
//            return;
//        }

        SekiroClient sekiroClient = new SekiroClient("demo", clientId, "192.168.0.106", 5612);
        sekiroClient.setupSekiroRequestInitializer(new SekiroRequestInitializer() {
            @Override
            public void onSekiroRequest(SekiroRequest sekiroRequest, HandlerRegistry handlerRegistry) {
                handlerRegistry.registerSekiroHandler(new SighHandler(lpparam.classLoader));
            }
        });
        sekiroClient.start();
    }


}
