package ratel.com.yuanrenxue.challenge.handlers;

import android.util.Log;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.rposed.RposedHelpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.AutoBind;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

public class AppOneHandler implements ActionHandler {
    private String TAG = "yrx01";

    @Override
    public String action() {
        return "app01";
    }

    @AutoBind
    private int page;

    public String genSign(int mPage, long tmp){
        Object signObj = RposedHelpers.newInstance(
                RposedHelpers.findClass("o00o0oO0.o00oO0o", RatelToolKit.hostClassLoader)
                );
        StringBuilder sb = new StringBuilder();
        sb.append("page=");
        sb.append(mPage);
        sb.append(tmp);

        Object sign = RposedHelpers.callMethod(signObj, "OooO", sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return sign.toString();
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, final SekiroResponse sekiroResponse) {
        // o00O00OO.OooO00o#OooO0O0
        Object retrofitApiManagerClass = RposedHelpers.callStaticMethod(
                RposedHelpers.findClass("o00O00OO.OooO00o", RatelToolKit.hostClassLoader),
                "OooO0O0"
        );
        Object rfApiManager = RposedHelpers.callMethod(retrofitApiManagerClass, "OooO00o", false);
        // oo0o0O0.OooO0O0#OooOOOO
        Object explorerService = RposedHelpers.callMethod(rfApiManager, "OooOOOO",
                RposedHelpers.findClass("o00o0O0.OooO0o", RatelToolKit.hostClassLoader)
        );
//        RposedHelpers.callMethod()
        long currentTimeMillis = java.lang.System.currentTimeMillis();
        Object observable = RposedHelpers.callMethod(explorerService, "OooO0oo",
                page, genSign(page, currentTimeMillis), currentTimeMillis
        );
        // rxjava observe
        // o0O0000O.o00Ooo
        final Class<?> observerClass = RposedHelpers.findClass("o0O0000O.o00Ooo", RatelToolKit.hostClassLoader);
        Object observer = Proxy.newProxyInstance(RatelToolKit.hostClassLoader, new Class[]{observerClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() != observerClass){
                    return method.invoke(this, args);
                }
                if (method.getName().equals("onNext")){
                    sekiroResponse.success(args[0]);
                    return null;
                }
                if (method.getName().equals("onError")){
                    Throwable error = (Throwable) args[0];
                    Log.e(TAG, "app 01 failed", error);
                    sekiroResponse.failed(-1, error);
                    return null;
                }
                return null;
            }
        });
        RposedHelpers.callMethod(observable, "subscribe", observer);
    }

}
