package tv.newtv.cboxtv.views.detail;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.util.GsonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;


/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         15:55
 * 创建人:           weihaichao
 * 创建日期:          2018/10/25
 */
public class TvEpisodeFragment extends AbsEpisodeFragment {
    private static final int mListLayout = R.layout.episode_programe_page_item_layout;
    private static final String mItemTag = "rl_focus_30_";
    private int DEFAULT_SIZE = 30;
    private String TAG = "TvEpisodeFragment";
    private boolean hasAD = false;
    private ADHelper.AD.ADItem adItem;
    private List<SubContent> mData;
    private View contentView;
    private View firstView;
    private View lastView;
    private WeakReference<ResizeViewPager> mWeakViewPager;
    private int mPosition;
    private EpisodeChange mChange;
    private int currentIndex = -1;
    private List<ViewHolder> viewHolders = new ArrayList<>();

    @Override
    public void setAdItem(ADHelper.AD.ADItem adItem) {
        if (adItem != null && !TextUtils.isEmpty(adItem.AdUrl)) {
            setHasAD(true);
            this.adItem = adItem;
        }
    }

    public void setHasAD(boolean hasAD) {
        this.hasAD = hasAD;
    }

    @Override
    public int getPageSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public void destroy() {
        mData = null;
        contentView = null;
        firstView = null;
        lastView = null;
        mWeakViewPager.clear();
        mChange = null;
        if (viewHolders != null && !viewHolders.isEmpty()) {
            for (ViewHolder viewholder : viewHolders) {
                viewholder.destroy();
            }
            viewHolders.clear();
        }

        viewHolders = null;
    }

    @Override
    public void clear() {
        currentIndex = -1;
    }

    @Override
    public void setViewPager(ResizeViewPager viewPager, int position, EpisodeChange change) {
        mWeakViewPager = new WeakReference<ResizeViewPager>(viewPager);
        mWeakViewPager.get().setUseResize(false);
        mChange = change;
        mPosition = position;
    }

    @Override
    public int getCurrentIndex() {
        return currentIndex;
    }

    @Override
    public void requestDefaultFocus() {
        if (currentIndex == -1) {
            if (firstView != null) {
                firstView.requestFocus();
            }
        } else {
            if (viewHolders.size() > 0 && viewHolders.size() > currentIndex) {
                if (viewHolders.get(currentIndex).itemView != null) {
                    viewHolders.get(currentIndex).itemView.requestFocus();
                }
            }
        }
    }

