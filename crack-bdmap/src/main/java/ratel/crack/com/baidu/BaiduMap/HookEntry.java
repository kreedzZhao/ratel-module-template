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
import android.widget.TextView;

import com.virjar.ratel.api.RatelToolKit;
import com.virjar.ratel.api.inspect.ForceFiledViewer;
import com.virjar.ratel.api.rposed.IRposedHookLoadPackage;
import com.virjar.ratel.api.rposed.RC_MethodHook;
import com.virjar.ratel.api.rposed.RposedBridge;
import com.virjar.ratel.api.rposed.RposedHelpers;
import com.virjar.ratel.api.rposed.callbacks.RC_LoadPackage;

import external.com.alibaba.fastjson.JSONObject;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "BD_HOOK";


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {


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


        addFloatingButtonForActivity(lpparam);
        Log.i(TAG, "hook end");
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
