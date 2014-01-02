package seeqr.scj;

/**
 * Created by yluo on 12/24/13.
 */
public class SignatureEnumerator {
    long mask[];
    long progress[];
    int sig_len;

    public SignatureEnumerator(long signature[]) {
        mask = signature;
        sig_len = signature.length;
        progress = new long[sig_len];
        for(int i = 0; i < sig_len; i++) {
            progress[i] = 1; // initial state
        }
        //number of substrings to check
    }


    public long[] getNextSubstring() {

        return progress;
    }

    /**
     * return whether the progress is all zeros
     * @return
     */
    private boolean isEmpty() {
        for(int i = 0; i < sig_len; i++) {

        }
        return  false;
    }


}
