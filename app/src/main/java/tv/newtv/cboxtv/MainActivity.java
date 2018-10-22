package tv.newtv.cboxtv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Time;
import com.newtv.cms.bean.UpVersion;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.bean.TimeBean;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ServiceTimeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.annotation.PopupAD;
import tv.newtv.cboxtv.cms.mainPage.menu.MainNavManager;
import tv.newtv.cboxtv.cms.mainPage.menu.NavFragment;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.ModuleLayoutManager;

import com.newtv.cms.contract.AppMainContract;
import com.newtv.cms.contract.VersionUpdateContract;

import tv.newtv.cboxtv.views.UpdateDialog;
import tv.newtv.cboxtv.views.widget.MenuRecycleView;

@PopupAD
public class MainActivity extends BaseActivity implements BgChangManager.BGCallback, AppMainContract
        .View, VersionUpdateContract.View {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.id_root)
    RelativeLayout mRootLayout;

    @BindView(R.id.list_view)
    MenuRecycleView mFirMenuRv;

    @BindView(R.id.first_focus)
    View mFirFocus;

    @BindView(R.id.timer)
    TextView timeTV;

    private AppMainContract.Presenter mPresenter;
    private VersionUpdateContract.Presenter mUpdatePresenter;

    private String mExternalAction;
    private String mExternalParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey
                ("android:support:fragments")) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //注册广播，显示系统时间

        Intent mIntent = getIntent();
        if (mIntent != null) {
            mExternalAction = mIntent.getStringExtra("action");
            mExternalParams = mIntent.getStringExtra("params");
            Log.e("---External", mExternalAction + "--" + mExternalParams);
        }
        initModules();

        if (mExternalAction != null) {
            if (mExternalAction.equals("news")) {
                if (mExternalParams != null) {
                    String params[] = mExternalParams.split("&");
                    if (params.length > 1) {
                        JumpUtil.detailsJumpActivity(MainActivity.this, params[0], params[1]);
                    }
                }
            } else if (Constant.EXTERNAL_OPEN_URI.equals(mExternalAction)) {
                if (mExternalParams != null) {
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toSecondPageFromAd(mExternalParams);
                        }
                    }, 1500);
                }
            }
        }

        showServiceTime();

        mPresenter = new AppMainContract.MainPresenter(getApplicationContext(), this);

        mUpdatePresenter = new VersionUpdateContract.UpdatePresenter(getApplicationContext(), this);
        mUpdatePresenter.checkVersionUpdate(getApplicationContext());
    }

    private void versionUpDialog(final UpVersion versionBeen, boolean isForceUp) {
        new UpdateDialog().show(this, versionBeen, isForceUp);
    }

    @Override
    protected void onResume() {
        // 上报进入首页日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_HOME_PAGE, "0");
        BgChangManager.getInstance().registerTargetView(this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.e(Constant.TAG, "MainActivity onDestory");
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }

        if (mUpdatePresenter != null) {
            mUpdatePresenter.destroy();
            mUpdatePresenter = null;
        }

        Constant.isInitStatus = true;

        ModuleLayoutManager.getInstance().unit();
        SuperScriptManager.getInstance().unit();
        MainNavManager.getInstance().unInit();

    }

    /**
     * 1.初始化状态栏模块
     * 2.初始化内容区模块
     */
    private void initModules() {

        Map<String, View> mainPageWidgets = new HashMap<>(Constant.BUFFER_SIZE_8);

        mainPageWidgets.put("root", mRootLayout);
        mFirMenuRv.setFocusView(mFirFocus);
        mainPageWidgets.put("firmenu", mFirMenuRv);
        MainNavManager.getInstance().setActionIntent(mExternalAction, mExternalParams);
        MainNavManager.getInstance().init(this, getSupportFragmentManager(), mainPageWidgets);

        SuperScriptManager.getInstance().init(getApplicationContext());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.i("onNewIntent");

        if (intent != null) {
            mExternalAction = intent.getStringExtra("action");
            mExternalParams = intent.getStringExtra("params");
            Log.e("---External", mExternalAction + "--" + mExternalParams);
        }

        // TODO 跳转到相应的页面
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        LogUtils.e("MainNavManager", MainNavManager.getInstance().toString());
        BaseFragment currentFragment = (BaseFragment) MainNavManager.getInstance()
                .getCurrentFragment();
        if (currentFragment != null) {
            LogUtils.e("MainNavManager", currentFragment.toString());
            currentFragment.dispatchKeyEvent(event);
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mFirMenuRv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                return true;
            }
            Log.e("mFirMenuRv2", "mFirMenuRv.getScrollState()" + mFirMenuRv.getScrollState());
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (!mFirMenuRv.hasFocus()) {
                    if (currentFragment != null && !currentFragment.onBackPressed()) {
                        return true;
                    }
                    if (mFirMenuRv.mCurrentCenterChildView != null) {
                        mFirMenuRv.mCurrentCenterChildView.requestFocus();
                    }
                    return true;
                }
                startActivityForResult(new Intent().setClass(this, WarningExitActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        , 0);
                return true;
            }

            if (mFirMenuRv.hasFocus() && currentFragment instanceof
                    NavFragment && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {

                if (((NavFragment) currentFragment).mainListPageManager != null) {
                    boolean result = ((NavFragment) currentFragment).mainListPageManager
                            .processKeyEvent(event, "status_bar");
                    if (result) {
                        return true;
                    }
                }
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (currentFragment instanceof NavFragment) {
                    if (((NavFragment) currentFragment).isNoTopView() && currentFragment.getView
                            () != null) {
                        View focusView = currentFragment.getView().findFocus();
                        View topView = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                        currentFragment.getView(),
                                focusView, View.FOCUS_UP);
                        if (topView != null && topView.getParent()
                                instanceof MenuRecycleView) {
                            ((NavFragment) currentFragment).requestMenuFocus();
                            return true;
                        }
                    }
                } else {
                    if (currentFragment != null && currentFragment.isNoTopView() &&
                            currentFragment.getView() != null) {
                        View focusView = currentFragment.getView().findFocus();
                        View topView = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                        currentFragment.getView(),
                                focusView, View.FOCUS_UP);
                        if (topView == null) {
                            if (mFirMenuRv.hasFocus()) return true;
                            mFirMenuRv.mCurrentCenterChildView.requestFocus();
                            return true;
                        }
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, "1");//退出应用
            ActivityStacks.get().ExitApp();
        }
    }

    private void showServiceTime() {
        ServiceTimeUtils.getServiceTime(new ServiceTimeUtils.TimeListener() {
            @Override
            public void success(TimeBean timeBean) {
                CharSequence sysTimeStr = DateFormat.format("HH:mm", timeBean.getResponse());
                //时间显示格式
                timeTV.setText(sysTimeStr); //更新时间
            }

            @Override
            public void fail() {
                long sysTime = System.currentTimeMillis();//获取系统时间
                CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);//时间显示格式
                timeTV.setText(sysTimeStr); //更新时间
            }
        });

    }

    @Override
    public View getTargetView() {
        return mRootLayout;
    }

    private void toSecondPageFromAd(String eventContentString) {
        Log.i(TAG, "toSecondPageFromAd");
        try {
            AdEventContent adEventContent = GsonUtil.fromjson(eventContentString, AdEventContent
                    .class);
            JumpUtil.activityJump(this, adEventContent.actionType, adEventContent.contentType,
                    adEventContent.contentUUID, adEventContent.actionURI);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void versionCheckResult(@Nullable UpVersion versionBeen, boolean isForce) {
        versionUpDialog(versionBeen, isForce);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {

    }

    @Override
    public void syncServerTime(Time result) {
        if(result != null) {
            CharSequence sysTimeStr = DateFormat.format("HH:mm", result.getResponse());
            timeTV.setText(sysTimeStr); //更新时间
        }else{
            long sysTime = System.currentTimeMillis();//获取系统时间
            CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);//时间显示格式
            timeTV.setText(sysTimeStr); //更新时间
        }
    }
}
