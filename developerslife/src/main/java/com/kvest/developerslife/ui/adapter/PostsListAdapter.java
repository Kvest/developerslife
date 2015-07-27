package com.kvest.developerslife.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.volley.toolbox.NetworkImageView;
import com.kvest.developerslife.R;
import com.kvest.developerslife.datastorage.table.PostTable;
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
public class PostsListAdapter extends CursorAdapter {
    private static final String DATE_FORMAT_PATTERN = "dd.MM.yyyy";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    private int maxImageWidth;

    private int authorColumnIndex = -1;
    private int dateColumnIndex = -1;
    private int entryNumberColumnIndex = -1;
    private int postDescriptionColumnIndex = -1;
    private int previewImageColumnIndex = -1;

    public PostsListAdapter(Context context, int flags) {
        super(context, null, flags);
        maxImageWidth = (int)context.getResources().getDimension(R.dimen.gif_max_width);
    }

    private String getString(Context context, int resId, Object... formatArgs) {
        return context.getString(resId, formatArgs);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        //create view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.posts_list_item, viewGroup, false);

        //create holder
        ViewHolder holder = new ViewHolder();
        holder.author = (TextView)view.findViewById(R.id.author);
        holder.date = (TextView)view.findViewById(R.id.date);
        holder.entryNumber = (TextView)view.findViewById(R.id.entry_number);
        holder.postDescription = (TextView)view.findViewById(R.id.post_description);
        holder.previewImage = (NetworkImageView)view.findViewById(R.id.preview_image);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder)view.getTag();

        if (!isColumnIndexesCalculated()) {
            calculateColumnIndexes(cursor);
        }

        holder.author.setText(cursor.getString(authorColumnIndex));

        Date date = new Date(cursor.getLong(dateColumnIndex));
        holder.date.setText(DATE_FORMAT.format(date));

        holder.entryNumber.setText(getString(context, R.string.entry_number, cursor.getLong(entryNumberColumnIndex)));

        holder.postDescription.setText(Html.fromHtml(getString(context, R.string.description_html, cursor.getString(postDescriptionColumnIndex))));

        holder.previewImage.setImageUrl(cursor.getString(previewImageColumnIndex), VolleyHelper.getInstance().getImageLoader());
        holder.previewImage.setMaxWidth(maxImageWidth);
    }

    private boolean isColumnIndexesCalculated() {
        return (authorColumnIndex >= 0);
    }

    private void calculateColumnIndexes(Cursor cursor) {
        authorColumnIndex = cursor.getColumnIndex(PostTable.AUTHOR_COLUMN);
        dateColumnIndex = cursor.getColumnIndex(PostTable.DATE_COLUMN);
        entryNumberColumnIndex = cursor.getColumnIndex(PostTable._ID);
        postDescriptionColumnIndex = cursor.getColumnIndex(PostTable.DESCRIPTION_COLUMN);
        previewImageColumnIndex = cursor.getColumnIndex(PostTable.PREVIEW_URL_COLUMN);
    }

    private static class ViewHolder {
        public TextView author;
        public TextView date;
        public TextView entryNumber;
        public TextView postDescription;
        public NetworkImageView previewImage;
    }
}
