package com.oddlyspaced.calci.utilities.events;
/* loaded from: classes.dex */
public abstract class Observer<A> implements ObserverType {
    Event<A> mEvent;

    public abstract void handle(A a);

    @Override // com.oddlyspaced.calci.utilities.events.ObserverType
    public final void remove() {
        if (this.mEvent != null) {
            this.mEvent.remove(this);
        }
    }
}
