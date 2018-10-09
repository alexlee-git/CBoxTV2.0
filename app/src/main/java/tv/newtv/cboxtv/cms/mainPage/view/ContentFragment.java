package tv.newtv.cboxtv.cms.mainPage.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Page;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.Navigation;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.NewTVViewPager;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalAdapter;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.views.ScrollSpeedLinearLayoutManger;

/**
 * Created by lixin on 2018/1/23.
 */

public class ContentFragment extends BaseFragment implements PageContract.View {

    private String param;
    private String contentId;
    private String parentId;

    @SuppressWarnings("unused")
    private String defaultFocus;

    @SuppressWarnings("unused")
    private String actionType;

    private AiyaRecyclerView mRecyclerView; // 推荐位容器
    private TextView mEmptyView;
    private List<Page> mDatas; // 数据源--即组件列表
    private SharedPreferences mSharedPreferences;

    private View contentView;
    private TextView loadingView;

    @SuppressWarnings("unused")
    private boolean isPrepared = false;

    private boolean isFromNav = false;


    private boolean BodyScrolling = false;
    private boolean ImScrolling = false;

    private UniversalAdapter adapter;

    public static ContentFragment newInstance(Bundle paramBundle) {
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
            mRecyclerView = null;
        }
        if (adapter != null) {
            adapter.destroyItem();
            adapter.destroy();
            adapter = null;
        }
        loadingView = null;
        contentView = null;
        mSharedPreferences = null;
        mDatas = null;

    }

    public boolean isNoTopView() {
        if (mRecyclerView != null) {
            LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            if (manager != null) {
                if (manager.findFirstVisibleItemPosition() == 0 && mRecyclerView.getScrollState()
                        == RecyclerView
                        .SCROLL_STATE_IDLE) {
                    View view = FocusFinder.getInstance().findNextFocus((ViewGroup) mRecyclerView
                                    .getChildAt(0),
                            mRecyclerView.getChildAt(0).findFocus(), View.FOCUS_UP);
                    return view == null;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onBackPressed() {
        if (mRecyclerView != null) {
            ScrollSpeedLinearLayoutManger linearLayoutManager = (ScrollSpeedLinearLayoutManger)
                    mRecyclerView.getLayoutManager();
            if (mRecyclerView.computeVerticalScrollOffset() != 0) {
                linearLayoutManager.smoothScrollToPosition(mRecyclerView, 0);
            }
        }
        return true;
    }

    private void onScrollStateChange() {
        Log.d("contentFragment", "onScrollStateChange() param=" + param + " BodyScrolling=" +
                BodyScrolling +
                " " +
                "ImScrolling=" + ImScrolling + " isVisible=" + isVisible);
//        if (!BodyScrolling && !ImScrolling && isVisible) {
//            Picasso.with(getContext()).resumeTag(contentId);
//        } else {
//            Picasso.with(getContext()).pauseTag(contentId);
//        }
    }

    @Override
    public void setViewPager(NewTVViewPager viewPager) {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
                if (positionOffset == 0f) {
                    BodyScrolling = false;
                } else {
                    BodyScrolling = true;
                }
                onScrollStateChange();
            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.d("select page index");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    BodyScrolling = false;
                } else {
                    BodyScrolling = true;
                }
                onScrollStateChange();
            }
        });
    }

    @Override
    public View getFirstFocusView() {
        if (mRecyclerView != null && mRecyclerView.getAdapter() != null && mRecyclerView
                .getChildAt(0)
                != null) {
            String tag = ((UniversalAdapter) mRecyclerView.getAdapter()).getFirstViewId();
            if (TextUtils.isEmpty(tag)) return null;
            return mRecyclerView.getChildAt(0).findViewWithTag(tag);
        }
        return null;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        parseBundle(getArguments());
    }

    private void parseBundle(Bundle bundle) {
        if (bundle != null) {
            param = bundle.getString("nav_text");
            contentId = bundle.getString("content_id");
            parentId = bundle.getString("nav_parent_contentid");
            actionType = bundle.getString("actionType");
            isFromNav = bundle.getBoolean("is_from_nav", false);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        Navigation.get().setCurrentUUID(contentId);
        LogUtils.e(ContentFragment.class.getSimpleName(), "onVisible : " + param);

//        if (mPresenter != null) {
//            ModuleInfoResult moduleInfoResult = mPresenter.getModuleInfoResult();
//            if (moduleInfoResult != null) {
//                changeBG(moduleInfoResult);
//            }
//        }
    }

    @Override
    public void onEnterComplete() {
        super.onEnterComplete();

//        Picasso.with(LauncherApplication.AppContext).resumeTag(contentId);
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        LogUtils.e(ContentFragment.class.getSimpleName(), "onInvisible : " + param);
    }

    @Override
    public Context getContext() {
        return LauncherApplication.AppContext;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        initSharedPreferences();
        defaultFocus = mSharedPreferences.getString("page-defaultFocus", "");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mNotifyNoPageDataListener != null) {
            mNotifyNoPageDataListener.notifyNoPageData(false);
        }
        LogUtils.e(Constant.TAG, "onDestroyView navText : " + param);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.layout_content_fragment, container, false);
        } else {
            ViewGroup parentGroup = (ViewGroup) contentView.getParent();
            if (parentGroup != null) {
                parentGroup.removeView(contentView);
            }
        }
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isPrepared) {
            mEmptyView = (TextView) contentView.findViewById(R.id.id_empty_view);
            mRecyclerView = (AiyaRecyclerView) contentView.findViewById(R.id
                    .id_content_fragment_root);

            Log.d("contentFragment", "onViewCreated param=" + param + " recyle=" + mRecyclerView);


            if (mRecyclerView == null) {
                LogUtils.e("mRecyclerView == null");
                return;
            }

            mRecyclerView.setCanReversMove(false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAlign(AiyaRecyclerView.ALIGN_START);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        ImScrolling = false;
                    } else {
                        ImScrolling = true;
                    }
                    onScrollStateChange();
                }
            });
            loadingView = contentView.findViewById(R.id.id_loading_view);

            mRecyclerView.setItemAnimator(null);
            isPrepared = true;

            int pxSize = getResources().getDimensionPixelSize(R.dimen.width_60px);

            if (mRecyclerView != null) {
                setAnimRecyclerView(mRecyclerView);
            }

            //如果首页有专题或者page页面，则调整高度
            if (isFromNav && contentView != null) {
                FrameLayout frameLayout = contentView.findViewById(R.id.content_framelayout);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout
                        .getLayoutParams();
                layoutParams.topMargin = DisplayUtils.translate(pxSize, 1);
            }

            updateRecycleView();

            new PageContract.ContentPresenter(getContext(), this);
        }


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();

        if (mDatas != null && mDatas.size() > 0 && mRecyclerView != null && mRecyclerView.getAdapter
                () != null && mRecyclerView.getAdapter().getItemCount() > 0) {
            return;
        } else {
            if (loadingView != null)
                loadingView.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(contentId)) {
            onError(LauncherApplication.AppContext,"暂无数据内容。");
        } else {

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        if (contentView != null) {
//            BitmapUtil.recycleImageBitmap((ViewGroup) contentView);
//            ((ViewGroup) contentView).removeAllViews();
//        }
//        contentView = null;
//        mRecyclerView = null;
//        if (mDatas != null) {
//            mDatas.clear();
//            mDatas = null;
//        }
    }

    //创建共享参数，存储一些需要的信息
    private void initSharedPreferences() {
        mSharedPreferences = getContext().getSharedPreferences("config", 0);
    }

    /**
     * 设置empty view的可见性, 该emptyview用于提示页面数据获取异常
     *
     * @param visibility
     */
    private void setTipVisibility(int visibility) {
        if (mEmptyView != null) {
            mEmptyView.setVisibility(visibility);
        }
    }

    public void inflateContentPage(List<Page> pageList, String dataFrom) {
        setTipVisibility(View.GONE);

        if (mDatas == null) {
            mDatas = pageList;
        } else {
            mDatas.clear();
            mDatas.addAll(pageList);
        }
        // 设置背景图片

        updateRecycleView();
    }


    private void updateRecycleView() {
        if (contentView == null || mRecyclerView == null || mDatas == null) return;

        adapter = (UniversalAdapter) mRecyclerView.getAdapter();
        if (adapter == null) {
            ScrollSpeedLinearLayoutManger layoutManager = new ScrollSpeedLinearLayoutManger
                    (LauncherApplication.AppContext);
            layoutManager.setSpeed(0.08f);
            layoutManager.setSmoothScrollbarEnabled(true);
            mRecyclerView.setLayoutManager(layoutManager);

            adapter = new UniversalAdapter(LauncherApplication.AppContext, mDatas);
            adapter.setPicassoTag(contentId);
            adapter.setPlayerUUID(contentId);
            Log.d("contentFragment", "setAdapter param=" + param + " data=" + mDatas);
            mRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        Log.d("contentFragment", "updateRecycleView recyle=" + mRecyclerView);

        contentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loadingView != null)
                    loadingView.setVisibility(View.GONE);

                if (mDatas == null || mDatas.size() == 0) {
                    onError(LauncherApplication.AppContext, "数据为空");
                }

            }
        }, 10);
    }

    public AiyaRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void destroyItem() {
        if (adapter != null) {
            adapter.destroyItem();
        }
    }

    @Override
    public void onPageResult(List<Page> page) {
        inflateContentPage(page, "server");
    }

    @Override
    public void setPresenter(@NotNull PageContract.Presenter presenter) {
        presenter.getPageContent(contentId);
    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {
        loadingView.setText("暂无数据内容");
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }
}
