package cn.openwatch.internal.communication.os.google.china;

import java.util.ArrayList;
import java.util.Iterator;

import cn.openwatch.internal.google.china.android.gms.common.data.Freezable;

public final class FreezableUtils {

    private FreezableUtils() {
    }

    public static <T, E extends Freezable<T>> ArrayList<T> freeze(ArrayList<E> list) {
        ArrayList<T> localArrayList = new ArrayList<T>(list.size());
        int i = 0;
        int j = list.size();
        while (i < j) {
            localArrayList.add(list.get(i).freeze());
            i++;
        }
        return localArrayList;
    }

    public static <T, E extends Freezable<T>> ArrayList<T> freeze(E[] array) {
        ArrayList<T> localArrayList = new ArrayList<T>(array.length);
        for (int i = 0; i < array.length; i++) {
            localArrayList.add(array[i].freeze());
        }
        return localArrayList;
    }

    public static <T, E extends Freezable<T>> ArrayList<T> freezeIterable(Iterable<E> iterable) {
        ArrayList<T> localArrayList = new ArrayList<T>();
        Iterator<E> localIterator = iterable.iterator();
        while (localIterator.hasNext()) {
            Freezable<T> localFreezable = localIterator.next();
            localArrayList.add(localFreezable.freeze());
        }
        return localArrayList;
    }
}
