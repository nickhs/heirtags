package com.nickhs.heirtags;

import java.util.List;

/**
 * Created by nickhs on 2/22/17.
 */
public class TagSearchPathIterator extends AbstractTagPathIterator<TagSearchPath> {
    TagSearchPathIterator(TagSearchPath path) {
        super(path);
    }

    @Override
    TagSearchPath create(boolean isRoot, List<String> pathItems) {
        return new TagSearchPath(isRoot, false, pathItems);
    }
}
