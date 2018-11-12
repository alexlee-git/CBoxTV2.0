package tv.newtv.cboxtv.cms.mainPage.menu;

/**
 * Created by gaoleichao on 2018/3/13.
 */

import android.support.v7.widget.RecyclerView;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseRecyclerAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    protected final List<T> mList = new LinkedList<>();

    /**
     * 获取列表数据
     */
    public List<T> getList() {
        return mList;
    }

    public void appendToTop(T t) {
        if (t == null) {
            return;
        }
        mList.add(0, t);
        notifyItemInserted(0);
    }

    public void append(T t) {
        if (t == null) {
            return;
        }
        mList.add(t);
        notifyDataSetChanged();
    }

    public void replaceItem(int position, T item) {
        mList.set(position, item);
    }

    public void removieBottom() {
        if (mList.size() > 0) {
            mList.remove(mList.size() - 1);
            notifyDataSetChanged();
        }
    }

    public void removieByIndex(int index) {
        mList.remove(index);
        notifyItemRemoved(index);
        notifyDataSetChanged();
    }

    /**
     * 追加数据
     *
     * @param list
     */
    public void appendToList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(list);
        notifyItemRangeInserted(getItemCount(), mList.size());
    }


    /**
     * 追加数据
     *
     * @param list
     */
    public void appendToTopList(List<T> list) {
        if (list == null) {
            return;
        }
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void clear() {
        int i = getItemCount();
        mList.clear();
//        notifyItemRangeRemoved(0, i);
        notifyDataSetChanged();
    }


    public void deleteAll() {
        mList.clear();
    }

    public void replace(List<T> list) {
        if (list == null) {
            return;
        }
        mList.clear();
        mList.addAll(0, list);
        notifyDataSetChanged();
    }

    /**
     * 获取数量
     */
    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    /**
     * 根据position 获取单个对象
     */

    @Nullable
    public T getItem(int position) {
        // TODO Auto-generated method stub
        if (position > mList.size() - 1 || position<0) {
            return null;
        }
        return mList.get(position);
    }

    /**
     * 获取position
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}