package com.tungnd.android.wallpaperpuzzle.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Some image tool
 */
public final class ImageUtils {
    public static final Random RANDOM = new Random();

    public static Bitmap decodeSampledBitmapFromFile(String imagePath,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    public static Bitmap resize(Bitmap bm, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
    }

    /**
     * Create roundedBitmap
     * @param context
     * @param bm bitmap
     * @param radiusDp corner radius
     * @return
     */
    public static RoundedBitmapDrawable cutBitmapToRoundTile(Context context, final Bitmap bm, float radiusDp) {
        //final Bitmap[] tiles = cutBitmapToTiles(bm, tileHorizontal, tileVertical);
        //RoundedBitmapDrawable[] roundedTiles = new RoundedBitmapDrawable[tiles.length];
        Resources res = context.getResources();
        //for (int i = 0; i < tiles.length; i++) {
            RoundedBitmapDrawable dr =
                    RoundedBitmapDrawableFactory.create(res,bm);
            dr.setCornerRadius(radiusDp);
            //final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                   bm.recycle();
                }
            }).start();
        //}
        
        return dr;
    }

    /**
     * resize a bitmap to nxn width
     * @param context
     * @param drawableRrs
     * @param width
     * @return
     */
    public static Bitmap getFitBitmapSquare(Context context,int drawableRrs, int width){
        Bitmap boardBg = ImageUtils
                .decodeSampledBitmapFromResource(context.getResources()
                        , drawableRrs, (int) (width / ThisApp.BACKGROUND_SCALE_FACTOR), (int) (width / ThisApp.BACKGROUND_SCALE_FACTOR));
        if (boardBg.getHeight() != boardBg.getWidth()) {
            boardBg = ImageUtils.CropSquareCenter(boardBg);
        }
        if (boardBg.getHeight() > width) {
            boardBg = ImageUtils.resize(boardBg, width, width);
        }
        return boardBg;
    }



    /**
     * Cut a bitmap to number of tiles and return arrays of bitmap
     *
     * @param bm
     * @param tileHorizontal
     * @param tileVertical
     * @return tiles and recycle bm
     */
    public static Bitmap[] cutBitmapToTiles(final Bitmap bm, int tileHorizontal, int tileVertical) {
        Bitmap[] tiles = new Bitmap[tileHorizontal * tileVertical];
        int tileW = bm.getWidth() / tileHorizontal;
        int tileH = bm.getHeight() / tileVertical;
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = Bitmap.createBitmap(bm, (i%tileHorizontal)*tileW, (i/tileHorizontal)*tileH,tileW, tileH );
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                bm.recycle();
            }
        }).start();
        return tiles;
    }

    public static Drawable createDrawableFromFile(String imagePath) {
        return Drawable.createFromPath(imagePath);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Tung
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

       /* if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        else {*/
        inSampleSize = Math.round(((float) Math.max(height, width)) / Math.max(reqHeight, reqWidth));
//        }
        Log.d(ImageUtils.class.toString(), "sample:" + inSampleSize);
        return inSampleSize;//(int) Math.pow(2,inSampleSize)-1;
    }



    /**
     * Crop an image to a square x.x image
     *
     * @param bitmap original image will be changed and return as a cropped image
     * @return
     */
    public static Bitmap CropSquareCenter(Bitmap bitmap) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        final int size = Math.min(width, height);
        final int startxy = Math.abs(width - height) / 2;
        if (width > height) {
            bitmap = Bitmap.createBitmap(bitmap, startxy, 0, size, size);
        } else {
            bitmap = Bitmap.createBitmap(bitmap, 0, startxy, size, size);
        }
        return bitmap;
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Config.ARGB_4444);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(new Random().nextFloat());
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

}
