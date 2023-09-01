- url hook
```text
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.fork.b.a(RxForkHttpService.java:182)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.fork.b.exec(RxForkHttpService.java:102)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m.a(OnSubscribeWithCache.java:190)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m.b(OnSubscribeWithCache.java:43)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m$b.a(OnSubscribeWithCache.java:460)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.preload.engine.fetch.d.a(FetchPreloadInterceptor.kt:104)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.preload.engine.fetch.d.intercept(FetchPreloadInterceptor.kt:34)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m$b.a(OnSubscribeWithCache.java:452)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.s.intercept(RxNVNetworkMockInterceptor.java:39)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m$b.a(OnSubscribeWithCache.java:452)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.dataservice.mapi.impl.d.intercept(MapiInterceptor.java:53)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m$b.a(OnSubscribeWithCache.java:452)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.meituan.metrics.traffic.shark.SharkRxInterceptor.intercept(SharkRxInterceptor.java:65)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m$b.a(OnSubscribeWithCache.java:452)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m.a(OnSubscribeWithCache.java:137)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at com.dianping.nvnetwork.m.call(OnSubscribeWithCache.java:43)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at rx.internal.operators.k.a(OnSubscribeLift.java:50)
08-31 22:24:54.148 17432 17650 I DP_HOOK : 	at rx.internal.operators.k.call(OnSubscribeLift.java:30)
```
- com.dianping.nvnetwork.Request 构造函数
```text
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.nvnetwork.Request$Builder.build(Request.java:596)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.dataservice.mapi.impl.DefaultMApiService.transferRequest(DefaultMApiService.java:267)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.dataservice.mapi.impl.DefaultMApiService.exec(DefaultMApiService.java:301)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.dataservice.mapi.impl.DefaultMApiService.exec(DefaultMApiService.java:53)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.preload.commons.network.c.a(Mapi.kt:131)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.preload.engine.fetch.b.a(FetchPreloadEngine.kt:175)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.preload.bridge.PreloadRequestBridge.doPreloadRequest(PreloadRequestBridge.kt:47)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at java.lang.reflect.Method.invoke(Native Method)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.picassocontroller.annotation.c.a(PicassoModuleUtil.java:268)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.picassocontroller.annotation.c.a(PicassoModuleUtil.java:241)
08-31 22:47:08.952 19633 19929 D DP_HOOK : 	at com.dianping.picassocontroller.bridge.a.exec(PCSBImpl.java:33)
```
- com.dianping.nvnetwork.NVDefaultNetworkService#exec(com.dianping.nvnetwork.Request, com.dianping.nvnetwork.o)
- com.dianping.dataservice.mapi.impl.DefaultMApiService.a#onRequestFinish 返回值
- 另一个异步解密 com.dianping.dataservice.mapi.impl.DefaultMApiService.a 第三个参数fVal2 就是
- com.dianping.picasso.commonbridge.MapiModule$3
- com.dianping.picasso.commonbridge.MapiModule#resolveData(com.dianping.archive.DPObject, boolean, int)