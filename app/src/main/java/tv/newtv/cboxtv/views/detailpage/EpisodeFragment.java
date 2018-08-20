package tv.newtv.cboxtv.views.detailpage;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.utils.ScaleUtils;
import tv.newtv.cboxtv.views.CurrentPlayImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:54
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class EpisodeFragment extends Fragment {

    private static final String TAG = EpisodeFragment.class.getSimpleName();
    private List<ProgramSeriesInfo.ProgramsInfo> mData;
    private View contentView;
    private View firstView;
    private View lastView;
    private WeakReference<ResizeViewPager> mWeakViewPager;
    private int mPosition;
    private EpisodeChange mChange;
    private int currentIndex = -1;
    private List<ViewHolder> viewHolders = new ArrayList<>();

    public EpisodeFragment() {
        super();
    }

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

    public void clear(){
        currentIndex = -1;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void setViewPager(ResizeViewPager viewPager, int position, EpisodeChange change) {
        mWeakViewPager = new WeakReference<>(viewPager);
        mChange = change;
        mPosition = position;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

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

    public void setSelectIndex(final int index) {
        Log.d(TAG,"setSelectIndex index="+index);
        currentIndex = index;
        if (contentView != null && currentIndex >= 0 && viewHolders != null && !viewHolders
                .isEmpty()) {
            contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (viewHolders != null && viewHolders.size() > currentIndex) {
                        viewHolders.get(currentIndex).performClick();
                    }
                }
            }, 100);
        }
    }

    public void setData(List<ProgramSeriesInfo.ProgramsInfo> data) {
        mData = data;
        updateUI();
    }

    public void requestFirst() {
        if (firstView != null)
            firstView.requestFocus();
    }

    public void requestLast() {
        if (lastView != null) {
            lastView.requestFocus();
        }
    }

    private void updateUI() {
        if (mData != null && contentView != null) {
            Resources resources = getContext().getResources();
            int Level = 0;
            for (int index = 0; index < 8; index++) {
                String model = "id_module_8_view" + (index + 1);
                int id = resources.getIdentifier(model, "id",
                        getContext().getPackageName());
                View view = contentView.findViewById(id);
                if (index == 0) {
                    firstView = view;
                } else if (index == 7) {
                    lastView = view;
                }
                ProgramSeriesInfo.ProgramsInfo item = getData(index);
                if (item != null) {
                    if (view != null) {
                        if (view.getTop() != Level) {
                            Level += view.getTop();
                        }
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
        }
    }

    private ProgramSeriesInfo.ProgramsInfo getData(int index) {
        return index < mData.size() ? mData.get(index) : null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.episode_page_item, null, false);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateUI();
    }

    private class ViewHolder {
        CurrentPlayImageView PosterView;
        ImageView FocusView;
        TextView TitleView;
        int mIndex;
        private View itemView;

        ViewHolder(View view, int postion) {
            itemView = view;
            mIndex = postion;
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    View viewMove  = view.findViewWithTag("tag_poster_title");
                    if (viewMove!=null){
                        viewMove.setSelected(b);
                    }

                    FocusView.setVisibility(b ? View.VISIBLE : View.GONE);
                    if (b) {
                        ScaleUtils.getInstance().onItemGetFocus(itemView);
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(itemView);
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performClick();
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
        }

        public void destroy() {
            FocusView = null;
            PosterView = null;
            TitleView = null;
            itemView = null;
        }

        public void performClick() {
            if (mChange != null) {
                mChange.onChange(PosterView, mPosition * 8 + mIndex);
            }
        }

        private void update(ProgramSeriesInfo.ProgramsInfo programsInfo) {
            if (programsInfo != null) {
                itemView.setVisibility(View.VISIBLE);
                if (PosterView != null) {
                    PosterView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (!TextUtils.isEmpty(programsInfo.gethImage())) {
                        Picasso.with(getContext())
                                .load(programsInfo.gethImage())
                                .transform(new PosterCircleTransform(getActivity(), 4))
                                .placeholder(R.drawable.focus_384_216)
                                .error(R.drawable.focus_384_216)
                                .resize(384, 216)
                                .into(PosterView);
                    } else {
                        Picasso.with(getContext())
                                .load(R.drawable.focus_384_216)
                                .resize(384, 216)
                                .transform(new PosterCircleTransform(getActivity(), 4))
                                .into(PosterView);
                    }
                }
                if (TitleView != null) {
                    TitleView.setText(programsInfo.getTitle());
                }

            } else {
                itemView.setVisibility(View.GONE);
            }
        }
    }
}
