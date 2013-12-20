package seeqr.scj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.primitives.Ints;

/**
 * Created by yluo on 12/19/13.
 */
public class SimpleJoinAlgorithms {
    /**
     * The most naive nested loop join
     * @param R
     * @param S
     */
    public void NLNormalJoin(ArrayList<SimpleTuple> R, ArrayList<SimpleTuple> S) {
        int count = 0;
        for(SimpleTuple r:R) {
            for(SimpleTuple s : S) {
                if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                    //output;
                    count ++;
                }
            }
        }
        System.out.println("will return "+Integer.toString(count)+" results");
    }

    /**
     * nested loop join with signatures
     * @param R
     * @param S
     */
    public void NLSignatureJoin(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
        //create signatures
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
        }

        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
        }
        //compare
        int count = 0;
        for(SigSimpleTuple r:R) {
            for(SigSimpleTuple s:S) {
                if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature))>=0) {
                    if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                        //output;
                        count ++;
                    }
                }
            }
        }
        System.out.println("will return "+Integer.toString(count)+" results");
    }

    /**
     * nested loop join with bigint signature
     * @param R
     * @param S
     * @param sig_len
     */
    public void NLBigintSignatureJoin(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {

    }

    /**
     * SHJ, but this is not the really SHJ, simplified with only a shorter signature.
     * @param R
     * @param S
     * @param sig_len
     */
    public void SHJ(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sig_len) {
        //create signatures
        //initially not too big, but big enough
        HashMap<List<Integer>,SigSimpleTuple> hashMap = new HashMap<List<Integer>, SigSimpleTuple>(R.size()/2);
        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sig_len);
            hashMap.put(Ints.asList(r.signature), r);
        }

        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sig_len);
            //enumerate all subsignatures

            //if one such signature is in hashMap,
            //return iterate in the bucket and compare real set value
        }
        //create hash map on R.signature
        //int hashmap_size = new Double(Math.sqrt(R.size())).intValue();


    }

}
