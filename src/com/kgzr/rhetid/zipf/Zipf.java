package com.kgzr.rhetid.zipf;

import com.kgzr.rhetid.util.StopWords;
import com.kgzr.rhetid.zipf.adt.NumPair;

import java.util.*;

/**
 * Created by Kevin and Zach on 11/30/14.
 * This implementation of Zipf takes one word at a time.
 *
 */
public class Zipf {
    public String project;
    private static final int NUM_RETURN=20;
    private int wordCount = 0;
    private Map<String,NumPair> words = new HashMap<String, NumPair>();

    public void putWord(String word) {
        if (this.words.containsKey(word)) {
            this.words.get(word).increment();
        } else
            this.words.put(word, new NumPair(word));
        this.wordCount++;
    }

    /**
     * Push all the Zipf elements into a PQ to get the max values.
     * This can be used with a stop-words dict to find most freq non-stop words.
     * //TODO: Untested.
     * @return
     */
    public List<NumPair> getMax(boolean stopWords){
        PriorityQueue<NumPair> maxNumPQ = new PriorityQueue<NumPair>(NUM_RETURN, new NumPair.NumComparator());
        List<NumPair> maxWords = new ArrayList<NumPair>();
        for (Map.Entry<String,NumPair> word : words.entrySet()){
            NumPair wordInfo = word.getValue();
            wordInfo.setFrequency(this.wordCount);
            maxNumPQ.add(wordInfo);
        }
        System.out.println("Total Words: " + this.wordCount);
        while(maxWords.size() < NUM_RETURN && !maxNumPQ.isEmpty()) {
            NumPair maxVal = maxNumPQ.poll();
            if (stopWords) {
                if (!StopWords.check(maxVal.getWord())) {
                    maxWords.add(maxVal);
                }
            }
            else {
                maxWords.add(maxVal);
            }
            System.out.println(maxVal.toString());
        }
        return maxWords;
    }

}
