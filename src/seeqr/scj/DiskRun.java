package seeqr.scj;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.mapdb.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by yluo on 4/10/2014.
 */
public class DiskRun {

    public static void printHelp() {
        System.out.println("\nDisk join execution usage:\n");
        System.out.println("java -jar some.jar\n" +
                "-r=fileR file path for relation R\n" +
                "-s=fileS file path for relation S\n" +
                "-j=joinMethod {nlj, nljsig, shj, pretti, ptsj}\n" +
                "[l=sigLength] length for signature in integer, for nljsig shj and ptsj, system will try to assign the best if not specified\n" +
                "[p=partitionSize] partitionSize for breaking the relation into pieces and join\n");

        System.out.println("Example: java -jar some.jar -r=R.data -s=S.data -j=shj -l=4 -p=10000");
    }

    /**
     * scj R \supeq S
     * r - file for relation R
     * s - file for relation S
     * j - join methods, choices from {nlj, nljsig, shj, pretti, ptsj}
     * l - optional, length of signature in integer, for nljsig shj and ptsj
     * p - optional
     * @param args
     */
    public static void main(String[] args) {

        if(args.length <= 1) {
            printHelp();
            return;
        }

        int numberOfRuns = 5;
        OptionParser parser = new OptionParser( "r:s:j:p::l::" );
        OptionSet options = parser.parse(args);
        String rFile = (String) options.valueOf("r");
        String sFile = (String) options.valueOf("s");
        String joinMethod = (String) options.valueOf("j");
        int sigLength = 0;
        int partitionSize = 0;
        if(options.hasArgument("l")) {
            sigLength = Integer.valueOf((String) options.valueOf("l"));
        }
        if(options.hasArgument("p")) {
            partitionSize = Integer.valueOf((String) options.valueOf("p"));
        }

        long startTime;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        //get some stats from both relations from the beginning, sample 1000 tuples, such as dom card, set card

        //fill in the setcard and domcard
        DescriptiveStatistics setcard = new DescriptiveStatistics();
        DescriptiveStatistics domcard = new DescriptiveStatistics();
        //we probe only S
        try{
            BufferedReader reader = new BufferedReader(new FileReader(sFile));
            String line;
            String[] words;
            int lineCount = 0;

            while((line = reader.readLine()) != null) {
                if(line.startsWith("##")) {
                    continue;
                }
                words = line.trim().split(" ");
                SimpleTuple st = RelationLoader.getTuple(words,SimpleTuple.class);
                setcard.addValue(st.setValues.length);
                for(int element = 0; element < st.setSize; element++) {
                    domcard.addValue(st.setValues[element]);
                }
                lineCount ++;
                if(lineCount >= 1000) {
                    break;
                }
            }
            reader.close();
        }catch (Exception e) {

        }

        //decide the sigLength if not specified
        if(sigLength == 0){
            if(joinMethod.equals("shj") || joinMethod.equals("nljsig")) {
                sigLength = (int)Math.ceil((1/(1-Math.pow(0.5,1/(setcard.getPercentile(90)))))/ Integer.SIZE);
            }else if(joinMethod.equals("ptsj")) {
                int left = (int)Math.ceil(setcard.getPercentile(90)/8)+1;
                int right = (int)Math.ceil(domcard.getPercentile(90)/32)+1;
                int breakpoint = 32;
                if(left <= breakpoint && right <= breakpoint) {
                    sigLength = (int)(0.25 * left + 0.75 * right);
                }else if(left <= breakpoint && right >= breakpoint) {
                    sigLength = (int)(0.75 * left + 0.25 * right);
                }else if(left > breakpoint && right < breakpoint) {
                    sigLength = right;
                }else {
                    sigLength = (int)(0.75 * left + 0.25 * right);
                }
            }
            System.err.println("Set card.: " + setcard.getPercentile(90));
            System.err.println("Dom. card.: " + domcard.getPercentile(90));
            System.err.println("Signatue length: " + sigLength);
        }

        //decide partition size to work on
        if(partitionSize == 0) {
            //apply the formula
            int a;
            int b;
            if(joinMethod.equals("shj")) {
                a = 2;
                b = 150;
            }else if(joinMethod.equals("ptsj")) {
                a = 2;
                b = 300;
            }else if(joinMethod.equals("pretti")) {
                a = 60;
                b = 0;
            }else {
                a = 2;
                b = 150;
            }
            //memory in bytes that each element may consume
            int elementMem = (int)(a * setcard.getPercentile(90)) + b;
            Runtime runtime = Runtime.getRuntime();
            runtime.gc();
            long totalFreeMem = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
            partitionSize = (int)((totalFreeMem*0.8)/(2*elementMem));

            //System.err.println(totalFreeMem*0.8);
            System.err.println("Tuple mem: " + elementMem + " byte");
            System.err.println("Partition size: " + partitionSize);
        }

        //create dbs, load files to dbs
        DB db = DBMaker.newFileDB(new File("scjdb")).closeOnJvmShutdown().deleteFilesAfterClose().transactionDisable().make();

        //DB db = DBMaker.newMemoryDB().make();

        //1. load two relations
        //2. perform the join algorithm and test
        switch (joinMethod) {
            case "shj":
                Set<SigSimpleTuple> Rshj = db.getTreeSet("R");
                Set<SigSimpleTuple> Sshj = db.getTreeSet("S");
                //store tuples in maps
                RelationLoader.loadRelationDB(Rshj,rFile,SigSimpleTuple.class,sigLength);
                RelationLoader.loadRelationDB(Sshj, sFile, SigSimpleTuple.class, sigLength);

                //CommandRun.printMemory();
                System.err.println("Data loading done.");

                int bitCount = (int)Math.floor(Math.log(Rshj.size())/Math.log(2));

                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    SimpleJoinAlgorithms.SHJDB(Rshj, Sshj, sigLength, bitCount, partitionSize);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                Rshj.clear();
                Sshj.clear();
                break;

            case "ptsj":
                Set<SigSimpleTuple> Rptsj = db.getTreeSet("R");
                Set<SigSimpleTuple> Sptsj = db.getTreeSet("S");

                RelationLoader.loadRelationDB(Rptsj, rFile, SigSimpleTuple.class, sigLength);
                RelationLoader.loadRelationDB(Sptsj,sFile,SigSimpleTuple.class,sigLength);

                System.err.println("Data loading done.");

                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    AdvancedJoinAlgorithms.ASHJ_PatriciaDB(Rptsj, Sptsj, sigLength, partitionSize);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                Rptsj.clear();
                Sptsj.clear();
                break;

            case "pretti":
                Set<SimpleTuple> Rpretti = db.getTreeSet("R");
                Set<SimpleTuple> Spretti = db.getTreeSet("S");

                RelationLoader.loadRelationDB(Rpretti, rFile, SimpleTuple.class, sigLength);
                RelationLoader.loadRelationDB(Spretti, sFile, SimpleTuple.class, sigLength);

                System.err.println("Data loading done.");

                for(int i = 0; i < numberOfRuns; i++) {
                    startTime = System.nanoTime();
                    AdvancedJoinAlgorithms.PETTI_JoinDB(Rpretti, Spretti, partitionSize);
                    stats.addValue((System.nanoTime()-startTime)/(1000000.0));
                }
                Rpretti.clear();
                Spretti.clear();
                break;

            default:
                System.err.println(joinMethod + " is not supported in disk version");
        }


        System.out.println(Paths.get(rFile).getFileName().toString()+"," +
                Paths.get(sFile).getFileName().toString() + "\t"+ joinMethod+"\t"+stats.getMean()/1000 + "\t" + stats.getStandardDeviation()/1000 + "\t" + stats.getPercentile(50)/1000);
        db.close();
    }


}
