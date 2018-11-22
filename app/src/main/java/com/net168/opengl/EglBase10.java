package com.net168.opengl;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EglBase10 extends EglBase {

    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    private final EGL10 egl;
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLDisplay eglDisplay;
    private EGLSurface eglSurface = EGL10.EGL_NO_SURFACE;

    public EglBase10(EglBase10.Context sharedContext, int[] configAttributes) {
        //获取一个EGL10的引擎入口
        egl = (EGL10) EGLContext.getEGL();
        eglDisplay = getEglDisplay();
        eglConfig = getEglConfig(eglDisplay, configAttributes);
        eglContext = createEglContext(sharedContext, eglDisplay, eglConfig);
    }

    private EGLContext createEglContext(Context sharedContext, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        if (sharedContext != null && sharedContext.eglContext == EGL10.EGL_NO_CONTEXT) {
            throw new RuntimeException("Invalid sharedContext");
        }
        int[] contextAttributes = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
        EGLContext rootContext =
                sharedContext == null ? EGL10.EGL_NO_CONTEXT : sharedContext.eglContext;
        EGLContext eglContext;
        synchronized (lock) {
            eglContext = egl.eglCreateContext(eglDisplay, eglConfig, rootContext, contextAttributes);
        }
        if (eglContext == null) {
            throw new RuntimeException("Failed to create EGL context");
        }
        return eglContext;
    }

    private EGLConfig getEglConfig(EGLDisplay eglDisplay, int[] configAttributes) {
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        //通过传入的configAttributes让EGL返回一个可用的EGLConfig
        if (!egl.eglChooseConfig(eglDisplay, configAttributes, configs, configs.length, numConfigs)) {
            throw new RuntimeException("eglChooseConfig failed");
        }
        if (numConfigs[0] <= 0) {
            throw new RuntimeException("Unable to find any matching EGL config");
        }
        final EGLConfig eglConfig = configs[0];
        if (eglConfig == null) {
            throw new RuntimeException("eglChooseConfig returned null");
        }
        return eglConfig;
    }

    private EGLDisplay getEglDisplay() {
        //获取一个默认的EGLDisplay,其是一个封装物理屏幕的一个数据类型，也可以说是绘制目标的一个抽象
        EGLDisplay eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        //由于可能获取不到EGLDisplay,所以需要确认是否EGL_NO_DISPLAY
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL10 display");
        }
        //初始化EGLDisplay,传入的version数组将会回调相对的版本号
        int[] version = new int[2];
        if (!egl.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("Unable to initalize EGL10");
        }
        return eglDisplay;
    }

    @Override
    public void createSurface(Surface surface) {
        createSurfaceImpl(new SimpleSurfaceHolder(surface));
    }

    @Override
    public void createSurface(SurfaceTexture surfaceTexture) {
        createSurfaceImpl(surfaceTexture);
    }

    private void createSurfaceImpl(Object nativeWindow) {
        if (!(nativeWindow instanceof SurfaceHolder) && !(nativeWindow instanceof SurfaceTexture)) {
            throw new RuntimeException("Input must be either a SurfaceHodler or SurfaceTexture");
        }
        checkIsNotReleased();
        if (eglSurface != EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        int[] surfaceAttribs = {EGL10.EGL_NONE};
        eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, nativeWindow, surfaceAttribs);
        if (eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Failed to create window surface");
        }
    }

    @Override
    public void createPbufferSurface(int width, int height) {
        checkIsNotReleased();
        if (eglSurface != EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Already has an EGLSurface");
        }
        int[] sufaceAttribs = {
                EGL10.EGL_WIDTH, width,
                EGL10.EGL_HEIGHT, height,
                EGL10.EGL_NONE
        };
        eglSurface = egl.eglCreatePbufferSurface(eglDisplay, eglConfig, sufaceAttribs);
        if (eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("Failed to carete pixel buffer surface");
        }
    }

    @Override
    public EglBase.Context getEglBaseContext() {
        return new Context(eglContext);
    }

    @Override
    public boolean hasSurface() {
        return eglSurface != EGL10.EGL_NO_SURFACE;
    }

    @Override
    public int getSurfaceWidth() {
        return querySurfaceType(EGL10.EGL_WIDTH);
    }

    @Override
    public int getSurfaceHeight() {
        return querySurfaceType(EGL10.EGL_HEIGHT);
    }

    private int querySurfaceType(int type) {
        int array[] = new int[1];
        egl.eglQuerySurface(eglDisplay, eglSurface, type, array);
        return array[0];
    }

    @Override
    public void releaseSuface() {
        if (eglSurface != EGL10.EGL_NO_SURFACE) {
            egl.eglDestroySurface(eglDisplay, eglSurface);
            eglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    @Override
    public void release() {
        checkIsNotReleased();
        releaseSuface();
        detachCurrent();
        egl.eglDestroyContext(eglDisplay, eglContext);
        egl.eglTerminate(eglDisplay);
        eglContext = EGL10.EGL_NO_CONTEXT;
        eglDisplay = EGL10.EGL_NO_DISPLAY;
        eglConfig = null;
    }

    @Override
    public void makeCurrent() {
        checkIsNotReleased();
        if (eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface can't make current");
        }
        synchronized (lock) {
            if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
                throw new RuntimeException("eglMakeCurrent failed");
            }
        }
    }

    @Override
    public void detachCurrent() {
        synchronized (lock) {
            if (!egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
                throw new RuntimeException("detachCurrent failed");
            }
        }
    }

    @Override
    public void swapBuffers() {
        checkIsNotReleased();
        if (eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("No EGLSurface can't make current");
        }
        synchronized (lock) {
            egl.eglSwapBuffers(eglDisplay, eglSurface);
        }
    }

    private void checkIsNotReleased() {
        if (eglDisplay == EGL10.EGL_NO_DISPLAY || eglContext == EGL10.EGL_NO_CONTEXT
                || eglConfig == null) {
            throw new RuntimeException("This object has been released");
        }
    }


    public static class Context extends EglBase.Context {
        private final EGLContext eglContext;
        public Context(EGLContext eglContext) {
            this.eglContext = eglContext;
        }
    }

}
