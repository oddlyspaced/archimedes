package com.sparkappdesign.archimedes.utilities;

import android.graphics.RectF;
import android.view.View;
import android.view.ViewParent;
/* loaded from: classes.dex */
public final class ViewUtil {
    private ViewUtil() {
    }

    public static boolean isDescendant(View candidateDescendant, View candidateParent) {
        for (ViewParent parent = candidateDescendant.getParent(); parent != null; parent = parent.getParent()) {
            if (parent == candidateParent) {
                return true;
            }
        }
        return false;
    }

    public static RectF getBoundsInScreenCoordinates(View view) {
        RectF viewBounds = new RectF((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom());
        int[] locationHolder = new int[2];
        view.getLocationOnScreen(locationHolder);
        return RectUtil.setOrigin(viewBounds, (float) locationHolder[0], (float) locationHolder[1]);
    }

    public static RectF getBoundsInParentCoordinates(View view) {
        return new RectF((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom());
    }
}
