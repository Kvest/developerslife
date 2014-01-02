package com.kvest.developerslife.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
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
            return Response.success(gson.fromJson(json, GetPostResponse.class), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
