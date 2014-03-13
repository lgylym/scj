package seeqr.scj;

import java.util.*;

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

        //array deque is likely to be faster than linked list implementation
        //LinkedList<PatriciaTrie.PatriciaTrieNode> queue = new LinkedList<PatriciaTrie.PatriciaTrieNode>();
        ArrayDeque<PatriciaTrie.PatriciaTrieNode> queue = new ArrayDeque<PatriciaTrie.PatriciaTrieNode>(S.size());
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


    public void PETTI_Join(ArrayList<SimpleTuple> R, ArrayList<SimpleTuple> S) {
        //put tuples from S to the prefix tree
        PETTI pt = new PETTI();
        for(SimpleTuple st:S) {
            pt.put(st);
        }
        //put tuples from R to inverted index
        HashMap<Integer,ArrayList<Integer>> invertedList = new HashMap<Integer, ArrayList<Integer>>();
        for(SimpleTuple r:R) {
            for(int element:r.setValues) {
                if(!invertedList.containsKey(element)) {
                    ArrayList<Integer> l = new ArrayList<Integer>();
                    l.add(r.tupleID);
                    invertedList.put(element,l);
                }else {
                    invertedList.get(element).add(r.tupleID);
                }
            }
        }
        int count = 0;
        for(PETTI.Node child:pt.root.map.values()) {
            count += join(child,null,invertedList);
        }
        System.out.println("PETTI_Join will return "+count+" results");
    }

    private int join(PETTI.Node node, ArrayList upList, HashMap<Integer,ArrayList<Integer>> invertedList) {
        int levelCount = 0;
        ArrayList<Integer> currList;
        if(upList == null) {
            currList = invertedList.get(node.setItem);
        }else {
            //a join of uplist and cuurList
            //currList = new ArrayList<Integer>(upList);
            //currList.retainAll(invertedList.get(node.setItem));
            currList = intersect(upList, invertedList.get(node.setItem));
        }
        if(node.tupleList != null) {
            for(Integer t:node.tupleList) {
                for(int j:currList){
                    levelCount ++;
                }
            }
        }

        for(PETTI.Node child:node.map.values()) {
            levelCount += join(child, currList,invertedList);
        }
        return levelCount;
    }

    //l1 join l2 -> l1, both are sorted
    private ArrayList<Integer> intersect(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int i = 0; int j = 0;
        int size1 = l1.size(); int size2 = l2.size();


        while(i < size1 && j < size2) {
            int e1 = l1.get(i);
            int e2 = l2.get(j);
            if(e1 < e2) {
                i++;
            }else if(e1 > e2) {
                j++;
            }else {//equal
                result.add(e1);
                i++;
                j++;
            }
        }

        return result;
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
