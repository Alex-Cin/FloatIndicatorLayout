package github.alex.floatindicatorlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Alex on 2016/4/2.
 */
public class FloatIndicatorLayout extends RelativeLayout {
    /**
     * 顶部 菜单
     */
    protected int titleLayoutId;
    /**
     * 顶部 视图 资源id
     */
    protected int topLayoutId;
    /**
     * 顶部的  背景图
     */
    private View topLayout;
    /**
     * 悬浮的  指示器  菜单视图 资源id
     */
    protected int indicatorLayoutId;
    /**
     * 用于切换的导航条
     */
    private View indicatorLayout;
    private Context context;
    private float lastX, lastY;
    /**
     * 指示器的 上边距
     */
    private int indicatorMarginTop;
    private int statusHeight;
    /**
     * 顶部导航条 能滑动的最大距离
     */
    private int indicatorScrollDistanceMax;
    /**
     * 当前 顶部导航 固定在 顶部， 正处于悬浮状态
     */
    private boolean isFloated;
    /**
     * 顶部导航 指示器  Y点 在屏幕中的位置
     */
    private int indicatorLocationYInScreen;
    private VelocityTracker velocityTracker;
    private OnFloatScrollListener onFloatScrollListener;

    /**
     * 当速度超过这个临界值，视为最快速度滑动 ，向左为负
     */
    //private static final float xVelocityMax = 300;
    /**
     * 当速度超过这个临界值，视为最快速度滑动 ，向上为负
     */
    private static final float yVelocityMax = 300;
    private int[] topLayoutLocationArray;
    private int[] indicatorLocationArray;

    /**
     * Viewpager 的孩子，可以做下拉刷新
     */
    private boolean canChildPullDown;
    private LayoutParams topLayoutParams;
    /**
     * 确定 是x 轴上滑动
     */
    private LikeXY likeXY;
    /*  ViewPager 的 子控件，ListView 或者 ScrollView 滑动到了*/
    private boolean isChildOnTop;

