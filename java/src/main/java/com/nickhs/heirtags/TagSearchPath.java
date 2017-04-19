package com.nickhs.heirtags;

import java.util.Iterator;
import java.util.List;

/**
 * This is a marker type to signify a search path
 *
 * Created by nickhs on 2/7/17.
 */
public class TagSearchPath extends AbstractTagPath implements Iterable<TagSearchPath> {
    private final boolean isTrailing;

    public TagSearchPath(boolean isRoot, boolean isTrailing, List<String> pathItems) {
        super(isRoot, pathItems);
        this.isTrailing = isTrailing;
    }

    public TagSearchPath(String key) {
        super(key);
        this.isTrailing = key.endsWith(DELIMITER);
    }

    @Override
    public Iterator<TagSearchPath> iterator() {
        return new TagSearchPathIterator(this);
    }

    public boolean isTrailing() {
        return isTrailing;
    }
 };
