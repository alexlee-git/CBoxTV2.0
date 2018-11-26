package tv.newtv.cboxtv.cms.details;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.uc.v2.listener.INotifyLoginStatusCallback;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.custom.DivergeView;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeAdView;
import tv.newtv.cboxtv.views.detail.EpisodeHelper;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.details
 * 创建事件:         10:39
 * 创建人:           weihaichao
 * 创建日期:          2018/8/6
 */
@SuppressWarnings("FieldCanBeLocal")
@BuyGoodsAD
public class SingleDetailPageActivity extends DetailPageActivity {

    private static final String ACTION = SingleDetailPageActivity.class.getName();
    private HeadPlayerView headPlayerView;
    private EpisodeAdView mAdView;
    private SmoothScrollView scrollView;
    private SuggestView suggestView;
    private boolean isLogin = false;
    private Content mProgramSeriesInfo;

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (headPlayerView != null) {
            headPlayerView.onActivityStop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ywy y", "onStop");
        if (headPlayerView != null) {
            headPlayerView.onActivityStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ywy y", "onResume");
        initLoginStatus();
        if (headPlayerView != null) {
            headPlayerView.onActivityResume();
        }
    }

    @Override
    public void prepareMediaPlayer() {
        if (headPlayerView != null) {
            headPlayerView.prepareMediaPlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ywy y", "onDestory");
        headPlayerView = null;
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
    protected void buildView(@Nullable Bundle savedInstanceState, String contentUUID) {
        setContentView(R.layout.activity_single_detail_page);
        Log.d("ywy y", "onCreate");

        mAdView = findViewById(R.id.ad_view);
        scrollView = findViewById(R.id.root_view);
        headPlayerView = findViewById(R.id.header_video);
        suggestView = findViewById(R.id.suggest);

        //进入节目详情页上传日志
        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "1," + contentUUID);

        initHeadPlayerView(contentUUID);
    }

    private void initHeadPlayerView(String contentUUID) {
        if (null != headPlayerView) {
            headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.single_item_head)
                    .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView
                                    .Builder.DB_TYPE_COLLECT),
                            new HeadPlayerView.CustomFrame(R.id.vip_pay, HeadPlayerView.Builder
                                    .DB_TYPE_VIPPAY),
                            new HeadPlayerView.CustomFrame(R.id.vip_pay_tip, HeadPlayerView
                                    .Builder.DB_TYPE_VIPTIP))
                    .SetPlayerId(R.id.video_container)
                    .SetDefaultFocusID(R.id.full_screen)
                    .SetClickableIds(R.id.full_screen, R.id.add, R.id.vip_pay)
                    .SetContentUUID(contentUUID,"")
                    .autoGetSubContents()
                    .setTopView(fromOuter,isPopup)
                    .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                        @Override
                        public void onResult(Content info) {
                            mProgramSeriesInfo = info;
                            suggestView.setContentUUID(EpisodeHelper.TYPE_SEARCH, info, null);
                            mAdView.requestAD();
                        }
                    })
                    .SetPlayerCallback(new PlayerCallback() {
                        @Override
                        public void onEpisodeChange(int index, int position) {
                        }

                        @Override
                        public void ProgramChange() {

                        }

                        public void onPlayerClick(VideoPlayerView videoPlayerView) {
                            videoPlayerView.EnterFullScreen(SingleDetailPageActivity
                                    .this, false);
                        }

                        @Override
                        public void AllPlayComplete(boolean isError, String info, VideoPlayerView
                                videoPlayerView) {
                            if (!isError) {
                                videoPlayerView.onComplete();
                            }
                        }
                    })
                    .SetVideoExitFullScreenCallBack(new VideoExitFullScreenCallBack() {
                        @Override
                        public void videoEitFullScreen() {

                        }
                    }).SetClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.add:
                                    DivergeView mPaiseView = ((DivergeView) headPlayerView
                                            .findViewUseId(R.id
                                                    .view_praise));
                                    mPaiseView.setEndPoint(new PointF(mPaiseView.getMeasuredWidth
                                            () /
                                            2, 0));
                                    mPaiseView.setStartPoint(new PointF(getResources()
                                            .getDimension(R
                                                    .dimen.width_40px),
                                            getResources().getDimension(R.dimen.height_185px)));
                                    mPaiseView.setDivergeViewProvider(new DivergeView
                                            .DivergeViewProvider() {
                                        @Override
                                        public Bitmap getBitmap(Object obj) {
                                            return ((BitmapDrawable) ResourcesCompat.getDrawable
                                                    (getResources(), R.drawable
                                                            .icon_praise, null)).getBitmap();
                                        }
                                    });
                                    mPaiseView.startDiverges(0);
                                    break;
                                case R.id.full_screen:
                                    headPlayerView.EnterFullScreen
                                            (SingleDetailPageActivity.this);
                                    break;
                                case R.id.vip_pay:
                                    if (mProgramSeriesInfo != null && mProgramSeriesInfo
                                            .getVipFlag() != null) {
                                        final int vipState = Integer.parseInt(mProgramSeriesInfo
                                                .getVipFlag());
                                        if (isLogin) {
                                            //1 单点包月  3vip  4单点
                                            if (vipState == 1) {
                                                UserCenterUtils.startVIP1
                                                        (SingleDetailPageActivity.this,
                                                                mProgramSeriesInfo, ACTION);
                                            } else if (vipState == 3) {
                                                UserCenterUtils.startVIP3
                                                        (SingleDetailPageActivity.this,
                                                                mProgramSeriesInfo, ACTION);
                                            } else if (vipState == 4) {
                                                UserCenterUtils.startVIP4
                                                        (SingleDetailPageActivity.this,
                                                                mProgramSeriesInfo, ACTION);
                                            }
                                        } else {
                                            UserCenterUtils.startLoginActivity
                                                    (SingleDetailPageActivity.this,
                                                            mProgramSeriesInfo, ACTION, true);
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }));
        }
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
