package com.net168.opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

public class TextureMatrixOESRender extends TextureRender {

    private static final String VERTEX_SHADER =
            "uniform mat4 uPosMatrix;\n" +
            "uniform mat4 uTexMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "   gl_Position = uPosMatrix * aPosition;\n" +
            "   vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
            "}\n";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES uTexrue;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(uTexrue, vTextureCoord);\n" +
            "}\n";

    private static final float[] DEFLAUT_POS_MATRIX = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    private static final float[] DEFLAUT_TEX_MATRIX = {
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    };

    private int muPosMatrixLoc;
    private int muTexMatrixLoc;

    private float[] mPosMatrix = DEFLAUT_POS_MATRIX;
    private float[] mTexMatrix = DEFLAUT_TEX_MATRIX;

    public void updatePosMatrix(float[] matrix) {
        mPosMatrix = matrix;
    }

    public void updateTexMatrix(float[] matrix) {
        mTexMatrix = matrix;
    }

    @Override
    protected void beforeDraw() {
        GLES20.glUniformMatrix4fv(muPosMatrixLoc, 1, false, mPosMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, mTexMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");
    }


    @Override
    protected int getTextureTarget() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    protected String getFragmentShader() {
        return FRAGMENT_SHADER;
    }

    @Override
    public String getVertexShader() {
        return VERTEX_SHADER;
    }

    @Override
    protected void getLocIndex() {
        super.getLocIndex();
        muPosMatrixLoc = GLES20.glGetUniformLocation(mProgram, "uPosMatrix");
        GlUtil.checkLocation(muPosMatrixLoc, "uPosMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgram, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");
    }
}
