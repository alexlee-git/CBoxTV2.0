package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.newtv.cms.bean.Page;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixin on 2018/2/1.
 */

public class UniversalAdapter extends RecyclerView.Adapter<UniversalViewHolder> {
    private static final String TAG = UniversalAdapter.class.getName();

    private List<Page> mDatas;

    private int bottomMargin = 0;
    private BlockBuilder blockBuilder;
    private List<UniversalViewHolder> holderList = new ArrayList<>();

    public UniversalAdapter(Context context, List<Page> datas) {
        mDatas = datas;
        blockBuilder = new BlockBuilder(context);
    }

    public void destroy() {
        if (mDatas != null) {
            mDatas.clear();
            mDatas = null;
        }
        if (blockBuilder != null) {
            blockBuilder.destroy();
            blockBuilder = null;
        }
        if (holderList != null) {
            for (UniversalViewHolder holder : holderList) {
                holder.destroy();
            }
            holderList.clear();
            holderList = null;
        }
    }

    public String getFirstViewId() {
        Page moduleItem = mDatas.get(0);
        String layoutCode = moduleItem.getLayoutCode(); // 形如"layout_002"
        if (TextUtils.isEmpty(layoutCode)) return null;
        String layoutId = layoutCode.substring(layoutCode.indexOf("_") + 1); // 形如"002"
        return "cell_" + layoutId + "_1";
    }

    public void setPlayerUUID(String uuid) {
        blockBuilder.setPlayerUUID(uuid);
    }

    public void setPicassoTag(String tag) {
        blockBuilder.setPicassoTag(tag);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NotNull
    @Override
    public UniversalViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        UniversalViewHolder holder = blockBuilder.onCreateViewHolder(parent, viewType);
        holderList.add(holder);
        return holder;
    }

    public void showFirstLineTitle(boolean value) {
        blockBuilder.showFirstLineTitle(value);
    }

    public void setLastItemBottomMargin(int margin) {
        bottomMargin = margin;
    }

    @Override
    public void onBindViewHolder(UniversalViewHolder holder, final int position) {
        try {
            final Page moduleItem = mDatas.get(position); // 这里mData.get(positon)拿到的是一行的信息

            if (holder.getAdapterPosition() == getItemCount() - 1) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder
                        .itemView.getLayoutParams();
                layoutParams.bottomMargin = bottomMargin;
                holder.itemView.setLayoutParams(layoutParams);
            }

            blockBuilder.build(moduleItem, holder.itemView, position);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void onViewRecycled(@NonNull UniversalViewHolder holder) {
        super.onViewRecycled(holder);
        holder.destroy();
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        Page item = mDatas.get(position);
        int viewType = blockBuilder.getItemViewType(position, item);
        if (viewType != -1) {
            return viewType;
        }
        return super.getItemViewType(position);
    }

    public void destroyItem() {
        for (UniversalViewHolder holder : holderList) {
            holder.releaseImageView();
        }
    }
}
