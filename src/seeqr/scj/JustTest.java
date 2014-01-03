package seeqr.scj;

import java.util.Random;

/**
 * Created by yluo on 1/2/14.
 */
public class JustTest {
    public static void main(String[] args) {
        int threshold = 20000000;
        //int j = 0;
        int[] array = new int[threshold];
        int[] b = new int[threshold];
        Random random = new Random();
        for(int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }
        long startTime;
        long estimatedTime;


//        for(int j = 0; j < 100; j++){
//        startTime = System.nanoTime();
//        int c;
//        for(int i = 0; i < threshold; i++) {
//            c = array[i];
//            b[i] = c < 0 ? -c : c;
//        }
//        estimatedTime = System.nanoTime() - startTime;
//        System.out.print(estimatedTime/(1000000.0));
//        System.out.print("ms\n");
//        }

        for(int j = 0; j < 100; j++){
        startTime = System.nanoTime();
        for(int i = 0; i < threshold; i++) {
            b[i] = (array[i] ^ (array[i] >> 31)) - (array[i] >> 31);
            //b[i] = Math.abs(array[i]);
        }
        estimatedTime = System.nanoTime() - startTime;
        System.out.print(estimatedTime/(1000000.0));
        System.out.print("ms\n");
        }


//        for(int j = 0; j < 100; j++){
//        startTime = System.nanoTime();
//        for(int i = 0; i < threshold; i++) {
//            b[i] = array[i] < 0 ? -array[i] : array[i];
//        }
//        estimatedTime = System.nanoTime() - startTime;
//        System.out.print(estimatedTime/(1000000.0));
//        System.out.print("ms\n");
//        }


        //System.out.println(Integer.toString(j));


    }
}
