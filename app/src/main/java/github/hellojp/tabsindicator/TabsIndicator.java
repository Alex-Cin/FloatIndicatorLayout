package github.hellojp.tabsindicator;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alex.floatindicatorlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hellojp
 * @link https://github.com/helloJp/TabIndicator
 * @improver Alex 
 * @SDK  2.1
 */
public class TabsIndicator extends LinearLayout implements View.OnClickListener, OnPageChangeListener {
	private static final String TAG = "TabsIndicator" ;
	private final int BASE_ID = 0xffff00;
	private ColorStateList textColor;
	private int textSizeNormal = 12;
	private int textSizeSelected = 15;
	private int lineColor = android.R.color.white;
	private int lineHeight = 5;
	private int lineMarginTab = 20;
	private boolean hasDivider = true;
	private int dividerColor = android.R.color.black;
	private int dividerWidth = 3;
	private int dividerVerticalMargin = 10;
	private int linePosition = 1;
	private boolean isAnimation = true;
	private Paint paintLine;
	private ViewPager viewPager;
	private int tabCount = 0;
	private int currentTabIndex;
	private int lineMarginX = 0;
	private Path path = new Path();
	private int tabWidth;
	private Context context;
	private OnPageChangeListener onPageChangeListener;
	private List<StateListDrawable> tabIcons;
	private int textBackgroundResId = R.drawable.hellojp_background_tab;

	public TabsIndicator(Context context) {
		this(context, null);
	}

