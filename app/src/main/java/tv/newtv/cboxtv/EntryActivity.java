package tv.newtv.cboxtv;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.contract.ActiveAuthContract;
import com.newtv.cms.contract.AdContract;
import com.newtv.cms.contract.EntryContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.NetworkManager;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.SystemUtils;
import com.trello.rxlifecycle2.components.support.RxFragmentActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;

import tv.newtv.cboxtv.cms.net.HeadersInterceptor;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.player.ad.ADPlayerView;

/**
 * Created by TCP on 2018/4/12.
 */
public class EntryActivity extends RxFragmentActivity implements ActiveAuthContract.View,
        EntryContract.View, AdContract.View {
    private static final String TAG = "EntryActivity";
    private static final String EXTERNAL = "external";

    private ActiveAuthContract.ActiveAuthPresenter mAuthPresenter;
    private EntryContract.EntryPresenter mSplashPresenter;
    private AdContract.AdPresenter mAdPresenter;

    //广告内容显示
    private ADPlayerView videoView;
    private ImageView imageView;
    private TextView mAuthingView;

    private Dialog mAlertDialog;

    private boolean mStoped = false;

    private Intent mIntent;
    private String mExternalAction;
    private String mExternalParams;

    private boolean isShowingAD = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (imageView != null) {
            imageView.setImageDrawable(null);
        }

        if (mAuthPresenter != null) {
            mAuthPresenter.destroy();
            mAuthPresenter = null;
        }


        if (mSplashPresenter != null) {
            mSplashPresenter.destroy();
            mAuthPresenter = null;
        }


        if (videoView != null) {
            videoView.release();
            videoView = null;
        }

        isShowingAD = false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            Intent i = getIntent();
            String action = i.getAction();
            if (!TextUtils.isEmpty(action)
                    && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_splash);

        if (BuildConfig.DEBUG) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            Log.i("Splash", "screenWidth=" + screenWidth + ":screenHeight" + screenHeight);
        }

        /*
        认证流程：
        1、判断是不是设备和apk版本一致，是不是合作终端
        2、调取激活接口，获取UUID
        3、调取认证接口，进行认证工作
        */
        // 1、判断是不是设备和apk版本一致，是不是合作终端
        if (!DeviceUtil.isSelfDevice()) {
            String displayMessage = getResources().getString(R.string
                    .tip_text_auth_error) + "\n\n";
            displayMessage += getErrorMsg(ActiveAuthContract.ActiveAuthPresenter.Constract.NOT_SELF_DEVICE);
            mAlertDialog = getDialog(displayMessage, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                    }
                    finish();
                }
            });
            if (!isFinishing()) {
                mAlertDialog.show();
            }
            return;
        }

        // 判断时候有网络
        if (!NetworkManager.getInstance().isConnected()) {
            mAlertDialog = getDialog(getResources().getString(R.string.net_error), new View
                    .OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                    }
                    finish();
                }
            });
            if (!isFinishing()) {
                mAlertDialog.show();
            }
            return;
        }

        mIntent = getIntent();
        if (mIntent != null) {
            mExternalAction = mIntent.getStringExtra("action");
            mExternalParams = mIntent.getStringExtra("params");
        }

        initView();

        mAuthPresenter = new ActiveAuthContract.ActiveAuthPresenter(getApplicationContext(), this);
        mAdPresenter = new AdContract.AdPresenter(getApplicationContext(), this);
        mSplashPresenter = new EntryContract.EntryPresenter(getApplicationContext(), this);
        mSplashPresenter.initCNTVLog(getApplication());
    }

    private void initRetryUrls() {
        Constant.activateUrls.clear();

        String activate = !TextUtils.isEmpty(Constant.getBaseUrl(HeadersInterceptor.ACTIVATE))
                ? Constant.getBaseUrl(HeadersInterceptor.ACTIVATE)
                : "https://terminal.cloud.ottcn.com/";
        Constant.activateUrls.add(activate);

        String activate2 = !TextUtils.isEmpty(Constant.getBaseUrl(HeadersInterceptor.ACTIVATE2))
                ? Constant.getBaseUrl(HeadersInterceptor.ACTIVATE2)
                : "https://terminal2.cloud.ottcn.com/";
        Constant.activateUrls.add(activate2);
    }

    protected void initView() {
        System.out.print("EntryActivity init");

        videoView = findViewById(R.id.splash_video_view);
        imageView = findViewById(R.id.splash_image_view);
        mAuthingView = findViewById(R.id.authing);

        //视频全屏
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        videoView.setLayoutParams(layoutParams);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Override
    public void failed(int type, int status) {
        switch (type) {
            case ActiveAuthContract.ActiveAuthPresenter.Constract.AUTH:

                //因为不是用的激活认证的sdk，所以版本类型和版本号都不用上传
                RxBus.get().post(Constant.INIT_SDK, Constant.INIT_LOGSDK);
                authLogFailed(status);//认证失败

                mAlertDialog = getDialog(new StringBuilder().append(getResources().getString(R
                        .string.tip_text_auth_error)).append("\n\n").append(getErrorMsg(status))
                        .toString(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                        finish();
                    }
                });
                if (!isFinishing()) {
                    mAlertDialog.show();
                }
                break;
            case ActiveAuthContract.ActiveAuthPresenter.Constract.ACTIVATE:
                mAlertDialog = getDialog(new StringBuilder().append(getResources().getString(R
                                .string.tip_text_active_error)).append(getErrorMsg(status))
                                .toString(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mAlertDialog != null) {
                                    mAlertDialog.dismiss();
                                }
                                finish();
                            }
                        });
                if (!isFinishing()) {
                    mAlertDialog.show();
                }
                break;
        }

        mAuthingView.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
        // 广告播放的过程中按方向键跳过广告
        if (event.getAction() == KeyEvent.ACTION_DOWN && isShowingAD) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    enterMain();
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    // TODO test
                    if (mAdPresenter.isAdHasEvent()) {
                        mExternalAction = Constant.EXTERNAL_OPEN_URI;
                        enterMain();
                    }
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //当在播放广告的时候用户按home键，如果没有取消，会自动弹出此activity
        mStoped = true;
        if (mAuthPresenter != null) {
            mAuthPresenter.destroy();
            mAuthPresenter = null;
        }
        finish();
    }

    private void enterMain() {
        mAdPresenter.destroy();
        authLogSuccess();//认证成功

        if (mStoped) {
            ActivityStacks.get().ExitApp();
            return;
        }

        Intent intent = null;
        if (!TextUtils.isEmpty(mExternalAction)) {
            if (Constant.EXTERNAL_OPEN_NEWS.equals(mExternalAction) || Constant
                    .EXTERNAL_OPEN_PANEL.equals(mExternalAction)) {
                intent = new Intent(EntryActivity.this, MainActivity.class);
                intent.putExtra("action", mExternalAction);
                intent.putExtra("params", mExternalParams);
            } else if (Constant.EXTERNAL_OPEN_URI.equals(mExternalAction)) {//点击广告进入详情页
                intent = new Intent(EntryActivity.this, MainActivity.class);
                intent.putExtra("action", mExternalAction);
                intent.putExtra("params", mAdPresenter.getAdItem().eventContent);
            } else {
                boolean jump = JumpUtil.parseExternalJump(getApplicationContext(),
                        mExternalAction,
                        mExternalParams);
                // add log
                LogUploadUtils.uploadEnterAppLog(getApplicationContext());
                // end
                if (jump) {
                    finish();
                    return;
                }
            }
        } else {
            intent = new Intent(EntryActivity.this, MainActivity.class);
        }
        //因为不是用的激活认证的sdk，所以版本类型和版本号都不用上传
        startActivity(intent);

        finish();
    }

    public Dialog getDialog(String message, View.OnClickListener l) {
        Dialog dialog = new Dialog(this, R.style.search_dialog);
        dialog.setCancelable(false);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_dialog_view,
                null, false);
        dialog.setContentView(dialogView);

        TextView dialogTextView = (TextView) dialogView.findViewById(R.id.id_dialog_msg);
        dialogTextView.setText(message);

        Button dialogBtn = (Button) dialogView.findViewById(R.id.id_dialog_btn);
        dialogBtn.setOnClickListener(l);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = DisplayUtils.translate(543, 0);
            lp.height = DisplayUtils.translate(423, 1);
            window.setAttributes(lp);
        }

        return dialog;
    }

    private void authLogSuccess() {
        //因为不是用的激活认证的sdk，所以版本类型和版本号都不用上传
        StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_16);
        logBuff.append(0 + ",")
                .append("" + ",")
                .append("")
                .trimToSize();

        LogUploadUtils.uploadLog(Constant.LOG_NODE_AUTH_INFO, logBuff
                .toString());//认证成功
    }

    private void authLogFailed(int status) {
        //因为不是用的激活认证的sdk，所以版本类型和版本号都不用上传
        StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_16);
        logBuff.append(1 + ",")
                .append("" + ",")
                .append("" + ",")
                .append(status)
                .trimToSize();//认证失败

        LogUploadUtils.uploadLog(Constant.LOG_NODE_AUTH_INFO, logBuff
                .toString());//认证成功
    }

    private String getErrorMsg(int errorCode) {
        String errorMsg = "\n\n错误码:" + errorCode +
                "\n应用版本号:" + DeviceUtil.getAppVersion(this) +
                "\nMAC地址:" + SystemUtils.getDeviceMac(EntryActivity.this) +
                "\n如有疑问请拨打客服电话 : 4000463366" +
                "\nQQ : 800085092";

        return errorMsg;
    }

    @Override
    public void bootGuildResult() {
        initRetryUrls();

        String appkey = BuildConfig.APP_KEY;
        String channelId = BuildConfig.CHANNEL_ID;
        if (TextUtils.isEmpty(appkey) || TextUtils.isEmpty(channelId)) {
            Toast.makeText(this, "参数丢失，无法激活设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mAuthPresenter.active();
    }

    @Override
    public void authResult() {
        RxBus.get().post(Constant.INIT_SDK, Constant.INIT_LOGSDK);
        mAdPresenter.getAdByType("open", "", "",null);
        mAuthingView.setVisibility(View.GONE);
    }

    @Override
    public void activeResult() {
        mAuthPresenter.auth();
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String desc) {

    }


    @Override
    public void updateTime(int total, int left) {
        TextView timer = findViewById(R.id.count_down);
        if (timer != null) {
            timer.setVisibility(View.VISIBLE);
            timer.setText(String.format(Locale
                    .getDefault(), "广告剩余时间 %d 秒", left));
        }
    }

    @Override
    public void complete() {
        enterMain();
    }

    @Override
    public void showAd(@Nullable String type, @Nullable String url, @Nullable HashMap<?, ?> hashMap) {
        if (TextUtils.isEmpty(url)) {
            enterMain();
        } else {
            isShowingAD = true;

            if (Constant.AD_IMAGE_TYPE.equals(type)) {
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                imageView.setImageURI(Uri.parse(url));
            } else if (Constant.AD_VIDEO_TYPE.equals(type)) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
                videoView.setDataSource(url);
                videoView.play();
            }
        }
    }
}
