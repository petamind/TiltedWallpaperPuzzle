package com.tungnd.android.wallpaperpuzzle.utils;

import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Created by tunguyen on 4/02/2016.
 */
public class TileAnimationListener implements Animation.AnimationListener {
    private View tile;
    public TileAnimationListener(View tile){
        this.tile = tile;
    }
    @Override
    public void onAnimationEnd(Animation animation) {
        tile.clearAnimation();
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(tile.getWidth(), tile.getHeight());
//        lp.setMargins(50, 100, 0, 0);
//        tile.setLayoutParams(lp);
        //tile.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }
}
