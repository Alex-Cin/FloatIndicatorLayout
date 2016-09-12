package github.alex.utils;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;

public class DisplayUtil
{
	public static int getViewWidth(View view){
		if(view == null){
			return -1;
		}
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int measuredWidth = view.getMeasuredWidth();
		return measuredWidth;
	}
	public static int getViewHeight(View view){
		if(view == null){
			return -1;
		}
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		int measuredHeight = view.getMeasuredHeight();
		return measuredHeight;
	}
	/**数据转换: dp---->px*/
	public static float dp2Px(Context context, float dp) 
	{
		if (context == null) {
			return -1;
		}
		return dp * context.getResources().getDisplayMetrics().density;
	}

}
