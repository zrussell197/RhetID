package com.kgzr.rhetid.zipf.adt;

import java.util.Comparator;

/**
 * Created by GleasonK on 10/4/14.
 */
public class NumPair {
    private String word;
    private int count;
    private double frequency;

    public NumPair(String word){
        this.word=word;
        this.count=1;
    }

    public void increment(){
        this.count++;
    }

    public void setFrequency(int wordCount){
        this.frequency= this.count / (double) wordCount * 100.0;
    }

    public int getCount(){ return this.count; }

    public double getFrequency(){ return this.frequency; }

    public String getWord(){ return this.word; }

    public String toString(){
        return "Word: " + this.word + " \tCount: " + this.count + " \tFrequency: " + this.frequency;
    }

    public static class NumComparator implements Comparator<NumPair> {
        @Override
        public int compare(NumPair x, NumPair y)
        {
            // Assume neither string is null. Real code should
            // return the count
            if (x.getCount() < y.getCount())
            {
                return 1;
            }
            if (x.getCount() > y.getCount())
            {
                return -1;
            }
            return 0;
        }
    }

    public static void main(String[] args) {
        NumPair pair = new NumPair("Hello");
        for (int i = 0; i < 10; i++) {
            pair.increment();
        }
        pair.setFrequency(11);
        System.out.println(pair);
    }
}
