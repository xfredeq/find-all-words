package put.poznan.tools;

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

    public static void main(String[] args) {
        Map<String,Integer> map = new TreeMap<>();
        map.put("A", 3);
        map.put("B", 2);
        map.put("C", 1);
        map.put("seba", 1);
        map.put("szymon", 2);
        map.put("mati", -90);

        System.out.println(map);
        // prints "{A=3, B=2, C=1}"
        System.out.println(entriesSortedByValues(map));
        // prints "[C=1, B=2, A=3]"
    }
}


