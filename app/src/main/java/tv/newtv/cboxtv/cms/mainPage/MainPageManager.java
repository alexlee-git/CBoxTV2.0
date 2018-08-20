package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyNoPageDataListener;
import tv.newtv.cboxtv.cms.mainPage.presenter.MainPagePresenter;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyNavItemSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyPageSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;
import tv.newtv.cboxtv.cms.mainPage.presenter.IMainPagePresenter;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;
import tv.newtv.cboxtv.cms.mainPage.view.IMainPageView;

/**
 * Created by lixin on 2018/1/16.
 */

public class MainPageManager implements IMainPageView,
        INotifyPageSelectedListener,
        INotifyNavItemSelectedListener,
        INotifyNoPageDataListener {

    private volatile static MainPageManager mInstance;
    private String mCurNavDataFrom;
    private IMainPagePresenter mPresenter;
    private NewTVViewPager mViewPager;
    private Context mContext;
    private RelativeLayout mRootLayout;
    private RecyclerView mNavBar;
    private List<BaseFragment> mFragments;
    private List<NavInfoResult.NavInfo> mNavInfos;
    private boolean isNoPageData;
    private NavBarAdapter mAdapter;
    private StaggeredAdapter mViewPagerAdapter;

    private MainPageManager() {
    }

    public static MainPageManager getInstance() {
        if (mInstance == null) {
            synchronized (MainPageManager.class) {
                if (mInstance == null) {
                    mInstance = new MainPageManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void inflateNavigationBar(NavInfoResult navInfoResult, String dataFrom) {
        if (navInfoResult == null) {
            Log.e(Constant.TAG, "navigation data invalid");
            // Toast.makeText(mContext, "-------未获取到导航栏数据, 请检查设备是否已联网", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.equals(mCurNavDataFrom, "server")) {
            Log.e(Constant.TAG, "current nav data is from server");
            return;
        }

        if (TextUtils.equals(mCurNavDataFrom, "local")) {
            Log.e(Constant.TAG, "current nav data is from local");
        }

        mCurNavDataFrom = dataFrom;

        // 添加导航栏控件
        if (mNavBar == null) {
            mNavBar = new RecyclerView(mContext);
            mNavBar.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 96);
            layoutParams.topMargin = 148;
            mNavBar.setLayoutParams(layoutParams);
            mNavBar.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int index = parent.getChildAdapterPosition(view);
                    if (index == 0) {
                        outRect.left = 120;
                    } else {
                        outRect.left = 32;
                    }
                }
            });
            mRootLayout.addView(mNavBar);
        }

        List<NavInfoResult.NavInfo> navInfos = (List<NavInfoResult.NavInfo>) navInfoResult.getData();
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

        // 创建导航栏的适配器
        if (mAdapter == null) {
            mAdapter = new NavBarAdapter(mContext, mNavInfos, navInfoResult.getDefaultFocus());
            mAdapter.setNotifyPageSelectedListener(this);
            mNavBar.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        String defaultFocus = navInfoResult.getDefaultFocus();
        int defaultPageIdx = 0;
        try {
            if (defaultFocus != null && !defaultFocus.equals("")) {
                mEditor.putString("defaultFocus", defaultFocus);
            } else if (((List<NavInfoResult.NavInfo>) navInfoResult.getData()).get(defaultPageIdx).getContentID() != null && !((List<NavInfoResult.NavInfo>) navInfoResult.getData()).get(defaultPageIdx).getContentID().equals("")) {
                mEditor.putString("defaultFocus", ((List<NavInfoResult.NavInfo>) navInfoResult.getData()).get(defaultPageIdx).getContentID());
            }
            mEditor.commit();
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.e("--defaultFocus--Exception-----" + e.toString());
        }
        // 创建页面载体---fragment
        if (mFragments == null) {
            mFragments = new ArrayList<>(Constant.BUFFER_SIZE_8);
        }
        mFragments.clear();

        for (NavInfoResult.NavInfo navInfo : (List<NavInfoResult.NavInfo>) navInfoResult.getData()) {
            if (TextUtils.equals(defaultFocus, navInfo.getContentID())) {
                defaultPageIdx = ((List<NavInfoResult.NavInfo>) navInfoResult.getData()).indexOf(navInfo);
                Log.e(Constant.TAG, "锁定目标为 : " + defaultPageIdx);
            }

            Bundle bundle = new Bundle();
            bundle.putString("nav_text", navInfo.getTitle());
            bundle.putString("content_id", navInfo.getContentID());

            BaseFragment fragment = ContentFragment.newInstance(bundle);
            fragment.setNotifyNoPageDataListener(this);
            mFragments.add(fragment);
        }

        // 创建页面区域的适配器
        if (mViewPagerAdapter == null) {
            mViewPagerAdapter = new StaggeredAdapter(((FragmentActivity) mContext).getSupportFragmentManager(), mFragments);
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    Log.e(Constant.TAG, "viewpager onPageSelected pos : " + position);
                    if (mNavInfos != null && position <= mNavInfos.size() - 1) {
                        NavInfoResult.NavInfo info = mNavInfos.get(position);
                        if (info != null) {
                            StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_8);
                            logBuff.append(mNavInfos.get(position).getContentID() + ",")
                                    .append(position);
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT, logBuff.toString());
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } else {
            Constant.isInitStatus = true;
            mViewPagerAdapter.notifyDataSetChanged();
        }

        // 显示默认页
        showDefaultPage(defaultPageIdx);


        // 上报launcher对默认选中的导航页的日志上报
        if (mNavInfos != null && defaultPageIdx <= (mNavInfos.size() - 1)) {
            NavInfoResult.NavInfo info = mNavInfos.get(defaultPageIdx);
            if (info != null) {
                StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_8);
                logBuff.append(info.getContentID() + ",")
                        .append(defaultPageIdx)
                        .trimToSize();
                LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT, logBuff.toString());
            }
        }
    }

    @Override
    public void onFailed(String desc) {
        Toast.makeText(LauncherApplication.AppContext, desc, Toast.LENGTH_SHORT).show();
    }

    public void init(Context context, Map<String, View> widgets) {
        mContext = context;
        mPresenter = new MainPagePresenter(this, mContext);
        mRootLayout = (RelativeLayout) widgets.get("root");
        mViewPager = (NewTVViewPager) widgets.get("viewpager");
        mViewPager.setScrollable(true);
        mViewPager.setCustomScroller(new NewTVScroller(mContext, new LinearInterpolator(), 600));
        mViewPager.setOffscreenPageLimit(3);

        //创建共享参数，存储一些需要的信息
        initSharedPreferences();
        requestNavData();
    }

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    //创建共享参数，存储一些需要的信息
    private void initSharedPreferences() {
        mSharedPreferences = mContext.getSharedPreferences("config", 0);
        mEditor = mSharedPreferences.edit();
    }

    public void requestNavData() {
        if (mPresenter != null) {
            mPresenter.requestNavData();
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
     * 显示初始状态下要显示的页面
     *
     * @param defaultPageIdx 初始状态待显示页面在页面链表中的索引值
     */
    private void showDefaultPage(int defaultPageIdx) {
//        //先强制设定跳转到指定页面
//        try {
//            Field field = mViewPager.getClass().getDeclaredField("mCurItem");// 拿到mCurItem域
//            field.setAccessible(true);
//            field.setInt(mViewPager, defaultPageIdx);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //然后调用下面的函数刷新数据
//        StaggeredAdapter adapter = (StaggeredAdapter) mViewPager.getAdapter();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }

        //再调用setCurrentItem()函数设置一次
        mViewPager.setCurrentItem(defaultPageIdx);
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
        if ((TextUtils.equals(from, "status_bar") && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) ||
                TextUtils.equals(from, "content_fragment") && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            View view = mNavBar.getChildAt(mAdapter.getTheLoveIdx());
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
        mViewPager = null;
        mNavBar = null;
        mViewPagerAdapter = null;
        mAdapter = null;
        mCurNavDataFrom = null;
        if (mFragments != null) {
            mFragments.clear();
            mFragments = null;
        }

        if (mNavInfos != null) {
            mNavInfos.clear();
            mNavInfos = null;
        }
        mInstance = null;
    }
}
