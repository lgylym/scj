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
        int relationSizeBase = 10000;
        int maxSetSize = 1<<7;//128
        int maxSetPool = 1<<10;//Integer.MAX_VALUE;//1<<8;////1024
        int sig_len = 32<<2; //better to be a multiplier of Integer.SIZE (32),128
        //take input, store them in R and S

//        //NLJ test
//        for(int i = 1; i < 6; i++) {
//            try{
//                ArrayList<SimpleTuple> R = Generator.generateRandomRelation(i*10000, maxSetSize, maxSetPool, SimpleTuple.class);
//                ArrayList<SimpleTuple> S = Generator.generateRandomRelation(i*10000, maxSetSize, maxSetPool, SimpleTuple.class);
//
//                SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
//                //DAGJoinAlgorithms dja = new DAGJoinAlgorithms();
//
//                /****simple join algorithm*********************************************************************************/
//                startTime = System.nanoTime();
//                sja.NLNormalJoin(R,S);
//                estimatedTime = System.nanoTime() - startTime;
//                System.out.print(estimatedTime/(1000000.0));
//                System.out.print("ms\n");
//
//                R.clear();
//                S.clear();
//
//            }catch (Exception e) {
//                System.out.print(e);
//            }
//        }


//        //NLJ Signature test
//        for(int i = 1; i < 6; i++) {
//            try{
//                ArrayList<SimpleTuple> R = Generator.generateRandomRelation(i*10000, maxSetSize, maxSetPool, SimpleTuple.class);
//                ArrayList<SimpleTuple> S = Generator.generateRandomRelation(i*10000, maxSetSize, maxSetPool, SimpleTuple.class);
//
//                ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
//                ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);
//
//                SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
//                //DAGJoinAlgorithms dja = new DAGJoinAlgorithms();
//
//                /****simple join algorithm*********************************************************************************/
//                startTime = System.nanoTime();
//                sja.NLSignatureJoin(R1, S1, sig_len / Integer.SIZE);
//                estimatedTime = System.nanoTime() - startTime;
//                System.out.print(estimatedTime/(1000000.0));
//                System.out.print("ms\n");
//
//                R.clear();R1.clear();
//                S.clear();S1.clear();
//
//            }catch (Exception e) {
//                System.out.print(e);
//            }
//        }


        //SHJ test
        for(int i = 1; i < 6; i++) {
            try{
                ArrayList<SimpleTuple> R = Generator.generateRandomRelation(i*relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);
                ArrayList<SimpleTuple> S = Generator.generateRandomRelation(i*relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);

                ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
                ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);

                SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
                //DAGJoinAlgorithms dja = new DAGJoinAlgorithms();

                /****simple join algorithm*********************************************************************************/
                int bitcount = (int)Math.floor(Math.log(i*10000)/Math.log(2));
                System.out.print("*"+bitcount);
                startTime = System.nanoTime();
                sja.SHJ(R1, S1, sig_len / Integer.SIZE, bitcount);
                estimatedTime = System.nanoTime() - startTime;
                System.out.print(estimatedTime/(1000000.0));
                System.out.print("ms\n");

                R.clear();R1.clear();
                S.clear();S1.clear();

            }catch (Exception e) {
                System.out.print(e);
            }
        }

//        try{
//            ArrayList<SimpleTuple> R = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);
//            ArrayList<SimpleTuple> S = Generator.generateRandomRelation(relationSize, maxSetSize, maxSetPool, SimpleTuple.class);
//
//            SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
//            DAGJoinAlgorithms dja = new DAGJoinAlgorithms();
//
//
//            /****simple join algorithm*********************************************************************************/
//            startTime = System.nanoTime();
//            sja.NLNormalJoin(R,S);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");

//            /****simple join algorithm with int array as long_signature*****************************************************/
//            ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
//            ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);
//            startTime = System.nanoTime();
//            sja.NLSignatureJoin(R1,S1,sig_len/Integer.SIZE);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//            R1.clear();S1.clear();

//            /****simple join algorithm with int array as long_signature*****************************************************/
//            ArrayList<BitmapSimpleTuple> Rbit = Generator.toBitmapSimpleTuples(R);
//            ArrayList<BitmapSimpleTuple> Sbit = Generator.toBitmapSimpleTuples(S);
//            startTime = System.nanoTime();
//            sja.NLBitmapJoin(Rbit, Sbit, sig_len/Integer.SIZE);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//            Rbit.clear();Sbit.clear();


//
////            /***simple join algorithm with bitset as long_signature*******************************************************/
////            ArrayList<BitsetSimpleTuple> R2 = Generator.toBigintSimpleTuples(R);
////            ArrayList<BitsetSimpleTuple> S2 = Generator.toBigintSimpleTuples(S);
////            startTime = System.nanoTime();
////            sja.NLBigintSignatureJoin(R2, S2, sig_len);
////            estimatedTime = System.nanoTime() - startTime;
////            System.out.print(estimatedTime/(1000000.0));
////            System.out.print("ms\n");
////            R2.clear();S2.clear();
//
//            /****SHJ algorithm with int array as long_signature********************************************************/
//            ArrayList<SigSimpleTuple> R3 = Generator.toSigSimpleTuples(R);
//            ArrayList<SigSimpleTuple> S3 = Generator.toSigSimpleTuples(S);
//            startTime = System.nanoTime();
//            int bitmask = (int)Math.ceil(1.738*Math.log(relationSize));
//            sja.SHJ(R3,S3,sig_len/Integer.SIZE,bitmask);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//            R3.clear();S3.clear();
//
//            /****SHJ array algorithm with int array as long_signature********************************************************/
//            ArrayList<SigSimpleTuple> R4 = Generator.toSigSimpleTuples(R);
//            ArrayList<SigSimpleTuple> S4 = Generator.toSigSimpleTuples(S);
//            startTime = System.nanoTime();
//            sja.SHJArray(R4, S4, sig_len / Integer.SIZE);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//            R4.clear();S4.clear();
            /*******DSHJ algorithm using hash map***********************************************************************/

//            ArrayList<DAGSST> R5 = Generator.transferRelation(R, DAGSST.class);
//            ArrayList<DAGSST> S5 = Generator.transferRelation(S, DAGSST.class);
//            startTime = System.nanoTime();
//
//            dja.DSHJ(R5, S5, sig_len / Integer.SIZE);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//            R5.clear();S5.clear();

//            /****SHJ array algorithm with int array as long_signature********************************************************/
//            ArrayList<SigSimpleTuple> R6 = Generator.toSigSimpleTuples(R);
//            ArrayList<SigSimpleTuple> S6 = Generator.toSigSimpleTuples(S);
//            startTime = System.nanoTime();
//            sja.SHJBitmap(R6, S6, sig_len / Integer.SIZE, 12);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//
//            startTime = System.nanoTime();
//            sja.SHJBitmap(R6, S6, sig_len / Integer.SIZE, 13);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//
//            startTime = System.nanoTime();
//            sja.SHJBitmap(R6, S6, sig_len / Integer.SIZE, 14);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//
//            startTime = System.nanoTime();
//            sja.SHJBitmap(R6, S6, sig_len / Integer.SIZE, 15);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");
//
//            R6.clear();S6.clear();
//


//        }catch(Exception e) {
//            System.out.print(e);
//        }
    }
}
