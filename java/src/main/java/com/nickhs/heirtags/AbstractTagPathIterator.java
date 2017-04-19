package com.nickhs.heirtags;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by nickhs on 2/1/17.
 */
public abstract class AbstractTagPathIterator<T extends AbstractTagPath> implements Iterator<T> {
    private final T path;
    private final List<String> pathUnderlying;
    private int state = 0;

    AbstractTagPathIterator(T path) {
        this.path = path;
        this.state = 0;
        this.pathUnderlying = path.getUnderlying();
    }

    abstract T create(boolean isRoot, List<String> pathItems);

    @Override
    public boolean hasNext() {
        return this.state < this.pathUnderlying.size();
    }

    @Override
    public T next() {
        String item = this.pathUnderlying.get(state);
        this.state += 1;
        if (this.state == 1) {
            return this.create(this.path.isRoot(), Collections.singletonList(item));
        }

        return this.create(false, Collections.singletonList(item));
    }
}
