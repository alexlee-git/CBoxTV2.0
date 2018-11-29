package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.AdContract;
import com.newtv.cms.contract.ContentContract;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.reactivex.disposables.Disposable;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.player.PlayerConfig;


/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         13:02
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class EpisodePageView extends RelativeLayout implements IEpisode, EpisodeChange,
        AdContract.View, ContentContract.View {
    private static final int DEFAULT_SIZE = 8;
    private static final int DEFAULT_LIST_SIZE = 30;
    private static final int HAS_AD_SIZE = 7;

    private static final String INFO_TEXT_TAG = "info_text";
    private ResizeViewPager ListPager;
    private FragmentManager mFragmentManager;
    private String mContentUUID;
    private AiyaRecyclerView aiyaRecyclerView;
    private EpisodePageAdapter pageItemAdapter;
    private OnEpisodeChange mOnEpisodeChange;
    private int currentIndex = 0;
    private List<AbsEpisodeFragment> fragments;
    private TextView leftDir, rightDir;
    private IEpisodePlayChange mCurrentPlayImage;
    private View mControlView;
    private boolean move = true;
    private LinearLayoutManager mLinearLayoutManager;
    private View TitleView;
    private Disposable mDisposable;
    private SmallWindowView smallWindowView;
    private Content seriesContent;

    private int mEpisodeType;

    private List<SubContent> mContentList;

//    private AdContract.Presenter adPresenter;
    private ContentContract.Presenter mContentPresenter;
    private int mPageSize;
    private String mVideoType;
    private TextView mTitleView;
    private TextView mUpTitle;

    public EpisodePageView(Context context) {
        this(context, null);
    }

    public EpisodePageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EpisodePageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
//        getAD();
    }

    private void getAD() {
//        adPresenter = new AdContract.AdPresenter(getContext(), this);
//        adPresenter.getAdByChannel(Constant.AD_DESK, Constant.AD_DETAILPAGE_CONTENTLIST, "",
//                PlayerConfig
//                        .getInstance().getFirstChannelId(), PlayerConfig.getInstance()
//                        .getSecondChannelId
//                                (), PlayerConfig.getInstance().getTopicId(), null);
    }

    @Override
    public void destroy() {
//        if (adPresenter != null) {
//            adPresenter.destroy();
//            adPresenter = null;
//        }
        if (mContentPresenter != null) {
            mContentPresenter.destroy();
            mContentPresenter = null;
        }
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (aiyaRecyclerView != null) {
            aiyaRecyclerView = null;
        }
        if (ListPager != null) {
            ListPager.destroy();
            ListPager = null;
        }
        mOnEpisodeChange = null;
        if (pageItemAdapter != null) {
            pageItemAdapter.release();
        }
        pageItemAdapter = null;
        if (fragments != null && fragments.size() > 0) {
            for (AbsEpisodeFragment episodeFragment : fragments) {
                episodeFragment.destroy();
            }
            fragments.clear();
        }
        fragments = null;
        mControlView = null;
        removeAllViews();
    }

    //设置播放index
    public void setCurrentPlayIndex(final int index) {
        currentIndex = index;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int page = 0;
                if (index >= mPageSize) {
                    page = index / mPageSize;
                }
                int selectIndex = index >= mPageSize ? index % mPageSize : index;
                if (ListPager != null)
                    ListPager.setCurrentItem(page, selectIndex);
            }
        }, 300);
    }

    public void resetProgramInfo() {
        if (mOnEpisodeChange != null && mContentList != null) {
            mOnEpisodeChange.onGetProgramSeriesInfo(mContentList);
        }
    }

    public void setOnEpisodeChange(OnEpisodeChange onEpisodeChange) {
        mOnEpisodeChange = onEpisodeChange;
    }

    private void ShowInfoTextView(String text) {
        TextView infoText = findViewWithTag(INFO_TEXT_TAG);
        if (!TextUtils.isEmpty(text)) {
            if (infoText == null) {
                infoText = new TextView(getContext().getApplicationContext());
                infoText.setTag(INFO_TEXT_TAG);
                infoText.setTextAppearance(getContext(), R.style.ModuleTitleStyle);
                infoText.setText(text);
                LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                        .WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                infoText.setLayoutParams(layoutParams);
                addView(infoText, layoutParams);
            } else {
                infoText.setText(text);
            }
        } else {
            if (infoText != null) {
                removeView(infoText);
            }
        }
    }

    public void moveToPosition(int n) {
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        if (aiyaRecyclerView == null) return;
        //然后区分情况
        if (n <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            aiyaRecyclerView.smoothScrollToPosition(n);
        } else if (n <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int top = aiyaRecyclerView.getChildAt(n - firstItem).getLeft();
            aiyaRecyclerView.scrollBy(top, 0);
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            aiyaRecyclerView.smoothScrollToPosition(n);
            //这里这个变量是用在RecyclerView滚动监听里面的
            move = true;
        }

    }

    private void initialize(Context context) {
        ShowInfoTextView("正在加载...");

        TitleView = LayoutInflater.from(context).inflate(R.layout.episode_header_ad_layout, this,
                false);
        smallWindowView = TitleView.findViewById(R.id.small_window_view);
        TitleView.setId(R.id.id_detail_title);
        LayoutParams title_layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        TitleView.setLayoutParams(title_layoutParams);
        addView(TitleView, title_layoutParams);

        TitleView.findViewById(R.id.id_title_icon).setVisibility(View.VISIBLE);
        mTitleView = TitleView.findViewById(R.id.id_title);
        mUpTitle = TitleView.findViewById(R.id.up_title);
        if (mTitleView != null) {
            mTitleView.setVisibility(View.VISIBLE);
            mTitleView.setText("播放列表");
        }

        ListPager = new ResizeViewPager(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.id_detail_title);
        layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.height_22px);
        ListPager.setId(R.id.id_viewpager);

        PagerScroller pagerScroller = new PagerScroller(getContext());
        pagerScroller.attachToViewPager(ListPager, 500);
        ListPager.setLayoutParams(layoutParams);
        ListPager.addOnPageChange(new ResizeViewPager.OnPageChange() {
            @Override
            public void onChange(int prePage, int currentPage, int totalPage) {
                if (totalPage == 1) {
                    leftDir.setVisibility(View.GONE);
                    rightDir.setVisibility(View.GONE);
                    return;
                }
                if (currentPage == 0) {
                    leftDir.setVisibility(View.GONE);
                    rightDir.setVisibility(View.VISIBLE);
                } else if (currentPage == ListPager.getAdapter().getCount() - 1) {
                    rightDir.setVisibility(View.GONE);
                    leftDir.setVisibility(View.VISIBLE);
                } else {
                    leftDir.setVisibility(View.VISIBLE);
                    rightDir.setVisibility(View.VISIBLE);
                }
            }
        });
        ListPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
                ListPager.requestLayout();
            }

            @Override
            public void onPageSelected(final int position) {
                if (pageItemAdapter.getSelectedIndex() == position) return;
                pageItemAdapter.setSelectedIndex(position);
                aiyaRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if (move) {
                                move = false;
                                moveToPosition(position);
                            }


                        }

                    }
                });

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {

                }
            }
        });
        addView(ListPager, layoutParams);

        leftDir = new TextView(getContext());
        int dir_width = getResources().getDimensionPixelOffset(R.dimen.width_25px);
        int dir_height = getResources().getDimensionPixelOffset(R.dimen.width_41px);
        LayoutParams left_layoutParam = new LayoutParams(dir_width, dir_height);
        //  left_layoutParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        left_layoutParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            left_layoutParam.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        }
        left_layoutParam.leftMargin = dir_width;
