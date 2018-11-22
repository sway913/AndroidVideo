#include <jni.h>
#include <texture_render.h>
#include <egl_base.h>
#include <android/native_window_jni.h>

TextureRender *textureRender;
EglBase *eglBase;


extern "C"
JNIEXPORT jint JNICALL
Java_com_net168_androidvideo_JniOpenGLDrawActivity_start(JNIEnv *env, jobject instance,
                                                         jobject surface) {
    eglBase = new EglBase();
    eglBase->create();
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    eglBase->createSurface(window);
    eglBase->makeCurrent();
    textureRender = new TextureRender(TEXTURE_OES);
    textureRender->init();
    return textureRender->createTexId();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_net168_androidvideo_JniOpenGLDrawActivity_draw(JNIEnv *env, jobject instance, jint texId) {

    textureRender->draw(texId);
    eglBase->swapBuffers();

}extern "C"
JNIEXPORT void JNICALL
Java_com_net168_androidvideo_JniOpenGLDrawActivity_release(JNIEnv *env, jobject instance) {

    eglBase->release();
    delete eglBase;
    textureRender->release();
    delete textureRender;

}