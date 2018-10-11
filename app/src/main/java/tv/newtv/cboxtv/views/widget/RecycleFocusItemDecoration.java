package tv.newtv.cboxtv.views.widget;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         14:36
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class RecycleFocusItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public RecycleFocusItemDecoration(int space){
        mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //竖直方向的
        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.bottom = 0;
            } else {
                outRect.bottom = view.getPaddingTop() * 2 + mSpace;
            }
            outRect.left = 0;
            outRect.right = 0;
        } else {
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.right = 0;
            } else {
                outRect.right = -view.getPaddingLeft() * 2 + mSpace;
            }
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}
