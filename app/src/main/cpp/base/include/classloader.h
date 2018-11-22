
#ifndef __CLASS_LOADER__
#define __CLASS_LOADER__

#include <jni.h>

/**
 * 加载自定义的class
 * 由于native层子线程调用env->FindClass获取不了自定义的class
 * 所以在native初始化线程或者jclass转globalref缓存cache
 */
void load_jvm_class();

/**
 * 获取自定义jclass
 * @param cls: 获取的自定义class类名
 */
jclass class_find(const char *cls);

#endif
