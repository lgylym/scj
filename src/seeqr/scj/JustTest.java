package seeqr.scj;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by yluo on 1/2/14.
 */
public class JustTest {
    public static void main(String[] args) {
        ArrayList<Integer> a= new ArrayList<>();
        a.add(1);
        a.add(2);
        a.add(3);
        Collections.shuffle(a);
        System.out.print(a);
//        DescriptiveStatistics de = new DescriptiveStatistics();
//        de.addValue(1);
//        de.addValue(1);
//        //de.addValue(2);
//        System.out.println(de.getMin());
//        //System.out.print(Integer.numberOfLeadingZeros(-1));


        //JustTest jt = new JustTest();
        //jt.testSplit();
        //jt.testHashSet();
        //jt.enumerateDescending(0x5131);
        //int i = (1<<31)>>11;
        //System.out.print(Integer.toBinaryString(i));

        //long temp = (long)50000*(long)50000;
        //System.out.print(temp);
//        HashFunction hf = Hashing.murmur3_32();
//        for(int i = 0; i < 10; i++){
//        System.out.print(hf.hashInt(i).asInt()+" ");}
//
//        for(int i = 0; i < 10; i++){
//            System.out.print(hf.newHasher().putInt(i).hash().asInt()+" ");}



//        int threshold = 20000000;
//        //int j = 0;
//        int[] array = new int[threshold];
//        int[] b = new int[threshold];
//        Random random = new Random();
//        for(int i = 0; i < array.length; i++) {
//            array[i] = random.nextInt();
//        }
//        long startTime;
//        long estimatedTime;


//        for(int j = 0; j < 100; j++){
//        startTime = System.nanoTime();
//        int c;
//        for(int i = 0; i < threshold; i++) {
//            c = array[i];
//            b[i] = c < 0 ? -c : c;
//        }
//        estimatedTime = System.nanoTime() - startTime;
//        System.out.print(estimatedTime/(1000000.0));
//        System.out.print("ms\n");
//        }

//        for(int j = 0; j < 100; j++){
//        startTime = System.nanoTime();
//        for(int i = 0; i < threshold; i++) {
//            b[i] = (array[i] ^ (array[i] >> 31)) - (array[i] >> 31);
//            //b[i] = Math.abs(array[i]);
//        }
//        estimatedTime = System.nanoTime() - startTime;
//        System.out.print(estimatedTime/(1000000.0));
//        System.out.print("ms\n");
//        }


//        for(int j = 0; j < 100; j++){
//        startTime = System.nanoTime();
//        for(int i = 0; i < threshold; i++) {
//            b[i] = array[i] < 0 ? -array[i] : array[i];
//        }
//        estimatedTime = System.nanoTime() - startTime;
//        System.out.print(estimatedTime/(1000000.0));
//        System.out.print("ms\n");
//        }


        //System.out.println(Integer.toString(j));


    }


    public void enumerateDescending(int mask) {

        int[] key={mask};
        while(true) {
            System.out.println((key[0]));
            if(key[0] == 0) {
                break;
            }
            key[0] = (key[0]-1)&mask;
        }



//        int[] key = {1};
//        //int[] t = {0};
//        key[0] = mask & (-mask);
//        System.out.println(BitOperations.toStringBitStream(key));
//        while(key[0] != 0) {
//            //key[0] = mask & (mask - key[0]);
//            //System.out.println(BitOperations.toStringBitStream(t));
//            key[0] = mask & (key[0] - mask);
//            System.out.println(BitOperations.toStringBitStream(key));
//        }
    }

    public void testHashSet() {
        long startTime;

        int relationSize = 10000;
        int maxSetSize = 100;
        int maxSetPool = 1000;
        int sig_len = 128/Integer.SIZE; //better to be a multiplier of Integer.SIZE (32)

        try{
            ArrayList<SimpleTuple> R = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);
            ArrayList<SimpleTuple> S = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);

            //{

            SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
            ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
            ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);

            DescriptiveStatistics stats = new DescriptiveStatistics();

            {//test long_signature op
                for(SigSimpleTuple r:R1) {
                    r.signature = Utils.create_sig_normal(r.setValues, sig_len);
                }

                for(SigSimpleTuple s:S1) {
                    s.signature = Utils.create_sig_normal(s.setValues, sig_len);
                }
                //compare

                startTime = System.nanoTime();
                int count = 0;
                for(SigSimpleTuple r:R1) {
                    for(SigSimpleTuple s:S1) {
                        Utils.compare_set(r.setValues, s.setValues);
                        count++;
                    }
                }
                stats.addValue(System.nanoTime()-startTime);

                System.out.print(stats.getPercentile(50)/count + "," + stats.getMean()/count);
                System.out.print("nano\n");
                stats.clear();
            }


            {//test set compare
                int count = 0;
                for(int i = 0; i < 10; i++){

                startTime = System.nanoTime();
                count = 0;
                for(SigSimpleTuple r:R1) {
                    for(SigSimpleTuple s:S1) {
                        if(r.setSize >= s.setSize){
                            Utils.compare_set(r.setValues, s.setValues);
                            count++;
                        }
                    }
                }
                stats.addValue(System.nanoTime()-startTime);
                }

                System.out.print(stats.getPercentile(50)/count + "," + stats.getMean()/count);
                System.out.print("nano\n");
                stats.clear();

            }

            {//test hashset

                int count = 0;
                for(int i = 0; i < 10; i++){
                    HashSet<Integer> hs = new HashSet<Integer>(R1.size());
                    startTime = System.nanoTime();
                    count = 0;
                    for(SigSimpleTuple r:R1) {
                        for(int value:r.setValues) {
                            hs.add(value);
                            count ++;
                        }
                    }

                    for(SigSimpleTuple r:S1) {
                        for(int value:r.setValues) {
                            hs.contains(value);
                            //count ++;
                        }
                    }
                    stats.addValue(System.nanoTime()-startTime);

                    hs.clear();
                }
                System.out.print(stats.getPercentile(50)/count + "," + stats.getMean()/count);
                System.out.print("nano\n");
                stats.clear();
            }

        }catch (Exception e) {
            System.out.print(e);
        }


    }


    /**
     * test if worth doing SimpleJoinAlgorithms.NLSignatureJoinSplit()
     */
    public void testSplit() {
        long startTime;

        int relationSize = 20000;
        int maxSetSize = 100;
        int maxSetPool = 1000;
        int sig_len = 128; //better to be a multiplier of Integer.SIZE (32)

        //take input, store them in R and S
        try{
            ArrayList<SimpleTuple> R = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);
            ArrayList<SimpleTuple> S = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);

            //{

            SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
            ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
            ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);

            DescriptiveStatistics stats = new DescriptiveStatistics();
            {
                //the original algo
                for(int i = 0; i < 20; i++){
                    startTime = System.nanoTime();
                    sja.NLSignatureJoin(R1,S1,sig_len/Integer.SIZE);
                    stats.addValue(System.nanoTime() - startTime);
                }
                System.out.print(stats.getPercentile(50)/(1000000.0) + "," + stats.getMean()/(1000000.0));
                System.out.print("ms\n");
                stats.clear();
            }

            {
                for(int j = 0; j <= 5; j++) {
                    for(int i = 0; i < 20; i++){
                        startTime = System.nanoTime();
                        //sja.NLSignatureJoin(R1,S1,sig_len/Integer.SIZE);
                        sja.NLSignatureJoinSplit(R1,S1,sig_len/Integer.SIZE,j);
                        stats.addValue(System.nanoTime() - startTime);
                    }
                    System.out.print("**"+stats.getPercentile(50)/(1000000.0) + "," + stats.getMean()/(1000000.0));
                    System.out.print("ms\n");
                }
            }

            {
                //again, does media work?
                for(int i = 0; i < 20; i++){
                    startTime = System.nanoTime();
                    sja.NLSignatureJoin(R1,S1,sig_len/Integer.SIZE);
                    stats.addValue(System.nanoTime() - startTime);
                }
                System.out.print(stats.getPercentile(50)/(1000000.0) + "," + stats.getMean()/(1000000.0));
                System.out.print("ms\n");
                stats.clear();
            }


        }catch (Exception e) {
            System.out.print(e);
        }

    }
}
