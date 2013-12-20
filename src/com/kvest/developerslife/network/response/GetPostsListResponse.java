package com.kvest.developerslife.network.response;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.12.13
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class GetPostsListResponse {
    public ArrayList<Post> result;

    public static class Post {
        public long id;
        public String description;
        public int votes;
        public String author;
        public String date;
        public String gifURL;
        public String previewURL;
    }
}
