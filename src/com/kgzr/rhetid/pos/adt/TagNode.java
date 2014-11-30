package com.kgzr.rhetid.pos.adt;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kevin and Zach on 10/27/14.
 * A Single node of the Viterbi algorithm.
 */
public class TagNode {
    private TaggedWord taggedWord;
    private double probability;
    private List<TagNode> pointers = new LinkedList<TagNode>();
    private TagNode backPointer;

    public TagNode(){}

    public TagNode(TaggedWord word){
        this.taggedWord = word;
    }

    public TagNode(TaggedWord word, double probability){
        this.taggedWord = word;
        this.probability=probability;
    }

    public TagNode(TaggedWord word, List<TagNode> pointers){
        this.taggedWord = word;
        this.pointers=pointers;
    }

    public String getWord(){
        return this.taggedWord.getWord();
    }

    public String getTag(){
        return this.taggedWord.getTag();
    }

    public TaggedWord getTaggedWord(){
        return this.taggedWord;
    }

    public double getProbability(){
        return this.probability;
    }

    public List<TagNode> getPointers(){
        return this.pointers;
    }

    public void setPointers(List<TagNode> pointers){
        this.pointers = pointers;
    }

    public void setBackPointer(TagNode back){
        this.backPointer=back;
    }

    public TagNode getBack(){
        return this.backPointer;
    }

    public String toString(){
        return this.taggedWord.toString();
    }
}
