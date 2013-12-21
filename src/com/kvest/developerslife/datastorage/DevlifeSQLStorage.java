package com.kvest.developerslife.datastorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.kvest.developerslife.datastorage.table.CategoriesTable;
import com.kvest.developerslife.datastorage.table.PostTable;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.13
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class DevlifeSQLStorage extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "devlife";
    private static final int DATABASE_VERSION = 1;

    public DevlifeSQLStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create DB structure
        db.execSQL(PostTable.CREATE_TABLE_SQL);
        db.execSQL(CategoriesTable.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Nothing to do yet
    }
}
