package seeqr.scj;

import java.math.BigInteger;
import java.util.*;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.googlecode.javaewah.EWAHCompressedBitmap;

import com.googlecode.javaewah.IntIterator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import sun.java2d.pipe.SpanShapeRenderer;

/**
 * Created by yluo on 12/19/13.
 */
public class SimpleJoinAlgorithms {
    /**
     * The most naive nested loop join
     * @param R
     * @param S
     */
    public static void NLNormalJoin(ArrayList<SimpleTuple> R, ArrayList<SimpleTuple> S) {
        int count = 0;


        for(SimpleTuple r:R) {
            for(SimpleTuple s : S) {
                if(r.tupleID == 341129 && s.tupleID == 771902) {
                    System.out.println(r);
                    System.out.println(s);
                }
                if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                    //output;
                    count ++;
                }
            }
        }
        System.err.println("NL normal will return "+Integer.toString(count)+" results");
    }

    /**
     * nested loop join with signatures
     * @param R
     * @param S
     */
    public static void NLSignatureJoin(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
        //create signatures
        //long used = 0;
        //long start = System.nanoTime();

        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
        }

        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
        }
        //System.out.println((System.nanoTime() - start)/1000000);
        //DescriptiveStatistics stats = new DescriptiveStatistics();

        //compare

        long count = 0;
        //long temp = 0;
        for(SigSimpleTuple r:R) {

            //int result = 0;

            for(SigSimpleTuple s:S) {
                //
                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature))>=0) {
                    //temp++;
                    if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                        //output;
                        count ++;
                        //result ++;
                    }
                    //used += System.nanoTime() - start;
                }

            }

            //stats.addValue(result);
        }
        System.err.println("NL signature will return "+Long.toString(count)+" results");
        //System.out.println(count/((long)R.size()*(long)R.size()+0.0));//result percentage
        //System.out.println(temp/((long)R.size()*(long)R.size()+0.0));//P_hit
        //System.out.println(stats.getPercentile(50) + "," + stats.getMean() + "," + stats.getMax());
        //System.out.println("sig compare take "+MakeTests.sig_compare/1000000+"ms");
        //System.out.println("set compare take "+MakeTests.set_compare/1000000+"ms");

    }

    public void NLSignatureJoinMeasure(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
        //create signatures
        //long used = 0;
        long start = System.nanoTime();

        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
        }

        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
        }
        System.out.println("create signatures take "+(System.nanoTime() - start)/1000000+" ms");
        //DescriptiveStatistics stats = new DescriptiveStatistics();

        //compare

        long count = 0;
        long temp = 0;
        MakeTests.reset_counters();

        for(SigSimpleTuple r:R) {

            //int result = 0;

            for(SigSimpleTuple s:S) {
                //
                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain_measure(r.signature, s.signature))>=0) {
                    temp++;
                    if(Utils.compare_set_measure(r.setValues, s.setValues) >= 0) {
                        //output;
                        count ++;
                        //result ++;
                    }
                    //used += System.nanoTime() - start;
                }

            }

            //stats.addValue(result);
        }
        System.out.println("NL signature will return "+Long.toString(count)+" results");
        //System.out.println(count/((long)R.size()*(long)R.size()+0.0));//result percentage
        //System.out.println(temp/((long)R.size()*(long)R.size()+0.0));//P_hit
        //System.out.println(stats.getPercentile(50) + "," + stats.getMean() + "," + stats.getMax());
        MakeTests.print_counters();
    }


    /**
     * nested loop join with bitmap as the set presentation
     * @param R
     * @param S
     */
    public void NLBitmapJoin(ArrayList<BitmapSimpleTuple> R, ArrayList<BitmapSimpleTuple> S, int sig_len) {
        //create signatures
        //long used = 0;
        long start = System.nanoTime();

        //long temp = 0;
        for(BitmapSimpleTuple r:R) {
            //System.out.println(BitOperations.toStringBitStream(r.setValues));
            r.signature = Utils.create_sig_normal(r.setValues,sig_len);
            r.long_signature = Utils.create_sig_bitmap(r.setValues);
            //r.long_signature = Utils.create_sig_bitset(r.setValues);
            //temp += r.long_signature.sizeInBytes();
            //System.out.println(r);
        }
        //System.out.print(temp/1000);

        for(BitmapSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues,sig_len);
            s.long_signature = Utils.create_sig_bitmap(s.setValues);
            //s.long_signature = Utils.create_sig_bitset(s.setValues);
        }

        System.out.print((System.nanoTime() - start)/1000000+"ms\n");
        //System.out.print("hello");

        //DescriptiveStatistics stats = new DescriptiveStatistics();

        //compare

        int count = 0;
        for(BitmapSimpleTuple r:R) {
            for(BitmapSimpleTuple s:S) {
                if((r.setSize >= s.setSize)
                        //&&(Utils.compare_sig_contain(r.signature,s.signature) >=0)
                        //&&(!s.long_signature.andNot(r.long_signature).intIterator().hasNext())) {
                        &&(s.long_signature.andNotCardinality(r.long_signature) == 0)){
                       //&&(Utils.andNotTrue(s.long_signature,r.long_signature))){
                       //&&(Utils.compare_sig_contain(r.long_signature,s.long_signature))){
                    //System.out.print(r.long_signature);
                    //System.out.print(s.long_signature);
                   // System.out.print(s.long_signature.andNotCardinality(r.long_signature));
                    //output;
                    count ++;
                    //result ++;
                }
                //used += System.nanoTime() - start;
            }

        }
            //stats.addValue(result);
        //}
        System.out.println("NL bitmap join will return "+Integer.toString(count)+" results");
        //System.out.println(used/1000000);
        //System.out.println(stats.getPercentile(50) + "," + stats.getMean() + "," + stats.getMax());
    }

    /**
     * nested loop join with signatures, do set compare before split point,
     * doesn't perform better than the original
     *
     * @param R
     * @param S
     */
    public void NLSignatureJoinSplit(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len, int split) {
        //create signatures
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
        }

        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
        }
        //compare
        int count = 0;
        for(SigSimpleTuple r:R) {
            if(r.setSize <= split) {
                for(SigSimpleTuple s:S) {
                    if((Utils.compare_set(r.setValues, s.setValues) >= 0)) {
                            //output;
                            count ++;
                        }
                    }
            } else {
                for(SigSimpleTuple s:S) {
                    if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature))>=0) {
                        if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                            //output;
                            count ++;
                        }
                    }
                }
            }
        }
        //System.out.println("NL Split long_signature will return "+Integer.toString(count)+" results");
    }


    /**
     * nested loop join with BitSet long_signature
     * @param R
     * @param S
     * @param sig_len
     */
    public void NLBigintSignatureJoin(ArrayList<BitsetSimpleTuple> R, ArrayList<BitsetSimpleTuple> S, int sig_len) {
        //create signatures
        for(BitsetSimpleTuple r:R) {
            r.signature = Utils.create_sig_bitset(r.setValues, sig_len);
        }

        for(BitsetSimpleTuple s:S) {
            s.signature = Utils.create_sig_bitset(s.setValues, sig_len);
        }
        //compare
        int count = 0;
        for(BitsetSimpleTuple r:R) {
            for(BitsetSimpleTuple s:S) {
                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature))) {
                    if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                        //output;
                        count ++;
                    }
                }
            }
        }
        System.out.println("bitset long_signature will return "+Integer.toString(count)+" results");

    }




    public static void SHJDB(Set<SigSimpleTuple> R, Set<SigSimpleTuple> S, int sig_len, int useBitsInMap, int partitionSize) {
        HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(partitionSize/2);
        final int bitmask = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1

        long count = 0;
        int i = 0;
        int sSize = S.size();
        for(SigSimpleTuple s:S) {
            int keys = bitmask & (s.signature[0]);
            List<SigSimpleTuple> ls = hashMap.get(keys);
            if(ls == null) {
                ls = new ArrayList<SigSimpleTuple>();
                ls.add(s);
                hashMap.put(keys,ls);
            }else {
                ls.add(s);
            }
            i++;


            //time for the join to happen
            if((i % partitionSize == 0) || (i == sSize)) {

                for(SigSimpleTuple r:R) {
                    int mask = bitmask & r.signature[0];
                    int key = 1;



                    if((mask & 1) == 1) {
                        //hash_probe ++;
                        List<SigSimpleTuple> l = hashMap.get(key);
                        if(l != null) {
                            //hash_probe_success ++;

                            for(SigSimpleTuple lr:l) {
                                //temp++;
                                if((r.setSize >= lr.setSize) && (Utils.compare_sig_contain(r.signature, lr.signature)>=0)) {
                                    //phit++;
                                    if(Utils.compare_set(r.setValues, lr.setValues) >= 0) {
                                        count ++;
                                    }
                                }
                            }
                        }
                    }

                    while(key != 0) {
                        key = mask & (key - mask);
                        //hash_probe ++;
                        List<SigSimpleTuple> l = hashMap.get(key);
                        //System.out.print(Integer.toBinaryString(key>>>20));System.out.print(" ");
                        if(l != null) {
                            //hash_probe_success ++;
                            for(SigSimpleTuple lr:l) {
                                //temp++;
                                if((r.setSize >= lr.setSize) && (Utils.compare_sig_contain(r.signature, lr.signature)>=0)) {
                                    //phit++;
                                    if(Utils.compare_set(r.setValues, lr.setValues) >= 0) {
                                        count ++;
                                    }
                                }
                            }
                        }
                    }

                    //System.out.print("\n");
                }



            }
        }

        System.err.println("SHJ DB will return "+Long.toString(count)+" results");

    }


    /**
     * SHJ, but this is not the really SHJ, simplified with only a shorter long_signature.
     * @param R
     * @param S
     * @param sig_len
     */
    public static void SHJ(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len, int useBitsInMap) {
        //create signatures
        //initially not too big, but big enough
        HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(S.size()/2);
        final int bitmask = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1


        //HashSet<BitSet> set = new HashSet<BitSet>();




        //long startTime = System.nanoTime();
        //int[] newsig = new int[4];

        //put all tuples in S to hashmap
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);

            //Utils.signatureOR(newsig,s.signature);

            //set.add(Utils.create_sig_bitset(s.setValues, sig_len * Integer.SIZE));
            int key = bitmask & (s.signature[0]);
            //int key = Utils.create_hashkey(s.setValues,useBitsInMap);
            List<SigSimpleTuple> l = hashMap.get(key);
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                hashMap.put(key,l);
            }else {
                l.add(s);
            }
        }

