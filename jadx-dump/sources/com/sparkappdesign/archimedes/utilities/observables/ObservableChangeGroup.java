package com.sparkappdesign.archimedes.utilities.observables;

import java.util.HashMap;
/* loaded from: classes.dex */
public final class ObservableChangeGroup {
    private HashMap<Observable<?>, ObservableChange<?>> mChanges = new HashMap<>();
    private boolean mPerformed = false;

    public <T> void setNewValue(MutableObservable<T> observable, T newValue, Object extraInfo) {
        setNewValueInternal(observable, newValue, extraInfo);
    }

    public <T> void setNewValue(MutableObservable<T> observable, T newValue) {
        setNewValue(observable, newValue, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void setNewValueInternal(Observable<T> observable, T newValue, Object extraInfo) {
        if (this.mPerformed) {
            throw new IllegalStateException("Can't modify a group after its changes have been performed.");
        }
        ObservableChange<T> existingChange = changeForObservable(observable);
        if (existingChange != null) {
            T existingNewValue = existingChange.mNewValue;
            if (existingNewValue != null) {
                if (existingNewValue.equals(newValue)) {
                    return;
                }
            } else if (existingChange == newValue) {
                return;
            }
            existingChange.mGroup = null;
        } else {
            T oldValue = observable.mValue;
            if (oldValue != null) {
                if (oldValue.equals(newValue)) {
                    return;
                }
            } else if (oldValue == newValue) {
                return;
            }
        }
        setNewValueUnchecked(observable, newValue, extraInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T> void setNewValueUnchecked(Observable<T> observable, T newValue, Object extraInfo) {
        ObservableChange<?> observableChange = new ObservableChange<>(observable, observable.mValue, newValue, this, extraInfo);
        this.mChanges.put(observable, observableChange);
        observableChange.raiseWillChange();
    }

    public <T> ObservableChange<T> changeForObservable(Observable<T> observable) {
        return (ObservableChange<T>) this.mChanges.get(observable);
    }

    public void performChanges() {
        if (this.mPerformed) {
            throw new IllegalStateException("Can't perform the same group of changes twice.");
        }
        for (ObservableChange<?> change : this.mChanges.values()) {
            change.setNewValue();
        }
        this.mPerformed = true;
        for (ObservableChange<?> change2 : this.mChanges.values()) {
            change2.raiseDidChange();
        }
    }
}
