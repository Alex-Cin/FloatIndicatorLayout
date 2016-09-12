package com.alex.floatindicatorlayout.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.alex.floatindicatorlayout.MainActivity;
import com.alex.floatindicatorlayout.R;
import com.alex.floatindicatorlayout.adapter.baseadapter.game.ScoreAdapter;
import com.alex.floatindicatorlayout.config.App;

import org.alex.callback.OnFragmentSelectedListener;
import org.alex.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import github.maxwin.xlistview.XListView;

@SuppressLint("InflateParams")
public class BeforeGameScoreFragment extends Fragment {
    protected int index;
    protected int pageindex = 1;
    /**加载类型：  首次加载  上拉加载  下拉刷新*/
    protected String loadType;
    protected View rootView;

    private ScoreAdapter adapter;
    private XListView xListView;
    /**
     * ViewPager 的 子控件，ListView 或者 ScrollView 滑动到了
     */
    private boolean isChildOnTop;
    private MainActivity mainActivity;
    public static BeforeGameScoreFragment getInstance(int index) {
        BeforeGameScoreFragment fragment = new BeforeGameScoreFragment();
        fragment.index = index;
        return fragment;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadType = App.loadFirst;
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_game_before_score, null);
            initView();
            loadJsonData();
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
        xListView = (XListView) rootView.findViewById(R.id.xlv);

        adapter = new ScoreAdapter(getActivity());
        xListView.setPullLoadEnable(true);
        xListView.setXListViewListener(new MyIXListViewListener(xListView));
        xListView.setAdapter(adapter);
        xListView.setOnScrollListener(new MyOnScrollListener());

    }

    /**TODO: 下拉刷新
     * */
    protected final class MyIXListViewListener implements XListView.IXListViewListener
    {
        private XListView xlistView;
        public MyIXListViewListener(XListView xlistView) {
            this.xlistView = xlistView;
        }
        @Override
        public void onRefresh()
        {
            String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
            xlistView.setRefreshTime(label);
            loadType = App.loadRefresh;
            pageindex = 1;
            new LoadTadk().execute();
        }
        @Override
        public void onLoadMore()
        {
            loadType = App.loadMore;
            pageindex++;
            new LoadTadk().execute();
        }
    }

    private final class LoadTadk extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params)
        {
            SystemClock.sleep(1500);
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            ((XListView) rootView.findViewById(R.id.xlv)).stopXListView();
            super.onPostExecute(result);
        }

    }

    private void loadJsonData() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            list.add("我是数据 " + i);
        }
        adapter.addItem(list);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if ((rootView != null) && (rootView.getParent() != null)) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }
    private final class MyOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            isChildOnTop = (0 == firstVisibleItem) && xListView.isHeadViewHidden();
            LogUtil.e("isChildOnTop = "+isChildOnTop);
            if(MainActivity.indexFragment == index){
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
