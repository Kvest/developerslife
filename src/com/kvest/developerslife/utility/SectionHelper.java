package com.kvest.developerslife.utility;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 20.12.13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public class SectionHelper {
    public static final int UNKNOWN_SECTION_ID = -1;
    public static final int LATEST_SECTION_ID = 0;
    public static final int HOT_SECTION_ID = 1;
    public static final int TOP_SECTION_ID = 2;
    public static final String UNKNOWN_SECTION_NAME = "unknown";
    public static final String LATEST_SECTION_NAME = "latest";
    public static final String HOT_SECTION_NAME = "hot";
    public static final String TOP_SECTION_NAME = "top";

    private SectionHelper() {}

    public static int name2Id(String name) {
        if (LATEST_SECTION_NAME.equals(name)) {
            return LATEST_SECTION_ID;
        } else if (HOT_SECTION_NAME.equals(name)) {
            return HOT_SECTION_ID;
        } if (TOP_SECTION_NAME.equals(name)) {
            return TOP_SECTION_ID;
        } else {
            return UNKNOWN_SECTION_ID;
        }
    }

    public static String id2Name(int id) {
        switch (id) {
            case LATEST_SECTION_ID : return LATEST_SECTION_NAME;
            case HOT_SECTION_ID : return HOT_SECTION_NAME;
            case TOP_SECTION_ID : return TOP_SECTION_NAME;
            default : return UNKNOWN_SECTION_NAME;
        }
    }
}
