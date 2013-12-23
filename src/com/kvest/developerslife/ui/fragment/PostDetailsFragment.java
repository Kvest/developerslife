package com.kvest.developerslife.ui.fragment;

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
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.NetworkRequestHelper;
import com.kvest.developerslife.utility.Constants;
import com.kvest.developerslife.utility.FileUtility;

import java.io.File;
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOAD_POST_ID :
                Bundle arguments = getArguments();
                long postId = (arguments != null && arguments.containsKey(POST_ID_ARGUMENT)) ? arguments.getLong(POST_ID_ARGUMENT) : -1;
                Uri uri = Uri.withAppendedPath(DevlifeProviderMetadata.POST_ITEMS_URI, Long.toString(postId));
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
        String description = getString(R.string.description, cursor.getString(cursor.getColumnIndex(PostTable.DESCRIPTION_COLUMN)));
        ((TextView)root.findViewById(R.id.post_description)).setText(Html.fromHtml(description));

        //load gif
        gifLoader = new GifLoader();
        gifLoader.execute(cursor.getString(cursor.getColumnIndex(PostTable.GIF_URL_COLUMN)));
    }

    private class GifLoader extends AsyncTask<String, Void, String> {
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
                //TODO
                //test if file still not exists
            } else {
                //TODO
                //show error
            }

            gifLoader = null;
        }
    }
}
