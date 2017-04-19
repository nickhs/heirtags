package com.nickhs.heirtags;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Immutable tag path object that represents the keys in a {@link com.nickhs.heirtags.stores.TagBagStore}
 *
 * Tag's are equivalent in terms of capitalization and are normalized to be lower case.
 *
 * Created by nickhs on 1/31/17.
 */
public class TagPath extends AbstractTagPath implements Iterable<TagPath> {
    public TagPath(boolean isRoot, List<String> pathItems) {
        super(isRoot, pathItems);
    }

    public TagPath(String key) {
        super(key);
    }

    public TagPath add(TagPath toAdd) {
        if (toAdd.isRoot()) {
            throw new IllegalArgumentException("Can't add root item");
        }

        ArrayList<String> newList = new ArrayList<>(this.path);
        newList.addAll(toAdd.getUnderlying());
        return new TagPath(this.isRoot(), newList);
    }

    public TagPath add(String key) {
        TagPath toAdd = new TagPath(key);
        return this.add(toAdd);
    }

    @Override
    public Iterator<TagPath> iterator() {
        return new TagPathIterator(this);
    }
}
