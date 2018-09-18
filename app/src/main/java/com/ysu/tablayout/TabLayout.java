package com.ysu.tablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztz on 2018-01-19.
 */

public class TabLayout extends HorizontalScrollView implements Tab.OnTabSelectListener {
    private ViewPager mViewPager;
    private TabSlidingStrip mTabSlidingStrip;

    private Tab mSelectedTab;
    private List<Tab> mTabs = new ArrayList<>();

    private Mode mMode = Mode.SCROLLABLE;
    private int mTabWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
    private int mTabHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
    private final int DEFAULT_SELECT_TEXT_COLOR = Color.RED;
    private final int DEFAULT_NORMAL_TEXT_COLOR = Color.BLACK;
    private int mTabTextSize = 14;
    private int mTabTextSelectedSize = 30;
    private int mTabGravity;
    private int mTabTextMarginTop;
    private int mTabTextMarginLeft;
    private int mTabTextMarginRight;
    private int mTabTextMarginBottom;
    private int mTabTextColorRes;
    private int mTabBackgroundRes;
    /**
     * 用于选中某个标签后文字放大的动画
     */
    private ValueAnimator timeValueAnimator;
    private int titleIndex = 0;//当前索引
//    /**
//     * 最大状态下文字的默认大小
//     */
//    private  final float MAX_TITLE_TEXT_SIZE = dpToPx(getContext(),40);//sp
//    /**
//     * 最小状态下文字的默认大小
//     */
//    private  final float MIN_TITLE_TEXT_SIZE = dpToPx(getContext(),28);//sp


    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setHorizontalScrollBarEnabled(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);