//        for(SigSimpleTuple r:R) {
//            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
//        }
        //todo delete
        CommandRun.printMemory();

        //long estimatedTime = System.nanoTime() - startTime;
        //System.out.println(BitOperations.toStringBitStream(newsig));
        //System.out.println("build hash table" + estimatedTime / (1000000.0) + " ms");
        int output = 0;

//        for(BitSet i:set) {
//            for(BitSet j:set) {
//                if(Utils.compare_sig_contain(i,j)) {
//                    output++;
//                }
//            }
//        }
//        System.out.print("----"+set.size()+","+output);

        int count = 0;
        //int temp = 0;
        //int phit = 0;
        //int hash_probe = 0;
        //int hash_probe_success = 0;
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            //System.out.print(r.long_signature[0]);
            //s.long_signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask = bitmask & r.signature[0];
            //int mask = Utils.create_hashkey(r.setValues,useBitsInMap);
            //System.out.print(Integer.toBinaryString(mask));
            //System.out.print(" ");

            int key = 1;



            if((mask & 1) == 1) {
                //hash_probe ++;
                List<SigSimpleTuple> l = hashMap.get(key);
                if(l != null) {
                    //hash_probe_success ++;

                    for(SigSimpleTuple s:l) {
                        //temp++;
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                            //phit++;
                            if(Utils.compare_set(r.setValues, s.setValues) >= 0) {

                                count ++;
                            }
                        }
                    }
                }
            }

            while(key != 0) {
                key = mask & (key - mask);
                //hash_probe ++;
                List<SigSimpleTuple> l = hashMap.get(key);
                //System.out.print(Integer.toBinaryString(key>>>20));System.out.print(" ");
                if(l != null) {
                    //hash_probe_success ++;
                    for(SigSimpleTuple s:l) {
                        //temp++;
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                            //phit++;
                            if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }

            //System.out.print("\n");
        }
        System.err.println("SHJ will return "+Integer.toString(count)+" results");
        //System.out.println("SHJ will return "+Integer.toString(count)+" results, "+(temp/((long)R.size()*(long)S.size()+0.0))
        //    +","+(hashMap.keySet().size()+0.0)/(1<<useBitsInMap));
        //System.out.println(phit/((long)R.size()*(long)S.size()+0.0));
        //System.out.println(hash_probe/(long)R.size());// /((*(hashMap.keySet().size()+0.0))
        //System.out.println(hash_probe_success/(long)R.size());
        //System.out.println(hashMap.keySet().size());
        //System.out.println(temp/(long)R.size());
        //System.out.println("sig compare take "+MakeTests.sig_compare/1000000+"ms");
        //System.out.println("set compare take "+MakeTests.set_compare/1000000+"ms");
    }


    public void SHJMeasure(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len, int useBitsInMap) {
        //create signatures
        //initially not too big, but big enough
        HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(S.size()/2);
        final int bitmask = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1


        //HashSet<BitSet> set = new HashSet<BitSet>();




        long startTime = System.nanoTime();
        //int[] newsig = new int[4];

        //put all tuples in S to hashmap
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);

            //Utils.signatureOR(newsig,s.signature);

            //set.add(Utils.create_sig_bitset(s.setValues, sig_len * Integer.SIZE));
            int key = bitmask & (s.signature[0]);
            //int key = Utils.create_hashkey(s.setValues,useBitsInMap);
            List<SigSimpleTuple> l = hashMap.get(key);
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                hashMap.put(key,l);
            }else {
                l.add(s);
            }
        }

        long estimatedTime = System.nanoTime() - startTime;
        //System.out.println(BitOperations.toStringBitStream(newsig));
        System.out.println("build hash table take " + estimatedTime / 1000000 + " ms");
        int output = 0;

