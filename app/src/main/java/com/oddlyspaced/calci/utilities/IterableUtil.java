package com.oddlyspaced.calci.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/* loaded from: classes.dex */
public final class IterableUtil {
    private IterableUtil() {
    }

    public static <T> Iterable<T> reverse(final List<T> list) {
        return new Iterable<T>() { // from class: com.oddlyspaced.calci.utilities.IterableUtil.1
            @Override // java.lang.Iterable
            public Iterator<T> iterator() {
                final ListIterator listIterator = list.listIterator(list.size());
                return new Iterator<T>() { // from class: com.oddlyspaced.calci.utilities.IterableUtil.1.1
                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return listIterator.hasPrevious();
                    }

                    /* JADX WARN: Type inference failed for: r0v1, types: [T, java.lang.Object] */
                    @Override // java.util.Iterator
                    public T next() {
                        return listIterator.previous();
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        listIterator.remove();
                    }
                };
            }
        };
    }
}
