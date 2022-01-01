package com.oddlyspaced.calci.utilities;

import android.content.Context;
/* loaded from: classes.dex */
public class DeviceUtil {
    private DeviceUtil() {
    }

    public static boolean isPortraitMode(Context context) {
        return context.getResources().getConfiguration().orientation == 1;
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }
}
