package com.oddlyspaced.calci.utilities.observables;
/* loaded from: classes.dex */
public interface ObservableChainLink<T, U> {
    Observable<U> get(T t);
}
