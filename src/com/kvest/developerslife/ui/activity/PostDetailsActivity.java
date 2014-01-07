package com.kvest.developerslife.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.kvest.developerslife.R;
import com.kvest.developerslife.ui.fragment.PostDetailsFragment;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 22:00
 * To change this template use File | Settings | File Templates.
 */
public class PostDetailsActivity extends DevlifeBaseActivity implements PostDetailsFragment.ShareReadyListener {
    public static final String SHARE_CONTENT_TYPE = "image/gif";
    public static final String POST_ID_EXTRA = "com.kvest.developerslife.ui.activity.PostDetailsActivity.POST_ID";
    private static final int SHARE_MENU_ID = 0;

    private long postId;

    private String description = null;
    private String gifFilePath = null;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (canShare()) {
            MenuItemCompat.setShowAsAction(menu.add(0, SHARE_MENU_ID, 0, getString(R.string.share))
                          .setIcon(android.R.drawable.ic_menu_share), MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case SHARE_MENU_ID : shareGig(); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean canShare() {
        return (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(gifFilePath));
    }

    private void shareGig() {
        if (!canShare()) {
            Toast.makeText(this, R.string.share_error, Toast.LENGTH_LONG).show();
            return;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(SHARE_CONTENT_TYPE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, description);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + gifFilePath));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onReadyForSharing(String description, String filePath) {
        this.description = description;
        this.gifFilePath = filePath;

        supportInvalidateOptionsMenu();
    }
}
