package com.kvest.developerslife.network;

import com.kvest.developerslife.utility.CategoryHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 19.12.13
 * Time: 22:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class Urls {
    public static String DEVLIFE_URL = " http://developerslife.ru";

    public static final String PAGE_SIZE_PARAM = "pageSize";

    public static String getPostsUrl(int category, int page, int pageSize) {
        return DEVLIFE_URL + "/" + CategoryHelper.id2Name(category) + "/" + page + "?json=true&" +
               PAGE_SIZE_PARAM + "=" + pageSize;
    }
}
