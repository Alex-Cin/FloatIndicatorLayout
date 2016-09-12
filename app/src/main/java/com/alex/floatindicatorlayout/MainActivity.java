package com.alex.floatindicatorlayout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alex.floatindicatorlayout.adapter.fragmentpageradapter.TitleFragmentPagerAdapter;
import com.alex.floatindicatorlayout.fragment.BeforeGameDataFragment;
import com.alex.floatindicatorlayout.fragment.BeforeGameInfoStationFragment;
import com.alex.floatindicatorlayout.fragment.BeforeGameScoreFragment;

import org.alex.callback.OnFragmentSelectedListener;
import org.alex.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import github.alex.floatindicatorlayout.FloatIndicatorLayout;
import github.hellojp.tabsindicator.TabsIndicator;

public class MainActivity extends FragmentActivity{
    private Context context;
    private View leftAnimLayout;
    private View rightAnimLayout;
    private RelativeLayout.LayoutParams leftAnimParams;
    private RelativeLayout.LayoutParams rightAnimParams;
    private int xMarginMin, xMarginMax, yMarginMin, yMarginMax;
    private float sizeMin, sizeMax;
    private List<Fragment> listFragment;
    private FloatIndicatorLayout floatTitleLayout;
    public static int indexFragment;
    protected ArrayList<OnFragmentSelectedListener> listOnFragmentSelectedListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        indexFragment = 0;
        initView();
    }

    private void initView() {
        listOnFragmentSelectedListener = new ArrayList<>();
        xMarginMin = (int) dp2Px(32);
        xMarginMax = (int) dp2Px(112);
        yMarginMin = (int) dp2Px(10);
        yMarginMax = (int) dp2Px(96);
        sizeMin = dp2Px(36);
        sizeMax = dp2Px(72);
        leftAnimLayout = findViewById(R.id.iv_anim_left);
        rightAnimLayout = findViewById(R.id.iv_anim_right);
        floatTitleLayout = (FloatIndicatorLayout) findViewById(R.id.ftl);
        floatTitleLayout.setIndicatorMarginTop(56);
        ViewGroup bodyLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.layout_body, null);
        ViewPager viewPager = (ViewPager) bodyLayout.findViewById(R.id.vp);
        TitleFragmentPagerAdapter adapter = new TitleFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        listFragment = new ArrayList<>();
        BeforeGameDataFragment beforeGameDataFragment = BeforeGameDataFragment.getInstance(0);
        BeforeGameScoreFragment beforeGameScoreFragment = BeforeGameScoreFragment.getInstance(1);
        BeforeGameInfoStationFragment beforeGameInfoStationFragment = BeforeGameInfoStationFragment.getInstance(2);
        listFragment.add(beforeGameDataFragment);
        listFragment.add(beforeGameScoreFragment);
        listFragment.add(beforeGameInfoStationFragment);
        listOnFragmentSelectedListener.add(beforeGameDataFragment.new MyOnFragmentSelectedListener());
        listOnFragmentSelectedListener.add(beforeGameScoreFragment.new MyOnFragmentSelectedListener());
        listOnFragmentSelectedListener.add(beforeGameInfoStationFragment.new MyOnFragmentSelectedListener());
        adapter.addItem(listFragment);
        List<String> listTitle = new ArrayList<>();
        listTitle.add("赛前数据");
        listTitle.add("赛前情评分");
        listTitle.add("赛前情报站");
        adapter.addTitle(listTitle);

        View indicatorLayout = LayoutInflater.from(context).inflate(R.layout.layout_indicator, null);
        TabsIndicator tabsIndicator = (TabsIndicator) indicatorLayout.findViewById(R.id.ti);
        tabsIndicator.setViewPager(0, viewPager);
        tabsIndicator.setAnimationWithTabChange(true);
        tabsIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
        floatTitleLayout.setFloatIndicatorLayout(R.layout.layout_top, indicatorLayout, R.layout.layout_indicator, bodyLayout);
        floatTitleLayout.setOnFloatScrollListener(new MyOnFloatTitleScrollListener());
    }

    private final class MyOnFloatTitleScrollListener implements FloatIndicatorLayout.OnFloatScrollListener {
        @Override
        public void onFloatTitleScroll(int maxDistance, int currDistance, float progress) {

            int size = (int) (sizeMax - (sizeMax - sizeMin) * progress);
            leftAnimParams = (leftAnimParams == null) ? new RelativeLayout.LayoutParams(leftAnimLayout.getWidth(), leftAnimLayout.getHeight()) : leftAnimParams;
            rightAnimParams = (rightAnimParams == null) ? new RelativeLayout.LayoutParams(rightAnimLayout.getWidth(), rightAnimLayout.getHeight()) : rightAnimParams;
            leftAnimParams.leftMargin = (int) (xMarginMin + (xMarginMax - xMarginMin) * progress);
            leftAnimParams.topMargin = (int) (yMarginMax - (yMarginMax - yMarginMin) * progress);
            leftAnimParams.width = size;
            leftAnimParams.height = size;
            leftAnimLayout.setLayoutParams(leftAnimParams);
            rightAnimParams.rightMargin = (int) (xMarginMin + (xMarginMax - xMarginMin) * progress);
            rightAnimParams.topMargin = (int) (yMarginMax - (yMarginMax - yMarginMin) * progress);
            rightAnimParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightAnimParams.width = size;
            rightAnimParams.height = size;
            rightAnimLayout.setLayoutParams(rightAnimParams);
        }
    }

    public FloatIndicatorLayout getFloatTitleLayout(){
        return floatTitleLayout;
    }
    private final class MyOnPageChangeListener implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            indexFragment = position;
            LogUtil.e("indexFragment = "+indexFragment);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
    /**
     * 数据转换: dp---->px
     */
    private float dp2Px(float dp) {
        if (context == null) {
            return -1;
        }
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
