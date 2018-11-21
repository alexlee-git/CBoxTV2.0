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

import com.newtv.cms.bean.Nav;
import com.newtv.cms.contract.NavContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScreenUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.BackGroundManager;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;
import tv.newtv.cboxtv.cms.search.SearchFragment;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.uc.UserCenterFragment;
import tv.newtv.cboxtv.views.widget.MenuRecycleView;

import static android.content.Context.MODE_PRIVATE;

public class MainNavManager implements NavContract.View {

    private static MainNavManager mInstance;
    private MenuRecycleView.MenuAdapter<Nav> menuAdapter = null;
    private List<BaseFragment> mFragments;
    private MenuRecycleView mFirMenu;
    private FragmentManager mFragmentManager;
    private BaseFragment mCurrentShowFragment;

    private SharedPreferences mSharedPreferences;
    private String currentFocus = "";
    private int Navbarfoused = -1;
    private String mExternalAction, mExternalParams;
    private List<Nav> mNavInfos;

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


    private void inflateNavigationBar(final List<Nav> navInfos, final Context context, String
            dataFrom) {

        ScreenUtils.initScreen(context);

        // 添加导航栏控件

        if (navInfos == null || navInfos.size() == 0) {
            return;
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
            mFirMenu.setMenuFactory(new MenuRecycleView.MenuFactory<Nav>() {
                @Override
                public void onItemSelected(int position, Nav value) {
                    /**/
                    if (!TextUtils.isEmpty(mNavInfos.get
                            (position).getId())) {
                        mSharedPreferences.edit().putString("nav-defaultFocus", mNavInfos.get
                                (position)
                                .getId()).apply();
                    }
                    currentFocus = value.getId();
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
                public void onBindView(final RecyclerView.ViewHolder holder, Nav value, int
                        position) {
                    if (TextUtils.isEmpty(value.getFocusIcon())) {
                        ((NavPageMenuViewHolder) holder).setText(value.getTitle());
                    } else {
                        if (BuildConfig.DEBUG) {
                            String url = value.getFocusIcon();
                            if (url.contains("http://172.25.102.19/")) {
                                url = url.replace("http://172.25.102.19/",
                                        "http://111.32.132.156/");
                            }
                            if (url.contains("http://172.25.101.210/")) {
                                url = url.replace("http://172.25.101.210/",
                                        "http://111.32.132.156/");
                            }
                            ((NavPageMenuViewHolder) holder).setImage(url);
                        } else {
                            ((NavPageMenuViewHolder) holder).setImage(value.getFocusIcon());
                        }
                    }
                }

                @Override
                public boolean isDefaultTabItem(Nav value, int position) {
                    return value.getId().equals(currentFocus);
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
                        SharedPreferences sp = context.getSharedPreferences("secondConfig",
                                MODE_PRIVATE);
                        String menu = sp.getString("secondMenu", "");
                        if (TextUtils.isEmpty(menu)) {
                            return;
                        } else {
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT,
                                    menu);
                        }


                    }

                }
            });
            mFirMenu.setAdapter(menuAdapter);

            if (Navbarfoused != -1 && Navbarfoused < mNavInfos.size()) {
                defaultPageIdx = Navbarfoused;
                Nav navInfo = mNavInfos.get(defaultPageIdx);
                if (navInfo != null) {
                    currentFocus = navInfo.getId();
                }
            }
            int count = mNavInfos.size();
            for (int index = 0; index < count; index++) {
                Nav navInfo = mNavInfos.get(index);
                if (!TextUtils.isEmpty(currentFocus)) {
                    if (currentFocus.equals(navInfo.getId())) {
                        defaultPageIdx = index;
                    }
                } else {
                    if ("1".equals(navInfo.isFocus())) {
                        defaultPageIdx = index;
                        currentFocus = navInfo.getId();
                    }
                }
            }

            if (TextUtils.isEmpty(currentFocus) && mNavInfos != null && mNavInfos.size() > 0) {
                currentFocus = mNavInfos.get(0).getId();
            }


            Log.e("--defaultPageIdx-------", Navbarfoused + "----" + defaultPageIdx);

            menuAdapter.setMenuItems(mNavInfos, defaultPageIdx, mNavInfos.size());
        }

