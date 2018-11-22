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

import com.net168.capture.CameraCapture;
import com.net168.capture.SurfaceViewPreView;
import com.net168.capture.TextureViewPreView;
import com.net168.capture.VideoCapture;
import com.net168.opengl.EglBase;
import com.net168.opengl.Texture2dRender;
import com.net168.opengl.TextureOESFboRender;
import com.net168.opengl.TextureOESRender;

public class FboDrawActivity extends Activity implements SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener {

    private static final int CMD_INIT = 0x001;
    private static final int CMD_FBO_DRAW = 0x002;
    private static final int CMD_SCREEN_DRAW = 0x003;
    private static final int CMD_RELEASE = 0x004;

    private VideoCapture mCapture;
    private HandlerThread mThread;
    private GLHandler mGlHandler;

    private EglBase mEgl;

    private TextureOESFboRender mFboRender;
    private Texture2dRender mRender;

    private int mTexFboId;
    private int mTexId;

    private SurfaceTexture mSurfaceTexture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_fbo);

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

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGlHandler.removeMessages(CMD_FBO_DRAW);
        mGlHandler.sendEmptyMessage(CMD_FBO_DRAW);
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
                    mEgl.createSurface((Surface) msg.obj);
                    mEgl.makeCurrent();
                    mRender = new Texture2dRender();
                    mRender.updateRotateAndMirror(mCapture.getOrientationAsyn(), true);
                    mFboRender = new TextureOESFboRender(720, 1280);
                    mTexFboId = mFboRender.createTetureID();
                    mTexId = mFboRender.getFboTextureId();
                    mSurfaceTexture = new SurfaceTexture(mTexFboId);
                    mSurfaceTexture.setOnFrameAvailableListener(FboDrawActivity.this);
                    mCapture.setDisplay(new TextureViewPreView(mSurfaceTexture));
                    mCapture.startPreview();
                    break;
                case CMD_FBO_DRAW:
                    mSurfaceTexture.updateTexImage();
                    mFboRender.draw(mTexFboId);
                    mGlHandler.sendEmptyMessage(CMD_SCREEN_DRAW);
                    break;
                case CMD_SCREEN_DRAW:
                    mRender.draw(mTexId);
                    mEgl.swapBuffers();
                    break;
                case CMD_RELEASE:
                    mRender.release();
                    mFboRender.release();
                    break;
            }
        }
    }

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
        mGlHandler.removeMessages(CMD_RELEASE);
        mGlHandler.sendEmptyMessage(CMD_RELEASE);
    }

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, FboDrawActivity.class);
        activity.startActivity(intent);
    }
}
