package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Alternate;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.AlternateContract;
import com.newtv.cms.contract.ContentContract;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SPrefUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.ActivityStacks;
import tv.newtv.cboxtv.Navigation;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.listener.ScreenListener;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.VideoFrameLayout;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         18:24
 * 创建人:           weihaichao
 * 创建日期:          2018/11/12
 * <p>
 * 1. 通过页面ID获取对应的轮播列表
 * 2. 解析轮播列表，通过查找算法，找到当前时间正在播放的节目的index索引，记录该值
 * 3. 列表中通过index获取对应的Alternate内容，使用内容接口根据Alternate的contentID获取该节目对应的播放列表并根据ALternate的contentUUID
 * 过滤播放内容，你内容中的播放列表仅包含对应contentUUID的一条播放内容。
 * 4.
 */
public class AlternateView extends VideoFrameLayout implements ContentContract.View, Navigation
        .NavigationChange, NewTVLauncherPlayerView.OnPlayerStateChange,
        PlayerCallback, AlternateContract.LoadingView, ICustomPlayer {
    private static final String ALTERNATE_TIP_TAG = "alternate_tip";
    private ContentContract.Presenter mPresenter;                   //内容接口
    private AlternateContract.Presenter mAlternatePresenter;        //轮播接口
    private String playContentId;           //正在播放节目的ID
    private String playContentUUID;         //正在播放的节目的UUID
    private boolean isRequesting = false;
    private int currentPlayIndex = 0;       //正在播放的节目索引值
    private String mContentUUID;            //当前页面的contentID
    private VideoPlayerView playerView;     //播放器
    private List<Alternate> mAlternates;    //当前轮播播放列表
    private ViewGroup videoContainer;
    private TextView channelText, titleText;
    private String mChannel, mTitle;
    private String mPageUUID;
    private Disposable mDisposable;
    private Long currentStartTime = 0L;
    private Alternate currentAlternate;
    private NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;     //播放器销毁前的默认配置
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
            Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss",
            Locale.getDefault());
    private AlternateCallback mCallback;
    private ScreenListener mListener;
    private boolean hasTipAlternate = false;
    private ScreenListener mScreenListener = new ScreenListener() {
        @Override
        public void enterFullScreen() {
            if (mListener != null) {
                mListener.enterFullScreen();
            }
        }

        @Override
        public void exitFullScreen() {
            if (mListener != null) {
                mListener.exitFullScreen();
            }
        }
    };
    private boolean isFullScreen;

    public AlternateView(Context context) {
        this(context, null);
    }

    public AlternateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlternateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        refreshTipStatus();

        //FIXBUG 为了测试轮播提示，每次创建ALternateView的时候，重置本地提示标识
        SPrefUtils.setValue(getContext(), ALTERNATE_TIP_TAG, true);

        LayoutInflater.from(getContext()).inflate(R.layout.alternate_player_layout, this, true);

        videoContainer = findViewById(R.id.video_container);
        channelText = findViewById(R.id.alter_channel);
        titleText = findViewById(R.id.alter_title);
    }

    private void refreshTipStatus() {
        hasTipAlternate = (boolean) SPrefUtils.getValue(getContext(), ALTERNATE_TIP_TAG, false);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        if (!Navigation.get().isCurrentPage(mPageUUID)) return;
        if (visibility == GONE) {
            stop();
        } else {
            if (mAlternates != null) {
                handleAlternates();
            } else {
                setContentUUID(mContentUUID, false, mChannel, mTitle);
            }
        }
    }

    public void setPageUUID(String uuid) {
        mPageUUID = uuid;
    }

    public void onClick() {
        onPlayerClick(playerView);
    }

    public void setCallback(AlternateCallback callback) {
        mCallback = callback;
    }

    private void setContentUUID(String uuid, boolean compare, String channel, String title) {
        if (compare && TextUtils.equals(uuid, mContentUUID)) {
            onError(getContext(), "正在播放当前节目");
            return;
        }
        if (isRequesting) return;
        mContentUUID = uuid;
        mChannel = channel;
        mTitle = title;

        stop();
        requestAlternates();
    }

    /**
     * 设置轮播ID
     *
     * @param uuid    轮播ID
     * @param channel 轮播频道ID
     * @param title   轮播频道名称
     */
    public void setContentUUID(String uuid, String channel, String title) {
        setContentUUID(uuid, true, channel, title);
    }

    /* 设置轮播ID */
    public void setContentUUID(String uuid) {
        setContentUUID(uuid, true, "", "");
    }

    /* 请求轮播数据 */
    private void requestAlternates() {
        if (TextUtils.isEmpty(mContentUUID)) {
            onError(getContext(), "ContentUUID为空");
            return;
        }
        if (mAlternatePresenter == null) {
            mAlternatePresenter = new AlternateContract.AlternatePresenter(getContext(), this);
        }

        prepareMediaPlayer();
        isRequesting = true;
        mAlternatePresenter.getTodayAlternate(mContentUUID);
    }

    public void onResume() {
        if (playerView != null && defaultConfig != null) {
            if (mAlternates != null) {
                handleAlternates();
            } else {
                requestAlternates();
            }
        }
    }

    /* 停止播放 */
    public void stop() {
        if (playerView != null) {
            defaultConfig = playerView.getDefaultConfig();

            playerView.setTipText("");

            playContentId = "";
            playContentUUID = "";

            titleText.setText("");
            channelText.setText("");

            playerView.release();
            playerView.destory();
            playerView = null;
        }


        if (mAlternatePresenter != null) {
            mAlternatePresenter.stop();
        }

        if (mPresenter != null) {
            mPresenter.stop();
        }
        isRequesting = false;

        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mDisposable = null;
        }

    }

    @Override
    public void destroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }

        if (mAlternatePresenter != null) {
            mAlternatePresenter.destroy();
            mAlternatePresenter = null;
        }

        mCallback = null;
    }

    /* 初始化计时 */
    private void initialize() {

        if (!isInEditMode()) {
            if (currentAlternate != null) {
                titleText.setText("即将播放");
            }
            if (mDisposable == null) {
                mDisposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                if (playerView.isPlaying()) {
                                    if (mAlternates != null && currentAlternate != null &&
                                            playerView != null) {
                                        if (!TextUtils.isEmpty(mChannel) && !TextUtils.isEmpty
                                                (mTitle)) {
                                            channelText.setText(String.format("%s %s", mChannel,
                                                    mTitle));
                                        } else {
                                            String value = "";
                                            if (TextUtils.isEmpty(mChannel)) {
                                                value = mTitle;
                                            } else if (TextUtils.isEmpty(mTitle)) {
                                                value = mChannel;
                                            }
                                            if (!TextUtils.isEmpty(value)) {
                                                channelText.setText(value);
                                            }
                                        }
                                        titleText.setText(currentAlternate.getTitle());
                                    }
                                } else {
                                    titleText.setText("即将播放");
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("Alternate", "interval exception = " + throwable
                                        .getMessage());
                                initialize();
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.e("Alternate", "interval complete");
                            }
                        });
            }
        }
    }

    /* 准备播放器 */
    public void prepareMediaPlayer() {
        if (playerView != null && playerView.isReleased()) {
            ViewGroup parent = (ViewGroup) playerView.getParent();
            if (parent != null) {
                parent.removeView(playerView);
            }
            playerView = null;
        }

        if (playerView == null) {
            //全屏显示
            if (defaultConfig == null) {
                playerView = new VideoPlayerView(getContext());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
                        .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                playerView.setLayoutParams(layoutParams);
                videoContainer.addView(playerView, layoutParams);
            } else {
                playerView = new VideoPlayerView(defaultConfig, getContext());
                if (defaultConfig.defaultFocusView instanceof VideoPlayerView) {
                    playerView.requestFocus();
                }
            }
            if (playerView != null) {
                playerView.setAlternatePlay();
                playerView.outerControl();
                playerView.setOnPlayerStateChange(this);
                playerView.registerScreenListener(mScreenListener);
                playerView.setPlayerCallback(this);
            }
        }
    }

    /**
     * 播放节目
     *
     * @param contentId
     * @param contentUUID
     */
    public void play(String contentId, String contentUUID) {
        if (TextUtils.equals(contentId, playContentId) && TextUtils.equals(playContentUUID,
                contentUUID)) {
            return;
        }
        playContentId = contentId;
        playContentUUID = contentUUID;
        if (mPresenter == null) {
            mPresenter = new ContentContract.ContentPresenter(getContext(), this);
        }

        mPresenter.getContent(contentId, true);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {
        if (!isInEditMode()) {
            Toast.makeText(context, desc, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (TextUtils.equals(playContentId, uuid)) {
            if (content != null) {
                if(content.getData() != null) {
                    ArrayList<SubContent> subContents = new ArrayList<>();
                    for (SubContent sub : content.getData()) {
                        if (TextUtils.equals(sub.getContentUUID(), playContentUUID)) {
                            subContents.add(sub);
                            break;
                        }
                    }
                    content.setData(subContents);
                }
                prepareMediaPlayer();
                playerView.setSeriesInfo(content);

                currentStartTime = CmsUtil.parse(currentAlternate.getStartTime());
                initialize();
                playerView.playSingleOrSeries(0,
                        (int) (System.currentTimeMillis() - currentStartTime),mContentUUID);
            }
        }
    }

    /* 准备播放 */
    private void preparePlay() {
        currentAlternate = mAlternates.get(currentPlayIndex);
        LogUtils.e("Alternate", "play index=" + currentPlayIndex + " start=" + currentAlternate
                .getStartTime());
        play(currentAlternate.getContentID(), currentAlternate.getContentUUID());
        if (mCallback != null) {
            mCallback.onPlayIndexChange(currentPlayIndex);
        }
    }

    @Override
    public void onEpisodeChange(int index, int position) {

    }

    /* 解析轮播内容 */
    private void handleAlternates() {
        titleText.setText("正在解析数据...");

        if (mCallback != null) {
            mCallback.onAlternateResult(mAlternates);
        }
        if (mAlternates != null && mAlternates.size() > 0) {
            currentPlayIndex = CmsUtil.binarySearch(mAlternates, System.currentTimeMillis(), 0,
                    mAlternates.size() - 1);
            if (currentPlayIndex >= 0) {
                preparePlay();
                return;
            }
        }

        titleText.setText("暂无播放");
    }

    public void enterFullScreen() {
        playerView.enterFullScreen(ActivityStacks.get().getCurrentActivity());
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        playerView.enterFullScreen(ActivityStacks.get().getCurrentActivity());
    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
        Toast.makeText(getContext().getApplicationContext(), "播放完了", Toast.LENGTH_SHORT).show();

        if (currentPlayIndex < mAlternates.size()) {
            currentPlayIndex++;
            preparePlay();
        }
    }

    @Override
    public void ProgramChange() {

    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void onAlternateResult(@Nullable List<Alternate> alternates) {
        mAlternates = alternates;
        isRequesting = false;
        handleAlternates();
    }

    @Override
    public void onLoading() {
        titleText.setText("正在拉取轮播数据...");
    }

    @Override
    public void loadComplete() {
        if (playerView != null) {
            playerView.setTipText("");
        }
    }

    @Override
    public void onWindowVisibleChange(int visible) {
        LogUtils.d("AlternateView", mPageUUID + " visible change = " + visible);

    }

    @Override
    public void attachScreenListener(ScreenListener listener) {
        mListener = listener;
    }

    @Override
    public void detachScreenListener(ScreenListener listener) {
        if (mListener == listener) {
            mListener = null;
        }
    }

    /**
     * 当首页界面二级导航页面切换的时候
     *
     * @param uuid 切换至页面的ID
     */
    @Override
    public void onChange(String uuid) {
        if (Navigation.get().isCurrentPage(mPageUUID)) {
            onWindowVisibleChange(VISIBLE);
        } else {
            onWindowVisibleChange(GONE);
        }
    }

    @Override
    public boolean onStateChange(boolean fullScreen, int visible, boolean videoPlaying) {
        isFullScreen = fullScreen;
        LogUtils.d("AlternateView", "onStateChange = " + fullScreen);
        LogUtils.d("AlternateView", "visible = " + visible);
        LogUtils.d("videoPlaying", "videoPlaying = " + videoPlaying);
        if (isFullScreen && playerView != null && visible == 0 && hasTipAlternate && videoPlaying) {
            AlternateTipView tipView = new AlternateTipView(getContext());
            playerView.tip(tipView, 1);
            hasTipAlternate = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean processKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER || keyEvent.getKeyCode() ==
                    KeyEvent.KEYCODE_DPAD_CENTER) {
                playerView.dismissTipView();
            }
        }
        switch (keyEvent.getKeyCode()){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MENU:
                return true;
            default:
                break;

        }
        return false;
    }

    public interface AlternateCallback {

        void onAlternateResult(@Nullable List<Alternate> result);

        void onPlayIndexChange(int index);
    }
}
