package com.kgzr.rhetid.pos.util;

import com.kgzr.rhetid.RhetIdApp;
import com.kgzr.rhetid.pos.adt.TaggedWord;
import com.kgzr.rhetid.pos.tokenizer.Tokenizer;

import java.io.*;
import java.util.*;

/**
 * Created by Kevin and Zach on 10/31/14.
 * The Tree Bank loader class.
 * Class can write the POS Maps to files in the FILE_LOC location.
 * This class is then used to statically load all the treebank files quickly at a low cost.
 */
public class BankLoader {
    /*
      probability tables for use by Taggers
    */
    static private Map<String, Double> transitionProbs =
            new HashMap<String, Double>();
    static private Map<String, Double> observationProbs =
            new HashMap<String, Double>();
    static private Map<String, Double> inverseProbs =
            new HashMap<String, Double>();
    static private Set<String> allTags = new HashSet<String>();

    public static final String FILE_LOC = "POSFiles/";
    /*
      static getter methods for use by Taggers
    */
    static public Map<String, Double> getTransitionProbs() {
        return transitionProbs;
    }

    static public Map<String, Double> getObservationProbs() {
        return observationProbs;
    }

    static public Map<String, Double> getInverseProbs() {
        return inverseProbs;
    }

    static public Set<String> getAllTags() {
        return allTags;
    }

    /*
      readTaggedData(Scanner);

      This method is a utility which reads a set of tagged data from a
      Scanner and loads it into a list of tagged sentences.  Each
      tagged sentence is a list of tagged words.
    */
    static public List<List<TaggedWord>> readTaggedData(Scanner taggedData) {
        // create an empty list of sentences
        List<List<TaggedWord>> sentences = new LinkedList<List<TaggedWord>>();

        // loop through the input, one line at a time
        while (taggedData.hasNextLine()) {
            String line = taggedData.nextLine(); // read a line
            Scanner lineScanner = new Scanner(line); // scanner for the line
            // create an empty sentence
            List<TaggedWord> taggedSentence = new LinkedList<TaggedWord>();

            // loop through the word/tag items in this sentence
            while (lineScanner.hasNext()) {
                String raw = lineScanner.next(); // get a word/tag
                int k = raw.lastIndexOf("/"); // find the slash
                if (k < 0) {
                    System.out.println("missing slash:" + raw);
                }
                // split into word, tag
                String word = raw.substring(0, k);
                String tag = raw.substring(k + 1);
                // instantiate a com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord object
                TaggedWord tword = new TaggedWord(word, tag);
                // add the tagged word to the sentence
                taggedSentence.add(tword);
            }
            // add the tagged sentence to the sentence list
            sentences.add(taggedSentence);
        }
        return sentences;
    }

    /**
     * For reading and tagging an untagged selection of data.
     * @param taggedData
     * @return
     */
    static public List<List<String>> readUntaggedData(Scanner taggedData) {
        // create an empty list of sentences
        List<List<String>> rawSentences = new LinkedList<List<String>>();
        Tokenizer tokenizer = new Tokenizer(false);
        // loop through the input, one line at a time
        while (taggedData.hasNextLine()) {
            String line = taggedData.nextLine(); // read a line
            // create an empty sentence
            List<String> rawSentence = tokenizer.tokenize(line);

            // loop through the word/tag items in this sentence
            // add the tagged sentence to the sentence list
            rawSentences.add(rawSentence);
        }
        return rawSentences;
    }

