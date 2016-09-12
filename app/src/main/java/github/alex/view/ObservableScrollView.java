package github.alex.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/4/3.
 * 可以被检测是否滑动到顶部的ScrollView
 */
public class ObservableScrollView extends ScrollView{
    private OnScrollListener onScrollListener = null;
    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScrollListener != null) {
            onScrollListener.onScroll(this, x, y, oldx, oldy);
        }
    }
    public interface OnScrollListener {
       public void onScroll(ScrollView scrollView, int x, int y, int oldx, int oldy);
    }
}
