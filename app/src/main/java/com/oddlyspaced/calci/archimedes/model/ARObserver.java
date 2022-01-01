package com.sparkappdesign.archimedes.archimedes.model;

import com.sparkappdesign.archimedes.utilities.events.ObserverType;
/* loaded from: classes.dex */
public abstract class ARObserver implements ObserverType {
    private ARObserverDelegate mDelegate;

    public ARObserverDelegate getDelegate() {
        return this.mDelegate;
    }

    public void setDelegate(ARObserverDelegate delegate) {
        this.mDelegate = delegate;
    }

    public void notifyDidChange() {
        if (this.mDelegate != null) {
            this.mDelegate.observerDidObserveChange(this);
        }
    }
}
