package com.sparkappdesign.archimedes.utilities.responder;

import com.sparkappdesign.archimedes.mathtype.views.MTMathTypeView;
import com.sparkappdesign.archimedes.utilities.observables.MutableObservable;
import com.sparkappdesign.archimedes.utilities.observables.Observable;
/* loaded from: classes.dex */
public class ResponderManager {
    private static MutableObservable<Responder> mFirstResponder = new MutableObservable<>();

    public static Observable<Responder> getFirstResponder() {
        return mFirstResponder;
    }

    public static void setFirstResponder(MTMathTypeView newFirstResponder) {
        mFirstResponder.setValue(newFirstResponder);
    }
}
