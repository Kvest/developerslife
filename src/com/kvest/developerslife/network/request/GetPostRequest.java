package com.kvest.developerslife.network.request;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.kvest.developerslife.application.DevlifeApplication;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.Urls;
import com.kvest.developerslife.network.response.GetPostResponse;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 02.01.14
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
public class GetPostRequest extends JsonRequest<GetPostResponse> {
    private static Gson gson = new Gson();

    public GetPostRequest(long postId, Response.Listener<GetPostResponse> listener,
                          Response.ErrorListener errorListener) {
        super(Method.GET, Urls.getPostUrl(postId), null, listener, errorListener);
    };

    @Override
    protected Response<GetPostResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetPostResponse getPostResponse = gson.fromJson(json, GetPostResponse.class);

            //save data
            if (!getPostResponse.isErrorOccur()) {
                updatePostCache(getPostResponse);
            }

            return Response.success(getPostResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private void updatePostCache(GetPostResponse response) {
        ContentValues values = new ContentValues(5);
        values.put(PostTable.AUTHOR_COLUMN, response.author);
        values.put(PostTable.DESCRIPTION_COLUMN, response.description);
        values.put(PostTable.DATE_COLUMN, response.getDate());
        values.put(PostTable.VOTES_COLUMN, response.votes);
        values.put(PostTable.PREVIEW_URL_COLUMN, response.previewURL);

        ContentResolver contentResolver = DevlifeApplication.getApplication().getContentResolver();
        contentResolver.update(Uri.withAppendedPath(DevlifeProviderMetadata.POST_ITEMS_URI, Long.toString(response.id)), values, null, null);

    }
}
