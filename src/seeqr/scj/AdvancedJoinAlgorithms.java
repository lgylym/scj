package seeqr.scj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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


    public void ASHJ_Patricia(ArrayList<SigSimpleTuple> R, ArrayList<SigSimpleTuple> S, int sigLen) {
        PatriciaTrie pt = new PatriciaTrie(sigLen);
        for(SigSimpleTuple s:S) {
            s.signature = Utils.create_sig_normal(s.setValues, sigLen);//sigLen is 4 by default
            pt.put(s);
        }

        LinkedList<PatriciaTrie.PatriciaTrieNode> queue = new LinkedList<PatriciaTrie.PatriciaTrieNode>();
        int count = 0;

        for(SigSimpleTuple r:R) {
            r.signature = Utils.create_sig_normal(r.setValues, sigLen);
            queue.clear();
            queue.add(pt.root);
            while (!queue.isEmpty()) {
                PatriciaTrie.PatriciaTrieNode node = queue.poll();
                //compare prefix
                if(PatriciaTrie.containCompare(r.signature, node.prefix, node.start, node.split-1) == true) {
                    if(node.split >= sigLen * Integer.SIZE) {//reach the end, compare
                        for(SigSimpleTuple s:node.items) {
                            if((r.setSize >= s.setSize) && (Utils.compare_set(r.setValues, s.setValues) >= 0)) {
                                count ++;
                            }
                        }
                    }else {//need to compare more
                        //get the split point of signature
                        int bit = r.signature[node.split/Integer.SIZE] & (Integer.MIN_VALUE >>> (node.split%Integer.SIZE));
                        //if(node.left == null || node.right == null) {
                        //    System.out.print("fishy");
                        //}
                        if(bit == 0) {
                            queue.add(node.left);
                        }else {
                            queue.add(node.left);
                            queue.add(node.right);
                        }
                    }
                }

            }


        }
        System.out.println("ASHJ_Patricia will return "+Integer.toString(count)+" results");
        //pt.print(pt.root);
    }


    public static void main(String[] args) {
        AdvancedJoinAlgorithms aja = new AdvancedJoinAlgorithms();
        ArrayList<SigSimpleTuple> R = new ArrayList<SigSimpleTuple>();
        SigSimpleTuple r1 = new SigSimpleTuple();
        r1.tupleID = 1;
        int[] array = {425};
        r1.setValues = array;
        r1.setSize = r1.setValues.length;

        SigSimpleTuple r2 = new SigSimpleTuple();
        r2.tupleID = 2;
        int[] array2 = {425};
        r2.setValues = array2;
        r2.setSize = r2.setValues.length;

        //r.signature = Utils.create_sig_normal(r.setValues,1);
        R.add(r1);
        R.add(r2);
        aja.ASHJ_Patricia(R,R,1);
    }

}