    public FloatIndicatorLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public FloatIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        statusHeight = getStatusBarHeight();
        isFloated = false;
        isChildOnTop = true;
        canChildPullDown = false;
        lastX = -1;
        lastY = -1;
        topLayoutLocationArray = new int[2];
        indicatorLocationArray = new int[2];
    }

    private void setTopLayout(int topLayoutId) {
        this.topLayoutId = topLayoutId;
        topLayout = LayoutInflater.from(getContext()).inflate(topLayoutId, null);
        topLayout.setId(topLayoutId);
        topLayout.measure(0, 0);
        topLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, topLayout.getMeasuredHeight());
        topLayoutParams.addRule(RelativeLayout.BELOW, titleLayoutId);
        topLayout.setId(topLayoutId);
        addView(topLayout, topLayoutParams);
    }

    private void setIndicatorLayout(View indicatorLayout, int indicatorLayoutId) {
        this.indicatorLayoutId = indicatorLayoutId;
        this.indicatorLayout = indicatorLayout;
        indicatorLayout.measure(0, 0);
        LayoutParams indicatorLayoutParams;
        //KLog.e("56dp = " + dpToPx(56) + "getHeight = " + indicatorLayout.getMeasuredHeight());
        indicatorLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, indicatorLayout.getMeasuredHeight());
        indicatorLayoutParams.addRule(RelativeLayout.BELOW, topLayoutId);
        //RelativeLayout.params.addRule
        indicatorLayout.setId(indicatorLayoutId);
        addView(indicatorLayout, indicatorLayoutParams);
    }

    private void setBodyLayout(ViewGroup bodyLayout) {
        LayoutParams bodyLayoutParams;
        bodyLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        bodyLayoutParams.addRule(RelativeLayout.BELOW, indicatorLayoutId);
        //RelativeLayout.params.addRule
        addView(bodyLayout, bodyLayoutParams);
    }

    /**
     * @param topLayoutId     顶部视图的 布局文件的 资源id
     * @param indicatorLayout FloatTableLayout 的内置菜单的 选项卡 的布局文件的  资源id
     * @param bodyLayout      主视图的 布局文件的 资源 id
     */
    public void setFloatIndicatorLayout(int topLayoutId, View indicatorLayout, int indicatorLayoutId, ViewGroup bodyLayout) {
        setTopLayout(topLayoutId);
        setIndicatorLayout(indicatorLayout, indicatorLayoutId);
        setBodyLayout(bodyLayout);
    }

    public void setIsChildOnTop(boolean isChildOnTop) {
        this.isChildOnTop = isChildOnTop;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /*手指滑动距离  向左滑动 distanceX > 0*/
        float distanceX;
        /*手指滑动距离  向上滑动 distanceY > 0*/
        float distanceY;
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            likeXY = LikeXY.likeNo;
            /*计算背景图片的位置*/
            topLayout.getLocationOnScreen(topLayoutLocationArray);
            velocityTracker = VelocityTracker.obtain();
            lastX = event.getRawX();
            lastY = event.getRawY();
            //topLayoutParams = new RelativeLayout.LayoutParams(matchParent, topLayout.getHeight());
            topLayoutParams.topMargin = topLayoutLocationArray[1] - statusHeight;
            indicatorScrollDistanceMax = topLayout.getHeight() - indicatorMarginTop;
        } else if (MotionEvent.ACTION_MOVE == event.getAction()) {


            velocityTracker.addMovement(event);
            velocityTracker.computeCurrentVelocity(50);
            float yVelocity = velocityTracker.getYVelocity(0);
            /*计算顶部导航的位置*/
            indicatorLayout.getLocationOnScreen(indicatorLocationArray);
            indicatorLocationYInScreen = indicatorLocationArray[1];
            /*计算背景图片的位置*/
            topLayout.getLocationOnScreen(topLayoutLocationArray);

            distanceX = lastX - event.getRawX();
            distanceY = lastY - event.getRawY();
            lastY = event.getRawY();
            lastX = event.getRawX();

            if ((indicatorMarginTop + statusHeight) >= indicatorLocationYInScreen) {
                /*计算得出  当前状态处于 悬浮状态*/
                isFloated = true;
            } else if ((indicatorMarginTop + statusHeight) < indicatorLocationYInScreen) {
                /*计算得出  当前状态处于 非悬浮状态*/
                isFloated = false;
            }
            if (Math.abs(distanceY) * 0.5 > Math.abs(distanceX)) {
                if (likeXY == LikeXY.likeNo) {
                    likeXY = LikeXY.likeY;
                }
            } else if (Math.abs(distanceX) * 0.5 > Math.abs(distanceY)) {
                if (likeXY == LikeXY.likeNo) {
                    likeXY = LikeXY.likeX;
                }
            }
            if (LikeXY.likeY != likeXY) {
                likeXY = LikeXY.likeNo;
                return super.dispatchTouchEvent(event);
            }
            //KLog.e("likeXY = "+likeXY+" isFloated = "+isFloated+" distanceY = "+distanceY);
            /*非悬浮状态，向上滑动↓*/
            if ((!isFloated) && (distanceY > 0)) {
               // KLog.e("非悬浮状态，向上滑动");
                /*非悬浮状态，向上滑动，速度过大↓*/
                if (yVelocity < -yVelocityMax) {
                    /*非悬浮状态，向上滑动，速度过大，导航条在初始状态，直接将导航条，全部拖上去↓*/
                    if (topLayoutParams.topMargin == 0) {
                        distanceY = indicatorMarginTop - topLayout.getHeight();
                    } else {
                        /*非悬浮状态，向上滑动，速度过大，导航条不在初始状态，直接将导航条，全部拖上去↑*/
                        distanceY = -indicatorLocationYInScreen + indicatorMarginTop + statusHeight;
                    }
                    topLayoutParams.topMargin = topLayoutParams.topMargin + (int) distanceY;
                    topLayout.setLayoutParams(topLayoutParams);
                    updateScrollProgress(indicatorScrollDistanceMax);
                    isFloated = true;
                    likeXY = LikeXY.likeNo;
                    return true;
                } else if (yVelocity >= -yVelocityMax) {
                    /*非悬浮状态，向上滑动，速度正常↓*/
                    if (indicatorMarginTop + statusHeight + distanceY > indicatorLocationYInScreen) {
                        distanceY = indicatorLocationYInScreen - indicatorMarginTop - statusHeight;
                    }
                    topLayoutParams.topMargin = topLayoutParams.topMargin - (int) distanceY;
                    topLayout.setLayoutParams(topLayoutParams);
                    /*计算后得到当前真实的  indicatorLocationYInScreen*/
                    int locationIndicatorYInScreenTrue = (indicatorLocationYInScreen - (int) distanceY);
                    int indicatorScrollDistanceCurr = topLayout.getHeight() + statusHeight - locationIndicatorYInScreenTrue;
                    //KLog.e("计算后的 indicatorLocationYInScreen "+locationIndicatorYInScreenTrue+" indicatorScrollDistanceCurr = "+indicatorScrollDistanceCurr);
                    //KLog.e("背景图高度 = "+topLayout.getHeight()+" topLayoutLocationYInScreen = "+topLayoutLocationYInScreen+" indicatorLocationYInScreen = " + indicatorLocationYInScreen + " statusHeight = " + statusHeight + " indicatorMarginTop = " + indicatorMarginTop+" distanceY = "+distanceY+" indicatorScrollDistanceMax = "+indicatorScrollDistanceMax);
                    updateScrollProgress(indicatorScrollDistanceCurr);
                    likeXY = LikeXY.likeNo;
                    return true;
                }
            } else if ((!isFloated) && (distanceY < 0)) {
                /*非悬浮状态，向下滑动 ↑*/
                //KLog.e("非悬浮状态，向下滑动 ");
                int indicatorScrollDistanceCurr = 0;
                if (indicatorLocationYInScreen == (topLayout.getHeight() + statusHeight)) {
                    //KLog.e("1111 canChildPullDown = "+canChildPullDown);
                    /*TODO: 初始状态*/
                    if (canChildPullDown) {
                        likeXY = LikeXY.likeNo;
                        return super.dispatchTouchEvent(event);
                    } else {
                        likeXY = LikeXY.likeNo;
                        return true;
                    }
                } else if (indicatorLocationYInScreen < (topLayout.getHeight() + statusHeight)) {
                    if (yVelocity > yVelocityMax) {
                        /*非悬浮状态，向下滑动，速度过大*/
                        indicatorScrollDistanceCurr = 0;
                        topLayoutParams.topMargin = 0;
                    } else if (yVelocity < yVelocityMax) {
                        /*非悬浮状态，向下滑动 ，速度正常*/
                        //KLog.e("distanceY = "+topLayoutParams.distanceY+" indicatorLocationYInScreen = "+indicatorLocationYInScreen+" distanceY = "+distanceY);
                        if ((indicatorLocationYInScreen - (int) distanceY) <= (topLayout.getHeight() + statusHeight)) {
                            topLayoutParams.topMargin = topLayoutParams.topMargin - (int) distanceY;
                            indicatorScrollDistanceCurr = topLayout.getHeight() + statusHeight - (indicatorLocationYInScreen - (int) distanceY);
                        } else {
                            topLayoutParams.topMargin = 0;
                            indicatorScrollDistanceCurr = 0;
                        }
                    }
                    topLayout.setLayoutParams(topLayoutParams);
                    updateScrollProgress(indicatorScrollDistanceCurr);
                    likeXY = LikeXY.likeNo;
                    return true;
                }
            } else if (isFloated && (distanceY < 0)) {
                //KLog.e("悬浮状态，向下滑动 isChildOnTop = " + isChildOnTop);
                if (isChildOnTop) {
                    canChildPullDown = false;
                    isFloated = false;
                    int indicatorScrollDistanceCurr = 0;
                    /*悬浮状态，向下滑动，ScrollView 展示第一条数据 ↑*/
                    //KLog.e("悬浮状态，向下滑动 isChildOnTop = " + isChildOnTop);
                    if (yVelocity > yVelocityMax) {
                        /*悬浮状态，向下滑动，ScrollView 展示第一条数据 ，速度过大，直接将背景图，全部拖出来*/
                        indicatorScrollDistanceCurr = 0;
                        topLayoutParams.topMargin = 0;
                    } else if (yVelocity <= yVelocityMax) {
					/*悬浮状态，向下滑动，ScrollView 展示第一条数据 ，速度正常，将背景图，慢慢拖出来*/
                        //KLog.e("distanceY = " + distanceY + " indicatorLocationYInScreen = " + indicatorLocationYInScreen);
                        if ((indicatorLocationYInScreen - (int) distanceY) <= (topLayout.getHeight() + statusHeight)) {
                            topLayoutParams.topMargin = topLayoutParams.topMargin - (int) distanceY;
                            indicatorScrollDistanceCurr = topLayout.getHeight() + statusHeight - (indicatorLocationYInScreen - (int) distanceY);
                        } else {
                            topLayoutParams.topMargin = topLayout.getHeight() + statusHeight;
                            indicatorScrollDistanceCurr = indicatorScrollDistanceMax;
                        }
                    }
                    topLayout.setLayoutParams(topLayoutParams);
                    updateScrollProgress(indicatorScrollDistanceCurr);
                    likeXY = LikeXY.likeNo;
                    return super.dispatchTouchEvent(event);
                } else {
                    return super.dispatchTouchEvent(event);
                }
            } else if (isFloated && (distanceY > 0)) {
                //KLog.e("悬浮状态，向上滑动");
                canChildPullDown = false;
				/*悬浮状态，向上滑动 ↑*/
                likeXY = LikeXY.likeNo;
                return super.dispatchTouchEvent(event);
            }
			/*处理滑动事件：结束*/
        } else if (MotionEvent.ACTION_UP == event.getAction()) {
            if (indicatorLocationYInScreen == (topLayout.getHeight() + statusHeight)) {
                if (!canChildPullDown) {
                    canChildPullDown = true;
                    likeXY = LikeXY.likeNo;
                    return true;
                }
            } else if (indicatorLocationYInScreen < (topLayout.getHeight() + statusHeight)) {
                canChildPullDown = false;
            }

        }
        //KLog.e("4444");
        lastY = event.getRawY();
        likeXY = LikeXY.likeNo;
        return super.dispatchTouchEvent(event);
    }

    /**
     * 更新滑动的进度
     */
    private void updateScrollProgress(int indicatorScrollDistanceCurr) {
        if (onFloatScrollListener != null) {
            /**
             * 悬浮菜单的Scroll进度[0, 1]，向上滑动进度增加，向下滑动进度减小
             */
            float progress = ((float) indicatorScrollDistanceCurr / (float) indicatorScrollDistanceMax);
            onFloatScrollListener.onFloatTitleScroll(indicatorScrollDistanceMax, indicatorScrollDistanceCurr, progress);
        }
    }

    /**
     * 获得状态栏的高度
     */
    private int getStatusBarHeight() {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            Log.e(VIEW_LOG_TAG, "e = " + e);
        }
        return statusHeight;
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

    public void setOnFloatScrollListener(OnFloatScrollListener onFloatScrollListener) {
        this.onFloatScrollListener = onFloatScrollListener;
    }

    /**
     * 设置悬浮导航的，指示器的 上边距，单位dp
     *
     * @param indicatorMarginTop 高度 单位dp
     */
    public void setIndicatorMarginTop(float indicatorMarginTop) {
        this.indicatorMarginTop = (int) dp2Px(indicatorMarginTop);
    }

    public interface OnFloatScrollListener {
        /**
         * 向上滚动  0 ---> 1
         *
         * @param maxDistance  最大滚动距离 px
         * @param currDistance 当前滚动距离 px
         * @param progress     滚动的进度 [0, 1]
         */
        void onFloatTitleScroll(int maxDistance, int currDistance, float progress);
    }

    /**
     * 手势滑动——X轴、Y轴，初始化
     */
    private static enum LikeXY {
        /**
         * 初始化
         */
         likeNo,
        /**
         * X轴方向表现明显
         */
        likeX,
        /**
         * Y轴方向表现明显
         */
        likeY;
    }

}
