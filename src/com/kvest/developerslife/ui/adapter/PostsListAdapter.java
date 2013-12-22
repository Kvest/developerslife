package com.kvest.developerslife.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.developerslife.R;
import com.kvest.developerslife.network.VolleyHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 22.12.13
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class PostsListAdapter extends SimpleCursorAdapter implements SimpleCursorAdapter.ViewBinder {
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private Context context;

    public PostsListAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        this.context = context;
        setViewBinder(this);
    }

    @Override
    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        if (view.getId() == R.id.date) {
            Date date = new Date(cursor.getLong(columnIndex));
            ((TextView)view).setText(DATE_FORMAT.format(date));
            return true;
        }
        if (view.getId() == R.id.entry_number) {
            ((TextView)view).setText(getString(R.string.entry_number, cursor.getLong(columnIndex)));
            return true;
        }
        if (view.getId() == R.id.post_description) {
            ((TextView) view).setText(Html.fromHtml(getString(R.string.description, cursor.getString(columnIndex))));
            return true;
        }
        if (view.getId() == R.id.preview_image) {
            ((NetworkImageView)view).setDefaultImageResId(R.drawable.ic_launcher);
            ((NetworkImageView)view).setImageUrl(cursor.getString(columnIndex), VolleyHelper.getInstance().getImageLoader());
            return true;
        }

        return false;
    }

    private String getString(int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }
}
