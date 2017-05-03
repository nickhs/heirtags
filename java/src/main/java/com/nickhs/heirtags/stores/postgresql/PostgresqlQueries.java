package com.nickhs.heirtags.stores.postgresql;

/**
 * Created by nickhs on 2/23/17.
 */
public class PostgresqlQueries {


    public static final String FIND_MATCHING_ITEMS_NO_PARENT = String.join("\n",
        "SELECT id FROM heirtags",
        "WHERE name = ?"
    );

    public static final String FIND_MATCHING_ITEMS_NO_NAME = String.join("\n",
        "SELECT id from heirtags",
        "WHERE parent_id = ?"
    );

    public static final String CREATE_DB = String.join("\n",
         "CREATE TABLE IF NOT EXISTS heirtags (",
            "id SERIAL PRIMARY KEY,",
            "name TEXT NOT NULL,",
            "parent_id INTEGER REFERENCES heirtags (id),",
            "entities TEXT[]",
        ")"
    );

    public static final String FIND_MATCHING_ITEMS = String.join("\n",
            "SELECT id FROM heirtags",
        "WHERE parent_id IS NOT DISTINCT FROM ?",
        "AND name LIKE ?");

    public static final String INSERT_ITEM = String.join("\n",
            "INSERT INTO heirtags (id, name, parent_id)",
        "VALUES (DEFAULT, ?, ?)",
        "RETURNING id");

    public static final String UPDATE_ENTITY = String.join("\n",
            "UPDATE heirtags",
        "SET entities = array_append(entities, CAST (? as TEXT))",
        "WHERE id IS NOT DISTINCT FROM ?");
}
