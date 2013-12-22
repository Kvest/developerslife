package com.kvest.developerslife.ui.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetPostsListRequest;
import com.kvest.developerslife.network.response.GetPostsListResponse;
import com.kvest.developerslife.ui.fragment.PostsListFragment;

public class MainActivity extends DevlifeBaseActivity implements PostsListFragment.LoadMorePostsListener {
    private boolean isLoading;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState == null) {
            initVolley();

//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            try {
//               transaction.add(R.id.fragment_container, new PostsListFragment());
//            } finally {
//                transaction.commit();
//            }
        }

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //test();
                startActivity(new Intent(MainActivity.this, PostsListActivity.class));
            }
        });

        isLoading = false;
    }

    private void initVolley() {
        VolleyHelper.getInstance().init(getApplicationContext());
    }

    private void test() {
        GetPostsListRequest request = new GetPostsListRequest(0, 0, new Response.Listener<GetPostsListResponse>() {
            @Override
            public void onResponse(GetPostsListResponse response) {
                Log.d("KVEST_TAG", "all is ok");
                savePosts(response);
                printLatestPosts();
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("KVEST_TAG", "error=" + error.getMessage());
            }
        });
        request.setTag("test");
        VolleyHelper.getInstance().addRequest(request);

//        ContentValues values = new ContentValues(6);
//        values.put(PostTable._ID, 27);
//        values.put(PostTable.AUTHOR_COLUMN,"author");
//        values.put(PostTable.DESCRIPTION_COLUMN,"description");
//        values.put(PostTable.DATE_COLUMN,"date");
//        values.put(PostTable.GIF_URL_COLUMN,"gif_url");
//        values.put(PostTable.PREVIEW_URL_COLUMN ,"preview_url");
//        Log.d("KVEST_TAG", "!!!!" + getContentResolver().insert(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, values));


    }

    private void savePosts(final GetPostsListResponse response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (GetPostsListResponse.Post post : response.result)  {
                    ContentValues values = new ContentValues(6);
                    values.put(PostTable._ID, post.id);
                    values.put(PostTable.AUTHOR_COLUMN, post.author);
                    values.put(PostTable.DESCRIPTION_COLUMN, post.description);
                    values.put(PostTable.DATE_COLUMN, post.getDate());
                    values.put(PostTable.GIF_URL_COLUMN, post.gifURL);
                    values.put(PostTable.PREVIEW_URL_COLUMN, post.previewURL);

                    getContentResolver().insert(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, values);
                }

                isLoading = false;
            }
        }).start();
    }

    private void printLatestPosts() {
        Cursor cursor = getContentResolver().query(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, PostTable.FULL_PROJECTION, null,null,null);
        Log.d("KVEST_TAG", "Whole data:" + cursor.getCount());
        Log.d("KVEST_TAG", "--------------------------------------------");
        Log.d("KVEST_TAG", getCursorHeader(cursor));
        Log.d("KVEST_TAG", "--------------------------------------------");
        while(cursor.moveToNext()) {
            Log.d("KVEST_TAG", getCursorData(cursor));
        }
        Log.d("KVEST_TAG", "--------------------------------------------");
        cursor.close();
    }

    private static String getCursorHeader(Cursor cursor) {
        String result = "";
        for (int i = 0; i < cursor.getColumnCount(); ++i) {
            result += cursor.getColumnName(i) + "\t";
        }

        return result;
    }

    private static String getCursorData(Cursor cursor) {
        String result = "";
        for (int i = 0; i < cursor.getColumnCount(); ++i) {
            result += cursor.getString(i) + "\t";
        }

        return result;
    }

    @Override
    public void loadMorePosts(int page) {
        if (isLoading) {
            return;
        }
        isLoading = true;

        Log.d("KVEST_TAG", "page=" + page);
        GetPostsListRequest request = new GetPostsListRequest(0, page, new Response.Listener<GetPostsListResponse>() {
            @Override
            public void onResponse(GetPostsListResponse response) {
                Log.d("KVEST_TAG", "all is ok");
                savePosts(response);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                Log.d("KVEST_TAG", "error=" + error.getMessage());
            }
        });
        request.setTag("test");
        VolleyHelper.getInstance().addRequest(request);
    }
}
