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

    Paint mLikeBitmapPaint;

    Paint mTextPaint;

    Bitmap mLikeBitmap;

    Rect mLikeSrcRect;

    Rect mLikeDstRect;

    Rect mTextBoundRect;

    int mWidth, mHeight, mCurrentLikeNumber, mLastLikeNumber, mNeedScrollDigit;

    float mDistance;

    {
        mCurrentLikeNumber = 2333;
        mLikeBitmapPaint = new Paint();
        mLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.like);
        mLikeSrcRect = new Rect(0, 0, mLikeBitmap.getWidth(), mLikeBitmap.getHeight());
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(40);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(10);
        mTextBoundRect = new Rect();
        mTextPaint.getTextBounds(String.valueOf(mCurrentLikeNumber), 0, 3, mTextBoundRect);
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
        canvas.drawBitmap(mLikeBitmap, mLikeSrcRect, mLikeDstRect, mLikeBitmapPaint);
        int x = mWidth / 2 - mTextBoundRect.width() / 2;
        int y = mHeight / 2 + mTextBoundRect.height() / 2;
        int outY, inY;
        String nextText, lastText;
        int digit = computeDigit();
        for (int i = digit; i > 0; i--) {
            nextText = String.valueOf(computeDigitValue(mCurrentLikeNumber, i));
            if (i <= mNeedScrollDigit) {
                if(Integer.valueOf(mLastLikeNumber)>mCurrentLikeNumber){
                    //减一
                    nextText = String.valueOf(computeDigitValue(mLastLikeNumber,i));
                    lastText = String.valueOf(computeDigitValue(mCurrentLikeNumber, i));
                }else{
                    //加一
                    lastText = String.valueOf(computeDigitValue(mLastLikeNumber, i));
                }

                outY = (int) (y - mTextBoundRect.height() * mDistance);
                inY = (int) (y + mTextBoundRect.height() - mTextBoundRect.height() * mDistance);
                mTextPaint.setAlpha((int) (255 - 255 * mDistance));
                canvas.drawText(lastText, x, outY, mTextPaint);
                mTextPaint.setAlpha((int) (255 * mDistance));
                canvas.drawText(nextText, x, inY, mTextPaint);
            } else {
                mTextPaint.setAlpha(255);
                canvas.drawText(nextText, x, y, mTextPaint);
            }
            x += mTextPaint.measureText(nextText);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mLikeDstRect = new Rect(dpToPixel(5), mHeight / 2 - dpToPixel(30) / 2, dpToPixel(35), mHeight / 2 + dpToPixel(30) / 2);
    }

    public static int dpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    /**
     * 计算当前点赞数量的 位数
     */
    private int computeDigit() {
        if (mCurrentLikeNumber / 10000 > 0) {
            return 5;
        } else if (mCurrentLikeNumber / 1000 > 0) {
            return 4;
        } else if (mCurrentLikeNumber / 100 > 0) {
            return 3;
        } else if (mCurrentLikeNumber / 10 > 0) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 计算需要滑动的位数
     */
    private int computeScrollDigitByAdd() {
        if (mCurrentLikeNumber % 10000 == 9999) {
            return 5;
        } else if (mCurrentLikeNumber % 1000 == 999) {
            return 4;
        } else if (mCurrentLikeNumber % 100 == 99) {
            return 3;
        } else if (mCurrentLikeNumber % 10 == 9) {
            return 2;
        } else {
            return 1;
        }
    }

    private int computeScrollDigitBySubtraction(){
        if (mCurrentLikeNumber % 10000 == 0) {
            return 5;
        } else if (mCurrentLikeNumber % 1000 == 0) {
            return 4;
        } else if (mCurrentLikeNumber % 100 == 0) {
            return 3;
        } else if (mCurrentLikeNumber % 10 == 0) {
            return 2;
        } else {
            return 1;
        }
    }

    /**
     * 计算位数对应的值
     */
    private int computeDigitValue(int value, int digit) {
        if (digit == 5) {
            return value / 10000;
        } else if (digit == 4) {
            return value % 10000 / 1000;
        } else if (digit == 3) {
            return value % 1000 / 100;
        } else if (digit == 2) {
            return value % 100 / 10;
        } else {
            return value % 10;
        }
    }

    public void add() {
        mLastLikeNumber = mCurrentLikeNumber;
        mNeedScrollDigit = computeScrollDigitByAdd();
        mCurrentLikeNumber++;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(500);
        valueAnimator.addUpdateListener(animation -> {
            mDistance = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        valueAnimator.start();
    }

    public void subtraction() {
        mLastLikeNumber = mCurrentLikeNumber;
        mNeedScrollDigit = computeScrollDigitBySubtraction();
        mCurrentLikeNumber--;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(500);
        valueAnimator.addUpdateListener(animation -> {
            mDistance = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        valueAnimator.start();
    }

}
