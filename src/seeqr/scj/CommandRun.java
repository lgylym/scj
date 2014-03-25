package seeqr.scj;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.ardverk.collection.IntegerKeyAnalyzer;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by yluo on 3/19/14.
 */
public class CommandRun {

    public static void printHelp() {
        System.out.println("\nJoin execution usage:\n");
        System.out.println("java -jar some.jar\n" +
                "-r=fileR file path for relation R\n" +
                "-s=fileS file path for relation S\n" +
                "-j=joinMethod {nlj, nljsig, shj, pretti, ptsj}\n" +
                "[l=sigLength] length for signature in integer, for nljsig shj and ptsj, system will try to assign the best if not specified\n");
        System.out.println("Example: java -jar some.jar -r=R.data -s=S.data -j=shj -l=4");
    }


    /**
     * scj R \supeq S
     * r - file for relation R
     * s - file for relation S
     * j - join methods, choices from {nlj, nljsig, shj, pretti, ptsj}
     * l - optional, length of signature in integer, for nljsig shj and ptsj
     * @param args
     */
    public static void main(String[] args) {

        if(args.length <= 1) {
            printHelp();
            return;
        }

        int numberOfRuns = 5;
        OptionParser parser = new OptionParser( "r:s:j:l::" );
        OptionSet options = parser.parse(args);
        String rFile = (String) options.valueOf("r");
        String sFile = (String) options.valueOf("s");
        String joinMethod = (String) options.valueOf("j");
        int sigLength = 0;
        if(options.hasArgument("l")) {
            sigLength = Integer.valueOf((String) options.valueOf("l"));
        }
        long startTime;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        //1. load two relations
        //2. perform the join algorithm and test
        switch (joinMethod) {
            case "nlj":
                ArrayList<SimpleTuple> R = RelationLoader.loadRelation(rFile,SimpleTuple.class);
                ArrayList<SimpleTuple> S = RelationLoader.loadRelation(sFile,SimpleTuple.class);
                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    SimpleJoinAlgorithms.NLNormalJoin(R,S);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                R.clear();
                S.clear();
                break;
            case "nljsig":
                ArrayList<SigSimpleTuple> R4 = RelationLoader.loadRelation(rFile,SigSimpleTuple.class);
                ArrayList<SigSimpleTuple> S4 = RelationLoader.loadRelation(sFile,SigSimpleTuple.class);
                if(sigLength <= 0) {
                    //get some set card. info
                    int maxSetCard = 0;
                    //sample the first 1000 tuples
                    for(int i = 0; i < 1000 && i < R4.size(); i++) {
                        maxSetCard = maxSetCard < R4.get(i).setSize ? R4.get(i).setSize : maxSetCard;
                    }
                    sigLength = (int)Math.ceil((1/(1-Math.pow(0.5,1/(maxSetCard+0.0))))/ Integer.SIZE);
                    //(maxSetCard/Integer.SIZE);
                    //System.out.println("Signature length has to be bigger than 0");
                    //return;
                    System.err.println("Sig length set to " + sigLength);
                }
                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    SimpleJoinAlgorithms.NLSignatureJoin(R4, S4, sigLength);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                R4.clear();
                S4.clear();


                break;

            case "shj":
                ArrayList<SigSimpleTuple> R1 = RelationLoader.loadRelation(rFile,SigSimpleTuple.class);
                ArrayList<SigSimpleTuple> S1 = RelationLoader.loadRelation(sFile,SigSimpleTuple.class);
                int bitCount = (int)Math.floor(Math.log(R1.size())/Math.log(2));
                if(sigLength <= 0) {
                    //get some set card. info
                    int maxSetCard = 0;
                    //sample the first 1000 tuples
                    for(int i = 0; i < 1000 && i < R1.size(); i++) {
                        maxSetCard = maxSetCard < R1.get(i).setSize ? R1.get(i).setSize : maxSetCard;
                    }
                    sigLength = (int)Math.ceil((1/(1-Math.pow(0.5,1/(maxSetCard+0.0))))/ Integer.SIZE);
                            //(maxSetCard/Integer.SIZE);
                    //System.out.println("Signature length has to be bigger than 0");
                    //return;
                    System.err.println("Sig length set to " + sigLength);
                }

                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    SimpleJoinAlgorithms.SHJ(R1, S1, sigLength, bitCount);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                R1.clear();
                S1.clear();
                break;
            case "pretti":
                ArrayList<SimpleTuple> R2 = RelationLoader.loadRelation(rFile,SimpleTuple.class);
                ArrayList<SimpleTuple> S2 = RelationLoader.loadRelation(sFile,SimpleTuple.class);
                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    AdvancedJoinAlgorithms.PETTI_Join(R2, S2);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                R2.clear();
                S2.clear();
                break;
            case "ptsj":
                ArrayList<SigSimpleTuple> R3 = RelationLoader.loadRelation(rFile,SigSimpleTuple.class);
                ArrayList<SigSimpleTuple> S3 = RelationLoader.loadRelation(sFile,SigSimpleTuple.class);

                //near optimal solution here
                //sigLength = (int)Math.ceil(Math.log(2*R3.size())/Math.log(2));
                if(sigLength <= 0) {
                    sigLength = (int)Math.ceil(Math.log(2*R3.size())/Math.log(2))+1;
                    System.err.println("Sig length set to " + sigLength);
                    //System.out.println("Signature length has to be bigger than 0");
                    //return;
                }


                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    AdvancedJoinAlgorithms.ASHJ_Patricia(R3, S3, sigLength);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                R3.clear();
                S3.clear();
                break;
        }

        System.out.println(Paths.get(rFile).getFileName().toString()+"," +
                Paths.get(sFile).getFileName().toString() + "\t"+ joinMethod+"\t"+stats.getPercentile(50)+"ms");
    }

}
