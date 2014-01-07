package seeqr.scj;

import java.util.ArrayList;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import java.util.HashMap;
import java.util.List;

import com.google.common.primitives.Ints;
import com.googlecode.javaewah.IntIterator;
import com.googlecode.javaewah.IntIteratorOverIteratingRLW;

/**
 * Created by yluo on 12/19/13.
 */
public class SimpleJoinAlgorithms {
    /**
     * The most naive nested loop join
     * @param R
     * @param S
     */
    public void NLNormalJoin(ArrayList<SimpleTuple> R, ArrayList<SimpleTuple> S) {
        int count = 0;
        for(SimpleTuple r:R) {
            for(SimpleTuple s : S) {
                if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                    //output;
                    count ++;
                }
            }
        }
        System.out.println("NL normal will return "+Integer.toString(count)+" results");
    }

    /**
     * nested loop join with signatures
     * @param R
     * @param S
     */
    public void NLSignatureJoin(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
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
            for(SigSimpleTuple s:S) {
                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature))>=0) {
                    if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                        //output;
                        count ++;
                    }
                }
            }
        }
        //System.out.println("NL signature will return "+Integer.toString(count)+" results");
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
        //System.out.println("NL Split signature will return "+Integer.toString(count)+" results");
    }


    /**
     * nested loop join with BitSet signature
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
        System.out.println("bitset signature will return "+Integer.toString(count)+" results");

    }

    /**
     * SHJ, but this is not the really SHJ, simplified with only a shorter signature.
     * @param R
     * @param S
     * @param sig_len
     */
    public void SHJ(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
        //create signatures
        //initially not too big, but big enough
        HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(S.size()/2);

        //put all tuples in S to hashmap
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
            int key = BitOperations.FIRSTBITS & (s.signature[0]);

            List<SigSimpleTuple> l = hashMap.get(key);
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                hashMap.put(key,l);
            }else {
                l.add(s);
            }
        }

        //System.out.print(hashMap.size());

        int count = 0;
        //match with tuples in R
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            //System.out.print(r.signature[0]);
            //s.signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask = r.signature[0] & BitOperations.FIRSTBITS;

            //System.out.print(Integer.toBinaryString(mask));
            //System.out.print(" ");

            int key = 1;

            if((mask & 1) == 1) {
                List<SigSimpleTuple> l = hashMap.get(key);
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
                List<SigSimpleTuple> l = hashMap.get(key);
                //System.out.print(Integer.toBinaryString(key>>>20));System.out.print(" ");
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

        System.out.println("SHJ will return "+Integer.toString(count)+" results");
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
            //System.out.print(r.signature[0]);
            //s.signature[0] & FIRSTBITS is the mask
            //enumerate all subsignatures
            int mask = (BitOperations.FIRSTBITS & r.signature[0])>>>20;

            //System.out.print(Integer.toBinaryString(mask));System.out.print(",");
           // mask = mask>>>20;
            //System.out.print(r.signature[0]);
            //System.out.print("mask:"+Integer.toBinaryString(mask)+" ");

            //System.out.print((r.signature[0] & BitOperations.FIRSTBITS)>>>20);
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
