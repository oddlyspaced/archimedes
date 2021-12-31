package com.sparkappdesign.archimedes.utilities.observables;

import com.sparkappdesign.archimedes.utilities.WeakableReference;
import com.sparkappdesign.archimedes.utilities.events.Event;
import com.sparkappdesign.archimedes.utilities.events.Observer;
import com.sparkappdesign.archimedes.utilities.events.OwnedEvent;
import java.lang.ref.WeakReference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ObservableChain<B, C> extends Observable<C> {
    private Observable<B> mBase;
    private BaseObserver<B, C> mBaseObserver;
    private Observable<C> mChild;
    private ChildObserver<B, C> mChildObserver;
    private ObservableChainLink<B, C> mLink;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ObservableChain(Observable<B> base, ObservableChainLink<B, C> link) {
        this.mBase = base;
        this.mLink = link;
        this.mChild = link.get(base.mValue);
        if (this.mChild != null) {
            this.mValue = this.mChild.mValue;
        }
        this.mBaseObserver = new BaseObserver<>(this);
        base.getWillChange().add(this.mBaseObserver);
        updateChildObserver();
    }

    private void updateChildObserver() {
        if (this.mChildObserver != null) {
            this.mChildObserver.remove();
        }
        if (this.mChild != null) {
            this.mChildObserver = new ChildObserver<>(this);
            this.mChild.getWillChange().add(this.mChildObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBaseValueWillChange(ObservableChange<B> change) {
        ObservableChange<C> childChange;
        C newValue = null;
        this.mChild = this.mLink.get(change.mNewValue);
        updateChildObserver();
        if (change.mGroup == null || this.mChild == null) {
            childChange = null;
        } else {
            childChange = change.mGroup.changeForObservable(this.mChild);
        }
        if (childChange != null) {
            newValue = (C) childChange.mNewValue;
        } else if (this.mChild != null) {
            newValue = this.mChild.mValue;
        }
        change.mGroup.setNewValueInternal(this, newValue, childChange != null ? childChange.mExtraInfo : change.mExtraInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleChildValueWillChange(ObservableChange<C> change) {
        change.mGroup.setNewValueInternal(this, change.mNewValue, change.mExtraInfo);
    }

    @Override // com.sparkappdesign.archimedes.utilities.observables.Observable
    public Event<ObservableChange<C>> getWillChange() {
        if (this.mWillChange != null) {
            return this.mWillChange;
        }
        ChainOwnedEvent chainOwnedEvent = new ChainOwnedEvent();
        this.mWillChange = chainOwnedEvent;
        return chainOwnedEvent;
    }

    @Override // com.sparkappdesign.archimedes.utilities.observables.Observable
    public Event<ObservableChange<C>> getDidChange() {
        if (this.mDidChange != null) {
            return this.mDidChange;
        }
        ChainOwnedEvent chainOwnedEvent = new ChainOwnedEvent();
        this.mDidChange = chainOwnedEvent;
        return chainOwnedEvent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateShouldKeepAlive() {
        this.mBaseObserver.setShouldKeepChainAlive((this.mWillChange != null && this.mWillChange.hasObservers()) || (this.mDidChange != null && this.mDidChange.hasObservers()));
    }

    /* loaded from: classes.dex */
    private final class ChainOwnedEvent<A> extends OwnedEvent<A> {
        private ChainOwnedEvent() {
        }

        @Override // com.sparkappdesign.archimedes.utilities.events.Event
        public Observer<A> add(Observer<A> observer) {
            Observer<A> result = super.add(observer);
            ObservableChain.this.updateShouldKeepAlive();
            return result;
        }

        @Override // com.sparkappdesign.archimedes.utilities.events.Event
        public void remove(Observer<A> observer) {
            super.remove(observer);
            ObservableChain.this.updateShouldKeepAlive();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class BaseObserver<B, C> extends Observer<ObservableChange<B>> {
        private WeakableReference<ObservableChain<B, C>> mChain;

        @Override // com.sparkappdesign.archimedes.utilities.events.Observer
        public /* bridge */ /* synthetic */ void handle(Object obj) {
            handle((ObservableChange) ((ObservableChange) obj));
        }

        BaseObserver(ObservableChain<B, C> chain) {
            this.mChain = new WeakableReference<>(chain);
        }

        public void handle(ObservableChange<B> change) {
            ObservableChain<B, C> chain = this.mChain.get();
            if (chain != null) {
                chain.handleBaseValueWillChange(change);
            }
        }

        public void setShouldKeepChainAlive(boolean shouldKeepChainAlive) {
            this.mChain.setIsWeak(!shouldKeepChainAlive);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ChildObserver<B, C> extends Observer<ObservableChange<C>> {
        WeakReference<ObservableChain<B, C>> mChain;

        @Override // com.sparkappdesign.archimedes.utilities.events.Observer
        public /* bridge */ /* synthetic */ void handle(Object obj) {
            handle((ObservableChange) ((ObservableChange) obj));
        }

        ChildObserver(ObservableChain<B, C> chain) {
            this.mChain = new WeakReference<>(chain);
        }

        public void handle(ObservableChange<C> change) {
            ObservableChain<B, C> chain = this.mChain.get();
            if (chain != null) {
                chain.handleChildValueWillChange(change);
            }
        }
    }
}
