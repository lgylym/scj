package seeqr.scj;

import java.util.BitSet;

/**
 * Created by yluo on 12/20/13.
 */
public class BitsetSimpleTuple extends SimpleTuple {
    BitSet signature;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tuple:");
        sb.append(tupleID);
        sb.append(",set size:");
        sb.append(setSize);
        sb.append(",signature:");
        sb.append(signature.toString());

        sb.append("\nset value:");

        for(int value:setValues) {
            sb.append(value);
            sb.append(',');
        }
        sb.append('\n');
        return sb.toString();
    }


}
