package com.sparkappdesign.archimedes.utilities;

import android.content.Context;
import android.graphics.Typeface;
import java.util.Hashtable;
/* loaded from: classes.dex */
public final class TypefaceCache {
    private static final Hashtable<String, Typeface> FONT_CACHE = new Hashtable<>();
    public static final String MYRIAD_PRO_BOLD = "myriad_pro_bold.otf";
    public static final String MYRIAD_PRO_LIGHT = "myriad_pro_light.otf";

    public static Typeface getMyriadProLight(Context context) {
        if (get(MYRIAD_PRO_LIGHT) == null) {
            put(context, MYRIAD_PRO_LIGHT);
        }
        return get(MYRIAD_PRO_LIGHT);
    }

    public static Typeface getMyriadProBold(Context context) {
        if (get(MYRIAD_PRO_BOLD) == null) {
            put(context, MYRIAD_PRO_BOLD);
        }
        return get(MYRIAD_PRO_BOLD);
    }

    private TypefaceCache() {
    }

    public static Typeface get(String assetPath) {
        Typeface typeface;
        synchronized (FONT_CACHE) {
            typeface = FONT_CACHE.get(assetPath);
        }
        return typeface;
    }

    public static void put(Context context, String assetPath) {
        synchronized (FONT_CACHE) {
            if (!FONT_CACHE.containsKey(assetPath)) {
                try {
                    FONT_CACHE.put(assetPath, Typeface.createFromAsset(context.getAssets(), assetPath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
