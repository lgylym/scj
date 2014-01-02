package seeqr.scj;

/**
 * Created by yluo on 1/2/14.
 */
public class JustTest {
    public static void main(String[] args) {
        int mask = 0;
        int output = 1;
        if((mask & 1) == 1) {
            System.out.print("1 ");
        }
        while(output != 0) {
            output = mask & (output - mask);
            System.out.print(Integer.toBinaryString(output) + " ");
        }
    }
}
