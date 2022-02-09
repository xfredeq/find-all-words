package gui.helpers;

import java.util.*;

public class ComparePoints {

    public static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e1.getValue().compareTo(e2.getValue());
                    if (res != 0) return -res;
                    return 1;
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}


