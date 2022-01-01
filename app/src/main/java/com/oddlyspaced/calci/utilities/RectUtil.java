package com.sparkappdesign.archimedes.utilities;

import android.graphics.PointF;
import android.graphics.RectF;
/* loaded from: classes.dex */
public final class RectUtil {
    private RectUtil() {
    }

    public static RectF create(float x, float y, float width, float height) {
        return new RectF(x, y, x + width, y + height);
    }

    public static PointF getOrigin(RectF rect) {
        return new PointF(rect.left, rect.top);
    }

    public static RectF setOrigin(RectF rect, PointF origin) {
        return setOrigin(rect, origin.x, origin.y);
    }

    public static RectF setOrigin(RectF rect, float x, float y) {
        return new RectF(x, y, rect.width() + x, rect.height() + y);
    }

    public static RectF translate(RectF rect, PointF point) {
        return translate(rect, point.x, point.y);
    }

    public static RectF translate(RectF rect, float x, float y) {
        RectF rect2 = new RectF(rect);
        rect2.left += x;
        rect2.top += y;
        rect2.right += x;
        rect2.bottom += y;
        return rect2;
    }

    public static RectF setWidth(RectF rect, float width) {
        return new RectF(rect.left, rect.top, rect.left + width, rect.bottom);
    }

    public static RectF expand(RectF rect, float amount) {
        return new RectF(rect.left - amount, rect.top - amount, rect.right + amount, rect.bottom + amount);
    }

    public static RectF intersection(RectF rect1, RectF rect2) {
        return new RectF(Math.max(rect1.left, rect2.left), Math.max(rect1.top, rect2.top), Math.min(rect1.right, rect2.right), Math.min(rect1.bottom, rect2.bottom));
    }

    public static void setInterpolated(RectF rect, RectF initial, RectF target, float fraction) {
        rect.top = GeneralUtil.interpolate(initial.top, target.top, fraction);
        rect.left = GeneralUtil.interpolate(initial.left, target.left, fraction);
        rect.right = GeneralUtil.interpolate(initial.right, target.right, fraction);
        rect.bottom = GeneralUtil.interpolate(initial.bottom, target.bottom, fraction);
    }
}
