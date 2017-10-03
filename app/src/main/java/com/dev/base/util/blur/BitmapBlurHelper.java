package com.dev.base.util.blur;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.RSRuntimeException;

/**
 * 用于高斯模糊效果
 */
public final class BitmapBlurHelper {

    /**
     * 对Bitmap进行高斯模糊处理
     *
     * @param context Context
     * @param source  Bitmap
     * @return Bitmap
     */
    public static Bitmap blur(Context context, Bitmap source) {
        int sampling = 1;
        int radius = 25;

        int width = source.getWidth();
        int height = source.getHeight();
        int scaledWidth = width / sampling;
        int scaledHeight = height / sampling;
        Bitmap blurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(blurredBitmap);
        canvas.scale(1.0F / (float) sampling, 1.0F / (float) sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0.0F, 0.0F, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                blurredBitmap = RSBlur.blur(context, blurredBitmap, radius);
            } catch (RSRuntimeException var11) {
                blurredBitmap = FastBlur.blur(blurredBitmap, radius, true);
            }
        } else {
            blurredBitmap = FastBlur.blur(blurredBitmap, radius, true);
        }

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(blurredBitmap, source.getWidth(), source.getHeight(), true);
        blurredBitmap.recycle();
        return scaledBitmap;
    }

    /**
     * 做高斯模糊处理
     *
     * @param source Bitmap
     * @param radius 值越大越模糊，取值范围1~100
     */
    public static void blur(Bitmap source, int radius) {
        FastBlur.blur(source, radius, true);
    }

}