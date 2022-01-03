package com.oddlyspaced.calci.utilities.events;
/* loaded from: classes.dex */
public abstract class Observer<A> implements ObserverType {
    Event<A> mEvent;

    // com.oddlyspaced.calci.utilities.events.Observer
    public abstract void handle(A obj);

    @Override // com.oddlyspaced.calci.utilities.events.ObserverType
    public final void remove() {
        if (this.mEvent != null) {
            this.mEvent.remove(this);
        }
    }
}
