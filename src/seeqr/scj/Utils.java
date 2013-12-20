package seeqr.scj;

import org.omg.CORBA._PolicyStub;

/**
 * Created by yluo on 12/19/13.
 */
public class Utils {
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

        if(len1 > len2) {
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
        }else if(len1 == len2) {
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
     * return 0 if equal
     * return -1 if not equal
     * @param sig1
     * @param sig2
     * @return
     */
    public static int compare_sig(int[] sig1, int[] sig2) {
        for(int i = 0; i < sig1.length; i++) {
            if(sig1[i] != sig2[i]) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * the normal implementation, using modulo sig_len
     * one element -> one bit in the signature
     * @param set
     * @param sig_len how many integers we use to represent a signature
     * @return
     */
    public static int[] create_sig_normal(int[] set, int sig_len) {
        int[] signature = new int[sig_len];
        int remainder = 0;
        int index = 0;
        int bit = 0;
        for(int i = 0; i < set.length; i++) {
            remainder = set[i]%(32*sig_len);
            index = remainder / 32;
            bit = remainder % 32;
            signature[index] |= 1 << (bit);
        }
        return signature;
    }

    public static void main(String[] args) {
        int[] set1 = {0,1,2,3,4,5,6,7};
        int[] set2 = {1,2,3};
        int[] set3 = {1,2,5,6,7,8};
        int[] set4 = {64};
        int[] sig1 = {1,10};
        int[] sig2 = {1,10};
        int[] sig3 = {2,10};
        //System.out.print(compare_set(set1, set2));
        assert compare_set(set1, set2) == 1;
        assert compare_set(set2, set1) == -1;
        assert compare_set(set1, set3) == -1;
        assert compare_set(set1, set1) == 0;
        assert compare_sig(sig1, sig2) == 0;
        assert compare_sig(sig1, sig3) == -1;

        assert create_sig_normal(set4, 1)[0] == 1;
        assert create_sig_normal(set1, 1)[0] == 0xFF;
    }
}
