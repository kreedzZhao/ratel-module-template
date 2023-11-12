#include <jni.h>
#include <android/log.h>

// 必须要在 lib 加载之后才能 hook
// 直接 hook loadLibrary 会报错
// 直接去 hook loadLibrary0 或者 doLoad 也可以，可以之后试试
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reversed){
    JNIEnv* env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK){
        return JNI_ERR;
    }
    __android_log_print(ANDROID_LOG_INFO, "yrx", "ratelso-lib JNI_OnLoad load success");
    return JNI_VERSION_1_6;
}