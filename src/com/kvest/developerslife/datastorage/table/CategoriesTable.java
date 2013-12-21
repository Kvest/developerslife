package com.kvest.developerslife.datastorage.table;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class CategoriesTable implements BaseColumns {
    public static final String TABLE_NAME = "categories";

    public static final String POST_ID_COLUMN = "post_id";
    public static final String CATEGORY_COLUMN = "category";

    public static final String[] FULL_PROJECTION = {_ID, POST_ID_COLUMN, CATEGORY_COLUMN };

    public static final String CREATE_TABLE_SQL = "CREATE TABLE \"" + TABLE_NAME + "\" (\"" +
            _ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT, \"" +
            POST_ID_COLUMN + "\" INTEGER NOT NULL, \"" +
            CATEGORY_COLUMN + "\" INTEGER DEFAULT -1, " +
            "UNIQUE (\"" + POST_ID_COLUMN + "\", \"" + CATEGORY_COLUMN + "\") ON CONFLICT IGNORE);";

    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS \"" + TABLE_NAME + "\";";
}