//        for(BitSet i:set) {
//            for(BitSet j:set) {
//                if(Utils.compare_sig_contain(i,j)) {
//                    output++;
//                }
//            }
//        }
//        System.out.print("----"+set.size()+","+output);

        int count = 0;
        int temp = 0;
        int phit = 0;
        int hash_probe = 0;
        int hash_probe_success = 0;
        MakeTests.reset_counters();
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            //System.out.print(r.long_signature[0]);
            //s.long_signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask = bitmask & r.signature[0];
            //int mask = Utils.create_hashkey(r.setValues,useBitsInMap);
            //System.out.print(Integer.toBinaryString(mask));
            //System.out.print(" ");

            int key = 1;



            if((mask & 1) == 1) {
                hash_probe ++;
                List<SigSimpleTuple> l = hashMap.get(key);
                if(l != null) {
                    hash_probe_success ++;
                    for(SigSimpleTuple s:l) {
                        temp++;
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain_measure(r.signature, s.signature)>=0)) {
                            phit++;
                            if(Utils.compare_set_measure(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }

            while(key != 0) {
                key = mask & (key - mask);
                hash_probe ++;
                List<SigSimpleTuple> l = hashMap.get(key);
                //System.out.print(Integer.toBinaryString(key>>>20));System.out.print(" ");
                if(l != null) {
                    hash_probe_success ++;
                    for(SigSimpleTuple s:l) {
                        temp++;
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain_measure(r.signature, s.signature)>=0)) {
                            phit++;
                            if(Utils.compare_set_measure(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }

            //System.out.print("\n");
        }

        System.out.println("SHJ will return "+Integer.toString(count)+" results, "+(temp/((long)R.size()*(long)S.size()+0.0))
                +","+(hashMap.keySet().size()+0.0)/(1<<useBitsInMap));
        System.out.println(phit/((long)R.size()*(long)S.size()+0.0));
        System.out.println(hash_probe/(long)R.size());// /((*(hashMap.keySet().size()+0.0))
        System.out.println(hash_probe_success/(long)R.size());
        System.out.println(hashMap.keySet().size());
        System.out.println(temp/(long)R.size());
        MakeTests.print_counters();

        long sum = 0;
        for(int i = 0; i < 4; i++) {
            sum += MakeTests.sig_compare_call[i];
        }

        System.out.println("calls to sigcompare " + sum);

        DescriptiveStatistics stats = new DescriptiveStatistics();
        //ones in hash map key
        for(int key:hashMap.keySet()) {
                stats.addValue(BitOperations.number_of_ones(key));
        }

        System.out.println(stats.getMin()+","+stats.getMax()+","+stats.getMean()+","+stats.getPercentile(50));

        //onese in signature[0]
        stats.clear();
        for(SigSimpleTuple r:R) {
            stats.addValue(BitOperations.number_of_ones(r.signature[0]));
        }

        System.out.println(stats.getMin()+","+stats.getMax()+","+stats.getMean()+","+stats.getPercentile(50));

        //list length in hashmap
        stats.clear();

        sum = 0;
        for(int key:hashMap.keySet()) {

            if(hashMap.get(key).size() > 500) {
                //System.out.println(Integer.toBinaryString(key));

                for(SigSimpleTuple i:hashMap.get(key)) {
                    stats.addValue(i.setSize);
                }

                sum += hashMap.get(key).size();
            }else {
                //stats.addValue(hashMap.get(key).size());
            }
        }

        System.out.println(stats.getMin()+","+stats.getMax()+","+stats.getMean()+","+stats.getPercentile(50));
        System.out.print(sum);



    }


    /**
     * SHHJ, not only use one mask as the key, but two
     * @param R
     * @param S
     * @param sig_len
     */
    public void SHHJ(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len, int useBitsInMap) {
        //create signatures
        //initially not too big, but big enough
        Table<Integer, Integer, ArrayList<SigSimpleTuple>> table = HashBasedTable.create();

        //HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(S.size()/2);
        final int bitmask1 = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1
        final int bitmask2 = (1<<31)>>4;
        //put all tuples in S to hashmap
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
            int key1 = bitmask1 & (s.signature[0]);
            int key2 = bitmask2 & (s.signature[sig_len-1]);
            //int key = Utils.create_hashkey(s.setValues,useBitsInMap);
            ArrayList<SigSimpleTuple> l = table.get(key1,key2);
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                table.put(key1, key2, l);
            }else {
                l.add(s);
            }
        }

        System.out.print("table done");

        int count = 0;
        int temp = 0;
        int phit = 0;
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            //System.out.print(r.long_signature[0]);
            //s.long_signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask1 = bitmask1 & r.signature[0];
            int mask2 = bitmask2 & (r.signature[sig_len-1]);
            //int mask = Utils.create_hashkey(r.setValues,useBitsInMap);
            //System.out.print(Integer.toBinaryString(mask));
            //System.out.print(" ");  
            
            
            int key1 = 1;
            //int key2 = 1;

            if((mask1 & 1) == 1) {

                if(table.containsRow(key1)) {
                    int[] subsets2 = Utils.getSubsets(mask2);
                    for(int key2:subsets2) {
                        ArrayList<SigSimpleTuple> l = table.get(key1,key2);
                        if(l != null) {
                            for(SigSimpleTuple s:l) {
                                temp++;
                                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                                    phit++;
                                    if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                        count ++;
                                    }
                                }
                            }
                        }
                    }
                }
            }


            while(key1 != 0) {
                key1 = mask1 & (key1 - mask1);

                if(table.containsRow(key1)) {
                    int[] subsets2 = Utils.getSubsets(mask2);
                    for(int key2:subsets2) {
                        ArrayList<SigSimpleTuple> l = table.get(key1,key2);
                        if(l != null) {
                            for(SigSimpleTuple s:l) {
                                temp++;
                                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                                    phit++;
                                    if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                        count ++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //System.out.print("\n");
        }

        System.out.println("SHJ will return "+Integer.toString(count)+" results, "+(temp/((long)R.size()*(long)S.size()+0.0))
                );
        System.out.println(phit/((long)R.size()*(long)S.size()+0.0));
    }



    /**
     * ASHJ, A for advanced. Enumerate signature subset more efficiently, we first try to use binary search, then maybe
     * treemap
     * @param R
     * @param S
     * @param sig_len
     */
    public void ASHJ(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len, int useBitsInMap) {
        //create signatures
        //initially not too big, but big enough
        HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(S.size()/2);
        final int bitmask = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1


        HashSet<BitSet> set = new HashSet<BitSet>();

        //put all tuples in S to hashmap
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
            set.add(Utils.create_sig_bitset(s.setValues, sig_len * Integer.SIZE));
            int key = bitmask & (s.signature[0]);
            //int key = Utils.create_hashkey(s.setValues,useBitsInMap);
            List<SigSimpleTuple> l = hashMap.get(key);
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                hashMap.put(key,l);
            }else {
                l.add(s);
            }
        }

        int output = 0;

        for(BitSet i:set) {
            for(BitSet j:set) {
                if(Utils.compare_sig_contain(i,j)) {
                    output++;
                }
            }
        }
        System.out.print("----"+set.size()+","+output);

        int count = 0;
        int temp = 0;
        int phit = 0;
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            //System.out.print(r.long_signature[0]);
            //s.long_signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask = bitmask & r.signature[0];
            //int mask = Utils.create_hashkey(r.setValues,useBitsInMap);
            //System.out.print(Integer.toBinaryString(mask));
            //System.out.print(" ");

            int key = 1;



            if((mask & 1) == 1) {
                List<SigSimpleTuple> l = hashMap.get(key);
                if(l != null) {
                    for(SigSimpleTuple s:l) {
                        temp++;
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                            phit++;
                            if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }

            while(key != 0) {
                key = mask & (key - mask);
                List<SigSimpleTuple> l = hashMap.get(key);
                //System.out.print(Integer.toBinaryString(key>>>20));System.out.print(" ");
                if(l != null) {
                    for(SigSimpleTuple s:l) {
                        temp++;
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                            phit++;
                            if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }

            //System.out.print("\n");
        }

        System.out.println("SHJ will return "+Integer.toString(count)+" results, "+(temp/((long)R.size()*(long)S.size()+0.0))
                +","+(hashMap.keySet().size()+0.0)/(1<<useBitsInMap));
        System.out.println(phit/((long)R.size()*(long)S.size()+0.0));
    }


    /**
     * SHJ, with an array instead of a hash table
     * @param R
     * @param S
     * @param sig_len
     */
    public void SHJArray(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
        //create signatures
        //initially not too big, but big enough
        ArrayList<SigSimpleTuple>[] map;
        map = new ArrayList[(1<<12)];

        //put all tuples in S to hashmap
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
            int key = (BitOperations.FIRSTBITS & (s.signature[0]));
            key = key >>> 20;
            //System.out.print(key);
            //System.out.print(" ");


            ArrayList<SigSimpleTuple> l = map[key];
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                map[key] = l;
            }else {
                l.add(s);
            }
        }

//        int size= 0;
//        for(int i = 0; i < map.length; i++) {
//            if(map[i] != null) {
//                size++;
//            }
//        }
//        System.out.print(size);


        //System.out.println("put S in hashmap done");

        int count = 0;
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            //System.out.print(r.long_signature[0]);
            //s.long_signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask = (BitOperations.FIRSTBITS & r.signature[0])>>>20;

            //System.out.print(Integer.toBinaryString(mask));System.out.print(",");
           // mask = mask>>>20;
            //System.out.print(r.long_signature[0]);
            //System.out.print("mask:"+Integer.toBinaryString(mask)+" ");

            //System.out.print((r.long_signature[0] & BitOperations.FIRSTBITS)>>>20);
            //System.out.print(" ");


            int key = 1;

            if((mask & 1) == 1) {
                List<SigSimpleTuple> l = map[key];
                if(l != null) {
                    for(SigSimpleTuple s:l) {
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                            if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }


            while(key != 0) {
                key = mask & (key - mask);
                List<SigSimpleTuple> l = map[key];
                //System.out.print(Integer.toBinaryString(key));System.out.print(" ");
                if(l != null) {
                    for(SigSimpleTuple s:l) {
                        if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                            if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                                count ++;
                            }
                        }
                    }
                }
            }

            //System.out.print("\n");
        }

        System.out.println("SHJ Array will return "+Integer.toString(count)+" results");
    }

    /**
     * SHJ, with an array instead of a hash table, with bitmap (JavaEWAH ) instead of hashset
     * we can use BitSet in java as well, not much difference, but here at least we save space
     * @param R
     * @param S
     * @param sig_len
     * @param useBitsInMap number of bits to use in map
     */
    public void SHJBitmap(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len, int useBitsInMap) {
        //create signatures
        //initially not too big, but big enough
        int bitmask = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1
        EWAHCompressedBitmap[] map;
        map = new EWAHCompressedBitmap[(1<<useBitsInMap)];
        for(int i = 0; i < map.length; i++) {
            map[i] = new EWAHCompressedBitmap();
        }

        //put all tuples in S to hashmap
        for(int i = 0; i < S.size(); i++) {
            SigSimpleTuple s = S.get(i);
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
            int key = (bitmask & (s.signature[0]));
            key = key >>> (32-useBitsInMap);
            map[key].set(i);
        }

        int count = 0;
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            int mask = (bitmask & r.signature[0])>>>(32-useBitsInMap);

            int key = 1;
            IntIterator iter;
            if((mask & 1) == 1) {
                iter = map[key].intIterator();
                while (iter.hasNext()) {
                    SigSimpleTuple s = S.get(iter.next());
                    if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                        if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                            count ++;
                        }
                    }
                }
            }

            while(key != 0) {
                key = mask & (key - mask);
                //for BitSet: for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
                // operate on index i here
                    //}

                iter = map[key].intIterator();
                while(iter.hasNext()) {
                    SigSimpleTuple s = S.get(iter.next());
                    if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                        if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                            count ++;
                        }
                    }
                }
            }

            //System.out.print("\n");
        }

        int temp = 0;
        for(EWAHCompressedBitmap i:map) {
            temp += i.sizeInBytes();

        }
        System.out.print(temp/1024 + "KB\n");

        System.out.println("SHJ Bitmap will return " + Integer.toString(count) + " results");
    }

}
