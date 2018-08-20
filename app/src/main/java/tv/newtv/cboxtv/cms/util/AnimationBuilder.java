package tv.newtv.cboxtv.cms.util;

import android.view.animation.ScaleAnimation;

public class AnimationBuilder {

	private static AnimationBuilder mInstance;
	private AnimationBuilder() {}
	
	public static AnimationBuilder getInstance() {
		if (mInstance == null) {
			synchronized (AnimationBuilder.class) {
				if (mInstance == null) {
					mInstance = new AnimationBuilder();
				}
			}
		}
		return mInstance;
	}
	
	public ScaleAnimation getScaleAnimation(float fromX, float toX,
                                            float fromY, float toY, int pivotXType, float pivotXValue,
                                            int pivotYType, float pivotYValue, int duration) {
		
		ScaleAnimation sa = new ScaleAnimation(fromX, toX, fromY, toY,
				                               pivotXType, pivotXValue, 
				                               pivotYType, pivotYValue);
		sa.setDuration(duration);
		sa.setFillAfter(true);
		return sa;
	}
}
