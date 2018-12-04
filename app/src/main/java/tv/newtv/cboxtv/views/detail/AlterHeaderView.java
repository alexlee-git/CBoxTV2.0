package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Alternate;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.ActivityStacks;
import tv.newtv.cboxtv.MultipleClickListener;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.AlternateCallback;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.uc.v2.listener.ICollectionStatusCallback;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.custom.FocusToggleSelect;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         14:21
 * 创建人:           weihaichao
 * 创建日期:          2018/11/13
 */
public class AlterHeaderView extends FrameLayout implements IEpisode, ContentContract.View, View
        .OnClickListener, AlternateCallback, PlayerCallback {

    private Content mContent;
    private String mContentUUID;
    private ContentContract.Presenter mPresenter;

    private ViewGroup viewContainer;
    private TextView alternateIdText;
    private TextView alternateFromText;
    private TextView alternateDescText;
    private VideoPlayerView alternateView;

    private View fullScreenBtn;
    private View payBtn;

    private NewTVLauncherPlayerView.PlayerViewConfig playerViewConfig;
    private AlternateCallback mAlternateCallback;

    public AlterHeaderView(Context context) {
        this(context, null);
    }

    public AlterHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlterHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void stop() {
        if (alternateView != null) {
            if (!alternateView.isReleased()) {
                playerViewConfig = alternateView.getDefaultConfig();
            }
            viewContainer.removeView(alternateView);
            alternateView.release();
            alternateView.destory();
            alternateView = null;
        }
    }

    public void onResume() {
        if (!TextUtils.isEmpty(mContentUUID)) {
            prepareMediaPlayer();
            setContentUUID(mContentUUID);
        }
    }

    public boolean isFullScreen() {
        if (alternateView != null) {
            alternateView.isFullScreen();
        }
        return false;
    }

    public void setCallback(AlternateCallback callback) {
        mAlternateCallback = callback;
    }

    public void prepareMediaPlayer() {
        if (alternateView != null && alternateView.isReleased()) {
            stop();
        }

        if (alternateView == null) {
            if (playerViewConfig != null) {
                alternateView = new VideoPlayerView(playerViewConfig, getContext());
            } else {
                alternateView = new VideoPlayerView(getContext());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams
                        .MATCH_PARENT, LayoutParams.MATCH_PARENT);
                alternateView.setLayoutParams(layoutParams);
                viewContainer.addView(alternateView, layoutParams);
            }

            alternateView.setPlayerCallback(this);
        }
    }

    public void play(String contentId) {

    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.alternate_head_layout, this, true);

        viewContainer = findViewById(R.id.video_container);
        alternateIdText = findViewById(R.id.id_detail_title);
        alternateFromText = findViewById(R.id.id_detail_from);
        alternateDescText = findViewById(R.id.id_detail_desc);
        alternateView = findViewById(R.id.video_player);

        final View collect = findViewById(R.id.collect);
        if (collect != null) {
            collect.setOnClickListener(new MultipleClickListener() {
                @Override
                public void onMultipleClick(View view) {
                    if (collect instanceof FocusToggleSelect) {

                    } else {

                    }


                }
            });
        }
        alternateView.setPlayerCallback(this);
        alternateView.setAlternateCallback(this);

        fullScreenBtn = findViewById(R.id.full_screen);
        payBtn = findViewById(R.id.vip_pay);

        fullScreenBtn.setOnClickListener(this);
        payBtn.setOnClickListener(this);


        mPresenter = new ContentContract.ContentPresenter(getContext(), this);
    }

    @Override
    public String getContentUUID() {
        return mContentUUID;
    }

    public void setContentUUID(String contentUUID) {
        mContentUUID = contentUUID;

        if (mPresenter == null) {
            mPresenter = new ContentContract.ContentPresenter(getContext(), this);
        }
        mPresenter.getContent(contentUUID, false);
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (!hasFocus() && alternateView != null) {
                    alternateView.requestFocus();
                    return true;
                } else return hasFocus();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                return alternateView != null && alternateView.hasFocus();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return payBtn.hasFocus();
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if (alternateView != null) {
            alternateView.destroy();
        }
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (content == null) {
            return;
        }

        mContent = content;

        if (alternateIdText != null) {
            alternateIdText.setText(String.format("%s %s", content.getAlternateNumber(), content
                    .getTitle()));
        }
        if (alternateFromText != null) {

        }

        if (alternateDescText != null) {
            alternateDescText.setText(content.getDescription());
        }

        if (alternateView != null) {
            alternateView.setSeriesInfo(content);
            alternateView.setAlternateCallback(this);
            alternateView.playAlternate(mContentUUID, content.getTitle(), content
                    .getAlternateNumber());
        }
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.full_screen:
                alternateView.enterFullScreen(ActivityStacks.get().getCurrentActivity());
                break;
            case R.id.collect:

                break;
            case R.id.vip_pay:

                break;
        }
    }

    @Override
    public void onAlternateResult(@Nullable List<Alternate> result) {
        if (mAlternateCallback != null) {
            mAlternateCallback.onAlternateResult(result);
        }
    }

    @Override
    public void onPlayIndexChange(int index) {
        if (mAlternateCallback != null) {
            mAlternateCallback.onPlayIndexChange(index);
        }
    }

    @Override
    public void onEpisodeChange(int index, int position) {

    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoPlayerView.enterFullScreen(ActivityStacks.get().getCurrentActivity());
    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {

    }

    @Override
    public void ProgramChange() {
        if (TextUtils.isEmpty(mContentUUID)) return;
        if (mContent != null) {
            alternateView.playAlternate(mContentUUID, mContent.getTitle(), mContent
                    .getAlternateNumber());
        } else {
            setContentUUID(mContentUUID);
        }
    }
}
