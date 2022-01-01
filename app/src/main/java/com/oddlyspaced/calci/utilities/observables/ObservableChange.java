package com.oddlyspaced.calci.utilities.observables;

import com.oddlyspaced.calci.utilities.events.Condition;
/* loaded from: classes.dex */
public final class ObservableChange<T> {
    Object mExtraInfo;
    ObservableChangeGroup mGroup;
    T mNewValue;
    Observable<T> mObservable;
    T mOldValue;

    public Observable<T> getObservable() {
        return this.mObservable;
    }

    public T getOldValue() {
        return this.mOldValue;
    }

    public T getNewValue() {
        return this.mNewValue;
    }

    public ObservableChangeGroup getGroup() {
        return this.mGroup;
    }

    public Object getExtraInfo() {
        return this.mExtraInfo;
    }

    public ObservableChange(Observable<T> observable, T oldValue, T newValue, ObservableChangeGroup group, Object extraInfo) {
        this.mObservable = observable;
        this.mOldValue = oldValue;
        this.mNewValue = newValue;
        this.mGroup = group;
        this.mExtraInfo = extraInfo;
    }

    public void raiseWillChange() {
        if (this.mObservable.mWillChange != null) {
            this.mObservable.mWillChange.raise(this, new Condition() { // from class: com.oddlyspaced.calci.utilities.observables.ObservableChange.1
                @Override // com.oddlyspaced.calci.utilities.events.Condition
                public boolean isSatisfied() {
                    return ObservableChange.this.mGroup == null;
                }
            });
        }
    }

    public void setNewValue() {
        this.mObservable.mValue = this.mNewValue;
    }

    public void raiseDidChange() {
        if (this.mObservable.mDidChange != null) {
            this.mObservable.mDidChange.raise(this);
        }
    }
}
