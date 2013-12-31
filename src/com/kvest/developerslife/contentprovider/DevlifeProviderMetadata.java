package com.kvest.developerslife.contentprovider;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 21.12.13
 * Time: 0:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class DevlifeProviderMetadata {
    public static final String AUTHORITY = "com.kvest.developerslife.contentprovider.DevlifeProvider";

    public static final String CONTENT_TYPE_POST_COLLECTION = "vnd.android.cursor.dir/vnd.kvest.proposal";
    public static final String CONTENT_TYPE_POST_SINGLE = "vnd.android.cursor.item/vnd.kvest.proposal";
    public static final String CONTENT_TYPE_COMMENT_COLLECTION = "vnd.android.cursor.dir/vnd.kvest.comment";
    public static final String CONTENT_TYPE_COMMENT_SINGLE = "vnd.android.cursor.item/vnd.kvest.comment";

    public static final String POST_ITEMS_PATH = "posts";
    public static final String LATEST_POST_ITEMS_PATH = "posts/latest";
    public static final String HOT_POST_ITEMS_PATH = "posts/hot";
    public static final String TOP_POST_ITEMS_PATH = "posts/top";
    public static final String COMMENTS_PATH = "comments";
    public static final String ENTRY_COMMENTS_PATH = "comments/entry";

    public static final Uri POST_ITEMS_URI = Uri.parse("content://" + AUTHORITY + "/" + POST_ITEMS_PATH);
    public static final Uri LATEST_POSTS_ITEMS_URI = Uri.parse("content://" + AUTHORITY + "/" + LATEST_POST_ITEMS_PATH);
    public static final Uri HOT_POSTS_ITEMS_URI = Uri.parse("content://" + AUTHORITY + "/" + HOT_POST_ITEMS_PATH);
    public static final Uri TOP_POSTS_ITEMS_URI = Uri.parse("content://" + AUTHORITY + "/" + TOP_POST_ITEMS_PATH);
    public static final Uri COMMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + COMMENTS_PATH);
    public static final Uri ENTRY_COMMENTS_URI = Uri.parse("content://" + AUTHORITY + "/" + ENTRY_COMMENTS_PATH);
}
