package com.oddlyspaced.calci.utilities.observables;

import com.oddlyspaced.calci.utilities.IterableUtil;
import com.oddlyspaced.calci.utilities.events.Observer;
import com.oddlyspaced.calci.utilities.events.ObserverType;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class ListObserver<T> implements ObserverType {
    private ObserverType mObserver;

    public abstract void handleAdd(T t, int i);

    public abstract void handleRemove(T t, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public final void notifyOfDifferences(List<T> oldList, List<T> newList) {
        List<Integer> removalIndexes;
        List<Integer> insertionIndexes;
        if (oldList == null) {
            oldList = new ArrayList<>();
        }
        if (newList == null) {
            newList = new ArrayList<>();
        }
        if (oldList.size() < newList.size()) {
            removalIndexes = new ArrayList<>();
            insertionIndexes = indexesOfElementsInListMissingFromList(newList, oldList, removalIndexes);
        } else if (oldList.size() > newList.size()) {
            insertionIndexes = new ArrayList<>();
            removalIndexes = indexesOfElementsInListMissingFromList(oldList, newList, insertionIndexes);
        } else {
            removalIndexes = new ArrayList<>();
            insertionIndexes = new ArrayList<>();
            for (int i = 0; i < oldList.size(); i++) {
                if (!oldList.get(i).equals(newList.get(i))) {
                    removalIndexes.add(Integer.valueOf(i));
                    insertionIndexes.add(Integer.valueOf(i));
                }
            }
        }
        for (Integer num : IterableUtil.reverse(removalIndexes)) {
            int index = num.intValue();
            handleRemove(oldList.get(index), index);
        }
        for (Integer num2 : insertionIndexes) {
            int index2 = num2.intValue();
            handleAdd(newList.get(index2), index2);
        }
    }

    private final List<Integer> indexesOfElementsInListMissingFromList(List<T> list1, List<T> list2, List<Integer> indexesOfExtraElements) {
        List<Integer> indexes = new ArrayList<>();
        int i2 = 0;
        for (int i1 = 0; i1 < list1.size(); i1++) {
            if (i2 >= list2.size() || !list2.get(i2).equals(list1.get(i1))) {
                indexes.add(Integer.valueOf(i1));
            } else {
                i2++;
            }
        }
        for (int j = i2; j < list2.size(); j++) {
            indexesOfExtraElements.add(Integer.valueOf(j));
        }
        return indexes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final <U extends List<T>> void attachTo(Observable<U> observable) {
        remove();
        this.mObserver = observable.getDidChange().add(new Observer<ObservableChange<U>>() { // from class: com.oddlyspaced.calci.utilities.observables.ListObserver.1
            public void handle(ObservableChange<U> change) {
                ListObserver.this.notifyOfDifferences((List) change.mOldValue, (List) change.mNewValue);
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
