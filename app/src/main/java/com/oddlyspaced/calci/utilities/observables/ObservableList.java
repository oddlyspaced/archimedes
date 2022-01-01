package com.sparkappdesign.archimedes.utilities.observables;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/* loaded from: classes.dex */
public final class ObservableList<E> extends MutableObservable<ImmutableList<E>> implements List<E> {
    public ObservableList() {
        this.mValue = new ImmutableList();
    }

    public ObservableList(List<E> initialValue) {
        this.mValue = new ImmutableList(initialValue);
    }

    public void setValue(Collection<? extends E> newValue) {
        setValue((ObservableList<E>) new ImmutableList(newValue));
    }

    private ImmutableList<E> setValueWithoutCopy(List<E> newValue) {
        ImmutableList<E> newValueImmutable = new ImmutableList<>(newValue, false);
        setValue((ObservableList<E>) newValueImmutable);
        return newValueImmutable;
    }

    private boolean setValueWithoutCopyWithChangeCheck(List<E> newValue) {
        return !((List) this.mValue).equals(setValueWithoutCopy(newValue));
    }

    private ArrayList<E> getMutableCopy() {
        return new ArrayList<>((Collection) this.mValue);
    }

    @Override // java.util.List, java.util.Collection
    public boolean add(E element) {
        add(size(), element);
        return true;
    }

    @Override // java.util.List
    public void add(int index, E element) {
        ArrayList<E> newValue = getMutableCopy();
        newValue.add(index, element);
        setValueWithoutCopy(newValue);
    }

    @Override // java.util.List, java.util.Collection
    public boolean addAll(Collection<? extends E> collection) {
        return addAll(size(), collection);
    }

    @Override // java.util.List
    public boolean addAll(int index, Collection<? extends E> collection) {
        ArrayList<E> newValue = getMutableCopy();
        boolean changed = newValue.addAll(index, collection);
        if (changed) {
            return setValueWithoutCopyWithChangeCheck(newValue);
        }
        return changed;
    }

    @Override // java.util.List, java.util.Collection
    public void clear() {
        setValueWithoutCopy(new ArrayList());
    }

    @Override // java.util.List
    public E remove(int index) {
        ArrayList<E> newValue = getMutableCopy();
        E removedElement = newValue.remove(index);
        setValueWithoutCopy(newValue);
        return removedElement;
    }

    @Override // java.util.List, java.util.Collection
    public boolean remove(Object object) {
        ArrayList<E> newValue = getMutableCopy();
        boolean changed = newValue.remove(object);
        if (changed) {
            return setValueWithoutCopyWithChangeCheck(newValue);
        }
        return changed;
    }

    @Override // java.util.List, java.util.Collection
    public boolean removeAll(Collection<?> collection) {
        ArrayList<E> newValue = getMutableCopy();
        boolean changed = newValue.removeAll(collection);
        if (changed) {
            return setValueWithoutCopyWithChangeCheck(newValue);
        }
        return changed;
    }

    @Override // java.util.List, java.util.Collection
    public boolean retainAll(Collection<?> collection) {
        ArrayList<E> newValue = getMutableCopy();
        boolean changed = newValue.retainAll(collection);
        if (changed) {
            return setValueWithoutCopyWithChangeCheck(newValue);
        }
        return changed;
    }

    @Override // java.util.List
    public E set(int index, E element) {
        ArrayList<E> newValue = getMutableCopy();
        E replacedElement = newValue.set(index, element);
        setValueWithoutCopy(newValue);
        return replacedElement;
    }

    @Override // java.util.List, java.util.Collection, java.lang.Iterable
    public Iterator<E> iterator() {
        return listIterator();
    }

    @Override // java.util.List
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override // java.util.List
    public ListIterator<E> listIterator(int index) {
        return new ObservableArrayView(this).listIterator(index);
    }

    @Override // java.util.List
    public List<E> subList(int fromIndex, int toIndex) {
        return new ObservableArrayView(fromIndex, toIndex);
    }

    @Override // java.util.List, java.util.Collection
    public boolean contains(Object object) {
        return ((ImmutableList) this.mValue).contains(object);
    }

    @Override // java.util.List, java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        return ((ImmutableList) this.mValue).containsAll(collection);
    }

    @Override // java.util.List
    public E get(int index) {
        return (E) ((ImmutableList) this.mValue).get(index);
    }

    @Override // java.util.List
    public int indexOf(Object object) {
        return ((ImmutableList) this.mValue).indexOf(object);
    }

    @Override // java.util.List, java.util.Collection
    public boolean isEmpty() {
        return ((ImmutableList) this.mValue).isEmpty();
    }

    @Override // java.util.List
    public int lastIndexOf(Object object) {
        return ((ImmutableList) this.mValue).lastIndexOf(object);
    }

    @Override // java.util.List, java.util.Collection
    public int size() {
        return ((ImmutableList) this.mValue).size();
    }

    @Override // java.util.List, java.util.Collection
    public Object[] toArray() {
        return ((ImmutableList) this.mValue).toArray();
    }

    @Override // java.util.List, java.util.Collection
    public <E> E[] toArray(E[] array) {
        return (E[]) ((ImmutableList) this.mValue).toArray(array);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ObservableArrayView extends AbstractList<E> {
        private int mFromIndex;
        private int mToIndex;

        public ObservableArrayView(ObservableList observableList) {
            this(0, observableList.size());
        }

        public ObservableArrayView(int fromIndex, int toIndex) {
            if (fromIndex < 0 || toIndex > ObservableList.this.size() || fromIndex > toIndex) {
                throw new IndexOutOfBoundsException("Illegal endpoint index value.");
            }
            this.mFromIndex = fromIndex;
            this.mToIndex = toIndex;
        }

        @Override // java.util.AbstractList, java.util.List
        public E get(int index) {
            return (E) ObservableList.this.get(this.mFromIndex + index);
        }

        @Override // java.util.AbstractCollection, java.util.List, java.util.Collection
        public int size() {
            return this.mToIndex - this.mFromIndex;
        }

        @Override // java.util.AbstractList, java.util.List
        public E set(int index, E element) {
            return (E) ObservableList.this.set(this.mFromIndex + index, element);
        }

        @Override // java.util.AbstractList, java.util.List
        public void add(int index, E element) {
            this.mToIndex++;
            ObservableList.this.add(this.mFromIndex + index, element);
        }

        @Override // java.util.AbstractList, java.util.List
        public E remove(int index) {
            this.mToIndex--;
            return (E) ObservableList.this.remove(this.mFromIndex + index);
        }
    }
}
