
#include <include/assets.h>
#include <include/android_ctx.h>
#include <include/jvm.h>

AAsset *Android_AAssetManager_open(const char* name)
{
    bool other_env;
    JNIEnv *env = getEnv(&other_env);
    AAssetManager *mgr = AAssetManager_fromJava(env, getAssetManager());
    //需要AASSET_MODE_STREAMING，不能达不到流读取的效果
    AAsset *as = AAssetManager_open(mgr, name, AASSET_MODE_STREAMING);
    if (other_env)
        detatchEnv();
    return as;
}

int Android_AAsset_read(AAsset *as, void *data, size_t len)
{
    return AAsset_read(as, data, len);
}

void Android_AAsset_close(AAsset *as)
{
    AAsset_close(as);
}