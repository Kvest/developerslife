package com.kvest.developerslife.network.response;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 02.01.14
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class BaseResponse {
    protected String error;

    public boolean isErrorOccur() {
        return (error != null);
    }

    public String getError() {
        return error;
    }
}
