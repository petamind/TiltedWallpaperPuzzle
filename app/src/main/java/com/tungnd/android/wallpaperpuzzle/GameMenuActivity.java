package com.tungnd.android.wallpaperpuzzle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tungnd.android.wallpaperpuzzle.utils.Fonts;
import com.tungnd.android.wallpaperpuzzle.utils.ImageUtils;
import com.tungnd.android.wallpaperpuzzle.utils.MusicManager;
import com.tungnd.android.wallpaperpuzzle.utils.ThisApp;
import com.tungnd.android.wallpaperpuzzle.R;

import fr.castorflex.android.flipimageview.library.FlipImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameMenuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FLIP";
    private BitmapDrawable backgroundBitmapDrawable;
    private ViewGroup container;
    private FlipImageView musicButton;
    private int boardsize = 3;
    private ImageButton shareButton;
    private TextView startBtn;
    private TextView helpBtn;
    private TextView sizeBtn;
    private ProgressDialog progressDialog;
    private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        boardsize = ThisApp.getSharedPreferences().getInt(getString(R.string.key_size), 3);

        container = (ViewGroup) findViewById(R.id.container);

        TextView title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.MAIN_FONT.getFont());

        musicButton = (FlipImageView) findViewById(R.id.music_button);
        musicButton.setOnClickListener(this);
        shareButton = (ImageButton) findViewById(R.id.share_button);
        shareButton.setOnClickListener(this);

        startBtn = (TextView) findViewById(R.id.start_button);
        startBtn.setTypeface(Fonts.STAT_FONT.getFont());
        startBtn.setOnClickListener(this);
        helpBtn = (TextView) findViewById(R.id.help_button);
        helpBtn.setTypeface(Fonts.STAT_FONT.getFont());
        helpBtn.setOnClickListener(this);
        sizeBtn = (TextView) findViewById(R.id.size_button);
        sizeBtn.setTypeface(Fonts.STAT_FONT.getFont());
        sizeBtn.setOnClickListener(this);
        sizeBtn.setText(boardsize + "x" + boardsize);

    }


    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setIcon(R.drawable.ic_cart);
            progressDialog.setMessage(getString(R.string.please_wait));
        }
        if (set) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        } else {
            progressDialog.dismiss();
        }
    }
    //----------------------------------------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setBackground();
            }
        }, 0);
        updateGUI();
        startBtn.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        sizeBtn.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        helpBtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slice_in_right));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            super.onBackPressed();

            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.confirm_exit, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    /**
     * update gui, including
     */
    private void updateGUI() {
        if (ThisApp.getSharedPreferences().getBoolean(getString(R.string.music_on), true)) {
            MusicManager.start(this, MusicManager.MUSIC_MENU);
            if (musicButton.isFlipped())
                musicButton.toggleFlip();
        } else {
            if (!musicButton.isFlipped())
                musicButton.toggleFlip();
        }
        if (ThisApp.getSharedPreferences().getBoolean(getString(R.string.premium), false)) {
            findViewById(R.id.upgrade_button).setVisibility(View.GONE);
        }
    }

    private void setBackground() {
        backgroundBitmapDrawable = new BitmapDrawable(
                getResources(),
                ImageUtils.decodeSampledBitmapFromResource(
                        getResources(),
                        R.drawable.splash,
                        (int) (ThisApp.getScreenDimension().width / ThisApp.BACKGROUND_SCALE_FACTOR),
                        (int) (ThisApp.getScreenDimension().height / ThisApp.BACKGROUND_SCALE_FACTOR)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            container.setBackground(backgroundBitmapDrawable);
        } else {
            container.setBackgroundDrawable(backgroundBitmapDrawable);
        }
    }

    public void onBuyNoAdsButtonClicked(View view) {
        Toast.makeText(this, "Demo only! Create your own app purchase!", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boardsize = savedInstanceState.getInt("SIZE");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SIZE", boardsize);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
//        v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
        switch (v.getId()) {
            case R.id.music_button:
                ((FlipImageView) v).toggleFlip();
                if (MusicManager.isPlaying()) {
                    MusicManager.pause();
                    ThisApp.getSharedPreferences().edit().putBoolean(getString(R.string.music_on), false).commit();
                } else {
                    MusicManager.start(this, MusicManager.MUSIC_MENU);
                    ThisApp.getSharedPreferences().edit().putBoolean(getString(R.string.music_on), true).commit();
                }
                break;
            case R.id.start_button:
                Intent intent = new Intent(this, GamePanelActivity.class);
                intent.putExtra("SIZE", boardsize);
                startActivity(intent);
                break;
            case R.id.size_button:
                boardsize = (++boardsize % 9 == 0 ? 2 : boardsize);
                ((Button) v).setText(boardsize + "x" + boardsize);
                ThisApp.getSharedPreferences().edit().putInt(getString(R.string.key_size), boardsize).commit();
                break;
            case R.id.share_button:
                ThisApp.share(this);
                break;
            case R.id.rate:
                ThisApp.rate(this);
                break;
            case R.id.help_button:
                ThisApp.help(this);

                break;
        }
    }

}
