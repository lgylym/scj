package seeqr.scj;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.googlecode.javaewah.BitmapStorage;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import com.googlecode.javaewah.EWAHIterator;
import com.googlecode.javaewah.IteratingBufferedRunningLengthWord;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Created by yluo on 12/19/13.
 */
public class Utils {

    static HashFunction hf = Hashing.murmur3_32();

    /**
     * compare two sets, return whether set1 \supset set2
     * if set1 == set2, return 0;
     * if set1 \supset set2, return 1;
     * else return -1;
     *
     * assume that set1 and set2 are sorted ascending
     *        |set1| >= |set2|
     * @param set1
     * @param set2
     */
    public static int compare_set(int[] set1, int[] set2) {
        int i = 0;
        int j = 0;
        int len1 = set1.length;
        int len2 = set2.length;

        if(len1 > len2) {//containment
            while((i < len1)&&(j < len2)) {
                if(set1[i] == set2[j]) {
                    i++;j++;
                }else if(set1[i] > set2[j]) {
                    return -1;
                }else {
                    i++;
                }
            }
            return (j == len2)?1:-1;
        }else if(len1 == len2) {//equality
            while(i < len1) {
                if(set1[i] != set2[i]) {
                    return -1;
                }
                i++;
            }
            return 0;
        }else {
            return -1;
        }
    }

    /**
     * compare two signatures, sig1 and sig2 should have the same size
     * return 1 if sig1 contains sig2
     * return 0 if equal
     * return -1 if not equal
     * @param sig1
     * @param sig2
     * @return
     */
    public static int compare_sig(long[] sig1, long[] sig2) {
        int compare;
        boolean contain = false;
        for(int i = 0; i < sig1.length; i++) {
            if(sig1[i] != sig2[i]) {
                if(((~sig1[i])&(sig2[i]))!=0) {
                    return -1;
                }else {
                    contain = true;
                }
            }
        }
        return contain?1:0;
    }

    /**
     * only care about containment, do not care about equality
     * return 1 if sig1 \supseteq sig2
     * return -1 if not
     * @param sig1
     * @param sig2
     * @return
     */
    public  static int compare_sig_contain(int[] sig1, int[] sig2) {
        for(int i = 0; i < sig1.length; i++) {
            if(((~sig1[i])&(sig2[i]))!=0) {
                return -1;
            }
        }
        return 1;
    }

    /**
     *
     * @param sig1
     * @param sig2
     * @return true if sig1 contains sig2
     */
    public static boolean compare_sig_contain(BitSet sig1, BitSet sig2) {
        BitSet sig3 = (BitSet)sig2.clone();
        sig3.andNot(sig1);
        return sig3.isEmpty();
    }

    /**
     * and not, return true if the result is empty
     * a and not b
     * @param b
     */
    public static boolean andNotTrue(final EWAHCompressedBitmap a, final EWAHCompressedBitmap b
                                  ) {
        //final BitmapStorage container = new EWAHCompressedBitmap();
        final EWAHIterator i = a.getEWAHIterator();
        final EWAHIterator j = b.getEWAHIterator();
        final IteratingBufferedRunningLengthWord rlwi = new IteratingBufferedRunningLengthWord(i);
        final IteratingBufferedRunningLengthWord rlwj = new IteratingBufferedRunningLengthWord(j);
        while ((rlwi.size()>0) && (rlwj.size()>0)) {
            final int nbre_literal = Math.min(rlwi.getNumberOfLiteralWords(),
                    rlwj.getNumberOfLiteralWords());
            if (nbre_literal > 0) {
                for (int k = 0; k < nbre_literal; ++k){
                    if((rlwi.getLiteralWordAt(k) & (~rlwj.getLiteralWordAt(k))) != 0) {
                        return false;
                    }
                }
                rlwi.discardFirstWords(nbre_literal);
                rlwj.discardFirstWords(nbre_literal);
            }
        }
        return true;
    }




        /**
         * the normal implementation, using modulo sig_len
         * one element -> one bit in the long_signature
         * @param set
         * @param sig_len how many integers we use to represent a long_signature
         * @return
         */
        public static int[] create_sig_normal(int[] set, int sig_len) {
            int[] signature = new int[sig_len];
            int remainder = 0;
            int index = 0;
            int bit = 0;
            for(int i = 0; i < set.length; i++) {
                //TODO
                // = hf.hashInt(set[i]).asInt()%(Integer.SIZE*sig_len);
                //remainder = remainder < 0? -remainder : remainder;
                remainder = set[i]%(Integer.SIZE*sig_len);
                index = remainder / Integer.SIZE;
                bit = remainder % Integer.SIZE;
                //System.out.print(index+","+bit);
                signature[index] |= 1 << (bit);
            }
            return signature;
        }

    public static EWAHCompressedBitmap create_sig_bitmap(int[] set) {
        EWAHCompressedBitmap signature = new EWAHCompressedBitmap();
        for(int element:set) {
            signature.set(element);
        }
        return signature;
    }

    public static BitSet create_sig_bitset(int[] set) {
        BitSet signature = new BitSet();
        for(int element:set) {
            signature.set(element);
        }
        return signature;
    }

    /**
     * one element -> one bit in the long_signature
     * sig_len is the number of bits in the long_signature
     * @param set
     * @param sig_len
     * @return
     */
    public static BitSet create_sig_bitset(int[] set, int sig_len) {
        BitSet signature = new BitSet(sig_len);
        for(int i = 0; i < set.length; i++) {
            signature.set(set[i] % sig_len);
        }
        return signature;
    }

    /**
     * return the subsets of a given integer mask
     * @param mask
     * @return
     */
    public static int[] getSubsets(int mask) {
        int[] result = new int[1<<Integer.bitCount(mask)];
        int temp = 1;
        int i = 0;
        if((mask & 1) == 1) {
            result[i] = 1;
            i++;
        }
        while(temp != 0) {
            temp = mask & (temp - mask);
            result[i] = temp;
            i++;
        }
        return result;
    }



    public static void main(String[] args) {
        int mask = 0x3;
        int[] a = getSubsets(mask);
        System.out.print(BitOperations.toStringBitStream(a));
//        int[] set1 = {0,1,2,3,4,5,6,7};
//        int[] set2 = {1,2,3};
//        int[] set3 = {1,2,5,6,7,8};
//        int[] set4 = {64};
//        long[] sig1 = {1,10};
//        long[] sig2 = {1,10};
//        long[] sig3 = {2,10};
//        //System.out.print(compare_set(set1, set2));
//        assert compare_set(set1, set2) == 1;
//        assert compare_set(set2, set1) == -1;
//        assert compare_set(set1, set3) == -1;
//        assert compare_set(set1, set1) == 0;
//        assert compare_sig(sig1, sig2) == 0;
//        assert compare_sig(sig1, sig3) == -1;
//
//        assert create_sig_normal(set4, 1)[0] == 1;
//        assert create_sig_normal(set1, 1)[0] == 0xFF;
//
//        System.out.println(create_sig_bitset(set3,8).toString());
//        System.out.println(compare_sig_contain(create_sig_bitset(set2,8),create_sig_bitset(set3,8)));
    }
}
