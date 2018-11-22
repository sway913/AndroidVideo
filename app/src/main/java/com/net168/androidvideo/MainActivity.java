package com.net168.androidvideo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .start();

        findViewById(R.id.play_with_surfaceview).setOnClickListener(this);
        findViewById(R.id.play_with_textureview).setOnClickListener(this);
        findViewById(R.id.play_with_opengl).setOnClickListener(this);
        findViewById(R.id.play_with_glview).setOnClickListener(this);
        findViewById(R.id.play_with_fbo).setOnClickListener(this);
        findViewById(R.id.play_with_opengl_jni).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_with_surfaceview:
                SurfaceViewActivity.gotoActivity(this);
                break;
            case R.id.play_with_textureview:
                TextureViewActivity.gotoActivity(this);
                break;
            case R.id.play_with_opengl:
                OpenGLDrawActivity.gotoActivity(this);
                break;
            case R.id.play_with_glview:
                GLSurfaceViewActivity.gotoActivity(this);
                break;
            case R.id.play_with_fbo:
                FboDrawActivity.gotoActivity(this);
                break;
            case R.id.play_with_opengl_jni:
                JniOpenGLDrawActivity.gotoActivity(this);
                break;
        }
    }
}
