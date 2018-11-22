package com.net168.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TexTransformUtil {

    public static final float[] TEX_COORDS = new float[] {0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
    public static final float[] VERTEX_COORDS = new float[] {-1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F};

    public static float[] getTexCoords() {
        return TEX_COORDS;
    }

    public static float[] getVertexCoords() {
        return VERTEX_COORDS;
    }

    public static float[] getHFlipTexCoords() {
        return getHFlipTexCoords(TEX_COORDS);
    }

    public static float[] getHFlipTexCoords(float[] coords) {
        float[] data = new float[coords.length];
        System.arraycopy(coords, 0, data, 0, coords.length);

        System.arraycopy(coords, 2, data, 0, 2);
        System.arraycopy(coords, 0, data, 2, 2);
        System.arraycopy(coords, 6, data, 4, 2);
        System.arraycopy(coords, 4, data, 6, 2);
        return data;
    }

    public static float[] getVFlipTexCoords() {
        return getVFlipTexCoords(TEX_COORDS);
    }

    public static float[] getVFlipTexCoords(float[] coords) {
        float[] data = new float[coords.length];
        System.arraycopy(coords, 0, data, 0, coords.length);

        System.arraycopy(coords, 4, data, 0, 2);
        System.arraycopy(coords, 6, data, 2, 2);
        System.arraycopy(coords, 0, data, 4, 2);
        System.arraycopy(coords, 2, data, 6, 2);
        return data;
    }

    public static float[] getRotateTexCoords(int rotate) {
        return getRotateTexCoords(TEX_COORDS, rotate);
    }

    public static float[] getRotateTexCoords(float[] coords, int rotate) {
        float[] data = new float[coords.length];
        System.arraycopy(coords, 0, data, 0, coords.length);
        switch (rotate) {
            case 90:
                System.arraycopy(coords, 2, data, 0, 2);
                System.arraycopy(coords, 6, data, 2, 2);
                System.arraycopy(coords, 0, data, 4, 2);
                System.arraycopy(coords, 4, data, 6, 2);
                break;
            case 180:
                System.arraycopy(coords, 6, data, 0, 2);
                System.arraycopy(coords, 4, data, 2, 2);
                System.arraycopy(coords, 2, data, 4, 2);
                System.arraycopy(coords, 0, data, 6, 2);
                break;
            case 270:
                System.arraycopy(coords, 4, data, 0, 2);
                System.arraycopy(coords, 0, data, 2, 2);
                System.arraycopy(coords, 6, data, 4, 2);
                System.arraycopy(coords, 2, data, 6, 2);
                break;
            default:
        }
        return data;
    }


    public static FloatBuffer createFloatBuffer(float[] data) {
        ByteBuffer buf = ByteBuffer.allocateDirect(data.length * 4);
        buf.order(ByteOrder.nativeOrder());
        FloatBuffer result = buf.asFloatBuffer();
        result.put(data);
        result.position(0);
        return result;
    }

}
