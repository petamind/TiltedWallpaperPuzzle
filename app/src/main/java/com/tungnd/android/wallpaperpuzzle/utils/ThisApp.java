package com.tungnd.android.wallpaperpuzzle.utils;

import android.app.Application;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.tungnd.android.wallpaperpuzzle.R;
import com.tungnd.android.wallpaperpuzzle.widget.HelpDialog;

import java.io.IOException;

/**
 * Created by Tung Doan Nguyen on 1/28/2016.
 * Contains single app context
 */
public class ThisApp extends Application {

    public static final float BACKGROUND_SCALE_FACTOR = 2.0f;
    private static Context context;
    private static Dimension deviceDimension;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setDeviceDimension();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public static Context getContext() {
        return context;
    }

    /**
     * Device screen dimensions
     *
     * @return
     */
    public static final Dimension getScreenDimension() {
        return ThisApp.deviceDimension;
    }

    private static void setDeviceDimension() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
            // only for android older than gingerbread
            deviceDimension = new Dimension(display.getWidth(),
                    display.getHeight());
        } else {
            Point size = new Point();
            display.getSize(size);
            ThisApp.deviceDimension = new Dimension(Math.min(size.x, size.y),
                    Math.max(size.x, size.y));
        }
    }

    /**
     * Share this app to social netwokr
     * @param context
     */
    public static void share(Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share)+context.getPackageName());
        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.send_to)));
    }

    public static void rate(Context context) {
        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void setWallPaper(final Context context, final int drawableId, final float scale) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(R.string.please_wait);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setMessage(context.getString(R.string.setting_wallpaper));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressDialog.dismiss();
                Toast.makeText(context, R.string.finish_setting_wall, Toast.LENGTH_LONG).show();
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

                Bitmap bitmap = ImageUtils.decodeSampledBitmapFromResource(context.getResources()
                        , drawableId
                        , (int) (ThisApp.getScreenDimension().width / scale)
                        , (int) (ThisApp.getScreenDimension().height / scale));
                try {
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    handler.sendEmptyMessage(0);
                    if(bitmap!=null && !bitmap.isRecycled())
                    {
                        Log.d(ThisApp.class.toString(),"Wall: "+bitmap.getWidth()+":"+bitmap.getHeight());
                        bitmap.recycle();
                    }
                }
            }
        }).start();
    }

    public static void help(Context context) {
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(((Activity) context).getWindow().getAttributes());

        /*HelpDialog dialog = */new HelpDialog(context, R.style.DialogThemeFullScreen).show();//, R.style.DialogThemeFullScreen).show();
//        dialog.getWindow().setAttributes(lp);
//        dialog.show();
    }
}
