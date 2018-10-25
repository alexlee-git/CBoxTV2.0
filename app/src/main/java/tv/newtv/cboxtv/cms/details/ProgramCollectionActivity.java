package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.ad.ADConfig;

import java.util.ArrayList;

import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;
import tv.newtv.cboxtv.views.detail.onEpisodeItemClick;

/**
 * 合集页
 *
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.details
 * 创建事件:         13:44
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 * 节目合集
 */
public class ProgramCollectionActivity extends DetailPageActivity {

    private HeadPlayerView headPlayerView;
    private SmoothScrollView scrollView;
    private Content mContent;
    private EpisodeHorizontalListView mListView;

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
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_collec_page);

        ADConfig.getInstance().setSeriesID(getContentUUID());
        if (TextUtils.isEmpty(getContentUUID())) {
            Toast.makeText(this, "节目合集信息异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        headPlayerView = findViewById(R.id.header_video);
        scrollView = findViewById(R.id.root_view);
        final SuggestView suggestView = findViewById(R.id.suggest);
        mListView = findViewById(R.id.episode_horizontal_list_view);
        headPlayerView.Build(
                HeadPlayerView.Builder.build(R.layout.video_program_collect_layout)
                        .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView
                                .Builder.DB_TYPE_COLLECT))
                        .SetPlayerId(R.id.video_container)
                        .autoGetSubContents()
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
                            public void AllPlayComplete(boolean isError, String info,
                                                        VideoPlayerView videoPlayerView) {

                            }

                            @Override
                            public void ProgramChange() {

                            }
                        })
                        .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                            @Override
                            public void onResult(Content info) {
                                if(info == null) return;
                                mContent = info;
                                mListView.setContentUUID(getContentUUID());
                                mListView.onSubContentResult(new ArrayList<>(info.getData()));
                                suggestView.setContentUUID(SuggestView.TYPE_COLUMN_SEARCH,
                                        info,null);
                            }
                        })
                        .SetContentUUID(getContentUUID()));


        mListView.setOnItemClick(new onEpisodeItemClick() {
            @Override
            public void onItemClick(int position, SubContent data) {
                headPlayerView.Play(position, 0, true);
            }
        });
    }
}
