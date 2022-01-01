package com.sparkappdesign.archimedes.utilities;

import android.graphics.Matrix;
import android.graphics.PointF;
/* loaded from: classes.dex */
public final class MatrixUtil {
    private MatrixUtil() {
    }

    public static PointF mapPoints(PointF point, Matrix matrix) {
        float[] points = {point.x, point.y};
        matrix.mapPoints(points);
        return new PointF(points[0], points[1]);
    }
}