    /*
      train(List<List<com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord>>);

      This method obtains training probabilities from a list of tagged
      sentences.  It first finds integer counts of words, tags, tag-tag
      transitions, and tag-word observations, and then converts them into
      probabilities.
    */
    static public void train(List<List<TaggedWord>> taggedSentences) {
        Map<String, Integer> wordCounts = new HashMap<String, Integer>();
        Map<String, Integer> tagCounts = new HashMap<String, Integer>();
        Map<String, Integer> obsCounts = new HashMap<String, Integer>();
        Map<String, Integer> transCounts = new HashMap<String, Integer>();
        int tokenCount = 0;

        // Initialize tag count for sentence-start marker
        int sentenceCount = 0;
        tagCounts.put("<s>", sentenceCount);

        // Do counting of words, tags, tag transitions, and word observations
        for (List<TaggedWord> sentence : taggedSentences) {
            String prevTag = "<s>";
            tagCounts.put(prevTag, ++sentenceCount);

            for (TaggedWord tword : sentence) {
                tokenCount++;
                String word = tword.getWord();
                String tag = tword.getTag();
                allTags.add(tag);

                // count the word
                Integer wcount = wordCounts.get(word);
                if (wcount == null)
                    wordCounts.put(word, 1);
                else
                    wordCounts.put(word, wcount + 1);

                // count the tag
                Integer tcount = tagCounts.get(tag);
                if (tcount == null)
                    tagCounts.put(tag, 1);
                else
                    tagCounts.put(tag, tcount + 1);

                // count the prevTag -> tag transition
                String transition = prevTag + "^" + tag;
                Integer trcount = transCounts.get(transition);
                if (trcount == null)
                    transCounts.put(transition, 1);
                else
                    transCounts.put(transition, trcount + 1);

                // count the tag -> word observation
                String observation = word + "^" + tag;
                Integer ocount = obsCounts.get(observation);
                if (ocount == null)
                    obsCounts.put(observation, 1);
                else
                    obsCounts.put(observation, ocount + 1);

                prevTag = tag;
            }
        }

        // Compute transition probabilities
        //   P(tag | prevTag) = count(prevTag tag) / count(prevtag)
        Set<String> transitions = transCounts.keySet();
        for (String transition : transitions) {
            String[] parsedTrans = transition.split("\\^");
            String prevTag = parsedTrans[0];
            Integer trc = transCounts.get(transition);
            Integer tc = tagCounts.get(prevTag);
            double prob = (double) trc / tc;
            transitionProbs.put(transition, prob);
        }

        // Compute observation probabilities
        //   P(word | tag) = count(word/tag) / count(tag)
        Set<String> observations = obsCounts.keySet();
        for (String observation : observations) {
            String[] parsedObs = observation.split("\\^");
            String tag = parsedObs[1];
            double prob = (double) obsCounts.get(observation) / tagCounts.get(tag);
            observationProbs.put(observation, prob);
        }

        // Compute inverse observation probabilities
        //   P(tag | word) = count(word/tag) / count(word)
        for (String observation : observations) {
            String[] parsedObs = observation.split("\\^");
            if (parsedObs.length < 2)
                System.out.println(observation);
            String word = parsedObs[0];
            double prob = (double) obsCounts.get(observation) / wordCounts.get(word);
            inverseProbs.put(observation, prob);
        }

    }

    /*
      strip(List<List<com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord>>);

      This method converts a list of tagged sentences into a list of
      raw (untagged) sentences.  It is used so that we can test
      a tagger against a set of pretagged test data.
    */
    static public List<List<String>> strip(List<List<TaggedWord>> taggedSentences) {
        List<List<String>> rawSentences = new LinkedList<List<String>>();
        for (List<TaggedWord> taggedSentence : taggedSentences) {
            List<String> raw = new LinkedList<String>();
            for (TaggedWord tword : taggedSentence) {
                raw.add(tword.getWord());
            }
            rawSentences.add(raw);
        }
        return rawSentences;
    }

    /**
     * Write to properties file for quicker loading on web interfaces
     */
    public static void writeToFile(Object obj, String fname){
        try {

            File file = new File(FILE_LOC + fname);
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(obj);
            s.close();
        }
        catch (IOException e){ e.printStackTrace();}
    }


    public static Object readFromFile(String fname){
        Object obj = new Object();
        try {

            File file = new File(FILE_LOC + fname);
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            obj =  s.readObject();
            s.close();
        }
        catch (IOException e){ e.printStackTrace();}
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        return obj;
    }

    public static void writeFiles(){
        writeToFile(BankLoader.getObservationProbs(),"ObsProbs.map");
        writeToFile(BankLoader.getTransitionProbs(),"TransProbs.map");
        writeToFile(BankLoader.getInverseProbs(),"InvProbs.map");
        writeToFile(BankLoader.getAllTags(),"AllTags.set");
    }

    @SuppressWarnings("unchecked")
    public static void readFiles(){
        BankLoader.observationProbs = (Map<String,Double>) readFromFile("ObsProbs.map");
        BankLoader.transitionProbs = (Map<String,Double>) readFromFile("TransProbs.map");
        BankLoader.inverseProbs = (Map<String,Double>) readFromFile("InvProbs.map");
        BankLoader.allTags = (Set<String>) readFromFile("AllTags.set");
    }


}
