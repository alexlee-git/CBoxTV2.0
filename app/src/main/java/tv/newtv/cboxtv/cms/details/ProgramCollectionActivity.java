package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.LogUploadUtils;

import java.util.ArrayList;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.uc.v2.listener.INotifyLoginStatusCallback;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeAdView;
import tv.newtv.cboxtv.views.detail.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;
import tv.newtv.cboxtv.views.detail.onEpisodeItemClick;

/**
 * 合集页
 * <p>
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.details
 * 创建事件:         13:44
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 * 节目合集
 */
public class ProgramCollectionActivity extends DetailPageActivity {

    private static final String ACTION = ProgramCollectionActivity.class.getName();
    private HeadPlayerView headPlayerView;
    private SmoothScrollView scrollView;
    private Content mContent;
    private EpisodeHorizontalListView mListView;
    private boolean isLogin = false;
    private EpisodeAdView mAdView;
    private boolean isFullScreenIng;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        headPlayerView = null;
        scrollView = null;
        mListView = null;
    }

    @Override
    public void prepareMediaPlayer() {
        if (headPlayerView != null) {
            headPlayerView.prepareMediaPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (headPlayerView != null) {
            headPlayerView.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (headPlayerView != null) {
            headPlayerView.onActivityStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headPlayerView != null) {
            headPlayerView.onActivityResume();
        }
    }

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    protected boolean interruptDetailPageKeyEvent(KeyEvent event) {
        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && headPlayerView != null &&
                headPlayerView.hasFocus()) {
            if (event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    ||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isFull(KeyEvent event) {
        if (isFullScreenIng&&event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN){
            if (isFullScreen()){
                isFullScreenIng = false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void buildView(@Nullable Bundle savedInstanceState, final String contentUUID) {
        setContentView(R.layout.activity_program_collec_page);
        //进入节目详情页上传日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "0," + contentUUID);

        ADConfig.getInstance().setSeriesID(contentUUID);
        initLoginStatus();


        headPlayerView = findViewById(R.id.header_video);
        mAdView = findViewById(R.id.column_detail_ad_fl);
        scrollView = findViewById(R.id.root_view);
        final SuggestView suggestView = findViewById(R.id.suggest);
        mListView = findViewById(R.id.episode_horizontal_list_view);
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.video_program_collect_layout)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView.Builder
                                .DB_TYPE_COLLECT),
                        new HeadPlayerView.CustomFrame(R.id.vip_pay, HeadPlayerView.Builder
                                .DB_TYPE_VIPPAY),
                        new HeadPlayerView.CustomFrame(R.id.vip_pay_tip, HeadPlayerView.Builder
                                .DB_TYPE_VIPTIP))
                .SetPlayerId(R.id.video_container)
                .SetContentUUID(contentUUID,getChildContentUUID())
                .autoGetSubContents()
                .setTopView(fromOuter,isPopup)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add, R.id.vip_pay)
                .SetClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.full_screen:
                                headPlayerView.EnterFullScreen(ProgramCollectionActivity
                                        .this);
                                break;
                            case R.id.vip_pay:
                                if (mContent != null && mContent.getVipFlag() != null) {
                                    final int vipState = Integer.parseInt(mContent.getVipFlag());
                                    if (isLogin) {
                                        //1 单点包月  3vip  4单点
                                        if (vipState == 1) {
                                            UserCenterUtils.startVIP1(ProgramCollectionActivity
                                                    .this, mContent, ACTION);
                                        } else if (vipState == 3) {
                                            UserCenterUtils.startVIP3(ProgramCollectionActivity
                                                    .this, mContent, ACTION);
                                        } else if (vipState == 4) {
                                            UserCenterUtils.startVIP4(ProgramCollectionActivity
                                                    .this, mContent, ACTION);
                                        }
                                    } else {
                                        UserCenterUtils.startLoginActivity
                                                (ProgramCollectionActivity.this, mContent,
                                                        ACTION, true);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                })
                .SetVideoExitFullScreenCallBack(new VideoExitFullScreenCallBack() {
                    @Override
                    public void videoEitFullScreen() {
                        isFullScreenIng = false;
                    }
                })
                .SetPlayerCallback(new PlayerCallback() {
                    @Override
                    public void onEpisodeChange(int index, int position) {
                        mListView.setCurrentPlay(index);
                    }

                    @Override
                    public void onPlayerClick(VideoPlayerView videoPlayerView) {
                        videoPlayerView.enterFullScreen(ProgramCollectionActivity.this);
                    }

                    @Override
                    public void AllPlayComplete(boolean isError, String info, VideoPlayerView
                            videoPlayerView) {

                    }


                    @Override
                    public void ProgramChange() {

                    }
                })
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(Content info) {
                        if (info == null) return;
                        mContent = info;
                        mListView.setContentUUID(contentUUID);
                        mListView.onSubContentResult(contentUUID, new ArrayList<>(info.getData()));
                        suggestView.setContentUUID(SuggestView.TYPE_COLUMN_SEARCH,
                                info, null);

                        if (mAdView != null) {
                            mAdView.requestAD();
                        }
                    }
                }));
        mListView.setOnItemClick(new onEpisodeItemClick<SubContent>() {
            @Override
            public boolean onItemClick(int position, SubContent data) {
                isFullScreenIng = false;
                headPlayerView.Play(position, 0, true);
                return false;
            }
        });
    }

    //获取登陆状态
    private void initLoginStatus() {
        UserCenterUtils.getLoginStatus(new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                isLogin = status;
            }
        });
    }
}
