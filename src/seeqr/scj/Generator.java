package seeqr.scj;

import java.util.*;

/**
 * Relation generator
 * Created by yluo on 12/20/13.
 */
public class Generator {
    /**
     * Generate a random relation with everything random
     * @param relationSize
     * @param setMaxSize the maximum cardinality one set is allowed to have
     * @param setMaxRange the range the set value is allowed (exclusive)
     * @return
     */
    public static ArrayList<Tuple> generateRandomRelation(int relationSize, int setMaxSize, int setMaxRange){
        ArrayList<Tuple> result = new ArrayList<Tuple>(relationSize);
        Random rng = new Random(); // Ideally just create one instance globally
        Tuple tuple;
        int setSize;
        for (int i = 0; i < relationSize; i++) {
            setSize = rng.nextInt(setMaxSize)+1;
            tuple = new Tuple(i,setSize);
            tuple.setValues = generateIntArray(rng,setSize,setMaxRange);
            result.add(tuple);
        }
        //shuffle the list
        Collections.shuffle(result,rng);
        return result;
    }

    /**
     * return an array with arraySize, pick elements in arrayRange
     * arrayRange should >= arraySize
     * the returned array is sorted
     * @param arraySize
     * @param arrayRange
     * @return
     */
    protected static int[] generateIntArray(Random rng, int arraySize, int arrayRange) {

        // Note: use TreeSet to maintain sorting order
        TreeSet<Integer> generated = new TreeSet<Integer>();
        while (generated.size() < arraySize) {
            Integer next = rng.nextInt(arrayRange);
            // As we're adding to a set, this will automatically do a containment check
            generated.add(next);
        }
        int[] result = new int[arraySize];
        int i = 0;
        for(Integer gen:generated) {
            result[i] = gen;
            i++;
        }
        return result;
    }

    public static void main(String[] args) {
        /*
        int[] result = generateIntArray(10,15);
        for(int re:result) {
            System.out.print(re);
            System.out.print(" ");
        }

        ArrayList<Tuple> result = generateRandomRelation(10,10,100);
        //System.out.print(result.size());
        for(Tuple re:result) {
            System.out.print(re.toString());
        }
        */
    }

}
