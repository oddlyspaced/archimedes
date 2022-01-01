package com.oddlyspaced.calci.utilities;
/* loaded from: classes.dex */
public final class GeneralUtil {
    private GeneralUtil() {
    }

    public static int hashCode(Object object) {
        if (object != null) {
            return object.hashCode();
        }
        return 0;
    }

    public static int constrain(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return value > max ? max : value;
    }

    public static float constrain(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        return value > max ? max : value;
    }

    public static long constrain(long value, long min, long max) {
        if (value < min) {
            return min;
        }
        return value > max ? max : value;
    }

    public static int constrainMin(int value, int min) {
        return value < min ? min : value;
    }

    public static float constrainMin(float value, float min) {
        return value < min ? min : value;
    }

    public static long constrainMin(long value, long min) {
        return value < min ? min : value;
    }

    public static int constrainMax(int value, int max) {
        return value > max ? max : value;
    }

    public static float constrainMax(float value, float max) {
        return value > max ? max : value;
    }

    public static long constrainMax(long value, long max) {
        return value > max ? max : value;
    }

    public static float mapConstrained(float value, float fromMin, float fromMax, float toMin, float toMax) {
        return constrain((((value - fromMin) / (fromMax - fromMin)) * (toMax - toMin)) + toMin, Math.min(toMin, toMax), Math.max(toMin, toMax));
    }

    public static float interpolate(float initialValue, float finalValue, float fraction) {
        return ((finalValue - initialValue) * fraction) + initialValue;
    }

    public static boolean equalOrBothNull(Object a, Object b) {
        return (a != null && a.equals(b)) || (a == null && b == null);
    }

    public static boolean doesStringHaveMatchingParenthesesAroundIt(String string) {
        int depth = 0;
        if (string.length() == 0) {
            return false;
        }
        char[] charArray = string.toCharArray();
        for (char chr : charArray) {
            if (chr == '(') {
                depth++;
            } else if (chr == ')') {
                depth--;
            } else if (depth == 0) {
                return false;
            }
        }
        if (depth == 0) {
            return true;
        }
        return false;
    }
}