	public TabsIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(context, attrs);
		initPaint();
	}

	@SuppressWarnings("ResourceAsColor")
	private void initView(Context context, AttributeSet attrs) {
		currentTabIndex = 0;
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabsIndicator);
		//indicator line
		lineColor = ta.getColor(R.styleable.TabsIndicator_lineColor, lineColor);
		lineMarginTab = ta.getDimensionPixelOffset(R.styleable.TabsIndicator_lineMarginTab, lineMarginTab);
		lineHeight = ta.getDimensionPixelOffset(R.styleable.TabsIndicator_lineHeight, lineHeight);
		linePosition = ta.getInt(R.styleable.TabsIndicator_linePosition, 1);
		//tabs text
		textColor = ta.getColorStateList(R.styleable.TabsIndicator_textColor);
		textSizeNormal = ta.getDimensionPixelOffset(R.styleable.TabsIndicator_textSizeNormal, textSizeNormal);
		textSizeSelected = ta.getDimensionPixelOffset(R.styleable.TabsIndicator_textSizeSelected, textSizeSelected);
		//divider between tabs
		hasDivider = ta.getBoolean(R.styleable.TabsIndicator_hasDivider, hasDivider);
		dividerColor = ta.getColor(R.styleable.TabsIndicator_dividerColor, dividerColor);
		dividerWidth = ta.getDimensionPixelOffset(R.styleable.TabsIndicator_dividerWidth, dividerWidth);
		dividerVerticalMargin = ta.getDimensionPixelOffset(R.styleable.TabsIndicator_dividerVerticalMargin, dividerVerticalMargin);
		textBackgroundResId = ta.getResourceId(R.styleable.TabsIndicator_textBackgroundResId, textBackgroundResId);
		ta.recycle();
	}

	@SuppressWarnings("ResourceAsColor")
	private void initPaint() {
		paintLine = new Paint();
		paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
		paintLine.setColor(lineColor);
	}

	public void setViewPager(int index, ViewPager viewPager) {
		this.removeAllViews();
		this.viewPager = viewPager;
		PagerAdapter pagerAdapter = this.viewPager.getAdapter();
		if (pagerAdapter == null) {
			Log.e(TAG, "setViewPager: viewPager 调用viewPager.setAdapter(adapter); ");
			return;
		}
		tabCount = pagerAdapter.getCount();
		this.viewPager.setOnPageChangeListener(this);
		initTabs(index);
		postInvalidate();
	}

	@SuppressWarnings("ResourceAsColor")
	private void initTabs(int index) {
		if (getBackground() == null) {
			setBackgroundColor(Color.parseColor("#009688"));
		}
		for (int i = 0; i < tabCount; i++) {
			TextView tvTab = new TextView(context);
			tvTab.setId(BASE_ID + i);
			tvTab.setOnClickListener(this);
			tvTab.setBackgroundResource(textBackgroundResId);
			tvTab.setGravity(Gravity.CENTER);
			if (null != viewPager.getAdapter().getPageTitle(i)) {
				if (null != textColor) {
					tvTab.setTextColor(textColor);
				}
				tvTab.setText(viewPager.getAdapter().getPageTitle(i));
			}
			if (tabIcons != null && tabIcons.size() > i) {
				StateListDrawable drawable = tabIcons.get(i);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				tvTab.setCompoundDrawables(null, drawable, null, null);
				tvTab.setCompoundDrawablePadding(0);
				tvTab.setPadding(0, 10, 0, 0);
			}
			LayoutParams tabLp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
			tabLp.gravity = Gravity.CENTER;
			tvTab.setLayoutParams(tabLp);
			this.addView(tvTab);

			if (i == 0) {
				resetTab(tvTab, false);
			}
			if (i != tabCount - 1 && hasDivider) {
				LayoutParams dividerLp = new LayoutParams(dividerWidth, LayoutParams.MATCH_PARENT);
				dividerLp.setMargins(0, dividerVerticalMargin, 0, dividerVerticalMargin);
				View vLine = new View(getContext());
				vLine.setBackgroundColor(dividerColor);
				vLine.setLayoutParams(dividerLp);
				this.addView(vLine);
			}
		}
		setCurrentTab(index);
	}

	private void setCurrentTab(int index) {

		if (currentTabIndex != index && index > -1 && index < tabCount) {
			TextView oldTab = (TextView) findViewById(BASE_ID + currentTabIndex);
			resetTab(oldTab, false);
			currentTabIndex = index;
			TextView newTab = (TextView) findViewById(BASE_ID + currentTabIndex);
			resetTab(newTab, true);
			if (viewPager.getCurrentItem() != currentTabIndex) {
				viewPager.setCurrentItem(currentTabIndex, isAnimation);
			}
			postInvalidate();
		}

		if(currentTabIndex==0 && index == 0){
			TextView textView = (TextView) findViewById(BASE_ID + 0);
			resetTab(textView, true);
		}
	}

	private void resetTab(TextView tvTab, boolean isSelected) {
		tvTab.setTextSize(TypedValue.COMPLEX_UNIT_PX, isSelected ? textSizeSelected : textSizeNormal);
		tvTab.setSelected(isSelected);
	}

	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

	@Override
	public void onClick(View v) {
		int currentTabIndex = v.getId() - BASE_ID;
		setCurrentTab(currentTabIndex);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		path.rewind();
		//TODO
		float leftX = lineMarginTab + lineMarginX;
		float rightX = leftX + tabWidth - 2 * lineMarginTab;
		float topY = 0;
		float bottomY = 0;
		switch (linePosition) {
			case 0:
				topY = 0;
				bottomY = lineHeight;
				break;
			case 1:
				topY = getHeight() - lineHeight;
				bottomY = getHeight();
				break;
		}

		path.moveTo(leftX, topY);
		path.lineTo(rightX, topY);
		path.lineTo(rightX, bottomY);
		path.lineTo(leftX, bottomY);
		path.close();

		canvas.drawPath(path, paintLine);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		tabWidth = getWidth();
		if (tabCount != 0) {
			tabWidth = getWidth() / tabCount;
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		lineMarginX = (int) (tabWidth * (position + positionOffset));

		postInvalidate();

		if (onPageChangeListener != null) {
			onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	@Override
	public void onPageSelected(int position) {
		setCurrentTab(position);

		if (onPageChangeListener != null) {
			onPageChangeListener.onPageSelected(position);
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (onPageChangeListener != null) {
			onPageChangeListener.onPageScrollStateChanged(state);
		}
	}

	public void setAnimationWithTabChange(boolean isAnimation) {
		this.isAnimation = isAnimation;
	}

	public void addTabIcon(int normalDrawableResId, int selectedDrawableResId) {
		StateListDrawable sld = new StateListDrawable();
		Drawable normal = normalDrawableResId == -1 ? null : context.getResources().getDrawable(normalDrawableResId);
		Drawable select = selectedDrawableResId == -1 ? null : context.getResources().getDrawable(selectedDrawableResId);
		sld.addState(new int[]{android.R.attr.state_selected}, select);
		sld.addState(new int[]{}, normal);
		if (tabIcons == null) {
			tabIcons = new ArrayList<StateListDrawable>();
		}
		tabIcons.add(sld);
	}
}