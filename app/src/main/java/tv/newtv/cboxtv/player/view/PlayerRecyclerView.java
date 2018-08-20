package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wangkun on 2018/1/23.
 */

public class PlayerRecyclerView extends RecyclerView {
    public PlayerRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public PlayerRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        this.setChildrenDrawingOrderEnabled(true);
        this.addItemDecoration(new SpaceItemDecoration(24));

    }
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View view = this.getFocusedChild();
        int focusedItemIdx = this.indexOfChild(view);
        if (focusedItemIdx <= -1) {
            return i;
        }

        if (focusedItemIdx == i) {
            return childCount - 1;
        } else if (i == childCount - 1) {
            return focusedItemIdx;
        } else {
            return i;
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

//            if(parent.getChildPosition(view) != 0)
                outRect.right = space;
        }
    }
}
