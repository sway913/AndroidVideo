package com.net168.opengl;

import android.opengl.GLES11Ext;

public class TextureOESRender extends TextureRender {

    public static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform samplerExternalOES uTexrue;\n" +
            "void main() {\n" +
            "   gl_FragColor = texture2D(uTexrue, vTextureCoord);\n" +
            "}\n";

    @Override
    protected int getTextureTarget() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    protected String getFragmentShader() {
        return FRAGMENT_SHADER;
    }
}
