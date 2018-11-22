package com.net168.opengl;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Build;
import android.view.Surface;

@TargetApi(18)
public class EglBase14 extends EglBase {

    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLConfig eglConfig;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;

    public EglBase14(EglBase14.Context sharedContext, int[] configAttributes) {
        eglDisplay = getEglDisplay();
        eglConfig = getEglConfig(eglDisplay, configAttributes);
        eglContext = createEglContext(sharedContext, eglDisplay, eglConfig);
    }

    private static EGLDisplay getEglDisplay() {
        EGLDisplay eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL14 display");
        }
        int version[] = new int[2];
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("Unable to initialize EGL14");
        }
        return eglDisplay;
    }

    private static EGLConfig getEglConfig(EGLDisplay eglDisplay, int[] configAttributes) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!EGL14.eglChooseConfig(eglDisplay, configAttributes, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            throw new RuntimeException("eglChooseConfig failed");
        }
        if (numConfigs[0] < 0) {
            throw new RuntimeException("Unable to find any matching EGL config");
        }
        EGLConfig eglConfig = configs[0];
        if (eglConfig == null) {
            throw new RuntimeException("eglChooseConfig returned null");
        }
        return eglConfig;
    }

    private static EGLContext createEglContext(Context sharedContext, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        if (sharedContext != null && sharedContext.eglContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("Invalid sharedContext");
        }
        int[] contextAttributes = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
        EGLContext rootContext = sharedContext == null ? EGL14.EGL_NO_CONTEXT : sharedContext.eglContext;
        EGLContext eglContext;
        synchronized (lock) {
            eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, rootContext, contextAttributes, 0);
        }
        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw new RuntimeException("Failed to create EGL context");
        }
        return eglContext;
    }

    @Override
    public void createSurface(Surface surface) {
        createSufaceImpl(surface);
    }

    @Override
    public void createSurface(SurfaceTexture surfaceTexture) {
        createSufaceImpl(surfaceTexture);
    }

    private void createSufaceImpl(Object surface) {
        if (!(surface instanceof Surface) && !(surface instanceof SurfaceTexture)) {
            throw new RuntimeException("Input must be either a Surface or SurfaceTexture");
        }
        checkIsNotReleased();
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        int[] surfaceAttribs = {EGL14.EGL_NONE};
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribs, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Failed to create window surface");
        }
    }

    @Override
    public void createPbufferSurface(int width, int height) {
        checkIsNotReleased();
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        int[] surfaceAttribs = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };
        eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0);
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("Failed to create pixel buffer surface");
        }
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return new Context(eglContext);
    }

    @Override
    public boolean hasSurface() {
        return eglSurface != EGL14.EGL_NO_SURFACE;
    }

    @Override
    public int getSurfaceWidth() {
        return querySurfaceType(EGL14.EGL_WIDTH);
    }

    @Override
    public int getSurfaceHeight() {
        return querySurfaceType(EGL14.EGL_HEIGHT);
    }

    private int querySurfaceType(int type) {
        int[] array = new int[1];
        EGL14.eglQuerySurface(eglDisplay, eglSurface, type, array, 0);
        return array[0];
    }

    @Override
    public void releaseSuface() {
        if (eglSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface);
            eglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    @Override
    public void release() {
        checkIsNotReleased();
        releaseSuface();
        detachCurrent();
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(eglDisplay);
        eglContext = EGL14.EGL_NO_CONTEXT;
        eglDisplay = EGL14.EGL_NO_DISPLAY;
        eglConfig = null;
    }

    @Override
    public void makeCurrent() {
        checkIsNotReleased();
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface can't make current");
        }
        synchronized (lock) {
            if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                throw new RuntimeException("eglMakeCurrent failed");
            }
        }
    }

    @Override
    public void detachCurrent() {
        synchronized (lock) {
            if (!EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)) {
                throw new RuntimeException("detachCurrent failed");
            }
        }
    }

    @Override
    public void swapBuffers() {
        checkIsNotReleased();
        if (eglSurface == EGL14.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface can't make current");
        }
        synchronized (lock) {
            EGL14.eglSwapBuffers(eglDisplay, eglSurface);
        }
    }

    public static class Context extends EglBase.Context {
        private final EGLContext eglContext;
        public Context(EGLContext eglContext) {
            this.eglContext = eglContext;
        }
    }

    private void checkIsNotReleased() {
        if (eglDisplay == EGL14.EGL_NO_DISPLAY || eglContext == EGL14.EGL_NO_CONTEXT
                || eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }

    public static boolean isEGL14Supported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

}
