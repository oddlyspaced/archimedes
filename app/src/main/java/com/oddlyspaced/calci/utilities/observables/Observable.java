package com.oddlyspaced.calci.utilities.observables;

import com.oddlyspaced.calci.utilities.events.Event;
import com.oddlyspaced.calci.utilities.events.Observer;
import com.oddlyspaced.calci.utilities.events.ObserverType;
import com.oddlyspaced.calci.utilities.events.OwnedEvent;
/* loaded from: classes.dex */
public abstract class Observable<T> {
    OwnedEvent<ObservableChange<T>> mDidChange;
    private boolean mIsChanging = false;
    T mValue;
    OwnedEvent<ObservableChange<T>> mWillChange;

    public Event<ObservableChange<T>> getWillChange() {
        if (this.mWillChange != null) {
            return this.mWillChange;
        }
        OwnedEvent<ObservableChange<T>> ownedEvent = new OwnedEvent<>();
        this.mWillChange = ownedEvent;
        return ownedEvent;
    }

    public Event<ObservableChange<T>> getDidChange() {
        if (this.mDidChange != null) {
            return this.mDidChange;
        }
        OwnedEvent<ObservableChange<T>> ownedEvent = new OwnedEvent<>();
        this.mDidChange = ownedEvent;
        return ownedEvent;
    }

    public final T getValue() {
        return this.mValue;
    }

    public void setValue(T newValue) {
        if (this.mValue != null) {
            if (this.mValue.equals(newValue)) {
                return;
            }
        } else if (this.mValue == newValue) {
            return;
        }
        if (this.mIsChanging) {
            throw new IllegalStateException("Observable value set during notification of a previous change.");
        }
        this.mIsChanging = true;
        if (this.mWillChange != null && this.mWillChange.hasObservers()) {
            ObservableChangeGroup group = new ObservableChangeGroup();
            group.setNewValueUnchecked(this, newValue, null);
            group.performChanges();
        } else if (this.mDidChange == null || !this.mDidChange.hasObservers()) {
            this.mValue = newValue;
        } else {
            ObservableChange<T> change = new ObservableChange<>(this, this.mValue, newValue, null, null);
            change.setNewValue();
            change.raiseDidChange();
        }
        this.mIsChanging = false;
    }

    public final ObserverType addObserver(Observer<ObservableChange<T>> observer) {
        return getDidChange().add(observer);
    }

    public final ObserverType addObserver(ValueObserver<T> observer) {
        observer.attachTo(this);
        return observer;
    }

    public final <E> ObserverType addObserver(ListObserver<E> observer) {
        observer.attachTo(this);
        return observer;
    }

    public final <C> Observable<C> chain(ObservableChainLink<T, C> link) {
        return new ObservableChain(this, link);
    }
}
