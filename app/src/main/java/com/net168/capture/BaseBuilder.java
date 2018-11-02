package com.net168.capture;

import android.graphics.ImageFormat;
import android.util.Range;

public abstract class BaseBuilder {

    protected boolean isFront = true;
    protected int mWidth = 360;
    protected int mHeight = 540;
    protected int mMaxFps = 30;
    protected int mMinFps = 15;
    protected int mFormat = ImageFormat.NV21;

    public BaseBuilder setFront(boolean front) {
        this.isFront = front;
        return this;
    }
    public BaseBuilder setSize(int widht, int height) {
        this.mHeight = widht;
        this.mWidth = height;
        return this;
    }
    public BaseBuilder setFormat(int format) {
        this.mFormat = format;
        return this;
    }
    public BaseBuilder setFps(int minfps, int maxfps) {
        this.mMinFps = minfps;
        this.mMaxFps = maxfps;
        return this;
    }

    public abstract VideoCapture build();

}
