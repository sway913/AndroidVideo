
#include <include/android_ctx.h>
#include <include/jvm.h>

static jobject g_context;
static jobject g_assets;

void initGlobalContext(jobject ctx)
{
    bool other_env;
    JNIEnv *env = getEnv(&other_env);
    g_context = env->NewGlobalRef(ctx);
    if (other_env)
        detatchEnv();
}

void releaseContext()
{
    bool other_env;
    JNIEnv *env = getEnv(&other_env);
    env->DeleteGlobalRef(g_context);
    g_context = nullptr;
    if (g_assets)
        env->DeleteGlobalRef(g_assets);
    if (other_env)
        detatchEnv();
}

jobject getContext()
{
    return g_context;
}

jobject getAssetManager()
{
    if (g_assets)
        return g_assets;
    bool other_env;
    JNIEnv *env = getEnv(&other_env);
    jclass cls = env->GetObjectClass(g_context);
    jmethodID getAssets = env->GetMethodID(cls, "getAssets", "()Landroid/content/res/AssetManager;");
    jobject as = env->CallObjectMethod(g_context, getAssets);
    g_assets = env->NewGlobalRef(as);
    if (other_env)
        detatchEnv();
    return g_assets;
}



