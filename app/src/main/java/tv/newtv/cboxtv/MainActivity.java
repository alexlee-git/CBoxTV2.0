package tv.newtv.cboxtv;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.ActivityStacks;
import tv.newtv.cboxtv.bean.TimeBean;
import tv.newtv.cboxtv.cms.ad.model.AdEventContent;
import tv.newtv.cboxtv.cms.mainPage.MainPageManager;
import tv.newtv.cboxtv.cms.mainPage.menu.MainNavManager;
import tv.newtv.cboxtv.cms.mainPage.menu.NavFragment;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.util.GsonUtil;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.ModuleLayoutManager;
import tv.newtv.cboxtv.cms.util.NetworkManager;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.cms.util.SystemUtils;
import tv.newtv.cboxtv.uc.bean.ProgressListener;
import tv.newtv.cboxtv.uc.bean.Updater;
import tv.newtv.cboxtv.uc.bean.VersionBeen;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.utils.ServiceTimeUtils;
import tv.newtv.cboxtv.views.MenuRecycleView;

public class MainActivity extends BaseActivity implements BgChangManager.BGCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private final String LOGIN_CODE_SUCCESS = "1";
    private final int MSG_INIT_SDK_RETRY = 1;
    //private final int MSG_GET_SERVER_ADDR_RETRY = 2;
    private final int MSG_GET_TIME = 3;
    @BindView(R.id.id_root)
    RelativeLayout mRootLayout;
    @BindView(R.id.list_view)
    MenuRecycleView mFirMenuRv;
    @BindView(R.id.first_focus)
    View mFirFocus;
    @BindView(R.id.timer)
    TextView timeTV;
    private Map<String, String> mServerAddressMap;
    private int serverAddressRetryCnt;
    private int TIME_ONE_SECOND = 1 * 1000;
    private AlertDialog mAlertDialog;
    private AlertDialog mAuthAlertDialog;
    private String mExternalAction;
    private String mExternalParams;
    private int versionCode;
    private AlertDialog constraintDialog;
    private RelativeLayout rlUp;
    private LinearLayout linerPrograss;
    private TextView tvPrograss;
    private ProgressBar pbUpdate;
    private Observable<String> versionUpFaildObservable;
    private SharedPreferences mSharedPreferences;
    private boolean isScroller = true;
    //广播显示系统时间
    private BroadcastReceiver mTimeRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                showServiceTime();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey
                ("android:support:fragments")) {
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initVersionUpFaild();
        registerReceiver(mTimeRefreshReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        //注册广播，显示系统时间

        Intent mIntent = getIntent();
        if (mIntent != null) {
            mExternalAction = mIntent.getStringExtra("action");
            mExternalParams = mIntent.getStringExtra("params");
            Log.e("---External", mExternalAction + "--" + mExternalParams);
        }

        mSharedPreferences = getSharedPreferences("VersionMd5", MODE_PRIVATE);
        initIsOriented();
        initModules();
        //loadVersion("");
        logUpInitData();

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
    }

    //检查是否定向升级
    private void initIsOriented() {
        final String hardwareCode = SystemUtils.getMac(this);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appKey", Constant.APP_KEY);
        hashMap.put("channelCode", Constant.CHANNEL_ID);
        try {
            versionCode = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.toString());
        }
        hashMap.put("versionCode", "" + versionCode);
        hashMap.put("uuid", Constant.UUID);
        hashMap.put("mac", SystemUtils.getMac(this));
        hashMap.put("hardwareCode", "" + hardwareCode);
        NetClient.INSTANCE.getUpVersion().getIsOriented(hashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String string = responseBody.string();
                    JSONObject jsonObject = new JSONObject(string);
                    boolean orientedHas = jsonObject.has("oriented");
                    if (orientedHas) {
                        String oriented = jsonObject.getString("oriented");
                        if (!TextUtils.isEmpty(oriented) && oriented.equals("enable")) {
                            loadVersion(hardwareCode);
                        } else {
                            loadVersion("");
                        }
                    }

                } catch (IOException e) {
                    LogUtils.e(e.toString());
                } catch (JSONException e) {
                    LogUtils.e(e.toString());
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


    }

    @SuppressLint("CheckResult")
    private void initVersionUpFaild() {
        versionUpFaildObservable = RxBus.get().register(Constant.UP_VERSION_IS_SUCCESS);
        versionUpFaildObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s) && "version_up_faild".equals(s)) {
                    loadVersion("");
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    //开始获取版本
    private void loadVersion(String hardwareCode) {
        //获取软件版本号，对应AndroidManifest.xml下android:versionCode
        try {
            versionCode = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.toString());
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appKey", Constant.APP_KEY);
        hashMap.put("channelCode", Constant.CHANNEL_ID);
        hashMap.put("versionCode", "" + versionCode);
        if (!TextUtils.isEmpty(hardwareCode)) {
            hashMap.put("hardwareCode", "" + hardwareCode);
        }
        hashMap.put("uuid", Constant.UUID);
        hashMap.put("mac", SystemUtils.getMac(this));
        NetClient.INSTANCE.getUpVersion().getUpVersion(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            String data = value.string();
                            Gson gson = new Gson();
                            VersionBeen versionBeen = gson.fromJson(data, VersionBeen.class);
                            if (versionBeen == null) {
                                return;
                            }
                            if (versionBeen.getVersionCode() > versionCode && !TextUtils.isEmpty
                                    (versionBeen.getVersionName())) {
                                if (!TextUtils.isEmpty(versionBeen.getPackageMD5())) {
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    editor.putString("versionmd5", versionBeen.getPackageMD5());
                                    editor.apply();
                                }
                                if (versionBeen.getUpgradeType() == 1) {
                                    //强制升级
                                    initConstraintDialog(versionBeen, true);
                                } else {
                                    //非强制升级
                                    initConstraintDialog(versionBeen, false);
                                }
                            }
                        } catch (Exception e) {
                            LogUtils.e(e.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void initConstraintDialog(final VersionBeen versionBeen, boolean isForceUp) {
        View inflate = LayoutInflater.from(this).inflate(R.layout.version_up_item, null);
        TextView tvUserTitle = (TextView) inflate.findViewById(R.id.tv_user_title);
        tvUserTitle.setText(R.string.new_version_tip);
        rlUp = (RelativeLayout) inflate.findViewById(R.id.rl_up);
        TextView tvVersion = (TextView) inflate.findViewById(R.id.tv_version);
        linerPrograss = ((LinearLayout) inflate.findViewById(R.id.liner_prograss));
        pbUpdate = ((ProgressBar) inflate.findViewById(R.id.pb_prograss));
        tvPrograss = ((TextView) inflate.findViewById(R.id.tv_prograss));
        ImageView ivLatter = (ImageView) inflate.findViewById(R.id.iv_imate_latter);
        TextView tvInfo = (TextView) inflate.findViewById(R.id.tv_up_info);
        if (versionBeen != null && !TextUtils.isEmpty(versionBeen.getVersionName())) {
            tvVersion.setText(String.format("版本更新(%s)", versionBeen.getVersionName()));
        }
        if (versionBeen != null && !TextUtils.isEmpty(versionBeen.getVersionDescription())) {
            tvInfo.setText(versionBeen.getVersionDescription());
        }
        ImageView ivImateup = (ImageView) inflate.findViewById(R.id.iv_imate_up);
        if (isForceUp) {
            ivLatter.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivImateup
                    .getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            ivImateup.setLayoutParams(layoutParams);

        } else {
            ivLatter.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParamsLeft = (RelativeLayout.LayoutParams)
                    ivImateup.getLayoutParams();
            layoutParamsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParamsLeft.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            ivImateup.setLayoutParams(layoutParamsLeft);
            RelativeLayout.LayoutParams layoutParamsRight = (RelativeLayout.LayoutParams)
                    ivLatter.getLayoutParams();
            layoutParamsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParamsRight.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            ivLatter.setLayoutParams(layoutParamsRight);
        }

        ivImateup.requestFocus();
        ivLatter.setFocusable(true);
        ivImateup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlUp.setVisibility(View.GONE);
                linerPrograss.setVisibility(View.VISIBLE);
                if (versionBeen != null && !TextUtils.isEmpty(versionBeen.getPackageAddr())) {
                    loadApk(versionBeen.getPackageAddr());
                }

            }
        });
        constraintDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(inflate)
                .create();
        if (constraintDialog.getWindow() != null) {
            constraintDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            //去掉这句话，背景会变暗
            constraintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color
                    .TRANSPARENT));
        }
        constraintDialog.show();
        ivLatter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (constraintDialog != null && constraintDialog.isShowing()) {
                    constraintDialog.dismiss();
                }
            }
        });
    }

    //更新下载apk
    private void loadApk(String addString) {
        final Updater updater = new Updater.Builder(MainActivity.this)
                .setDownloadUrl(addString)
                .setApkFileName("CBox.apk")
                .setNotificationTitle("updater")
                .start();
        updater.addProgressListener(new ProgressListener() {
            @Override
            public void onProgressChange(long totalBytes, long curBytes, float progress) {
                pbUpdate.setProgress((int) progress);
                tvPrograss.setText(String.format(Locale.getDefault(), "努力下载中 %d%%", (int)
                        progress));
                if ((int) progress == 100) {
                    if (constraintDialog != null && constraintDialog.isShowing()) {
                        constraintDialog.dismiss();
                        // Toast.makeText(MainActivity.this,"安装..并等待",Toast.LENGTH_SHORT).show();
                    }
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            //updater.getDownloadManager().remove(updater.getmTaskId());
                        }
                    }, 1000);

                }

            }
        });


    }

    /**
     * 上报一些基础信息
     * 1.终端信息上报（厂商, 设备型号, android版本）
     * 2.app版本号的上报
     */
    private void logUpInitData() {
        try {

            StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
            PackageInfo pckInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            dataBuff.append("0,")
                    .append(pckInfo.versionName)
                    .trimToSize();
            LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, dataBuff.toString());//进入应用


            dataBuff.delete(0, dataBuff.length());
            dataBuff.append(Build.MANUFACTURER)
                    .append(",")
                    .append(Build.MODEL)
                    .append(",")
                    .append(Build.VERSION.RELEASE)
                    .trimToSize(); // 设备信息
            LogUploadUtils.uploadLog(Constant.LOG_NODE_DEVICE_INFO, dataBuff.toString());

            dataBuff.delete(0, dataBuff.length());

            dataBuff.append(pckInfo.applicationInfo.loadLabel(getPackageManager()))
                    .append(",")
                    .append(pckInfo.versionName)
                    .append(",")
                    .trimToSize(); // 版本信息

            LogUploadUtils.uploadLog(Constant.LOG_NODE_APP_VERSION, dataBuff.toString());


        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
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
        if (versionUpFaildObservable != null) {
            RxBus.get().unregister(Constant.UP_VERSION_IS_SUCCESS, versionUpFaildObservable);
        }
        LogUtils.e(Constant.TAG, "MainActivity onDestory");
        super.onDestroy();
        if (mTimeRefreshReceiver != null) {
            unregisterReceiver(mTimeRefreshReceiver);
        }

        serverAddressRetryCnt = 0;

        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }

        if (mAuthAlertDialog != null) {
            mAuthAlertDialog.dismiss();
            mAuthAlertDialog = null;
        }

        Constant.isInitStatus = true;

        MainPageManager.getInstance().unInit();
        ModuleLayoutManager.getInstance().unit();
        SuperScriptManager.getInstance().unit();
        MainNavManager.getInstance().unInit();

    }

    /**
     * 1.初始化状态栏模块
     * 2.初始化内容区模块
     */
    private void initModules() {
        NetworkManager.getInstance().init(getApplicationContext());

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
            if (!BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA)) {
                if (SystemUtils.isFastDoubleClick()) {
                    return true;
                }
            }
//            Log.e("mFirMenuRv1", "mFirMenuRv.getScrollState()" + mFirMenuRv.getScrollState());
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, "1");//退出应用
            ActivityStacks.get().ExitApp();
        }
    }

    private boolean parseServerAddress(String serverInfo) {
        boolean result = false;
        if (TextUtils.isEmpty(serverInfo)) {
            LogUtils.e("data from bootg ¬uide interface is empty");
            return result;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(serverInfo
                    .getBytes())));
            NodeList list = document.getElementsByTagName("address");
            for (int i = 0; i < list.getLength(); ++i) {
                NamedNodeMap namedNodeMap = list.item(i).getAttributes();
                Node urlNode = namedNodeMap.getNamedItem("url");
                Node nameNode = namedNodeMap.getNamedItem("name");
                mServerAddressMap.put(nameNode.getNodeValue(), urlNode.getNodeValue());
            }
            result = true;
            LogUtils.i("parse server address completed");
            return result;
        } catch (ParserConfigurationException e) {
            LogUtils.e("parse server address ParserConfigurationException" + e);
        } catch (SAXException e) {
            LogUtils.e("parse server address SAXException" + e);
        } catch (IOException e) {
            LogUtils.e("parse server address IOException" + e);
        }
        return result;
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
}
