package com.nickhs.heirtags.stores.postgresql;

import com.nickhs.heirtags.TagPath;
import com.nickhs.heirtags.TagSearchPath;
import com.nickhs.heirtags.stores.TagBagStore;
import com.nickhs.heirtags.stores.postgresql.PostgresqlQueries;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nickhs on 2/20/17.
 */
// We only handle strings for now as we don't want to have to bother
// with serializing/deserializing objects. We can let the caller
// handle that...
public class PostgresqlStore implements TagBagStore<String> {
    // NB: this is also harcoded in the sqls
    // FIXME(nickhs): make this user customizable?
    public static final String TABLE_NAME = "heirtags";

    private final Connection connection;

    public PostgresqlStore(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(PostgresqlQueries.CREATE_DB);
    }

    @Override
    public void insert(TagPath key, String value) throws SQLException, IOException {
        PreparedStatement searchStatement = connection.prepareStatement(PostgresqlQueries.FIND_MATCHING_ITEMS);
        PreparedStatement insertStatement = connection.prepareStatement(PostgresqlQueries.INSERT_ITEM);

        Integer parentId = null;
        for (TagPath item : key) {
            if (parentId == null) {
                searchStatement.setNull(1, Types.INTEGER);
            } else {
                searchStatement.setInt(1, parentId);
            }

            searchStatement.setString(2, item.getUnderlying().get(0));
            ResultSet results = searchStatement.executeQuery();
            if (!results.next()) {
                insertStatement.setString(1, item.getUnderlying().get(0));

                if (parentId == null) {
                    insertStatement.setNull(2, Types.INTEGER);
                } else {
                    insertStatement.setInt(2, parentId);
                }

                results = insertStatement.executeQuery();
                results.next();
                parentId = results.getInt(1);
            } else {
                parentId = results.getInt(1);
            }
        }

        // FIXME don't insert duplicate entities
        PreparedStatement updateStatement = connection.prepareStatement(PostgresqlQueries.UPDATE_ENTITY);
        updateStatement.setString(1, value);

        // parentId is now the id of the current record
        if (parentId == null) {
            updateStatement.setNull(2, Types.INTEGER);
        } else {
            updateStatement.setInt(2, parentId);
        }

        updateStatement.execute();
    }

    @Override
    public Set<String> findMatching(TagSearchPath key) throws SQLException {
        Iterator<TagSearchPath> iterator = key.iterator();

        PreparedStatement initialStatement;
        // initial start
        if (key.isRoot()) {
            initialStatement = connection.prepareStatement(PostgresqlQueries.FIND_MATCHING_ITEMS);
            initialStatement.setNull(1, Types.INTEGER);
            initialStatement.setString(2, iterator.next().getUnderlying().get(0));
        } else {
            initialStatement = connection.prepareStatement(PostgresqlQueries.FIND_MATCHING_ITEMS_NO_PARENT);
            initialStatement.setString(1, iterator.next().getUnderlying().get(0));
        }

        // FIXME may need to do an ILIKE?
        ResultSet resultSet = initialStatement.executeQuery();

        Set<Integer> previousIds = new HashSet<>();
        while (resultSet.next()) {
            previousIds.add(resultSet.getInt(1));
        }

        PreparedStatement statement = connection.prepareStatement(PostgresqlQueries.FIND_MATCHING_ITEMS);
        while (iterator.hasNext() && previousIds.size() > 0) {
            String item = iterator.next().getUnderlying().get(0);
            HashSet<Integer> oldIds = new HashSet<>(previousIds);
            previousIds = new HashSet<>();
            for (Integer previousId : oldIds) {
                statement.setInt(1, previousId);
                statement.setString(2, item);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    previousIds.add(resultSet.getInt(1));
                }
            }
        }

        // if we have a trailing search then we need to
        // go another layer deep
        if (key.isTrailing()) {
            HashSet<Integer> oldIds = new HashSet<>(previousIds);
            previousIds = new HashSet<>();
            statement = connection.prepareStatement(PostgresqlQueries.FIND_MATCHING_ITEMS_NO_NAME);
            for (Integer previousId : oldIds) {
                statement.setInt(1, previousId);
                resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    previousIds.add(resultSet.getInt(1));
                }
            }
        }

        if (previousIds.size() == 0) {
            return Collections.emptySet();
        }

        PreparedStatement getEntities = connection.prepareStatement("SELECT entities FROM heirtags WHERE id = ANY (?)");
        getEntities.setArray(1, connection.createArrayOf("integer", previousIds.toArray()));
        ResultSet entities = getEntities.executeQuery();
        Set<String> ret = new HashSet<>();
        while (entities.next()) {
            Array arr = entities.getArray(1);
            if (arr != null) { // happens when there are no matches
                String[] temp = (String[]) arr.getArray();
                ret.addAll(Arrays.asList(temp));
            }
        }

        return ret;
    }
}
