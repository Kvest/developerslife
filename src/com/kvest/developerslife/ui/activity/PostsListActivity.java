package com.kvest.developerslife.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
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
public class PostsListActivity extends DevlifeBaseActivity implements PostsListFragment.LoadMorePostsListener,
                                                                      PostsListFragment.OnPostClickListener {
    private static final int REFRESH_MENU_ID = 0;
    private Handler handler = new Handler();
    private PostsListFragment postsListFragment;
    private boolean isDataLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        setDataLoading(false);
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItemCompat.setShowAsAction(menu.add(0, REFRESH_MENU_ID, 0, getString(R.string.refresh))
                                            .setIcon(android.R.drawable.ic_popup_sync), MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case REFRESH_MENU_ID : refreshPostsList(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadMorePosts(int page) {
        if (isDataLoading) {
            return;
        }
        setDataLoading(true);

        loadPosts(postsListFragment.getCategory(), page);
    }

    private void refreshPostsList() {
        //clean cache by category (and the loading will start automatically)
        switch (postsListFragment.getCategory()) {
            case CategoryHelper.LATEST_CATEGORY_ID :
                getContentResolver().delete(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, null, null);
                break;
            case CategoryHelper.HOT_CATEGORY_ID :
                getContentResolver().delete(DevlifeProviderMetadata.HOT_POSTS_ITEMS_URI, null, null);
                break;
            case CategoryHelper.TOP_CATEGORY_ID :
                getContentResolver().delete(DevlifeProviderMetadata.TOP_POSTS_ITEMS_URI, null, null);
                break;
        }
    }

    private void loadPosts(final int category, int page) {
        GetPostsListRequest request = new GetPostsListRequest(category, page, new Response.Listener<GetPostsListResponse>() {
            @Override
            public void onResponse(GetPostsListResponse response) {
                savePosts(response, category);
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

    private void savePosts(final GetPostsListResponse response, final int category) {
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

                    switch (category) {
                        case CategoryHelper.LATEST_CATEGORY_ID :
                            getContentResolver().insert(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, values);
                            break;
                        case CategoryHelper.HOT_CATEGORY_ID :
                            getContentResolver().insert(DevlifeProviderMetadata.HOT_POSTS_ITEMS_URI, values);
                            break;
                        case CategoryHelper.TOP_CATEGORY_ID :
                            getContentResolver().insert(DevlifeProviderMetadata.TOP_POSTS_ITEMS_URI, values);
                            break;
                    }
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

    @Override
    public void onPostClick(long postId) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA, postId);
        startActivity(intent);
    }
}
