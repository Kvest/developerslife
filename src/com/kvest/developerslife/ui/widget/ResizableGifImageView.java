package com.kvest.developerslife.ui.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    private int maxWidth = Integer.MAX_VALUE;

//    private Matrix matrix;
//
//    private ScaleGestureDetector scaleGestureDetector;
//    private float scaleFactor = 1.f;
//
//    private float oldX, oldY;
//    private float density;

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
//        matrix = new Matrix();
//        setScaleType(ScaleType.MATRIX);
//
//        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
//
//        //get density
//        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
//        density = metrics.density;
    }

    public void stop() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            ((GifDrawable)drawable).stop();
        }
    }

    public void start() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            ((GifDrawable)drawable).start();
        }
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
//        float scaleFactor = getWidth() / (float)gifFromPath.getIntrinsicWidth();
//        matrix.setScale(scaleFactor, scaleFactor);
//        setImageMatrix(matrix);
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

//    @Override
//    public boolean onTouchEvent (MotionEvent event) {
//        int action = MotionEventCompat.getActionMasked(event);
//
//        scaleGestureDetector.onTouchEvent(event);
//
//        if (action == MotionEvent.ACTION_MOVE) {
//            matrix.preTranslate((event.getX() - oldX) / density, (event.getY() - oldY) / density);
//            oldX = event.getX();
//            oldY = event.getY();
//            setImageMatrix(matrix);
//            return true;
//        } else if (action == MotionEvent.ACTION_DOWN) {
//            oldX = event.getX();
//            oldY = event.getY();
//            return true;
//        }
//
//        return super.onTouchEvent(event);
//    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor();
//
//            // Don't let the object get too small or too large.
//            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
//
//            matrix.preScale(detector.getScaleFactor(), detector.getScaleFactor());
//            setImageMatrix(matrix);
//            return true;
//        }
//    }
}
