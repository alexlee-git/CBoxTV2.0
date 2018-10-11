package tv.newtv.cboxtv.uc;

import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newtv.libs.util.ScaleUtils;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.player.BaseActivity;
import tv.newtv.cboxtv.uc.bean.ProgressListener;
import tv.newtv.cboxtv.uc.bean.Updater;
import tv.newtv.cboxtv.uc.bean.VersionBeen;

public class VersionUpdateActivity extends BaseActivity {

    private TextView tvUpdate;
    private TextView tvCancel;
    private RelativeLayout linerIsUpdate;
    private ProgressBar pbUpdate;
    private LinearLayout linerPrograss;
    private Object retrofit;
    private int versionCode;
    private String data;
    private DownloadManager mDownloadManager;
    private long mId;
    private VersionBeen versionBeen;
    private TextView tvPrograss;
    private TextView tvVersion;

    private TextView tvDescribe,tvSystemVersion;
    private TextView tv;
    private TextView tvIntroduce;
    private TextView tvUser;
    private SharedPreferences.Editor editor;
    private String versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);
        SharedPreferences pref = getSharedPreferences("VersionMd5",MODE_PRIVATE);
        editor = pref.edit();


        initView();
        initUp();


    }

    private void initView() {
        tvIntroduce = ((TextView) findViewById(R.id.tv_version_introduce));
        tvUpdate = ((TextView) findViewById(R.id.tv_update));
        tvCancel = ((TextView) findViewById(R.id.tv_cancel));
        linerIsUpdate = ((RelativeLayout) findViewById(R.id.liner_isupdate));
        pbUpdate = ((ProgressBar) findViewById(R.id.pb_prograss));
        linerPrograss = ((LinearLayout) findViewById(R.id.liner_prograss));
        tvPrograss = ((TextView) findViewById(R.id.tv_prograss));
        tvVersion = ((TextView) findViewById(R.id.tv_version));
        tvDescribe = ((TextView) findViewById(R.id.tv_describe));
        tvUser = ((TextView) findViewById(R.id.tv_user));
        tvSystemVersion = ((TextView) findViewById(R.id.tv_systemversion));
        pbUpdate.setFocusable(true);

        initListener();


    }

    private void initListener() {
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
                       if (!TextUtils.isEmpty(versionBeen.getPackageAddr())){
                           loadApk(versionBeen);
                       }

                    } else {
                        Toast.makeText(VersionUpdateActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }

                }


            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VersionUpdateActivity.this.finish();
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
        if(!TextUtils.isEmpty(versionName)){
            tvSystemVersion.setText(getResources().getString(R.string.user_system_version)+"  "+versionName);}

        //获取软件版本号，对应AndroidManifest.xml下android:versionCode
        try {
            versionCode = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("appKey", BuildConfig.APP_KEY);
        hashMap.put("channelCode", BuildConfig.CHANNEL_ID);
        hashMap.put("versionCode", "" + versionCode);
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
                                    tvUser.setVisibility(View.VISIBLE);
                                    tvVersion.setText("版本更新(已是最新版)");
                                    tvIntroduce.setText("此版本已是最新版本");
                                    linerIsUpdate.setVisibility(View.GONE);
                                }

                            } else {
                                if (!TextUtils.isEmpty(versionBeen.getPackageMD5())){
                                    editor.putString("versionmd5", versionBeen.getPackageMD5());
                                    editor.commit();
                                }

                                tvUser.setVisibility(View.VISIBLE);
                                linerIsUpdate.setVisibility(View.VISIBLE);
                                tvDescribe.setText(versionBeen.getVersionDescription());
                                tvIntroduce.setText("能享受新版本的服务了,更新如下");
                                tvVersion.setText("发现新版本:(v" + versionBeen.getVersionName() + ")");
                            }

                        } catch (Exception e) {
                            getDataError();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("=====", "onError");
                        getDataError();
                    }

                    @Override
                    public void onComplete() {
                    }
                });


    }

    private void loadApk(VersionBeen versionBeen) {

        final Updater updater = new Updater.Builder(VersionUpdateActivity.this)
                .setDownloadUrl(versionBeen.getPackageAddr())
                .setApkFileName("CBox.apk")
                .setNotificationTitle("updater")
                .start();
        updater.addProgressListener(new ProgressListener() {
            @Override
            public void onProgressChange(long totalBytes, long curBytes, float progress) {
                pbUpdate.setProgress((int) progress);
                tvPrograss.setText("努力下载中" + (int) progress + "%");
                if ((int) progress == 100) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // updater.getDownloadManager().remove(updater.getmTaskId());
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
//        tvVersion.setText("版本更新");
        tvIntroduce.setText("获取版本信息失败,请稍后重试");
        linerIsUpdate.setVisibility(View.GONE);
    }
}
