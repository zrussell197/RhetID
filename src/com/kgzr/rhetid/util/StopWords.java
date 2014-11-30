package com.kgzr.rhetid.util;

import com.kgzr.rhetid.RhetIdApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by Kevin and Zach on 11/30/14.
 * Statically instantiated HashSet for quick lookup of stopwords
 */
public class StopWords {

    private static Set<String> stopWords = new HashSet<String>();
    private static final String STOPWORDS_LOC = "UtilFiles/StopWords.txt";

    public static void loadStopWords(){
        try {
            Scanner scanner = new Scanner(new File(STOPWORDS_LOC));
            while (scanner.hasNext()) {
                stopWords.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a word is contained in the stop words.
     * @param word Word to be checked
     * @return whether or not word is contained
     */
    public static boolean check(String word){
        return stopWords.contains(word);
    }

    public static Set<String> getStopWords(){
        return stopWords;
    }

}
