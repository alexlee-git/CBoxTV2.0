package tv.newtv.cboxtv.cms.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.custom.DivergeView;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeHelper;
import tv.newtv.cboxtv.views.detail.EpisodePageView;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;


/**
 * 栏目详情页
 * <p>
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.details
 * 创建事件:         19:13
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
@BuyGoodsAD
public class ColumnPageActivity extends DetailPageActivity {

    private EpisodePageView playListView;
    private HeadPlayerView headPlayerView;
    private DivergeView mPaiseView;
    private long lastClickTime = 0;
    private SmoothScrollView scrollView;
    private Content pageContent;
    private int currentIndex = -1;

    @Override
    protected void FocusToTop() {
        Toast.makeText(getApplicationContext(), "ColumnPageActivity 到顶了",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void prepareMediaPlayer() {
        super.prepareMediaPlayer();

        if (headPlayerView != null) {
            headPlayerView.prepareMediaPlayer();
        }
    }

    @Override
    public boolean hasPlayer() {
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPaiseView = null;
        playListView = null;

        headPlayerView = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_page);
        playListView = findViewById(R.id.play_list);
        scrollView = findViewById(R.id.root_view);

        String contentUUID = getIntent().getStringExtra("content_uuid");
        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "0," + contentUUID);
        ADConfig.getInstance().setSeriesID(contentUUID);
        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "栏目信息异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final SuggestView sameType = findViewById(R.id.same_type);
        headPlayerView = findViewById(R.id.header_video);
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.video_layout)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.subscibe, HeadPlayerView
                        .Builder.DB_TYPE_SUBSCRIP))
                .autoGetSubContents()
                .SetPlayerId(R.id.video_container)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add)
                .SetContentUUID(getContentUUID())
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(Content info) {
                        if (info != null) {
                            pageContent = info;
                            playListView.setContentUUID(info,EpisodeHelper.TYPE_COLUMN_DETAIL,
                                    info.getVideoType(),
                                    getSupportFragmentManager(),
                                    getContentUUID(), null);
                            if (sameType != null) {
                                sameType.setContentUUID(SuggestView.TYPE_COLUMN_SUGGEST, info,
                                        null);
                            }

                            SuggestView starView = findViewById(R.id.star);
                            starView.setContentUUID(SuggestView.TYPE_COLUMN_FIGURES, info,
                                    null);
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "内容信息错误");
                            ColumnPageActivity.this.finish();
                        }
                    }
                })
                .SetPlayerCallback(new PlayerCallback() {
                    @Override
                    public void onEpisodeChange(int index, int position) {
                        currentIndex = index;
                        if (index >= 0) {
                            playListView.setCurrentPlayIndex(index);
                        }
                    }

                    @Override
                    public void ProgramChange() {

                        if (playListView != null) {
                            playListView.resetProgramInfo();
                        }
                    }

                    @Override
                    public void onPlayerClick(VideoPlayerView videoPlayerView) {
                        if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                            lastClickTime = System.currentTimeMillis();//记录这次点击时间
                            headPlayerView.EnterFullScreen(ColumnPageActivity.this);
                        }
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
                        if (currentIndex > 8) {
                            playListView.moveToPosition(currentIndex);
                        }
                    }
                })
                .SetClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.add:
                                mPaiseView = ((DivergeView) headPlayerView.findViewUseId(R.id
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
                                if (System.currentTimeMillis() - lastClickTime >= 2000)
                                {//判断距离上次点击小于2秒
                                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                    headPlayerView.EnterFullScreen(ColumnPageActivity.this);
                                }

                                break;
                        }
                    }
                }));

        playListView.setOnEpisodeChange(new EpisodePageView.OnEpisodeChange() {
            @Override
            public void onGetProgramSeriesInfo(List<SubContent> seriesInfo) {
//                ArrayList<SubContent> contents = new ArrayList<>(seriesInfo);
//                pageContent.setData(contents);
//                headPlayerView.setProgramSeriesInfo(pageContent);
            }

            @Override
            public void onChange(int index, boolean fromClick) {
                headPlayerView.Play(index, 0, fromClick);
            }
        });


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
}
