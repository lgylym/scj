package seeqr.scj;

import java.util.*;

/**
 * Relation generator
 * Created by yluo on 12/20/13.
 */
public class Generator {
    /**
     * Generate a random relation with everything random
     * A bit generic programming to handle different tuple classes
     * @param relationSize
     * @param setMaxSize the maximum cardinality one set is allowed to have
     * @param setMaxRange the range the set value is allowed (exclusive)
     * @return
     */

    protected static int tupleCounter = 0;//same tuple id must corresponds to the same set

    public static<T extends SimpleTuple> ArrayList<T> generateRandomRelation(
            int relationSize, int setMaxSize, int setMaxRange, Class<T> cls) throws Exception {
        ArrayList<T> result = new ArrayList<T>(relationSize);
        Random rng = new Random(); // Ideally just create one instance globally
        T tuple;
        int setSize;

        for (int i = tupleCounter; i < relationSize+tupleCounter; i++) {
            setSize = rng.nextInt(setMaxSize)+1;
            tuple = cls.newInstance();
            tuple.tupleID = i;
            tuple.setSize = setSize;
            tuple.setValues = generateIntArray(rng,setSize,setMaxRange);
            result.add(tuple);
        }
        tupleCounter = tupleCounter+relationSize;
        //shuffle the list
        Collections.shuffle(result,rng);
        return result;
    }


    public static ArrayList<SigSimpleTuple> toSigSimpleTuples(ArrayList<SimpleTuple> relation) {
        ArrayList<SigSimpleTuple> result = new ArrayList<SigSimpleTuple>(relation.size());
        for(SimpleTuple r : relation) {
            SigSimpleTuple s = new SigSimpleTuple();
            s.tupleID = r.tupleID;
            s.setSize = r.setSize;
            s.setValues = r.setValues;
            result.add(s);
        }
        return result;
    }

    public static ArrayList<BitmapSimpleTuple> toBitmapSimpleTuples(ArrayList<SimpleTuple> relation) {
        ArrayList<BitmapSimpleTuple> result = new ArrayList<BitmapSimpleTuple>(relation.size());
        for(SimpleTuple r : relation) {
            BitmapSimpleTuple s = new BitmapSimpleTuple();
            s.tupleID = r.tupleID;
            s.setSize = r.setSize;
            s.setValues = r.setValues;
            result.add(s);
        }
        return result;
    }

    public static  ArrayList<DAGSST> toDAGSST(ArrayList<SimpleTuple> relation) {
        ArrayList<DAGSST> result = new ArrayList<DAGSST>(relation.size());
        for(SimpleTuple r: relation) {
            DAGSST d = new DAGSST();
            d.tupleID = r.tupleID;
            d.setSize = r.setSize;
            d.setValues = r.setValues;
            result.add(d);
        }
        return result;
    }

    public static <T extends SimpleTuple> ArrayList<T> transferRelation(ArrayList<SimpleTuple> relation, Class<T> cls) {
        ArrayList<T> result = new ArrayList<T>(relation.size());
        try{
            for(SimpleTuple r: relation) {
                T d = cls.newInstance();
                d.tupleID = r.tupleID;
                d.setSize = r.setSize;
                d.setValues = r.setValues;
                result.add(d);
            }
        }catch(Exception e) {
            System.out.print(e);
        }
        return result;
    }

    public static ArrayList<BitsetSimpleTuple> toBigintSimpleTuples(ArrayList<SimpleTuple> relation) {
        ArrayList<BitsetSimpleTuple> result = new ArrayList<BitsetSimpleTuple>(relation.size());
        for(SimpleTuple r : relation) {
            BitsetSimpleTuple s = new BitsetSimpleTuple();
            s.tupleID = r.tupleID;
            s.setSize = r.setSize;
            s.setValues=  r.setValues;
            result.add(s);
        }
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

        ArrayList<SimpleTuple> result = generateRandomRelation(10,10,100);
        //System.out.print(result.size());
        for(SimpleTuple re:result) {
            System.out.print(re.toString());
        }
        */
    }

}
