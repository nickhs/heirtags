package com.nickhs.heirtags.tests.stores;

import com.nickhs.heirtags.stores.MemoryStore;
import org.junit.Test;

/**
 * Created by nickhs on 2/4/17.
 */
public class MemoryStoreTest {

    @Test
    public void testInterface() throws Exception {
        TagBagStoreTest test = new TagBagStoreTest(MemoryStore::new);
        test.allTests();
    }

    @Test
    public void testExternal() throws Exception {
        ExternalTest test = new ExternalTest(MemoryStore::new);
        test.runExternalTests();
    }
}
