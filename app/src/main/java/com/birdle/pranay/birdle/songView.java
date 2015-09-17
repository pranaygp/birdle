package com.birdle.pranay.birdle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.widget.TextView;
import android.util.Log;
import  android.util.EventLogTags;

@Deprecated

public class songView extends TextView implements
        GestureDetector.OnGestureListener  {


    private String title, artist; // TODO: use a default from R.string...
    private int fontColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private int paddingLeft=10;
    private int paddingRight=10;
    private int paddingTop=20;
    private int paddingBottom=20;

    private Song song;

    public songView(Context context) {
        super(context);
        init(null, 0, null);
        //because this shouldn't happen
        // //throw new Exception("SongView did not recieve a song");
       // Log.e(TAG, "songView song was not passed in");
    }

    public songView(Context context, Song song) {
        super(context);
        init(null, 0, song);

    }

    public songView(Context context, AttributeSet attrs, Song song) {
        super(context, attrs);
        init(attrs, 0, song);
    }

    public songView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle, song);
    }

    private void init(AttributeSet attrs, int defStyle, Song inSong) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.songView, defStyle, 0);

        this.song = inSong;

        title = song.getTitle();
        title = "title"; // DEBUG TODO: remove this line
        artist= song.getArtist();
        artist = "artist"; // DEBUG TODO: remove this line
        fontColor = a.getColor(
                R.styleable.songView_exampleColor,
                fontColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.songView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.songView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.songView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(fontColor);
        mTextWidth = mTextPaint.measureText(title);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(title,
                paddingLeft + (contentWidth - mTextWidth) / 4,
                paddingTop + (contentHeight + mTextHeight) / 4,
                mTextPaint);

        canvas.drawText(artist,
                paddingLeft + (contentWidth - mTextWidth) / 4,
                paddingTop + (contentHeight + mTextHeight) * 2/ 4,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
    }



    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getFontColor() {
        return fontColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param color The example color attribute value to use.
     */
    public void setExampleColor(int color) {
        fontColor = color;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
