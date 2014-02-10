package com.kvest.developerslife.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetPostsListRequest;
import com.kvest.developerslife.network.response.GetPostsListResponse;
import com.kvest.developerslife.ui.adapter.DevlifePagesAdapter;
import com.kvest.developerslife.ui.fragment.PostsListFragment;
import com.kvest.developerslife.utility.CategoryHelper;

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
    private boolean[] isDataLoading = new boolean[CategoryHelper.CATEGORIES_COUNT];

    private ViewPager pager;
    private DevlifePagesAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_lists);

        initVolley();

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new DevlifePagesAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.category_names));
        pager.setAdapter(pagerAdapter);

        initDataLoadingFlags();
    }

    @Override
    public void onStop() {
        super.onStop();

        VolleyHelper.getInstance().cancelAll(this);
    }

    private void initVolley() {
        VolleyHelper.getInstance().init(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuItemCompat.setShowAsAction(menu.add(0, REFRESH_MENU_ID, 0, getString(R.string.refresh))
                                            .setIcon(R.drawable.refresh), MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case REFRESH_MENU_ID : refreshPostsList(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int getShownCategory() {
        return pagerAdapter.getCategoryByPageNumber(pager.getCurrentItem());
    }

    @Override
    public void loadMorePosts(int category, int page) {
        if (isDataLoading[category]) {
            return;
        }
        setDataLoading(category, true);

        loadPosts(category, page);
    }

    private void refreshPostsList() {
        //clean cache by category (and the loading will start automatically)
        int deletedCount = -1;
        switch (getShownCategory()) {
            case CategoryHelper.LATEST_CATEGORY_ID :
                deletedCount = getContentResolver().delete(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, null, null);
                break;
            case CategoryHelper.HOT_CATEGORY_ID :
                deletedCount = getContentResolver().delete(DevlifeProviderMetadata.HOT_POSTS_ITEMS_URI, null, null);
                break;
            case CategoryHelper.TOP_CATEGORY_ID :
                deletedCount = getContentResolver().delete(DevlifeProviderMetadata.TOP_POSTS_ITEMS_URI, null, null);
                break;
        }

        //if nothing was deleted, then event will not occurs. So we need to start load manually
        if (deletedCount == 0) {
            loadMorePosts(getShownCategory(), 0);
        }
    }

    private void loadPosts(final int category, int page) {
        GetPostsListRequest request = new GetPostsListRequest(category, page, new Response.Listener<GetPostsListResponse>() {
            @Override
            public void onResponse(GetPostsListResponse response) {
                setDataLoading(category, false);
                if (response.isErrorOccur()) {
                    Toast.makeText(PostsListsActivity.this, getText(R.string.error_loading_posts), Toast.LENGTH_LONG).show();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PostsListsActivity.this, getText(R.string.error_loading_posts), Toast.LENGTH_LONG).show();
                setDataLoading(category, false);
            }
        });
        request.setTag(this);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void initDataLoadingFlags() {
        for (int i = 0; i < isDataLoading.length; ++i) {
            isDataLoading[i] = false;
        }

        hideProgress();
    }

    private boolean isAnyDataLoading() {
        for (int i = 0; i < isDataLoading.length; ++i) {
            if (isDataLoading[i]) {
                return true;
            }
        }

        return false;
    }

    private void setDataLoading(int category, boolean value) {
        isDataLoading[category] = value;
        if (isAnyDataLoading()) {
            showProgress();
        } else {
            hideProgress();
        }
    }

    @Override
    public void onPostClick(long postId) {
        PostDetailsActivity.start(this, postId);
    }
}
