package com.kvest.developerslife.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.CommentsTable;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.NetworkRequestHelper;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetCommentsRequest;
import com.kvest.developerslife.network.response.GetCommentsResponse;
import com.kvest.developerslife.ui.activity.DevlifeBaseActivity;
import com.kvest.developerslife.utility.Constants;
import com.kvest.developerslife.utility.FileUtility;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 22:16
 * To change this template use File | Settings | File Templates.
 */
public class PostDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private static final String POST_ID_ARGUMENT = "com.kvest.developerslife.ui.fragment.PostDetailsFragment.POST_ID";
    private static final int LOAD_POST_ID = 0;
    private static final int LOAD_COMMENTS_ID = 1;

    private GifLoader gifLoader;

    public static PostDetailsFragment createPostDetailsFragment(long postId) {
        Bundle arguments = new Bundle();
        arguments.putLong(POST_ID_ARGUMENT, postId);

        PostDetailsFragment result = new PostDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.post_details_fragment, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().getSupportLoaderManager().initLoader(LOAD_POST_ID, null, this);
    }

    @Override
    public void onStop() {
        super.onStop();

        //cancel gif request
        if (gifLoader != null) {
            gifLoader.cancel(true);
            gifLoader = null;
        }
    }

    private long getPostId() {
        Bundle arguments = getArguments();
        return (arguments != null && arguments.containsKey(POST_ID_ARGUMENT)) ? arguments.getLong(POST_ID_ARGUMENT) : -1;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOAD_POST_ID :
                Uri uri = Uri.withAppendedPath(DevlifeProviderMetadata.POST_ITEMS_URI, Long.toString(getPostId()));
                return new CursorLoader(getActivity(), uri, PostTable.FULL_PROJECTION, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            setContent(cursor);
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        //nothing to do
    }

    private void setContent(Cursor cursor) {
        View root = getView();
        ((TextView)root.findViewById(R.id.author)).setText(cursor.getString(cursor.getColumnIndex(PostTable.AUTHOR_COLUMN)));
        Date date = new Date(cursor.getLong(cursor.getColumnIndex(PostTable.DATE_COLUMN)));
        ((TextView)root.findViewById(R.id.date)).setText(DATE_FORMAT.format(date));
        ((TextView)root.findViewById(R.id.entry_number)).setText(getString(R.string.entry_number, cursor.getLong(cursor.getColumnIndex(PostTable._ID))));
        String tmp = getString(R.string.description_html, cursor.getString(cursor.getColumnIndex(PostTable.DESCRIPTION_COLUMN)));
        ((TextView)root.findViewById(R.id.post_description)).setText(Html.fromHtml(tmp));
        tmp = getString(R.string.rating_html, cursor.getInt(cursor.getColumnIndex(PostTable.VOTES_COLUMN)));
        ((TextView)root.findViewById(R.id.post_rating)).setText(Html.fromHtml(tmp));
        tmp = getString(R.string.comments_html, 0);
        ((TextView)root.findViewById(R.id.post_comments)).setText(Html.fromHtml(tmp));

        //load gif
        gifLoader = new GifLoader();
        gifLoader.execute(cursor.getString(cursor.getColumnIndex(PostTable.GIF_URL_COLUMN)));
    }

    private void setGifFile(String filePath) {
        try {
            GifDrawable gifFromPath = new GifDrawable(filePath);
            ((GifImageView)getView().findViewById(R.id.gif_image)).setImageDrawable(gifFromPath);
        } catch (IOException ioException) {
            Toast.makeText(getActivity(), R.string.error_loading_gif, Toast.LENGTH_LONG).show();
        }
    }

    private void setCommentsCount(int commentsCount) {
        String tmp = getString(R.string.comments_html, commentsCount);
        ((TextView)getView().findViewById(R.id.post_comments)).setText(Html.fromHtml(tmp));
    }

    private void updateComments() {
        GetCommentsRequest request = new GetCommentsRequest(getPostId(), new Response.Listener<GetCommentsResponse>() {
            @Override
            public void onResponse(GetCommentsResponse response) {
                //save comments
                saveComments(response);

                //try to update data about post
                //TODO
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //show comments
                loadCommentsFromCache();

                //hide progress
                Activity activity = getActivity();
                if (activity != null) {
                    ((DevlifeBaseActivity)activity).hideProgress();
                }

                //show message
                Toast.makeText(getActivity(), R.string.error_updating_comments, Toast.LENGTH_LONG).show();
            }
        });
        request.setTag(Constants.VOLLEY_COMMON_TAG);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void loadCommentsFromCache() {
        DevlifeBaseActivity activity = (DevlifeBaseActivity)getActivity();
        if (activity != null) {
            activity.getSupportLoaderManager().initLoader(LOAD_COMMENTS_ID, null, this);
        }
    }

    private void showComments() {
        //TODO
        //setCommentsCount(response.comments.size());
    }

    private void saveComments(final GetCommentsResponse response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = null;
                if (getActivity() != null) {
                    contentResolver = getActivity().getContentResolver();
                }
                if (contentResolver != null) {
                    for (GetCommentsResponse.Comment comment : response.comments) {
                        ContentValues values = new ContentValues(8);
                        values.put(CommentsTable._ID, comment.id);
                        values.put(CommentsTable.PARENT_ID_COLUMN, comment.parentId);
                        values.put(CommentsTable.ENTRY_ID_COLUMN, comment.entryId);
                        values.put(CommentsTable.TEXT_COLUMN, comment.text);
                        values.put(CommentsTable.DATE_COLUMN, comment.getDate());
                        values.put(CommentsTable.AUTHOR_ID_COLUMN, comment.authorId);
                        values.put(CommentsTable.AUTHOR_NAME_COLUMN, comment.authorName);
                        values.put(CommentsTable.VOTE_COUNT_COLUMN, comment.voteCount);

                        contentResolver.insert(DevlifeProviderMetadata.COMMENTS_URI, values);
                    }
                }

                //show comments
                loadCommentsFromCache();
            }
        }).start();
    }

    private class GifLoader extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //show progress
            Activity activity = getActivity();
            if (activity != null) {
                ((DevlifeBaseActivity)activity).showProgress();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            //get file name
            String url = params[0];
            String[] urlParts = url.split("/");
            String fullFileName = Constants.GIFS_CACHE_DIRECTORY + urlParts[urlParts.length - 1];

            //if file already loaded - return path
            if (FileUtility.fileExists(fullFileName)) {
                return fullFileName;
            }

            //create cache dir
            FileUtility.createDirIfNotExists(Constants.GIFS_CACHE_DIRECTORY);

            //load gif file
            if (NetworkRequestHelper.loadFile(url, new File(fullFileName))) {
                return fullFileName;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                setGifFile(result);
            } else {
                //show error
                Toast.makeText(getActivity(), R.string.error_loading_gif, Toast.LENGTH_LONG).show();
            }

            //try t update comments
            updateComments();

            gifLoader = null;
        }
    }
}
