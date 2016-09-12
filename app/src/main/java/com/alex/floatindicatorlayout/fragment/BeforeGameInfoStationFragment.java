package com.alex.floatindicatorlayout.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.alex.floatindicatorlayout.MainActivity;
import com.alex.floatindicatorlayout.R;

import org.alex.callback.OnFragmentSelectedListener;

import github.alex.view.ObservableScrollView;

@SuppressLint("InflateParams")
public class BeforeGameInfoStationFragment extends Fragment {
    protected int index;
    protected View rootView;
    private ObservableScrollView scrollView;
    private MainActivity mainActivity;
    public static BeforeGameInfoStationFragment getInstance(int index) {
        BeforeGameInfoStationFragment fragment = new BeforeGameInfoStationFragment();
        fragment.index = index;
        return fragment;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    /**
     * ViewPager 的 子控件，ListView 或者 ScrollView 滑动到了
     */
    private boolean isChildOnTop;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_game_before_data, null);
            initView();
        }
        /*过滤Fragment重叠，如果是 Fragment嵌套Fragment，不能加这个*/
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    /**
     * 初始化视图
     *
     * @time 2014-12-27    09:52
     */
    private void initView() {
        scrollView = (ObservableScrollView) rootView.findViewById(R.id.sv);
        scrollView.setOnScrollListener(new ObservableScrollListener());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ((rootView != null) && (rootView.getParent() != null)) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private final class ObservableScrollListener implements ObservableScrollView.OnScrollListener {

        @Override
        public void onScroll(ScrollView scrollView, int x, int y, int oldx, int oldy) {
            isChildOnTop = (0 == scrollView.getScrollY());
            //KLog.e("scrollView.0 = "+(0 == scrollView.getScrollY()));
            if(MainActivity.indexFragment == 2){
                mainActivity.getFloatTitleLayout().setIsChildOnTop(isChildOnTop);
            }
        }
    }
    public final class MyOnFragmentSelectedListener implements OnFragmentSelectedListener {
        /**
         * 被选中的 Fragment的下标
         *
         * @param indexSelected
         */
        @Override
        public void onFragmentSelected(int indexSelected, Object extra) {
            if (index != indexSelected) {
                return;
            }
            if ((index == indexSelected) && (mainActivity != null) && (mainActivity.getFloatTitleLayout() != null)) {
                mainActivity.getFloatTitleLayout().setIsChildOnTop(isChildOnTop);
            }
        }
    }
}
