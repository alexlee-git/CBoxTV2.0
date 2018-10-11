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
public class RecycleSpaceDecoration extends RecyclerView.ItemDecoration {
    private int leftRight;
    private int topBottom;

    public RecycleSpaceDecoration(int tb,int lr) {
        topBottom = tb;
        leftRight = lr;
    }

    public int getLRSpace(){
        return leftRight;
    }

    public int getTBSpace(){
        return topBottom;
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
                outRect.bottom = topBottom;
            }
            outRect.left = leftRight;
            outRect.right = leftRight;
        } else {
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.right = 0;
            } else {
                outRect.right = leftRight;
            }
            outRect.top = topBottom;
            outRect.bottom = topBottom;
        }
    }
}
