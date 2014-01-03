package com.kvest.developerslife.utility;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 20.12.13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public class CategoryHelper {
    public static final int UNKNOWN_CATEGORY_ID = -1;
    public static final int LATEST_CATEGORY_ID = 0;
    public static final int TOP_CATEGORY_ID = 1;
    public static final int HOT_CATEGORY_ID = 2;
    public static final String UNKNOWN_CATEGORY_NAME = "unknown";
    public static final String LATEST_CATEGORY_NAME = "latest";
    public static final String HOT_CATEGORY_NAME = "hot";
    public static final String TOP_CATEGORY_NAME = "top";
    public static final int CATEGORIES_COUNT = 3;

    private CategoryHelper() {}

    public static int name2Id(String name) {
        if (LATEST_CATEGORY_NAME.equals(name)) {
            return LATEST_CATEGORY_ID;
        } else if (HOT_CATEGORY_NAME.equals(name)) {
            return HOT_CATEGORY_ID;
        } if (TOP_CATEGORY_NAME.equals(name)) {
            return TOP_CATEGORY_ID;
        } else {
            return UNKNOWN_CATEGORY_ID;
        }
    }

    public static String id2Name(int id) {
        switch (id) {
            case LATEST_CATEGORY_ID : return LATEST_CATEGORY_NAME;
            case HOT_CATEGORY_ID : return HOT_CATEGORY_NAME;
            case TOP_CATEGORY_ID : return TOP_CATEGORY_NAME;
            default : return UNKNOWN_CATEGORY_NAME;
        }
    }
}
