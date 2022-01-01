package com.sparkappdesign.archimedes.utilities;

import android.graphics.PointF;
import android.graphics.RectF;
/* loaded from: classes.dex */
public final class PointUtil {
    private PointUtil() {
    }

    public static PointF addPoints(PointF point1, PointF point2) {
        return new PointF(point1.x + point2.x, point1.y + point2.y);
    }

    public static PointF subtractPoints(PointF point1, PointF point2) {
        return new PointF(point1.x - point2.x, point1.y - point2.y);
    }

    public static float distanceToRectAsFloat(PointF point, RectF rect) {
        if (rect.contains(point.x, point.y)) {
            return 0.0f;
        }
        return (float) distanceToPoint(point, new PointF(GeneralUtil.constrain(point.x, rect.left, rect.right), GeneralUtil.constrain(point.y, rect.top, rect.bottom)));
    }

    public static PointF distanceToRectAsPoint(PointF point, RectF rect) {
        if (rect.contains(point.x, point.y)) {
            return new PointF();
        }
        return new PointF(Math.abs(point.x - GeneralUtil.constrain(point.x, rect.left, rect.right)), Math.abs(point.y - GeneralUtil.constrain(point.y, rect.top, rect.bottom)));
    }

    public static double distanceToPoint(PointF point1, PointF point2) {
        return Math.hypot((double) (point2.x - point1.x), (double) (point2.y - point1.y));
    }

    public static void setInterpolated(PointF point, PointF initial, PointF target, float fraction) {
        point.x = GeneralUtil.interpolate(initial.x, target.x, fraction);
        point.y = GeneralUtil.interpolate(initial.y, target.y, fraction);
    }
}
