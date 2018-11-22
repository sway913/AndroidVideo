package com.net168.opengl;

import android.opengl.GLES20;

public abstract class FboDraw {

    protected int mFboId;

    protected int mWidth;
    protected int mHeight;

    FboDraw(int width, int height) {
        mWidth = width;
        mHeight = height;
        createFbo();
    }

    private void createFbo() {
        int[] fboIds = new int[1];
        GLES20.glGenFramebuffers(1, fboIds, 0);
        GlUtil.checkGlError("glGenFramebuffers");
        mFboId = fboIds[0];
    }

    public int getFboId() {
        return mFboId;
    }

    public void release() {
        GLES20.glDeleteFramebuffers(1, new int[] {mFboId}, 0);
        GlUtil.checkGlError("glDeleteFramebuffers");
    }

}
