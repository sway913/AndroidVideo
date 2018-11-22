package com.net168.opengl;


import android.opengl.GLES20;

public class TextureOESFboRender extends TextureOESRender {

    private FboTextureDraw mDraw;

    public TextureOESFboRender(int width, int height) {
        super();
        mDraw = new FboTextureDraw(width, height);
    }

    @Override
    protected void beforeDraw() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mDraw.getFboId());
    }

    @Override
    protected void afterDraw() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    @Override
    public void draw(int textureId) {
        super.draw(textureId);
    }

    public int getFboTextureId() {
        return mDraw.getTextureId();
    }

    @Override
    public void release() {
        super.release();
        mDraw.release();
    }
}
