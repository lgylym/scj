package seeqr.scj;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class run through the tests
 * Created by yluo on 12/19/13.
 */
public class MakeTests {

    public static long[] sig_compare_time = {0,0,0,0};//call time measure
    public static long[] sig_compare_call = {0,0,0,0};//call count
    public static long set_compare = 0;//call time measure
    public final static int relationSizeBase = 10000;

    public static void reset_counters() {
        for(int i = 0; i < 4; i++) {
            sig_compare_time[i] = 0;
            sig_compare_call[i] = 0;
        }

        set_compare = 0;
    }

    public static void print_counters() {
        for(int i = 0; i < 4; i++) {
            System.out.println("sig compare "+ i + " take "+MakeTests.sig_compare_time[i]/1000000+"ms");
        }
        System.out.println("set compare take "+MakeTests.set_compare/1000000+"ms");
        for(int i = 0; i < 4; i++) {
            System.out.println("sig compare call " + i + " " + MakeTests.sig_compare_call[i]);
        }

        System.out.println("p1:" + (MakeTests.sig_compare_call[0]+MakeTests.sig_compare_call[1]+
                                    MakeTests.sig_compare_call[2]+MakeTests.sig_compare_call[3])/
                (relationSizeBase*relationSizeBase+0.0));
        System.out.println("p2:" + (MakeTests.sig_compare_call[1]+
                MakeTests.sig_compare_call[2]+MakeTests.sig_compare_call[3])/
                (relationSizeBase*relationSizeBase+0.0));
        System.out.println("p3:" + (MakeTests.sig_compare_call[2]+MakeTests.sig_compare_call[3])/
                (relationSizeBase*relationSizeBase+0.0));
        System.out.println("p4:" + (MakeTests.sig_compare_call[3])/
                (relationSizeBase*relationSizeBase+0.0));
    }

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
        //int relationSizeBase = 30000;
        int maxSetSize = 100;//1<<7;//128
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
        for(int i = 100; i <= 100; i++) {

            try{
                Generator.rng.setSeed(0);
                ArrayList<SimpleTuple> R = Generator.generateRandomRelation(i*relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);
                ArrayList<SimpleTuple> S = Generator.generateRandomRelation(i*relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);

                ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
                ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);
                //for(int j = (int)Math.ceil(Math.log(2*i*10000)/Math.log(2))-10; j < 25; j++){
                SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
                //AdvancedJoinAlgorithms aja = new AdvancedJoinAlgorithms();
                //DAGJoinAlgorithms dja = new DAGJoinAlgorithms();
                //int j = 18;
                /****simple join algorithm*********************************************************************************/
                //System.out.print(j+",");
                int bitcount = (int)Math.floor(Math.log(i*10000)/Math.log(2));
                System.out.print("*"+bitcount);
                startTime = System.nanoTime();
                //aja.ASHJ_Patricia(R1,S1,j);
                sja.SHJ(R1, S1, sig_len / Integer.SIZE, bitcount);
                estimatedTime = System.nanoTime() - startTime;
                System.out.print(estimatedTime/(1000000.0));
                System.out.print("ms\n");
                //}
                R.clear();R1.clear();
                S.clear();S1.clear();

            }catch (Exception e) {
                System.out.print(e);
            }

        }

