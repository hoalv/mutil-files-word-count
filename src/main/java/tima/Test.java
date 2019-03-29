package tima;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Test {

    public static final int SET_SIZE_REQUIRED = 200000;
    public static final int NUMBER_RANGE = 1000000;

    public static void main(String[] args) {
        Random random = new Random();

        Set set1 = new HashSet<Integer>(SET_SIZE_REQUIRED);
        Set set2 = new HashSet<Integer>(SET_SIZE_REQUIRED);

        while(set1.size()< SET_SIZE_REQUIRED) {
            while (set1.add(random.nextInt(NUMBER_RANGE)) != true);
        }

        while(set2.size()< SET_SIZE_REQUIRED) {
            while (set2.add(random.nextInt(NUMBER_RANGE)) != true);
        }
//        assert set1.size() == SET_SIZE_REQUIRED;
//        System.out.println(set1);

        Set<Integer> intersection = Sets.intersection(set1, set2);
        Set<Integer> union = Sets.union(set1, set2);
        System.out.printf("Intersection of two Sets in Java is %s %n", intersection.toString());
//        System.out.printf("Union of two Sets in Java is %s %n", union.toString());
    }
}


