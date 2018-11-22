
#include <include/baselib.h>
#include <include/jvm.h>
#include <include/android_ctx.h>
#include <include/classloader.h>

void baselib_register_all(JNIEnv *env, jobject context)
{
    initGlobalJvm(env);
    initGlobalContext(context);
    load_jvm_class();
}
