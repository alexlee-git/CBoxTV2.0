package tv.newtv.cboxtv.cms.mainPage.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.newtv.cms.bean.Nav;
import com.newtv.libs.Constant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.MainListPageManager;
import tv.newtv.cboxtv.cms.mainPage.NewTVViewPager;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.views.widget.MenuRecycleView;


public class NavFragment extends BaseFragment {

    public MainListPageManager mainListPageManager;
    private String param;
    private String contentId;
    private String defaultFocus;
    private SharedPreferences mSharedPreferences;
    private NewTVViewPager viewPager;
    private MenuRecycleView mMenuNav;
    private RelativeLayout mRootLayout;
    private List<Nav> mMenus;
    private View rootView;

    private String mExternalAction, mExternalParams;

    public static NavFragment newInstance(Bundle paramBundle) {
        NavFragment fragment = new NavFragment();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRootLayout = null;
        if (mMenuNav != null) {
            mMenuNav.setAdapter(null);
            mMenuNav = null;
        }
        if (viewPager != null) {
            viewPager.destroy();
            viewPager = null;
        }
        rootView = null;
        if (mainListPageManager != null) {
            mainListPageManager.unInit();
            mainListPageManager = null;
        }
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
    }


    public void requestMenuFocus() {
        if (mMenuNav.hasFocus()) return;
        if (mMenuNav.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
            mMenuNav.requestDefaultFocus(true);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            param = bundle.getString("nav_text");
            contentId = bundle.getString("content_id");
            mExternalAction = bundle.getString("action");
            mExternalParams = bundle.getString("params");
            if (bundle.containsKey("child")) {
                mMenus = bundle.getParcelableArrayList("child");
            }
        }
        initSharedPreferences();
        defaultFocus = mSharedPreferences.getString("defaultFocus", "");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View getFirstFocusView() {
        return mMenuNav;
    }

    public boolean isNoTopView() {
        if (mainListPageManager != null) {
            return mainListPageManager.isNoTopView();
        } else {
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.e(Constant.TAG, "onDestroyView navText : " + param);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.nav_fragment, null, false);

            mRootLayout = (RelativeLayout) rootView.findViewById(R.id.id_root);
            viewPager = (NewTVViewPager) rootView.findViewById(R.id.id_viewpager);
            mMenuNav = (MenuRecycleView) rootView.findViewById(R.id.sub_list_view);
            ImageView subFocus = rootView.findViewById(R.id.sub_focus);
            mMenuNav.setFocusView(subFocus);
            Map<String, View> mainPageWidgets = new HashMap<>(Constant.BUFFER_SIZE_8);
            mainPageWidgets.put("root", mRootLayout);
            mainPageWidgets.put("viewpager", viewPager);
            mainPageWidgets.put("nav", mMenuNav);
            mainListPageManager = new MainListPageManager();
            mainListPageManager.init(this, getActivity(), getChildFragmentManager(),
                    mainPageWidgets,
                    contentId, mMenus);
            mainListPageManager.setActionIntent(mExternalAction, mExternalParams);
        }

        return rootView;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
    }

    //创建共享参数，存储一些需要的信息
    private void initSharedPreferences() {
        mSharedPreferences = getContext().getSharedPreferences("config", 0);
    }


    @Override
    public boolean onBackPressed() {
        if (mainListPageManager != null) {
            return mainListPageManager.onBackPressed();
        } else {
            return super.onBackPressed();
        }
    }
}
