package com.nickhs.heirtags.stores;

import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;

import java.util.Set;

/**
 * Created by nickhs on 1/31/17.
 */
public interface TagBagStore<E> {
    // FIXME how to avoid throws Exception?
    void insert(TagPath key, E value) throws Exception;

    /**
     * Find matching keys given a tag path. Supports queries
     * like:
     *
     *   /foo/bar/baz (full path)
     *   /foo/bar/ (gets all the children)
     *   bar (partial)
     *   bar/ (partial, gets all children)
     *
     * @param key tag path to match on
     * @return matching entities
     */
    Set<E> findMatching(TagSearchPath key) throws Exception;
}