package com.net168.capture;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SurfaceViewPreView extends PreView {

    private SurfaceHolder mHolder;

    public SurfaceViewPreView(SurfaceHolder holder) {
        mHolder = holder;
    }

    @Override
    public Class getOutputClass() {
        return SurfaceHolder.class;
    }

    @Override
    public Surface getSurface() {
        return mHolder.getSurface();
    }

    @Override
    public SurfaceHolder getSurfaceHolder() {
        return mHolder;
    }
}
