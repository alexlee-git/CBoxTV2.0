package tv.newtv.cboxtv.uc.v2;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.newtv.libs.Constant;
import com.newtv.libs.util.FileCacheUtils;
import com.newtv.libs.util.FileUtil;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.SharePreferenceUtils;

import java.io.File;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;


/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         17:38
 * 创建人:           weihaichao
 * 创建日期:          2018/8/24
 */
public class SettingActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnKeyListener,
        View.OnClickListener {
    private String[] SelectValues = {"是", "否"};

    private int subscribeIndex = 0;
    private int syncIndex = 0;

    private CheckBox syncSelector;
    private CheckBox subscribeSelector;
    private TextView tvVersion, tvNewTag, tvCacheSize;
    private boolean isNewTag = false;
    private String TAG = "SettingActivity";
    private PopupWindow popupWindow;
    private TextView tvClearCache, tvCancel, tvConfirmTimer, tvPopCache;
    private LinearLayout llClearCacheStart, llClearCacheFinish;
    private String cacheSize = "0.0";
    private CountDownTimer timer;
    private File picassoFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TextView titleView = findViewById(R.id.user_info_title);
        if (titleView != null) {
            titleView.setText("设置");
        }
        tvVersion = findViewById(R.id.update_text);
        tvNewTag = findViewById(R.id.update_tag);
        tvCacheSize = findViewById(R.id.clear_cache_text);
        findViewById(R.id.sync_continer).setOnFocusChangeListener(this);
        findViewById(R.id.subscribe_continer).setOnFocusChangeListener(this);
        findViewById(R.id.update_container).setOnFocusChangeListener(this);
        findViewById(R.id.clear_continer).setOnFocusChangeListener(this);

        findViewById(R.id.subscribe_continer).setOnKeyListener(this);
        findViewById(R.id.sync_continer).setOnKeyListener(this);

        findViewById(R.id.update_container).setOnClickListener(this);
        findViewById(R.id.clear_continer).setOnClickListener(this);

        subscribeSelector = findViewById(R.id.subscribe_text);
        syncSelector = findViewById(R.id.sync_text);
        initData();
        initCacheSize();
    }
    private void initData(){
        isNewTag = Constant.VERSION_UPDATE;
        syncIndex = SharePreferenceUtils.getSyncStatus(SettingActivity.this);
        initVersion();
        updateUI();
    }
    private void updateUI() {
        if (subscribeSelector != null)
            subscribeSelector.setText(SelectValues[subscribeIndex]);

        if (syncSelector != null)
            syncSelector.setText(SelectValues[syncIndex]);
        Log.e(TAG, "======updateUI========syncSelector=" + syncSelector.getText());

    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(v);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(v);
        }

        CheckBox target = v.findViewWithTag("infoText");
        if (target != null) {
            target.setChecked(hasFocus);
        }

        CheckBox title = v.findViewWithTag("InfoTitle");
        if (title != null) {
            title.setChecked(hasFocus);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT
                    && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
                return false;
            }
            switch (v.getId()) {
                case R.id.sync_continer:
                    onSyncChange(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
                    break;
                case R.id.subscribe_continer:
                    onSubscribeChange(keyCode == KeyEvent.KEYCODE_DPAD_LEFT);
                    break;
            }
        }

        return false;
    }

    /**
     * 调整同步
     *
     * @param isLeftKey
     */
    private void onSyncChange(boolean isLeftKey) {
        if (isLeftKey) {
            syncIndex--;
        } else {
            syncIndex++;
        }
        if (syncIndex < 0) {
            syncIndex = SelectValues.length - 1;
        } else if (syncIndex > SelectValues.length - 1) {
            syncIndex = 0;
        }
        Log.e(TAG, "======onSyncChange========syncIndex=" + syncIndex);
        updateUI();
    }

    /**
     * 调整订阅
     *
     * @param isLeftKey
     */
    private void onSubscribeChange(boolean isLeftKey) {
        if (isLeftKey) {
            subscribeIndex--;
        } else {
            subscribeIndex++;
        }
        if (subscribeIndex < 0) {
            subscribeIndex = SelectValues.length - 1;
        } else if (subscribeIndex > SelectValues.length - 1) {
            subscribeIndex = 0;
        }
        updateUI();
    }

    private void initVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
            String localVersion = packageInfo.versionName;
            tvVersion.setText("当前版本号：" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e(e.toString());
        }
        if (isNewTag) {
            tvNewTag.setVisibility(View.VISIBLE);
        } else {
            tvNewTag.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_continer:
                initPopup(v);
                break;
            case R.id.update_container:
                Intent intent = new Intent(this, VersionUpdateOneActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化并显示清除缓存弹框
     * @param v
     */
    private void initPopup(View v) {
        popupWindow = new PopupWindow(this);
        final View popView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.pop_clearcache, null);
        popupWindow.setContentView(popView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(true);

        tvClearCache = (TextView) popView.findViewById(R.id.tv_clear_cache);
        tvCancel = (TextView) popView.findViewById(R.id.tv_clear_cancel);
        tvPopCache = (TextView) popView.findViewById(R.id.tv_pop_cache_size);
        tvConfirmTimer = (TextView) popView.findViewById(R.id.tv_confirm_timer);
        llClearCacheStart = (LinearLayout) popView.findViewById(R.id.ll_clearcache_start);
        llClearCacheFinish = (LinearLayout) popView.findViewById(R.id.ll_clearcache_finish);
        tvPopCache.setText("当前" + cacheSize);

        tvClearCache.setOnFocusChangeListener(popFocusListener);
        tvCancel.setOnFocusChangeListener(popFocusListener);
        tvConfirmTimer.setOnFocusChangeListener(popFocusListener);

        tvClearCache.requestFocus();
        tvClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, " tvClearCache.setOnClickListener");
                FileCacheUtils.cleanInternalCache(getApplicationContext());
                FileCacheUtils.deleteFilesByDirectory(picassoFile);
                llClearCacheStart.setVisibility(View.GONE);
                llClearCacheFinish.setVisibility(View.VISIBLE);
                tvConfirmTimer.requestFocus();
                initTimer();
                timer.start();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, " tvCancel.setOnClickListener");
                dismissPop();
            }
        });
        tvConfirmTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, " tvConfirmTimer.setOnClickListener");
                if (timer != null) {
                    timer.cancel();
                }
                dismissPop();
            }
        });
        //消失监听听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e(TAG, "======0000=====onDismiss: " );
                initCacheSize();
                if (timer !=null){
                    timer.cancel();
                }
            }
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    private void dismissPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            initCacheSize();
        }
    }

    /**
     * 计时器
     */
    private void initTimer() {
        timer = new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvConfirmTimer.setText("确定 (" + (millisUntilFinished / 1000) + ")");
            }

            @Override
            public void onFinish() {
                dismissPop();
            }
        };
    }
    /**
     * 当前缓存数据
     */
    private void initCacheSize() {
    /*
    * 获取SD卡根目录：Environment.getExternalStorageDirectory().getAbsolutePath();
        外部Cache路径：/mnt/sdcard/android/data/com.xxx.xxx/cache 一般存储缓存数据（注：通过getExternalCacheDir()获取）
        外部File路径：/mnt/sdcard/android/data/com.xxx.xxx/files 存储长时间存在的数据
        （注：通过getExternalFilesDir(String type)获取， type为特定类型，可以是以下任何一种
                    Environment.DIRECTORY_MUSIC,
                    Environment.DIRECTORY_PODCASTS,
                     Environment.DIRECTORY_RINGTONES,
                     Environment.DIRECTORY_ALARMS,
                     Environment.DIRECTORY_NOTIFICATIONS,
                     Environment.DIRECTORY_PICTURES,
                      Environment.DIRECTORY_MOVIES. ）
    * */
        picassoFile = FileUtil.getCacheDirectory(getApplicationContext(),"cache_image");
        Log.e(TAG, "==picassoFile=====sdPath=" + picassoFile.getPath());
//        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Log.e(TAG, "==sdPath=======sdPath=" + sdPath);
//        File outFilePath = getExternalFilesDir(Environment.DIRECTORY_ALARMS);
//        Log.e(TAG, "==outFilePath=======path=" + outFilePath.getPath());
        File cacheDir = getCacheDir();
        Log.e(TAG, "==CacheFile=======path=" + cacheDir.getPath());
        try {
            long picassoCache  = FileCacheUtils.getFolderSize(picassoFile);
            long innerCache = FileCacheUtils.getFolderSize(cacheDir);
            double allSize = picassoCache + innerCache;
            cacheSize = FileCacheUtils.getFormatSize(allSize);
            Log.e(TAG, "==CacheFile=======cacheSize=" + cacheSize);
            tvCacheSize.setText(cacheSize);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharePreferenceUtils.saveSyncStatus(SettingActivity.this,syncIndex);
        if (popupWindow != null) {
            dismissPop();
        }
    }
    /**
     * popupwindow的焦点监听
     */
    private View.OnFocusChangeListener popFocusListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.setBackground(getResources().getDrawable(R.drawable.userinfo_set_clearcache_btn));
                doBigAnimation(v);
            } else {
                v.setBackground(getResources().getDrawable(R.drawable.userinfo_set_cancel_btn));
                doSmallAnimation(v);
            }
        }
    };
    /**
     * 选中时放大
     */
    private void doBigAnimation(View imageView) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bigx = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.1f);
        ObjectAnimator bigy = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.1f);
        animatorSet.play(bigx).with(bigy);
        animatorSet.start();
    }
    /**
     * 缩小恢复正常
     */
    private void doSmallAnimation(View imageView) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bigx = ObjectAnimator.ofFloat(imageView, "scaleX", 1.1f, 1f);
        ObjectAnimator bigy = ObjectAnimator.ofFloat(imageView, "scaleY", 1.1f, 1f);
        animatorSet.play(bigx).with(bigy);
        animatorSet.start();
    }
}
