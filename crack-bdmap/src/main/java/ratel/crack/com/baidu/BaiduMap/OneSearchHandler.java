package ratel.crack.com.baidu.BaiduMap;

import android.os.Handler;
import android.os.Looper;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.sekiro.api.ActionHandler;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroResponse;
import com.virjar.sekiro.api.databind.AutoBind;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class OneSearchHandler implements ActionHandler {
    @Override
    public String action() {
        return "OneSearch";
    }

    @AutoBind(require = true)
    private String keyword;

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        // com.baidu.mapframework.searchcontrol.SearchControl
        final Object oneSearchWrapper = createOneSearchWrapper();
        final Object searchResponseCallback = createSearchResponse(sekiroResponse);

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        RposedHelpers.callStaticMethod(
                                RposedHelpers.findClass("com.baidu.mapframework.searchcontrol.SearchControl", RatelToolKit.hostClassLoader),
                                "searchRequest",
                                oneSearchWrapper, searchResponseCallback
                        );
                    }
                });



    }

    private Object createSearchResponse(final SekiroResponse sekiroResponse){
        Class<?> searchResponseClass = RposedHelpers.findClass("com.baidu.mapframework.searchcontrol.SearchResponse", RatelToolKit.hostClassLoader);
        return Proxy.newProxyInstance(
                searchResponseClass.getClassLoader(),
                new Class[]{searchResponseClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                            if (method.getDeclaringClass().equals(Object.class)){
                                return method.invoke(this, objects);
                            }
                            if (method.getName().equals("onSearchComplete")){
                                // onSearchComplete
                                sekiroResponse.success(ForceFiledViewer.toView(objects[0]));
                            }else if (method.getName().equals("onSearchError")){
                                int errorCode = (int) RposedHelpers.callMethod(objects[0], "getErrorCode");
                                sekiroResponse.failed(-1, String.valueOf(errorCode));
                                // onSearchError
                            }
                            return null;
                    }
                }
        );
    }

    private Object createOneSearchWrapper(){
        // com.baidu.platform.comapi.basestruct.MapBound#MapBound(int, int, int, int)
        Object mapBound = RposedHelpers.newInstance(
          RposedHelpers.findClass("com.baidu.platform.comapi.basestruct.MapBound", RatelToolKit.hostClassLoader),
                (int)12681134, (int)2557455, (int)12682729, (int)2560847
        );
        // com.baidu.platform.comapi.basestruct.Point#Point(double, double)
        Object point = RposedHelpers.newInstance(
          RposedHelpers.findClass("com.baidu.platform.comapi.basestruct.Point", RatelToolKit.hostClassLoader),
                1.2681932E7D, 2559593D
        );
        Map<java.lang.String, java.lang.Object> extParam = new HashMap<>();
        extParam.put("bt", "external_input");
        extParam.put("ext_src", "searchJingang");
        extParam.put("preview", 1);
        return RposedHelpers.newInstance(
                RposedHelpers.findClass("com.baidu.mapframework.provider.search.controller.OneSearchWrapper", RatelToolKit.hostClassLoader),
                keyword, "340", 0,
                mapBound
                , 17,
                point,
                extParam,
                10
                );
    }
}
