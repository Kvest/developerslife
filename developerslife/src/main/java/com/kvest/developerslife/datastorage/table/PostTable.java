package com.kvest.developerslife.datastorage.table;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.13
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public abstract class PostTable implements BaseColumns {
    public static final String TABLE_NAME = "post";

    public static final String AUTHOR_COLUMN = "author";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String DATE_COLUMN = "date";
    public static final String GIF_URL_COLUMN = "gif_url";
    public static final String PREVIEW_URL_COLUMN = "preview_url";
    public static final String VOTES_COLUMN = "votes";

    public static final String[] FULL_PROJECTION = {_ID, AUTHOR_COLUMN, DESCRIPTION_COLUMN, DATE_COLUMN,
                                                    GIF_URL_COLUMN, PREVIEW_URL_COLUMN, VOTES_COLUMN};

    public static final String CREATE_TABLE_SQL = "CREATE TABLE \"" + TABLE_NAME + "\" (\"" +
            _ID + "\" INTEGER PRIMARY KEY, \"" +
            AUTHOR_COLUMN + "\" TEXT DEFAULT \"\", \"" +
            DESCRIPTION_COLUMN + "\" TEXT DEFAULT \"\", \"" +
            DATE_COLUMN + "\" INTEGER DEFAULT 0, \"" +
            VOTES_COLUMN + "\" INTEGER DEFAULT 0, \"" +
            GIF_URL_COLUMN + "\" TEXT DEFAULT \"\", \"" +
            PREVIEW_URL_COLUMN + "\" TEXT DEFAULT \"\");";

    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS \"" + TABLE_NAME + "\";";
}
