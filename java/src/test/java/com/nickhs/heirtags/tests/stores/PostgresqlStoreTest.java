package com.nickhs.heirtags.tests.stores;

import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;
import com.nickhs.heirtags.stores.postgresql.PostgresqlStore;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by nickhs on 2/20/17.
 */
public class PostgresqlStoreTest {
    private static Connection connFactory() throws SQLException {
        String postgresURL = System.getProperty("POSTGRESQL_URL");
        return DriverManager.getConnection(postgresURL);
    }

    @BeforeClass
    public static void checkForPostgres() {
        try {
            Connection conn = connFactory();
            Assume.assumeTrue(conn.isValid(10));
        } catch (SQLException ex) {
            Assume.assumeNoException(ex);
        }
    }

    @Test
    public void testCreateDb() throws SQLException {
        Connection conn = connFactory();
        conn.createStatement().execute("DROP TABLE IF EXISTS " + PostgresqlStore.TABLE_NAME);
        PostgresqlStore store = new PostgresqlStore(conn);
        store.createTable();
    }

    @Test
    public void testInsertion() throws SQLException, IOException {
        Connection conn = connFactory();
        PostgresqlStore store = new PostgresqlStore(conn);
        store.insert(new TagPath("/test"), "test");
        PostgresqlStore store2 = new PostgresqlStore(conn);
        store2.insert(new TagPath("/test/2"), "1");
        store2.insert(new TagPath("/test/3"), "2");
        store2.insert(new TagPath("/test/2"), "3");
    }

    @Test
    public void testFindMatching() throws SQLException {
        Connection conn = connFactory();
        PostgresqlStore store = new PostgresqlStore(conn);
        Set<String> ret = store.findMatching(new TagSearchPath("/test/2"));
        assertEquals(ret.size(), 2);
        assertTrue(ret.contains("1"));
        assertTrue(ret.contains("3"));
    }

    @Test
    public void testInterface() throws Exception {
        Connection conn = connFactory();
        TagBagStoreTest test = new TagBagStoreTest(() -> new PostgresqlStore(conn));
        test.allTests();
    }
}
