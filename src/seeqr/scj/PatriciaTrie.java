package seeqr.scj;

import java.util.ArrayList;
import java.util.BitSet;

/**
 * Created by yluo on 3/7/14.
 */
public class PatriciaTrie {
    public static int[] lmasks = new int[Integer.SIZE];//masks for an integer
    public static int[] rmasks = new int[Integer.SIZE];



    private final int prefix_len = 4;

    protected PatriciaTrieNode root;

    public class PatriciaTrieNode{
        PatriciaTrieNode left;
        PatriciaTrieNode right;
        int start; //where the prefix begins
        int split; //at which index the node branch, the prefix ends at split-1
        int[] prefix; //prefix from the last split point to this one
        ArrayList<SigSimpleTuple> items;//a list of items that are attached to this node

        public PatriciaTrieNode() {}

        public PatriciaTrieNode(int[] signature) {
            prefix = new int[signature.length];
            for(int i = 0; i < signature.length; i++) {
                prefix[i] = signature[i];
            }
        }

        @Override
        public String toString() {
            String result = "start:" + start + ",split:" + split+"  ";
            return result;
        }
    }


    public PatriciaTrie() {
        //initially, we store an empty item in the trie, in the root
        root = new PatriciaTrieNode();
        root.start = 0;
        root.split = prefix_len * Integer.SIZE; //out of the array does we split
        root.prefix = new int[prefix_len];
        for(int i = 0; i < prefix_len; i++) {
            root.prefix[i] = 0;
        }

        lmasks[0] = -1;
        rmasks[Integer.SIZE-1] = -1;
        for(int i = 1; i < lmasks.length; i++) {
            lmasks[i] = lmasks[i-1]>>>1;

        }

        for(int i = rmasks.length-2; i >= 0; i--) {
            rmasks[i] = rmasks[i+1]<<1;
        }

        /*
        lmask
        1111
        0111
        0011
        0001

        rmask
        1000
        1100
        1110
        1111


        for(int i = 0; i < lmasks.length; i++) {
            System.out.println(Integer.toBinaryString(rmasks[i]));
        }
        */
    }


    public void print(PatriciaTrieNode node) {
        System.out.print(node);
        if(node.left != null) print(node.left);
        if(node.right != null) print(node.right);
    }

    //put a tuple into the trie, call insert
    public void put(SigSimpleTuple tuple) {
        root = insert(root, tuple, 0);
    }

    /**
     * insert tuple to the subtree using node as root,
     * return some node as the root, old node or a new root
     * @param node
     * @param tuple
     * @param start
     * @return
     */
    private PatriciaTrieNode insert(PatriciaTrieNode node, SigSimpleTuple tuple, int start) {
        //get the prefixes
        int index = equalCompare(node.prefix,tuple.signature,start,node.split-1);
        if(index == -1) {//equal
            if(node.split > 127) {//out of boundary
                //directly insert
                if(node.items == null) {
                    node.items = new ArrayList<SigSimpleTuple>();
                }
                node.items.add(tuple);
            }else {//keep inserting
                //get the bit at the split point
                int bit = tuple.signature[node.split/Integer.SIZE] & (1<<(node.split%Integer.SIZE));
                if(bit == 0) {//left branch
                    node.left = insert(node.left,tuple,node.split);
                }else {//right branch
                    node.right = insert(node.right,tuple,node.split);
                }
            }
            return node;
        }else {//should split at index
            PatriciaTrieNode node1 = new PatriciaTrieNode(node.prefix);
            PatriciaTrieNode node2 = new PatriciaTrieNode(tuple.signature);
            node2.items = new ArrayList<SigSimpleTuple>();
            node2.items.add(tuple);
            node2.split = prefix_len * Integer.SIZE;
            node2.start = index;

            node.start = index;

            node1.start = start;
            node1.split = index;

            int bit = tuple.signature[index/Integer.SIZE] & (1 << (index%Integer.SIZE));
            if(bit == 0) {//tuple on the left
                node1.left = node2;
                node1.right = node;
            }else {
                node1.left = node;
                node1.right = node2;
            }
            return node1;
        }
    }


    /**
     * compare array1 and array2 from start to end (inclusive)
     * return -1 if the two are the same
     * return some positive number (index) if the prefix break at some point
     * @param array1
     * @param array2
     * @return
     */
    public static int equalCompare(int[] array1, int[] array2, int start, int end) {
        int starti = start / Integer.SIZE; //i is the index of the array
        int startj = start % Integer.SIZE; //j is the index in a number
        int endi = end / Integer.SIZE;
        int endj = end % Integer.SIZE;

        //System.out.print(startj);
        //System.out.print(endj);

        if(starti < endi) {
            //starti
            int mask = lmasks[startj];
            int result =(array1[starti] & mask) ^ (array2[starti]&mask);
            int index = Integer.numberOfLeadingZeros(result);
            if(index != Integer.SIZE) {
                return starti*Integer.SIZE + index;
            }
            //starti+1 to endi-1
            for(int i = starti + 1; i < endi - 1; i++) {
                result = array1[i] ^ array2[i];
                index = Integer.numberOfLeadingZeros(result);
                if(index != Integer.SIZE) {
                    return i*Integer.SIZE + index;
                }
            }
            //endi
            mask = rmasks[endj];
            result = (array1[endi] & mask) ^ (array2[endi]&mask);
            index = Integer.numberOfLeadingZeros(result);
            if(index != Integer.SIZE) {
                return endi*Integer.SIZE + index;
            }
        }else if(starti == endi) {
            //create masks
            int mask = lmasks[startj] & rmasks[endj];
            int result = (array1[starti] & mask) ^ (array2[starti]&mask);
            //System.out.print(result);
            int index = Integer.numberOfLeadingZeros(result);
            if(index != Integer.SIZE) {
                return starti*Integer.SIZE + index;
            }else {
                return -1;
            }
        }else {
            System.out.print("Something is wrong in equalCompare");
        }
        return -1;
    }


    public static void main(String[] args) {
        PatriciaTrie pt = new PatriciaTrie();
        int[] array1 = {0x11,0x21,0,0};
        int[] array2 = {0x10,0x21,0,0};
        SigSimpleTuple sst1 = new SigSimpleTuple();
        sst1.signature = array1;
        pt.put(sst1);
        pt.print(pt.root);System.out.print('\n');
        SigSimpleTuple sst2 = new SigSimpleTuple();
        sst2.signature = array2;
        pt.put(sst2);
        pt.print(pt.root);

        //System.out.println(equalCompare(array1,array2,0,127));

    }
}
