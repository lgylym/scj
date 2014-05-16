package seeqr.scj;

import com.google.common.collect.ArrayListMultimap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yluo on 3/7/14.
 */
public class PatriciaTrie {
    public static int[] lmasks = new int[Integer.SIZE];//masks for an integer
    public static int[] rmasks = new int[Integer.SIZE];

    public int nodeCount;
    public ArrayListMultimap<Integer,Integer> represent;//use mapping to maintain duplicate tuples,


    private int prefixLen = 4;//should be the same as the signature size, 4 by default

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
            StringBuffer result = new StringBuffer();
            result.append("start:" + start + ",split:" + split+" signature:" + BitOperations.toStringBitStream(prefix));
            if(items != null) {
                for(SigSimpleTuple s:items){
                    result.append(" " + s.toString());
                }
            }
            return result.toString();
        }
    }

    public int getMaxHeight() {
        return getMaxHeight(root);
    }

    public int getMinHeight() {
        return getMinHeight(root);
    }


    public void dfsHeight() {
        dfsHeight(root,0);
    }

    private void dfsHeight(PatriciaTrieNode node, int currentHeight) {
        if(node == null) {//reach the leaf
            CommandRun.trie_height.addValue(currentHeight);
        }
        else {
            dfsHeight(node.left, currentHeight + 1);
            dfsHeight(node.right, currentHeight + 1);
        }
    }


    private int getMinHeight(PatriciaTrieNode node) {
        if(node == null) {
            return 0;
        }
        return 1 + Math.min(getMinHeight(node.left), getMinHeight(node.right));
    }

    private int getMaxHeight(PatriciaTrieNode node) {
        if(node == null) {
            return 0;
        }
        return 1 + Math.max(getMaxHeight(node.left),getMaxHeight(node.right));
    }

    public PatriciaTrie(int sigLen) {
        prefixLen = sigLen;
        nodeCount = 1;
        represent = ArrayListMultimap.create();
        //initially, we store an empty item in the trie, in the root
        //note that here we set the empty signature to be all 1, so that it can (almost) always be filtered
        root = null;
        lmasks[0] = -1;//1111...1111
        rmasks[Integer.SIZE-1] = -1;//1111...1111
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


    public void print() {
        print(root);
    }

    private void print(PatriciaTrieNode node) {
        System.out.println(node);
        if(node.left != null) {
            print(node.left);
        }
        if(node.right != null) {
            print(node.right);
        }
    }

    //put a tuple into the trie, call insert
    public void put(SigSimpleTuple tuple) {
        if(root == null) {
            root = new PatriciaTrieNode();
            root.start = 0;
            root.split = prefixLen * Integer.SIZE; //out of the array do we split, means the leaf node
            root.prefix = new int[prefixLen];
            for(int i = 0; i < prefixLen; i++) {
                root.prefix[i] = tuple.signature[i];
            }
            root.items = new ArrayList<>();
            root.items.add(tuple);
        }else {
            root = insert(root, tuple, 0);
        }
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
            if(node.split >= Integer.SIZE * prefixLen) {//out of boundary
                //directly insert
                if(node.items == null) {
                    node.items = new ArrayList<SigSimpleTuple>();
                }

                //we consider duplicate situations here
                boolean needInsert = true;
                for(SigSimpleTuple s:node.items) {
                    if(Utils.compare_set(s.setValues, tuple.setValues) == 0) {
                        represent.put(s.tupleID,tuple.tupleID);
                        needInsert = false;
                        break;
                    }
                }
                if(needInsert) {
                    node.items.add(tuple);
                }

            }else {//keep inserting
                //get the bit at the split point
                int bit = tuple.signature[node.split/Integer.SIZE] & (Integer.MIN_VALUE >>> (node.split%Integer.SIZE));
                if(bit == 0) {//left branch
                    node.left = insert(node.left,tuple,node.split);
                }else {//right branch
                    node.right = insert(node.right,tuple,node.split);
                }
            }
            return node;
        }else {//should split at index
            //
            nodeCount += 2;
            PatriciaTrieNode newParent = new PatriciaTrieNode(node.prefix);
            PatriciaTrieNode newChild = new PatriciaTrieNode(tuple.signature);
            newChild.items = new ArrayList<SigSimpleTuple>();
            newChild.items.add(tuple);
            newChild.split = prefixLen * Integer.SIZE;
            newChild.start = index;

            node.start = index;

            newParent.start = start;
            newParent.split = index;

            int bit = tuple.signature[index/Integer.SIZE] & (Integer.MIN_VALUE >>>  (index%Integer.SIZE));
            //System.out.print(Integer.toBinaryString((Integer.MIN_VALUE >>>  (index%Integer.SIZE))));

            if(bit == 0) {//tuple on the left
                newParent.left = newChild;
                newParent.right = node;
            }else {
                newParent.left = node;
                newParent.right = newChild;
            }
            return newParent;
        }
    }


    /**
     * Put the list entry length in statistics
     */
    public void getListLength() {
        ArrayDeque<PatriciaTrieNode> queue = new ArrayDeque();
        queue.add(root);
        while(!queue.isEmpty()) {
            PatriciaTrieNode node = queue.poll();
            if(node.left != null && node.right != null) {
                queue.add(node.left);
                queue.add(node.right);
            }else {
                CommandRun.entry_len.addValue(node.items.size());
            }
        }
    }

    /**
     * search for some signature in the trie. return the leaf node if possible
     * @param node
     * @param signature
     * @param start
     * @return
     */
    public PatriciaTrieNode search(PatriciaTrieNode node, int[] signature, int start) {
        int index = equalCompare(node.prefix,signature,start,node.split-1);
        if(index == -1) {
            if(node.split >= Integer.SIZE * prefixLen) {//out of boundary
                return node;
            }else {//keep inserting
                //get the bit at the split point
                int bit = signature[node.split/Integer.SIZE] & (Integer.MIN_VALUE >>> (node.split%Integer.SIZE));
                if(bit == 0) {//left branch
                    return search(node.left,signature,node.split);
                }else {//right branch
                    return search(node.right,signature,node.split);
                }
            }
        }
        return null;
    }

    /**
     * compare array1 and array2 from start to end (both inclusive)
     * return -1 if the two are the same
     * return some positive number (index) if the prefix break at some point
     * @param array1
     * @param array2
     * @return
     */
    public static int equalCompare(int[] array1, int[] array2, int start, int end) {
        if(start > end) {//compare two empty strings
            return -1;
        }

        int starti = start / Integer.SIZE; //i is the index of the array
        int startj = start % Integer.SIZE; //j is the index in an element
        int endi = end / Integer.SIZE;
        int endj = end % Integer.SIZE;

        //System.out.print(startj);
        //System.out.print(endj);

        if(starti < endi) {
            //starti
            int mask = lmasks[startj];
            int result =(array1[starti] & mask) ^ (array2[starti] & mask);
            if(result != 0) {
                return starti*Integer.SIZE + Integer.numberOfLeadingZeros(result);
            }
            //starti+1 to endi-1
            for(int i = starti + 1; i <= endi - 1; i++) {
                result = array1[i] ^ array2[i];
                if(result != 0) {
                    return i*Integer.SIZE + Integer.numberOfLeadingZeros(result);
                }
            }
            //endi
            mask = rmasks[endj];
            result = (array1[endi] & mask) ^ (array2[endi] & mask);
            if(result != 0) {
                return endi*Integer.SIZE + Integer.numberOfLeadingZeros(result);
            }
        }else if(starti == endi) {
            //create masks
            int mask = lmasks[startj] & rmasks[endj];
            int result = (array1[starti] & mask) ^ (array2[starti]&mask);
            if(result == 0) {
                return -1;
            }else {
                return starti*Integer.SIZE + Integer.numberOfLeadingZeros(result);
            }
        }else {
            System.out.print("Something is wrong in equalCompare");
        }
        return -1;
    }

    /**
     * check weather array1 contains array2 in the certain range [start,end] (inclusive)
     * return false if there is no containment, return true if there is containment
     * @param array1
     * @param array2
     * @param start
     * @param end
     * @return
     */
    public static boolean containCompare(int[] array1, int[] array2, int start, int end) {
        if(start > end) {//two empty strings
            return true;
        }
        int starti = start / Integer.SIZE; //i is the index of the array
        int startj = start % Integer.SIZE; //j is the index in an element
        int endi = end / Integer.SIZE;
        int endj = end % Integer.SIZE;

        if(starti < endi) {
            //starti
            int mask = lmasks[startj];
            if((~(array1[starti]&mask) & (array2[starti]&mask)) != 0) {
                return false;
            }
            //starti+1 to endi-1
            for(int i = starti+1; i < endi-1; i++) {
                if((~array1[i] & array2[i]) != 0) {
                    return false;
                }
            }
            //endi
            mask = rmasks[endj];
            if((~(array1[endi]&mask) & (array2[endi]&mask)) != 0) {
                return false;
            }
            return true;

        }else if(starti == endi) {
            int mask = lmasks[startj] & rmasks[endj];
            if((~(array1[starti]&mask) & (array2[starti]&mask)) != 0) {
                return false;
            }else {
                return true;
            }
        }else {
            System.out.print("Something is wrong in containCompare");
            return false;
        }
    }


    public static void main(String[] args) {
        PatriciaTrie pt = new PatriciaTrie(4);
        int[] array1 = {0x11,0x21,0,0};
        int[] array2 = {0x11,0x21,0,0};
        SigSimpleTuple sst1 = new SigSimpleTuple();
        sst1.signature = array1;
        pt.put(sst1);
        pt.print(pt.root);System.out.print('\n');
        SigSimpleTuple sst2 = new SigSimpleTuple();
        sst2.signature = array2;
        pt.put(sst2);
        pt.print(pt.root);

        System.out.print("\n"+pt.search(pt.root,array1,0).items);
        //System.out.println(containCompare(array1,array2,24,31));

    }
}
