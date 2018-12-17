package tv.newtv.cboxtv.cms.search.custom;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.NewTVScroller;
import tv.newtv.cboxtv.cms.search.fragment.SearchBaseFragment;
import tv.newtv.cboxtv.cms.search.fragment.ColumnFragment;
import tv.newtv.cboxtv.cms.search.fragment.DramaFragment;
import tv.newtv.cboxtv.cms.search.fragment.PersonFragment;
import tv.newtv.cboxtv.cms.search.listener.SearchResultDataInfo;

/**
 * 项目名称： NewTVLauncher
 * 类描述：搜索结果页
 * 创建人：wqs
 * 创建时间： 2018/3/9 0009 12:57
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NewTVSearchResult extends RelativeLayout implements SearchResultDataInfo, View.OnFocusChangeListener {

    @BindView(R.id.id_frameLayout_result_label_drama)
    FrameLayout mDramaFrameLayout;
    @BindView(R.id.id_frameLayout_result_label_person)
    FrameLayout mPersonFrameLayout;
    @BindView(R.id.id_frameLayout_result_label_column)
    FrameLayout mColumnFrameLayout;
    @BindView(R.id.id_result_label_drama_title)
    TextView mDramaTitle;
    @BindView(R.id.id_result_label_person_title)
    TextView mPersonTitle;
    @BindView(R.id.id_result_label_column_title)
    TextView mColumnTitle;
    @BindView(R.id.id_search_result_empty_img)
    ImageView mSearchResultImg;
    @BindView(R.id.id_search_result_empty)
    TextView mSearchResultEmpty;
    @BindView(R.id.id_search_result_empty_line)
    ImageView mSearchResultEmptyLine;
    @BindView(R.id.id_result_label_person_focus_bottom)
    ImageView mPersonFocusImageView;
    @BindView(R.id.id_result_label_drama_focus_bottom)
    ImageView mDramaFocusImageView;
    @BindView(R.id.id_result_label_column_focus_bottom)
    ImageView mColumnFocusImageView;
    @BindView(R.id.id_search_result_viewpager)
    public SearchViewPager mViewpager;
    @BindView(R.id.id_result_left_arrow)
    ImageView mLeftArrow;
    @BindView(R.id.search_loading)
    LinearLayout mLoadingLayout;
    @BindView(R.id.search_loading_image)
    View mLoadingImg;

    @BindView(R.id.tab_container)
    ViewGroup tabContainer;
    private List<ViewGroup> tabs;

    private float SearchViewResultWidth = 0;

    public List<SearchBaseFragment> mFragments;
    private SearchViewPagerAdapter mViewPagerAdapter;

    public ColumnFragment mColumnFragment;
    public PersonFragment mPersonFragment;
    public DramaFragment mDramaFragment;


    public NewTVSearchResult(Context context) {
        this(context, null);
    }

    public NewTVSearchResult(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTVSearchResult(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }

    //填充布局
    private void initLayout() {
        View.inflate(this.getContext(), R.layout.newtv_search_result_page_list_result, this);
        ButterKnife.bind(this);
        init();
        resetLoadingLayout(false);
    }

    public void resetLoadingLayout(boolean keyboardIsHidden){
        int loadingLeftMargin;
        if (mLoadingLayout != null) {
            LayoutParams loadingParams = (LayoutParams) mLoadingLayout.getLayoutParams();

            if (keyboardIsHidden){
                loadingLeftMargin = (int)((ScreenUtils.getScreenW()
                        - getResources().getDimension(R.dimen.width_25px)//leftbtn width
                        - getResources().getDimension(R.dimen.width_29px)//leftbtn marginleft
                        - getResources().getDimension(R.dimen.width_10px)//recycleview paddingLeft
                        - getResources().getDimension(R.dimen.width_100px)//recycleview paddingRight
                        - getResources().getDimension(R.dimen.width_90px)//SearchViewPager marginLeft
                        - getResources().getDimension(R.dimen.width_100px))//SearchRecyclerView paddingRight
                )/2;
            }else {
                loadingLeftMargin = (int) getResources().getDimension(R.dimen.width_419px);
            }
            loadingParams.leftMargin = loadingLeftMargin;
            loadingParams.topMargin = (int) getResources().getDimension(R.dimen.width_453px);
            mLoadingLayout.setLayoutParams(loadingParams);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View focusView = findFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (tabContainer.hasFocus()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    SearchBaseFragment mFragment = mFragments.get(mViewpager.getCurrentItem());
                    if(mFragment.getSearchRecyclerView().getChildAt(0)!=null) {
                        mFragment.getSearchRecyclerView().getChildAt(0).requestFocus();
                        return true;
                    }
                }

                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }

                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    View left = FocusFinder.getInstance().findNextFocus(tabContainer, focusView, View.FOCUS_LEFT);
                    if (left != null) {
                        left.requestFocus();
                    }
                    return true;
                }

                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    View right = FocusFinder.getInstance().findNextFocus(tabContainer, focusView, View.FOCUS_RIGHT);
                    if (right != null) {
                        right.requestFocus();
                    }
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void init() {
        mViewpager.setScrollable(true);
        SearchViewResultWidth = DisplayUtils.translate((int) SearchViewResultWidth, 0);
        mViewpager.setCustomScroller(new NewTVScroller(getContext(), new LinearInterpolator(), 250));
        mFragments = new ArrayList<>();
        tabs = new ArrayList<>();

        mColumnFragment = new ColumnFragment();
        mColumnFragment.setIndex(0);
        mColumnFragment.attachDataInfoResult(this);
        mColumnFragment.setLabelView(mColumnFrameLayout);
        mColumnFragment.setLoadingLayout(mLoadingLayout,mLoadingImg);
        tabs.add(mColumnFrameLayout);
        mColumnFragment.setLabelFocusView(mColumnFocusImageView);
        mFragments.add(mColumnFragment);

        mDramaFragment = new DramaFragment();
        mDramaFragment.setIndex(1);
        mDramaFragment.attachDataInfoResult(this);
        mDramaFragment.setLabelView(mDramaFrameLayout);
        mDramaFragment.setLoadingLayout(mLoadingLayout,mLoadingImg);
        tabs.add(mDramaFrameLayout);
        mDramaFragment.setLabelFocusView(mDramaFocusImageView);
        mFragments.add(mDramaFragment);

        mPersonFragment = new PersonFragment();
        mPersonFragment.setIndex(2);
        mPersonFragment.attachDataInfoResult(this);
        mPersonFragment.setLabelView(mPersonFrameLayout);
        mPersonFragment.setLoadingLayout(mLoadingLayout,mLoadingImg);
        tabs.add(mPersonFrameLayout);
        mPersonFragment.setLabelFocusView(mPersonFocusImageView);
        mFragments.add(mPersonFragment);

        mViewPagerAdapter = new SearchViewPagerAdapter(((FragmentActivity) getContext()).getSupportFragmentManager(), mFragments);
        mViewpager.setAdapter(mViewPagerAdapter);
        mViewpager.setOffscreenPageLimit(2);

        mColumnFrameLayout.setOnFocusChangeListener(this);
        mPersonFrameLayout.setOnFocusChangeListener(this);
        mDramaFrameLayout.setOnFocusChangeListener(this);

    }

    public void requestFirstTab() {
        for (int index = 0; index < 3; index++) {
            View viewGroup = tabContainer.getChildAt(index);
            if (viewGroup.getVisibility() == View.GONE) {
                continue;
            } else {
                viewGroup.requestFocus();
                break;
            }
        }
    }

    private int findIndex(View target) {
        int position = 0;
        for (int index = 0; index < tabs.size(); index++) {
            ViewGroup viewGroup = tabs.get(index);
            if (viewGroup == target) {
                return position;
            } else if (viewGroup.getVisibility() == View.GONE) {
                continue;
            } else {
                position++;
            }
        }
        return position;
    }

    public void setKey(String key) {
        mColumnFragment.setKey(key);
        mPersonFragment.setKey(key);
        mDramaFragment.setKey(key);
    }

    public boolean isLoadComplete(){
        return !mPersonFragment.isLoading() && !mDramaFragment.isLoading() && !mColumnFragment
                .isLoading();
    }

    public void setEmptyViewVisible() {
        if (isLoadComplete()) {
            if (mFragments != null && mFragments.size() > 0) {
                mSearchResultEmpty.setVisibility(GONE);
                mSearchResultImg.setVisibility(GONE);
                mSearchResultEmptyLine.setVisibility(GONE);
            } else {
                mSearchResultEmpty.setVisibility(VISIBLE);
                mSearchResultImg.setVisibility(VISIBLE);
                mSearchResultEmptyLine.setVisibility(VISIBLE);
                if (mLoadingLayout != null){
                    mLoadingLayout.setVisibility(GONE);
                }
            }
        }else{
            mSearchResultEmpty.setVisibility(GONE);
            mSearchResultImg.setVisibility(GONE);
            mSearchResultEmptyLine.setVisibility(GONE);
        }
    }


    /**
     * 显示指定页面
     *
     * @param index 选择指定页面
     */
    public void showIndexPage(int index) {
        mViewpager.setCurrentItem(index);
    }

    public void showLeftBackView(boolean focusStatus) {
        if (focusStatus) {
            mLeftArrow.setVisibility(View.VISIBLE);
        } else {
            mLeftArrow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateFragmentList(SearchBaseFragment fragment, boolean isGone) {
        if (isGone) {
            if (mFragments.contains(fragment)) {
                mFragments.remove(fragment);
                mViewPagerAdapter.notifyDataSetChanged();
            }
        } else {
            if (!mFragments.contains(fragment)) {
                if(fragment.getIndex() == 0){
                    mFragments.add(0,fragment);
                }else if(fragment.getIndex() == 2){
                    mFragments.add(fragment);
                }else{
                    if(mFragments.size() == 0) {
                        mFragments.add(fragment);
                    }if(mFragments.size() == 1){
                        if(mFragments.get(0).getIndex() == 0){
                            mFragments.add(1,fragment);
                        }else if(mFragments.get(0).getIndex() == 2){
                            mFragments.add(0,fragment);
                        }
                    }else{
                        mFragments.add(1,fragment);
                    }
                }

                mViewPagerAdapter.notifyDataSetChanged();
            }
        }

        setEmptyViewVisible();

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mFragments.size()>0) {
                    showIndexPage(0);
                    mFragments.get(0).setUserVisibleHint(true);
                }
            }
        },300);


    }

    @Override
    public void onFocusChange(View view, boolean b) {

        switch (view.getId()) {
            case R.id.id_frameLayout_result_label_drama:
                mDramaFocusImageView.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                break;
            case R.id.id_frameLayout_result_label_column:
                mColumnFocusImageView.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                break;
            case R.id.id_frameLayout_result_label_person:
                mPersonFocusImageView.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
                break;
        }
        if (b) {
            showIndexPage(findIndex(view));
        }
    }

    public void requestDefaultFocus() {
        if (mViewPagerAdapter != null && mViewpager != null) {
            SearchBaseFragment targetFragment = mViewPagerAdapter.getFragmentByIndex(mViewpager.getCurrentItem());
            if (targetFragment != null) {
                View focusView = targetFragment.findDefaultFocus();
                if (focusView != null) {
                    focusView.requestFocus();
                }else{
                    requestFirstTab();
                }
                return;
            }
        }
        requestFirstTab();
    }

}
