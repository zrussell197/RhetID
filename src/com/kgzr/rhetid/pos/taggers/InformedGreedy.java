package com.kgzr.rhetid.pos.taggers;

import com.kgzr.rhetid.pos.adt.TagTree;
import com.kgzr.rhetid.pos.adt.TaggedWord;
import com.kgzr.rhetid.pos.adt.Tagger;

import java.util.LinkedList;
import java.util.List;

/**
 * An HMM tagger using the com.gleason.pos.taggers.Viterbi algorithm, but still selecting the greedy option.
 * argmax(transition probability)
 */
public class InformedGreedy implements Tagger {
    private static final boolean CONTROL = false;
    private static final int LOOP_CONTROL = 50;
    private int loopNum = 0;

    public List<List<TaggedWord>> tag(List<List<String>> rawList) {
        List<List<TaggedWord>> taggedList = new LinkedList<List<TaggedWord>>();
        for (List<String> rawSentence : rawList) {
            TagTree tagTree = new TagTree();
            List<TaggedWord> taggedSentence = new LinkedList<TaggedWord>();
            for (String word : rawSentence) {
                taggedSentence.add(tagTree.informedGreedyStep(word));
            }
            taggedList.add(taggedSentence);
        }
        return taggedList;
    }

}
