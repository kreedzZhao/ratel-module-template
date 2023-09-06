package ratel.crack.com.baidu.BaiduMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.extension.superappium.SuperAppium;
import com.virjar.ratel.api.inspect.ClassLoadMonitor;
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;
import com.virjar.sekiro.api.SekiroClient;
import com.virjar.sekiro.log.SekiroLogger;



import java.util.Map;
import java.util.UUID;

import external.com.alibaba.fastjson.JSONObject;


/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "BD_HOOK";


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
        /*
        com.baidu.mapframework.provider.search.controller.OneSearchWrapper#OneSearchWrapper(
        java.lang.String, java.lang.String, int,
        com.baidu.platform.comapi.basestruct.MapBound,
        int, com.baidu.platform.comapi.basestruct.Point,
        java.util.Map<java.lang.String,java.lang.Object>,
        int)
         */
        RposedHelpers.findAndHookConstructor(
                "com.baidu.mapframework.provider.search.controller.OneSearchWrapper",
                lpparam.classLoader,
                String.class, String.class, int.class,
                "com.baidu.platform.comapi.basestruct.MapBound", int.class,
                "com.baidu.platform.comapi.basestruct.Point",
                Map.class, int.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "OneSearchWrapper: "+ JSONObject.toJSONString(param.args));
                    }
                }
        );

        startSekiro(lpparam);
        addFloatingButtonForActivity(lpparam);
        Log.i(TAG, "hook end");
    }

    public static void startSekiro(RC_LoadPackage.LoadPackageParam lpparam){
        // 是否在主进程
        if (!lpparam.packageName.equals(lpparam.processName)){
            return;
        }
        SekiroLogger.tag = TAG;
        SuperAppium.TAG = TAG;
        SekiroClient.start("192.168.1.18",
                UUID.randomUUID().toString(), "bdMap")
                .registerHandler(new OneSearchHandler());
    }

    public void heatMap(final RC_LoadPackage.LoadPackageParam lpparam){
        // com.baidu.mapframework.common.mapview.HeatMapView#onClicked
        RposedBridge.hookAllMethods(
                RposedHelpers.findClass("com.baidu.mapframework.common.mapview.HeatMapView", lpparam.classLoader),
                "onClicked",
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "onclicked is called ", new Throwable());
                    }
                }
        );
        // com.baidu.platform.comapi.util.BMEventBus#doEvent
        RposedBridge.hookAllMethods(
                RposedHelpers.findClass("com.baidu.platform.comapi.util.BMEventBus", lpparam.classLoader),
                "doEvent",
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        // com.baidu.platform.comapi.util.BMEventBus$ModuleOnEvent
                        Log.i(TAG, "BMEventBus doEvent param1 type: "+param.args[0]);
                        Log.i(TAG, "BMEventBus doEvent param2 type: "+param.args[1], new Throwable());
                    }
                }
        );
        // 所有的事件存放在一个 hashmap 中
        // com.baidu.platform.comapi.util.BMEventBus#regist(com.baidu.platform.comapi.util.BMEventBus.OnEvent, java.lang.Object, java.lang.Class<?>, java.lang.Class<?>...)
        // 当前 注册了所有重要的事件，例如安全相关 com.baidu.baidumaps.secure.g
        RposedBridge.hookAllMethods(
                RposedHelpers.findClass("com.baidu.platform.comapi.util.BMEventBus", lpparam.classLoader),
                "regist",
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "regist param1 type: "+param.args[0]);
                    }
                }
        );
        // 其实任一事件进去之后，就可以看出来 com.baidu.titan.sdk.runtime.Interceptable
        // 实现了一个全局的匿名类 $ic 开始是反射生成的
        /*
         this.mLoadFuture = newSingleThreadExecutor.submit(new java.util.concurrent.Callable<java.lang.Integer>() {
            public java.lang.Integer call() throws java.lang.Exception {
                int access$000 = com.baidu.titan.sdk.loader.LoaderManager.this.loadPatch(false, patchInstallInfo);
                com.baidu.titan.sdk.loader.LoaderManager.this.mLoadState = access$000;
                return java.lang.Integer.valueOf(access$000);
            }
        });
         */
        // com.baidu.titan.sdk.pm.PatchInstallInfo#getOrderedDexList
        ClassLoadMonitor.addClassLoadMonitor(
                "com.baidu.titan.sdk.pm.TitanPaths",
                new ClassLoadMonitor.OnClassLoader() {
                    @Override
                    public void onClassLoad(Class<?> clazz) {
                        Log.d(TAG, "TitanPaths loaded");
                        // com.baidu.titan.sdk.pm.TitanPaths#getPatchDir
                        RposedHelpers.findAndHookMethod(
                                "com.baidu.titan.sdk.pm.TitanPaths",
                                lpparam.classLoader,
                                "getPatchDir",
                                String.class,
                                new RC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                        super.afterHookedMethod(param);
                                        Log.i(TAG, "path dir: "+param.getResult().toString());
                                    }
                                }
                        );
                    }
                }
        );
        // 发现热加载没用


        // com.baidu.baidumaps.aihome.map.widget.AiHomeMapLayout#initViews
        RposedHelpers.findAndHookMethod(
                "com.baidu.baidumaps.aihome.map.widget.AiHomeMapLayout",
                lpparam.classLoader,
                "initViews",
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "initViews", new Throwable());
                    }
                }
        );

        // 这个猜测是首页的定位事件
        // "roadLoc": 怀疑这个是 aoi
        // com.baidu.mapframework.common.mapview.action.LocationMapAction#updateLocOverlay
        RposedHelpers.findAndHookMethod(
                "com.baidu.mapframework.common.mapview.action.LocationMapAction",
                lpparam.classLoader,
                "updateLocOverlay",
                RposedHelpers.findClass("com.baidu.mapframework.location.LocationManager.LocData", lpparam.classLoader),
                RposedHelpers.findClass("com.baidu.mapframework.common.mapview.MapViewConfig.PositionStatus", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "updateLocOverlay param1: "+JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])));
                        Log.i(TAG, "updateLocOverlay param2: "+JSONObject.toJSONString(ForceFiledViewer.toView(param.args[1])), new Throwable());
                    }
                }
        );
        // com.baidu.platform.comjni.map.basemap.AppBaseMap#ShowHotMap(boolean, int)
        // com.baidu.platform.comjni.map.basemap.AppBaseMap#ShowLayers 发现展示热力图用的是 这个
        RposedHelpers.findAndHookMethod(
                "com.baidu.platform.comjni.map.basemap.AppBaseMap",
                lpparam.classLoader,
                "ShowLayers",
                long.class, boolean.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "ShowHotMap param1: "+param.args[0]);
                        Log.d(TAG, "ShowHotMap param2: "+param.args[1], new Throwable());
                    }
                }
        );

        // com.baidu.platform.comjni.map.basemap.NABaseMap#nativeShowLayers(long j13, long j14, boolean z13)
        RposedHelpers.findAndHookMethod(
                "com.baidu.platform.comjni.map.basemap.NABaseMap",
                lpparam.classLoader,
                "nativeShowLayers",
                long.class, long.class, boolean.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.d(TAG, "ShowHotMap param1: "+param.args[0]);
                        Log.d(TAG, "ShowHotMap param2: "+param.args[1]);
                        Log.d(TAG, "ShowHotMap param3: "+param.args[2], new Throwable());
                    }
                }
        );
        // at com.baidu.platform.comjni.map.basemap.NABaseMap.access$300(SourceFile:1)
    }


    public void backup(RC_LoadPackage.LoadPackageParam lpparam) {


        //寻找pojo
        RposedBridge.hookAllMethods(TextView.class, "setText",
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String textContent = null;
                        for (Object obj : param.args) {
                            if (obj instanceof CharSequence) {
                                textContent = obj.toString();
                                break;
                            }
                        }
                        if (!TextUtils.isEmpty(textContent)) {
                            //Log.i(TAG, "setText for textView:" + textContent);
                            if (textContent.contains("如家酒店")) {
                                Log.i(TAG, "hint content:" + textContent, new Throwable());
                            }
                        }
                    }
                }
        );

        // com.baidu.mapframework.place.PoiItem
        // at com.baidu.baidumaps.poi.newpoi.list.presenter.RecyclerViewPresenter.getPoiItem(SourceFile:1)
        RposedBridge.hookAllConstructors(
                RposedHelpers.findClass("com.baidu.mapframework.place.PoiItem", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "PoiItem: " + JSONObject.toJSONString(ForceFiledViewer.toView(param.thisObject)), new Throwable());
                    }
                }
        );
        // com.baidu.entity.pb.PoiResult poiResult
        RposedBridge.hookAllConstructors(
                RposedHelpers.findClass("com.baidu.entity.pb.PoiResult", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "PoiResult: " + JSONObject.toJSONString(ForceFiledViewer.toView(param.thisObject)), new Throwable());
                    }
                }
        );
        // 关键类 com.baidu.platform.comapi.newsearch.SearcherImpl.getPBResultInternal
        // 但是这个往上追溯是一个新的线程，所以在他当前的文件中，一般解析和请求会在一个文件里
        // sendRequest(com.baidu.platform.comapi.newsearch.SearchRequest searchRequest)
        // com.baidu.platform.comapi.newsearch.SearcherImpl#sendRequest
        RposedHelpers.findAndHookMethod(
                "com.baidu.platform.comapi.newsearch.SearcherImpl",
                lpparam.classLoader,
                "sendRequest",
                RposedHelpers.findClass("com.baidu.platform.comapi.newsearch.SearchRequest", lpparam.classLoader),
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "sendRequest: "+JSONObject.toJSONString(param.args[0]), new Throwable());
                    }
                }
        );

        // com.baidu.mapframework.searchcontrol.baseline.SearchBaseLineImpl#sendRequest

        // com.baidu.mapframework.searchcontrol.SearchControl#searchRequest
        RposedBridge.hookAllMethods(
                RposedHelpers.findClass("com.baidu.mapframework.searchcontrol.SearchControl", lpparam.classLoader),
                "searchRequest",
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "searchRequest param1 type: "+JSONObject.toJSONString(param.args[0]));
                        Log.i(TAG, "searchRequest param2 type: "+param.args[1], new Throwable());
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
