
#ifndef __BASELIB__
#define __BASELIB__

#include <jni.h>

/**
 * baselib系列的库的总初始化入口
 */
void baselib_register_all(JNIEnv *env, jobject context);

#endif
