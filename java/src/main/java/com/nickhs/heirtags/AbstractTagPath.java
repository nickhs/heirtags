package com.nickhs.heirtags;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by nickhs on 2/20/17.
 */
public abstract class AbstractTagPath {
    public static final String DELIMITER = "/";

    final List<String> path;
    private final boolean isRoot;
    private Pattern pattern; // If the TagPath is complex

    public AbstractTagPath(boolean isRoot, List<String> pathItems) {
        this.isRoot = isRoot;
        List<String> normalized = Collections.unmodifiableList(normalizePath(pathItems));
        validatePath(normalized);
        this.path = normalized;
    }

    public AbstractTagPath(String key) {
        boolean isRoot = false;
        if (key.startsWith(DELIMITER)) {
            key = key.substring(1);
            isRoot = true;
        }

        this.isRoot = isRoot;
        List<String> normalized = Collections.unmodifiableList(parsePath(key));
        validatePath(normalized);
        this.path = normalized;
    }

    private static List<String> parsePath(String key) {
        return normalizePath(Arrays.asList(key.split(DELIMITER)));
    }

    private static List<String> normalizePath(List<String> pathItems) {
        return pathItems.stream().map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    /**
     * Ensures that passed paths are valid. Will throw a {@link IllegalArgumentException}
     * if that's the case
     * @param pathItems path to validate
     * @return whether it's valid
     */
    public static boolean validatePath(List<String> pathItems) {
        Optional<String> invalidItem = pathItems.stream().filter(x -> x.contains(DELIMITER)).findAny();
        if (invalidItem.isPresent()) {
            throw new IllegalArgumentException(String.format(
                    "path items cannot contain delimiter %s: %s", DELIMITER, invalidItem.get()));
        }

        // FIXME(hanley): check for empty string in path

        return true;
    }

    public List<String> getUnderlying() {
        return new ArrayList<>(this.path);
    }

    public boolean isRoot() {
        return isRoot;
    }

    public String toString() {
        String ret = String.join(DELIMITER, this.path);
        if (isRoot()) {
            return DELIMITER + ret;
        } else {
            return ret;
        }
    }
}
