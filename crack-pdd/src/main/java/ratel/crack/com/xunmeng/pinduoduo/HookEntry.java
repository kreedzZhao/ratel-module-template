package ratel.crack.com.xunmeng.pinduoduo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import java.util.Map;

import external.com.alibaba.fastjson.JSONObject;

/**
 * Created by virjar on 2018/10/6.
 */

public class HookEntry implements IRposedHookLoadPackage {
    private static final String TAG = "PDD_HOOK";
    /*
    com.xunmeng.pinduoduo.search.fragment.SearchRequestController#N
    这里 最后的 execute 是关键点，已经找的很清楚，但是当前没有办法修改smali，所以暂时停止了
     */


    @Override
    public void handleLoadPackage(final RC_LoadPackage.LoadPackageParam lpparam) {
        // com.xunmeng.pinduoduo_6.84.0_68400
//        RposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, boolean.class, int.class,
//                new RC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        String textContent = param.args[0] + "";
////                        Log.i(TAG, "setText for textView:" + textContent);
//                        if (textContent.contains("Apple/苹果")) {
//                            Log.i(TAG, "hint text:" + textContent, new Throwable());
//                        }
//                    }
//                });

        // 找到了 recyclerView 说明已经进入渲染数据部分
        // com.xunmeng.pinduoduo.search.entity.a
//        RposedBridge.hookAllConstructors(
//                RposedHelpers.findClass("com.xunmeng.pinduoduo.search.entity.a.a", lpparam.classLoader),
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "data a: "+ JSONObject.toJSONString(ForceFiledViewer.toView(param.args)), new Throwable());
//                    }
//                }
//        );

        // 找到数据解析部分 com.xunmeng.pinduoduo.basekit.http.callback.CommonCallback#parseResponseString
//        RposedHelpers.findAndHookMethod(
//                "com.xunmeng.pinduoduo.basekit.http.callback.CommonCallback",
//                lpparam.classLoader,
//                "parseResponseString",
//                String.class,
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "parseResponseString success: "+param.args[0].toString(), new Throwable());
////                        loge(TAG, "parseResponseString: " + param.args[0].toString());
//                    }
//                }
//        );

        // com.xunmeng.pinduoduo.search.fragment.SearchRequestController#N
        RposedHelpers.findAndHookMethod(
                "com.xunmeng.pinduoduo.search.fragment.SearchRequestController",
                lpparam.classLoader,
                "N",
                RposedHelpers.findClass("com.xunmeng.pinduoduo.search.entity.n", lpparam.classLoader),
                Map.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.i(TAG, "SearchRequestController arg1: " + JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])), new Throwable());
                        Log.i(TAG, "SearchRequestController arg2: " + JSONObject.toJSONString(param.args[1]));
                    }
                }
        );
        // com.xunmeng.pinduoduo.search.fragment.SearchRequestController#S
        RposedBridge.hookAllMethods(
                RposedHelpers.findClass("com.xunmeng.pinduoduo.search.fragment.SearchRequestController", lpparam.classLoader),
                "S",
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "request tag: "+param.getResult().toString());
                    }
                }
        );

        RposedHelpers.findAndHookMethod(
                "com.xunmeng.pinduoduo.search.fragment.SearchRequestController",
                lpparam.classLoader,
                "S",
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "request tag: "+param.getResult().toString(), new Throwable());
                    }
                }
        );
        // com.xunmeng.pinduoduo.search.fragment.SearchRequestController#P
        RposedHelpers.findAndHookMethod(
                "com.xunmeng.pinduoduo.search.fragment.SearchRequestController",
                lpparam.classLoader,
                "P",
                Map.class,
                new RC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "request url : "+param.getResult().toString());
                    }
                }
        );

        // com.xunmeng.pinduoduo.am.c#b
        RposedHelpers.findAndHookMethod(
                "com.xunmeng.pinduoduo.am.c",
                lpparam.classLoader,
                "b",
                new RC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i(TAG, "request header: "+JSONObject.toJSONString(param.getResult()));
                    }
                }
        );

        // com.aimi.android.common.cmt.CMTCallback

        // com.xunmeng.pinduoduo.search.fragment.SearchInputFragment.w
//        RposedHelpers.findAndHookMethod(
//                "com.xunmeng.pinduoduo.search.fragment.SearchInputFragment",
//                lpparam.classLoader,
//                "w",
//                String.class, int.class,
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "SearchInputFragment arg0: "+param.args[0], new Throwable());
//                        Log.i(TAG, "SearchInputFragment arg1: "+param.args[1], new Throwable());
//                    }
//                }
//        );

        // com.xunmeng.pinduoduo.search.fragment.LiveDataBus.a#setValue
//        RposedHelpers.findAndHookMethod(
//                "com.xunmeng.pinduoduo.search.fragment.LiveDataBus.a",
//                lpparam.classLoader,
//                "setValue",
//                RposedHelpers.findClass("com.xunmeng.pinduoduo.search.entity.n", lpparam.classLoader),
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        loge(TAG, "setValue: "+JSONObject.toJSONString(ForceFiledViewer.toView(param.args[0])));
//                    }
//                }
//        );

        // com.xunmeng.pinduoduo.search.fragment.LiveDataBus.a#observe
        // android.arch.lifecycle.LiveData#observe
//        RposedHelpers.findAndHookMethod(
//                "android.arch.lifecycle.LiveData",
//                lpparam.classLoader,
//                "observe",
//                RposedHelpers.findClass("android.arch.lifecycle.LifecycleOwner", lpparam.classLoader),
//                RposedHelpers.findClass("android.arch.lifecycle.Observer", lpparam.classLoader),
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "observe arg0: "+param.args[0], new Throwable());
//                        Log.i(TAG, "observe arg1: "+param.args[1]);
//                    }
//                }
//        );

//        RposedHelpers.findAndHookMethod(
//                "android.arch.lifecycle.LiveData",
//                lpparam.classLoader,
//                "observeForever",
//                RposedHelpers.findClass("android.arch.lifecycle.Observer", lpparam.classLoader),
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "observeForever arg0: "+param.args[0], new Throwable());
//                    }
//                }
//        );

        /*
        void innerExecute(long j)
        组装请求 final com.xunmeng.pinduoduo.arch.quickcall.QuickCall L = o.v(str, afVar).x(this.gzip).p(this.headers).E(false).C(isUseCmt()).m(priorityInterceptor(url2)).w(this.retryCnt).z(this.tag).y(this.customShardKey, this.customShardValue).i(this.extensionMap).j(recordHttpCallStart).L();
        使用默认的解析，内部调用了 enqueue
        L.x();
        d.a(this.f, this, false, bVar, E, this.k, H()); 这里其实就是 okhttp 源码了，this.f 需要看看是什么类型
         */

        // com.xunmeng.pinduoduo.arch.quickcall.e#a
//        RposedBridge.hookAllMethods(
//                RposedHelpers.findClass("com.xunmeng.pinduoduo.arch.quickcall.e", lpparam.classLoader),
//                "a",
//                new RC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        super.beforeHookedMethod(param);
//                        Log.i(TAG, "quickcall.e#a: "+param.args[0], new Throwable());
//                    }
//                }
//        );



        addFloatingButtonForActivity(lpparam);
        Log.i(TAG, "hook end");
    }

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
