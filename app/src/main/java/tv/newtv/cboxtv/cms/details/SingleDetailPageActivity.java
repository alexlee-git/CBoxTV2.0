package tv.newtv.cboxtv.cms.details;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.custom.DivergeView;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
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

    private HeadPlayerView headPlayerView;
    private String contentUUID;
    private SmoothScrollView scrollView;
    private boolean isADEntry = false;

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
    public void prepareMediaPlayer() {
        if (headPlayerView != null) {
            headPlayerView.prepareMediaPlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        headPlayerView = null;
    }


    @Override
    protected boolean interruptDetailPageKeyEvent(KeyEvent event) {
        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && headPlayerView != null &&
                headPlayerView.hasFocus()) {
            if (event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_detail_page);
        final LinearLayout upTop = findViewById(R.id.up_top);
        if (fromOuter) {
            new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    upTop.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    upTop.setVisibility(View.GONE);
                }
            }.start();
        }
        if (savedInstanceState == null) {
            contentUUID = getIntent().getStringExtra("content_uuid");
            isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY, false);
        } else {
            contentUUID = savedInstanceState.getString("content_uuid");
        }

        scrollView = findViewById(R.id.root_view);
        headPlayerView = findViewById(R.id.header_video);
        final SuggestView suggestView = findViewById(R.id.suggest);
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.single_item_head)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView.Builder
                        .DB_TYPE_COLLECT))
                .SetPlayerId(R.id.video_container)
                .autoGetSubContents()
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add)
                .SetContentUUID(contentUUID)
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(Content info) {
                        suggestView.setContentUUID(SuggestView.TYPE_COLUMN_SEARCH, info, null);
                    }
                })
                .SetPlayerCallback(new PlayerCallback() {
                    @Override
                    public void onEpisodeChange(int index, int position) {

                    }

                    @Override
                    public void ProgramChange() {

                    }

                    @Override
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
                })
                .SetClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.add:
                                DivergeView mPaiseView = ((DivergeView) headPlayerView
                                        .findViewUseId(R.id
                                                .view_praise));
                                mPaiseView.setEndPoint(new PointF(mPaiseView.getMeasuredWidth() /
                                        2, 0));
                                mPaiseView.setStartPoint(new PointF(getResources().getDimension(R
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
                        }
                    }
                }));
    }
}
