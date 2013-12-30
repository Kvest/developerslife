package com.kvest.developerslife.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 12/30/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResizableGifImageView extends GifImageView {
    public ResizableGifImageView(Context context) {
        super(context);
    }

    public ResizableGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizableGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();

        if(d!=null){
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
