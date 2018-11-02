package com.net168.capture;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.util.Range;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TargetApi(21)
public class Camera2Capture extends VideoCapture {

    private CameraManager mManager;
    private CameraDevice mDevice;
    private CameraCaptureSession mSession;
    private String mCameraId;
    private CameraCharacteristics mCameraCharacteristics;
    private CaptureRequest.Builder mRequest;
    private ImageReader mReader;

    private Camera2Capture(@NonNull Context context, Builder config) {
        super(config);
        mManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void openCamera() {
        if (mState != STATE.IDEL && mState != STATE.WAIT_START) {
            throw new RuntimeException("camera state not IDEL");
        }
        if (mManager == null)
            throw new RuntimeException("the CameraManager is null");
        //根据设置寻找可用的摄像头信息
        chooseCamera();
        if (mCameraId == null)
            throw new RuntimeException("can not find camera");
        try {
            //打开摄像头，正常打开会回调到CameraDeviceStateCallback的onOpened方法
            mManager.openCamera(mCameraId, mCameraDeviceStateCallback, mHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            mDevice = camera;
            boolean waitStart = false;
            if (mState == STATE.WAIT_START)
                waitStart = true;
            mState = STATE.INIT;
            if (waitStart)
                startPreviewImpl();

        }
        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            releaseCamera();
        }
        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            releaseCamera();
        }
    };

    private void chooseCamera() {
        try {
            //查看可用的摄像头列表
            for (String item : mManager.getCameraIdList()) {
                int fac = mConfig.isFront ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK;
                CameraCharacteristics characteristics = mManager.getCameraCharacteristics(item);
                //获取摄像头的朝向
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing != fac) {
                    continue;
                }
                mCameraId = item;
                mCameraCharacteristics = characteristics;
                StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    throw new RuntimeException("the StreamConfigurationMap is null");
                }
                //将入参和support列表进行筛选
                CameraUtils.choosePreviewFormat(map.getOutputFormats(), mConfig);
                CameraUtils.choosePreviewFps(mCameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES), mConfig, true);
                CameraUtils.choosePreviewSize(CameraUtils.translateCamera2Size(map.getOutputSizes(mConfig.mFormat)), mConfig);
                //ImageReader是一个数据回调模块，类似于Camera1的setPreviewCallbackWithBuffer
                mReader = ImageReader.newInstance(mConfig.mWidth, mConfig.mHeight, mConfig.mFormat, 2);
                mReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void startPreview() {
        if (mState == STATE.INIT)
            startPreviewImpl();
        else
            mState = STATE.WAIT_START;
    }
    private void startPreviewImpl() {
        try {
            if (mPreView == null)
                return;
            //获取一个采集Seesion会话，正常流程回回调到CameraCaptureSessionStateCallback的onConfigured方法
            mDevice.createCaptureSession(Arrays.asList(mPreView.getSurface(), mReader.getSurface()), mCameraCaptureSessionStateCallback, mHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.StateCallback mCameraCaptureSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                mSession = session;
                List<Surface> outputs = new ArrayList<>();
                outputs.add(mPreView.getSurface());
                if (mCallback != null)
                    outputs.add(mReader.getSurface());
                //创建一个采集的请求
                mRequest = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                mRequest.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range.create(mConfig.mMinFps, mConfig.mMaxFps));
                mRequest.set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraMetadata.STATISTICS_FACE_DETECT_MODE_SIMPLE);
                for (Surface surface : outputs)
                    mRequest.addTarget(surface);
                //重复发送这个请求，开始采集
                session.setRepeatingRequest(mRequest.build(), mCameraCaptureSessionCaptureCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            releaseCamera();
        }
    };

    private CameraCaptureSession.CaptureCallback mCameraCaptureSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

    };

    @Override
    public void setPreviewCallback(final PreviewCallback callback) {
        mCallback = callback;
    }

    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            //数据回调，这里注意ImageReader不支持NV21，建议用YUV_420_888格式
            Image image = reader.acquireNextImage();
            if (mCallback != null) {
                byte[][] data = new byte[3][];
                for (int i = 0; i < 3; i++) {
                    ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    data[i] = bytes;
                }
                mCallback.onPreviewFrame(data);
            }
            image.close();
        }
    };

    @Override
    public void setDisplay(final PreView view) {
        mPreView = view;
        if (mPreView.getOutputClass() == SurfaceHolder.class) {
            mPreView.getSurfaceHolder().setFixedSize(mConfig.mWidth, mConfig.mHeight);
        }
        else if (mPreView.getOutputClass() == SurfaceTexture.class) {
            mPreView.getSurfaceTexture().setDefaultBufferSize(mConfig.mWidth, mConfig.mHeight);
        }
    }

    @Override
    public void releaseCamera() {
        if (mReader != null) {
            mReader.close();
            mReader = null;
        }
        if (mSession != null) {
            mSession.close();
            mSession = null;
        }
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }
        mState = STATE.INIT;
    }
    @Override
    public void stopPreview() {
        try {
            mSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public static class Builder extends BaseBuilder {

        private Context mContext;

        public Builder(@NonNull Context context) {
            mContext = context;
            setFormat(ImageFormat.YUV_420_888);
        }

        public VideoCapture build() {
            return new Camera2Capture(mContext, this);
        }

    }

}
