package com.kvest.developerslife.network.request;

import android.util.Log;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.kvest.developerslife.network.Urls;
import com.kvest.developerslife.network.response.GetPostsListResponse;
import com.kvest.developerslife.utility.CategoryHelper;
import com.kvest.developerslife.utility.Constants;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.12.13
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class GetPostsListRequest extends JsonRequest<GetPostsListResponse> {
    private static Gson gson = new Gson();

    private int category;

    public GetPostsListRequest(int category, int page, Response.Listener<GetPostsListResponse> listener,
                               Response.ErrorListener errorListener) {
        super(Method.GET, Urls.getPostsUrl(category, page, Constants.DEFAULT_PAGE_SIZE), null, listener, errorListener);
        this.category = category;
    }

    @Override
    protected Response<GetPostsListResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            //get string response
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            GetPostsListResponse getPostsListResponse = gson.fromJson(json, GetPostsListResponse.class);
            if (!getPostsListResponse.isErrorOccur()) {
                CategoryHelper.IS_LIST_FINISHED[category] = getPostsListResponse.result.size() < Constants.DEFAULT_PAGE_SIZE;
            }
            return Response.success(getPostsListResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
