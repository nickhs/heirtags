package com.nickhs.heirtags;

import java.util.List;

/**
 * Created by nickhs on 2/22/17.
 */
public class TagPathIterator extends AbstractTagPathIterator<TagPath> {
    TagPathIterator(TagPath path) {
        super(path);
    }

    @Override
    TagPath create(boolean isRoot, List pathItems) {
        return new TagPath(isRoot, pathItems);
    }
}
