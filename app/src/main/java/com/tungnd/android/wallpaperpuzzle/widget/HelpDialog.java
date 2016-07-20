package com.tungnd.android.wallpaperpuzzle.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tungnd.android.wallpaperpuzzle.R;
import com.tungnd.android.wallpaperpuzzle.utils.Fonts;

/**
 * Created by Tung Doan Nguyen on 1/31/2016.
 */
public class HelpDialog extends Dialog implements View.OnClickListener {
    public enum MODE {HELP_MODE, WIN_MODE, THUMBNAIL_MODE}

    ;// HELP_MODE =
    private MODE mode = MODE.HELP_MODE;
    private Bitmap bitmap;

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p/>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     * @see android.R.styleable#Theme_dialogTheme
     */
    public HelpDialog(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p/>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     * @see android.R.styleable#Theme_dialogTheme
     */
    public HelpDialog(Context context,  int themeResId, MODE mode, Bitmap bitmap) {
        this(context, themeResId, mode);
        this.bitmap = bitmap;
        init();
    }

    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p/>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     * @see android.R.styleable#Theme_dialogTheme
     */
    public HelpDialog(Context context, int themeResId, MODE mode) {
        super(context, themeResId);
        this.mode = mode;
        init();
    }

    /**
     * Creates a dialog window that uses a custom dialog style.
     * <p/>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     * <p/>
     * The supplied {@code theme} is applied on top of the context's theme. See
     * <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">
     * Style and Theme Resources</a> for more information about defining and
     * using styles.
     *
     * @param context    the context in which the dialog should run
     * @param themeResId a style resource describing the theme to use for the
     *                   window, or {@code 0} to use the default dialog theme
     */
    public HelpDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected HelpDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        final Window window = getWindow();
//        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT
//                , RelativeLayout.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        switch (mode) {
            case HELP_MODE:
                setContentView(R.layout.help_dialog);
                break;
            case THUMBNAIL_MODE:
                setContentView(R.layout.thumbnail);
                if(bitmap!=null)
                {
                    ((ImageView)findViewById(R.id.imageview)).setImageBitmap(bitmap);
                   // ((TextView) findViewById(R.id.textview)).setText(getContext().getString(R.string.sample_image));
                }
                break;
            case WIN_MODE:
                setContentView(R.layout.win_dialog);
                break;

        }

        findViewById(R.id.imageview).setOnClickListener(this);
        ((TextView) findViewById(R.id.textview)).setTypeface(Fonts.INSTRUCTION_FONT.getFont());
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        dismiss();
    }
}
