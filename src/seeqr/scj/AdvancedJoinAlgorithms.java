package seeqr.scj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yluo on 2/19/14.
 */
public class AdvancedJoinAlgorithms {

    /**
     *
     * ASHJ_Trie using trie to do subset enumeration.
     * It is part of the ASHJ (Advanced Signature Hash Join) discussion.
     *
     * @param R
     * @param S
     * @param sigLen signature length in bits
     * @param useBitsInMap bit mask length in the hash table
     */
    public void ASHJ_Trie(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sigLen, int useBitsInMap) {

        HashMap<Integer,List<SigSimpleTuple>> hashMap = new HashMap<Integer, List<SigSimpleTuple>>(S.size()/2);
        final int bitmask = (1<<31)>>(useBitsInMap-1);//the first useBitsInMap bits set to 1

        SimpleTrie trie = new SimpleTrie();
        //put all tuples in S to hashmap, and in trie
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sigLen);
            int key = bitmask & (s.signature[0]);

            List<SigSimpleTuple> l = hashMap.get(key);
            if(l == null) {
                l = new ArrayList<SigSimpleTuple>();
                l.add(s);
                hashMap.put(key,l);
            }else {
                l.add(s);
            }

            trie.put(key,useBitsInMap);
        }


        LinkedList<SimpleTrie.SimpleTrieNode> queue = new LinkedList<SimpleTrie.SimpleTrieNode>();

        int count = 0;

        //System.out.println("prepare done");

        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sigLen);
            int mask = bitmask & r.signature[0];
            //get all subsets of mask that exists in trie

            for(int subset:trie.getSubsets(mask,useBitsInMap)) {
                List<SigSimpleTuple> l = hashMap.get(subset);
                for(SigSimpleTuple s:l) {
                    //temp++;
                    if((r.setSize >= s.setSize) && (Utils.compare_sig_contain(r.signature, s.signature)>=0)) {
                        //phit++;
                        if(Utils.compare_set(r.setValues, s.setValues) >= 0) {
                            count ++;
                        }
                    }
                }
            }
        }

        System.out.println("ASHJ_Trie will return "+Integer.toString(count)+" results");

    }

}
