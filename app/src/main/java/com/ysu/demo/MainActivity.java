package com.ysu.demo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ysu.tablayout.R;
import com.ysu.tablayout.TabLayout;

public class MainActivity extends AppCompatActivity {

    public TabLayout mTabLayout;
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mTabLayout = (TabLayout) findViewById(R.id.base_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.vp_content);

        mViewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