        mTabSlidingStrip = new TabSlidingStrip(context);
        addView(mTabSlidingStrip);

        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);

        mTabSlidingStrip.setIndicatorColor(typedArray.getColor(R.styleable.TabLayout_tab_indicator_color, Color.BLUE));
        mTabSlidingStrip.setIndicatorWidth(typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_indicator_width, dpToPx(context, 10)));
        mTabSlidingStrip.setIndicatorHeight(typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_indicator_height, dpToPx(context, 2)));

        if (typedArray.hasValue(R.styleable.TabLayout_tab_width)) {
            setTabWidth(typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_width, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        if (typedArray.hasValue(R.styleable.TabLayout_tab_height)) {
            setTabHeight(typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_height, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_text_size, 10);
        mTabTextSelectedSize = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_text_selected_size, 10);
        mTabTextMarginTop = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_text_margin_top, 0);
        mTabTextMarginBottom = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_text_margin_bottom, 0);
        mTabTextMarginLeft = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_text_margin_left, 0);
        mTabTextMarginRight = typedArray.getDimensionPixelSize(R.styleable.TabLayout_tab_text_margin_right, 0);
        mTabTextColorRes = typedArray.getResourceId(R.styleable.TabLayout_tab_text_color, 0);
        mTabBackgroundRes = typedArray.getResourceId(R.styleable.TabLayout_tab_background, 0);
        mTabGravity = typedArray.getResourceId(R.styleable.TabLayout_tab_gravity, 1);

        int mode = typedArray.getInt(R.styleable.TabLayout_mode, 1);
        switch (mode) {
            case 0:
                setMode(Mode.SCROLLABLE);
                break;
            case 1:
                setMode(Mode.FIXED);
                break;
        }

        typedArray.recycle();
    }

    public Tab newTab() {
        Tab tab = new Tab(getContext());
        tab.setTextSize(mTabTextSize);
        tab.setGravity(mTabGravity == 1 ? Gravity.BOTTOM : Gravity.CENTER);
        tab.setTextMargin(mTabTextMarginLeft,mTabTextMarginTop,mTabTextMarginRight, mTabTextMarginBottom);
        if (mTabBackgroundRes != 0) {
            tab.setBackgroundDrawable(getContext().getResources().getDrawable(mTabBackgroundRes));
        }
        if (mTabTextColorRes != 0) {
            tab.setTextColorStateList(mTabTextColorRes);
        }

        return tab;
    }

    public void addTab(Tab tab) {
        mTabSlidingStrip.addView(tab);

        if (mTabs.isEmpty()) {
            mSelectedTab = tab;
            mSelectedTab.setSelected(true);
            mSelectedTab.setTextSize(mTabTextSelectedSize);
            mSelectedTab.getTextView().setTypeface(Typeface.DEFAULT_BOLD);
            mTabSlidingStrip.setIndicatorPosition(0);
        }

        tab.setPosition(mTabs.size());
        mTabs.add(tab);
        tab.setOnTabSelectListener(this);

        setMode(mMode);
        setTabWidth(mTabWidth);
        setTabHeight(mTabHeight);
    }

    public void setMode(Mode mode) {
        mMode = mode;

        if (mMode.equals(Mode.FIXED) && !mTabs.isEmpty()) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int tabWidth = width / mTabs.size();
            setTabWidth(tabWidth);
        }
    }

    public void setTabWidth(int width) {
        mTabWidth = width;

        for (Tab tab : mTabs) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.getLayoutParams();
            params.width = mTabWidth;
            tab.setLayoutParams(params);
        }
    }

    public void setTabHeight(int height) {
        mTabHeight = height;

        for (Tab tab : mTabs) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.getLayoutParams();
            params.height = mTabHeight;
            tab.setLayoutParams(params);
        }
    }

    public void selectTab(int position) {
        mTabs.get(position).setSelected(true);
//        mTabs.get(position).setTextSize(mTabTextSelectedSize);
    }

    public void setupWithViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(new OnPageChangeListener());

        ViewPagerAdapter adapter = (ViewPagerAdapter) viewPager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Tab tab = newTab();
            tab.setText(adapter.getPageTitle(i));
            addTab(tab);
        }
    }

    public void setIndicatorColors(int[] colors) {
        mTabSlidingStrip.setIndicatorColors(colors);
    }

    private void smoothScrollTo(int position) {
        if (Mode.SCROLLABLE.equals(mMode)) {
            View selectedChild = mTabSlidingStrip.getChildAt(position);
            int scrollX = selectedChild.getLeft() + (selectedChild.getWidth() - getWidth()) / 2;
            smoothScrollTo(scrollX, getScrollY());
        }
    }

    private void smoothScrollTo(int position, float positionOffset) {
        if (Mode.SCROLLABLE.equals(mMode)) {
            View selectedChild = mTabSlidingStrip.getChildAt(position);
            View nextChild = position + 1 < mTabSlidingStrip.getChildCount() ? mTabSlidingStrip.getChildAt(position + 1) : null;
            int selectedWidth = selectedChild != null ? selectedChild.getWidth() : 0;
            int nextWidth = nextChild != null ? nextChild.getWidth() : 0;
            int scrollX = selectedChild.getLeft() + (int) (((selectedWidth + nextWidth) * positionOffset + selectedChild.getWidth() - getWidth()) / 2);
            smoothScrollTo(scrollX, getScrollY());
        }
    }

    @Override
    public void onTabSelected(Tab tab, int position) {
        if (tab == mSelectedTab) {
            return;
        }

        mSelectedTab.setSelected(false);
        mSelectedTab = tab;

        if (mViewPager != null) {
            mViewPager.setCurrentItem(position);
        } else {
            mTabSlidingStrip.animateTo(position);
            smoothScrollTo(position);
        }
    }

    @Override
    public void onTabUnselected(Tab tab, int position) {
    }

    private int dpToPx(Context context, float dps) {
        return Math.round(context.getResources().getDisplayMetrics().density * dps);
    }

    public enum Mode {
        SCROLLABLE, FIXED
    }

    public class OnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mTabSlidingStrip.setIndicatorPosition(position, positionOffset);

            smoothScrollTo(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {

            /**
             * 之前的动画如果没执行完,让它结束
             */
            if (timeValueAnimator != null && timeValueAnimator.isRunning()) {
                timeValueAnimator.end();
            }
            if (position == titleIndex) {
                return;
            }
            int lastIndex = titleIndex;

            final TextView preTextView = ((Tab) mTabSlidingStrip.getChildAt(lastIndex)).getTextView();
            final TextView curTextView = ((Tab) mTabSlidingStrip.getChildAt(position)).getTextView();

            timeValueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
            timeValueAnimator.setDuration(200);
            timeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (float) animation.getAnimatedValue();
                    /**
                     * 选中的这个放大,之前的那个缩小
                     */
                    int minSize = DensityUtil.px2dp(getContext(),mTabTextSize);
                    int maxSize = DensityUtil.px2dp(getContext(),mTabTextSelectedSize);
                    float preSize = minSize + (maxSize - minSize) * (1 - f);
                    float curSize = minSize + (maxSize - minSize) * f;
                    if (f >= 0.5f) {
                        preTextView.setTypeface(Typeface.DEFAULT);
                    } else {
                        curTextView.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    preTextView.setTextSize(preSize);
                    curTextView.setTextSize(curSize);
                }
            });
            timeValueAnimator.start();
            titleIndex = position;

            selectTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    public static abstract class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
    }
}
