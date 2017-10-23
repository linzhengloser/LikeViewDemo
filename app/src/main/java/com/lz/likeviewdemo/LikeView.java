package com.lz.likeviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 *     author : linzheng
 *     e-mail : 1007687534@qq.com
 *     time   : 2017/10/13
 *     desc   : 点赞 View
 *     version: 1.0
 * </pre>
 */
public class LikeView extends View {

    private static final int TEXT_PADDING = 5;

    private static final int TEXT_HORIZONTAL_PADDING = 10;

    Paint mLikeBitmapPaint;

    Paint mTextPaint;

    Bitmap mLikeBitmap;

    Rect mLikeSrcRect;

    Rect mLikeBitmapDstRect;

    Rect mTextBoundRect;

    int mWidth, mHeight;

    //当前动画值
    float mCurrentAnimationValue;

    //用于记录上一个值的 ArrayList
    ArrayList<Integer> mLastValueList = new ArrayList<>();

    //用于记录当前值的 ArrayList
    ArrayList<Integer> mCurrentValueList = new ArrayList<>();

    //上一个值和当前值
    int mLastValue, mCurrentValue;

    int mTotalHeight;

    {
        mCurrentValue = 2333;
        mLikeBitmapPaint = new Paint();
        mLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.like);
        mLikeSrcRect = new Rect(0, 0, mLikeBitmap.getWidth(), mLikeBitmap.getHeight());
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(40);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(10);
        mTextBoundRect = new Rect();
        mTextPaint.getTextBounds("0", 0, 1, mTextBoundRect);
        mTotalHeight = mTextBoundRect.height() + TEXT_HORIZONTAL_PADDING;
        computeDigit(mCurrentValue, mLastValueList);
        computeDigit(mCurrentValue, mCurrentValueList);

    }

    public LikeView(Context context) {
        super(context);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制点赞图标
        canvas.drawBitmap(mLikeBitmap, mLikeSrcRect, mLikeBitmapDstRect, mLikeBitmapPaint);
        int startX = mWidth / 2 - mTextBoundRect.width() / 2;
        int startY = mHeight / 2 + mTextBoundRect.height() / 2;
        int currentValue, lastValue, x;
        for (int i = 0; i < mCurrentValueList.size(); i++) {
            currentValue = mCurrentValueList.get(i);
            lastValue = mLastValueList.get(i);
            x = startX + i * (mTextBoundRect.width() + TEXT_PADDING);
            if (lastValue == currentValue) {
                mTextPaint.setAlpha(255);
                canvas.drawText(String.valueOf(currentValue), x, startY, mTextPaint);
            } else if (currentValue > lastValue) {
                drawTextIn(String.valueOf(currentValue), x, (int) (startY + (mTotalHeight - mTotalHeight * mCurrentAnimationValue)), canvas);
                drawTextOut(String.valueOf(lastValue), x, (int) (startY - mTotalHeight * mCurrentAnimationValue), canvas);
            } else {
                drawTextIn(String.valueOf(currentValue), x, (int) (startY - (mTotalHeight - mTotalHeight * mCurrentAnimationValue)), canvas);
                drawTextOut(String.valueOf(lastValue), x, (int) (startY+mTotalHeight*mCurrentAnimationValue), canvas);
            }
        }
    }

    private void drawTextIn(String text, int x, int y, Canvas canvas) {
        mTextPaint.setAlpha((int) (mCurrentAnimationValue * 255));
        canvas.drawText(text, x, y, mTextPaint);
    }

    private void drawTextOut(String text, int x, int y, Canvas canvas) {
        mTextPaint.setAlpha((int) (255 - mCurrentAnimationValue * 255));
        canvas.drawText(text, x, y, mTextPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mLikeBitmapDstRect = new Rect(dpToPixel(5), mHeight / 2 - dpToPixel(30) / 2, dpToPixel(35), mHeight / 2 + dpToPixel(30) / 2);
    }

    /**
     * 将一个整数的每位的值保存在 List 中
     */
    private void computeDigit(int value, List<Integer> list) {
        list.clear();
        if (value == 0) {
            list.add(0);
        }
        while (value > 0) {
            list.add(0, value % 10);
            value = value / 10;
        }
    }

    public void add() {
        mLastValue = mCurrentValue;
        mCurrentValue++;
        computeDigit(mCurrentValue, mCurrentValueList);
        computeDigit(mLastValue, mLastValueList);
        startAnimation();
    }

    public void reduce() {
        mLastValue = mCurrentValue;
        mCurrentValue--;
        computeDigit(mCurrentValue, mCurrentValueList);
        computeDigit(mLastValue, mLastValueList);
        startAnimation();
    }


    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(animator1 -> {
            mCurrentAnimationValue = (float) animator1.getAnimatedValue();
            postInvalidate();
        });
        animator.setDuration(500);
        animator.start();
    }

    public static int dpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

}
