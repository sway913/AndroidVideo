package com.net168.opengl;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public abstract class TextureRender {

    private static final String VERTEX_SHADER =
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "   gl_Position = aPosition;\n" +
            "   vTextureCoord = aTextureCoord.xy;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D uTexrue;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(uTexrue, vTextureCoord);\n" +
            "}\n";


    protected int mProgram;
    protected int mTextureId;

    protected int maPositionLoc;
    protected int maTextureCoordLoc;
    protected int muTexrueLoc;

    private int mRotate = 0;
    private boolean mMirror = false;
    private FloatBuffer mVertexCoords;
    private FloatBuffer mTexCoords;


    public TextureRender() {
        mProgram = GlUtil.createProgram(getVertexShader(), getFragmentShader());
        getLocIndex();
        mVertexCoords = TexTransformUtil.createFloatBuffer(TexTransformUtil.getVertexCoords());
        mTexCoords = TexTransformUtil.createFloatBuffer(TexTransformUtil.getTexCoords());
    }

    public void updateRotateAndMirror(int rotate, boolean mirror) {
        mRotate = rotate;
        mMirror = mirror;
        float[] data = TexTransformUtil.getRotateTexCoords(rotate);
        if (mirror)
            data = TexTransformUtil.getHFlipTexCoords(data);
        mTexCoords = TexTransformUtil.createFloatBuffer(data);
    }

    public void draw(int textureId) {

        GLES20.glUseProgram(mProgram);
        GlUtil.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GlUtil.checkGlError("glBindTexture");

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, mVertexCoords);
        GlUtil.checkGlError("glVertexAttribPointer");

        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mTexCoords);
        GlUtil.checkGlError("glVertexAttribPointer");

        GLES20.glUniform1i(muTexrueLoc, 0);   //0 也就是 GL_TEXTURE0
        GlUtil.checkGlError("glUniform1i");

        beforeDraw();

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GlUtil.checkGlError("glDrawArrays");

        afterDraw();

        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glBindTexture(getTextureTarget(), 0);
        GLES20.glUseProgram(0);

    }

    public int createTetureID() {
        mTextureId = GlUtil.genTextureId(getTextureTarget());
        return mTextureId;
    }

    protected void beforeDraw() {}

    protected void afterDraw() {}

    protected String getVertexShader() {
        return VERTEX_SHADER;
    }

    protected String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    protected abstract int getTextureTarget();

    protected void getLocIndex() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoordLoc");
        muTexrueLoc = GLES20.glGetUniformLocation(mProgram, "uTexrue");
        GlUtil.checkLocation(muTexrueLoc, "uTexrue");
    }


    public void release() {
        GLES20.glDeleteProgram(mProgram);
        GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
    }

}
