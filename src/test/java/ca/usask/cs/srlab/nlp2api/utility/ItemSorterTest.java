package ca.usask.cs.srlab.nlp2api.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class ItemSorterTest {

    HashMap<String, Double> realValueMap;
    HashMap<String, Integer> numberMap;
    HashMap<Integer, Double> keyScoreMap;


    @BeforeEach
    public void setup() {
        realValueMap = new HashMap<>();
        realValueMap.put("A", .5);
        realValueMap.put("C", .02);
        realValueMap.put("B", 1.0);

        numberMap = new HashMap<>();
        numberMap.put("A", 9);
        numberMap.put("C", 2);
        numberMap.put("B", 10);

        keyScoreMap = new HashMap<>();
        keyScoreMap.put(1, 5.0);
        keyScoreMap.put(2, .36);
        keyScoreMap.put(3, 7.0);
    }

    @Test
    public void testSortHashMap() {
        System.out.println(ItemSorter.sortHashMapDouble(realValueMap));
    }

    @Test
    public void testSortHashMapInt() {
        System.out.println(ItemSorter.sortHashMapInt(numberMap));
    }

    @Test
    public void testSortIntDoubleMap() {
        System.out.println(ItemSorter.sortHashMapIntDouble(keyScoreMap));
    }
}
