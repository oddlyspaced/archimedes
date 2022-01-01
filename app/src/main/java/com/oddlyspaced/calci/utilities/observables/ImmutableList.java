package com.sparkappdesign.archimedes.utilities.observables;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/* loaded from: classes.dex */
public final class ImmutableList<E> extends AbstractList<E> implements Serializable {
    private List<E> mElements;

    public ImmutableList() {
        this.mElements = new ArrayList();
    }

    public ImmutableList(Collection<? extends E> collection) {
        this.mElements = new ArrayList(collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImmutableList(List<E> elements, boolean copy) {
        this.mElements = copy ? new ArrayList<>(elements) : elements;
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        return this.mElements.get(index);
    }

    @Override // java.util.AbstractCollection, java.util.List, java.util.Collection
    public int size() {
        return this.mElements.size();
    }
}
