package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.BitmapUtil;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.views.detailpage.EpisodeHelper;
import tv.newtv.cboxtv.views.detailpage.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detailpage.HeadPlayerView;
import tv.newtv.cboxtv.views.detailpage.IEpisode;
import tv.newtv.cboxtv.views.detailpage.SmoothScrollView;
import tv.newtv.cboxtv.views.detailpage.SuggestView;
import tv.newtv.cboxtv.views.detailpage.onEpisodeItemClick;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.details
 * 创建事件:         13:44
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 * 节目合集
 */
public class ProgramCollectionActivity extends BaseActivity {

    private String contentUUID;
    private HeadPlayerView headPlayerView;
    private SmoothScrollView scrollView;
    private EpisodeHorizontalListView mListView;

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

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_collec_page);

        contentUUID = getIntent().getStringExtra("content_uuid");
        ADConfig.getInstance().setSeriesID(contentUUID);
        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "节目合集信息异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        headPlayerView = findViewById(R.id.header_video);
        scrollView = findViewById(R.id.root_view);
        final SuggestView suggestView = findViewById(R.id.suggest);
        mListView = findViewById(R.id.episode_horizontal_list_view);
        mListView.setTitle("合集节目");
        headPlayerView.Build(
                HeadPlayerView.Builder.build(R.layout.video_program_collect_layout)
                        .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView
                                .Builder.DB_TYPE_COLLECT))
                        .SetPlayerId(R.id.video_container)
                        .SetDefaultFocusID(R.id.full_screen)
                        .SetClickableIds(R.id.full_screen, R.id.add)
                        .SetClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.full_screen:
                                        headPlayerView.EnterFullScreen(ProgramCollectionActivity
                                                .this);
                                        break;
                                }
                            }
                        })
                        .SetPlayerCallback(new PlayerCallback() {
                            @Override
                            public void onEpisodeChange(int index, int position) {
                                mListView.setCurrentPlay(index);
                            }

                            @Override
                            public void onPlayerClick(VideoPlayerView videoPlayerView) {
                                videoPlayerView.enterFullScreen(ProgramCollectionActivity.this,
                                        false);
                            }

                            @Override
                            public void AllPalyComplete(boolean isError, String info,
                                                        VideoPlayerView videoPlayerView) {

                            }

                            @Override
                            public void ProgramChange() {

                            }
                        })
                        .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                            @Override
                            public void onResult(ProgramSeriesInfo info) {
                                headPlayerView.setProgramSeriesInfo(info);
                                mListView.setContentUUID(contentUUID, info);
                                suggestView.setContentUUID(EpisodeHelper.TYPE_SEARCH,
                                        info.getContentUUID(),
                                        info.getChannelId(),
                                        info.getVideoType()
                                        , null);
//                                headPlayerView.Play(0, 0, false);
                            }
                        })
                        .SetContentUUID(contentUUID));


        mListView.setOnItemClick(new onEpisodeItemClick() {
            @Override
            public void onItemClick(int position) {
                headPlayerView.Play(position, 0, true);
            }
        });
    }

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
                        View toView = null;
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
