package tv.newtv.cboxtv.views;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.UpVersion;
import com.newtv.libs.util.SPrefUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.bean.DownloadReceiver;
import tv.newtv.cboxtv.uc.bean.ProgressListener;
import tv.newtv.cboxtv.uc.bean.Updater;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         10:59
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class UpdateDialog {
    private static final String TAG = UpdateDialog.class.getSimpleName();
    private static final String APK_SIZE = "apk_size";
    private AlertDialog constraintDialog;
    private RelativeLayout rlUp;
    private LinearLayout linerPrograss;
    private TextView tvPrograss;
    private ProgressBar pbUpdate;


    public UpdateDialog() {

    }

    public void show(final Activity activity, final UpVersion versionBeen, boolean isForce) {
        View inflate = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout
                .version_up_item, null);
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
        if (isForce) {
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
                if (versionBeen != null && !TextUtils.isEmpty(versionBeen.getPackageAddr())) {//22696530
                    if (((Long) SPrefUtils.getValue(LauncherApplication.AppContext, APK_SIZE, 0L) > 0)
                            && String.valueOf(SPrefUtils.getValue(LauncherApplication.AppContext, APK_SIZE, 0L))
                            .equals(versionBeen.getPackageSize())) {
                        Intent intent = new Intent(LauncherApplication.AppContext, DownloadReceiver.MyIntentService.class);
                        intent.setAction("startIntentService");
                        LauncherApplication.AppContext.startService(intent);
                        if (constraintDialog != null && constraintDialog.isShowing()) {
                            constraintDialog.dismiss();
                        }
                    } else {
                        loadApk(activity, versionBeen.getPackageAddr());
                    }
                }
            }
        });

        constraintDialog = new AlertDialog.Builder(activity)
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
    private void loadApk(Activity activity, String addString) {
        final Updater updater = new Updater.Builder(activity)
                .setDownloadUrl(addString)
                .setApkFileName("CBox.apk")
                .setNotificationTitle("updater")
                .start();
        updater.addProgressListener(new ProgressListener() {
            @Override
            public void onProgressChange(long totalBytes, long curBytes, float progress) {
                pbUpdate.setProgress((int) progress);
                SPrefUtils.setValue(LauncherApplication.AppContext, APK_SIZE, curBytes);
                tvPrograss.setText(String.format(Locale.getDefault(), "努力下载中 %d%%", (int)
                        progress));
                if ((int) progress == 100) {
                    if (constraintDialog != null && constraintDialog.isShowing()) {
                        constraintDialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        LauncherApplication.AppContext.sendBroadcast(intent);
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
}
