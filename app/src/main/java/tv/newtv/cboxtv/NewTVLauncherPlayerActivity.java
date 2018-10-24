package tv.newtv.cboxtv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import com.newtv.libs.Libs;
import com.newtv.libs.util.NetworkManager;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ToastUtil;
import com.newtv.libs.util.YSLogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import tv.newtv.cboxtv.menu.IMenuGroupPresenter;
import tv.newtv.cboxtv.menu.MenuGroup;
import tv.newtv.cboxtv.menu.MenuGroupPresenter;
import tv.newtv.cboxtv.menu.model.Program;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.Player;
import tv.newtv.cboxtv.player.PlayerUrlConfig;
import tv.newtv.cboxtv.player.model.VideoPlayInfo;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.player.R;

/**
 * Created by wangkun on 2018/1/15.
 */

public class NewTVLauncherPlayerActivity extends BaseActivity implements ContentContract
        .LoadingView {

    private static final int PLAY_TYPE_SINGLE = 0;
    private static final int PLAY_TYPE_SERIES = 1;
    private static String TAG = "NewTVLauncherPlayerActivity";
    NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;
    int playPostion = 0;
    Content mProgramSeriesInfo;
    private FrameLayout mPlayerFrameLayoutContainer;
    private int mIndexPlay;
    private int mPositionPlay = 0;
    private ContentContract.ContentPresenter mPresenter;
    private FrameLayout rootView;
    private IMenuGroupPresenter menuGroupPresenter;
    private int abNormalExit = 0x002;//非正常退出
    private int normalExit = 0x001;//正常退出
    private int isContinue = normalExit;
    private int mPlayType = PLAY_TYPE_SINGLE;

    public static void play(Context context, String uuid, String suuid) {
        Intent intent = new Intent(context, NewTVLauncherPlayerActivity.class);
        intent.putExtra(Constant.CONTENT_UUID, uuid);
        intent.putExtra("seriesUUID", suuid);
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

        if (PlayerUrlConfig.getInstance().isFromDetailPage())
        {//如果是从小屏切到大屏的，关闭播放器时恢复DetailPage值false
            PlayerUrlConfig.getInstance().setFromDetailPage(false);
        } else {//由推荐位直接到大屏播放器的，关闭播放器时清空保存的值
            PlayerUrlConfig.getInstance().setPlayingContentId("");
            PlayerUrlConfig.getInstance().setPlayUrl("");
            YSLogUtils.getInstance(NewTVLauncherPlayerActivity.this).clearData();
        }

        if (menuGroupPresenter != null) {
            menuGroupPresenter.release();
            menuGroupPresenter = null;
        }

        rootView = null;
        mPlayerFrameLayoutContainer = null;
//        programSeriesInfo = null;
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

        rootView = findViewById(R.id.root_view);
        mPlayerFrameLayoutContainer = (FrameLayout) findViewById(R.id.player_view_container);
        menuGroupPresenter = new MenuGroupPresenter(this.getApplicationContext());
        rootView.addView(menuGroupPresenter.getRootView());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String contentUUID = (String) extras.getString(Constant.CONTENT_UUID);
            String seriesUUID = (String) extras.getString("seriesUUID");
            if (TextUtils.isEmpty(contentUUID)) {
                ToastUtil.showToast(getApplicationContext(), "节目ID为空");
                finish();
                return;
            }
            if (!TextUtils.isEmpty(seriesUUID)) {
                mPlayType = PLAY_TYPE_SERIES;
                mPresenter.getContent(seriesUUID, true);
            } else {
                mPlayType = PLAY_TYPE_SINGLE;
                mPresenter.getContent(contentUUID, true);
            }
        }
    }

    private void initListener() {
        menuGroupPresenter.addSelectListener(new MenuGroup.OnSelectListener() {
            @Override
            public void select(Program program) {
                // 什么时候会修改Constant.isLiving的值？
                // 5. 小屏时 强制点播下一个文件时 将isLiving置为false
                Constant.isLiving = false;
            }
        });

        NewTVLauncherPlayerViewManager.getInstance().addListener(new IPlayProgramsCallBackEvent() {

            @Override
            public void onNext(SubContent info, int index, boolean isNext) {
                mIndexPlay = index;
                mPositionPlay = 0;
                RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(mIndexPlay,
                        mPositionPlay, NewTVLauncherPlayerViewManager.getInstance()
                        .getProgramSeriesInfo().getContentID()));
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "action:" + event.getAction() + ",keyCode=" + event.getKeyCode());
        if (menuGroupPresenter != null && menuGroupPresenter.dispatchKeyEvent(event)) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_ESCAPE:
                    updateAndSave();
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

        if (isContinue == abNormalExit)
            NewTVLauncherPlayerViewManager.getInstance().setContinuePlay(Libs.get().getContext(),
                    mProgramSeriesInfo,
                    defaultConfig, playPostion);
        isContinue = normalExit;
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
        updateAndSave();

        mProgramSeriesInfo = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
        playPostion = NewTVLauncherPlayerViewManager.getInstance().getPlayPostion();
        defaultConfig = NewTVLauncherPlayerViewManager.getInstance().getDefaultConfig();
        NewTVLauncherPlayerViewManager.getInstance().releasePlayer();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState01: ");
        isContinue = abNormalExit;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i(TAG, "onSaveInstanceState02: ");
        isContinue = abNormalExit;
    }

    private void updateAndSave() {
        mIndexPlay = NewTVLauncherPlayerViewManager.getInstance().getIndex();
        mPositionPlay = NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition();
        if (NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo() != null) {
            RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(mIndexPlay,
                    mPositionPlay, NewTVLauncherPlayerViewManager.getInstance()
                    .getProgramSeriesInfo().getContentID()));
            addHistory();
        }
    }

    private void addHistory() {
        Content content = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
        if (content != null) {
            mPositionPlay = NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition();
            mIndexPlay = NewTVLauncherPlayerViewManager.getInstance().getIndex();
            //Player.get().onFinish(content, mIndexPlay, mPositionPlay);
        }
    }


    @Override
    public void onContentResult(@Nullable Content content) {
        doPlay(content);
    }

    private void doPlay(Content content) {
        boolean ready = false;
        switch (mPlayType) {
            case PLAY_TYPE_SINGLE:
                NewTVLauncherPlayerViewManager.getInstance().playProgramSingle
                        (getApplicationContext(), content, 0, false);
                ready = true;
                break;
            case PLAY_TYPE_SERIES:
                NewTVLauncherPlayerViewManager.getInstance().playProgramSeries
                        (getApplicationContext(), content, mIndexPlay, mPositionPlay);
                ready = true;
                break;
            default:
                break;
        }
        if (ready) {
            NewTVLauncherPlayerViewManager.getInstance().setPlayerViewContainer
                    (mPlayerFrameLayoutContainer, this);
            mIndexPlay = NewTVLauncherPlayerViewManager.getInstance().getIndex();
            initListener();
        }
    }

    @Override
    public void onSubContentResult(@Nullable ArrayList<SubContent> result) {

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
