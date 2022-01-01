package com.oddlyspaced.calci.utilities;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public final class ListUtil {
    private ListUtil() {
    }

    public static int hashCodeIgnoringOrder(List<?> list) {
        int hashCode = 0;
        Iterator<?> it = list.iterator();
        while (it.hasNext()) {
            hashCode ^= it.next().hashCode();
        }
        return hashCode;
    }

    public static <T> void removeDuplicates(List<T> list) {
        for (T entry : list) {
            for (T other : list) {
                if (list.indexOf(other) > list.indexOf(entry) && other.equals(entry)) {
                    list.remove(other);
                }
            }
        }
    }

    public static <T> void removeAllExcept(List<T> list, T objectToKeep) {
        for (T entry : list) {
            if (entry != objectToKeep) {
                list.remove(entry);
            }
        }
    }

    public static <T> List<T> objectsAfterObject(List<T> list, T object) {
        int index = list.indexOf(object);
        if (index + 1 >= list.size() || index == -1) {
            return null;
        }
        return list.subList(index, list.size());
    }

    public static <T> void mergeObjects(List<T> list, Merger<T> merger, boolean commutative, boolean recheckOnMerge) {
        int startJ;
        int i = 0;
        while (i < list.size()) {
            T obj1 = list.get(i);
            if (commutative) {
                startJ = i + 1;
            } else {
                startJ = 0;
            }
            int j = startJ;
            while (true) {
                if (j >= list.size()) {
                    break;
                }
                if (i != j) {
                    T obj2 = list.get(j);
                    AtomicBoolean merged = new AtomicBoolean(false);
                    AtomicBoolean stop = new AtomicBoolean(false);
                    T result = merger.merge(obj1, obj2, merged, stop);
                    if (!stop.get()) {
                        if (merged.get()) {
                            if (result != null) {
                                list.set(i, result);
                            } else {
                                list.remove(i);
                                if (j > i) {
                                    j--;
                                }
                            }
                            list.remove(j);
                            if (i > j) {
                                i--;
                            }
                            if (recheckOnMerge) {
                                i = -1;
                            } else {
                                i--;
                            }
                        }
                    } else {
                        return;
                    }
                }
                j++;
            }
            i++;
        }
    }
}
