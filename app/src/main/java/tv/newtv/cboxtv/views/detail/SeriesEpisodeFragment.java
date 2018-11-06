package tv.newtv.cboxtv.views.detail;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.views.custom.CurrentPlayImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:54
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class SeriesEpisodeFragment extends AbsEpisodeFragment {
    private static final int DEFAULT_SIZE = 8;
    private static final int HAS_AD_SIZE = 7;
    private static final String TAG = SeriesEpisodeFragment.class.getSimpleName();
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

    private int mLayoutId = R.layout.episode_page_item;
    private String mItemTag = "id_module_8_view";

    @Override
    public int getPageSize() {
        return DEFAULT_SIZE;
    }

    @Override
    public String getTabString() {
        if (mData.size() == 1) {
            return mData.get(0).getPeriods();
        }
        return String.format("%s-%s", mData.get(0).getPeriods(), mData.get(mData.size() - 1)
                .getPeriods());
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

    public void setLayout(int layout, String itemTag) {
        mLayoutId = layout;
        mItemTag = itemTag;
    }

    @Override
    public void clear() {
        currentIndex = -1;
    }

    @Override
    public void setViewPager(ResizeViewPager viewPager, int position, EpisodeChange change) {
        mWeakViewPager = new WeakReference<>(viewPager);
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
    public void setSelectIndex(final int index) {
        Log.d(TAG, "setSelectIndex index=" + index);
        currentIndex = index;
        if (contentView != null && currentIndex >= 0 && viewHolders != null && !viewHolders
                .isEmpty()) {
            contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (viewHolders != null && viewHolders.size() > currentIndex) {
                        viewHolders.get(currentIndex).performClick(false);
                    }
                }
            }, 100);
        }
    }

    @Override
    public void setData(List<SubContent> data) {
        mData = data;
        updateUI();
    }

    @Override
    public void requestFirst() {
        if (firstView != null)
            firstView.requestFocus();
    }

    @Override
    public void requestLast() {
        if (lastView != null) {
            lastView.requestFocus();
        }
    }

    private void updateUI() {
        if (mData != null && contentView != null) {
            Resources resources = getContext().getResources();
            for (int index = 0; index < DEFAULT_SIZE; index++) {
                String model = mItemTag + (index + 1);
                int id = resources.getIdentifier(model, "id", getContext().getPackageName());
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

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(mLayoutId, null, false);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();
    }

    private class ViewHolder extends BaseHolder<SubContent> implements IEpisodePlayChange {
        int mIndex;

        ViewHolder(View view, int postion) {
            super(view);
            mIndex = postion;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performClick(true);
                }
            });
        }

        @Override
        protected void onFocusChange(View view, boolean b) {
            View viewMove = view.findViewWithTag("tag_poster_title");
            if (viewMove != null) {
                viewMove.setSelected(b);
            }
        }

        public void destroy() {
            FocusView = null;
            PosterView = null;
            TitleView = null;
            itemView = null;
        }

        void performClick(boolean fromClick) {
            if (mChange != null) {
                mChange.onChange(this, mPosition * getPageSize() + mIndex, fromClick);
            }
        }

        public void update(SubContent programsInfo) {
            if (programsInfo != null) {
                itemView.setVisibility(View.VISIBLE);
                if (PosterView != null) {
                    PosterView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (!TextUtils.isEmpty(programsInfo.getHImage())) {
                        Picasso.get()
                                .load(programsInfo.getHImage())
                                .transform(new PosterCircleTransform(LauncherApplication
                                        .AppContext, 4))
                                .placeholder(R.drawable.focus_384_216)
                                .error(R.drawable.focus_384_216)
                                .resize(384, 216)
                                .into(PosterView);
                    } else {
                        Picasso.get()
                                .load(R.drawable.focus_384_216)
                                .resize(384, 216)
                                .transform(new PosterCircleTransform(LauncherApplication
                                        .AppContext, 4))
                                .into(PosterView);
                    }
                }
                if (TitleView != null) {
                    TitleView.setText(programsInfo.getTitle());
                }

                if(("1".equals(programsInfo.getVipFlag()) || "2".equals(programsInfo.getVipFlag())) && vipView != null){
                    vipView.setVisibility(View.VISIBLE);
                }
            } else {
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public void setIsPlay(boolean value) {
            PosterView.setIsPlaying(value);
        }
    }


    private class ADHolder extends BaseHolder<ADHelper.AD.ADItem> {

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
            if (!TextUtils.isEmpty(adItem.AdUrl)) {
                Picasso.get()
                        .load(adItem.AdUrl)
                        .resize(384, 216)
                        .transform(new PosterCircleTransform(LauncherApplication.AppContext, 4))
                        .into(PosterView);
            }
        }
    }

    private class BaseHolder<T> {
        protected View itemView;
        CurrentPlayImageView PosterView;
        ImageView FocusView;
        TextView TitleView;
        T mData;
        protected ImageView vipView;

        BaseHolder(View view) {
            this.itemView = view;
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    BaseHolder.this.onFocusChange(view, b);
                    FocusView.setVisibility(b ? View.VISIBLE : View.GONE);
                    if (b) {
                        ScaleUtils.getInstance().onItemGetFocus(itemView);
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(itemView);
                    }
                }
            });
            PosterView = view.findViewWithTag("tag_poster_image");
            ViewGroup.LayoutParams posterLayoutPara = PosterView.getLayoutParams();

            PosterView.setLayoutParams(posterLayoutPara);
            PosterView.requestLayout();


            FocusView = view.findViewWithTag("tag_img_focus");

            ViewGroup.LayoutParams layoutParams = FocusView.getLayoutParams();
            layoutParams.height = PosterView.getLayoutParams().height + 2 * getResources()
                    .getDimensionPixelOffset(R.dimen.width_17dp);
            layoutParams.width = PosterView.getLayoutParams().width + 2 * getResources()
                    .getDimensionPixelOffset(R.dimen.width_17dp);
            FocusView.setLayoutParams(layoutParams);
            FocusView.requestLayout();
            TitleView = view.findViewWithTag("tag_poster_title");
            vipView = view.findViewWithTag("tag_img_vip");
        }

        protected void onFocusChange(View view, boolean b) {
        }

        public void update(T item) {
            this.mData = item;
        }
    }

}
