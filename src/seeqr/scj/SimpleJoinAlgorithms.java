package seeqr.scj;

import java.util.ArrayList;

/**
 * Created by yluo on 12/19/13.
 */
public class SimpleJoinAlgorithms {
    /**
     * The most naive nested loop join
     * @param R
     * @param S
     */
    public void NLNormalJoin(ArrayList<Tuple> R, ArrayList<Tuple> S) {



        for(Tuple r:R) {
            for(Tuple s : S) {
                if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                    //output;
                }
            }
        }
    }
}
