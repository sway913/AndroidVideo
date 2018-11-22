package com.net168.opengl;

import android.opengl.GLES20;

public class FboRenderBufferDraw extends FboDraw {

    private int mRboId;

    FboRenderBufferDraw(int width, int height) {
        super(width, height);
        createRboId();
    }

    private void createRboId() {
        int[] rboIds = new int[1];
        GLES20.glGenRenderbuffers(1, rboIds, 0);
        GlUtil.checkGlError("glGenRenderbuffers");
        mRboId = rboIds[0];

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRboId);
        GlUtil.checkGlError("glBindRenderbuffer");
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, getFboId());
        GlUtil.checkGlError("glBindFramebuffer");

        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT, mWidth, mHeight);
        GlUtil.checkGlError("glRenderbufferStorage");

        GLES20.glFramebufferRenderbuffer(GLES20.GL_RENDERBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_RENDERBUFFER, mRboId);
        GlUtil.checkGlError("glFramebufferRenderbuffer");

        if (GLES20.glCheckFramebufferStatus(getFboId()) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("glCheckFramebufferStatus error");
        }

        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_FRAMEBUFFER, 0);

    }

    @Override
    public void release() {
        super.release();
        GLES20.glDeleteRenderbuffers(1, new int[] {mRboId}, 0);
        GlUtil.checkGlError("glDeleteRenderbuffers");
    }

    public int getRenderBufferId() {
        return mRboId;
    }

}
