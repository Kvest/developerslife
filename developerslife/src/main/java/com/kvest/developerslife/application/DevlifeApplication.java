package com.kvest.developerslife.application;

import android.app.Application;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.kvest.developerslife.BuildConfig;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 2/7/14
 * Time: 6:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class DevlifeApplication extends Application {
    private static final String BUGSENSE_API_KEY = "8be8af41";
    private static DevlifeApplication applicaion;

    public static DevlifeApplication getApplication() {
        return applicaion;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            BugSenseHandler.initAndStartSession(this, BUGSENSE_API_KEY);
        }

        applicaion = this;

        //init fresco
        Fresco.initialize(this);
    }
}
