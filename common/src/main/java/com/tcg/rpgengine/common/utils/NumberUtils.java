package com.tcg.rpgengine.common.utils;

public interface NumberUtils {

    static boolean isInt(float x) {
        return Double.compare(x, Math.floor(x)) == 0;
    }

    static String toString(float x) {
        return isInt(x) ? Integer.toString((int) x) : Float.toString(x);
    }

}
