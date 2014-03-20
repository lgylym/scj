package seeqr.scj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Load relations from disk to memory
 * Created by yluo on 3/20/2014.
 */
public class RelationLoader {

    /**
     * load some relation from file to memory, generic type
     * @param filePath
     * @param cls
     * @param <T>
     * @return
     */
    public static <T extends SimpleTuple> ArrayList<T> loadRelation(String filePath, Class<T> cls) {
        ArrayList<T> result = new ArrayList<T>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            String[] words;
            while((line = reader.readLine()) != null) {
                if(line.startsWith("##")) {
                    continue;
                }
                words = line.trim().split(" ");
                result.add(getTuple(words,cls));
            }
            return result;
        }catch (Exception e) {

        }
        return null;
    }



    public static <T extends SimpleTuple> T getTuple(String[] words, Class<T> cls) {
        try{
            T tuple = cls.newInstance();
            tuple.tupleID = Integer.valueOf(words[0]);
            tuple.setSize = words.length - 1;
            tuple.setValues = new int[tuple.setSize];
            for(int i = 0; i < tuple.setSize; i++) {
                tuple.setValues[i] = Integer.valueOf(words[i+1]);
            }
            return tuple;
        }catch (Exception e) {
            System.out.print(e);
        }
        return null;
    }
}
