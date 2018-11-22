
#include "include/egl_base.h"


EglBase::EglBase()
: _egl_display(EGL_NO_DISPLAY)
, _egl_context(EGL_NO_CONTEXT)
, _egl_surface(EGL_NO_SURFACE)
{

}

int EglBase::create()
{
    const EGLint config_attribs[] = {
            EGL_BUFFER_SIZE, 32,
            EGL_ALPHA_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_NONE
    };
    const EGLint context_attribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    EGLint major, minor, num_config;
    _egl_display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (_egl_display == EGL_NO_DISPLAY)
        goto error;
    if (!eglInitialize(_egl_display, &major, &minor))
        goto error;
    if (!eglChooseConfig(_egl_display, config_attribs, &_egl_config, 1, &num_config))
        goto error;
    _egl_context = eglCreateContext(_egl_display, _egl_config, NULL, context_attribs);
    if (_egl_context == EGL_NO_CONTEXT)
        goto error;

    return 0;
error:
    return -1;
}


int EglBase::createSurface(ANativeWindow *window)
{
    EGLint format;
    const EGLint attribs[] = { EGL_NONE };
    if (!eglGetConfigAttrib(_egl_display, _egl_config, EGL_NATIVE_VISUAL_ID, &format))
        goto error;
    ANativeWindow_setBuffersGeometry(window, 0, 0, format);
    _egl_surface = eglCreateWindowSurface(_egl_display, _egl_config, window, attribs);
    if (_egl_surface == EGL_NO_SURFACE)
        goto error;
    return 0;
error:
    return -1;
}

int EglBase::createPbufferSurface(int width, int height)
{
    const EGLint attribs[] = {
        EGL_WIDTH, width,
        EGL_HEIGHT, height,
        EGL_NONE
    };
    _egl_surface = eglCreatePbufferSurface(_egl_display, _egl_config, attribs);
    if (_egl_surface == EGL_NO_SURFACE)
        return -1;
    else
        return 0;
}

void EglBase::swapBuffers()
{
    eglSwapBuffers(_egl_display, _egl_surface);
}

int EglBase::makeCurrent()
{
    if (eglMakeCurrent(_egl_display, _egl_surface, _egl_surface, _egl_context))
        return 0;
    else
        return -1;
}

void EglBase::release()
{
    eglDestroySurface(_egl_display, _egl_surface);
    eglDestroyContext(_egl_display, _egl_context);
    eglTerminate(_egl_display);
}