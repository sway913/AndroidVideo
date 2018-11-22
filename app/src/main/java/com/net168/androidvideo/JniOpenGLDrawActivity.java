package com.net168.androidvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.net168.capture.CameraCapture;
import com.net168.capture.TextureViewPreView;
import com.net168.capture.VideoCapture;
import com.net168.opengl.EglBase;
import com.net168.opengl.TextureMatrixOESRender;

public class JniOpenGLDrawActivity extends Activity implements SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener {

    private static final int CMD_INIT = 0x001;
    private static final int CMD_DRAW = 0x002;
    private static final int CMD_RELEASE = 0x003;

    static {
        System.loadLibrary("native-lib");
    }

    private VideoCapture mCapture;
    private int mTexId;

    private HandlerThread mThread;
    private GLHandler mGlHandler;
    private SurfaceTexture mSurfaceTexture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni_draw_opengl);

        SurfaceView surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);

        mThread = new HandlerThread("GL thread");
        mThread.start();
        mGlHandler = new GLHandler(mThread.getLooper());

        mCapture = new CameraCapture.Builder()
                .setSize(720, 1280)
                .setFps(15, 15)
                .setFront(false)
                .build();
        mCapture.openCamera();
    }

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, JniOpenGLDrawActivity.class);
        activity.startActivity(intent);
    }

    private native int start(Surface surface);

    private native void draw(int texId);

    private native void release();


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Message msg = mGlHandler.obtainMessage(CMD_INIT);
        msg.obj = holder.getSurface();
        mGlHandler.removeMessages(CMD_INIT);
        mGlHandler.sendMessage(msg);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGlHandler.sendEmptyMessage(CMD_RELEASE);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGlHandler.removeMessages(CMD_DRAW);
        mGlHandler.sendEmptyMessage(CMD_DRAW);
    }

    private class GLHandler extends Handler {

        private GLHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CMD_INIT:
                    mTexId = start((Surface) msg.obj);
                    mSurfaceTexture = new SurfaceTexture(mTexId);
                    mSurfaceTexture.setOnFrameAvailableListener(JniOpenGLDrawActivity.this);
                    mCapture.setDisplay(new TextureViewPreView(mSurfaceTexture));
                    mCapture.startPreview();
                    break;
                case CMD_DRAW:
                    mSurfaceTexture.updateTexImage();
                    draw(mTexId);
                    break;
                case CMD_RELEASE:
                    release();
                    break;
            }
        }
    }
}
