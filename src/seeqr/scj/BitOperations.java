package seeqr.scj;

import java.util.BitSet;

/**
 * Created by yluo on 12/20/13.
 */
public class BitOperations {

    public static long INTMASK = 0x00000000ffffffffL;//get everything of an integer
    public static long RINTMASK = 0xffffffff00000000L;//get the rest of that integer
    public static int FIRSTBITS = 0xfff00000;//get the first bits of an integer, now 12 bits


    /**
     * logical and of two
     * the two should have the same length
     * @param stream1
     * @param stream2
     * @return
     */
    public static int[] bitAnd(int[] stream1, int[] stream2) {
        int[] result = new int[stream1.length];
        for(int i = 0; i < stream1.length; i++) {
            result[i] = stream1[i] & stream2[i];
        }
        return result;
    }

    /**
     * add 1 to stream1
     * @param stream1
     * @return
     */
    public static int[] bitAddOne(int[] stream1) {
        int[] result = new int[stream1.length];
        long temp;
        int carry = 1;
        for(int i = stream1.length-1; i >= 0; i --) {
            temp = (INTMASK & (long)stream1[i])+carry;
            result[i] = (int)temp;
            //if((temp > Integer.MAX_VALUE) || (temp < Integer.MIN_VALUE)) {
            if((temp&RINTMASK) != 0) {
                carry = 1;
            }else {
                carry = 0;
            }
        }
        return result;
    }

    /**
     * add stream1 and stream2
     * @param stream1
     * @param stream2
     * @return
     */
    public static int[] bitAdd(int[] stream1, int[] stream2) {
        int slen = stream1.length;
        int[] result = new int[slen];
        long temp;
        int carry = 0;
        for(int i = slen-1; i >= 0; i--) {
            temp = (INTMASK & (long)stream1[i]) + (INTMASK & (long)stream2[i]) + carry;
            result[i] = (int)temp;
            if((temp & RINTMASK) != 0) {
                carry = 1;
            }else {
                carry = 0;
            }
        }
        return result;
    }


    /**
     * int array to a string
     * @param stream
     */
    public static String toStringBitStream(int[] stream) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < stream.length; i++) {
            for(int j = 0; j < Integer.numberOfLeadingZeros(stream[i]); j++) {
                sb.append("0");
            }

            if(stream[i] != 0){
                sb.append(Integer.toBinaryString(stream[i]));
            }
            sb.append(" ");
        }
        return sb.toString();
    }


    /**
     * stream1 minus stream2, Two's complement subtraction
     * @param stream1
     * @param stream2
     * @return
     */
    public static  int[] bitSubtract(int[] stream1, int[] stream2) {
        int alen = stream1.length; // array length
        int[] result = new int[alen];

        //result = not stream2
        for(int i = 0; i < alen; i++) {
            result[i] = ~stream2[i];
        }

        result = bitAddOne(result);
        result = bitAdd(stream1,result);
        return result;
    }

    /**
     * return true if the byte stream has some 1s
     * @param stream
     * @return
     */
    public static boolean hasOnes(int[] stream) {
        for(int s:stream) {
            if(s != 0) {
                return true;
            }
        }
        return false;
    }


    public static void main(String[] args) {
        /*
        int[] a = new int[3];
        a[0] = 0xffffffff;
        a[1] = 0xffffffff;
        a[2] = 0xffffffff;

        int[] c = new int[3];
        c[0] = Integer.MAX_VALUE;
        c[1] = Integer.MAX_VALUE;
        c[2] = Integer.MAX_VALUE;

        printBitStream(c);
        int[] b = bitAddOne(c);
        printBitStream(b);
        printBitStream(bitAddOne(b));

        printBitStream(a);
        printBitStream(bitAddOne(a));
        */

        int[] mask = new int[2];
        mask[0] = 1;
        mask[1] = 0xC0000000;

        int[] result = new int[2];
        result[1] = 1;

        while(hasOnes(result)) {
            result = bitAnd(mask, bitSubtract(result,mask));
            System.out.println(toStringBitStream(result));
        }

    }

}
