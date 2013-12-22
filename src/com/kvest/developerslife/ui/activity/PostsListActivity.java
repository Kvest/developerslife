package com.kvest.developerslife.ui.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetPostsListRequest;
import com.kvest.developerslife.network.response.GetPostsListResponse;
import com.kvest.developerslife.ui.fragment.PostsListFragment;
import com.kvest.developerslife.utility.CategoryHelper;
import com.kvest.developerslife.utility.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class PostsListActivity extends DevlifeBaseActivity implements PostsListFragment.LoadMorePostsListener {
    private Handler handler = new Handler();
    private PostsListFragment postsListFragment;
    private boolean isDataLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        if (savedInstanceState == null) {
            postsListFragment = new PostsListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                transaction.add(R.id.fragment_container, postsListFragment);
            } finally {
                transaction.commit();
            }
        } else {
            postsListFragment = (PostsListFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
    }

    @Override
    public void loadMorePosts(int page) {
        if (isDataLoading) {
            return;
        }
        setDataLoading(true);

        loadPosts(CategoryHelper.LATEST_CATEGORY_ID, page);
    }

    private void loadPosts(int category, int page) {
        GetPostsListRequest request = new GetPostsListRequest(0, page, new Response.Listener<GetPostsListResponse>() {
            @Override
            public void onResponse(GetPostsListResponse response) {
                savePosts(response);
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostsListActivity.this, getText(R.string.error_loading_posts), Toast.LENGTH_LONG).show();
                setDataLoading(false);
            }
        });
        request.setTag(Constants.VOLLEY_COMMON_TAG);
        VolleyHelper.getInstance().addRequest(request);
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

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setDataLoading(false);
                    }
                });
            }
        }).start();
    }

    private void setDataLoading(boolean value) {
        isDataLoading = value;
        setSupportProgressBarIndeterminateVisibility(isDataLoading);
    }
}
