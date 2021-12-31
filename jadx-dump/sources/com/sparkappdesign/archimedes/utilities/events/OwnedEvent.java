package com.sparkappdesign.archimedes.utilities.events;

import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public class OwnedEvent<A> extends Event<A> {
    public final void raise(A args, Condition cancelCondition) {
        Iterator<Observer<A>> it = new ArrayList<>(this.mObservers).iterator();
        while (it.hasNext()) {
            Observer<A> observer = it.next();
            if (cancelCondition == null || !cancelCondition.isSatisfied()) {
                observer.handle(args);
            } else {
                return;
            }
        }
    }

    public final void raise(A args) {
        raise(args, null);
    }
}
