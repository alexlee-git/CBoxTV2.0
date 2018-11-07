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

import com.newtv.cms.bean.Nav;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;

import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.BgChangManager;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.NavFragment;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.views.widget.MenuRecycleView;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

import static android.content.Context.MODE_PRIVATE;


public class MainListPageManager{

    private String mCurNavDataFrom;
    private FragmentManager fragmentManager;
    private NewTVViewPager mViewPager;
    private Context mContext;
    private MenuRecycleView mCircleMenuRv;
    private RecyclerView mNavBar;

    private List<Nav> mNavInfos;
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

    public MainListPageManager() {

    }

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

    @SuppressWarnings("unchecked")
    public void inflateListPage(final List<Nav> navList, final String
            from) {

        if (navList == null) {
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

        mNavInfos = navList;
        if (mNavInfos == null || mNavInfos.size() == 0) {
            return;
        }

        for (Nav navInfo : mNavInfos) {
            String fragmentUUID = navInfo.getId();
            BgChangManager.getInstance().add(contentId, fragmentUUID);
        }

        if (defaultPageIdx == 0 && mSharedPreferences != null) {
            currentFocus = mSharedPreferences.getString("page-defaultFocus", "");
            int count = mNavInfos.size();
            boolean contain = false;
            for (int index = 0; index < count; index++) {
                Nav navInfo = mNavInfos.get(index);

                if (currentFocus.equals(navInfo.getId())) {
                    defaultPageIdx = index;
                    contain = true;
                    break;
                }

//                if (mNavListPageInfoResult.getDefaultFocus().equals(navInfo.getContentID())) {
//                    defaultPageIdx = index;
//                }
            }
//            if (!contain) currentFocus = mNavListPageInfoResult.getDefaultFocus();
            if (Navbarfoused != -1 && Navbarfoused < mNavInfos.size()) {
                Nav navInfo = mNavInfos.get(Navbarfoused);
                currentFocus = navInfo.getId();
            }
        }

        PlayerConfig.getInstance().setSecondChannelId(currentFocus);
        MenuRecycleView.MenuAdapter<Nav> menuAdapter = null;
        mCircleMenuRv.setFocusable(true);
        if (mCircleMenuRv.getAdapter() == null) {
            menuAdapter = new MenuRecycleView.MenuAdapter<>
                    (mContext, mCircleMenuRv, R.layout.view_sec_list_item, false);

            mCircleMenuRv.setMenuFactory(new MenuRecycleView.MenuFactory<Nav>() {

                @Override
                public void onItemSelected(int position, Nav value) {

                    if (mNavInfos.size() == 0 || mViewPager == null) return;

                    /**/
                    int select = position % mNavInfos.size();
                    Nav navInfo = mNavInfos.get(select);
                    String uuid = navInfo.getId();
                    PlayerConfig.getInstance().setSecondChannelId(uuid);
                    BgChangManager.getInstance().setCurrent(mContext, uuid);

                    if (mViewPager.getCurrentItem() % mNavInfos.size() ==
                            position % mNavInfos.size()) {
                        return;
                    }
                    mViewPagerAdapter.setShowItem(position);
                    mViewPager.setCurrentItem(position);
                    currentFocus = value.getId();

                    if (!TextUtils.isEmpty(uuid)) {
                        mSharedPreferences.edit().putString("page-defaultFocus", uuid).apply();
                    }
                }

                @Override
                public ListPageMenuViewHolder createViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .view_sec_list_item, parent, false);
                    return new ListPageMenuViewHolder(view);
                }

                @Override
                public void onBindView(RecyclerView.ViewHolder
                                               holder, Nav
                                               value, int position) {
                    if (!TextUtils.isEmpty(value.getFocusIcon())) {
                        ((ListPageMenuViewHolder) holder).setIcon(value.getFocusIcon());
                    } else {
                        ((ListPageMenuViewHolder) holder).setText(value.getTitle());
                    }
                }

                @Override
                public boolean isDefaultTabItem(Nav value,
                                                int position) {
                    return value.getId().equals(currentFocus);
                }

                @Override
                public boolean autoRequestFocus() {
                    return false;
                }

                @Override
                public View getNextFocusView() {
                    BaseFragment target = (BaseFragment) mViewPagerAdapter.getCurrentFragment();
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
            mViewPagerAdapter = new LooperStaggeredAdapter(fragmentManager, mNavInfos, parentId);
            //先强制设定跳转到指定页面
            int MiddleValue = LooperUtil.getMiddleValue(mNavInfos.size());
            int defaultIndex = MiddleValue + defaultPageIdx;
            mViewPager.setAdapter(mViewPagerAdapter);
            mViewPagerAdapter.setShowItem(defaultIndex);
            mViewPager.setCurrentItem(defaultIndex, false);

            currentPosition = defaultIndex % mNavInfos.size();
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
                    currentPosition = position % mNavInfos.size();
                    // currentFragment = (ContentFragment) mViewPagerAdapter.getItem(position);

                    if (mNavInfos.size() > currentPosition) {
                        currentTag = mNavInfos.get(currentPosition)
                                .getId();
                    }
                    Log.e(Constant.TAG, "viewpager onPageSelected pos : " + currentPosition);

                    setContentFragmentRecyclerViewToNavFragment();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        currentFragment = mViewPagerAdapter.getCurrentFragment();
                        //currentFragment = (BaseFragment) mViewPagerAdapter.getItem(mViewPager
                        // .getCurrentItem());
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
            //currentFragment = (BaseFragment) mViewPagerAdapter.getItem(mViewPager
            // .getCurrentItem());
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
            Nav info = mNavInfos.get(position);
            if (info != null) {
                String result = info.getId();
                StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_8);
                logBuff.append(result)
                        .append(",")
                        .append(position)
                        .trimToSize();
                SharedPreferences sp = mContext.getSharedPreferences("secondConfig", MODE_PRIVATE);
                sp.edit().putString("secondMenu",logBuff.toString()).commit();
                LogUploadUtils.uploadLog(Constant.LOG_NODE_NAVIGATION_SELECT,
                        logBuff.toString());
                PlayerConfig.getInstance().setSecondChannelId(result);
            }
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

    public void init(NavFragment navFragment, Context context, FragmentManager manager,
                     Map<String, View> widgets, String id, List<Nav> navData) {
        mNavInfos = navData;
        this.navFragment = navFragment;
        parentId = id;
        mContext = context;
        fragmentManager = manager;
        mViewPager = (NewTVViewPager) widgets.get("viewpager");
        mCircleMenuRv = (MenuRecycleView) widgets.get("nav");
        mCircleMenuRv.setNeedTransPosition(false);
        mViewPager.setScrollable(true);
        mViewPager.setCustomScroller(new NewTVScroller(mContext, new LinearInterpolator()
                , 500));
        mViewPager.setOffscreenPageLimit(1);


        //创建共享参数，存储一些需要的信息
        initSharedPreferences();

        inflateListPage(mNavInfos,"server");
    }

    //创建共享参数，存储一些需要的信息
    private void initSharedPreferences() {
        mSharedPreferences = mContext.getSharedPreferences("config", 0);
    }

    public boolean dispatchKeyEvent(KeyEvent event){
        ContentFragment fragment = (ContentFragment) mViewPagerAdapter.getCurrentFragment();
        if(fragment == null || fragment.dispatchKeyEvent(event)){
            return true;
        }
        return false;
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
