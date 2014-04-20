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
import com.kvest.developerslife.datastorage.table.PostTable;
import com.kvest.developerslife.network.Urls;
import com.kvest.developerslife.network.response.GetRandomPostResponse;

import java.io.UnsupportedEncodingException;


/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 3/19/14
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetRandomPostRequest extends JsonRequest<GetRandomPostResponse> {
    private static Gson gson = new Gson();

    public GetRandomPostRequest(Response.Listener<GetRandomPostResponse> listener,  Response.ErrorListener errorListener) {
        super(Method.GET, Urls.getRandomPostUrl(), null, listener, errorListener);
    };

    @Override
    protected Response<GetRandomPostResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetRandomPostResponse getRandomPostResponse = gson.fromJson(json, GetRandomPostResponse.class);

            //save result in case of success request
            if (!getRandomPostResponse.isErrorOccur()) {
                savePost(getRandomPostResponse);
            }

            return Response.success(getRandomPostResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private void savePost(GetRandomPostResponse response) {
        //create ContentValues
        ContentValues values = new ContentValues(6);
        values.put(PostTable._ID, response.id);
        values.put(PostTable.AUTHOR_COLUMN, response.author);
        values.put(PostTable.DESCRIPTION_COLUMN, response.description);
        values.put(PostTable.DATE_COLUMN, response.getDate());
        values.put(PostTable.VOTES_COLUMN, response.votes);
        values.put(PostTable.GIF_URL_COLUMN, response.gifURL);
        values.put(PostTable.PREVIEW_URL_COLUMN, response.previewURL);

        //save post
        ContentResolver resolver = DevlifeApplication.getApplication().getContentResolver();
        resolver.insert(DevlifeProviderMetadata.RANDOM_POSTS_ITEMS_URI, values);
    }
}
