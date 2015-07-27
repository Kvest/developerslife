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
                savePosts(getPostsListResponse, category);
                CategoryHelper.IS_LIST_FINISHED[category] = getPostsListResponse.result.size() < Constants.DEFAULT_PAGE_SIZE;
            }
            return Response.success(getPostsListResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    private void savePosts(GetPostsListResponse response, final int category) {
        if (response.result.size() > 0) {
            ContentValues[] values = new ContentValues[response.result.size()];
            for (int i = 0; i < response.result.size(); ++i) {
                GetPostsListResponse.Post post = response.result.get(i);

                values[i] = new ContentValues(6);
                values[i].put(PostTable._ID, post.id);
                values[i].put(PostTable.AUTHOR_COLUMN, post.author);
                values[i].put(PostTable.DESCRIPTION_COLUMN, post.description);
                values[i].put(PostTable.DATE_COLUMN, post.getDate());
                values[i].put(PostTable.VOTES_COLUMN, post.votes);
                values[i].put(PostTable.GIF_URL_COLUMN, post.gifURL);
                values[i].put(PostTable.PREVIEW_URL_COLUMN, post.previewURL);
            }

            ContentResolver resolver = DevlifeApplication.getApplication().getContentResolver();
            switch (category) {
                case CategoryHelper.LATEST_CATEGORY_ID :
                    resolver.bulkInsert(DevlifeProviderMetadata.LATEST_POSTS_ITEMS_URI, values);
                    break;
                case CategoryHelper.HOT_CATEGORY_ID :
                    resolver.bulkInsert(DevlifeProviderMetadata.HOT_POSTS_ITEMS_URI, values);
                    break;
                case CategoryHelper.TOP_CATEGORY_ID :
                    resolver.bulkInsert(DevlifeProviderMetadata.TOP_POSTS_ITEMS_URI, values);
                    break;
            }
        }
    }
}
