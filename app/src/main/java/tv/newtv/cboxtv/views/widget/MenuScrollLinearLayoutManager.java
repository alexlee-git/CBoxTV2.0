package tv.newtv.cboxtv.views.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.libs.util.ScreenUtils;


/**
 * 项目名称:         CBoxTV
 * 包名:            tv
 * 创建事件:         19:12
 * 创建人:           weihaichao
 * 创建日期:          2018/4/2
 */

public class MenuScrollLinearLayoutManager extends LinearLayoutManager {
    private static final int MAX_AUTO_RESIZE_COUNT = 5;
    private float MILLISECONDS_PER_INCH = 2.8f;  //修改可以改变数据,越大速度越慢
    private Context contxt;
    private int mSize = 0;
    private boolean mAutoResize = true;

    public void destroy(){
        contxt = null;
    }

    MenuScrollLinearLayoutManager(Context context) {
        super(context, LinearLayoutManager.HORIZONTAL, false);
        this.contxt = context;
    }

    public void setAutoResize(boolean value) {
        mAutoResize = value;
    }

    public void setDataSize(int size) {
        mSize = size;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int
            position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return MenuScrollLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    //This returns the milliseconds it takes to
                    //scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.density;
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    //可以用来设置速度
    public void setSpeedSlow(float x) {
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = contxt.getResources().getDisplayMetrics().density * 0.3f + (x);
    }


    public void changeHolderList(RecyclerView.ViewHolder holder) {
        if (holder instanceof MenuRecycleView.MenuViewHolder) {
            ((MenuRecycleView.MenuViewHolder) holder).checkHidden();
        }
    }

    private int getHiddenInt(int selectPos) {
        return selectPos + 2;
    }

    public boolean isHidden(int selectPos, int position) {
        if (mAutoResize) {
            if (mSize < MAX_AUTO_RESIZE_COUNT && mSize % 2 == 0) {
                int hidden = getHiddenInt(selectPos);
                Log.e("SLinearLayoutManager", "select->" + selectPos + " position->" + position
                        + " hidden->" + hidden);
                return position >= hidden;
            }
        }
        return false;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int
            widthSpec, int heightSpec) {
        if (getChildCount() > 0 && mAutoResize) {
            View firstChildView = getChildAt(0);
            RecyclerView parentView = (RecyclerView) firstChildView.getParent();
            measureChild(firstChildView, widthSpec, heightSpec);
            Log.e("SLinearLayoutManager", "childCount=" + getChildCount());

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)
                    firstChildView.getLayoutParams();
            ViewGroup.LayoutParams parentLayout = ((RecyclerView) firstChildView.getParent())
                    .getLayoutParams();

            int tabSize = mSize;
            if (mSize != MAX_AUTO_RESIZE_COUNT) {
                tabSize = MAX_AUTO_RESIZE_COUNT;
            }

            int measureSize = (firstChildView.getMeasuredWidth() + layoutParams
                    .leftMargin + layoutParams.rightMargin) * tabSize;

            int width = measureSize > ScreenUtils
                    .getScreenW() ? ScreenUtils.getScreenW() : measureSize;
            setMeasuredDimension(width, View.MeasureSpec.getSize(heightSpec));

            parentLayout.width = width;
            parentView.setLayoutParams(parentLayout);
            parentView.requestLayout();
        } else {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
        }
    }
}