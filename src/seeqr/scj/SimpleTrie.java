package seeqr.scj;

import java.util.*;

/**
 * Created by yluo on 2/19/14.
 */



public class SimpleTrie {

    protected SimpleTrieNode root;

    public class SimpleTrieNode {
        SimpleTrieNode left;//0
        SimpleTrieNode right;//1

        public SimpleTrieNode() {

        }

    }

    public class SimpleTrieLeaf extends SimpleTrieNode {
        int sigPart;//should be part of the signature, 32 bit is quite enough, 4 billion

        public SimpleTrieLeaf() {

        }
    }

    public SimpleTrie() {
        root = new SimpleTrieNode();
    }

    /**
     * put part of the signature into the trie
     * @param sigPart part of the signature, start from the head
     * @param sigPartLen the length to be used
     */
    public void put(int sigPart, int sigPartLen) {
        SimpleTrieNode currentNode = root;

        //iterate over bits of sigPart
        for(int i = Integer.SIZE-1; i >= Integer.SIZE - sigPartLen+1; i--) {
            int currentBit = (sigPart>>i)&1;

            if(currentBit == 1) {
                if(currentNode.right == null) {
                    currentNode.right = new SimpleTrieNode();
                }
                currentNode = currentNode.right;
            }else {//currentBit == 0
                if(currentNode.left == null) {
                    currentNode.left = new SimpleTrieNode();
                }
                currentNode = currentNode.left;
            }
        }
        //deal with the leaf, last bit
        int lastBit = (sigPart>>(Integer.SIZE - sigPartLen))&1;
        if(lastBit == 1) {
            if(currentNode.right == null) {
                currentNode.right = new SimpleTrieLeaf();
            }
            currentNode = currentNode.right;
        }else {
            if(currentNode.left == null) {
                currentNode.left = new SimpleTrieLeaf();
            }
            currentNode = currentNode.left;
        }
        ((SimpleTrieLeaf)currentNode).sigPart = sigPart;
    }

    /**
     * print the trie in a bfs manner
     * basically for testing purpose
     */
    public void printBFS() {
        LinkedList<SimpleTrieNode> queue = new LinkedList<SimpleTrieNode>();
        queue.add(root);
        while(!queue.isEmpty()) {
            SimpleTrieNode curr =  queue.poll();
            if(curr.left != null) {
                System.out.print(0+" ");
                queue.add(curr.left);
            }

            if(curr.right != null) {
                System.out.print(1+" ");
                queue.add(curr.right);
            }

            if(curr.left == null && curr.right == null) {
                System.out.print("**"+Integer.toBinaryString(((SimpleTrieLeaf)curr).sigPart)+"**");
            }
        }
    }


    /**
     * get subsets of a given sigPart, that exist in the trie
     * @param sigPart
     * @param sigPartLen
     * @return
     */
    public ArrayList<Integer> getSubsets(int sigPart, int sigPartLen) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        LinkedList<SimpleTrieNode> next = new LinkedList<SimpleTrieNode>();
        LinkedList<SimpleTrieNode> curr = new LinkedList<SimpleTrieNode>();
        LinkedList<SimpleTrieNode> temp;

        next.add(root);

        for(int i = Integer.SIZE-1; i >= Integer.SIZE - sigPartLen; i--) {
            temp = curr;
            curr = next;
            next = temp;
            next.clear();
            int currentBit = (sigPart>>i)&1;

            //System.out.println(currentBit);

            if(currentBit == 1) {
                for(SimpleTrieNode node:curr) {
                    if(node.left != null) next.add(node.left);
                    if(node.right != null) next.add(node.right);
                }
            }else {//currentBit == 0
                for(SimpleTrieNode node:curr) {
                    if(node.left != null) next.add(node.left);
                }
            }
        }

        for(SimpleTrieNode node:next) {
            result.add(((SimpleTrieLeaf)node).sigPart);
        }
        return result;
    }

    public static void main(String[] args) {
        SimpleTrie st = new SimpleTrie();
        st.put(0x90000000,4);
        st.put(0xA0000000,4);
        for(int i:st.getSubsets(0xE0000000,4)) {
            System.out.println(Integer.toBinaryString(i));
        }
    }

}
