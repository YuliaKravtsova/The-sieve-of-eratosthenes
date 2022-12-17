package main.java.resheto;

import java.io.*;

public class Keeper {
    static final File myFile = new File("algo.txt");
    public synchronized void write (String word){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(myFile));
            writer.write(word);
            writer.flush();
            writer.close();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
