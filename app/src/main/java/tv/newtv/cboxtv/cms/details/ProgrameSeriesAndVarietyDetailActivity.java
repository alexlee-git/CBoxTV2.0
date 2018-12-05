package tv.newtv.cboxtv.cms.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
import tv.newtv.cboxtv.views.detail.EpisodePageView;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;

/**
 * Created by weihaichao on 2018/10/19.
 */
@BuyGoodsAD
public class ProgrameSeriesAndVarietyDetailActivity extends DetailPageActivity implements
        ContentContract.LoadingView {

    private static final String ACTION = ProgrameSeriesAndVarietyDetailActivity.class.getName();
    Content pageContent;
    private HeadPlayerView headPlayerView;
    private DivergeView mPaiseView;
    private EpisodePageView playListView;
    private SmoothScrollView scrollView;
    private EpisodeAdView mAdView;
    private String videoType;
    private long lastClickTime;
    private FragmentTransaction transaction;
    private ContentContract.Presenter mContentPresenter;
    private int layoutId;
    private boolean isFullScreenIng;

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    public void prepareMediaPlayer() {
        super.prepareMediaPlayer();

        if (headPlayerView != null) {
            headPlayerView.prepareMediaPlayer();
        }
    }

    @Override
    protected void buildView(@Nullable Bundle savedInstanceState, String contentUUID) {

        if (!TextUtils.isEmpty(contentUUID) && contentUUID.length() >= 2) {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "0," + contentUUID);
            LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," + contentUUID);
            //requestData();
            mContentPresenter = new ContentContract.ContentPresenter(getApplicationContext(), this);
            mContentPresenter.getContent(contentUUID, true);
        } else {
            onError(getApplicationContext(), "节目集信息有误");
        }
    }


    private void initView(final Content content, final String contentUUID) {
        playListView = findViewById(R.id.play_list);
        scrollView = findViewById(R.id.root_view);
        mAdView = findViewById(R.id.ad_view);
        final SuggestView suggestView = findViewById(R.id.suggest);

        if (!videoType()) {
            layoutId = R.layout.variety_item_head_programe;
        } else {
            layoutId = R.layout.variety_item_head;
        }

        headPlayerView = ((HeadPlayerView) findViewById(R.id.header_video));
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.variety_item_head)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView.Builder.DB_TYPE_COLLECT),
                new HeadPlayerView.CustomFrame(R.id.vip_pay,HeadPlayerView.Builder.DB_TYPE_VIPPAY),
                        new HeadPlayerView.CustomFrame(R.id.vip_pay_tip,HeadPlayerView.Builder.DB_TYPE_VIPTIP))
                .SetPlayerId(R.id.video_container)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add, R.id.vip_pay)
                .SetContentUUID(contentUUID, getChildContentUUID())
                .autoGetSubContents()
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(Content info) {
                        if (info != null) {
                            ArrayList<String> productId = new ArrayList<>();
                            pageContent = info;
                            if (pageContent != null ) {
                                if (!TextUtils.isEmpty(pageContent.getVipFlag())){
                                    int vipState = Integer.parseInt(pageContent.getVipFlag());
                                    if ((vipState == 1||vipState == 3||vipState == 4)&&pageContent.getVipProductId()!=null){
                                        productId.add(String.format(BootGuide.getBaseUrl(BootGuide.MARK_VIPPRODUCTID),pageContent.getVipProductId()));
                                    }
                                }
                                if (!TextUtils.isEmpty(pageContent.is4k())){
                                    int is4k = Integer.parseInt(pageContent.is4k());
                                    if (is4k == 1){
                                        productId.add(BootGuide.getBaseUrl(BootGuide.MARK_IS4K));
                                    }
                                }
                                if (!TextUtils.isEmpty(pageContent.getNew_realExclusive())){
                                    productId.add(String.format(BootGuide.getBaseUrl(BootGuide.MARK_NEW_REALEXCLUSIVE),pageContent.getNew_realExclusive()));
                                }
                            }

                            switch (productId.size()){
                                case 3:
                                    Picasso.get().load(productId.get(2)).into((ImageView) findViewById(R.id.id_detail_mark3));
                                case 2:
                                    Picasso.get().load(productId.get(1)).into((ImageView) findViewById(R.id.id_detail_mark2));
                                case 1:
                                    Picasso.get().load(productId.get(0)).into((ImageView) findViewById(R.id.id_detail_mark1));
                                default:
                                    break;
                            }
                            suggestView.setContentUUID(SuggestView.TYPE_COLUMN_SEARCH, info, null);
                            playListView.setContentUUID(info, mContentPresenter.isTvSeries(content)
                                            ? EpisodeHelper.TYPE_PROGRAME_SERIES : EpisodeHelper
                                            .TYPE_VARIETY_SHOW,
                                    content.getVideoType(),
                                    getSupportFragmentManager(),
                                    contentUUID, null);

                            if (mAdView != null) {
                                mAdView.requestAD();
                            }
                        } else {
                            ToastUtil.showToast(getApplicationContext(), "内容信息错误");
                            ProgrameSeriesAndVarietyDetailActivity.this.finish();
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
                            if (videoPlayerView != null) {
                                videoPlayerView.EnterFullScreen
                                        (ProgrameSeriesAndVarietyDetailActivity
                                        .this, false);
                            }
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
                .SetClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.add:
                                mPaiseView = ((DivergeView) headPlayerView.findViewUseId(R.id
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
                                if (System.currentTimeMillis() - lastClickTime >= 2000)
                                {//判断距离上次点击小于2秒
                                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                    headPlayerView.EnterFullScreen
                                            (ProgrameSeriesAndVarietyDetailActivity.this);
                                }
                                break;
                            case R.id.vip_pay:
                                if (pageContent != null && pageContent.getVipFlag() != null) {
                                    final int vipState = Integer.parseInt(pageContent.getVipFlag());
                                    if (UserStatus.isLogin()) {
                                        //1 单点包月  3vip  4单点
                                        if (vipState == 1) {
                                            UserCenterUtils.startVIP1
                                                    (ProgrameSeriesAndVarietyDetailActivity.this,
                                                            pageContent, ACTION);
                                        } else if (vipState == 3) {
                                            UserCenterUtils.startVIP3
                                                    (ProgrameSeriesAndVarietyDetailActivity.this,
                                                            pageContent, ACTION);
                                        } else if (vipState == 4) {
                                            UserCenterUtils.startVIP4
                                                    (ProgrameSeriesAndVarietyDetailActivity.this,
                                                            pageContent, ACTION);
                                        }
                                    } else {
                                        UserCenterUtils.startLoginActivity
                                                (ProgrameSeriesAndVarietyDetailActivity.this,
                                                        pageContent, ACTION, true);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }));

        playListView.setOnEpisodeChange(new EpisodePageView.OnEpisodeChange() {
            @Override
            public void onGetProgramSeriesInfo(List<SubContent> seriesInfo) {
                if (seriesInfo != null) {
                    headPlayerView.resetSeriesInfo(pageContent);
                }
            }

            @Override
            public void onChange(int index, boolean fromClick) {
                isFullScreenIng = true;
                headPlayerView.Play(index, 0, fromClick);
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mContentPresenter != null) {
            mContentPresenter.destroy();
            mContentPresenter = null;
        }


        mPaiseView = null;
    }

    @Override
    protected boolean interruptDetailPageKeyEvent(KeyEvent event) {
        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && headPlayerView != null &&
                headPlayerView.isFullScreen()) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER
                    || event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN
                    || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                    || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
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
    protected void onPause() {
        super.onPause();
        if (headPlayerView != null) {
            headPlayerView.onActivityPause();
        }
    }

    @Override
    protected void onStop() {
        if (headPlayerView != null) {
            headPlayerView.onActivityStop();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (headPlayerView != null) {
            headPlayerView.onActivityResume();
        }
    }

    private boolean videoType() {
        if (!TextUtils.isEmpty(videoType) && (TextUtils.equals(videoType, "电视剧")
                || TextUtils.equals(videoType, "动漫"))) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (content != null) {
            videoType = content.getVideoType();
        }
        //这里跳转不同详情页 综艺、电视剧
        setContentView(R.layout.fragment_new_variety_show);
        ADConfig.getInstance().setSeriesID(uuid);
        initView(content, uuid);
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable
            ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @org.jetbrains.annotations.Nullable String desc) {
        Toast.makeText(context.getApplicationContext(), desc, Toast
                .LENGTH_SHORT).show();
        ProgrameSeriesAndVarietyDetailActivity.this.finish();
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void loadComplete() {

    }
}
