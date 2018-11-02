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
import android.view.ViewGroup;
import android.widget.Toast;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.player.videoview.DivergeView;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.BitmapUtil;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.views.detailpage.EpisodeHelper;
import tv.newtv.cboxtv.views.detailpage.HeadPlayerView;
import tv.newtv.cboxtv.views.detailpage.IEpisode;
import tv.newtv.cboxtv.views.detailpage.SmoothScrollView;
import tv.newtv.cboxtv.views.detailpage.SuggestView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.details
 * 创建事件:         10:39
 * 创建人:           weihaichao
 * 创建日期:          2018/8/6
 */
@SuppressWarnings("FieldCanBeLocal")
@BuyGoodsAD
public class SingleDetailPageActivity extends BaseActivity {
    private HeadPlayerView headPlayerView;
    private String leftUUID, rightUUID;
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
        ViewGroup viewGroup = findViewById(R.id.root_view);
        if (viewGroup != null) {
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view instanceof IEpisode) {
                    ((IEpisode) view).destroy();
                }
            }
            if (viewGroup instanceof SmoothScrollView) {
                ((SmoothScrollView) viewGroup).destroy();
            }
        }
        BitmapUtil.recycleImageBitmap(viewGroup);
        headPlayerView = null;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_detail_page);

        if (savedInstanceState == null) {
            contentUUID = getIntent().getStringExtra("content_uuid");
             isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY,false);
        } else {
            contentUUID = savedInstanceState.getString("content_uuid");
        }
        if (!TextUtils.isEmpty(contentUUID) && contentUUID.length() >= 2) {
            leftUUID = contentUUID.substring(0, 2);
            rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
            LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "0," + contentUUID);
        } else {
            Toast.makeText(getApplicationContext(), "节目集信息有误", Toast
                    .LENGTH_SHORT).show();
            SingleDetailPageActivity.this.finish();
            return;
        }

        scrollView = findViewById(R.id.root_view);
        headPlayerView = findViewById(R.id.header_video);
        final SuggestView suggestView = findViewById(R.id.suggest);
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.single_item_head)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView.Builder
                        .DB_TYPE_COLLECT))
                .SetPlayerId(R.id.video_container)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add)
                .SetContentUUID(contentUUID)
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(ProgramSeriesInfo info) {
                        headPlayerView.setProgramSeriesInfo(info);
//                        headPlayerView.Play(0, 0, false);
                        suggestView.setContentUUID(EpisodeHelper.TYPE_SEARCH, contentUUID, "",
                                info.getVideoType(), null);
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }


        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && headPlayerView != null &&
                headPlayerView.hasFocus()) {
            if (event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                return true;
            }
        }

        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent
                .ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return super.dispatchKeyEvent(event);
            }
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if(isADEntry){
                    startActivity(new Intent(SingleDetailPageActivity.this,MainActivity.class));
                    isADEntry = false;
                }
                return super.dispatchKeyEvent(event);
            }
            ViewGroup viewGroup = findViewById(R.id.root_view);
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view != null) {
                    if (!view.hasFocus()) {
                        continue;
                    }
                    if (view instanceof IEpisode && ((IEpisode) view).interuptKeyEvent
                            (event)) {
                        return true;
                    } else {
                        @SuppressWarnings("UnusedAssignment") View toView = null;
                        int pos = index;
                        int dir = 0;
                        boolean condition = false;
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                            dir = -1;
                            condition = true;
                        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                            dir = 1;
                            condition = true;
                        }
                        while (condition) {
                            pos += dir;
                            if (pos < 0 || pos > viewGroup.getChildCount()) break;
                            toView = viewGroup.getChildAt(pos);
                            if (toView != null) {
                                if (toView instanceof IEpisode && ((IEpisode) toView)
                                        .interuptKeyEvent
                                                (event)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
