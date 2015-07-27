package com.kvest.developerslife.utility;

import android.os.Environment;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class Constants {
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final String CACHE_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator + "devlife";
    public static final String GIFS_CACHE_DIRECTORY = CACHE_DIRECTORY + File.separator + "gifs" + File.separator;
}
