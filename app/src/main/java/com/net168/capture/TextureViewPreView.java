package com.net168.capture;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

public class TextureViewPreView extends PreView {

    private SurfaceTexture mTexture;

    public TextureViewPreView(SurfaceTexture texture) {
        mTexture = texture;
    }

    @Override
    public Class getOutputClass() {
        return SurfaceTexture.class;
    }

    @Override
    public Surface getSurface() {
        return new Surface(mTexture);
    }


    @Override
    public SurfaceTexture getSurfaceTexture() {
        return mTexture;
    }
}
