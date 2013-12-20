package com.kvest.developerslife.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetPostsListRequest;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState == null) {
            initVolley();
        }

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void initVolley() {
        VolleyHelper.getInstance().init(getApplicationContext());
    }

    private void test() {
        GetPostsListRequest request = new GetPostsListRequest(0, 0, new Response.Listener<Void>() {
            @Override
            public void onResponse(Void response) {
                Log.d("KVEST_TAG", "all is ok");
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("KVEST_TAG", "error=" + error.getMessage());
            }
        });
        request.setTag("test");
        VolleyHelper.getInstance().addRequest(request);
    }
}
