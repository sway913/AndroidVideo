package com.net168.androidvideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.net168.capture.Camera2Capture;
import com.net168.capture.CameraCapture;
import com.net168.capture.SurfaceViewPreView;
import com.net168.capture.VideoCapture;

public class SurfaceViewActivity extends Activity implements SurfaceHolder.Callback {

    private VideoCapture mCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surfaceview);
        SurfaceView surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(this);
        mCapture = new CameraCapture.Builder()
                .setSize(720, 1280)
                .setFps(15, 15)
                .setFront(false)
                .build();
        mCapture.openCamera();
        mCapture.setPreviewCallback(new VideoCapture.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[][] data) {
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCapture.setDisplay(new SurfaceViewPreView(holder));
        mCapture.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCapture.releaseCamera();
    }

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, SurfaceViewActivity.class);
        activity.startActivity(intent);
    }

}
