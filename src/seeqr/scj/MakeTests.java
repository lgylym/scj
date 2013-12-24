package seeqr.scj;

import java.util.ArrayList;

/**
 * This class run through the tests
 * Created by yluo on 12/19/13.
 */
public class MakeTests {
    public static void main(String[] args) {

//        BigInteger a = BigInteger.valueOf(0xFA);
//        BigInteger s = BigInteger.valueOf(1);
//        BigInteger zero = BigInteger.valueOf(0);
//        while(!s.equals(zero)) {
//            s = a.and(s.subtract(a));
//            System.out.println(Integer.toBinaryString(s.intValue()));
//        }

        long startTime;
        long estimatedTime;
        int relationSize = 20000;
        int maxSetSize = 100;
        int maxSetPool = 1000;
        int sig_len = 128; //better to be a multiplier of Long.SIZE (64)
        //take input, store them in R and S
        try{
            ArrayList<SimpleTuple> R = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);
            ArrayList<SimpleTuple> S = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);

            /****simple join algorithm*********************************************************************************/
            startTime = System.nanoTime();
            SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
            sja.NLNormalJoin(R,S);
            estimatedTime = System.nanoTime() - startTime;
            System.out.print(estimatedTime/(1000000.0));
            System.out.print("ms\n");

            /****simple join algorithm with int array as signature*****************************************************/
            ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
            ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);
            startTime = System.nanoTime();
            sja.NLSignatureJoin(R1,S1,sig_len/Integer.SIZE);
            estimatedTime = System.nanoTime() - startTime;
            System.out.print(estimatedTime/(1000000.0));
            System.out.print("ms\n");
            R1.clear();S1.clear();

            /****simple join algorithm with bitset as signature********************************************************/
            ArrayList<BitsetSimpleTuple> R2 = Generator.toBigintSimpleTuples(R);
            ArrayList<BitsetSimpleTuple> S2 = Generator.toBigintSimpleTuples(S);
            startTime = System.nanoTime();
            sja.NLBigintSignatureJoin(R2, S2, sig_len);
            estimatedTime = System.nanoTime() - startTime;
            System.out.print(estimatedTime/(1000000.0));
            System.out.print("ms\n");

        }catch(Exception e) {
            System.out.print(e);
        }
    }
}
