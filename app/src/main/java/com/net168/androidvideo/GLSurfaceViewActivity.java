package com.net168.androidvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.net168.capture.Camera2Capture;
import com.net168.capture.CameraCapture;
import com.net168.capture.TextureViewPreView;
import com.net168.capture.VideoCapture;
import com.net168.opengl.TextureMatrixOESRender;
import com.net168.opengl.TextureOESFboRender;
import com.net168.opengl.TextureOESRender;
import com.net168.opengl.TextureRender;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceViewActivity extends Activity implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private VideoCapture mCapture;
    private GLSurfaceView mGlsurfaceView;
    private TextureRender mRender;
    private SurfaceTexture mSurfaceTexture;
    private int mTexId;
    private final boolean isFront = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
        mGlsurfaceView = findViewById(R.id.glsurface_view);
        mGlsurfaceView.setEGLContextClientVersion(2);
        mGlsurfaceView.setRenderer(this);
        mGlsurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCapture = new CameraCapture.Builder()
                .setSize(720, 1280)
                .setFps(15, 15)
                .setFront(isFront)
                .build();
        mCapture.openCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlsurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlsurfaceView.onPause();
    }

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, GLSurfaceViewActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        mRender = new TextureOESRender();
        mRender.updateRotateAndMirror(mCapture.getOrientationAsyn(), !isFront);
        mTexId = mRender.createTetureID();
        mSurfaceTexture = new SurfaceTexture(mTexId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCapture.setDisplay(new TextureViewPreView(mSurfaceTexture));
        mCapture.startPreview();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {}

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTexture.updateTexImage();
        mRender.draw(mTexId);
    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mGlsurfaceView.requestRender();
    }
}
