package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.BgChangManager;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;
import tv.newtv.cboxtv.cms.listPage.presenter.IListPagePresenter;
import tv.newtv.cboxtv.cms.listPage.presenter.ListPagePresenter;
import tv.newtv.cboxtv.cms.listPage.view.ListPageView;
import tv.newtv.cboxtv.cms.mainPage.menu.NavFragment;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyNavItemSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyNoPageDataListener;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyPageSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.views.MenuRecycleView;
import tv.newtv.cboxtv.views.RecycleImageView;


public class MainListPageManager implements ListPageView,
        INotifyPageSelectedListener,
        INotifyNavItemSelectedListener,
        INotifyNoPageDataListener {

    private String mCurNavDataFrom;
    private FragmentManager fragmentManager;
    private IListPagePresenter mPresenter;
    private NewTVViewPager mViewPager;
    private Context mContext;
    private MenuRecycleView mCircleMenuRv;
    private RecyclerView mNavBar;

    private List<NavListPageInfoResult.NavInfo> mNavInfos;
    private boolean isNoPageData;
    private LooperStaggeredAdapter mViewPagerAdapter;
    private SharedPreferences mSharedPreferences;
    private String currentFocus = "";
    private int defaultPageIdx = 0;
    private BaseFragment currentFragment;
    private String parentId;

    private NavFragment navFragment;
    private int currentPosition = -1;
    private int Navbarfoused = -1;
    private String contentId;

    public void unInit() {
        mViewPager = null;
        mNavBar = null;
        currentFragment = null;
        mViewPagerAdapter = null;
        mCurNavDataFrom = null;
        navFragment = null;
        mSharedPreferences = null;
        mNavBar = null;
        mCircleMenuRv = null;
        mContext = null;
        mPresenter = null;

        if (mNavInfos != null) {
            mNavInfos.clear();
            mNavInfos = null;
        }
    }

    public MainListPageManager() {

    }

    // 外部跳转action、params
    public void setActionIntent(String action, String params) {
        Log.e("--mainlist-------", action + "----" + params);
        if (action != null) {
            if (action.equals("panel")) {
                try {
                    String[] panels = params.split("&");
//                    Log.e("--params----",panels[0]+"----"+panels[1]) ;
                    if (panels.length > 1) {
                        Navbarfoused = Integer.parseInt(panels[1]);
                    }

                } catch (Exception e) {
                    LogUtils.e(e.toString());
                    Navbarfoused = -1;
                }
            }
        } else {
            Navbarfoused = -1;
        }
    }

    public boolean isNoTopView() {
        if (mNavInfos != null && mNavInfos.size() > 0) {
            ContentFragment fragment = (ContentFragment) mViewPagerAdapter.getCurrentFragment();
            if (fragment != null)
                return fragment.isNoTopView();
        }
        return true;
    }

    public boolean isEmpty() {
        return mNavInfos == null || mNavInfos.size() == 0;
    }

    public boolean onBackPressed() {
        BaseFragment currentFragment = mViewPagerAdapter.getCurrentFragment();
        if (currentFragment == null) {
            return true;
        }

        if (!currentFragment.onBackPressed()) {
            return false;
        }

        if (!mCircleMenuRv.hasFocus()) {
            if (mCircleMenuRv.mCurrentCenterChildView != null) {
                mCircleMenuRv.mCurrentCenterChildView.requestFocus();
            }
            return false;
        }
        return true;
    }

    private String getContentUUID(NavListPageInfoResult.NavInfo navInfo) {
        String result = "";
        if (Constant.OPEN_PAGE.equals(navInfo.getActionType())) {
            result = navInfo.getContentID();
        } else if (Constant.OPEN_CHANNEL.equals(navInfo.getActionType())) {
            result = navInfo.getActionURI();
        } else {
            result = navInfo.getContentID();
        }

        if (result == null) {
            result = "";
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inflateListPage(final NavListPageInfoResult mNavListPageInfoResult, final String
            from) {

        if (mNavListPageInfoResult == null) {
            Log.e(Constant.TAG, "navigation data invalid");
            // Toast.makeText(mContext, "-------未获取到导航栏数据, 请检查设备是否已联网", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.equals(mCurNavDataFrom, "server")) {
            Log.e(Constant.TAG, "current nav data is from server");
//            return;
        }

        if (TextUtils.equals(mCurNavDataFrom, "local")) {
            Log.e(Constant.TAG, "current nav data is from local");
        }

        mCurNavDataFrom = "";

        mNavInfos = mNavListPageInfoResult.getData();
        if (mNavInfos == null || mNavInfos.size() == 0) {
            return;
        }

        for(NavListPageInfoResult.NavInfo navInfo : mNavInfos){
            String fragmentUUID = getContentUUID(navInfo);
            BgChangManager.getInstance().add(contentId,fragmentUUID);
        }

        try {
            Collections.sort(mNavInfos, new Comparator<NavListPageInfoResult.NavInfo>() {
                @Override
                public int compare(NavListPageInfoResult.NavInfo navInfo, NavListPageInfoResult
                        .NavInfo t1) {
                    int leftSortNum = Integer.parseInt(navInfo.getSortNum());
                    int rightSortNum = Integer.parseInt(t1.getSortNum());

                    if (leftSortNum >= rightSortNum) {
                        return 0;
                    }
                    return -1;
                }
            });
        } catch (Exception e) {
            LogUtils.e(e);
        }

        if (defaultPageIdx == 0) {
            currentFocus = mSharedPreferences.getString("page-defaultFocus", "");
            int count = mNavInfos.size();
            boolean contain = false;
            for (int index = 0; index < count; index++) {
                NavListPageInfoResult.NavInfo navInfo = mNavInfos.get(index);

                if (currentFocus.equals(getContentUUID(navInfo))) {
                    defaultPageIdx = index;
                    contain = true;
                    break;
                }

                if (mNavListPageInfoResult.getDefaultFocus().equals(navInfo.getContentID())) {
                    defaultPageIdx = index;
                }
            }
            if (!contain) currentFocus = mNavListPageInfoResult.getDefaultFocus();
            if (Navbarfoused != -1 && Navbarfoused < mNavInfos.size()) {
                NavListPageInfoResult.NavInfo navInfo = mNavInfos.get(Navbarfoused);
                currentFocus = navInfo.getContentID();
            }
        }

        PlayerConfig.getInstance().setSecondChannelId(currentFocus);
        MenuRecycleView.MenuAdapter<NavListPageInfoResult.NavInfo> menuAdapter = null;
        mCircleMenuRv.setFocusable(true);
        if (mCircleMenuRv.getAdapter() == null) {
            menuAdapter = new MenuRecycleView.MenuAdapter<>
                    (mContext, mCircleMenuRv, R.layout.view_sec_list_item, false);

            mCircleMenuRv.setMenuFactory(new MenuRecycleView.MenuFactory<NavListPageInfoResult
                    .NavInfo>() {

                @Override
                public void onItemSelected(int position, NavListPageInfoResult.NavInfo
                        value) {
                    if (mNavInfos.size() == 0) return;
                    if (mViewPager.getCurrentItem() % mNavInfos.size() == position % mNavInfos.size()) {
                        return;
                    }
                    mViewPagerAdapter.setShowItem(position);
                    mViewPager.setCurrentItem(position);
                    currentFocus = value.getContentID();
                    /**/
                    int select = position % mNavListPageInfoResult.getData().size();
                    NavListPageInfoResult.NavInfo navInfo = ((List<NavListPageInfoResult
                            .NavInfo>) mNavListPageInfoResult.getData()).get(select);
                    String uuid = getContentUUID(navInfo);
                    if (!TextUtils.isEmpty(uuid)) {
                        mSharedPreferences.edit().putString("page-defaultFocus", uuid).apply();
                    }
                    PlayerConfig.getInstance().setSecondChannelId(uuid);
                    BgChangManager.getInstance().setCurrent(mContext,uuid);
                }

                @Override
                public ListPageMenuViewHolder createViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .view_sec_list_item, parent, false);
                    return new ListPageMenuViewHolder(view);
                }

                @Override
                public void onBindView(RecyclerView.ViewHolder
                                               holder, NavListPageInfoResult.NavInfo
                                               value, int position) {
                    if (!TextUtils.isEmpty(value.getIcon1())) {
                        ((ListPageMenuViewHolder) holder).setIcon(value.getIcon1());
                    } else {
                        ((ListPageMenuViewHolder) holder).setText(value.getTitle());
                    }
                }

                @Override
                public boolean isDefaultTabItem(NavListPageInfoResult.NavInfo value,
                                                int position) {
                    return getContentUUID(value).equals(currentFocus);
                }

                @Override
                public boolean autoRequestFocus() {
                    return false;
                }

                @Override
                public View getNextFocusView() {
                    int position = mViewPager.getCurrentItem() % mNavInfos.size();
                    BaseFragment target = (BaseFragment) mViewPagerAdapter.getItem(position);
                    if (target == null) return null;
                    return target.getFirstFocusView();
                }

                @Override
                public void focusChanged(boolean hasFocus, int position) {
                    if (hasFocus) {

                        secondNavLogUpload(position);
                        Log.e("logsdk", "二级 position=" + position);
                    }
                }
            });

            mCircleMenuRv.setAdapter(menuAdapter);
            if (Navbarfoused != -1) {
                defaultPageIdx = Navbarfoused;
            }
            menuAdapter.setMenuItems(mNavInfos, defaultPageIdx);
        }

        // 创建页面区域的适配器
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new LooperStaggeredAdapter(fragmentManager, mNavListPageInfoResult.getData(), parentId);
            //先强制设定跳转到指定页面
            int MiddleValue = LooperUtil.getMiddleValue(mNavListPageInfoResult.getData().size());
            int defaultIndex = MiddleValue + defaultPageIdx;
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPagerAdapter.setShowItem(defaultIndex);
            mViewPager.setCurrentItem(defaultIndex, false);

            currentPosition = defaultIndex % mNavListPageInfoResult.getData().size();
            currentFragment = (BaseFragment) mViewPagerAdapter.getItem(defaultIndex);
            setContentFragmentRecyclerViewToNavFragment();

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                private String currentTag;

                @Override
                public void onPageScrolled(int position, float positionOffset, int
                        positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position % mNavListPageInfoResult.getData().size();
                    // currentFragment = (ContentFragment) mViewPagerAdapter.getItem(position);

                    if(mNavListPageInfoResult.getData().size() > currentPosition){
                        currentTag = mNavListPageInfoResult.getData().get(currentPosition)
                                .getContentID();
                    }
                    Log.e(Constant.TAG, "viewpager onPageSelected pos : " + currentPosition);

                    setContentFragmentRecyclerViewToNavFragment();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        currentFragment = mViewPagerAdapter.getCurrentFragment();
                        //currentFragment = (BaseFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
                        if (currentFragment != null) {
                            currentFragment.onEnterComplete();
                        }
                    }
                }
            });
        } else {
            Constant.isInitStatus = true;
            mViewPagerAdapter.notifyDataSetChanged();
        }

        if ("server".equals(from)) {
            //currentFragment = (BaseFragment) mViewPagerAdapter.getItem(mViewPager.getCurrentItem());
            currentFragment = mViewPagerAdapter.getCurrentFragment();
            setContentFragmentRecyclerViewToNavFragment();
        }
    }


    /**
     * 二级导航日志
     *
     * @param position
     */
    private void secondNavLogUpload(int position) {
        if (mNavInfos != null && position <= (mNavInfos.size() - 1)) {
            NavListPageInfoResult.NavInfo info = mNavInfos.get(position);
            String result = getContentUUID(info);
            if (info != null) {
                StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_8);
                logBuff.append(result + ",")
                        .append(position)
                        .trimToSize();
                LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT,
                        logBuff.toString());

            }
            PlayerConfig.getInstance().setSecondChannelId(result);
        }
    }

    /**
     * NavFragment中的Viewpager中的每一个item是一个ContentFragment
     * MainActivity中将dispatchKeyEvent分发到外层的fragment中，内部包含的ContengFragment无法分发到
     * 因此将contentFragment中的recyclerView设置到NavFragment中，在那里做滚动到底部检测
     */
    private void setContentFragmentRecyclerViewToNavFragment() {
        BaseFragment currentFragment = mViewPagerAdapter.getCurrentFragment();
        if (currentFragment instanceof ContentFragment && navFragment != null) {
            ContentFragment fragment = (ContentFragment) currentFragment;
            navFragment.setAnimRecyclerView(fragment.getRecyclerView());
        }
    }

    @Override
    public void onFailed(String desc) {
        Toast.makeText(mContext, "数据解析错误", Toast.LENGTH_SHORT).show();
    }

    public void init(NavFragment navFragment, Context context, FragmentManager manager,
                     Map<String, View> widgets, String
                             id) {
        this.navFragment = navFragment;
        parentId = id;
        mContext = context;
        fragmentManager = manager;
        mPresenter = new ListPagePresenter(this, LauncherApplication.AppContext);
        mViewPager = (NewTVViewPager) widgets.get("viewpager");
        mCircleMenuRv = (MenuRecycleView) widgets.get("nav");
        mCircleMenuRv.setNeedTransPosition(false);
        mViewPager.setScrollable(true);
        mViewPager.setCustomScroller(new NewTVScroller(mContext, new LinearInterpolator()
                , 500));
        mViewPager.setOffscreenPageLimit(1);


        //创建共享参数，存储一些需要的信息
        initSharedPreferences();
    }

    //创建共享参数，存储一些需要的信息
    private void initSharedPreferences() {
        mSharedPreferences = mContext.getSharedPreferences("config", 0);
    }

    public void requestNavData(String id) {
        contentId = id;
        if (mPresenter != null) {
            mPresenter.requestListPageNav(id);
        }
    }

    @Override
    public void notifyPageSelected(int pos) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(pos);
        }
    }

    @Override
    public void notifyNavItemSelected(int position) {
        if (mNavBar != null) {
            View child = mNavBar.getChildAt(position);
            if (child != null) {
                child.requestFocus();
            }
        }
    }

    /**
     * @param keyEvent
     * @param from
     */
    public boolean processKeyEvent(KeyEvent keyEvent, String from) {
        if (TextUtils.isEmpty(from)) {
            return false;
        }

        // 1.如果是通过状态栏按下键传递过来的keyEvent, 则让上次选中的导航item重新处于选中状态
        // 2.如果是通过推荐位区按上键传递过来的keyEvent, 则让上次选中的导航item重新处于选中状态
        if ((TextUtils.equals(from, "status_bar") && keyEvent.getKeyCode() == KeyEvent
                .KEYCODE_DPAD_DOWN) ||
                TextUtils.equals(from, "content_fragment") && keyEvent.getKeyCode() == KeyEvent
                        .KEYCODE_DPAD_UP) {
            if (!mCircleMenuRv.hasFocus()) {
                mCircleMenuRv.requestDefaultFocus(true);
                return true;
            }
        }

        return false;
    }

    @Override
    public void notifyNoPageData(boolean flag) {
        isNoPageData = flag;
    }

    public boolean isNoPageData() {
        return isNoPageData;
    }

    public boolean isDataFromServer() {
        Log.e(Constant.TAG, "当前导航数据来自 : " + mCurNavDataFrom);
        return TextUtils.equals(mCurNavDataFrom, "server");
    }

    static class ListPageMenuViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        RecycleImageView icon;

        ListPageMenuViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.title_text);
            icon = itemView.findViewById(R.id.title_icon_list);
        }

        public void setText(String value) {
            text.setVisibility(View.VISIBLE);
            icon.setVisibility(View.GONE);
            text.setText(value);
        }

        public void setIcon(String url) {
            text.setVisibility(View.GONE);
            icon.setVisibility(View.VISIBLE);

            if (icon.getTag() == null || !icon.getTag().toString().equals(url)) {
                icon.useResize(false).NoStore(false).hasCorner(false).load(url);
//                Picasso.with(LauncherApplication.AppContext)
//                        .load(url)
//                        .into(icon);
            }
        }
    }

//    public void setTwoMenuData(){
//        if(mNavInfos!=null)
//        mCircleMenuRv.setTwoMenuData(mNavInfos);
//    }

}
