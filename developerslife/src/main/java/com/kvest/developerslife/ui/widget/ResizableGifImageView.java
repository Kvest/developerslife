package com.kvest.developerslife.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 12/30/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResizableGifImageView extends GifImageView {
    private static final float SCALE_STEP = 0.1f;
    private static final int MOVE_STEP = 5;
    private static final float MAX_SCALE_FACTOR = 2.5f;
    private static final float MIN_SCALE_FACTOR = 0.3f;

    private int maxWidth = Integer.MAX_VALUE;

    private Matrix matrix;
    private float density;
    private float baseScaleFactor;
    private float scaleFactor;

    public ResizableGifImageView(Context context) {
        super(context);
        init(context);
    }

    public ResizableGifImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ResizableGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        matrix = new Matrix();
        scaleFactor = 1.0f;
        setScaleType(ScaleType.MATRIX);

        //get density
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        density = metrics.density;
    }

    public void zoomIn() {
        if (scaleFactor >= MAX_SCALE_FACTOR) {
            return;
        }
        scaleFactor += SCALE_STEP;
        matrix.setScale(scaleFactor * baseScaleFactor, scaleFactor * baseScaleFactor);

        setImageMatrix(matrix);
    }

    public void zoomOut() {
        if (scaleFactor <= MIN_SCALE_FACTOR) {
            return;
        }
        scaleFactor -= SCALE_STEP;
        matrix.setScale(scaleFactor * baseScaleFactor, scaleFactor * baseScaleFactor);

        setImageMatrix(matrix);
    }

    public void moveLeft() {
        matrix.preTranslate(MOVE_STEP * density, 0);
        setImageMatrix(matrix);
    }

    public void moveRight() {
        matrix.preTranslate(-MOVE_STEP * density, 0);
        setImageMatrix(matrix);
    }

    public void moveUp() {
        matrix.preTranslate(0, MOVE_STEP * density);
        setImageMatrix(matrix);
    }

    public void moveDown() {
        matrix.preTranslate(0, -MOVE_STEP * density);
        setImageMatrix(matrix);
    }

    public void recycle() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            ((GifDrawable)drawable).recycle();
        }
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setImageFile(String filePath) throws IOException {
        GifDrawable gifFromPath = new GifDrawable(filePath);
        setImageDrawable(gifFromPath);

        //scale image to fit all view size
        baseScaleFactor = getWidth() / (float)gifFromPath.getIntrinsicWidth();
        matrix.setScale(scaleFactor * baseScaleFactor, scaleFactor * baseScaleFactor);
        setImageMatrix(matrix);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        Drawable d = getDrawable();

        if(d!=null){
            // ceil not round - avoid thin vertical gaps along the left/right edges
            int width = Math.min(MeasureSpec.getSize(widthMeasureSpec), getMaxWidth());
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
