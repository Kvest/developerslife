package com.kvest.developerslife.network.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
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
            return Response.success(getRandomPostResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
