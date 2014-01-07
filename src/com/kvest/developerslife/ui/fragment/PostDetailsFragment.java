package com.kvest.developerslife.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datamodel.CommentNode;
import com.kvest.developerslife.datastorage.table.CommentsTable;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.NetworkRequestHelper;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetCommentsRequest;
import com.kvest.developerslife.network.request.GetPostRequest;
import com.kvest.developerslife.network.response.GetCommentsResponse;
import com.kvest.developerslife.network.response.GetPostResponse;
import com.kvest.developerslife.ui.activity.DevlifeBaseActivity;
import com.kvest.developerslife.ui.widget.ResizableGifImageView;
import com.kvest.developerslife.utility.Constants;
import com.kvest.developerslife.utility.FileUtility;

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
public class PostDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CommentNode.Visitor {
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);
    private static final String POST_ID_ARGUMENT = "com.kvest.developerslife.ui.fragment.PostDetailsFragment.POST_ID";
    private static final int LOAD_POST_ID = 0;
    private static final int LOAD_COMMENTS_ID = 1;
    private static final String[] COMMENTS_PROJECTION = new String[]{CommentsTable._ID, CommentsTable.PARENT_ID_COLUMN,
                                                                     CommentsTable.VOTE_COUNT_COLUMN, CommentsTable.AUTHOR_NAME_COLUMN,
                                                                     CommentsTable.DATE_COLUMN, CommentsTable.TEXT_COLUMN };
    private static final String COMMENTS_ORDERING = "\"" + CommentsTable.DATE_COLUMN + "\" ASC";
    private static final int COMMENT_MAX_NESTING = 5;

    private GifLoader gifLoader;
    private Handler handler = new Handler();
    private CommentNode commentsRoot;
    private LinearLayout commentsContainer;

    private ResizableGifImageView gifView;
    private ShareReadyListener shareReadyListener;
    private String descriptionText;

    //control buttons
    private RelativeLayout controlButtonsPane;
    private Animation disappear;
    private Animation appear;

    public static PostDetailsFragment newInstance(long postId) {
        Bundle arguments = new Bundle();
        arguments.putLong(POST_ID_ARGUMENT, postId);

        PostDetailsFragment result = new PostDetailsFragment();
        result.setArguments(arguments);
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        commentsRoot = new CommentNode();

        final View rootView = inflater.inflate(R.layout.post_details_fragment, container, false);

        gifView = ((ResizableGifImageView)rootView.findViewById(R.id.gif_image));
        gifView.setMaxWidth((int) getResources().getDimension(R.dimen.image_max_width));
        gifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toogleControlButtonsVisibility();
            }
        });

        controlButtonsPane = (RelativeLayout)rootView.findViewById(R.id.control_buttons);
        initControlButtons(controlButtonsPane);
        appear = AnimationUtils.loadAnimation(getActivity(), R.anim.appear);
        disappear = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);

        commentsContainer = (LinearLayout)rootView.findViewById(R.id.comments);

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

        gifView.recycle();

        //cancel gif request and other requests
        if (gifLoader != null) {
            gifLoader.cancel(true);
            gifLoader = null;
        }
        VolleyHelper.getInstance().cancelAll(this);
    }

    private void toogleControlButtonsVisibility() {
        if (controlButtonsPane.getVisibility() == View.INVISIBLE) {
            controlButtonsPane.setVisibility(View.VISIBLE);
            controlButtonsPane.startAnimation(appear);
        } else {
            controlButtonsPane.setVisibility(View.INVISIBLE);
            controlButtonsPane.startAnimation(disappear);
        }
    }

    private void initControlButtons(RelativeLayout pane) {
        pane.findViewById(R.id.zoom_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.zoomIn();
            }
        });
        pane.findViewById(R.id.zoom_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.zoomOut();
            }
        });
        pane.findViewById(R.id.left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.moveLeft();
            }
        });
        pane.findViewById(R.id.up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.moveUp();
            }
        });
        pane.findViewById(R.id.right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.moveRight();
            }
        });
        pane.findViewById(R.id.down).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gifView.moveDown();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            shareReadyListener = (ShareReadyListener)activity;
        } catch (ClassCastException cce) {}
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
            case LOAD_COMMENTS_ID :
                uri = Uri.withAppendedPath(DevlifeProviderMetadata.ENTRY_COMMENTS_URI, Long.toString(getPostId()));
                return new CursorLoader(getActivity(), uri, COMMENTS_PROJECTION, null, null, COMMENTS_ORDERING);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int cursorId = cursorLoader.getId();
        if (cursorId == LOAD_POST_ID) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                setContent(cursor);
            }
            cursor.close();
        } else if (cursorId == LOAD_COMMENTS_ID) {
            setComments(cursor);
        }
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
        descriptionText = cursor.getString(cursor.getColumnIndex(PostTable.DESCRIPTION_COLUMN));
        String tmp = getString(R.string.description_html, descriptionText);
        ((TextView)root.findViewById(R.id.post_description)).setText(Html.fromHtml(tmp));
        tmp = getString(R.string.rating_html, cursor.getInt(cursor.getColumnIndex(PostTable.VOTES_COLUMN)));
        ((TextView)root.findViewById(R.id.post_rating)).setText(Html.fromHtml(tmp));
        tmp = getString(R.string.comments_count_html, 0);
        ((TextView)root.findViewById(R.id.post_comments)).setText(Html.fromHtml(tmp));

        //load gif
        gifLoader = new GifLoader();
        gifLoader.execute(cursor.getString(cursor.getColumnIndex(PostTable.GIF_URL_COLUMN)));
    }

    private void setGifFile(String filePath) {
        //UGLYHACK: there is some problems with pist 8680 :(
        if (getPostId() != 8680L) {
            try {
                gifView.setImageFile(filePath);
            } catch (IOException ioException) {
                Toast.makeText(getActivity(), R.string.error_loading_gif, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.error_loading_gif, Toast.LENGTH_LONG).show();
        }

        //activate sharing
        if (shareReadyListener != null) {
            shareReadyListener.onReadyForSharing(descriptionText, filePath);
        }
    }

    private void setCommentsCount(int commentsCount) {
        String tmp = getString(R.string.comments_count_html, commentsCount);
        ((TextView)getView().findViewById(R.id.post_comments)).setText(Html.fromHtml(tmp));
    }

    private void updateComments() {
        GetCommentsRequest request = new GetCommentsRequest(getPostId(), new Response.Listener<GetCommentsResponse>() {
            @Override
            public void onResponse(GetCommentsResponse response) {
                //save comments
                saveComments(response);

                //try to update data about post
                updatePost();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //show comments
                loadCommentsFromCache();

                //hide progress bar
                Activity activity = getActivity();
                if (activity != null) {
                    ((DevlifeBaseActivity)activity).hideProgress();
                }

                //show message
                Toast.makeText(getActivity(), R.string.error_updating_comments, Toast.LENGTH_LONG).show();
            }
        });
        request.setTag(this);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void loadCommentsFromCache() {
        DevlifeBaseActivity activity = (DevlifeBaseActivity)getActivity();
        if (activity != null) {
            activity.getSupportLoaderManager().initLoader(LOAD_COMMENTS_ID, null, this);
        }
    }

    private void setComments(Cursor cursor) {
        int commentsCount = cursor.getCount();
        //set comments count
        setCommentsCount(commentsCount);

        //clean comments
        commentsRoot.clean();

        //set comments from cursor
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CommentNode node = cursor2CommentNode(cursor);
            commentsRoot.addComment(node);

            cursor.moveToNext();
        }

        //show comments
        showComments();
    }

    private void showComments() {
        //clear container
        commentsContainer.removeAllViews();

        //fill container using visitor
        commentsRoot.visit(this);
    }

    @Override
    public void nextNode(CommentNode node, int level) {
        if (level > 0) {
            int leftPadding = Math.min(level - 1, COMMENT_MAX_NESTING) * (int)getResources().getDimension(R.dimen.comment_nesting_padding);

            //comment's header
            TextView header = new TextView(getActivity());
            header.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.post_details_textsize));
            String headerText = getString(R.string.comment_header_html, node.rating,
                                          node.authorName, node.getDate());
            header.setText(Html.fromHtml(headerText));
            header.setPadding(leftPadding, 0, 0, 0);
            commentsContainer.addView(header);

            //comment's text
            TextView text = new TextView(getActivity());
            text.setTextColor(getResources().getColor(R.color.field_value_textcolor));
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.post_details_textsize_big));
            text.setLinksClickable(true);
            text.setAutoLinkMask(Linkify.ALL);
            text.setText(Html.fromHtml(node.text));
            text.setPadding(leftPadding, 0, 0, (int)getResources().getDimension(R.dimen.comment_nesting_padding_bottom));
            commentsContainer.addView(text);
        }
    }

    private CommentNode cursor2CommentNode(Cursor cursor) {
        CommentNode node = new CommentNode();

        node.id = cursor.getLong(cursor.getColumnIndex(CommentsTable._ID));
        node.parentId = cursor.getLong(cursor.getColumnIndex(CommentsTable.PARENT_ID_COLUMN));
        node.rating = cursor.getInt(cursor.getColumnIndex(CommentsTable.VOTE_COUNT_COLUMN));
        node.authorName = cursor.getString(cursor.getColumnIndex(CommentsTable.AUTHOR_NAME_COLUMN));
        node.date = new Date(cursor.getLong(cursor.getColumnIndex(CommentsTable.DATE_COLUMN)));
        node.text = cursor.getString(cursor.getColumnIndex(CommentsTable.TEXT_COLUMN));

        return node;
    }

    private void saveComments(final GetCommentsResponse response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = null;
                if (getActivity() != null) {
                    contentResolver = getActivity().getContentResolver();
                }
                if (contentResolver != null && !response.isErrorOccur()) {
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadCommentsFromCache();
                    }
                });
            }
        }).start();
    }

    private void updatePostCache(final GetPostResponse response) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = null;
                if (getActivity() != null) {
                    contentResolver = getActivity().getContentResolver();
                }
                if (contentResolver != null) {
                    ContentValues values = new ContentValues(5);
                    values.put(PostTable.AUTHOR_COLUMN, response.author);
                    values.put(PostTable.DESCRIPTION_COLUMN, response.description);
                    values.put(PostTable.DATE_COLUMN, response.getDate());
                    values.put(PostTable.VOTES_COLUMN, response.votes);
                    values.put(PostTable.PREVIEW_URL_COLUMN, response.previewURL);

                    contentResolver.update(Uri.withAppendedPath(DevlifeProviderMetadata.POST_ITEMS_URI, Long.toString(response.id)),
                                           values, null, null);
                }
            }
        }).start();
    }

    private void updatePost() {
        GetPostRequest request = new GetPostRequest(getPostId(), new Response.Listener<GetPostResponse>() {
            @Override
            public void onResponse(GetPostResponse response) {
                //update post cache and view
                if (!response.isErrorOccur()) {
                    //update cache
                    updatePostCache(response);

                    //update view
                    if (isAdded()) {
                        ((TextView)getView().findViewById(R.id.author)).setText(response.author);
                        ((TextView)getView().findViewById(R.id.date)).setText(DATE_FORMAT.format(response.getDate()));
                        String tmp = getString(R.string.description_html, response.description);
                        ((TextView)getView().findViewById(R.id.post_description)).setText(Html.fromHtml(tmp));
                        tmp = getString(R.string.rating_html, response.votes);
                        ((TextView)getView().findViewById(R.id.post_rating)).setText(Html.fromHtml(tmp));
                    }
                }

                //hide progress bar
                Activity activity = getActivity();
                if (activity != null) {
                    ((DevlifeBaseActivity)activity).hideProgress();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //just hide progress bar
                Activity activity = getActivity();
                if (activity != null) {
                    ((DevlifeBaseActivity)activity).hideProgress();
                }
            }
        });
        request.setTag(this);
        VolleyHelper.getInstance().addRequest(request);
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

    public interface ShareReadyListener {
        public void onReadyForSharing(String description, String filePath);
    }
}
