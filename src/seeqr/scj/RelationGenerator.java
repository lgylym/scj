package seeqr.scj;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.math3.distribution.AbstractIntegerDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

import static java.util.Collections.sort;

/**
 * Relation generator generate relations based on input,
 * such as relation size, set card., distribution and so on.
 * Created by yluo on 3/20/2014.
 */
public class RelationGenerator {

    public enum Distribution {
        Uniform, Zipf, Poisson
    }

    //singleton
    protected static RelationGenerator instance;
    public static RelationGenerator getInstance() {
        if(instance == null) {
            instance = new RelationGenerator();
        }
        return instance;
    }

    private JDKRandomGenerator rng;

    public RelationGenerator() {
        rng = new JDKRandomGenerator();
    }

    public void setSeed(long seed) {
        rng.setSeed(seed);
    }

    public <T extends SimpleTuple> ArrayList<T> generateRelation(
            int tupleIDStart,//from where the tuple id start
            int relationSize,
            Distribution setcardDistribution,
            int setcardMax,
            Distribution setelemDistribution,
            int setelemMax,
            Class<T> cls
    ) {

        AbstractIntegerDistribution setcardGen;
        switch (setcardDistribution) {
            case Uniform:
                setcardGen = new UniformIntegerDistribution(rng, 1, setcardMax);//uniform counts from 1
                break;
            case Zipf:
                setcardGen = new ZipfDistribution(rng, setcardMax,1);//zipf counts from 1
                break;
            case Poisson:
                setcardGen = new PoissonDistribution(rng,setcardMax,
                        PoissonDistribution.DEFAULT_EPSILON,PoissonDistribution.DEFAULT_MAX_ITERATIONS);
                break;
            default:
                setcardGen = new UniformIntegerDistribution(rng, 1, setcardMax);
        }

        AbstractIntegerDistribution setelemGen;
        switch (setelemDistribution) {
            case Uniform:
                setelemGen = new UniformIntegerDistribution(rng, 1, setelemMax);
                break;
            case Zipf:
                setelemGen = new ZipfDistribution(rng, setelemMax,1);
                break;
            case Poisson:
                setelemGen = new PoissonDistribution(rng,setelemMax,
                        PoissonDistribution.DEFAULT_EPSILON,PoissonDistribution.DEFAULT_MAX_ITERATIONS);
                break;
            default:
                setelemGen = new UniformIntegerDistribution(rng, 1, setelemMax);
        }


        ArrayList<T> result = new ArrayList<T>(relationSize);
        T tuple;
        int setSize;
        TreeSet<Integer> generated = new TreeSet<Integer>();
        try{
            for(int i = tupleIDStart; i < relationSize + tupleIDStart; i++) {
                setSize = setcardGen.sample();
                if(setcardDistribution == Distribution.Poisson) {
                    setSize ++;//make sure there is no zero set size
                }
                tuple = cls.newInstance();
                tuple.tupleID = i;
                tuple.setSize = setSize;
                //now we do the set generation
                generated.clear();
                while (generated.size() < setSize) {
                    generated.add(setelemGen.sample());
                }
                generated.toArray();

                tuple.setValues = new int[generated.size()];
                int index = 0;
                for(Integer gen:generated) {
                    tuple.setValues[index] = gen;
                    index++;
                }
                //set generation done
                result.add(tuple);
            }
        }catch (Exception e) {
            System.out.print(e);
        }
        Collections.shuffle(result,rng);

        //compute size info
        int relationMem = 0;
        for(SimpleTuple r:result) {
            relationMem += 2 + r.setSize;
        }
        System.out.println("## Relation size:" + relationSize+ ", set card. distribution:" + setcardDistribution +
                ", set card. max:" + setcardMax + ", set elem. distribution:" + setelemDistribution +
                ", set elem. max:" + setelemMax);
        System.out.println("## Generating relation consumes " + relationMem * Integer.SIZE + " bytes main memory");

        return result;
    }


    /**
     * s - seed, optional, default value is 0
     * r - relation size
     * c - set cardinality distribution, choices from {uniform, zipf, poisson}
     * m - set cardinality max, for Poisson Distribution this is the mean
     * e - set element distribution, choices from {uniform, zipf, poisson}
     * d - set element max (domain), for Poisson Distribution this is the mean
     * @param args
     */
    public static void main(String[] args) {
        OptionParser parser = new OptionParser("s::r:c:m:e:d:");
        OptionSet options = parser.parse(args);
        long seed = 0;
        if(options.hasArgument("s")) {
            seed = Long.valueOf((String) options.valueOf("s"));
        }
        int relationSize = Integer.valueOf((String)options.valueOf("r"));
        String setcardDistribution = (String)options.valueOf("c");
        int setcardMax = Integer.valueOf((String)options.valueOf("m"));
        String setelemDistribution = (String)options.valueOf("e");
        int setelemMax = Integer.valueOf((String)options.valueOf("d"));

        RelationGenerator rg = getInstance();
        rg.setSeed(seed);

        ArrayList<SimpleTuple> R = rg.generateRelation
                (0,relationSize,getDistribution(setcardDistribution),setcardMax,
                        getDistribution(setelemDistribution),setelemMax,SimpleTuple.class);


        for(SimpleTuple r:R) {
            System.out.print(r.tupleID + " ");
            for(int element:r.setValues) {
                System.out.print(element + " ");
            }
            System.out.print("\n");
        }
    }

    private static Distribution getDistribution(String description) {
        switch (description) {
            case "uniform":
                return Distribution.Uniform;
            case "zipf":
                return Distribution.Zipf;
            case "poisson":
                return Distribution.Poisson;
        }
        System.out.println("Cannot recognize distribution in getDistribution()");
        return Distribution.Uniform;
    }
}
