package tv.newtv.cboxtv.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CommonSpacesItemDecoration extends RecyclerView.ItemDecoration{

	private int left;
	private int top;
	private int right;
	private int bottom;

	public CommonSpacesItemDecoration(int left,int top,int right,int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
							   RecyclerView.State state) {
		outRect.left = left;
		outRect.top = top;
		outRect.right = right;
		outRect.bottom = bottom;
	}

}
