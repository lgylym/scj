package seeqr.scj;

/**
 * SimpleTuple with signature added
 * Created by yluo on 12/19/13.
 */
public class SigSimpleTuple extends SimpleTuple {
    protected int[] signature;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tuple:");
        sb.append(tupleID);
        sb.append(",set size:");
        sb.append(setSize);
        sb.append(",signature:");
        for(int sig:signature) {
            sb.append(Integer.toBinaryString(sig));
            sb.append(" ");
        }
        sb.append("\nset value:");

        for(int value:setValues) {
            sb.append(value);
            sb.append(',');
        }
        sb.append('\n');
        return sb.toString();
    }
}
