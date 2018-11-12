package tv.newtv.cboxtv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.NetworkManager;
import com.newtv.libs.util.ToastUtil;
import com.newtv.libs.util.YSLogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.player.PlayerUrlConfig;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.player.R;

/**
 * Created by wangkun on 2018/1/15.
 */
@BuyGoodsAD
public class NewTVLauncherPlayerActivity extends BaseActivity implements ContentContract
        .LoadingView {

    private static String TAG = "NewTVLauncherPlayerActivity";
    NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;
    int playPostion = 0;
    Content mProgramSeriesInfo;
    private FrameLayout mPlayerFrameLayoutContainer;

    private String contentUUID;
    private String contentType;

    private int mIndexPlay;
    private ContentContract.ContentPresenter mPresenter;

    public static void play(Context context, Bundle bundle) {
        Intent intent = new Intent(context, NewTVLauncherPlayerActivity.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public boolean isFullScreenActivity() {
        return true;
    }

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }

        if (PlayerUrlConfig.getInstance().isFromDetailPage())
        {//如果是从小屏切到大屏的，关闭播放器时恢复DetailPage值false
            PlayerUrlConfig.getInstance().setFromDetailPage(false);
        } else {//由推荐位直接到大屏播放器的，关闭播放器时清空保存的值
            PlayerUrlConfig.getInstance().setPlayingContentId("");
            PlayerUrlConfig.getInstance().setPlayUrl("");
            YSLogUtils.getInstance(NewTVLauncherPlayerActivity.this).clearData();
        }

        mPlayerFrameLayoutContainer = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtv_launcher_vod_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!NetworkManager.getInstance().isConnected()) {
            Toast.makeText(getApplicationContext(), R.string.net_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mPresenter = new ContentContract.ContentPresenter(getApplicationContext(), this);

        mPlayerFrameLayoutContainer = (FrameLayout) findViewById(R.id.player_view_container);
//        menuGroupPresenter = new MenuGroupPresenter2(this.getApplicationContext());
//        rootView.addView(menuGroupPresenter.getRootView());
        Bundle extras;
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
        } else {
            extras = savedInstanceState;
        }
        if (extras != null) {
            contentUUID = (String) extras.getString(Constant.CONTENT_UUID);
            contentType = (String) extras.getString(Constant.CONTENT_TYPE);
            if (TextUtils.isEmpty(contentUUID)) {
                ToastUtil.showToast(getApplicationContext(), "节目ID为空");
                finish();
                return;
            }

            mPresenter.getContent(contentUUID, true, contentType);
        }
    }

    private void initListener() {
//        menuGroupPresenter.addSelectListener(new MenuGroup.OnSelectListener() {
//            @Override
//            public void select(Program program) {
//                // 什么时候会修改Constant.isLiving的值？
//                // 5. 小屏时 强制点播下一个文件时 将isLiving置为false
//                Constant.isLiving = false;
//            }
//        });
//
//        NewTVLauncherPlayerViewManager.getInstance().addListener(new IPlayProgramsCallBackEvent
// () {
//
//            @Override
//            public void onNext(SubContent info, int index, boolean isNext) {
//                mIndexPlay = index;
//                mPositionPlay = 0;
//                RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(mIndexPlay,
//                        mPositionPlay, NewTVLauncherPlayerViewManager.getInstance()
//                        .getProgramSeriesInfo().getContentID()));
//            }
//        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "action:" + event.getAction() + ",keyCode=" + event.getKeyCode());
        if (NewTVLauncherPlayerViewManager.getInstance().dispatchKeyEvent(event)) {
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_ESCAPE:
                    releasePlayer();
                    finish();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyDown: ");
        NewTVLauncherPlayerViewManager.getInstance().onKeyDown(keyCode, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "onBackPressed");
        super.onBackPressed();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e(TAG, "onKeyUp: " + keyCode);
        NewTVLauncherPlayerViewManager.getInstance().onKeyUp(keyCode, event);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        if(mProgramSeriesInfo != null){
            doPlay(mProgramSeriesInfo);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");

        releasePlayer();
    }

    private void releasePlayer() {
        playPostion = NewTVLauncherPlayerViewManager.getInstance().getPlayPostion();
        mIndexPlay = NewTVLauncherPlayerViewManager.getInstance().getIndex();
        defaultConfig = NewTVLauncherPlayerViewManager.getInstance().getDefaultConfig();
        NewTVLauncherPlayerViewManager.getInstance().releasePlayer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState01: ");

        outState.putString(Constant.CONTENT_UUID, contentUUID);
        outState.putString(Constant.CONTENT_TYPE, contentType);
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        mProgramSeriesInfo = content;
        doPlay(content);
    }

    private void doPlay(Content content) {
        initListener();
        NewTVLauncherPlayerViewManager.getInstance().play(this, content, mIndexPlay, playPostion, false);
        NewTVLauncherPlayerViewManager.getInstance().setPlayerViewContainer
                (mPlayerFrameLayoutContainer, this);
        mIndexPlay = NewTVLauncherPlayerViewManager.getInstance().getIndex();
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {
        Toast.makeText(getApplicationContext(), desc, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void loadComplete() {

    }
}
