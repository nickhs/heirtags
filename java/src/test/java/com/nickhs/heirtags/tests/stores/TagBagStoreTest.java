package com.nickhs.heirtags.tests.stores;

import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;
import com.nickhs.heirtags.stores.TagBagStore;

import java.util.Set;
import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Created by nickhs on 2/1/17.
 */
public class TagBagStoreTest {
    private final Supplier<TagBagStore<String>> implFactory;

    public TagBagStoreTest(Supplier<TagBagStore<String>> implFactory)  {
        this.implFactory = implFactory;
    }

    public void allTests() throws Exception {
        testInsertBasic();
        testFindMatching();
    }

    private TagBagStore<String> getStore() {
        return implFactory.get();
    }

    public void testInsertBasic() throws Exception {
        TagBagStore<String> bag = this.getStore();
        bag.insert(new TagPath("/group/key"), "a");
        assertEquals(1, bag.findMatching(new TagSearchPath("/group/key")).size());
        assertEquals(0, bag.findMatching(new TagSearchPath("/not/a")).size());
    }

    public void testFindMatching() throws Exception {
        TagBagStore<String> bag = this.getStore();
        bag.insert(new TagPath("/group/test"), "/group/test");
        bag.insert(new TagPath("/group/something else"), "/group/something else");
        bag.insert(new TagPath("/group/more/specific"), "/group/more/specific");
        bag.insert(new TagPath("/group/xxx"), "/group/xxx");

        Set<String> matches = bag.findMatching(new TagSearchPath("/group/xxx"));
        assertEquals(1, matches.size());
        assertTrue(matches.contains("/group/xxx"));

        matches = bag.findMatching(new TagSearchPath("group/xxx"));
        assertEquals(1, matches.size());
        assertTrue("/group/xxx", matches.contains("/group/xxx"));

        matches = bag.findMatching(new TagSearchPath("xxx/"));
        assertEquals(0, matches.size());

        matches = bag.findMatching(new TagSearchPath("xxx"));
        assertEquals(1, matches.size());
        assertTrue("xxx", matches.contains("/group/xxx"));

        matches = bag.findMatching(new TagSearchPath("group"));
        assertEquals(0, matches.size());
        assertTrue(matches.stream().allMatch(x -> x.contains("group")));

        /*
        matches = bag.findMatching(new TagSearchPath("group/*"));
        assertEquals(5, matches.size());
        assertTrue(matches.stream().allMatch(x -> x.contains("group/")));
        */
    }
}
