package seeqr.scj;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yluo on 5/15/2014.
 * PRETTIPLUS is an advanced version of PRETTI, i.e., Patricia trie version
 */
public class PRETTIPLUS {
    public class Node {
        int[] prefix;
        HashMap<Integer,Node> map;
        ArrayList<Integer> tupleList;//only contain tuple ids

        public Node(int[] prefix) {
            this.prefix = prefix;
            map = new HashMap<>();
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("prefix:\t");
            for(int i : prefix) {
                sb.append(i+" ");
            }
            sb.append("\n");
            if(tupleList != null) {
                sb.append("tuples:\t");
                for(int tid:tupleList) {
                    sb.append(tid+" ");
                }
            }
            return sb.toString();
        }
    }

    public Node root;

    public PRETTIPLUS() {
        int[] emptySet = {};
        root = new Node(emptySet);
    }

    public void put(SimpleTuple t) {
        insert(root, t, 0);
    }

    //there is some tedious null check
    public static void addTupleID(Node node, int tupleID) {
        if(node.tupleList == null) {
            node.tupleList = new ArrayList<>();
        }
        node.tupleList.add(tupleID);
    }

    public static int[] getArrayCopyFrom(int[] array, int from) {
        int[] new_array = new int[array.length - from];
        for(int i = 0; i < new_array.length; i++) {
            new_array[i] = array[i+from];
        }
        return new_array;
    }

    //to is exclusive
    public static int[] getArrayFromTo(int[] array, int from, int to) {
        int[] new_array = new int[to - from];
        for(int i = 0; i < new_array.length; i++) {
            new_array[i] = array[i+from];
        }
        return new_array;
    }


    private Node insert(Node node, SimpleTuple t, int from) {
        //1. get common prefix
        //int[] cprefix = {};//common prefix len
        //|cp| <= node.prefix, <= t[from:]
        int clen = getCommonPrefixLen(node.prefix,0,t.setValues,from);
        int nlen = node.prefix.length;
        int tlen = t.setValues.length - from;
        if(clen == nlen) {
            if(clen == tlen) {
                addTupleID(node, t.tupleID);

            }else {//clen < tlen, add t below node
                int element = t.setValues[from+clen];
                if(node.map.containsKey(element)) {//insert recursively
                    Node branch = node.map.get(element);
                    branch = insert(branch, t, from+clen);
                    node.map.put(element, branch);
                }else {//insert as a leaf
                    Node newNode = new Node(getArrayCopyFrom(t.setValues,from+clen));
                    addTupleID(newNode, t.tupleID);
                    node.map.put(element, newNode);
                }
            }
            return node;
        }else {//clen < nlen
            if (clen == tlen) {//insert into the middle
                Node newNode = new Node(getArrayCopyFrom(t.setValues, from));
                addTupleID(newNode, t.tupleID);
                int element = node.prefix[clen];
                newNode.map.put(element, node);
                node.prefix = getArrayCopyFrom(node.prefix, clen);
                return newNode;
            } else {//clen < tlen
                Node newParent = new Node(getArrayFromTo(node.prefix,0,clen));
                Node newChild = new Node(getArrayCopyFrom(t.setValues,from+clen));
                addTupleID(newChild, t.tupleID);
                node.prefix = getArrayCopyFrom(node.prefix,clen);
                newParent.map.put(newChild.prefix[0], newChild);
                newParent.map.put(node.prefix[0],node);
                return newParent;
            }
        }
    }

    public int getCommonPrefixLen(int[] array1, int from1, int[] array2, int from2) {
        int i = 0;
        while((i+from1) < array1.length && (i+from2) < array2.length) {
            if(array1[i+from1] != array2[i+from2]) {
                break;
            }else {
                i++;
            }
        }
        return i;
    }

    public void print() {
        print(root);
    }

    public void delete() {
        delete(root);
    }

    private void delete(Node n) {
        for(Node child:n.map.values()) {
            delete(child);
        }
        n.map = null;
        n.prefix = null;
        n.tupleList = null;
    }

    private void print(Node n) {
        System.out.println(n);
        for(Node child:n.map.values()) {
            print(child);
        }
    }

    public static void main(String[] args) {
        PRETTIPLUS pp = new PRETTIPLUS();
        pp.put(new SimpleTuple(1, new int[]{1,2,3,4}));
        pp.put(new SimpleTuple(2, new int[]{1,2,3}));
        pp.put(new SimpleTuple(3, new int[]{1,2,4}));
        pp.put(new SimpleTuple(4, new int[]{5,6,7}));
        pp.print();
    }

}
