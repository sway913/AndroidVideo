package com.net168.opengl;

import android.opengl.GLES20;

public class Texture2dRender extends TextureRender {
    @Override
    protected int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }
}
