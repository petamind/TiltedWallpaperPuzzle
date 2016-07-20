package com.tungnd.android.wallpaperpuzzle.utils;

import java.security.SecureRandom;

/**
 * Created by Tung Doan Nguyen on 2/6/2016.
 */
public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private static SecureRandom random = new SecureRandom();
    /**
     * Returns a direction given an angle.
     * Directions are defined as follows:
     * <p/>
     * Up: [45, 135]
     * Right: [0,45] and [315, 360]
     * Down: [225, 315]
     * Left: [135, 225]
     *
     * @param angle an angle from 0 to 360 - e
     * @return the direction of an angle
     */
    public static Direction get(double angle) {
        if (inRange(angle, 45, 135)) {
            return Direction.UP;
        } else if (inRange(angle, 0, 45) || inRange(angle, 315, 360)) {
            return Direction.RIGHT;
        } else if (inRange(angle, 225, 315)) {
            return Direction.DOWN;
        } else {
            return Direction.LEFT;
        }
    }

    /**
     * @param angle an angle
     * @param init  the initial bound
     * @param end   the final bound
     * @return returns true if the given angle is in the interval [init, end).
     */
    private static boolean inRange(double angle, float init, float end) {
        return (angle >= init) && (angle < end);
    }

    public static Direction getRandomDirection(){
        return values()[random.nextInt(values().length)];
    }
}
