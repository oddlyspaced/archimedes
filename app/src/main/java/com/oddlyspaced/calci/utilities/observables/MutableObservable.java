package com.oddlyspaced.calci.utilities.observables;
/* loaded from: classes.dex */
public class MutableObservable<T> extends Observable<T> {
    @Override // com.oddlyspaced.calci.utilities.observables.Observable
    public final void setValue(T newValue) {
        super.setValue(newValue);
    }

    public MutableObservable() {
    }

    public MutableObservable(T initialValue) {
        this.mValue = initialValue;
    }
}
