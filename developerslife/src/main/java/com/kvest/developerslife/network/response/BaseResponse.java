package com.kvest.developerslife.network.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 02.01.14
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class BaseResponse {
    @SerializedName("error")
    protected String error;

    public boolean isErrorOccur() {
        return (error != null);
    }

    public String getError() {
        return error;
    }
}
