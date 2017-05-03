package com.nickhs.heirtags;

import java.util.Iterator;
import java.util.List;

/**
 * This is a marker type to signify a search path
 *
 * Search paths can have asterisks (*) unlike {@link TagPath}'s
 *
 * Intepretation of search paths:
 *  * foo/bar - check for all tags that have the foo/bar in them (not necessarily starting at root)
 *  * /foo/bar - check all tags that match /foo/bar exactly
 *  * /foo/bar/ - get all the immediate children of /foo/bar
 *  * /foo/bar/* - get all the immediate children of /foo/bar
 *  * /foo/bar/** - get all the children of /foo/bar
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
