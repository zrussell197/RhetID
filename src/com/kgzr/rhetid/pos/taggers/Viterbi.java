package com.kgzr.rhetid.pos.taggers;


import com.kgzr.rhetid.pos.adt.TagTree;
import com.kgzr.rhetid.pos.adt.TaggedWord;
import com.kgzr.rhetid.pos.adt.Tagger;

import java.util.LinkedList;
import java.util.List;

/**
 * The Viterbi tagger implementation.
*/
public class Viterbi implements Tagger {

    public List<List<TaggedWord>> tag(List<List<String>> rawList) {
        List<List<TaggedWord>> taggedList = new LinkedList<List<TaggedWord>>();
        for (List<String> rawSentence : rawList) {
            TagTree tagTree = new TagTree();
            List<TaggedWord> taggedSentence = new LinkedList<TaggedWord>();
            for (String word : rawSentence) {
                tagTree.viterbiStep(word);
            }
            taggedSentence.addAll(tagTree.traceViterbi());
            taggedList.add(taggedSentence);
        }
        return taggedList;
    }

}
