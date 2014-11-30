package com.kgzr.rhetid.pos.adt;

import com.kgzr.rhetid.pos.util.BankLoader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin and Zack on 10/27/14.
 * A tree of TagNodes used for the implementation of the Viterbi algorithm.
 */
public class TagTree {
    private static final boolean DEBUG = false;

    private TagNode head;
    private List<TagNode> prev;
    private List<TagNode> tail;
    private int possObservations;

    public static final String UNK_TAG = "NN";

    public  TagTree(){
        this.head = new TagNode(new TaggedWord("<s>","<s>"),1);
        this.tail = new LinkedList<TagNode>();
        this.tail.add(head);
    }

    public String getBestTag(String word) {
        Set<String> tags = BankLoader.getAllTags();
        Map<String, Double> transProbs = BankLoader.getTransitionProbs();
        Map<String, Double> observations = BankLoader.getObservationProbs();
        TagNode node = this.tail.get(0);

        String bestTag = "NP";
        Double best = 0d;
        for(String tag : tags) {
            Double tagProb = observations.get(word + "^" + tag);
            Double transProb = transProbs.get(node.getTaggedWord().getTag() + "^" + tag);
            if (tagProb != null && transProb != null) {
                Double prob = tagProb*transProb;
                if (prob > best) {
                    best = prob;
                    bestTag = tag;
                }
            }
        }
        return bestTag;
    }


    /**
     * The greedy algorithm should tag each word with the tag that
     *  maximizes P(word | tag)*P(tag | prevTag)
     *  If on first node, (node == head) it just uses the simple probability.
     *   Implemented so first iter uses a "."/"." treating it as the end of a sentence,
     *    but it was less accurate, so returned to first iter being probability.
     */
    public TaggedWord greedyStep(String word) {
        Set<String> tags = BankLoader.getAllTags();
        Map<String, Double> transProbs = BankLoader.getTransitionProbs();
        Map<String, Double> observations = BankLoader.getObservationProbs();
        List<TagNode> newTail = new LinkedList<TagNode>();
        TagNode node = this.tail.get(0);

        String bestTag = UNK_TAG;
        Double best = 0d;
        for(String tag : tags) {
            Double tagProb = observations.get(word + "^" + tag);
            Double transProb = transProbs.get(node.getTaggedWord().getTag() + "^" + tag);
            if (tagProb != null && transProb != null) {
                Double prob = tagProb*transProb;
                if (prob > best) {
                    best = prob;
                    bestTag = tag;
                }
            }
        }
        TaggedWord taggedWord = new TaggedWord(word, bestTag);
        newTail.add(new TagNode(taggedWord,best));
        node.setPointers(newTail);
        this.tail = newTail;
        return taggedWord;
    }

    /**
     * Get the most likely tagged word using the viterbi algorithm, but pick the greediest option going forward.
     * TODO: Consider using 2-d array and backpointers.
     * @param word the next word to be tagged.
     * @return
     */
    public TaggedWord informedGreedyStep(String word) {
        Set<String> tags = BankLoader.getAllTags();
        Map<String, Double> transProbs = BankLoader.getTransitionProbs();
        Map<String, Double> observations = BankLoader.getObservationProbs();
        List<TagNode> newTail = new LinkedList<TagNode>();
        List<TagNode> tailList = this.tail;

        String bestTag = UNK_TAG;
        Double best = 0d;
        for (String tag : tags) {
            Double tagProb = observations.get(word + "^" + tag);
            Double tagBest = 0d;
            if (tagProb != null) {
                if (DEBUG)
                    System.out.println("Word:" + word + "\t\tTag: " + tag + "\t\tTagProb " + tagProb);
                for (TagNode node : tailList) {
                    node.setPointers(newTail);
                    Double transProb = transProbs.get(node.getTaggedWord().getTag() + "^" + tag);
                    if (transProb != null) {
                        Double prevProb = node.getProbability();
                        Double prob = tagProb * transProb * prevProb;
                        if (DEBUG)
                            System.out.println("TransProb: " + node.getTaggedWord().getTag() + "^" + tag + " " + transProb +
                                    " PrevProb " + prevProb + " FinalProb " + prob);

                        if (prob > tagBest) {    //Find the best tag for a word based on previous states.
                            tagBest = prob;
                        }
                        if (prob > best) {      //Worry about best yet?
                            best = prob;
                            bestTag = tag;
                        }
                    }
                }
                newTail.add(new TagNode(new TaggedWord(word, tag), tagBest)); //Add the potential node to list
            }
        }
        TaggedWord taggedWord = new TaggedWord(word, bestTag);

        /**
         * Fine tune based on common trends of english language.
         */
        if (newTail.size()==0){
            newTail.add(new TagNode(englishNuance(word),1d));
        }
        if (DEBUG) {
            printPointElements(tailList);
        }

        this.tail = newTail;
        return taggedWord;
    }

