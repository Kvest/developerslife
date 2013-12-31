package com.kvest.developerslife.datastorage.table;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.13
 * Time: 21:02
 * To change this template use File | Settings | File Templates.
 */
public class CommentsTable implements BaseColumns {
    public static final String TABLE_NAME = "comment";

    public static final String PARENT_ID_COLUMN = "parent_id";
    public static final String ENTRY_ID_COLUMN = "entry_id";
    public static final String TEXT_COLUMN = "text";
    public static final String DATE_COLUMN = "date";
    public static final String AUTHOR_ID_COLUMN = "author_id";
    public static final String AUTHOR_NAME_COLUMN = "author_name";
    public static final String VOTE_COUNT_COLUMN = "vote_count";

    public static final String[] FULL_PROJECTION = {_ID, PARENT_ID_COLUMN, ENTRY_ID_COLUMN, TEXT_COLUMN,
                                                    DATE_COLUMN, AUTHOR_ID_COLUMN, AUTHOR_NAME_COLUMN,
                                                    VOTE_COUNT_COLUMN};

    public static final String TRIGGER = "CREATE TRIGGER IF NOT EXISTS \"after_post_delete\" AFTER DELETE ON \"" +
            PostTable.TABLE_NAME + "\" BEGIN " +
            "DELETE FROM \"" + TABLE_NAME + "\" WHERE \"" + ENTRY_ID_COLUMN + "\"=" + "old.\"" + PostTable._ID + "\"; " +
            "END;";

    public static final String CREATE_TABLE_SQL = "CREATE TABLE \"" + TABLE_NAME + "\" (\"" +
            _ID + "\" INTEGER PRIMARY KEY, \"" +
            PARENT_ID_COLUMN + "\" INTEGER DEFAULT 0, \"" +
            ENTRY_ID_COLUMN + "\" INTEGER DEFAULT 0, \"" +
            TEXT_COLUMN + "\" TEXT DEFAULT \"\", \"" +
            DATE_COLUMN + "\" INTEGER DEFAULT 0, \"" +
            AUTHOR_ID_COLUMN + "\" INTEGER DEFAULT 0, \"" +
            AUTHOR_NAME_COLUMN + "\" TEXT DEFAULT \"\", \"" +
            VOTE_COUNT_COLUMN + "\" INTEGER DEFAULT 0);";

    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS \"" + TABLE_NAME + "\";";
}
