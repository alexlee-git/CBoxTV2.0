package tv.newtv.cboxtv.cms.mainPage.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.BgChangManager;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.NewTVViewPager;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyNavItemSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyNoPageDataListener;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyPageSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;
import tv.newtv.cboxtv.cms.mainPage.presenter.IMainPagePresenter;
import tv.newtv.cboxtv.cms.mainPage.presenter.MainPagePresenter;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;
import tv.newtv.cboxtv.cms.mainPage.view.IMainPageView;
import tv.newtv.cboxtv.cms.search.SearchFragment;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.uc.UserCenterFragment;
import tv.newtv.cboxtv.utils.ScreenUtils;
import tv.newtv.cboxtv.views.MenuRecycleView;
import tv.newtv.cboxtv.views.RecycleImageView;


public class MainNavManager implements IMainPageView,
        INotifyPageSelectedListener,
        INotifyNavItemSelectedListener,
        INotifyNoPageDataListener {

    private static MainNavManager mInstance;
    private final int DISPLAY_SIZE = 5;
    MenuRecycleView.MenuAdapter menuAdapter = null;
    private String mCurNavDataFrom;
    private IMainPagePresenter mPresenter;
    private NewTVViewPager mViewPager;
    private Context mContext;
    private RelativeLayout mRootLayout;
    //private RecyclerView mNavBar;
    private List<BaseFragment> mFragments;
    private List<NavInfoResult.NavInfo> mNavInfos;
    private boolean isNoPageData;
    private MenuRecycleView mFirMenu;
    private FragmentManager mFragmentManager;
    private BaseFragment mCurrentShowFragment;

    private Map<String, View> widgets;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private String currentFocus = "";
    private int Navbarfoused = -1;
    private String mExternalAction, mExternalParams;

    private MainNavManager() {
    }

    public static MainNavManager getInstance() {
        if (mInstance == null) {
            synchronized (MainNavManager.class) {
                if (mInstance == null) {
                    mInstance = new MainNavManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inflateNavigationBar(final NavInfoResult navInfoResult, String dataFrom) {
        if (navInfoResult == null) {
            Log.e(Constant.TAG, "navigation data invalid");
            // Toast.makeText(mContext, "-------未获取到导航栏数据, 请检查设备是否已联网", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.equals(mCurNavDataFrom, "server")) {
            Log.e(Constant.TAG, "current nav data is from server");
            //return;
        }

        if (TextUtils.equals(mCurNavDataFrom, "local")) {
            Log.e(Constant.TAG, "current nav data is from local");
        }

        mCurNavDataFrom = dataFrom;
        ScreenUtils.initScreen(mContext);

        // 添加导航栏控件

        List<NavInfoResult.NavInfo> navInfos = (List<NavInfoResult.NavInfo>) navInfoResult
                .getData();
        if (navInfos == null || navInfos.size() == 0) {
            return;
        }

        try {
            Collections.sort(navInfos, new Comparator<NavInfoResult.NavInfo>() {
                @Override
                public int compare(NavInfoResult.NavInfo navInfo, NavInfoResult.NavInfo t1) {
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

        if (mNavInfos == null) {
            mNavInfos = navInfos;
        } else {
            mNavInfos.clear();
            mNavInfos.addAll(navInfos);
        }

        currentFocus = mSharedPreferences.getString("nav-defaultFocus", "");
        if (Navbarfoused != -1) {
            currentFocus = String.valueOf(Navbarfoused);
        }
        int defaultPageIdx = 0;

        if (menuAdapter != null) {
            mFirMenu.setMenuFactory(new MenuRecycleView.MenuFactory<NavInfoResult.NavInfo>() {
                @Override
                public void onItemSelected(int position, NavInfoResult.NavInfo value) {
                    /**/
                    if (!TextUtils.isEmpty(((List<NavInfoResult
                            .NavInfo>) navInfoResult.getData()).get
                            (position).getContentID())) {
                        mEditor.putString("nav-defaultFocus", ((List<NavInfoResult.NavInfo>)
                                navInfoResult.getData()).get(position)
                                .getContentID());
                        mEditor.commit();
                    }
                    currentFocus = value.getContentID();
                    switchPage(position);
                    PlayerConfig.getInstance().cleanChannelId();
                    PlayerConfig.getInstance().setFirstChannelId(currentFocus);
                }

                @Override
                public NavPageMenuViewHolder createViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .view_list_item, parent, false);
                    return new NavPageMenuViewHolder(view);
                }

                @Override
                public void onBindView(final RecyclerView.ViewHolder holder, NavInfoResult
                        .NavInfo value, int position) {
                    if (TextUtils.isEmpty(value.getIcon1())) {
                        ((NavPageMenuViewHolder) holder).setText(value.getTitle());
                    } else {
                        ((NavPageMenuViewHolder) holder).setImage(value.getIcon1());
                    }
                }

                @Override
                public boolean isDefaultTabItem(NavInfoResult.NavInfo value, int position) {
                    return value.getContentID().equals(currentFocus);
                }

                @Override
                public boolean autoRequestFocus() {
                    return true;
                }

                @Override
                public View getNextFocusView() {
                    if (mCurrentShowFragment == null) return null;
                    return mCurrentShowFragment.getFirstFocusView();
                }

                @Override
                public void focusChanged(boolean hasFocus, int position) {

                    if (hasFocus) {
                        navLogUpload(position);

                    }

                }
            });
            mFirMenu.setAdapter(menuAdapter);

            int count = mNavInfos.size();
            boolean contain = false;
            for (int index = 0; index < count; index++) {
                NavInfoResult.NavInfo navInfo = mNavInfos.get(index);
                if (currentFocus.equals(navInfo.getContentID())) {
                    defaultPageIdx = index;
                    contain = true;
                    break;
                }
                if (navInfoResult.getDefaultFocus().equals(navInfo.getContentID())) {
                    defaultPageIdx = index;
                }
            }

            if (!contain) {
                currentFocus = navInfoResult.getDefaultFocus();
            }
            if (Navbarfoused != -1 && Navbarfoused < mNavInfos.size()) {
                defaultPageIdx = Navbarfoused;
                NavInfoResult.NavInfo navInfo = mNavInfos.get(defaultPageIdx);
                if (navInfo != null) {
                    currentFocus = navInfo.getContentID();
                }
//                switchPage(defaultPageIdx);
            }
            Log.e("--defaultPageIdx-------", Navbarfoused + "----" + defaultPageIdx);

            menuAdapter.setMenuItems(mNavInfos, defaultPageIdx, mNavInfos.size());
//                    mNavInfos.size() >= DISPLAY_SIZE? DISPLAY_SIZE: mNavInfos.size());
        }
//        mFirMenu.setOneMenuData(mNavInfos);

        if (mFragments == null) {
            mFragments = new ArrayList<>(Constant.BUFFER_SIZE_8);
        }
        mFragments.clear();

    }

    private void navLogUpload(int position) {
        if (mNavInfos != null && position <= (mNavInfos.size() - 1) && position >= 0) {
            NavInfoResult.NavInfo info = mNavInfos.get(position);
            if (info != null) {
                StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_8);
                logBuff.append(info.getContentID() + ",")
                        .append(position)
                        .trimToSize();
                LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT,
                        logBuff.toString());

            }
        }
    }

    @Override
    public void onFailed(String desc) {
        Toast.makeText(LauncherApplication.AppContext, desc, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("CheckResult")
    public void init(Context context, FragmentManager manager, Map<String, View> widgets) {
        mContext = context;
        mPresenter = new MainPagePresenter(this, mContext);
        mRootLayout = (RelativeLayout) widgets.get("root");

        mFragmentManager = manager;

        this.widgets = widgets;

        //创建共享参数，存储一些需要的信息
        initSharedPreferences();

        mFirMenu = (MenuRecycleView) widgets.get("firmenu");

        mFirMenu.setAutoFocus(true);
        mFirMenu.setFocusable(true);
        if (mFirMenu.getAdapter() == null) {
            menuAdapter = new MenuRecycleView.MenuAdapter
                    (mContext, mFirMenu, R.layout.view_list_item, true);
        } else {
            menuAdapter = (MenuRecycleView.MenuAdapter) mFirMenu.getAdapter();
        }

        requestNavData();
    }


    // 外部跳转action、params
    public void setActionIntent(String action, String params) {
        mExternalAction = action;
        mExternalParams = params;
        if (action != null) {
            if (action.equals("panel")) {
                try {
                    String[] panels = params.split("&");
                    Log.e("--params----", panels[0] + "----");
                    if (panels.length > 0) {
                        Navbarfoused = Integer.parseInt(panels[0]);
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

    //创建共享参数，存储一些需要的信息
    private void initSharedPreferences() {
        mSharedPreferences = mContext.getSharedPreferences("config", 0);
        mEditor = mSharedPreferences.edit();
    }

    /**
     * 在这里实现切换fragment页面
     */
    private void switchPage(int position) {
//        navLogUpload(position);//选中页面上传
        BaseFragment willShowFragment = null;//

        NavInfoResult.NavInfo navInfo = mNavInfos.get(position % mNavInfos.size());
        Bundle bundle = new Bundle();
        bundle.putString("content_id", navInfo.getContentID());
        bundle.putString("actionType", navInfo.getActionType());
        bundle.putString("action", mExternalAction);
        bundle.putString("params", mExternalParams);

        BGEvent bgEvent = new BGEvent(navInfo.getContentID(), navInfo.getIsAd() == 1,
                navInfo.getBackground());
        BgChangManager.getInstance().dispatchFirstLevelEvent(mContext, bgEvent);

        willShowFragment = (BaseFragment) mFragmentManager.findFragmentByTag(navInfo.getContentID
                ());
        if (willShowFragment == null) {
            if (Constant.NAV_SEARCH.equals(navInfo.getTitle())) {
                willShowFragment = SearchFragment.newInstance(bundle);
            } else if (Constant.NAV_UC.equals(navInfo.getTitle())) {
                willShowFragment = UserCenterFragment.newInstance(bundle);
            } else if (Constant.OPEN_LISTPAGE.equals(navInfo.getActionType())) {
                willShowFragment = NavFragment.newInstance(bundle);
            } else if (Constant.OPEN_PAGE.equals(navInfo.getActionType())
                    || Constant.OPEN_SPECIAL.equals(navInfo.getActionType())) {
                bundle.putString("nav_text", navInfo.getTitle());
                bundle.putBoolean("is_from_nav", true);
                willShowFragment = ContentFragment.newInstance(bundle);
                ((BaseFragment) willShowFragment).setUseHint(false);

            } else {
                willShowFragment = ContentFragment.newInstance(bundle);
            }
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!willShowFragment.isAdded()) {
            transaction.add(R.id.main_page_content, willShowFragment, navInfo.getContentID());
        }

        if (mCurrentShowFragment != null) {
            transaction.detach(mCurrentShowFragment).attach(willShowFragment)
                    .commitAllowingStateLoss();
        } else {
            transaction.show(willShowFragment).commitAllowingStateLoss();
        }

        willShowFragment.setUseHint(true);

        if (mCurrentShowFragment != null) {
            mCurrentShowFragment.setUserVisibleHint(false);
        }

        NavUtil.getNavUtil().navFragment = willShowFragment;
        willShowFragment.setUserVisibleHint(true);
        mCurrentShowFragment = (BaseFragment) willShowFragment;
    }

    public void requestNavData() {
        if (mPresenter != null) {
            mPresenter.requestNavData();
        }
    }

    @Override
    public void notifyPageSelected(int pos) {
//        if (mViewPager != null) {
//            mViewPager.setCurrentItem(pos);
//        }
    }

    @Override
    public void notifyNavItemSelected(int position) {
//        if (mNavBar != null) {
//            View child = mNavBar.getChildAt(position);
//            if (child != null) {
//                child.requestFocus();
//            }
//        }
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
                TextUtils.equals(from, "content_fragment") && keyEvent.getKeyCode() ==
                        KeyEvent.KEYCODE_DPAD_UP) {
            View view = mFirMenu.mCurrentCenterChildView;
            if (view != null) {
                view.requestFocus();
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

    public void unInit() {
        if (mFragments != null) {
            mFragments.clear();
            mFragments = null;
        }
        mInstance = null;
    }

    public Fragment getCurrentFragment() {
        return mCurrentShowFragment;
    }

    static class NavPageMenuViewHolder extends MenuRecycleView.MenuViewHolder {

        private static final int MODE_TEXT = 1;
        private static final int MODE_IMAGE = 2;
        TextView title;
        RecycleImageView img;
        private int currentMode;

        NavPageMenuViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_text);
            img = itemView.findViewById(R.id.title_icon_nav);
        }

        @Override
        protected void setItemVisible(boolean show) {
            if (!show) {
                title.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
            } else {
                switch (currentMode) {
                    case MODE_TEXT:
                        title.setVisibility(View.VISIBLE);
                        img.setVisibility(View.GONE);
                        break;
                    case MODE_IMAGE:
                        title.setVisibility(View.GONE);
                        img.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        }

        public void setText(String value) {
            currentMode = MODE_TEXT;
            title.setVisibility(isHidden ? View.GONE : View.VISIBLE);
            img.setVisibility(View.GONE);
            title.setText(value);
        }

        public void setImage(String url) {
            currentMode = MODE_IMAGE;
            title.setVisibility(View.GONE);
            img.setVisibility(isHidden ? View.GONE : View.VISIBLE);
            img.useResize(false).NoStore(false).hasCorner(false).load(url);
        }
    }

}
