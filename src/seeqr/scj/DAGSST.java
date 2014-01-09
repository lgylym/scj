package seeqr.scj;

import java.util.Comparator;

/**
 * DAG-based long_signature simple tuple
 * Created by yluo on 1/3/14.
 */
public class DAGSST extends SigSimpleTuple {
    protected int[] children;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tuple:");
        sb.append(tupleID);
        sb.append(",set size:");
        sb.append(setSize);

        if(signature != null){
            sb.append(",long_signature:");
            sb.append(BitOperations.toStringBitStream(signature));
        }
        sb.append("\nset value:");
        for(int value:setValues) {
            sb.append(value);
            sb.append(',');
        }

        if(children != null) {
            sb.append("\nchildren:");
            for(int child:children) {
                sb.append(child);
                sb.append(',');
            }
        }
        sb.append('\n');
        return sb.toString();
    }
}

class SizeIDComparator implements Comparator<DAGSST> {

    @Override
    public int compare(DAGSST o1, DAGSST o2) {
        //sort on |setSize|,tupleID
        //get absolute values of o1.setSize and o2.setSize
        //http://lab.polygonal.de/?p=81

        int size1 = (o1.setSize ^ (o1.setSize >> 31)) - (o1.setSize >> 31);
        int size2 = (o2.setSize ^ (o2.setSize >> 31)) - (o2.setSize >> 31);

        if(size1 != size2) {
            return size1 > size2 ? 1 : -1;
        }else {
            return (o1.setSize < o2.setSize ? -1 : (o1.setSize==o2.setSize? 0 : 1));
            //return (o1.tupleID<o2.tupleID ? -1 : (o1.tupleID==o2.tupleID ? 0 : 1));
        }
    }
}
