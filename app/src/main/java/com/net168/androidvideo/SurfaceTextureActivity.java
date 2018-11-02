package com.net168.androidvideo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.net168.capture.Camera2Capture;
import com.net168.capture.TextureViewPreView;
import com.net168.capture.VideoCapture;

public class SurfaceTextureActivity extends Activity {

    private VideoCapture mCapture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_data);

        mCapture = new Camera2Capture.Builder(this)
                .setSize(720, 1280)
                .setFps(30, 30)
                .build();
        mCapture.openCamera();
        final SurfaceTexture texture = new SurfaceTexture(0);
        texture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            }
        });
        mCapture.setDisplay(new TextureViewPreView(texture));
        mCapture.setPreviewCallback(new VideoCapture.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[][] data) {
            }
        });
        mCapture.startPreview();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCapture.releaseCamera();
    }

    public static void gotoActivity(Activity activity) {
        Intent intent = new Intent(activity, SurfaceTextureActivity.class);
        activity.startActivity(intent);
    }
}
