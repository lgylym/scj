package seeqr.scj;

import static java.util.Arrays.*;
import static java.util.Collections.*;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by yluo on 3/19/14.
 */
public class CommandRun {

    /**
     * scj R \supeq S
     * r - file for relation R
     * s - file for relation S
     * j - join methods, choices from {nlj, nljsig, shj, pretti, ptsj}
     * l - optional, length of signature in integer, for nljsig shj and ptsj
     * @param args
     */
    public static void main(String[] args) {

        OptionParser parser = new OptionParser( "r:s:j:l::" );
        OptionSet options = parser.parse(args);
        String rFile = (String) options.valueOf("r");
        String sFile = (String) options.valueOf("s");
        String joinMethod = (String) options.valueOf("j");
        int sigLength = 0;
        if(joinMethod == "nljsig" || joinMethod == "shj" || joinMethod == "ptsj") {
            sigLength = Integer.valueOf((String) options.valueOf("l"));
        }
        //1. load two relations
        //2. perform the join algorithm and test
        switch (joinMethod) {
            case "nlj":
                ArrayList<SimpleTuple> R = RelationLoader.loadRelation(rFile,SimpleTuple.class);
                ArrayList<SimpleTuple> S = RelationLoader.loadRelation(sFile,SimpleTuple.class);
                SimpleJoinAlgorithms.NLNormalJoin(R,S);
                break;
        }
    }

}
