
#ifndef __ANDROID_CTX__
#define __ANDROID_CTX__

#include <jni.h>

/**
 * 初始化android上下文，需要传入java层的 application context
 */
void initGlobalContext(jobject ctx);
void releaseContext();

/**
 * 获取java层的 AssetManager实例
 * 等同实现 Activity.getAssets();
 */
jobject getAssetManager();

/**
 * 获取 initGlobalContext 设置的 application context
 */
jobject getContext();

#endif
