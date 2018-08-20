package tv.newtv.cboxtv.cms.listPage.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ListMenuView extends ListView {
	private int lastSelectedItemPosition = 0;
	
	public ListMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ListMenuView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		lastSelectedItemPosition = getSelectedItemPosition();
		int lastSelectItem = getSelectedItemPosition();
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		if (gainFocus) {
//			setSelection(lastSelectedItemPosition);
			View other = getChildAt(lastSelectItem - getFirstVisiblePosition());
			int top = (other == null) ? 0 : other.getTop();
			setSelectionFromTop(lastSelectItem, top);
		}
	}
}
