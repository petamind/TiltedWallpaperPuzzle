package com.tungnd.android.wallpaperpuzzle.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Tung Doan Nguyen on 1/28/2016.
 */
public enum Fonts {
    MAIN_FONT(ThisApp.getContext().getAssets(), "block.ttf"), STAT_FONT(ThisApp.getContext().getAssets(), "zorque.ttf"),
    INSTRUCTION_FONT(ThisApp.getContext().getAssets(), "barbie.ttf");
    private Typeface font;

    private static final Typeface statFont = Typeface.createFromAsset(
            ThisApp.getContext().getAssets(), "zorque.ttf");

    private static final Typeface instructionFont = Typeface.createFromAsset(
            ThisApp.getContext().getAssets(), "barbie.ttf");


    Fonts(AssetManager assets, String fontName) {
        this.font = Typeface.createFromAsset(
                assets, fontName);
    }

    public Typeface getFont() {
        return font;
    }
}
