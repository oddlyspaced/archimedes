package com.sparkappdesign.archimedes.utilities.events;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class Event<A> {
    ArrayList<Observer<A>> mObservers = new ArrayList<>();

    public final boolean hasObservers() {
        return this.mObservers.size() > 0;
    }

    public Observer<A> add(Observer<A> observer) {
        observer.remove();
        observer.mEvent = this;
        this.mObservers.add(observer);
        return observer;
    }

    public void remove(Observer<A> observer) {
        if (observer.mEvent == this) {
            observer.mEvent = null;
            this.mObservers.remove(observer);
        }
    }
}
