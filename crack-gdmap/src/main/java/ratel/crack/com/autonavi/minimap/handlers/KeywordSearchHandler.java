package ratel.crack.com.autonavi.minimap.handlers;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.RposedHelpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.AutoBind;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;
import ratel.crack.com.autonavi.minimap.HookEntry;

public class KeywordSearchHandler implements ActionHandler {
    private static String TAG = "GD_HOOK";
    @Override
    public String action() {
        return "KeywordSearch";
    }

    @AutoBind(require = true)
    private String keyWord;

    @AutoBind
    private int page;

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        final Object aosRequest = createAosRequest();
        final Object searchResponseCallback = createSearchResponseCallback(sekiroResponse);

        new Handler(Looper.getMainLooper()).post(
                new Runnable() {
                    @Override
                    public void run() {
                        // com.amap.network.http.HttpService#sendAos
                        Object httpService = RposedHelpers.newInstance(
                                RposedHelpers.findClass("com.amap.network.http.HttpService", RatelToolKit.hostClassLoader)
                                );
                        int sentRequestId = (int) RposedHelpers.callMethod(
                                httpService,
                                "sendAos", aosRequest, searchResponseCallback
                        );
                        Log.i(TAG, "sentRequestId: " + sentRequestId);
                    }
                }
        );

    }

    public Object createSearchResponseCallback(final SekiroResponse sekiroResponse){
        // com.amap.network.api.http.callback.Callback
        Class<?> searchResponseClass = RposedHelpers.findClass("com.amap.network.api.http.callback.Callback", RatelToolKit.hostClassLoader);
        return Proxy.newProxyInstance(searchResponseClass.getClassLoader(),
                new Class[]{searchResponseClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//                        Log.i(TAG, "callback method: " + method, new Throwable());
                        if (method.getDeclaringClass().equals(Object.class)) {
                            // Object. method
                            return method.invoke(this, args);
                        }
                        if (method.getName().equals("onSuccess")) {
                            //成功的
                            Object body = RposedHelpers.callMethod(args[0], "getBody");
                            Object bodyStr = RposedHelpers.callMethod(body, "getStringData");
                            sekiroResponse.success(JSONObject.parse(bodyStr.toString()));
//                            sekiroResponse.success(ForceFiledViewer.toView(body));
//                            sekiroResponse.success(ForceFiledViewer.toView(args[0]));
                        } else if (method.getName().equals("onFailure")) {
                            // 失败
                            int errorCode = (int) RposedHelpers.callMethod(args[0], "getStatusCode");
                            sekiroResponse.failed(-1, String.valueOf(errorCode));
                        }
                        return null;
                    }
                });
    }

    public HashMap<String, String> convertMap() {
        HashMap<String, String> bodyMap = new HashMap<>();
      /*
        {
			"addr_poi_merge": "true",
			"ajxVersion": "nearby:222138;scenic_area:056045;poi:222170;travel:050120;walkman:221302;idqplus:221312;idqmax:222031;search:222170;search_around:221960;hotel:222055;landing_page:222055;tour:220456;search_cloud:055716;poi_cloud:055716;favorites:222055;nearby_cloud:221960;comment:222031;order_center:222032;information_im:221949;car:222031;residential:222031;toolpro:222055;life:222055;ecology:222055;template_map:220602;plague_map:221132;search_home:222170;hkf:221993",
			"aosbusiness": "",
			"apiName": "searchList",
			"bffVoBizParams": "{\"version\":\"222170\",\"pageId\":\"HomePage.page.js\",\"name\":\"amap_bundle_search\"}",
			"busorcar": "",
			"citysuggestion": "true",
			"client_network_class": "",
			"cluster_state": "5",
			"cmspoi": "",
			"cur_adcode": "",
			"dib": "a",
			"direct_jump": "true",
			"geoobj": "113.88562515377996|22.53687331009011|113.8969078660011|22.515294910297655",
			"hotelcheckin": "",
			"hotelcheckout": "",
			"hotelcondition": "",
			"hotelissupper": "",
			"hotelstar": "",
			"input_method": "",
			"interior_floor": "",
			"isBrand": "",
			"isFc": "1",
			"isNewPath": "1",
			"is_classify": "true",
			"keywords": "美食",
			"loc_strict": "",
			"log_center_id": "",
			"need_codepoint": "true",
			"need_magicbox": "",
			"need_naviinfo": "",
			"need_parkinfo": "true",
			"need_recommend": "1",
			"need_utd": "true",
			"new_version": "100",
			"onlypoi": "",
			"pageFrom": "list",
			"pageId": "search",
			"pagenum": "1",
			"pagesize": "10",
			"personal_switch": "on",
			"qii": "true",
			"query_mode": "normal",
			"query_scene": "search",
			"query_type": "TQUERY",
			"sc_stype": "",
			"scenario": "1",
			"scenefilter": "",
			"schema_source": "",
			"search_operate": "1",
			"search_sceneid": "",
			"siv": "ANDH130000",
			"sort_rule": "0",
			"specialpoi": "",
			"superid": "a_05",
			"takeout_flag": "",
			"tip_rule": "",
			"transfer_filter_flag": "0",
			"transfer_mode": "",
			"transfer_nearby_bucket": "",
			"transfer_nearby_keyindex": "",
			"transfer_nearby_time_opt": "",
			"transfer_pdheatmap": "0",
			"transfer_realtimebus_poi": "",
			"transparent": "",
			"transparent_center_around": "",
			"ugc_unified_switch": "true",
			"unsupport_api": "0",
			"user_city": "440300",
			"user_loc": "113.891266,22.526087",
			"utd_sceneid": "101000",
			"version": "2.19",
			"wifi": "[{\"connected\":false,\"frequency\":2462,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":141346152610369,\"rssi\":-57,\"ssid\":\"QHKC Guest\",\"timestamp\":812077780},{\"connected\":false,\"frequency\":2437,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946678785,\"rssi\":-60,\"ssid\":\"AfterShip Guest\",\"timestamp\":812077062},{\"connected\":false,\"frequency\":2437,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946678786,\"rssi\":-60,\"ssid\":\"AfterShip IoT\",\"timestamp\":812077062},{\"connected\":false,\"frequency\":2437,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946678784,\"rssi\":-61,\"ssid\":\"AfterShip\",\"timestamp\":812077061},{\"connected\":true,\"frequency\":5785,\"freshness\":0,\"lastUpdateUtcMills\":0,\"mac\":185112946678801,\"rssi\":-63,\"ssid\":\"\\\"AfterShip Guest\\\"\",\"timestamp\":812095214},{\"connected\":false,\"frequency\":5785,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":185112946678801,\"rssi\":-63,\"ssid\":\"AfterShip Guest\",\"timestamp\":812078015},{\"connected\":false,\"frequency\":2412,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":141346152613825,\"rssi\":-65,\"ssid\":\"QHKC Guest\",\"timestamp\":812076356},{\"connected\":false,\"frequency\":5785,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":185112946678802,\"rssi\":-68,\"ssid\":\"AfterShip IoT\",\"timestamp\":812077233},{\"connected\":false,\"frequency\":5180,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946733457,\"rssi\":-69,\"ssid\":\"AfterShip Guest\",\"timestamp\":812076366},{\"connected\":false,\"frequency\":5785,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":185112946678800,\"rssi\":-69,\"ssid\":\"AfterShip\",\"timestamp\":812077233}]"
		}
         */
        bodyMap.put("addr_poi_merge", "true");
        bodyMap.put("ajxVersion", "nearby:222138;scenic_area:056045;poi:222170;travel:050120;walkman:221302;idqplus:221312;idqmax:222031;search:222170;search_around:221960;hotel:222055;landing_page:222055;tour:220456;search_cloud:055716;poi_cloud:055716;favorites:222055;nearby_cloud:221960;comment:222031;order_center:222032;information_im:221949;car:222031;residential:222031;toolpro:222055;life:222055;ecology:222055;template_map:220602;plague_map:221132;search_home:222170;hkf:221993");
        bodyMap.put("aosbusiness", "");
        bodyMap.put("apiName", "searchList");
        bodyMap.put("bffVoBizParams", "{\"version\":\"222170\",\"pageId\":\"HomePage.page.js\",\"name\":\"amap_bundle_search\"}");
        bodyMap.put("busorcar", "");
        bodyMap.put("citysuggestion", "true");
        bodyMap.put("client_network_class", "");
        bodyMap.put("cluster_state", "5");
        bodyMap.put("cmspoi", "");
        bodyMap.put("cur_adcode", "");
        bodyMap.put("dib", "a");
        bodyMap.put("direct_jump", "true");
        bodyMap.put("geoobj", "113.88562515377996|22.53687331009011|113.8969078660011|22.515294910297655");
        bodyMap.put("hotelcheckin", "");
        bodyMap.put("hotelcheckout", "");
        bodyMap.put("hotelcondition", "");
        bodyMap.put("hotelissupper", "");
        bodyMap.put("hotelstar", "");
        bodyMap.put("input_method", "");
        bodyMap.put("interior_floor", "");
        bodyMap.put("isBrand", "");
        bodyMap.put("isFc", "1");
        bodyMap.put("isNewPath", "1");
        bodyMap.put("is_classify", "true");
        bodyMap.put("keywords", keyWord);
        bodyMap.put("loc_strict", "");
        bodyMap.put("log_center_id", "");
        bodyMap.put("need_codepoint", "true");
        bodyMap.put("need_magicbox", "");
        bodyMap.put("need_naviinfo", "");
        bodyMap.put("need_parkinfo", "true");
        bodyMap.put("need_recommend", "1");
        bodyMap.put("need_utd", "true");
        bodyMap.put("new_version", "100");
        bodyMap.put("onlypoi", "");
        bodyMap.put("pageFrom", "list");
        bodyMap.put("pageId", "search");
        bodyMap.put("pagenum", String.valueOf(page));
        bodyMap.put("pagesize", "10");
        bodyMap.put("personal_switch", "on");
        bodyMap.put("qii", "true");
        bodyMap.put("query_mode", "normal");
        bodyMap.put("query_scene", "search");
        bodyMap.put("query_type", "TQUERY");
        bodyMap.put("sc_stype", "");
        bodyMap.put("scenario", "1");
        bodyMap.put("scenefilter", "");
        bodyMap.put("schema_source", "");
        bodyMap.put("search_operate", "1");
        bodyMap.put("search_sceneid", "");
        bodyMap.put("siv", "ANDH130000");
        bodyMap.put("sort_rule", "0");
        bodyMap.put("specialpoi", "");
        bodyMap.put("superid", "a_05");
        bodyMap.put("takeout_flag", "");
        bodyMap.put("tip_rule", "");
        bodyMap.put("transfer_filter_flag", "0");
        bodyMap.put("transfer_mode", "");
        bodyMap.put("transfer_nearby_bucket", "");
        bodyMap.put("transfer_nearby_keyindex", "");
        bodyMap.put("transfer_nearby_time_opt", "");
        bodyMap.put("transfer_pdheatmap", "0");
        bodyMap.put("transfer_realtimebus_poi", "");
        bodyMap.put("transparent", "");
        bodyMap.put("transparent_center_around", "");
        bodyMap.put("ugc_unified_switch", "true");
        bodyMap.put("unsupport_api", "0");
        bodyMap.put("user_city", "440300");
        bodyMap.put("user_loc", "113.891266,22.526087");
        bodyMap.put("utd_sceneid", "101000");
        bodyMap.put("version", "2.19");
//        bodyMap.put("wifi", "[{\"connected\":false,\"frequency\":2462,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":141346152610369,\"rssi\":-57,\"ssid\":\"QHKC Guest\",\"timestamp\":812077780},{\"connected\":false,\"frequency\":2437,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946678785,\"rssi\":-60,\"ssid\":\"AfterShip Guest\",\"timestamp\":812077062},{\"connected\":false,\"frequency\":2437,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946678786,\"rssi\":-60,\"ssid\":\"AfterShip IoT\",\"timestamp\":812077062},{\"connected\":false,\"frequency\":2437,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946678784,\"rssi\":-61,\"ssid\":\"AfterShip\",\"timestamp\":812077061},{\"connected\":true,\"frequency\":5785,\"freshness\":0,\"lastUpdateUtcMills\":0,\"mac\":185112946678801,\"rssi\":-63,\"ssid\":\"\\\"AfterShip Guest\\\"\",\"timestamp\":812095214},{\"connected\":false,\"frequency\":5785,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":185112946678801,\"rssi\":-63,\"ssid\":\"AfterShip Guest\",\"timestamp\":812078015},{\"connected\":false,\"frequency\":2412,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":141346152613825,\"rssi\":-65,\"ssid\":\"QHKC Guest\",\"timestamp\":812076356},{\"connected\":false,\"frequency\":5785,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":185112946678802,\"rssi\":-68,\"ssid\":\"AfterShip IoT\",\"timestamp\":812077233},{\"connected\":false,\"frequency\":5180,\"freshness\":18,\"lastUpdateUtcMills\":0,\"mac\":185112946733457,\"rssi\":-69,\"ssid\":\"AfterShip Guest\",\"timestamp\":812076366},{\"connected\":false,\"frequency\":5785,\"freshness\":17,\"lastUpdateUtcMills\":0,\"mac\":185112946678800,\"rssi\":-69,\"ssid\":\"AfterShip\",\"timestamp\":812077233}]");
        return bodyMap;
    }

    public Object createAosRequest() {
        // com.amap.network.api.http.request.AosRequest
        Object aosRequest = RposedHelpers.newInstance(
                RposedHelpers.findClass("com.amap.network.api.http.request.AosRequest", RatelToolKit.hostClassLoader)
        );

        RposedHelpers.callMethod(aosRequest, "setMethod", "POST");
        Object requestFormBody = RposedHelpers.newInstance(
                RposedHelpers.findClass("com.amap.network.api.http.body.RequestFormBody", RatelToolKit.hostClassLoader)
        );
        HashMap<String, String> bodyMap = convertMap();
        for (Map.Entry<String, String> entry: bodyMap.entrySet()){
            RposedHelpers.callMethod(
                    requestFormBody,
                    "addParam",
                    entry.getKey(),
                    entry.getValue()
            );
        }

        RposedHelpers.callMethod(aosRequest, "setBody", requestFormBody);
        RposedHelpers.callMethod(aosRequest, "setUrl", "https://m5.amap.com/ws/shield/search_poi/search/sp");

        String[] signKeys = new String[]{"id", "longitude", "latitude", "keywords", "category", "geoobj"};
        RposedHelpers.callMethod(aosRequest, "addSignKey", (Object) signKeys);


        return aosRequest;
    }
}
