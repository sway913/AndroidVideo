package com.net168.capture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Range;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraUtils {

    public static boolean getSupportCamera2(Context context, boolean isFornt) {
        if (Build.VERSION.SDK_INT < 21)
            return false;
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String id : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == null
                        || facing != (isFornt ? CameraCharacteristics.LENS_FACING_FRONT : CameraCharacteristics.LENS_FACING_BACK)) {
                    continue;
                }
                Integer level = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                if (level == null)
                    continue;
                if (level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
                        || level == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
                    return true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Integer choosePreviewFormat(int[] lists, BaseBuilder config) {
        List<Integer> l = new ArrayList<>();
        for (int i : lists) {
            l.add(i);
        }
        return choosePreviewFormat(l, config);
    }

    public static Integer choosePreviewFormat(List<Integer> lists, BaseBuilder config) {
        Integer result = lists.get(0);
        for (int item : lists) {
            if (item == config.mFormat) {
                result = config.mFormat;
                break;
            }
        }
        config.mFormat = result;
        return result;
    }

    @SuppressLint("NewApi")
    public static int[] choosePreviewFps(Range<Integer>[] arrays, BaseBuilder config, boolean orgrin) {
        List<int[]> list = new ArrayList<>();
        for (Range<Integer> item : arrays) {
            list.add(new int[]{item.getLower() * 1000, item.getUpper() * 1000});
        }
        return choosePreviewFps(list, config, orgrin);
    }

    public static int[] choosePreviewFps(List<int[]> list, BaseBuilder config, boolean orgrin) {
        int maxFps = config.mMaxFps * 1000;
        int minFps = config.mMinFps * 1000;
        int[] resolution = new int[2];
        int weight = Integer.MAX_VALUE;
        for (int[] previewFpsRange : list) {
            if (minFps == previewFpsRange[0] && maxFps == previewFpsRange[1]) {
                resolution[0] = orgrin ? previewFpsRange[0] : minFps;
                resolution[1] = orgrin ? previewFpsRange[1] :maxFps;
                break;
            }
            int den = (previewFpsRange[0] - minFps) * (previewFpsRange[0] - minFps)
                    + (previewFpsRange[1] - maxFps) * (previewFpsRange[1] - maxFps);
            if (den <= weight) {
                resolution[0] = orgrin ? previewFpsRange[0] : Math.max(previewFpsRange[0], minFps);
                resolution[1] = orgrin ? previewFpsRange[1] : Math.min(previewFpsRange[1], maxFps);
                weight = den;
            }
        }
        config.mMinFps = resolution[0] / 1000;
        config.mMaxFps = resolution[1] / 1000;
        return resolution;
    }

    public static List<CameraUtils.Size> translateCamera1Size(List<Camera.Size> list) {
        List<CameraUtils.Size> result = new ArrayList<>();
        for (Camera.Size item : list) {
            result.add(new CameraUtils.Size(item.width, item.height));
        }
        return result;
    }

    @SuppressLint("NewApi")
    public static List<CameraUtils.Size> translateCamera2Size(android.util.Size[] list) {
        List<CameraUtils.Size> result = new ArrayList<>();
        for (android.util.Size item : list) {
            result.add(new CameraUtils.Size(item.getWidth(), item.getHeight()));
        }
        return result;
    }

    public static CameraUtils.Size choosePreviewSize(List<CameraUtils.Size> list, BaseBuilder config) {
        int weight;
        int minWeight = Integer.MAX_VALUE;
        int minWidth = 0;
        int minHeight = 0;
        int wantWeight = Integer.MAX_VALUE;
        int wantWidth = 0;
        int wantHeight = 0;
        for (CameraUtils.Size size : list) {
            weight = (int) (Math.pow((size.height - config.mHeight), 2) + Math.pow((size.width - config.mWidth), 2));
            if (weight < minWeight) {
                minHeight = size.height;
                minWidth = size.width;
                minWeight = weight;
            }
            if (size.height >= config.mHeight && size.width >= config.mWidth && weight < wantWeight) {
                wantHeight = size.height;
                wantWidth = size.width;
                wantWeight = weight;
            }
        }
        if (wantWidth == 0 || wantHeight == 0) {
            wantHeight = minHeight;
            wantWidth = minWidth;
        }
        config.mWidth = wantWidth;
        config.mHeight = wantHeight;
        return new CameraUtils.Size(wantWidth, wantHeight);
    }

    public static class Size {
        public int width;
        public int height;
        public Size(int w, int h) {
            width = w;
            height = h;
        }
    }


}
