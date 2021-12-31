package com.sparkappdesign.archimedes.utilities.responder;

import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes.dex */
public final class ResponderUtil {
    private ResponderUtil() {
    }

    public static boolean isFirstResponder(Responder responder) {
        return ResponderManager.getFirstResponder().getValue() == responder;
    }

    public static boolean containsFirstResponder(Responder responder) {
        return findFirstResponder(responder) != null;
    }

    public static Responder findFirstResponder(Responder responder) {
        Responder firstResponder;
        if (isFirstResponder(responder)) {
            return responder;
        }
        if (responder instanceof ViewGroup) {
            ViewGroup responderAsViewGroup = (ViewGroup) responder;
            for (int i = 0; i < responderAsViewGroup.getChildCount(); i++) {
                View child = responderAsViewGroup.getChildAt(i);
                if ((child instanceof Responder) && (firstResponder = findFirstResponder((Responder) child)) != null) {
                    return firstResponder;
                }
            }
        }
        return null;
    }
}
