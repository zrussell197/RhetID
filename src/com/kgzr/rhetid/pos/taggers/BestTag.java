package com.kgzr.rhetid.pos.taggers;


import com.kgzr.rhetid.pos.app.TaggerApp;
import com.kgzr.rhetid.pos.adt.TaggedWord;
import com.kgzr.rhetid.pos.adt.Tagger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*

    This version of the tagger just tags every word as a noun (NN).
    
    TODO:  Replace the tag method which tags each word with its most
    likely tag.
*/
public class BestTag implements Tagger {

    public List<List<TaggedWord>> tag(List<List<String>> rawList) {
        List<List<TaggedWord>> taggedList = new LinkedList<List<TaggedWord>>();
        Set<String> tags = TaggerApp.getAllTags();
        Map<String,Double> observations = TaggerApp.getObservationProbs();
        for (List<String> rawSentence : rawList) {
            List<TaggedWord> taggedSentence = new LinkedList<TaggedWord>();
            for (String word : rawSentence) {
                String bestTag = "UNK";
                Double best = 0d;
                for(String tag : tags) {
                    Double prob = observations.get(word + "^" + tag);
                    if (prob != null && prob > best) {
                        best = prob;
                        bestTag = tag;
                    }

                }
                TaggedWord tword = new TaggedWord(word, bestTag);
                taggedSentence.add(tword);
            }
            taggedList.add(taggedSentence);
        }
        return taggedList;
    }
}