//        left_layoutParam.topMargin = getResources().getDimensionPixelOffset(R.dimen.height_300px);
        leftDir.setBackgroundResource(R.drawable.icon_detail_tips_left);
        leftDir.setLayoutParams(left_layoutParam);
        addView(leftDir, left_layoutParam);
        leftDir.setVisibility(View.INVISIBLE);
        rightDir = new TextView(getContext());
        LayoutParams right_layoutParam = new LayoutParams(dir_width, dir_height);
        right_layoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            right_layoutParam.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        }
        right_layoutParam.rightMargin = dir_width;
        rightDir.setBackgroundResource(R.drawable.icon_detail_tips_right);
        rightDir.setLayoutParams(right_layoutParam);
        addView(rightDir, right_layoutParam);
        rightDir.setVisibility(View.INVISIBLE);

        aiyaRecyclerView = new AiyaRecyclerView(context, false);
        int height = context.getResources().getDimensionPixelOffset(R.dimen.height_70px);
        LayoutParams aiyaLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, height);
        mLinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager
                .HORIZONTAL, false);
        aiyaRecyclerView.setLayoutManager(mLinearLayoutManager);
        aiyaRecyclerView.setAlign(AiyaRecyclerView.ALIGN_CENTER);
        aiyaRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) aiyaRecyclerView.getItemAnimator()).setSupportsChangeAnimations
                (false);
        int margin = context.getResources().getDimensionPixelOffset(R.dimen.width_120px);
        aiyaLayoutParam.addRule(RelativeLayout.BELOW, R.id.id_viewpager);
        aiyaLayoutParam.leftMargin = margin;
        aiyaLayoutParam.rightMargin = margin;
        aiyaRecyclerView.setLayoutParams(aiyaLayoutParam);
        addView(aiyaRecyclerView, aiyaLayoutParam);

        pageItemAdapter = new EpisodePageAdapter();
        pageItemAdapter.setOnItemClick(new EpisodePageAdapter.OnItemClick() {
            @Override
            public void onClick(int position, View view) {
                ListPager.setCurrentItem(position);
                aiyaRecyclerView.setFocusView(view);
            }
        });
        aiyaRecyclerView.setAdapter(pageItemAdapter);
    }

    public void setContentUUID(Content content, int episodeType, String videoType, FragmentManager
            manager, String uuid, View controlView) {
        seriesContent = content;
        mFragmentManager = manager;
        mContentUUID = uuid;
        mControlView = controlView;
        mEpisodeType = episodeType;
        mVideoType = videoType;

        if (content.getData() == null) {
            mContentPresenter = new ContentContract.ContentPresenter
                    (getContext(), this);
            mContentPresenter.getSubContent(mContentUUID);
        } else {
            parseResult(content.getData());
        }
    }

    private void onLoadError(String message) {
        ShowInfoTextView(message);
    }

    private void parseResult(List<SubContent> results) {
        if (results == null || results.size() == 0) {
            onLoadError("获取结果为空");
            return;
        }
        try {
            mContentList = new ArrayList<>(results);

            if (CmsUtil.isVideoTv(seriesContent)) {
                final boolean sortDesc = "0".equals(seriesContent.isFinish());
                Collections.sort(mContentList, new Comparator<SubContent>() {
                    @Override
                    public int compare(SubContent t1, SubContent t2) {
                        if (sortDesc) {
                            return Integer.parseInt(t2.getPeriods()) - Integer.parseInt
                                    (t1.getPeriods());
                        }
                        return Integer.parseInt(t1.getPeriods()) - Integer.parseInt(t2.getPeriods
                                ());
                    }
                });
            }

            if (mContentList != null && mContentList.size() > 0) {
                if (mControlView != null) {
                    mControlView.setVisibility(View.VISIBLE);
                }
                setVisibility(View.VISIBLE);
                ShowInfoTextView("");

                if (mContentList.size() > 0) {
                    if (mControlView != null) {
                        mControlView.setVisibility(View.VISIBLE);
                    }
                    setVisibility(View.VISIBLE);
                    ShowInfoTextView("");

                    if (!videoType(mVideoType)) {
                        mPageSize = DEFAULT_LIST_SIZE;
                    } else {
                        mPageSize = DEFAULT_SIZE;
                    }

                    initFragment(mPageSize);
                }
            } else {
                onLoadError("暂时没有数据");
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
            onLoadError(e.getMessage());
        }
    }

    /**
     *
     */
    private void initFragment(int mPageSize) {
        int size = mContentList.size();
        fragments = new ArrayList<>();
        boolean tvSeries = !videoType(mVideoType);
        List<EpisodePageAdapter.PageItem> pageItems = new ArrayList<>();
        for (int index = 0; index < size; index += mPageSize) {
            int endIndex = index + mPageSize;
            if (endIndex > size) {
                endIndex = size;
            }
            AbsEpisodeFragment episodeFragment;
            if (tvSeries) {
                mPageSize = DEFAULT_LIST_SIZE;
                episodeFragment = new TvEpisodeFragment();
                leftDir.setVisibility(GONE);
                rightDir.setVisibility(GONE);
            } else {
                episodeFragment = new SeriesEpisodeFragment();
            }
//            episodeFragment.setAdItem(adPresenter.getAdItem());
            episodeFragment.setData(mContentList.subList(index, endIndex));
            episodeFragment.setViewPager(ListPager, fragments.size(), this);
            fragments.add(episodeFragment);
            pageItems.add(new EpisodePageAdapter.PageItem(episodeFragment.getTabString(index,endIndex)));
        }
        EpisodeAdapter episodeAdapter = new EpisodeAdapter(mFragmentManager, fragments);
        ListPager.setAdapter(episodeAdapter);
        pageItemAdapter.setPageData(pageItems, aiyaRecyclerView).notifyDataSetChanged();
        requestLayout();
    }

    private boolean videoType(String videoType) {
        if (!TextUtils.isEmpty(videoType) && (TextUtils.equals(videoType, "电视剧")
                || TextUtils.equals(videoType, "动漫"))) {
            return false;
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;

        if (TitleView != null) {
            if (!videoType(mVideoType)) {
                mTitleView.setText("剧集列表");
                mTitleView.setVisibility(VISIBLE);

                if (seriesContent != null && "0".equals(seriesContent.isFinish())) {//没有更新完
                    mUpTitle.setText("已更新" + seriesContent.getRecentNum() + "集");
                } else if(seriesContent != null && "1".equals(seriesContent.isFinish())){
                    mUpTitle.setText("共"+seriesContent.getSeriesSum()+"集已剧终");
                }
            }else {
                if (seriesContent != null && "0".equals(seriesContent.isFinish())) {//没有更新完
                    mUpTitle.setText("更新到"+seriesContent.getUpdateDate()+"第"+seriesContent.getRecentNum()+"期");
                } else if(seriesContent != null && "1".equals(seriesContent.isFinish())){
                    mUpTitle.setText("已收官");
                }
            }
            LayoutParams layoutParams = (LayoutParams) TitleView.getLayoutParams();
            TitleView.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height += TitleView.getMeasuredHeight() + layoutParams.topMargin + layoutParams
                    .bottomMargin;
        }
        if (ListPager != null) {
            LayoutParams layoutParams = (LayoutParams) ListPager.getLayoutParams();
            ListPager.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height += ListPager.getMeasuredHeight() + layoutParams.topMargin + layoutParams
                    .bottomMargin;

            if (!videoType(mVideoType)) {
                leftDir.setVisibility(GONE);
                rightDir.setVisibility(GONE);
            } else {
                if (leftDir != null) {
                    LayoutParams leftParams = (LayoutParams) leftDir.getLayoutParams();
                    leftParams.topMargin = ListPager.getTop() + (ListPager.getMeasuredHeight() -
                            leftDir
                                    .getMeasuredHeight()) / 2;
                    leftDir.setLayoutParams(leftParams);
                }

                if (rightDir != null) {
                    LayoutParams rightParams = (LayoutParams) rightDir.getLayoutParams();
                    rightParams.topMargin = ListPager.getTop() + (ListPager.getMeasuredHeight() -
                            rightDir
                                    .getMeasuredHeight()) / 2;
                    rightDir.setLayoutParams(rightParams);
                }
            }
        }
        if (aiyaRecyclerView != null) {
            LayoutParams layoutParams = (LayoutParams) aiyaRecyclerView.getLayoutParams();
            aiyaRecyclerView.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height += aiyaRecyclerView.getMeasuredHeight() + layoutParams.topMargin +
                    layoutParams.bottomMargin;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);


    }

    @Override
    public String getContentUUID() {
        return mContentUUID;
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        if (ListPager.getChildCount() == 0) return false;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (aiyaRecyclerView.hasFocus()) {
                    return false;
                }
                if (!ListPager.hasFocus() && !aiyaRecyclerView.hasFocus()) {
                    ListPager.requestDefaultFocus();
                    return true;
                }
                View view = FocusFinder.getInstance().findNextFocus(ListPager, ListPager.
                        findFocus(), View.FOCUS_DOWN);
                if (view == null) {
                    final LinearLayoutManager layoutManager = (LinearLayoutManager) aiyaRecyclerView
                            .getLayoutManager();
                    int first = layoutManager.findFirstVisibleItemPosition();
                    int last = layoutManager.findLastVisibleItemPosition();
                    final int select = ListPager.getCurrentItem();
                    if (select >= first && select <= last) {
                        aiyaRecyclerView.getChildAt(select - first).requestFocus();
                    } else {
                        /**
                         * aiyaRecyclerView中的对应item未显示出来，需要先滚动到select位置，在获取焦点
                         */
                        aiyaRecyclerView.scrollToPosition(select);
                        MainLooper.get().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                int first = layoutManager.findFirstVisibleItemPosition();
                                aiyaRecyclerView.getChildAt(select - first).requestFocus();
                            }
                        }, 50);

                    }
                    return true;
                } else {
                    view.requestFocus();
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (!ListPager.hasFocus() && !aiyaRecyclerView.hasFocus() && !smallWindowView
                        .hasFocus()) {
                    if (aiyaRecyclerView.getDefaultFocusView() != null)
                        aiyaRecyclerView.getDefaultFocusView().requestFocus();
                    return true;
                }
                View view = FocusFinder.getInstance().findNextFocus(this, findFocus(),
                        View.FOCUS_UP);
                if (view != null) {
                    view.requestFocus();
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View focusView = null;
                ViewGroup parentView = null;
                if (aiyaRecyclerView.hasFocus()) {
                    focusView = aiyaRecyclerView.findFocus();
                    parentView = aiyaRecyclerView;
                } else if (ListPager.hasFocus()) {
                    focusView = ListPager.findFocus();
                    parentView = ListPager;
                } else if (smallWindowView.hasFocus()) {
                    return smallWindowView.interruptKeyEvent(event);
                }

                View target = FocusFinder.getInstance().findNextFocus(parentView, focusView, View
                        .FOCUS_RIGHT);
                if (target != null) {
                    target.requestFocus();
                } else {
                    if (ListPager.hasFocus()) {
                        if (ListPager.getCurrentItem() < ListPager.getAdapter().getCount() - 1) {
                            ListPager.setCurrentItem(ListPager.getCurrentItem() + 1);
                        }
                        return true;
                    }
                }
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                View focusView = null;
                ViewGroup parentView = null;
                if (aiyaRecyclerView.hasFocus()) {
                    focusView = aiyaRecyclerView.findFocus();
                    parentView = aiyaRecyclerView;
                } else if (ListPager.hasFocus()) {
                    focusView = ListPager.findFocus();
                    parentView = ListPager;
                } else if (smallWindowView.hasFocus()) {
                    return smallWindowView.interruptKeyEvent(event);
                }

                View target = FocusFinder.getInstance().findNextFocus(parentView, focusView, View
                        .FOCUS_LEFT);
                if (target != null) {
                    target.requestFocus();
                } else {
                    if (ListPager.hasFocus()) {
                        if (ListPager.getCurrentItem() > 0) {
                            ListPager.setCurrentItem(ListPager.getCurrentItem() - 1);
                        }
                        return true;
                    }
                }
                return true;
            }
        }
        return false;
    }


    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {

    }

    @Override
    public void showAd(@Nullable String type, @Nullable String url, @Nullable HashMap<?, ?>
            hashMap) {
        if (!TextUtils.isEmpty(url)) {
            mPageSize = mPageSize - 1;
            if (mContentList != null
                    && mContentList.size() > 0) {
                initFragment(mPageSize);
            }
        }
    }

    @Override
    public void updateTime(int total, int left) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {

    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {
        parseResult(result);
    }

    @Override
    public void updateUI(IEpisodePlayChange playChange, int index) {
        if (mCurrentPlayImage != null) {
            mCurrentPlayImage.setIsPlay(false);
        }
        mCurrentPlayImage = playChange;
        if (playChange != null) {
            playChange.setIsPlay(true);
        }
    }

    @Override
    public void onChange(IEpisodePlayChange playChange, int index, boolean fromClick) {
        currentIndex = index;
        if (mOnEpisodeChange != null) {
            mOnEpisodeChange.onChange(index, fromClick);
        }
    }

    public interface OnEpisodeChange {
        void onGetProgramSeriesInfo(List<SubContent> seriesInfo);

        void onChange(int index, boolean fromClick);
    }


}
