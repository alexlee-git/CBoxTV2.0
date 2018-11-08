package tv.newtv.cboxtv.uc.v2;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.StringUtils;
import com.newtv.libs.util.SystemUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.bean.ProgressListener;
import tv.newtv.cboxtv.uc.bean.Updater;
import tv.newtv.cboxtv.uc.bean.VersionBeen;
import tv.newtv.cboxtv.uc.v2.aboutmine.ScanScrollView;

public class VersionUpdateTwoActivity extends BaseActivity implements ScanScrollView.IScanScrollChangedListener {

    private String TAG = "VersionUpdateTwoActivity";
    private TextView tvUpdate;
    private TextView tvCancel;
    private RelativeLayout linerIsUpdate;
    private ProgressBar pbUpdate;
    private LinearLayout linerPrograss, llNoUpdate, llHaveUpdate, llUpdateIn;
    private int versionCode;
    private String data;
    private VersionBeen versionBeen;
    private TextView tvPrograss;
    private TextView tvVersion;

    private TextView tvDescribe, tvSystemVersion;
    private TextView tvCurrentDesc;
    private ScanScrollView svNoUpdate, svHaveUpdate;
    private ImageView ivBottomArrow1, ivBottomArrow2;

    private SharedPreferences.Editor editor;
    private String versionName;
    private Observable<String> versionUpFaildObservable;
    private String downApkUrl = "http://stage-bzo.cloud.ottcn.com/tiger/apps/NewTVCBoxTV_26468681.apk";
    private String apkPath;
    private boolean isUpdate = false;
    private long downloadId;
    private int saveCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update_new);
        SharedPreferences pref = getSharedPreferences("VersionMd5", MODE_PRIVATE);
        editor = pref.edit();
        apkPath = SharePreferenceUtils.getUpdateApkPath(VersionUpdateTwoActivity.this);
        downloadId = SharePreferenceUtils.getDownloadId(VersionUpdateTwoActivity.this);
        initView();
        initUp();
        initVersionUpFaild();
    }

    private void initView() {
        llNoUpdate = (LinearLayout) findViewById(R.id.ll_no_update);
        llHaveUpdate = (LinearLayout) findViewById(R.id.ll_have_update);
        llUpdateIn = (LinearLayout) findViewById(R.id.ll_update_sv_container);
        tvUpdate = ((TextView) findViewById(R.id.tv_update));
        tvCancel = ((TextView) findViewById(R.id.tv_cancel));
        linerIsUpdate = ((RelativeLayout) findViewById(R.id.liner_isupdate));
        pbUpdate = ((ProgressBar) findViewById(R.id.pb_prograss));
        linerPrograss = ((LinearLayout) findViewById(R.id.liner_prograss));
        tvPrograss = ((TextView) findViewById(R.id.tv_prograss));
        tvVersion = ((TextView) findViewById(R.id.tv_version));
        tvDescribe = ((TextView) findViewById(R.id.tv_describe));
        tvCurrentDesc = ((TextView) findViewById(R.id.tv_current_desc));
        tvSystemVersion = ((TextView) findViewById(R.id.tv_systemversion));
        svNoUpdate = (ScanScrollView) findViewById(R.id.sv_no_update);
        svHaveUpdate = (ScanScrollView) findViewById(R.id.sv_have_update);
        ivBottomArrow1 = (ImageView) findViewById(R.id.iv_bottom_arrow1);
        ivBottomArrow2 = (ImageView) findViewById(R.id.iv_bottom_arrow2);
        pbUpdate.setFocusable(true);

        initListener();


    }

    private void initListener() {
        svNoUpdate.setScanScrollChangedListener(this);
        svHaveUpdate.setScanScrollChangedListener(this);
        tvCancel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tvCancel.setBackgroundResource(R.drawable.later_update);
                    ScaleUtils.getInstance().onItemGetFocus(view);
                } else {

                    tvCancel.setBackgroundResource(R.drawable.later_normal);
                    ScaleUtils.getInstance().onItemLoseFocus(view);
                }
            }
        });


        tvUpdate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    tvUpdate.setBackgroundResource(R.drawable.atonce_up_fouces);
                    ScaleUtils.getInstance().onItemGetFocus(view);
                } else {

                    tvUpdate.setBackgroundResource(R.drawable.at_onece_normal);
                    ScaleUtils.getInstance().onItemLoseFocus(view);
                }
            }
        });


        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (versionBeen != null) {
                    if (versionBeen.getVersionCode() > versionCode) {
                        linerIsUpdate.setVisibility(View.GONE);
                        linerPrograss.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(versionBeen.getPackageAddr())) {
                            loadApk(versionBeen);
                        }
                    } else {
                        Toast.makeText(VersionUpdateTwoActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VersionUpdateTwoActivity.this.finish();
            }
        });


    }

    private void initUp() {
        try {
            versionName = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(versionName)) {
            tvSystemVersion.setText("V" + versionName);
        }

        //获取软件版本号，对应AndroidManifest.xml下android:versionCode
        try {
            versionCode = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final String hardwareCode = SystemUtils.getMac(this);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appKey", Libs.get().getAppKey());
        hashMap.put("channelCode", Libs.get().getChannelId());
        hashMap.put("versionCode", "" + versionCode);
        hashMap.put("uuid", Constant.UUID);
        hashMap.put("mac", SystemUtils.getMac(this));
        hashMap.put("hardwareCode", "" + hardwareCode);
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
                            data = value.string();
                            Log.e("=====", "data=" + data);
                            Gson gson = new Gson();
                            versionBeen = gson.fromJson(data, VersionBeen.class);
                            if (versionBeen.getVersionCode() == versionCode || TextUtils.isEmpty(versionBeen.getVersionName())) {
                                if (TextUtils.isEmpty(versionBeen.getVersionName())) {
                                    llNoUpdate.setVisibility(View.VISIBLE);
                                    llHaveUpdate.setVisibility(View.GONE);
                                    tvVersion.setText("版本信息");
                                    tvVersion.setTextColor(getResources().getColor(R.color.colorWhite));
                                    String currentInfo = SharePreferenceUtils.getUpdateInfo(VersionUpdateTwoActivity.this);
                                    if (TextUtils.isEmpty(currentInfo)) {
                                        tvCurrentDesc.setText("此版本已是最新版本");
                                    } else {
                                        tvCurrentDesc.setText(currentInfo);
                                    }
                                    tvCurrentDesc.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                        @Override
                                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                            if (tvCurrentDesc.getHeight() < svNoUpdate.getHeight()) {
                                                //文字信息显示内容不超过ScrollView固定高度时，箭头隐藏
                                                ivBottomArrow1.setVisibility(View.INVISIBLE);
                                            } else {
                                                ivBottomArrow1.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                    Constant.VERSION_UPDATE = false;
                                    isUpdate = false;
                                }

                            } else {
                                if (!TextUtils.isEmpty(versionBeen.getPackageMD5())) {
                                    editor.putString("versionmd5", versionBeen.getPackageMD5());
                                    editor.commit();
                                }
                                Constant.VERSION_UPDATE = true;
                                isUpdate = true;
                                llNoUpdate.setVisibility(View.GONE);
                                llHaveUpdate.setVisibility(View.VISIBLE);
                                tvVersion.setText("发现新版本:(v" + versionBeen.getVersionName() + ")");
                                tvDescribe.setText(versionBeen.getVersionDescription());
                                tvDescribe.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                    @Override
                                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                        if (llUpdateIn.getHeight() < svHaveUpdate.getHeight()) {
                                            //文字信息显示内容不超过ScrollView固定高度是，箭头隐藏
                                            ivBottomArrow2.setVisibility(View.INVISIBLE);
                                        } else {
                                            ivBottomArrow2.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                                SharePreferenceUtils.saveUpdateInfo(VersionUpdateTwoActivity.this, versionBeen.getVersionDescription());
                                if (versionBeen.getUpgradeType() == 1) {
                                    //强制升级
                                    tvCancel.setVisibility(View.GONE);
                                } else {
                                    //非强制升级
                                    tvCancel.setVisibility(View.VISIBLE);
                                }
                                linerIsUpdate.setVisibility(View.VISIBLE);
                                if (!TextUtils.isEmpty(apkPath) && StringUtils.fileIsExists(apkPath)) {
                                    justInstallApk(versionBeen.getPackageMD5());
                                } else {
                                    if (downloadId != 0L) {
                                        updateViews(downloadId);
                                    } else {
                                        linerIsUpdate.setVisibility(View.VISIBLE);
                                        linerPrograss.setVisibility(View.GONE);
                                    }

                                }
                            }

                        } catch (Exception e) {
                            getDataError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        getDataError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });


    }

    private Updater updater;
    private int progressData = 0;

    private void loadApk(VersionBeen versionBeen) {
        updater = new Updater.Builder(VersionUpdateTwoActivity.this)
                .setDownloadUrl(versionBeen.getPackageAddr())
//                .setDownloadUrl(downApkUrl)
                .setApkFileName("CBox.apk")
                .setNotificationTitle("updater")
                .start();
        updater.addProgressListener(new ProgressListener() {
            @Override
            public void onProgressChange(long totalBytes, long curBytes, final float progress) {
                pbUpdate.setProgress((int) progress);
                tvPrograss.setText("努力下载中" + (int) progress + "%");
                progressData = (int) progress;
                if ((int) progress == 100) {
                    Log.e(TAG, "---loadApk--------progress=" + progress);
                    if (saveCount <= 1) {
                        SharePreferenceUtils.saveDownloadId(VersionUpdateTwoActivity.this, 0L);
                    }
                    saveCount++;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                        }
                    }, 1000);

                }

            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void getDataError() {
        llNoUpdate.setVisibility(View.VISIBLE);
        llHaveUpdate.setVisibility(View.GONE);
        tvVersion.setText("版本信息");
        tvCurrentDesc.setText("获取版本信息失败,请稍后重试");
        tvVersion.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    /**
     * 用户已下载apk但未安装，判断apk是否存在并直接安装
     */
    private void justInstallApk(String versionBeanMd5) {
        //apk存在并且其md5与服务器返回数据相同说明存在最新下载的apk，触发发送下载完成的广播（此广播接收者调用apk安装方法）
        Log.e(TAG, "---justInstallApk---: ");
        File file = new File(apkPath);
        if (TextUtils.equals(versionBeanMd5, StringUtils.getFileMD5(file))) {
            Log.e(TAG, "---justInstallApk: md5相同：versionBeanMd5=" + versionBeanMd5);
            linerIsUpdate.setVisibility(View.GONE);
            linerPrograss.setVisibility(View.VISIBLE);
            pbUpdate.setProgress(100);
            tvPrograss.setText("已下载完成");
            installApk(file);
        } else {
            if (downloadId != 0L) {
                Log.e(TAG, "=========11111==: downloadId=" + downloadId);
                updateViews(downloadId);
            } else {
                linerIsUpdate.setVisibility(View.VISIBLE);
                linerPrograss.setVisibility(View.GONE);
            }
        }

    }

    /**
     * apk已下载直接去安装
     *
     * @param file
     */
    private void installApk(File file) {
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            Uri providerUri = FileProvider.getUriForFile(VersionUpdateTwoActivity.this, packageName + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(providerUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    /**
     * 下载文件有误,重新显示下载按钮
     */
    private void initVersionUpFaild() {
        versionUpFaildObservable = RxBus.get().register(Constant.UP_VERSION_IS_SUCCESS);
        versionUpFaildObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s) && "version_up_faild".equals(s)) {
                    SharePreferenceUtils.saveUpdateApkPath(VersionUpdateTwoActivity.this, "");
                    initUp();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (versionUpFaildObservable != null) {
            RxBus.get().unregister(Constant.UP_VERSION_IS_SUCCESS, versionUpFaildObservable);
        }
        if (progressData < 100) {
            if (updater != null) {
                SharePreferenceUtils.saveDownloadId(VersionUpdateTwoActivity.this, updater.getmTaskId());
            }
        }
    }

    @Override
    public void onScrolledToBottom() {
        setViewShow(true);
    }

    @Override
    public void onScrolledChange() {
        setViewShow(false);
    }

    @Override
    public void onScrolledToTop() {

    }

    /**
     * 根据当前升级状态动态控制向下箭头的显示
     */
    private void setViewShow(boolean isBottom) {
        if (isUpdate) {
            if (ivBottomArrow2 != null) {
                if (isBottom) {
                    ivBottomArrow2.setVisibility(View.INVISIBLE);
                } else {
                    ivBottomArrow2.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (ivBottomArrow1 != null) {
                if (isBottom) {
                    ivBottomArrow1.setVisibility(View.INVISIBLE);
                } else {
                    ivBottomArrow1.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void updateViews(final long downlaodId) {
        Log.e(TAG, "=========updateViews: downlaodId=" + downlaodId);
        linerIsUpdate.setVisibility(View.GONE);
        linerPrograss.setVisibility(View.VISIBLE);
        final Timer myTimer = new Timer();
        if (downlaodId != 0L) {
            myTimer.schedule(new TimerTask() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downlaodId);
                    Cursor cursor = ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).query(q);
                    cursor.moveToFirst();
                    int bytes_downloaded = cursor
                            .getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    cursor.close();
                    final int dl_progress = (bytes_downloaded * 100 / bytes_total);
                    if (dl_progress == 100) {
                        myTimer.cancel();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                tvPrograss.setText("已下载完成");
                                pbUpdate.setProgress(dl_progress);
                                if (saveCount <= 1) {
                                    SharePreferenceUtils.saveDownloadId(VersionUpdateTwoActivity.this, 0L);
                                }
                                saveCount++;
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pbUpdate.setProgress(dl_progress);
                                tvPrograss.setText("努力下载中" + (int) dl_progress + "%");
                            }
                        });

                    }

                }

            }, 0, 10);
        }


    }

}