    public void viterbiStep(String word){
        Set<String> tags = BankLoader.getAllTags();
        Map<String, Double> transProbs = BankLoader.getTransitionProbs();
        Map<String, Double> observations = BankLoader.getObservationProbs();
        List<TagNode> newTail = new LinkedList<TagNode>();
        List<TagNode> tailList = this.tail;

        String bestTag = UNK_TAG;
        TagNode bestTail = getBestTail();
        for (String tag : tags) {
            Double tagProb = observations.get(word + "^" + tag);
            Double tagBest = 0d;
            if (tagProb != null) {
                if (DEBUG)
                    System.out.println("Word:" + word + "\t\tTag: " + tag + "\t\tTagProb " + tagProb);
                for (TagNode node : tailList) {
                    node.setPointers(newTail);
                    Double transProb = transProbs.get(node.getTaggedWord().getTag() + "^" + tag);
                    if (transProb != null) {
                        Double prevProb = node.getProbability();
                        Double prob = tagProb * transProb * prevProb;
                        if (DEBUG)
                            System.out.println("TransProb: " + node.getTaggedWord().getTag() + "^" + tag + " " + transProb +
                                    " PrevProb " + prevProb + " FinalProb " + prob);

                        if (prob > tagBest) {    //Find the best tag for a word based on previous states.
                            tagBest = prob;
                            bestTail = node;
                        }
                    }
                }
                TagNode newNode = new TagNode(new TaggedWord(word, tag), tagBest);
                newNode.setBackPointer(bestTail); //Set the back pointer to what had the highest prob
                newTail.add(newNode); //Add the potential node to list
            }
        }
        if (newTail.size()==0){
            TagNode newNode = new TagNode(englishNuance(word), 1d);
            newNode.setBackPointer(getBestTail());
            newTail.add(newNode);
        }
        this.tail = newTail;
    }

    /**
     * Find which of the tail nodes has the highest probabilities, then trace back from it.
     * @return
     */
    public List<TaggedWord> traceViterbi(){
        List<TaggedWord> taggedWords = new LinkedList<TaggedWord>();
        TagNode currentNode = getBestTail();
        while(currentNode!=this.head){
            taggedWords.add(0,currentNode.getTaggedWord());
            currentNode=currentNode.getBack();
        }
        return taggedWords;
    }

    public TagNode getBestTail() {
        TagNode bestTail = tail.get(0);
        for (TagNode node : tail) {
            if (node.getProbability() > bestTail.getProbability())
                bestTail = node;
        }
        return bestTail;
    }

    /**
     * Select a word based on nuances of the english language.
     * @param word the next word to be tagged.
     * @return the Tagged Word.
     */
    public TaggedWord englishNuance(String word){
        if (word.charAt(0)!=word.toLowerCase().charAt(0)) {
            String prevWord = this.tail.get(0).getWord();
            if (prevWord.equals("<s>") || prevWord.equals("\"")){
                return new TaggedWord(word,getBestTag(word.toLowerCase()));
            }
            return new TaggedWord(word, "NP");
        }
        else if (word.contains("-")) {
            return new TaggedWord(word, "JJ");
        }
        else if (Character.isDigit(word.charAt(0))) {
            return new TaggedWord(word, "CD");
        }
        else if (word.charAt(word.length()-1)=='s') {
            return new TaggedWord(word, "NNS");
        }
        else if (word.contains("(") || word.contains("{") || word.contains("}") || word.contains(")")  ){
            return new TaggedWord(word, "(");
        }
        else if (word.equals("\"")){
            return new TaggedWord(word, "''");
        }
        else if (word.equals("!")){
            return new TaggedWord(word, ".");
        }
        else if (word.length() > 2 && word.substring(word.length()-2).equals("ly")){
            System.out.println("RB_TAG " + word);
            return new TaggedWord(word, "RB");
        }
        else {
            System.out.println("UNK_TAG: " + word);
            return new TaggedWord(word, UNK_TAG);
        }
    }

    public void printPointElements(List<TagNode> nodeList){
        for(TagNode node : nodeList){
            System.out.println("Node: " + node.getTaggedWord() + " TailListPointers: " + node.getPointers());
        }
    }

    public String toString(){
        return this.head.toString();
    }


}
