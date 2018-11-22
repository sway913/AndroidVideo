
#ifndef __JVM__
#define __JVM__

#include <jni.h>

/**
 * 初始化jvm全局实例
 */
void initGlobalJvm(JavaVM *jvm);
void initGlobalJvm(JNIEnv *env);

/**
 * 获取JniEnv环境
 * bool 值说明是否是新线程加载jvm， 新线程需要detatchEnv
 */
JNIEnv *getEnv();
JNIEnv *getEnv(bool *);


/**
 * 线程脱离JniEnv环境
 */
int detatchEnv();

#endif
