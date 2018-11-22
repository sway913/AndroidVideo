package com.net168.capture;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;

public abstract class VideoCapture {

    private HandlerThread mThread;
    protected Handler mHandler;
    protected ConditionVariable mLock;
    protected PreviewCallback mCallback;
    protected PreView mPreView;
    protected BaseBuilder mConfig;

    protected enum STATE {IDEL, WAIT_START, INIT, START, STOP};
    protected STATE mState = STATE.IDEL;

    public VideoCapture(BaseBuilder config) {
        mThread = new HandlerThread("Camera Handler Thread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        mLock = new ConditionVariable();
        mConfig = config;
    }

    protected void exceute(Runnable r) {
        mHandler.post(new WraperRunnable(r));
    }

    private class WraperRunnable implements Runnable {
        private Runnable runnable;
        private WraperRunnable(Runnable r) {
            runnable = r;
        }
        @Override
        public void run() {
            runnable.run();
            mLock.open();
        }
    }

    abstract public void openCamera();
    abstract public void releaseCamera();
    abstract public void startPreview();
    abstract public void stopPreview();
    abstract public void setDisplay(PreView view);
    abstract public int getOrientationAsyn();

    public void setPreviewCallback(PreviewCallback callback) {
    }

    public interface PreviewCallback {
        void onPreviewFrame(byte[][] data);
    }

}
