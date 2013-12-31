package com.kvest.developerslife.network.response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 30.12.13
 * Time: 20:48
 * To change this template use File | Settings | File Templates.
 */
public class GetCommentsResponse {
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);

    public ArrayList<Comment> comments;

    public static class Comment {
        public long id;
        public long parentId;
        public long entryId;
        public String text;
        public String date;
        public long authorId;
        public String authorName;
        public int voteCount;

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
