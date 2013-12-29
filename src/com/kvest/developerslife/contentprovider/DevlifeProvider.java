package com.kvest.developerslife.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.kvest.developerslife.datastorage.DevlifeSQLStorage;
import com.kvest.developerslife.datastorage.table.CategoriesTable;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.utility.CategoryHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.13
 * Time: 0:21
 * To change this template use File | Settings | File Templates.
 */
public class DevlifeProvider extends ContentProvider {
    private DevlifeSQLStorage sqlStorage;

    private static final int POST_URI_INDICATOR = 1;
    private static final int LATEST_POSTS_URI_INDICATOR = 2;
    private static final int HOT_POSTS_URI_INDICATOR = 3;
    private static final int TOP_POSTS_URI_INDICATOR = 4;

    private static final UriMatcher uriMatcher;
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DevlifeProviderMetadata.AUTHORITY, DevlifeProviderMetadata.POST_ITEMS_PATH + "/#", POST_URI_INDICATOR);
        uriMatcher.addURI(DevlifeProviderMetadata.AUTHORITY, DevlifeProviderMetadata.LATEST_POST_ITEMS_PATH, LATEST_POSTS_URI_INDICATOR);
        uriMatcher.addURI(DevlifeProviderMetadata.AUTHORITY, DevlifeProviderMetadata.HOT_POST_ITEMS_PATH, HOT_POSTS_URI_INDICATOR);
        uriMatcher.addURI(DevlifeProviderMetadata.AUTHORITY, DevlifeProviderMetadata.TOP_POST_ITEMS_PATH, TOP_POSTS_URI_INDICATOR);
    }

    @Override
    public boolean onCreate() {
        sqlStorage = new DevlifeSQLStorage(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int indicator = uriMatcher.match(uri);
        switch (indicator) {
            case POST_URI_INDICATOR :
                queryBuilder.setTables(PostTable.TABLE_NAME);
                queryBuilder.appendWhere(PostTable._ID + "=" + uri.getLastPathSegment());
                break;
            case LATEST_POSTS_URI_INDICATOR :
            case HOT_POSTS_URI_INDICATOR :
            case TOP_POSTS_URI_INDICATOR :
                queryBuilder.setTables("\"" + PostTable.TABLE_NAME  + "\" INNER JOIN \"" + CategoriesTable.TABLE_NAME +
                                       "\" ON \"" + PostTable.TABLE_NAME + "\".\"" + PostTable._ID + "\"=\"" +
                                        CategoriesTable.TABLE_NAME + "\".\"" + CategoriesTable.POST_ID_COLUMN + "\"");
                String[] oldProjection = projection;
                projection = new String[oldProjection.length];
                for (int i = 0; i < projection.length; ++i) {
                    projection[i] = "\"" + PostTable.TABLE_NAME  + "\"." + oldProjection[i];
                }
                queryBuilder.appendWhere(CategoriesTable.CATEGORY_COLUMN + "=" + selectCategoryId(indicator));
                break;
            default:
                throw new IllegalArgumentException("Unknown uri = " + uri);
        }

        //make query
        SQLiteDatabase db = sqlStorage.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = sqlStorage.getWritableDatabase();
        long rowId = 0;

        int indicator = uriMatcher.match(uri);
        switch (indicator) {
            case LATEST_POSTS_URI_INDICATOR :
            case HOT_POSTS_URI_INDICATOR :
            case TOP_POSTS_URI_INDICATOR :
                //replace works as "INSERT OR REPLACE"
                rowId = db.replace(PostTable.TABLE_NAME, null, values);
                if (rowId > 0)
                {
                    ContentValues categoryValues = new ContentValues(2);
                    categoryValues.put(CategoriesTable.POST_ID_COLUMN, rowId);
                    categoryValues.put(CategoriesTable.CATEGORY_COLUMN, selectCategoryId(indicator));
                    long categoryRowId = db.replace(CategoriesTable.TABLE_NAME, null, categoryValues);

                    if (categoryRowId > 0) {
                        Uri resultUri = ContentUris.withAppendedId(uri, rowId);
                        getContext().getContentResolver().notifyChange(resultUri, null);
                        return resultUri;
                    }
                }
                break;
        }

        throw new IllegalArgumentException("Faild to insert row into " + uri);
    }

    private int selectCategoryId(int indicator) {
        switch (indicator) {
            case LATEST_POSTS_URI_INDICATOR : return CategoryHelper.LATEST_CATEGORY_ID;
            case HOT_POSTS_URI_INDICATOR : return CategoryHelper.HOT_CATEGORY_ID;
            case TOP_POSTS_URI_INDICATOR : return CategoryHelper.TOP_CATEGORY_ID;
            default: return CategoryHelper.UNKNOWN_CATEGORY_ID;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;

        switch (uriMatcher.match(uri)) {
            case LATEST_POSTS_URI_INDICATOR :
                rowsDeleted = deletePostsByCategory(CategoryHelper.LATEST_CATEGORY_ID, selection, selectionArgs);
                break;
            case HOT_POSTS_URI_INDICATOR :
                rowsDeleted = deletePostsByCategory(CategoryHelper.HOT_CATEGORY_ID, selection, selectionArgs);
                break;
            case TOP_POSTS_URI_INDICATOR :
                rowsDeleted = deletePostsByCategory(CategoryHelper.TOP_CATEGORY_ID, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri = " + uri);
        }

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    private int deletePostsByCategory(int category, String selection, String[] selectionArgs) {
        boolean hasSelection = !TextUtils.isEmpty(selection);
        SQLiteDatabase db = sqlStorage.getWritableDatabase();
        int rowsDeleted = db.delete(CategoriesTable.TABLE_NAME, CategoriesTable.CATEGORY_COLUMN + "=" + category +
                                    (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionArgs : null));
        //clean posts
        if (rowsDeleted > 0) {
            db.delete(PostTable.TABLE_NAME, PostTable._ID + " NOT IN (?)",
                      new String[]{"SELECT DISTINCT \"" + CategoriesTable.POST_ID_COLUMN + "\" FROM " + CategoriesTable.TABLE_NAME});
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        boolean hasSelection = !TextUtils.isEmpty(selection);
        SQLiteDatabase db = sqlStorage.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case POST_URI_INDICATOR :
                rowsUpdated = db.update(PostTable.TABLE_NAME, values, PostTable._ID + "=" + uri.getLastPathSegment() +
                                        (hasSelection ? (" AND " + selection) : ""), (hasSelection ? selectionArgs : null));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public String getType(Uri uri) {
        switch(uriMatcher.match(uri))
        {
            case POST_URI_INDICATOR : return DevlifeProviderMetadata.CONTENT_TYPE_POST_SINGLE;
            case LATEST_POSTS_URI_INDICATOR :
            case HOT_POSTS_URI_INDICATOR :
            case TOP_POSTS_URI_INDICATOR : return DevlifeProviderMetadata.CONTENT_TYPE_POST_COLLECTION;
            default: throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }
}
