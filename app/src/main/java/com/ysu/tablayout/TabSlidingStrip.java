package com.ysu.tablayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.ysu.tablayout.interpolator.FastInSlowOutInterpolator;
import com.ysu.tablayout.interpolator.FastOutSlowInInterpolator;

/**
 * Created by ztz on 2018-01-17.
 */

public class TabSlidingStrip extends LinearLayout {
    private Shader mIndicatorShader = null;
    private Paint mIndicatorPaint = new Paint();

    private int mCurrentPosition = 0;
    private int mIndicatorWidth = dpToPx(getContext(), 10);
    private int mIndicatorHeight = dpToPx(getContext(), 2);

    private int mMeasuredWidth;
    private RectF mIndicatorRectF = new RectF();

    private int[] mIndicatorColors;
    private ValueAnimator mIndicatorAnimator;
    private FastOutSlowInInterpolator mIndicatorInterpolator = new FastOutSlowInInterpolator();

    TabSlidingStrip(Context context) {
        super(context);

        setWillNotDraw(false);
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setIndicatorWidth(int width) {
        mIndicatorWidth = width;
        invalidate();
    }

    public void setIndicatorHeight(int height) {
        mIndicatorHeight = height;
        invalidate();
    }

    public void setIndicatorColor(int color) {
        mIndicatorPaint.setColor(color);
        invalidate();
    }

    public void setIndicatorColors(int[] colors) {
        if (colors.length < 1) {
            setIndicatorColor(Color.BLUE);
            return;
        }

        if (colors.length < 2) {
            setIndicatorColor(colors[0]);
            return;
        }

        mIndicatorColors = colors;
    }

    public void setIndicatorPosition(int position) {
        View child = getChildAt(position);
        int childLeft = child.getLeft();
        int childRight = child.getRight();
        int childWidth = child.getMeasuredWidth();
        int left = childLeft + (childWidth - mIndicatorWidth) / 2;
        int right = childRight - (childWidth - mIndicatorWidth) / 2;

        setIndicatorPosition(left, right);

        invalidate();
    }

    public void setIndicatorPosition(int position, float offset) {
        float leftFraction = 0;
        float rightFraction = 0;

        if (position < mCurrentPosition) {
            leftFraction -= FastOutSlowInInterpolator.FAST_OUT_SLOW_IN[(int) (FastOutSlowInInterpolator.FAST_OUT_SLOW_IN.length * offset)];
            rightFraction -= FastInSlowOutInterpolator.FAST_IN_SLOW_OUT[(int) (FastInSlowOutInterpolator.FAST_IN_SLOW_OUT.length * offset)];
        } else {
            leftFraction = FastInSlowOutInterpolator.FAST_IN_SLOW_OUT[(int) (FastInSlowOutInterpolator.FAST_IN_SLOW_OUT.length * offset)];
            rightFraction = FastOutSlowInInterpolator.FAST_OUT_SLOW_IN[(int) (FastOutSlowInInterpolator.FAST_OUT_SLOW_IN.length * offset)];
        }

        View child = getChildAt(position);
        int childLeft = child.getLeft();
        int childRight = child.getRight();
        int childWidth = child.getMeasuredWidth();
        int left = (int) (childLeft + (childWidth - mIndicatorWidth) / 2 + leftFraction * childWidth);
        int right = (int) (childRight - (childWidth - mIndicatorWidth) / 2 + rightFraction * childWidth);

        setIndicatorPosition(left, right);
    }

    public void setIndicatorPosition(int left, int right) {
        mIndicatorRectF.left = left;
        mIndicatorRectF.right = right;

        invalidate();
    }

    public void animateTo(final int position) {
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.cancel();
        }

        View startChild = getChildAt(mCurrentPosition);
        View endChild = getChildAt(position);

        int startChildWidth = startChild.getMeasuredWidth();
        int endChildWidth = endChild.getMeasuredWidth();

        if (startChildWidth <= 0 && endChildWidth <= 0) {
            return;
        }

        int startChildLeft = startChild.getLeft();
        int endChildLeft = endChild.getLeft();
        int startChildRight = startChild.getRight();
        int endChildRight = endChild.getRight();

        final int startLeft = startChildLeft + (startChildWidth - mIndicatorWidth) / 2;
        final int endLeft = endChildLeft + (endChildWidth - mIndicatorWidth) / 2;
        final int startRight = startChildRight - (startChildWidth - mIndicatorWidth) / 2;
        final int endRight = endChildRight - (endChildWidth - mIndicatorWidth) / 2;

        ValueAnimator animator = mIndicatorAnimator = new ValueAnimator();
        animator.setInterpolator(mIndicatorInterpolator);
        animator.setDuration(Math.round((1f - animator.getAnimatedFraction()) * animator.getDuration()));
        animator.setFloatValues(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = animator.getAnimatedFraction();
                setIndicatorPosition(lerp(startLeft, endLeft, fraction), lerp(startRight, endRight, fraction));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                mCurrentPosition = position;
            }
        });

        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mIndicatorRectF.top = getMeasuredHeight() - mIndicatorHeight;
        mIndicatorRectF.bottom = getMeasuredHeight();

        if (mIndicatorColors != null && mIndicatorColors.length > 1) {
            if (mIndicatorShader == null || mMeasuredWidth != getMeasuredWidth()) {
                mMeasuredWidth = getMeasuredWidth();
                mIndicatorShader = new LinearGradient(0, 0, getMeasuredWidth(), 0, mIndicatorColors, null, Shader.TileMode.CLAMP);
            }
            mIndicatorPaint.setShader(mIndicatorShader);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIndicatorRectF.left > 0 && mIndicatorRectF.right > 0) {
            canvas.drawRoundRect(mIndicatorRectF, dpToPx(getContext(), 1), dpToPx(getContext(), 1), mIndicatorPaint);
        } else {
            setIndicatorPosition(mCurrentPosition);
        }
    }

    private int dpToPx(Context context, float dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }

    private int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (endValue - startValue));
    }
}
