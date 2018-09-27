package tv.newtv.cboxtv.player.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.details.model.VideoPlayInfo;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.NetworkManager;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.cms.util.Utils;
import tv.newtv.cboxtv.cms.util.YSLogUtils;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.PlayerUrlConfig;
import tv.newtv.cboxtv.player.menu.IMenuGroupPresenter;
import tv.newtv.cboxtv.player.menu.MenuGroup;
import tv.newtv.cboxtv.player.menu.MenuGroupPresenter;
import tv.newtv.cboxtv.player.menu.model.Program;
import tv.newtv.cboxtv.uc.db.DBCallback;
import tv.newtv.cboxtv.utils.DBUtil;

/**
 * Created by wangkun on 2018/1/15.
 */

public class NewTVLauncherPlayerActivity extends BaseActivity {

    private static String TAG = "NewTVLauncherPlayerActivity";

    private FrameLayout mPlayerFrameLayoutContainer;
    private int mIndexPlay;
    private int mPositionPlay = 0;
    private ProgramSeriesInfo programSeriesInfo;

    private FrameLayout rootView;
    private IMenuGroupPresenter menuGroupPresenter;
    private int abNormalExit = 0x002;//非正常退出
    private int normalExit = 0x001;//正常退出
    private int isContinue = normalExit;

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();

        if (PlayerUrlConfig.getInstance().isFromDetailPage()) {//如果是从小屏切到大屏的，关闭播放器时恢复DetailPage值false
            PlayerUrlConfig.getInstance().setFromDetailPage(false);
        } else {//由推荐位直接到大屏播放器的，关闭播放器时清空保存的值
            PlayerUrlConfig.getInstance().setPlayingContentId("");
            PlayerUrlConfig.getInstance().setPlayUrl("");
            YSLogUtils.getInstance(this).clearData();
        }

        if (menuGroupPresenter != null) {
            menuGroupPresenter.release();
            menuGroupPresenter = null;
        }

        rootView = null;
        mPlayerFrameLayoutContainer = null;
        programSeriesInfo = null;
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
        }

        rootView = findViewById(R.id.root_view);
        mPlayerFrameLayoutContainer = (FrameLayout) findViewById(R.id.player_view_container);
        menuGroupPresenter = new MenuGroupPresenter(this.getApplicationContext());
        rootView.addView(menuGroupPresenter.getRootView());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            programSeriesInfo = (ProgramSeriesInfo) extras.getSerializable("programSeriesInfo");
        }
        NewTVLauncherPlayerViewManager.getInstance().setPlayerViewContainer(mPlayerFrameLayoutContainer, this);

        mIndexPlay = NewTVLauncherPlayerViewManager.getInstance().getIndex();
        initListener();
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
            public void onNext(ProgramSeriesInfo.ProgramsInfo info, int index, boolean isNext) {
                mIndexPlay = index;
                mPositionPlay = 0;
                RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(mIndexPlay, mPositionPlay, NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo().getContentUUID()));
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
            NewTVLauncherPlayerViewManager.getInstance().setContinuePlay(LauncherApplication
                    .AppContext,mProgramSeriesInfo,defaultConfig,playPostion);
        isContinue = normalExit;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
    }

    NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;
    int playPostion = 0;
    ProgramSeriesInfo mProgramSeriesInfo;


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        updateAndSave();

        mProgramSeriesInfo = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
        playPostion = NewTVLauncherPlayerViewManager.getInstance().getPlayPostion();
        defaultConfig = NewTVLauncherPlayerViewManager.getInstance().getDefaultConfig();
        NewTVLauncherPlayerViewManager.getInstance().releasePlayer();

//        if (isContinue == normalExit) {
//            NewTVLauncherPlayerViewManager.getInstance().release();
//        } else if (isContinue == abNormalExit) {
//            NewTVLauncherPlayerViewManager.getInstance().setAbNormalExitPause();
//        }
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
            RxBus.get().post(Constant.UPDATE_VIDEO_PLAY_INFO, new VideoPlayInfo(mIndexPlay, mPositionPlay, NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo().getContentUUID()));
            addHistory();
        }
    }

    private void addHistory() {
        programSeriesInfo = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
        if (!NewTVLauncherPlayerViewManager.getInstance().isLiving() && mPositionPlay > 0 && programSeriesInfo != null) {
            mPositionPlay = NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition();
            if (NewTVLauncherPlayerViewManager.getInstance().isLive()) {
                return;
            }
            if (mPositionPlay > 0){
                DBUtil.addHistory(programSeriesInfo, mIndexPlay, mPositionPlay, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            if (programSeriesInfo != null && programSeriesInfo.getData() != null
                                    && mIndexPlay < programSeriesInfo.getData().size() && mIndexPlay >= 0) {
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," + programSeriesInfo.getData().get(mIndexPlay).getContentUUID());//添加历史记录
                            }
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    }
                });
            }

        }
    }


}
