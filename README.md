# 简介
【第一版】源码实现了基于Camera1和Camera2对应的api的摄像机调用封装。

【第二版】源码实现了OpenGL渲染相关逻辑，提供了EGL环境管理类，绘制逻辑涵盖2D、OES、FBO等。

【待实现】ANativeWindow渲染、RBO优化等。

# 采集模块使用方法
**接入工作**

请将[capture](https://github.com/net168/AndroidVideo/tree/master/app/src/main/java/com/net168/capture)目录下的文件拷贝到所需要使用的工程目录下即可。

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
# OpenGL渲染模块使用方法
**接入工作**

如果需要在Java层使用基于Java实现的OpenGL渲染逻辑，请将[opengl](https://github.com/net168/AndroidVideo/tree/master/app/src/main/java/com/net168/opengl)目录下文件拷贝到工程即可。

如果需要在Jni实现OpenGL渲染逻辑，需要拷贝cpp目录下的[opengl](https://github.com/net168/AndroidVideo/tree/master/app/src/main/cpp/opengl)和[base](https://github.com/net168/AndroidVideo/tree/master/app/src/main/cpp/base)，并且修改你的CMake编译脚本，本项目尚未提供Android.mk的编译脚本。

然后在你的主CMakeLists.txt添加如下配置：
```shell
#设置opengl基于jni实现的业务代码路径
set(OPENGL_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/opengl)
#添加子编译文件夹
add_subdirectory(${OPENGL_DIR})
#导入外部调用的头文件
include_directories(${OPENGL_DIR}/include)

#链接到工程
target_link_libraries(
        native-lib  #你的工程lib
        openglcore
        )
```

**使用方法**

请参考demo代码实现：
+ OpenGL ES进行绘制OES纹理的实现：[OpenGLDrawActivity.java](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/androidvideo/OpenGLDrawActivity.java) 。
+ OpenGL ES进行绘制OES纹理的实现(JNI环境)：[JniOpenGLDrawActivity.java](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/androidvideo/JniOpenGLDrawActivity.java)和[native-lib.cpp](https://github.com/net168/AndroidVideo/blob/master/app/src/main/cpp/native-lib.cpp)。
+ OpenGL ES进行FBO后绘制2D纹理的实现：[FboDrawActivity.java](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/androidvideo/FboDrawActivity.java)。


# 注意事项
本开源库源码只做参考，并不提供jcenter版本，如需要在项目中使用，建议参考实现或者直接拷贝相应模块目录下文件即可。
Camera2相关功能不稳定，请谨慎使用。

# 关键源码
+ [Camera1 api采集核心实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/capture/CameraCapture.java)
+ [Camera2 api采集核心实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/capture/Camera2Capture.java)
+ [EGL14环境管理核心实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/opengl/EglBase14.java)
+ [EGL10环境管理核心实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/java/com/net168/opengl/EglBase10.java)
+ [EGL 环境管理native实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/cpp/opengl/egl_base.cpp)
+ [OpenGL OES纹理绘制native实现](https://github.com/net168/AndroidVideo/blob/master/app/src/main/cpp/opengl/texture_render.cpp)
+ [OpenGL OES纹理绘制实现](https://github.com/net168/AndroidVideo/tree/master/app/src/main/java/com/net168/opengl)
