package seeqr.scj;

import com.googlecode.javaewah.EWAHCompressedBitmap;

import java.util.BitSet;

/**
 * Created by yluo on 1/7/14.
 */
public class BitmapSimpleTuple extends SigSimpleTuple {
    protected EWAHCompressedBitmap long_signature;

    //protected BitSet long_signature;
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tuple:");
        sb.append(tupleID);
        sb.append(",set size:");
        sb.append(setSize);
        sb.append(",long_signature:");

        if(long_signature != null)
        {sb.append(long_signature.toString());}

        sb.append("\nset value:");

        for(int value:setValues) {
            sb.append(value);
            sb.append(',');
        }
        sb.append('\n');
        return sb.toString();
    }

}
