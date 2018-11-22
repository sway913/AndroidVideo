package com.net168.capture;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

public class CameraCapture extends VideoCapture {

    private Camera mCamera;
    private Camera.CameraInfo mInfo;
    private byte[] mData;

    private CameraCapture(Builder config) {
        super(config);
    }

    @Override
    public void openCamera() {
        exceute(new Runnable() {
            @Override
            public void run() {

                if (mState != STATE.IDEL && mState != STATE.WAIT_START) {
                    throw new RuntimeException("camera state not IDEL");
                }

                if (mCamera != null) {
                    throw new RuntimeException("camera already initialized");
                }

                //根据是否前置摄像头寻找设备可用Camera
                findCamera(mConfig.isFront);
                if (mCamera == null) {
                    throw new RuntimeException("Unable to open camera");
                }

                //获取摄像头参数设置集合
                Camera.Parameters parms = mCamera.getParameters();

                //根据输入fps,与Support列表匹配出合适fps范围
                CameraUtils.choosePreviewFps(parms.getSupportedPreviewFpsRange(), mConfig, false);
                //设置预览fps
                parms.setPreviewFpsRange(mConfig.mMinFps * 1000, mConfig.mMaxFps * 1000);

                //根据输入格式,与Support列表匹配出合适格式，一般NV21为通用格式
                CameraUtils.choosePreviewFormat(parms.getSupportedPreviewFormats(), mConfig);
                //设置预览格式
                parms.setPreviewFormat(mConfig.mFormat);

                //根据输入宽高,与Support列表匹配出合适宽高
                CameraUtils.choosePreviewSize(CameraUtils.translateCamera1Size(parms.getSupportedPreviewSizes()), mConfig);
                //设置预览的宽高尺寸
                parms.setPreviewSize(mConfig.mWidth, mConfig.mHeight);

                //默认是关闭，此API可以告知我们是在录制视频，提高采集的fps,但是部分机型容易出现卡帧现象
                //生产环境使用建议配套机型白名单机制
//                parms.setRecordingHint(true);

                //必须setParameters后，属性才会生效
                mCamera.setParameters(parms);

                //由于Camera1的摄像头会做旋转，所以需要获取旋转角度
                //通过setDisplayOrientation将摄像头设置正常
                //如果是前置摄像头，还需要做镜像转换
                int degrees;
                if (mConfig.isFront) {
                    degrees = mInfo.orientation % 360;
                }
                else {
                    degrees = (mInfo.orientation + 360) % 360;
                }
                mCamera.setDisplayOrientation(degrees);

                boolean waitStart = false;
                if (mState == STATE.WAIT_START)
                    waitStart = true;
                mState = STATE.INIT;
                if (mPreView != null)
                    setDisplayImpl();
                if (mCallback != null)
                    setPreviewCallbackImpl();
                if (waitStart) {
                    startPreviewImpl();
                }
            }
        });
    }

    private void findCamera(boolean isFornt) {
        int cameraId = isFornt ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
        Camera.CameraInfo info = new Camera.CameraInfo();
        //获取设备的摄像头数量
        int cameraNum = Camera.getNumberOfCameras();
        //获取设备的后置摄像头
        for (int i = 0; i < cameraNum; i++) {
            //获取对应摄像头信息
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraId) {
                //打开摄像头
                mCamera = Camera.open(i);
                mInfo = info;
                break;
            }
        }
    }


    @Override
    public void setPreviewCallback(final PreviewCallback callback) {
        exceute(new Runnable() {
            @Override
            public void run() {
                mCallback = callback;
                if (mState == STATE.INIT) {
                    setPreviewCallbackImpl();
                }
            }
        });
    }
    private void setPreviewCallbackImpl() {
        //设置预览数据回调
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (mCallback != null)
                    mCallback.onPreviewFrame(new byte[][]{data});
                mCamera.addCallbackBuffer(data);
            }
        });
        mData = new byte[mConfig.mWidth * mConfig.mHeight * 3 / 2];
        mCamera.addCallbackBuffer(mData);
    }

    @Override
    public void startPreview() {
        exceute(new Runnable() {
            @Override
            public void run() {
                if (mState == STATE.INIT)
                    startPreviewImpl();
                else
                    mState = STATE.WAIT_START;
            }
        });
    }
    private void startPreviewImpl() {
        //开始预览
        mCamera.startPreview();
        mState = STATE.START;
    }


    public void setDisplay(final PreView view) {
        exceute(new Runnable() {
            @Override
            public void run() {
                mPreView = view;
                if (mState == STATE.INIT) {
                    setDisplayImpl();
                }
            }
        });
    }


    @Override
    public int getOrientationAsyn() {
        mLock.block();
        exceute(new Runnable() {
            @Override
            public void run() {

            }
        });
        mLock.open();
        int degrees;
        if (mConfig.isFront) {
            degrees = mInfo.orientation % 360;
        }
        else {
            degrees = (mInfo.orientation + 360) % 360;
        }
        return degrees;
    }

    private void setDisplayImpl() {
        try {
            if (mPreView.getOutputClass() == SurfaceHolder.class) {
                //如果预览是SurfaceView，这里设置
                mCamera.setPreviewDisplay(mPreView.getSurfaceHolder());
            }
            else if (mPreView.getOutputClass() == SurfaceTexture.class) {
                //如果预览是TextureView,这里设置
                mCamera.setPreviewTexture(mPreView.getSurfaceTexture());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void releaseCamera() {
        exceute(new Runnable() {
            @Override
            public void run() {
                mCamera.release();
                mState = STATE.IDEL;
            }
        });
    }
    @Override
    public void stopPreview() {
        exceute(new Runnable() {
            @Override
            public void run() {
                mCamera.stopPreview();
                mState = STATE.INIT;
            }
        });
    }

    public static class Builder extends BaseBuilder {

        public CameraCapture build() {
            return new CameraCapture(this);
        }

    }

}
