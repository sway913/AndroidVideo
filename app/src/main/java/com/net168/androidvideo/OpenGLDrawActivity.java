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
import android.view.TextureView;

import com.net168.capture.CameraCapture;
import com.net168.capture.TextureViewPreView;
import com.net168.capture.VideoCapture;
import com.net168.opengl.EglBase;
import com.net168.opengl.TextureMatrixOESRender;

public class OpenGLDrawActivity extends Activity implements TextureView.SurfaceTextureListener, SurfaceTexture.OnFrameAvailableListener {

    private static final int CMD_INIT = 0x001;
    private static final int CMD_DRAW = 0x002;
    private static final int CMD_RELEASE = 0x003;

    private VideoCapture mCapture;
    private HandlerThread mThread;
    private GLHandler mGlHandler;
    private TextureView mTextureView;
    private TextureMatrixOESRender mRender;

    private EglBase mEgl;
    private int mTexId;
    private SurfaceTexture mSurfaceTexture;
    private float[] mMatrix = new float[16];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_opengl);

        mTextureView = findViewById(R.id.texture_view);
        mTextureView.setSurfaceTextureListener(this);

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
                    mEgl = EglBase.create();
                    mEgl.createSurface((SurfaceTexture) msg.obj);
                    mEgl.makeCurrent();
                    mRender = new TextureMatrixOESRender();
                    mTexId = mRender.createTetureID();
                    mSurfaceTexture = new SurfaceTexture(mTexId);
                    mSurfaceTexture.setOnFrameAvailableListener(OpenGLDrawActivity.this);
                    mCapture.setDisplay(new TextureViewPreView(mSurfaceTexture));
                    mCapture.startPreview();
                    break;
                case CMD_DRAW:
                    mSurfaceTexture.updateTexImage();
                    mSurfaceTexture.getTransformMatrix(mMatrix);
                    mRender.updateTexMatrix(mMatrix);
                    mRender.draw(mTexId);
                    mEgl.swapBuffers();
                    break;
                case CMD_RELEASE:
                    mRender.release();
                    mEgl.release();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCapture.releaseCamera();
    }

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, OpenGLDrawActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Message msg = mGlHandler.obtainMessage(CMD_INIT);
        msg.obj = surface;
        mGlHandler.removeMessages(CMD_INIT);
        mGlHandler.sendMessage(msg);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mGlHandler.sendEmptyMessage(CMD_RELEASE);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
}
