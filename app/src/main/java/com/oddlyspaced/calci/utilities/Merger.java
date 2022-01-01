package com.sparkappdesign.archimedes.utilities;

import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public interface Merger<T> {
    T merge(T t, T t2, AtomicBoolean atomicBoolean, AtomicBoolean atomicBoolean2);
}
