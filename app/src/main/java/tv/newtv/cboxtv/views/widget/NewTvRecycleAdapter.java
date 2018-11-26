package tv.newtv.cboxtv.views.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static tv.newtv.cboxtv.views.widget.NewTvRecycleAdapter.NewTvViewHolder.Callback;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         14:41
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 */
public abstract class NewTvRecycleAdapter<D, H extends NewTvRecycleAdapter.NewTvViewHolder>
        extends RecyclerView.Adapter<H> {

    private static final String TAG = NewTvRecycleAdapter.class.getSimpleName();
    private int currentIndex = 0;
    private Callback mCallback = new Callback() {
        @Override
        public void onItemClick(int pos, NewTvViewHolder holder) {
            notifyItemChanged(currentIndex);
            currentIndex = pos;
            notifyItemChanged(pos);
            NewTvRecycleAdapter.this.onItemClick(getItem(pos), pos);
        }

        @Override
        public void onItemFocusChange(View view, int pos, boolean hasFocus) {
            NewTvRecycleAdapter.this.onFocusChange(view, pos, hasFocus);
        }
    };

    public int getSelectedIndex(){
        return currentIndex;
    }

    public abstract List<D> getDatas();

    public abstract H createHolder(ViewGroup parent, int viewType);

    public abstract void bind(D data, H holder, boolean selected);

    public abstract void onItemClick(D data, int position);

    public abstract void onFocusChange(View view, int position, boolean hasFocus);

    public boolean requestDefaultFocus() {
        return false;
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int viewType) {
        return createHolder(parent, viewType);
    }

    public void setSelectedIndex(int index) {
        if(currentIndex == index) return;
        currentIndex = index;
        notifyDataSetChanged();
    }

    /**
     * 检测是否被遮住显示不全
     *
     * @return
     */
    protected boolean isCover(View view) {
        boolean cover = false;
        Rect rect = new Rect();
        cover = view.getGlobalVisibleRect(rect);
        if (cover) {
            if (rect.width() >= view.getMeasuredWidth() && rect.height() >= view.getMeasuredHeight
                    ()) {
                return !cover;
            }
        }
        return true;
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        D item = getItem(holder.getAdapterPosition());
        if (item != null) {
            boolean select = holder.getAdapterPosition() == currentIndex;
            bind(item, holder, select);
            holder.setCallback(mCallback);
        } else {
            Log.e(TAG, String.format("Current position = %d value is null", holder
                    .getAdapterPosition()));
        }
    }

    @Override
    public void onViewRecycled(H holder) {
        super.onViewRecycled(holder);
        holder.setCallback(null);
    }

    private D getItem(int position) {
        List<D> mDatas = getDatas();
        if (mDatas == null) return null;
        if (mDatas.size() < position) return null;
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        List<D> mDatas = getDatas();
        return mDatas != null ? mDatas.size() : 0;
    }

    public static abstract class NewTvViewHolder extends RecyclerView.ViewHolder {

        private Callback mCallback;

        public NewTvViewHolder(View itemView) {
            super(itemView);
        }

        void setCallback(Callback callback) {
            mCallback = callback;
        }

        protected void performClick() {

            if (mCallback != null) {
                mCallback.onItemClick(getAdapterPosition(), this);
            }
        }

        protected void performFocus(boolean hasFocus) {
            if (mCallback != null) {
                mCallback.onItemFocusChange(itemView, getAdapterPosition(), hasFocus);
            }
        }

        interface Callback {
            void onItemClick(int pos, NewTvViewHolder viewHolder);

            void onItemFocusChange(View target, int pos, boolean hasFocus);
        }
    }
}
