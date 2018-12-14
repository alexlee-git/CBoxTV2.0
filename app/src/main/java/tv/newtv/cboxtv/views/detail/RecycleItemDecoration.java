package tv.newtv.cboxtv.views.detail;

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
public class RecycleItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;
    private int mHeadFoot;

    public RecycleItemDecoration(int space,int headFoot){
        mSpace = space;
        mHeadFoot  = headFoot;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
            outRect.right = -view.getPaddingRight() - mSpace + mHeadFoot;
        }else{
            outRect.right = -view.getPaddingRight() - mSpace;
        }
        outRect.left = -view.getPaddingLeft() - mSpace;


        outRect.top = -view.getPaddingTop();
        outRect.bottom = -view.getPaddingBottom();
    }
}