        if (mFragments == null) {
            mFragments = new ArrayList<>(Constant.BUFFER_SIZE_8);
        }
        mFragments.clear();

    }

    private void navLogUpload(int position) {
        if (mNavInfos != null && position <= (mNavInfos.size() - 1) && position >= 0) {
            Nav info = mNavInfos.get(position);
            if (info != null) {
                StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_8);
                logBuff.append(info.getId())
                        .append(",")
                        .append(position)
                        .trimToSize();
                LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT,
                        logBuff.toString());

            }
        }
    }

    @SuppressLint("CheckResult")
    public void init(Context context, FragmentManager manager, Map<String, View> widgets) {
        mFragmentManager = manager;

        //创建共享参数，存储一些需要的信息
        initSharedPreferences(context);

        mFirMenu = (MenuRecycleView) widgets.get("firmenu");
        if (mFirMenu != null) {
            mFirMenu.setAutoFocus(true);
            mFirMenu.setFocusable(true);
            if (mFirMenu.getAdapter() == null) {
                menuAdapter = new MenuRecycleView.MenuAdapter<Nav>
                        (context, mFirMenu, R.layout.view_list_item, true);
            } else {
                //noinspection unchecked
                menuAdapter = (MenuRecycleView.MenuAdapter<Nav>) mFirMenu.getAdapter();
            }
        }
        new NavContract.MainNavPresenter(context, this).requestNav();
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
    private void initSharedPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences("config", 0);
    }

    /**
     * 在这里实现切换fragment页面
     */
    private void switchPage(int position) {
//        navLogUpload(position);//选中页面上传
        BaseFragment willShowFragment = null;//

        Nav navInfo = mNavInfos.get(position % mNavInfos.size());
        Bundle bundle = new Bundle();
        bundle.putString("content_id", navInfo.getId());
        bundle.putString("actionType", navInfo.getPageType());
        bundle.putString("action", mExternalAction);
        bundle.putString("params", mExternalParams);
        if (navInfo.getChild() != null && navInfo.getChild().size() > 0) {
            bundle.putParcelableArrayList("child", navInfo.getChild());
        }

//        BGEvent bgEvent = new BGEvent(navInfo.getId(), navInfo.getIsAd() == 1,
//                navInfo.getLogo());
//        BgChangManager.getInstance().dispatchFirstLevelEvent(mContext, bgEvent);

        willShowFragment = (BaseFragment) mFragmentManager.findFragmentByTag(navInfo.getId());
        if (willShowFragment == null) {
            if (Constant.NAV_SEARCH.equals(navInfo.getTitle())) {
                willShowFragment = SearchFragment.newInstance(bundle);
            } else if (Constant.NAV_UC.equals(navInfo.getTitle())) {
                willShowFragment = UserCenterFragment.newInstance(bundle);
            } else if (navInfo.getChild() != null && navInfo.getChild().size() > 0) {
                willShowFragment = NavFragment.newInstance(bundle);
            } else if (Constant.OPEN_PAGE.equals(navInfo.getPageType())
                    || Constant.OPEN_SPECIAL.equals(navInfo.getPageType())) {
                bundle.putString("nav_text", navInfo.getTitle());
                bundle.putBoolean("is_from_nav", true);
                willShowFragment = ContentFragment.newInstance(bundle);
            } else {
                willShowFragment = ContentFragment.newInstance(bundle);
            }
        }

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!willShowFragment.isAdded()) {
            transaction.add(R.id.main_page_content, willShowFragment, navInfo.getId());
        }

        if (mCurrentShowFragment != null) {
            transaction.remove(mCurrentShowFragment);
        }
        transaction.show(willShowFragment).commitAllowingStateLoss();

        willShowFragment.setUseHint(true);

        if (mCurrentShowFragment != null) {
            mCurrentShowFragment.setUserVisibleHint(false);
        }

        NavUtil.getNavUtil().navFragment = willShowFragment;
        willShowFragment.setUserVisibleHint(true);
        BackGroundManager.getInstance().setCurrentNav(navInfo.getId());
        mCurrentShowFragment = (BaseFragment) willShowFragment;
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

    @Override
    public void onNavResult(Context context, List<Nav> result) {
        BackGroundManager.getInstance().parseNavigation(result);
        inflateNavigationBar(result, context, "server");
    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {
        tip(context, desc);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }
}
