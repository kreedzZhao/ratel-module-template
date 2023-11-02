package ratel.com.yuanrenxue.challenge.handlers;

import android.util.Log;

import cn.iinti.sekiro3.business.api.interfaze.Action;
import cn.iinti.sekiro3.business.api.interfaze.AutoBind;
import cn.iinti.sekiro3.business.api.interfaze.RequestHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

@Action("getSign")
public class SighHandler implements RequestHandler {
    public ClassLoader mClassLoader = null;

    @AutoBind
    private Integer page;
    private String TAG = "Kreedz";

    public SighHandler(ClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        sekiroResponse.success(System.currentTimeMillis());
    }
}
