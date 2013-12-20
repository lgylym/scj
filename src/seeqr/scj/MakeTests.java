package seeqr.scj;

import java.util.ArrayList;

/**
 * This class run through the tests
 * Created by yluo on 12/19/13.
 */
public class MakeTests {
    public static void main(String[] args) {
        long startTime;
        long estimatedTime;
        int relationSize = 10000;
        int maxSetSize = 100;
        //take input, store them in R and S
        try{
            ArrayList<SimpleTuple> R = Generator.generateRandomRelation(relationSize, maxSetSize, 1000, SimpleTuple.class);
            ArrayList<SimpleTuple> S = Generator.generateRandomRelation(relationSize, maxSetSize, 1000, SimpleTuple.class);
            startTime = System.nanoTime();
            SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
            sja.NLNormalJoin(R,S);
            estimatedTime = System.nanoTime() - startTime;
            System.out.print(estimatedTime/(1000000.0));
            System.out.print("ms\n");

            ArrayList<SigSimpleTuple> R1 = Generator.toSigSimpleTuples(R);
            ArrayList<SigSimpleTuple> S1 = Generator.toSigSimpleTuples(S);
            startTime = System.nanoTime();

            sja.NLSignatureJoin(R1,S1,4);

            estimatedTime = System.nanoTime() - startTime;
            System.out.print(estimatedTime/(1000000.0));
            System.out.print("ms\n");
            /*
            for(SigSimpleTuple s:R1) {
                System.out.print(s.toString());
            }*/

        }catch(Exception e) {
            System.out.print(e);
        }

    }
}
