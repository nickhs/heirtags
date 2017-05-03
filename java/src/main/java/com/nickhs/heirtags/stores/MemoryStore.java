package com.nickhs.heirtags.stores;

import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-memory store of tags available.
 *
 * @param <E> entity to store
 *
 * Created by nickhs on 1/31/17.
 */
@Slf4j
public class MemoryStore<E> implements TagBagStore<E> {

    /**
     * Internal class that holds the entities for a path
     * and a reference to it's parent
     */
    @ToString(exclude = {"children", "parent"})
    @EqualsAndHashCode(exclude = {"children", "parent"})
    private class TagNode {
        private final TagNode parent;
        private final Set<TagNode> children;
        private final TagPath name;
        private List<E> entities;

        TagNode(List<E> entities, TagPath name) {
            this(entities, name, null);
        }

        TagNode(List<E> entities, TagPath name, TagNode parent) {
            this.entities = entities;
            this.parent = parent;
            this.children = new HashSet<>();
            this.name = name;
        }

        Optional<TagNode> getParent() {
            return Optional.ofNullable(this.parent);
        }

        Set<TagNode> getChildren() {
            return this.children;
        }

        Set<TagNode> addChildren(TagNode el) {
            this.children.add(el);
            return this.getChildren();
        }
    }

    private Map<String, List<TagNode>> store;

    public MemoryStore() {
        store = new HashMap<>();
    }

    @Override
    public void insert(TagPath key, E value) {
        if (!key.isRoot()) {
            throw new IllegalArgumentException
                    (String.format("key must be root %s: %s", key, value));
        }

        // We travel along the tag path adding nodes as necessary
        Iterator<TagPath> iterator = key.iterator();
        TagPath path = iterator.next();
        List<TagNode> matches = this.store.getOrDefault(path.getUnderlying().get(0), new ArrayList<>());
        List<TagNode> validMatches = matches.stream()
                .filter(x -> !x.getParent().isPresent()).collect(Collectors.toList());

        TagNode prev = null;
        if (validMatches.size() == 0) {
            prev = new TagNode(new ArrayList<E>(), path, null);
            validMatches.add(prev);
            // we don't call #toString here as we don't want to get
            // an object with the root tag identifier (/)
            this.store.put(path.getUnderlying().get(0), validMatches);
        } else if (validMatches.size() == 1) {
           prev = validMatches.get(0);
        } else {
            // there should be only one root node...
            //noinspection ConstantConditions
            assert(false);
        }

        while (iterator.hasNext()) {
            TagPath next = iterator.next();
            matches = this.store.getOrDefault(next.getUnderlying().get(0), new ArrayList<>());
            TagNode finalPrev = prev;
            //noinspection ConstantConditions
            validMatches = matches.stream()
                    .filter(x -> x.getParent().isPresent())
                    .filter(x -> x.getParent().get() == finalPrev)
                    .collect(Collectors.toList());

            TagNode match;
            if (validMatches.size() == 0) {
                match = new TagNode(new ArrayList<>(), next, prev);
                matches.add(match);
                this.store.put(next.getUnderlying().get(0), matches);
            } else if (validMatches.size() == 0) {
                match = validMatches.get(0);
            } else {
                // FIXME(nickhs): better error message here
                throw new IllegalStateException("wat.");
            }

            prev.children.add(match);
            prev = match;
        }

        prev.entities.add(value);
    }

    private List<TagNode> getOrDefault(TagSearchPath search, List<TagNode> defaults) {
        assert search.getUnderlying().size() == 1;
        String key = search.getUnderlying().get(0);
        return this.store.getOrDefault(key, defaults);
    }

    private boolean matches(TagSearchPath searchPath, TagPath name) {
        String searchPathS = searchPath.toString();
        String nameS = name.toString();
        assert !searchPathS.contains("**");

        if (searchPathS.equals("*")) {
            return true;
        }

        // now we perform the glob matching
        // taken from https://research.swtch.com/glob
        int px = 0;
        int nx = 0;
        int nextPx = 0;
        int nextNx = 0;

        while (px < searchPathS.length() ||
                nx < nameS.length()) {

            if (px < searchPathS.length()) {
                char patChar = searchPathS.charAt(px);

                if (patChar == '*') {
                    nextPx = px;
                    nextNx = nx + 1;
                    px++;
                    continue;
                }

                else {
                    if (nx < nameS.length() && nameS.charAt(nx) == patChar) {
                        px++;
                        nx++;
                        continue;
                    }
                }
            }

            if (0 < nextNx && nextNx <= nameS.length()) {
                px = nextPx;
                nx = nextNx;
                continue;
            }

            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> findMatching(TagSearchPath key) {
        Iterator<TagSearchPath> iterator = key.iterator();
        TagSearchPath path = iterator.next();

        // FIXME what about initial tag paths having globbing?
        Set<TagNode> prev = new HashSet<>(
                getOrDefault(path, Collections.emptyList()));

        // if this is an explicit root node search,
        // then only grab root nodes
        if (key.isRoot()) {
            prev = prev.stream()
                    .filter(x -> !x.getParent().isPresent())
                    .collect(Collectors.toSet());
        }

        while (iterator.hasNext()) {
            TagSearchPath next = iterator.next();
            prev = prev.stream()
                    .flatMap(x -> x.getChildren().stream())
                    .filter(x -> matches(next, x.name))
                    .collect(Collectors.toSet());
        }

        Set<E> ret = new HashSet<>();
        for (TagNode match : prev) {
            if (key.isTrailing()) {
                match.getChildren().forEach(x -> ret.addAll(x.entities));
            } else {
                ret.addAll(match.entities);
            }
        }

        return ret;
    }
}
