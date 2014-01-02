package com.kvest.developerslife.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
public class PostsListsActivity extends DevlifeBaseActivity implements PostsListFragment.LoadMorePostsListener,
                                                                      PostsListFragment.OnPostClickListener {
    private static final int REFRESH_MENU_ID = 0;
    private Handler handler = new Handler();
    private boolean isDataLoading;
    private int shownCategoryId;
    private PostsListFragment[] fragments = new PostsListFragment[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_lists);

        showFragment(CategoryHelper.LATEST_CATEGORY_ID);

        //set button listeners
        findViewById(R.id.latest_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shownCategoryId != CategoryHelper.LATEST_CATEGORY_ID) {
                    showFragment(CategoryHelper.LATEST_CATEGORY_ID);
                }
            }
        });
        findViewById(R.id.hot_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shownCategoryId != CategoryHelper.HOT_CATEGORY_ID) {
                    showFragment(CategoryHelper.HOT_CATEGORY_ID);
                }
            }
        });
        findViewById(R.id.top_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shownCategoryId != CategoryHelper.TOP_CATEGORY_ID) {
                    showFragment(CategoryHelper.TOP_CATEGORY_ID);
                }
            }
        });

        setDataLoading(false);
    }

    private void showFragment(int category) {
        //save shown category
        shownCategoryId = category;

        //create fragment if needed
        if (fragments[shownCategoryId] == null) {
            fragments[shownCategoryId] = PostsListFragment.newInstance(shownCategoryId);
        }

        //show fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        try {
            transaction.replace(R.id.fragment_container, fragments[shownCategoryId]);
        } finally {
            transaction.commit();
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
    public void loadMorePosts(int category, int page) {
        if (isDataLoading) {
            return;
        }
        setDataLoading(true);

        loadPosts(category, page);
    }

    private void refreshPostsList() {
        //clean cache by category (and the loading will start automatically)
        switch (shownCategoryId) {
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
                if (!response.isErrorOccur()) {
                    savePosts(response, category);
                } else {
                    Toast.makeText(PostsListsActivity.this, getText(R.string.error_loading_posts), Toast.LENGTH_LONG).show();
                    setDataLoading(false);
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostsListsActivity.this, getText(R.string.error_loading_posts), Toast.LENGTH_LONG).show();
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
                    values.put(PostTable.VOTES_COLUMN, post.votes);
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
        if (isDataLoading) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    @Override
    public void onPostClick(long postId) {
        Intent intent = new Intent(this, PostDetailsActivity.class);
        intent.putExtra(PostDetailsActivity.POST_ID_EXTRA, postId);
        startActivity(intent);
    }
}
