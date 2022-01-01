package com.oddlyspaced.calci.utilities.responder;

import com.oddlyspaced.calci.mathtype.views.MTMathTypeView;
import com.oddlyspaced.calci.utilities.observables.MutableObservable;
import com.oddlyspaced.calci.utilities.observables.Observable;
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
