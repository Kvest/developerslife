package com.kvest.developerslife.utility;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 23.12.13
 * Time: 23:33
 * To change this template use File | Settings | File Templates.
 */
public class FileUtility {
    public static String getFileName(String filePath, boolean withExtension) {
        File file = new File(filePath);
        if (withExtension) {
            return file.getName();
        } else {
            String name = file.getName();
            int pos = name.lastIndexOf(".");
            if (pos > 0) {
                name = name.substring(0, pos);
            }
            return name;
        }
    }

    public static boolean createDirIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("FileUtility", "Problem creating \"" + path + "\" folder");
                return false;
            }
        }

        return true;
    }

    public static boolean fileExists(String fullFilePath) {
        File file = new File(fullFilePath);
        return file.exists();
    }
}