    @Override
    public void setSelectIndex(int index) {
        Log.d(TAG, "setSelectIndex index=" + index);
        currentIndex = index;
        if (contentView != null && currentIndex >= 0 && viewHolders != null && !viewHolders
                .isEmpty()) {
            contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (viewHolders != null && viewHolders.size() > currentIndex) {
                        viewHolders.get(currentIndex).select();
                    }
                }
            }, 100);
        }
    }

    @Override
    public void setData(List<SubContent> data) {
        mData = data;
        //得到数据更新数据
        Log.e(TAG, mData.toString());
        updateUI();
    }

    private void updateUI() {
        if (mData != null && contentView != null) {
            Resources resources = contentView.getContext().getResources();
            for (int index = 0; index < DEFAULT_SIZE; index++) {
                String model = mItemTag + (index + 1);
                int id = resources.getIdentifier(model, "id", contentView.getContext()
                        .getPackageName());
                View view = contentView.findViewById(id);
                if (index == 0) {
                    firstView = view;
                } else if (index == DEFAULT_SIZE - 1) {
                    lastView = view;
                }

                if (hasAD) {
                    if (index == 0) {
                        if (view != null) {
                            updateHolder(view, adItem);
                            view.setVisibility(View.VISIBLE);
                        }
                    } else {
                        updateItem(view, index - 1);
                    }
                } else {
                    updateItem(view, index);
                }
            }
        }
    }

    private <T> void updateHolder(View view, T t) {
        BaseHolder holder = null;
        if (view.getTag(R.id.id_view_tag) == null) {
            holder = new ADHolder(view);
            view.setTag(R.id.id_view_tag, holder);
        } else {
            holder = (ADHolder) view.getTag(R.id.id_view_tag);
        }
        holder.update(t);
    }

    private void updateItem(View view, int index) {
        SubContent item = getData(index);
        if (item != null) {
            if (view != null) {
                ViewHolder holder = null;
                if (view.getTag(R.id.id_view_tag) == null) {
                    holder = new ViewHolder(view, index);
                    view.setTag(R.id.id_view_tag, holder);
                } else {
                    holder = (ViewHolder) view.getTag(R.id.id_view_tag);
                }
                holder.update(item);
                viewHolders.add(holder);
                view.setVisibility(View.VISIBLE);
            }
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private SubContent getData(int index) {
        return index < mData.size() ? mData.get(index) : null;
    }

    @Override
    public void requestFirst() {
        if (firstView != null) {
            firstView.requestFocus();
        }
    }

    @Override
    public void requestLast() {
        if (lastView != null) {
            lastView.requestFocus();
        }
    }

    @Override
    public String getTabString(int index,int endIndex) {
        if (mData.size() == 1) {
            return mData.get(0).getPeriods();
        }
        return String.format("%s-%s", mData.get(0).getPeriods(), mData.get(mData.size() - 1)
                .getPeriods());
    }

    @Nullable
    @Override
    public View createView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(mListLayout, null, false);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }


    private class ViewHolder extends TvEpisodeFragment.BaseHolder<SubContent> implements
            IEpisodePlayChange {
        int mIndex;

        ViewHolder(View view, final int postion) {
            super(view);
            mIndex = postion;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performClick(true,true);
                }
            });
        }

        @Override
        protected void viewFocusChange(View view, boolean b) {
            View viewMove = view.findViewWithTag("tag_poster_title");
            if (viewMove != null) {
                viewMove.setSelected(b);
            }
        }

        public void destroy() {
            mFocusView = null;
            mTitleView = null;
            itemView = null;
            mImageView = null;
        }

        void select(){
            if(mChange != null){
                mChange.updateUI(this,mPosition * getPageSize() + mIndex);
            }
        }

        void performClick(boolean fromClick,boolean dispatch) {
            if (mChange != null) {
                mChange.updateUI(this, mPosition * getPageSize() + mIndex);
                mChange.onChange(this, mPosition * getPageSize() + mIndex, fromClick);
            }
        }


        public void update(SubContent programsInfo) {
            if (programsInfo != null) {
                itemView.setVisibility(View.VISIBLE);

                if (mTitleView != null) {
                    mTitleView.setText(programsInfo.getPeriods());
                }
                //为剧集页添加vip功能  1 单点包月  3vip  4单点
//                int vipFlag = Integer.parseInt(programsInfo.getVipFlag());
                if (TextUtils.equals("3",programsInfo.getVipFlag())){
                    mImageView.setVisibility(View.VISIBLE);
                }else {
                    mImageView.setVisibility(View.GONE);
                }
            } else {
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public void setIsPlay(boolean value) {
            if (value) {
                mTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color.color_62c0eb));
            } else {
                mTitleView.setTextColor(ContextCompat.getColor(getContext(), R.color
                        .detail_tvcolor));
            }
            mTitleView.postInvalidate();
        }
    }


    private class ADHolder extends TvEpisodeFragment.BaseHolder<ADHelper.AD.ADItem> {

        ADHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mData != null && !TextUtils.isEmpty(mData.eventContent)) {
                        AdEventContent adEventContent = GsonUtil.fromjson(mData.eventContent,
                                AdEventContent.class);
                        JumpUtil.activityJump(getContext(), adEventContent.actionType,
                                adEventContent.contentType,
                                adEventContent.contentUUID, adEventContent.actionURI);
                    }
                }
            });
        }

        @Override
        public void update(ADHelper.AD.ADItem adItem) {

        }
    }

    private class BaseHolder<T> {
        protected View itemView;
        TextView mTitleView;
        ViewGroup mFocusView;
        ImageView mImageView;
        T mData;

        BaseHolder(View view) {
            this.itemView = view;
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    viewFocusChange(view, b);
                    if (b) {
                        mFocusView.setBackgroundResource(R.drawable.icon_details_series_focus);
                    } else {
                        mFocusView.setBackgroundResource(R.color.color_transparent);
                        mTitleView.setBackgroundResource(R.drawable.shape_radius_vip);
                    }
                }
            });
            mFocusView = view.findViewWithTag("tag_img_focus");
            mTitleView = view.findViewWithTag("tag_poster_title");
            mImageView = view.findViewWithTag("tag_img_vip");
        }

        protected void viewFocusChange(View view, boolean b) {
        }

        public void update(T item) {
            this.mData = item;
        }
    }
}
