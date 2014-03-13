package seeqr.scj;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yluo on 3/13/14.
 * PETTI is from paper "Using Prefix-Trees for Efficiently Computing Set Joins"
 * PETTI is a prefix tree
 * http://dx.doi.org/10.1007/11408079_69
 */
public class PETTI {
    public class Node {
        int setItem;
        HashMap<Integer,Node> map;
        ArrayList<Integer> tupleList;//only contain tuple ids

        public Node(int setValue) {
            setItem = setValue;
            map = new HashMap<Integer, Node>();
        }

        public String toString() {
            String result = "item:" + Integer.toString(setItem)+"; children:";
            for(Integer i:map.keySet()) {
                result += i + ",";
            }
            if(tupleList != null) {
                result += "; tuples:";
                for(Integer st:tupleList) {
                    result += st+",";
                }
            }
            result += "\n";
            return result;
        }

    }

    Node root;

    public PETTI() {
        root = new Node(-1);//-1 is the root
    }


    public void put(SimpleTuple t) {
        insert(root,t,0);
    }

    /**
     * insert some set element to the children of node
     * @param node
     * @param t
     * @param index
     */
    private void insert(Node node, SimpleTuple t, int index) {
        if(index >= t.setSize) {//reach the end
            if(node.tupleList == null) {
                node.tupleList = new ArrayList<Integer>();
            }
            node.tupleList.add(t.tupleID);
            return;
        }

        int element = t.setValues[index];
        if(node.map.containsKey(element)) {
           insert(node.map.get(element),t,index+1);
        }else {
            Node child = new Node(element);
            node.map.put(element,child);
            insert(child,t,index+1);
        }
    }

    public void print(Node node) {
        System.out.print(node);
        for(Integer i : node.map.keySet()) {
            print(node.map.get(i));
        }
    }

    public static void main(String[] args) {
        int[] array = {1,3,4};
        int[] array2 = {2,3,4};
        SimpleTuple st = new SimpleTuple();
        st.setValues = array;
        st.setSize = array.length;
        PETTI p = new PETTI();
        //for(int i:array) {
        p.put(st);

        SimpleTuple st2 = new SimpleTuple();
        st2.setValues = array2;
        st2.setSize = array2.length;
        p.put(st2);
        //}
        p.print(p.root);
    }

}
