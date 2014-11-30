package com.kgzr.rhetid.pos.taggers;


import com.kgzr.rhetid.pos.adt.TagTree;
import com.kgzr.rhetid.pos.adt.TaggedWord;
import com.kgzr.rhetid.pos.adt.Tagger;

import java.util.LinkedList;
import java.util.List;

/*

  This version of the tagger just returns null.

  TODO:  Replace the tag method with your version of the greedy algorithm.

  The greedy algorithm should tag each word with the tag that
  maximizes P(word | tag)*P(tag | prevTag)
*/

public class Greedy implements Tagger {

    public List<List<TaggedWord>> tag(List<List<String>> rawList) {

        TagTree tagTree = new TagTree();
        List<List<TaggedWord>> taggedList = new LinkedList<List<TaggedWord>>();

        for (List<String> rawSentence : rawList) {
            List<TaggedWord> taggedSentence = new LinkedList<TaggedWord>();
            for (String word : rawSentence) {
                TaggedWord taggedWord = tagTree.greedyStep(word);
                taggedSentence.add(taggedWord);
            }
            taggedList.add(taggedSentence);
        }
        return taggedList;
    }

}
