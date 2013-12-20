package seeqr.scj;

/**
 * Created by yluo on 12/20/13.
 */
public class BitOperations {
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

    public static  int[] bitSubtract(int[] stream1, int[] stream2) {
        int[] result = new int[stream1.length];
        for(int i = 0; i < stream1.length; i++) {
            result[i] = stream1[i] - stream2[i];
        }
        return result;
    }



    /**
     *
     * @param bstream1
     * @param bstream2
     * @return
     */
    public static byte[] bytes_and(byte[] bstream1, byte[] bstream2) {
        byte[] result = new byte[bstream1.length];
        for(int i = 0; i < bstream1.length; i++) {
            result[i] = (byte) (bstream1[i] & bstream2[i]);
        }
        return result;
    }

    public static byte[] bytes_minus(byte[] bstream1, byte[] bstream2) {
        return null;
    }

    /**
     * return true if the byte stream has some 1s
     * @param bstream
     * @return
     */
    public static boolean hasOnes(byte[] bstream) {
        for(byte b:bstream) {
            if(b != 0) {
                return true;
            }
        }
        return false;
    }


}
