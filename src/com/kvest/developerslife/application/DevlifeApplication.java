package com.kvest.developerslife.application;

import android.app.Application;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 2/7/14
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DevlifeApplication extends Application {
    private static DevlifeApplication applicaion;

    public static DevlifeApplication getApplication() {
        return applicaion;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicaion = this;
    }
}
