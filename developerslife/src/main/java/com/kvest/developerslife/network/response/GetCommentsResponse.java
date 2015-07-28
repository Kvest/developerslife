package com.kvest.developerslife.network.response;

import com.google.gson.annotations.SerializedName;

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
public class GetCommentsResponse extends BaseResponse {
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);

    @SerializedName("comments")
    public ArrayList<Comment> comments;

    public static class Comment {
        @SerializedName("id")
        public long id;
        @SerializedName("parentId")
        public long parentId;
        @SerializedName("entryId")
        public long entryId;
        @SerializedName("text")
        public String text;
        @SerializedName("date")
        public String date;
        @SerializedName("authorId")
        public long authorId;
        @SerializedName("authorName")
        public String authorName;
        @SerializedName("voteCount")
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
