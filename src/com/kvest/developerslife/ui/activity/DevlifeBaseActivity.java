package com.kvest.developerslife.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import com.bugsense.trace.BugSenseHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public class DevlifeBaseActivity extends ActionBarActivity {
    private static final String BUGSENS_APIKEY = "8be8af41";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BugSenseHandler.initAndStartSession(this, BUGSENS_APIKEY);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    public void showProgress() {
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void hideProgress() {
        setSupportProgressBarIndeterminateVisibility(false);
    }
}
