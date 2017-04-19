package com.nickhs.heirtags.stores;

import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;

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
public class MemoryStore<E> implements TagBagStore<E> {

    /**
     * Internal class that holds the entities for a path
     * and a reference to it's parent
     */
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
        List<TagNode> matches = this.store.getOrDefault(path, new ArrayList<>());
        List<TagNode> validMatches = matches.stream()
                .filter(x -> !x.getParent().isPresent()).collect(Collectors.toList());

        TagNode prev = null;
        if (validMatches.size() == 0) {
            prev = new TagNode(new ArrayList<E>(), path, null);
            validMatches.add(prev);
            this.store.put(path.getUnderlying().get(0), validMatches);
        } else if (validMatches.size() == 1) {
           prev = validMatches.get(0);
        } else {
            //noinspection ConstantConditions
            assert(false);
        }

        while (iterator.hasNext()) {
            TagPath next = iterator.next();
            matches = this.store.getOrDefault(next, new ArrayList<>());
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
            } else {
                match = validMatches.get(0);
            }

            prev.children.add(match);
            prev = match;
        }

        prev.entities.add(value);
    }

    private List<TagNode> getOrDefault(TagSearchPath search, List<TagNode> defaults) {
        assert search.getUnderlying().size() == 1;
        String key = search.getUnderlying().get(0);

        /*
        // do a regex match on all keys
        if (tagPath.isComplex()) {
            if (key.equals("*")) {
                key = ".*";
            }

            Pattern pattern = Pattern.compile(key);
            return this.store.entrySet().stream().filter(x -> {
                return pattern.matcher(x.getKey()).matches();
            }).map(Map.Entry::getValue).reduce(new ArrayList<TagNode<E>>(),
            (prev, next) -> {
                prev.addAll(next);
                return prev;
            });
        }
        */

        return this.store.getOrDefault(key, defaults);
    }

    private boolean matches(TagSearchPath searchPath, TagPath name) {
        PathMatcher matcher = FileSystems.getDefault()
                .getPathMatcher(String.format("glob:%s", searchPath.toString()));
        return matcher.matches(Paths.get(name.toString()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<E> findMatching(TagSearchPath key) {
        Iterator<TagSearchPath> iterator = key.iterator();
        TagSearchPath path = iterator.next();

        Set<TagNode> prev = new HashSet<>(
                getOrDefault(path, Collections.emptyList()));

        while (iterator.hasNext()) {
            TagSearchPath next = iterator.next();
            prev = prev.stream()
                    .flatMap(x -> x.getChildren().stream())
                    .filter(x -> matches(next, x.name))
                    .collect(Collectors.toSet());

                    /*
            List<TagNode<E>> newMatches = getOrDefault(
                    next, Collections.emptyList());
            matches = newMatches.stream()
                    .filter(x -> x.getParent().isPresent())
                    .filter(x -> finalMatches.contains(x.getParent().get()))
                    .collect(Collectors.toSet());
                    */
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
