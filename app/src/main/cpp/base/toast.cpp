
#include <include/toast.h>
#include <include/jvm.h>
#include <include/android_ctx.h>
#include <include/classloader.h>

static jclass cls;
static jmethodID show;


void show_toast(const char *content)
{
    bool other;
    JNIEnv *env = getEnv(&other);
    if (!cls)
    {
        cls = class_find("ToastUtils");
        show = env->GetStaticMethodID(cls, "show", "(Landroid/content/Context;Ljava/lang/String;)V");
    }
    jstring str = env->NewStringUTF(content);
    env->CallStaticVoidMethod(cls, show, getContext(), str);
    env->DeleteLocalRef(str);
    if (other)
        detatchEnv();
}
