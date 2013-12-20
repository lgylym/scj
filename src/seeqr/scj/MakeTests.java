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
        //take input, store them in R and S
        ArrayList<Tuple> R = Generator.generateRandomRelation(20000, 100, 1000);
        ArrayList<Tuple> S = Generator.generateRandomRelation(20000, 100, 1000);

        startTime = System.nanoTime();
        SimpleJoinAlgorithms sja = new SimpleJoinAlgorithms();
        sja.NLNormalJoin(R,S);
        estimatedTime = System.nanoTime() - startTime;
        System.out.print(estimatedTime/(1000000.0));
        System.out.print("ms");
    }
}
