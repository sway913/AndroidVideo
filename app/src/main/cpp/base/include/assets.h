
#ifndef __ASSETS__
#define __ASSETS__

#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

/**
 * 根据asset文件名打开AAsset
 */
AAsset *Android_AAssetManager_open(const char* name);

/**
 * 读取asset文件内容
 * @param data: 需要写入的内存位置，需要自己申请内存
 * @param len: 读取数据的长度
 * return : 真实读取的数据长度
 */
int Android_AAsset_read(AAsset *as, void *data, size_t len);

/**
 * 释放asset资源
 */
void Android_AAsset_close(AAsset *as);

#endif
