package com.net168.opengl;

import android.opengl.GLES20;

public class FboTextureDraw extends FboDraw {

    private int mTexId;

    FboTextureDraw(int width, int height) {
        super(width, height);
        createTexId();
    }

    private void createTexId() {
        mTexId = GlUtil.genTextureId(GLES20.GL_TEXTURE_2D);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId);
        GlUtil.checkGlError("glBindFramebuffer");

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mTexId, 0);
        GlUtil.checkGlError("glFramebufferTexture2D");

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mWidth, mHeight,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GlUtil.checkGlError("glTexImage2D");

        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("glCheckFramebufferStatus error");
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public int getTextureId() {
        return mTexId;
    }

}
