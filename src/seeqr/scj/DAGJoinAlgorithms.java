package seeqr.scj;

import java.util.*;

/**
 * Created by yluo on 1/3/14.
 */
public class DAGJoinAlgorithms {
    /**
     * DAG-based long_signature hash join, using hash map for SHJ
     * The setSize of each SigSimpleTuple has the following meanings:
     *   t.setSize > 0, t belongs to R
     *   t.setSize < 0, t belongs to S
     *   t.setSize = 0, t is a duplicate
     *
     *   t.children points to children and duplicates in DAG
     * @param R
     * @param S
     * @param sig_len
     */
    public void DSHJ(ArrayList<DAGSST> R, ArrayList<DAGSST> S, int sig_len) {
        //1. merge R,S into one relation.
        ArrayList<DAGSST> oneRelation = new ArrayList<DAGSST>(R.size()+S.size());
        oneRelation.addAll(R);

        for(DAGSST s:S) {
            s.setSize = -s.setSize;//indicate the tuple is from S
            oneRelation.add(s);
        }

        //2. sort the one relation, based on (set size, tuple id)
        Collections.sort(oneRelation, new SizeIDComparator());
        //3. from lower levels to higher levels
        ArrayList[][] matrix = new ArrayList[1<<12][100];//12bit key, 100 set size
        int maxLen = Math.abs(oneRelation.get(oneRelation.size()-1).setSize);


        long startTime;
        long estimatedTime = 0;


        int count = 0;

        for(DAGSST t:oneRelation) {
            //get long_signature

            t.signature = Utils.create_sig_normal(t.setValues, sig_len);
            int row = (BitOperations.FIRSTBITS & (t.signature[0]))>>>20;
            int[] rows = Utils.getSubsets(row);//substring rows

            //System.out.print(matrix.length + " ");
            int column = Math.abs(t.setSize)-1;


            if(t.setSize > 0) {//tuple from R

            for(int i = column; i >= 0; i--) {
                for(int j:rows) {
                    //System.out.print(" "+j+","+i+" ");
                    //startTime = System.nanoTime();
                    if((matrix[j] != null) && (matrix[j][i] != null)) {
                        //System.out.print("reach here\n");

                        ArrayList<DAGSST> l = matrix[j][i];
                        //compare the ones in l with t

                        for(DAGSST d :l) {
                            if(//((d.setSize) < 0)//opposite signs//(t.setSize ^ d.setSize) < 0
                                    //&&
                                    (Utils.compare_sig_contain(t.signature, d.signature) >=0 )
                                    && (Utils.compare_set(t.setValues, d.setValues) >= 0)) {
                                count ++;
                            }
                        }

                    }
                    //estimatedTime = estimatedTime + System.nanoTime() - startTime;
                }
            }

            }
            else {//tuple from S
            //add t to the matrix
                //
            if(matrix[row] == null) {
                matrix[row] = new ArrayList[maxLen];
            }
            if(matrix[row][column] == null) {
                matrix[row][column] = new ArrayList();
            }
            matrix[row][column].add(t);
                //
            }
        }

        System.out.print(estimatedTime/(1000000.0));
        System.out.print("ms\n");

        System.out.println("DSHJ will return "+Integer.toString(count)+" results");
        R.clear();

    }

}
