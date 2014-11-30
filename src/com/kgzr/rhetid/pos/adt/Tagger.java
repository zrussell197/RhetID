package com.kgzr.rhetid.pos.adt;

import java.util.List;

public interface Tagger {

    public List<List<TaggedWord>> tag(List<List<String>> sentences);

}
