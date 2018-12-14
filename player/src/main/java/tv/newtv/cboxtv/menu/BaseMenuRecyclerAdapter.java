package tv.newtv.cboxtv.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by TCP on 2018/5/17.
 */

public abstract class BaseMenuRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T>{
    protected Context context;
    /**
     * 当前level左边的为到当前节点的路径view
     *
     */
    protected View selectView;
    /**
     * adapter中第一个的view
     */
    protected View firstPositionView = null;
    /**
     * 从根节点到当前正在播节点，存在一条路径
     * 该值就是这条路径上对应的view
     */
    protected View pathView;

    public BaseMenuRecyclerAdapter(Context context){
        this.context = context;
    }

    public View getFirstPositionView() {
        return firstPositionView;
    }

    public View getSelectView() {
        return selectView;
    }

    public void setSelectView(View selectView) {
        this.selectView = selectView;
    }

    public View getPathView() {
        return pathView;
    }
}
