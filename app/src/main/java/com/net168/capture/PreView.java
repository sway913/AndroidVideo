package com.net168.capture;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;

abstract public class PreView {

    public abstract Class getOutputClass();
    public abstract Surface getSurface();

    public SurfaceHolder getSurfaceHolder() {
        return null;
    }

    public SurfaceTexture getSurfaceTexture() {
        return null;
    }


}
