/*

  This program is an application designed to train and test a
  part-of-speech tagger.

  author: John Donaldson
*/

package com.kgzr.rhetid.pos.app;

import com.kgzr.rhetid.pos.adt.TaggedWord;
import com.kgzr.rhetid.pos.adt.Tagger;
import com.kgzr.rhetid.pos.taggers.BestTag;
import com.kgzr.rhetid.pos.taggers.Greedy;
import com.kgzr.rhetid.pos.taggers.InformedGreedy;
import com.kgzr.rhetid.pos.taggers.Viterbi;
import com.kgzr.rhetid.pos.util.BankLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class TaggerApp {

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

//    /*
//      readTaggedData(Scanner);
//
//      This method is a utility which reads a set of tagged data from a
//      Scanner and loads it into a list of tagged sentences.  Each
//      tagged sentence is a list of tagged words.
//    */
//    static private List<List<TaggedWord>> readTaggedData(Scanner taggedData) {
//        // create an empty list of sentences
//        List<List<TaggedWord>> sentences = new LinkedList<List<TaggedWord>>();
//
//        // loop through the input, one line at a time
//        while (taggedData.hasNextLine()) {
//            String line = taggedData.nextLine(); // read a line
//            Scanner lineScanner = new Scanner(line); // scanner for the line
//            // create an empty sentence
//            List<TaggedWord> taggedSentence = new LinkedList<TaggedWord>();
//
//            // loop through the word/tag items in this sentence
//            while (lineScanner.hasNext()) {
//                String raw = lineScanner.next(); // get a word/tag
//                int k = raw.lastIndexOf("/"); // find the slash
//                if (k < 0) {
//                    System.out.println("missing slash:" + raw);
//                }
//                // split into word, tag
//                String word = raw.substring(0, k);
//                String tag = raw.substring(k + 1);
//                // instantiate a com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord object
//                TaggedWord tword = new TaggedWord(word, tag);
//                // add the tagged word to the sentence
//                taggedSentence.add(tword);
//            }
//            // add the tagged sentence to the sentence list
//            sentences.add(taggedSentence);
//        }
//        return sentences;
//    }
//
//    /**
//     * For reading and tagging an untagged selection of data.
//     * @param taggedData
//     * @return
//     */
//    static private List<List<String>> readUntaggedData(Scanner taggedData) {
//        // create an empty list of sentences
//        List<List<String>> rawSentences = new LinkedList<List<String>>();
//        Tokenizer tokenizer = new Tokenizer(false);
//        // loop through the input, one line at a time
//        while (taggedData.hasNextLine()) {
//            String line = taggedData.nextLine(); // read a line
//            // create an empty sentence
//            List<String> rawSentence = tokenizer.tokenize(line);
//
//            // loop through the word/tag items in this sentence
//            // add the tagged sentence to the sentence list
//            rawSentences.add(rawSentence);
//        }
//        return rawSentences;
//    }
//
//    /*
//      train(List<List<com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord>>);
//
//      This method obtains training probabilities from a list of tagged
//      sentences.  It first finds integer counts of words, tags, tag-tag
//      transitions, and tag-word observations, and then converts them into
//      probabilities.
//    */
//    static private void train(List<List<TaggedWord>> taggedSentences) {
//        Map<String, Integer> wordCounts = new HashMap<String, Integer>();
//        Map<String, Integer> tagCounts = new HashMap<String, Integer>();
//        Map<String, Integer> obsCounts = new HashMap<String, Integer>();
//        Map<String, Integer> transCounts = new HashMap<String, Integer>();
//        int tokenCount = 0;
//
//        // Initialize tag count for sentence-start marker
//        int sentenceCount = 0;
//        tagCounts.put("<s>", sentenceCount);
//
//        // Do counting of words, tags, tag transitions, and word observations
//        for (List<TaggedWord> sentence : taggedSentences) {
//            String prevTag = "<s>";
//            tagCounts.put(prevTag, ++sentenceCount);
//
//            for (TaggedWord tword : sentence) {
//                tokenCount++;
//                String word = tword.getWord();
//                String tag = tword.getTag();
//                allTags.add(tag);
//
//                // count the word
//                Integer wcount = wordCounts.get(word);
//                if (wcount == null)
//                    wordCounts.put(word, 1);
//                else
//                    wordCounts.put(word, wcount + 1);
//
//                // count the tag
//                Integer tcount = tagCounts.get(tag);
//                if (tcount == null)
//                    tagCounts.put(tag, 1);
//                else
//                    tagCounts.put(tag, tcount + 1);
//
//                // count the prevTag -> tag transition
//                String transition = prevTag + "^" + tag;
//                Integer trcount = transCounts.get(transition);
//                if (trcount == null)
//                    transCounts.put(transition, 1);
//                else
//                    transCounts.put(transition, trcount + 1);
//
//                // count the tag -> word observation
//                String observation = word + "^" + tag;
//                Integer ocount = obsCounts.get(observation);
//                if (ocount == null)
//                    obsCounts.put(observation, 1);
//                else
//                    obsCounts.put(observation, ocount + 1);
//
//                prevTag = tag;
//            }
//        }
//
//        // Compute transition probabilities
//        //   P(tag | prevTag) = count(prevTag tag) / count(prevtag)
//        Set<String> transitions = transCounts.keySet();
//        for (String transition : transitions) {
//            String[] parsedTrans = transition.split("\\^");
//            String prevTag = parsedTrans[0];
//            Integer trc = transCounts.get(transition);
//            Integer tc = tagCounts.get(prevTag);
//            double prob = (double) trc / tc;
//            transitionProbs.put(transition, prob);
//        }
//
//        // Compute observation probabilities
//        //   P(word | tag) = count(word/tag) / count(tag)
//        Set<String> observations = obsCounts.keySet();
//        for (String observation : observations) {
//            String[] parsedObs = observation.split("\\^");
//            String tag = parsedObs[1];
//            double prob = (double) obsCounts.get(observation) / tagCounts.get(tag);
//            observationProbs.put(observation, prob);
//        }
//
//        // Compute inverse observation probabilities
//        //   P(tag | word) = count(word/tag) / count(word)
//        for (String observation : observations) {
//            String[] parsedObs = observation.split("\\^");
//            if (parsedObs.length < 2)
//                System.out.println(observation);
//            String word = parsedObs[0];
//            double prob = (double) obsCounts.get(observation) / wordCounts.get(word);
//            inverseProbs.put(observation, prob);
//        }
//
//    }
//
//    /*
//      strip(List<List<com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord>>);
//
//      This method converts a list of tagged sentences into a list of
//      raw (untagged) sentences.  It is used so that we can test
//      a tagger against a set of pretagged test data.
//    */
//     static private List<List<String>> strip(List<List<TaggedWord>> taggedSentences) {
//        List<List<String>> rawSentences = new LinkedList<List<String>>();
//        for (List<TaggedWord> taggedSentence : taggedSentences) {
//            List<String> raw = new LinkedList<String>();
//            for (TaggedWord tword : taggedSentence) {
//                raw.add(tword.getWord());
//            }
//            rawSentences.add(raw);
//        }
//        return rawSentences;
//    }

    /*
      compare(List<List<com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord>>,List<List<com.gleason.com.kgzr.rhetid.pos.adt.TaggedWord>>);

      This method compares two lists of tagged sentences and computes the
      fraction of discrepancies between the two.  It assumes that s1 
      is the standard and that s2 is the list to be tested.
    */
    static private double compare(List<List<TaggedWord>> s1, List<List<TaggedWord>> s2) {
        int wordCount = 0;
        int hits = 0;
        int misses = 0;

        ListIterator<List<TaggedWord>> s2Iterator;
        if (s2 != null)
            s2Iterator = s2.listIterator();
        else
            s2Iterator = null;
        for (List<TaggedWord> s1Sentence : s1) {
            List<TaggedWord> s2Sentence;
            ListIterator<TaggedWord> s2WordIterator;
            if (s2Iterator != null && s2Iterator.hasNext()) {
                s2Sentence = s2Iterator.next();
                if (s2Sentence != null)
                    s2WordIterator = s2Sentence.listIterator();
                else
                    s2WordIterator = null;
            } else {
                s2Sentence = null;
                s2WordIterator = null;
            }

            for (TaggedWord tword1 : s1Sentence) {
                wordCount++;
                if (s2WordIterator == null || !s2WordIterator.hasNext())
                    misses++;
                else {
                    TaggedWord tword2 = s2WordIterator.next();
                    if (tword1.getTag().equals(tword2.getTag()))
                        hits++;
                    else
                        misses++;
                }
            }
        }

        return (double) hits / wordCount;
    }

    public static void runApp(String[] args) throws FileNotFoundException{
        // read the training data
        List<List<TaggedWord>> trainingSentences =
                BankLoader.readTaggedData(new Scanner(new File(args[1])));

        // do the training
        BankLoader.train(trainingSentences);

        // create a com.gleason.com.kgzr.rhetid.pos.adt.Tagger
        Tagger tagger;
        switch (Integer.parseInt(args[0])) {
            case 1:
                tagger = new BestTag();
                break;
            case 2:
                tagger = new Greedy();
                break;
            case 3:
                tagger = new InformedGreedy();
                break;
            case 4:
                tagger = new Viterbi();
                break;
            default:
                tagger = null;
                System.err.println("Unknown tagger id : " + args[0]);
                System.exit(2);
        }

        // read the test data
        Scanner testData = new Scanner(new File(args[2]));
        List<List<String>> rawSentences;
        double score = 0d;
        if (args[2].length() > 4 && args[2].substring(args[2].length()-4).equals("test")) {
            List<List<TaggedWord>> testSentences = BankLoader.readTaggedData(testData);

            // strip off the tags
            rawSentences = BankLoader.strip(testSentences);

            List<List<TaggedWord>> taggedSentences = tagger.tag(rawSentences);
            System.out.println(taggedSentences);
            score = compare(testSentences, taggedSentences);
            System.out.println(score);
        } else {
            rawSentences = BankLoader.readUntaggedData(testData);
            // tag the data
            List<List<TaggedWord>> taggedSentences = tagger.tag(rawSentences);
            System.out.println(taggedSentences);
        }
    }


    /*
      main method

      The main method expects exactly three command-line arguments:
      
      1:  an integer representing which com.gleason.com.kgzr.rhetid.pos.adt.Tagger should be used in
      this run of the program. (1=com.gleason.com.kgzr.rhetid.pos.BestTag.taggers.BestTag,2=com.gleason.com.kgzr.rhetid.pos.taggersetid.pos.Greedy,3=com.gleason.com.kgzr.rhetid.pos.InformedGreedys.InformedGreedy,4=com.gleason.com.kgzr.rhetid.pos.Viterbi.taggers.Viterbi)

      2:  the name of a file containing tagged training data for the com.gleason.com.kgzr.rhetid.pos.adt.Tagger

      3:  the name of a file containing a tagged test data set
    */
    static public void main(String[] args) throws IOException {
        //TODO: remove
        args = new String[3];
        args[0] = "4";
        args[1] = "TreeBank/treebank.tagged";
        args[2] = "TreeBank/KevinWords.txt";
        //Todo: End remove

        if (args.length != 3) {
            System.err.println("Usage:  java com.gleason.com.kgzr.rhetid.pos.app.TaggerApp <version> <training-file> <test-file>");
            System.exit(1);
        }



        runApp(args);
    }

}