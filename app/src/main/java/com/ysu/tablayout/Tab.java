package com.ysu.tablayout;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ztz on 2018-01-17.
 */

public class Tab extends LinearLayout {
    private TextView mTextView;

    private OnTabSelectListener mListener;

    private int mPosition;

    Tab(Context context) {
        super(context);

        setClickable(true);
        setOrientation(VERTICAL);

        mTextView = new TextView(context);
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mTextView.setGravity(Gravity.BOTTOM);
        addView(mTextView);
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public TextView getTextView() {
        return mTextView;
    }


    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setText(@StringRes int resid) {
        mTextView.setText(resid);
    }

    public void setTextSize(float size) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setTextColorStateList(@ColorRes int resid) {
        mTextView.setTextColor(getResources().getColorStateList(resid));
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public boolean performClick() {
        setSelected(true);
        return super.performClick();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        mTextView.setSelected(selected);

        if (mListener != null && selected) {
            mListener.onTabSelected(this, mPosition);
        }
        if (mListener != null && !selected) {
            mListener.onTabUnselected(this, mPosition);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void setOnTabSelectListener(OnTabSelectListener listener) {
        mListener = listener;
    }

    private int dpToPx(Context context, float dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }

    public void setTextMargin(int left, int top, int right, int bottom) {
        LayoutParams params = (LayoutParams) mTextView.getLayoutParams();
        params.setMargins(left, top, right, bottom);
        mTextView.setLayoutParams(params);
    }

    interface OnTabSelectListener {
        void onTabSelected(Tab tab, int position);

        void onTabUnselected(Tab tab, int position);
    }
}