//        /**test on PRETTI**/
//        for(int i = 9; i <= 10; i++) {
//            try{
//                ArrayList<SimpleTuple> R = Generator.generateRandomRelation(i*relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);
//                ArrayList<SimpleTuple> S = Generator.generateRandomRelation(i*relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);
//
//
//                AdvancedJoinAlgorithms aja = new AdvancedJoinAlgorithms();
//                startTime = System.nanoTime();
//                aja.PETTI_Join(R,S);
//                estimatedTime = System.nanoTime() - startTime;
//                System.out.println(estimatedTime/(1000000.0)+"ms");
//
//            }catch(Exception e) {
//
//            }
//        }


        try{
            ArrayList<SimpleTuple> R = Generator.generateRandomRelation(relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);
            ArrayList<SimpleTuple> S = Generator.generateRandomRelation(relationSizeBase, maxSetSize, maxSetPool, SimpleTuple.class);

            SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
            DAGJoinAlgorithms dja = new DAGJoinAlgorithms();
            AdvancedJoinAlgorithms aja = new AdvancedJoinAlgorithms();



//            /****simple join algorithm*********************************************************************************/
//            startTime = System.nanoTime();
//            sja.NLNormalJoin(R,S);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0));
//            System.out.print("ms\n");

            /****ASHJ_trie join algorithm******************************************************************************/
            {
            ArrayList<SigSimpleTuple> R7 = Generator.toSigSimpleTuples(R);
            ArrayList<SigSimpleTuple> S7 = Generator.toSigSimpleTuples(S);

            int bitmask = (int)Math.floor(Math.log(relationSizeBase)/Math.log(2));
            //for(int bitmask = 10; bitmask < 32; bitmask++){
            //}
//            Thread.sleep(5000);//5 second
//                startTime = System.nanoTime();
//                sja.SHJ(R7,S7,sig_len/Integer.SIZE,bitmask);//
//                estimatedTime = System.nanoTime() - startTime;
//                System.out.print(estimatedTime/(1000000.0)+"ms\n");

//                startTime = System.nanoTime();
//                aja.PETTI_Join(R,S);
//                estimatedTime = System.nanoTime() - startTime;
//                System.out.println(estimatedTime/(1000000.0)+"ms");


//                for(int sigLen = 100; sigLen < 101; sigLen = sigLen + 4) {
//                    startTime = System.nanoTime();
//                    //aja.ASHJ_Trie(R7,S7,sig_len/Integer.SIZE,bitmask);
//                    aja.ASHJ_Patricia(R7,S7,sigLen);
//                    //aja.PETTI_Join(R,S);
//                    estimatedTime = System.nanoTime() - startTime;
//                    System.out.print(sigLen + ","+estimatedTime/(1000000.0)+"ms\n");
//            }

//                aja.pt = new PatriciaTrie(3);
//                ArrayList<SigSimpleTuple> S8 = new ArrayList<SigSimpleTuple>();
//                for(SigSimpleTuple s:S7) {
//                    s.signature = Utils.create_sig_normal(s.setValues,3);
//                    aja.pt.put(s);
//                    PatriciaTrie.PatriciaTrieNode node = aja.pt.search(aja.pt.root, s.signature, 0);
//                    if(node == null) {
//                        //System.out.print(s);
//                        System.out.println(node);
//                        S8.add(s);
//                    }
//                }

//                for(SigSimpleTuple s:S8) {
//                    s.signature = Utils.create_sig_normal(s.setValues,3);
//                    aja.pt.put(s);
//                    PatriciaTrie.PatriciaTrieNode node = aja.pt.search(aja.pt.root, s.signature, 0);
//                    if(node == null) {
//                        System.out.print(s);
//                        System.out.println(node);
//                        S8.add(s);
//                    }
//                }

//                result2.removeAll(result1);
//                for(Pair<SigSimpleTuple,SigSimpleTuple> p : result2) {
//                    PatriciaTrie.PatriciaTrieNode node = aja.pt.search(aja.pt.root, p.getRight().signature, 0);
//                    System.out.println(node);
//                }

            R7.clear();S7.clear();
            }

//            /****simple join algorithm with int array as long_signature*****************************************************/
//            ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
//            ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);
//            startTime = System.nanoTime();
//            for(int i = 0; i < 5;i++)
//                sja.NLSignatureJoin(R1,S1,sig_len/Integer.SIZE);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.println(estimatedTime / (1000000.0*5) + " ms");
//
//            sja.NLSignatureJoinMeasure(R1,S1,sig_len/Integer.SIZE);
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
//            //int bitmask = (int)Math.ceil(1.738*Math.log(relationSizeBase));
//            int bitmask = (int)Math.floor(Math.log(relationSizeBase)/Math.log(2));
//            //System.out.println(bitmask);
//            for(int i = 0; i < 5; i++)
//                sja.SHJ(R3,S3,sig_len/Integer.SIZE,bitmask);
//            estimatedTime = System.nanoTime() - startTime;
//            System.out.print(estimatedTime/(1000000.0*5));
//            System.out.print("ms\n");
//
//            sja.SHJMeasure(R3, S3, sig_len / Integer.SIZE, bitmask);
//
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


        }catch(Exception e) {
            System.out.print(e);
        }
    }
}
