package tv.newtv.cboxtv.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv
 * 创建事件:         13:33
 * 创建人:           weihaichao
 * 创建日期:          2018/4/18
 */
public class NewTvMenuAdapter<T extends RecyclerView.ViewHolder, W> extends RecyclerView
        .Adapter<T> {

    private static final int MAX_COUNT = 2000;
    private MenuCreator<T, W> menuCreator;
    private int mLayoutId;
    private LayoutInflater mInflater;
    private List mValues;
    private onFocusChange mFocusChange;

    NewTvMenuAdapter(Context context, MenuCreator<T, W> creator, int layoutId) {
        menuCreator = creator;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
    }

    public void setFocusChange(onFocusChange focusChange) {
        mFocusChange = focusChange;
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(mLayoutId, parent, false);
        view.setFocusable(true);
        return menuCreator.createViewHolder(view, viewType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(final T holder, int position) {
        if (mValues == null) return;
        int targetPosition = position % mValues.size();
        if(mValues.size() <= targetPosition) return;
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mFocusChange.onFocus
                        (view, b,holder.getAdapterPosition());
            }
        });
        menuCreator.onBindViewHolder(holder, (W) mValues.get(targetPosition),position);
    }

    public void setData(List datas) {
        mValues = datas;
        notifyDataSetChanged();
    }

    public int getDataSize(){
        return mValues != null ? mValues.size() : 0;
    }

    @Override
    public int getItemCount() {
        return mValues != null ? MAX_COUNT - MAX_COUNT % mValues.size() : 0;
    }

    public interface onFocusChange {
        void onFocus(View target, boolean hasFocus,int position);
    }

    public interface MenuCreator<T, W> {
        T createViewHolder(View view, int viewType);

        void onBindViewHolder(T holder, W data,int id);
    }
}
