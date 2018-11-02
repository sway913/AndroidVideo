package com.net168.androidvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;

import com.net168.capture.Camera2Capture;
import com.net168.capture.CameraCapture;
import com.net168.capture.TextureViewPreView;
import com.net168.capture.VideoCapture;

public class TextureViewActivity extends Activity implements TextureView.SurfaceTextureListener {

    private VideoCapture mCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textureview);
        TextureView textureView = findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(this);
        mCapture = new Camera2Capture.Builder(this)
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
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCapture.setDisplay(new TextureViewPreView(surface));
        mCapture.startPreview();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) { }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCapture.releaseCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, TextureViewActivity.class);
        activity.startActivity(intent);
    }
}
