package com.kvest.developerslife.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.ui.adapter.PostsListAdapter;
import com.kvest.developerslife.utility.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 11:44
 * To change this template use File | Settings | File Templates.
 */
public class PostsListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MIN_ITEMS_FOR_MORE_LOAD = 2;
    private static final String[] PROJECTION = {PostTable._ID, PostTable.AUTHOR_COLUMN, PostTable.DESCRIPTION_COLUMN,
                                                PostTable.DATE_COLUMN, PostTable.PREVIEW_URL_COLUMN};
    private static final int LOAD_POSTS_ID = 0;

    private PostsListAdapter adapter;
    private OnPostClickListener onPostClickListener;
    private LoadMorePostsListener loadMorePostsListener;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setCacheColorHint(Color.TRANSPARENT);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0 && (totalItemCount - firstVisibleItem - visibleItemCount) < MIN_ITEMS_FOR_MORE_LOAD) {
                    if (loadMorePostsListener != null) {
                        loadMorePostsListener.loadMorePosts(totalItemCount / Constants.DEFAULT_PAGE_SIZE);
                    }
                }
            }
        });

        //create and set adapter
        String[] from = {PostTable.AUTHOR_COLUMN, PostTable.DATE_COLUMN, PostTable._ID, PostTable.DESCRIPTION_COLUMN,
                         PostTable.PREVIEW_URL_COLUMN};
        int[] to = {R.id.author, R.id.date, R.id.entry_number, R.id.post_description, R.id.preview_image};
        adapter = new PostsListAdapter(getActivity(), R.layout.posts_list_item, null, from, to, PostsListAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onPostClickListener != null) {
                    onPostClickListener.onPostClick(id);
                }
            }
        });

        //load cursor
        getActivity().getSupportLoaderManager().initLoader(LOAD_POSTS_ID, null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onPostClickListener = (OnPostClickListener)activity;
        } catch (ClassCastException cce) {}
        try {
            loadMorePostsListener = (LoadMorePostsListener)activity;
        } catch (ClassCastException cce) {}
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOAD_POSTS_ID : return new CursorLoader(getActivity(), DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI,
                                                         PROJECTION, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //if list is empty - try to load data
        if (cursor.getCount() == 0 && loadMorePostsListener != null) {
            loadMorePostsListener.loadMorePosts(0);
        }

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    public interface OnPostClickListener {
        public void onPostClick(long postId);
    }

    public interface LoadMorePostsListener {
        public void loadMorePosts(int page);
    }
}
