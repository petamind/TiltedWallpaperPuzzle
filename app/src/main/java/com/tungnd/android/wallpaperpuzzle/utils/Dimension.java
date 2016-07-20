package com.tungnd.android.wallpaperpuzzle.utils;

public final class Dimension {
    public int width;
    public int height;

    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Dimension(Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

}
