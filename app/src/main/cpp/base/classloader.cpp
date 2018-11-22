
#include "include/classloader.h"
#include <include/jvm.h>
#include <include/hash_compare.h>

static jclass g_toast_utils;

void load_jvm_class()
{
    bool other;
    JNIEnv *env = getEnv(&other);
    g_toast_utils = static_cast<jclass>(env->NewGlobalRef(env->FindClass("com/net168/base/ToastUtils")));
    if (other)
        detatchEnv();
}

jclass class_find(const char *cls)
{
    jclass result = nullptr;
    switch (hash(cls)) {
        case "ToastUtils"_hash:
            result = g_toast_utils;
            break;
    }
    return result;
}