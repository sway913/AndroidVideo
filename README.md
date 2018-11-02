# 简介
【第一版】源码实现了基于Camera1和Camera2对应的api的摄像机调用封装。

【待实现】多方式渲染(ANativeWindow、OpenGL)
# 使用方法
**初始化**
```
//构造Capture实例
mCapture = new Camera2Capture.Builder(this)  //or  CameraCapture.Builder() 
                .setSize(720, 1280)
                .setFps(15, 15)
                .setFront(false)
                .build();
//打开Camera
mCapture.openCamera();
//设置预览数据回调
mCapture.setPreviewCallback(new VideoCapture.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[][] data) {
            }
        });
```

**开始预览**

如果采用SurfaceView，请在```surfaceCreated()```中调用
```
mCapture.setDisplay(new SurfaceViewPreView(holder));
mCapture.startPreview();
```
如果采用TextureView，请在```onSurfaceTextureAvailable()```中调用
```
mCapture.setDisplay(new TextureViewPreView(surface));
mCapture.startPreview();
```

# 注意事项
本开源库源码只做参考，并不提供jcenter版本，如需要在项目中使用，请拷贝[capture](https://github.com/net168/AndroidVideo/tree/master/app/src/main/java/com/net168/capture)目录下文件即可。
Camera2相关功能不稳定，请谨慎使用。

# 关键源码
+ [Camera1 api采集核心实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/capture/CameraCapture.java)
+ [Camera2 api采集核心实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/capture/Camera2Capture.java)
