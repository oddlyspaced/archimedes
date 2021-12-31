package com.sparkappdesign.archimedes.utilities;
/* loaded from: classes.dex */
public class Timer extends java.util.Timer {
    private boolean mIsCancelled;

    public boolean isCancelled() {
        return this.mIsCancelled;
    }

    @Override // java.util.Timer
    public void cancel() {
        super.cancel();
        this.mIsCancelled = true;
    }
}
