package com.kvest.developerslife.network.request;

import android.content.ContentResolver;
import android.content.ContentValues;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.kvest.developerslife.application.DevlifeApplication;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.CommentsTable;
import com.kvest.developerslife.network.Urls;
import com.kvest.developerslife.network.response.GetCommentsResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.13
 * Time: 20:47
 * To change this template use File | Settings | File Templates.
 */
public class GetCommentsRequest extends JsonRequest<GetCommentsResponse> {
    private static Gson gson = new Gson();

    public GetCommentsRequest(long postId, Response.Listener<GetCommentsResponse> listener,
                              Response.ErrorListener errorListener) {
        super(Method.GET, Urls.getCommentsUrl(postId), null, listener, errorListener);
    };

    @Override
    protected Response<GetCommentsResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetCommentsResponse getCommentsResponse = gson.fromJson(json, GetCommentsResponse.class);

            //save data
            if (!getCommentsResponse.isErrorOccur())  {
                saveComments(getCommentsResponse);
            }

            return Response.success(getCommentsResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private void saveComments(GetCommentsResponse response) {
        if (response.comments.size() > 0) {
            ContentValues[] values = new ContentValues[response.comments.size()];

            for (int i = 0; i < response.comments.size(); ++i) {
                GetCommentsResponse.Comment comment = response.comments.get(i);

                values[i] = new ContentValues(8);
                values[i].put(CommentsTable._ID, comment.id);
                values[i].put(CommentsTable.PARENT_ID_COLUMN, comment.parentId);
                values[i].put(CommentsTable.ENTRY_ID_COLUMN, comment.entryId);
                values[i].put(CommentsTable.TEXT_COLUMN, comment.text);
                values[i].put(CommentsTable.DATE_COLUMN, comment.getDate());
                values[i].put(CommentsTable.AUTHOR_ID_COLUMN, comment.authorId);
                values[i].put(CommentsTable.AUTHOR_NAME_COLUMN, comment.authorName);
                values[i].put(CommentsTable.VOTE_COUNT_COLUMN, comment.voteCount);
            }

            ContentResolver contentResolver = DevlifeApplication.getApplication().getContentResolver();
            contentResolver.bulkInsert(DevlifeProviderMetadata.COMMENTS_URI, values);
        }
    }
}
