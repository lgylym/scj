package seeqr.scj;

import java.math.BigInteger;

/**
 * Created by yluo on 12/20/13.
 */
public class BigintSimpleTuple extends SimpleTuple {
    BigInteger signature;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tuple:");
        sb.append(tupleID);
        sb.append(",set size:");
        sb.append(setSize);
        sb.append(",signature:");
        sb.append(Integer.toBinaryString(signature.intValue()));

        sb.append("\nset value:");

        for(int value:setValues) {
            sb.append(value);
            sb.append(',');
        }
        sb.append('\n');
        return sb.toString();
    }


}
