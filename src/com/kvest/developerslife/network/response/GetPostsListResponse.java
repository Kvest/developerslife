package com.kvest.developerslife.network.response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.12.13
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class GetPostsListResponse {
    public static final String TWITTER_DATE_TIME_FORMAT = "MMM dd, yyyy KK:mm:ss aa";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(TWITTER_DATE_TIME_FORMAT, Locale.ENGLISH);

    public ArrayList<Post> result;

    public static class Post {
        public long id;
        public String description;
        public int votes;
        public String author;
        public String date;
        public String gifURL;
        public String previewURL;

        public long getDate() {
            Date d = null;
            try {
                d = dateFormat.parse(date);
            } catch (ParseException pe) {};

            if (d != null) {
                return d.getTime();
            } else {
                return 0;
            }
        }
    }
}
