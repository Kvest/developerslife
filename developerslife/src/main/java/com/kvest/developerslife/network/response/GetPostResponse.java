package com.kvest.developerslife.network.response;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 02.01.14
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
public class GetPostResponse extends BaseResponse {
    public static final String DATE_TIME_FORMAT = "MMM dd, yyyy KK:mm:ss aa";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);

    @SerializedName("id")
    public long id;
    @SerializedName("description")
    public String description;
    @SerializedName("votes")
    public int votes;
    @SerializedName("author")
    public String author;
    @SerializedName("date")
    public String date;
    @SerializedName("gifURL")
    public String gifURL;
    @SerializedName("previewURL")
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
