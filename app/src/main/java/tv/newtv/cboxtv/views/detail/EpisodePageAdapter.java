package tv.newtv.cboxtv.views.detail;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         15:41
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class EpisodePageAdapter extends RecyclerView.Adapter<EpisodePageAdapter
        .EpisodePageViewHolder> {
    private List<PageItem> mPageItems;
    private int selectedIndex = 0;
    private OnItemClick onItemClickAction;
    private int layoutIndex = 0;
    private WeakReference<AiyaRecyclerView> weakRecycleView;

    public void release(){
        if(weakRecycleView != null){
            weakRecycleView.clear();
        }
        weakRecycleView = null;
        onItemClickAction = null;
        if(mPageItems != null){
            mPageItems.clear();
        }
        mPageItems = null;
    }

    public EpisodePageAdapter setPageData(List<PageItem> pageItems, AiyaRecyclerView recyclerView) {
        mPageItems = pageItems;
        weakRecycleView = new WeakReference<>(recyclerView);
        return this;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(final int index) {
        if (index == selectedIndex) return;
        notifyItemChanged(selectedIndex);
        selectedIndex = index;
        notifyItemChanged(index);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        onItemClickAction = onItemClick;
    }

    private void setSelectedIndex(final int index, final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onItemClickAction != null)
                    onItemClickAction.onClick(selectedIndex, view);
            }
        }, 300);

    }

    @Override
    public EpisodePageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .episode_episode, parent, false);
        return new EpisodePageViewHolder(view);
    }

    private PageItem getItem(int position) {
        if (mPageItems != null && position < mPageItems.size()) {
            return mPageItems.get(position);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(EpisodePageViewHolder holder, int position) {
        PageItem pageItem = getItem(position);
        if (pageItem != null) {
            holder.setData(pageItem);
        }
    }

    @Override
    public int getItemCount() {
        return mPageItems != null ? mPageItems.size() : 0;
    }

    public interface OnItemClick {
        void onClick(int position, View view);
    }

    static class PageItem {
        public String Title;

        PageItem(String title) {
            Title = title;
        }
    }

    class EpisodePageViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private RelativeLayout focusView;

        EpisodePageViewHolder(final View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_episode_text);
            focusView = itemView.findViewById(R.id.rl_episode_focus);

            titleText.setOnFocusChangeListener(new View
                    .OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    focusView.setVisibility(b ? View.VISIBLE : View.GONE);
                    if (b) {
                        if (selectedIndex == getAdapterPosition()) return;
                        setSelectedIndex(getAdapterPosition());
                    } else {
                        if (getAdapterPosition() == selectedIndex) {
                            return;
                        }
                        titleText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color
                                .detail_tvcolor));
                    }
                }
            });
        }

        public void setData(PageItem pageItem) {
            titleText.setText(pageItem.Title);

            titleText.setTextColor(getAdapterPosition
                    () == selectedIndex ? ContextCompat.getColor(itemView.getContext
                    (),R.color.color_62c0eb): ContextCompat.getColor(itemView.getContext
                    (), R.color
                    .detail_tvcolor));
            if (selectedIndex == getAdapterPosition()) {
                layoutIndex = weakRecycleView.get().getChildLayoutPosition(itemView);
                if (layoutIndex < 0) layoutIndex = 0;
                setSelectedIndex(getAdapterPosition(), itemView);
            }

        }
    }
}
