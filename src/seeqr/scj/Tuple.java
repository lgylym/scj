package seeqr.scj;

/**
 * Created by yluo on 12/19/13.
 */
public class Tuple {
    protected int tupleID;
    protected int setSize;
    protected int[] setValues;

    public Tuple() {}

    public Tuple(int tuple_id, int set_size) {
        this.tupleID = tuple_id;
        this.setSize = set_size;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("tuple:");
        sb.append(tupleID);
        sb.append(",set size:");
        sb.append(setSize);
        sb.append(" ");
        sb.append("\nset value:");

        for(int value:setValues) {
            sb.append(value);
            sb.append(',');
        }
        sb.append('\n');
        return sb.toString();
    }
 }
