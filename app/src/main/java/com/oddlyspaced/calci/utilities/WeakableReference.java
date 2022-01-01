package com.oddlyspaced.calci.utilities;

import java.lang.ref.WeakReference;
/* loaded from: classes.dex */
public class WeakableReference<T> {
    private boolean mIsWeak = false;
    private T mStrongReference;
    private WeakReference<T> mValue;

    public T get() {
        return this.mValue.get();
    }

    public boolean getIsWeak() {
        return this.mIsWeak;
    }

    public void setIsWeak(boolean isWeak) {
        this.mIsWeak = isWeak;
        updateStrongReference();
    }

    public WeakableReference(T value) {
        this.mValue = new WeakReference<>(value);
        updateStrongReference();
    }

    private void updateStrongReference() {
        this.mStrongReference = this.mIsWeak ? null : this.mValue.get();
    }
}
