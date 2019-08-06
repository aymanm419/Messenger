package com.example.messenger.Tools;

import android.content.Intent;
import android.graphics.Bitmap;

public class BitMapHandler {
    final static int COMPRESSED_RATIO = 13;
    final static int perPixelDataSize = 4;

    public Bitmap getResizedBitmapLessThanMaxSize(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width / (float) height;

        // For uncompressed bitmap, the data size is:
        // H * W * perPixelDataSize = H * H * bitmapRatio * perPixelDataSize
        //
        height = (int) Math.sqrt(maxSize * 1024 * COMPRESSED_RATIO / perPixelDataSize / bitmapRatio);
        width = (int) (height * bitmapRatio);
        Bitmap reduced_bitmap = Bitmap.createScaledBitmap(image, width, height, true);
        return reduced_bitmap;
    }

}
