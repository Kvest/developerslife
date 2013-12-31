package com.kvest.developerslife.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
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
            return Response.success(gson.fromJson(json, GetCommentsResponse.class), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
