package com.kvest.developerslife.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.kvest.developerslife.R;
import com.kvest.developerslife.ui.fragment.PostDetailsFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
public class PostDetailsActivity extends DevlifeBaseActivity {
    public static final String POST_ID_EXTRA = "com.kvest.developerslife.ui.activity.PostDetailsActivity.POST_ID";

    private long postId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        postId = getIntent().getLongExtra(POST_ID_EXTRA, -1);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            try {
                transaction.add(R.id.fragment_container, PostDetailsFragment.newInstance(postId));
            } finally {
                transaction.commit();
            }
        }
    }
}
