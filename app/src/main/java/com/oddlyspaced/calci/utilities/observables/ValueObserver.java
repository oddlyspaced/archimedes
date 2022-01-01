package com.oddlyspaced.calci.utilities.observables;

import com.oddlyspaced.calci.utilities.events.Observer;
import com.oddlyspaced.calci.utilities.events.ObserverType;
/* loaded from: classes.dex */
public abstract class ValueObserver<T> implements ObserverType {
    private ObserverType mObserver;

    public abstract void handle(T t);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void attachTo(Observable<T> observable) {
        remove();
        this.mObserver = observable.getDidChange().add(new Observer<ObservableChange<T>>() { // from class: com.oddlyspaced.calci.utilities.observables.ValueObserver.1
            @Override // com.oddlyspaced.calci.utilities.events.Observer
            public /* bridge */ /* synthetic */ void handle(Object obj) {
                handle((ObservableChange) ((ObservableChange) obj));
            }

            public void handle(ObservableChange<T> change) {
                ValueObserver.this.handle(change.mNewValue);
            }
        });
    }

    @Override // com.oddlyspaced.calci.utilities.events.ObserverType
    public final void remove() {
        if (this.mObserver != null) {
            this.mObserver.remove();
            this.mObserver = null;
        }
    }
}
