package tv.newtv.cboxtv.cms.details;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ToastUtil;
import com.squareup.picasso.Picasso;

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

    private static final String ACTION = ColumnPageActivity.class.getName();
    private EpisodePageView playListView;
    private HeadPlayerView headPlayerView;
    private DivergeView mPaiseView;
    private EpisodeAdView mAdView;
    private long lastClickTime = 0;
    private SmoothScrollView scrollView;
    private Content pageContent;
    private int currentIndex = -1;
    private boolean isFullScreenIng;

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
    protected void buildView(@Nullable Bundle savedInstanceState, final String contentUUID) {
        setContentView(R.layout.activity_column_page);

        playListView = findViewById(R.id.play_list);
        scrollView = findViewById(R.id.root_view);
        mAdView = findViewById(R.id.column_detail_ad_fl);

        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "栏目信息异常", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," + contentUUID);

        ADConfig.getInstance().setSeriesID(contentUUID);

        final SuggestView sameType = findViewById(R.id.same_type);
        headPlayerView = findViewById(R.id.header_video);
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.video_layout)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.subscibe, HeadPlayerView.Builder
                                .DB_TYPE_SUBSCRIP),
                        new HeadPlayerView.CustomFrame(R.id.vip_pay, HeadPlayerView.Builder
                                .DB_TYPE_VIPPAY),
                        new HeadPlayerView.CustomFrame(R.id.vip_pay_tip, HeadPlayerView.Builder
                                .DB_TYPE_VIPTIP))
                .autoGetSubContents()
                .SetPlayerId(R.id.video_container)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add, R.id.vip_pay)
                .SetContentUUID(contentUUID, getChildContentUUID())
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(Content info) {
                        if (info != null) {
                            ArrayList<String> productId = new ArrayList<>();
                            pageContent = info;
                            if (!TextUtils.isEmpty(info.getCategoryIDs())){
                                LogUploadUtils.uploadLog(Constant.LOG_COLUMN_INTO, "1," + info.getCategoryIDs());

                            }
                            if (pageContent != null ) {
                                if (!TextUtils.isEmpty(pageContent.getVipFlag())){
                                    int vipState = Integer.parseInt(pageContent.getVipFlag());
                                    if ((vipState == 1||vipState == 3||vipState == 4)&&!TextUtils.isEmpty(pageContent.getVipProductId())){
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
                            playListView.setContentUUID(info,EpisodeHelper.TYPE_COLUMN_DETAIL,
                                    info.getVideoType(),
                                    getSupportFragmentManager(),
                                    contentUUID, null);
                            if (sameType != null) {
                                sameType.setContentUUID(SuggestView.TYPE_COLUMN_SUGGEST, info,
                                        null);
                            }

                            SuggestView starView = findViewById(R.id.star);
                            starView.setContentUUID(SuggestView.TYPE_COLUMN_FIGURES, info,
                                    null);

                            if (mAdView != null) {
                                mAdView.requestAD();
                            }
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
                        isFullScreenIng = false;
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
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_LIKE, "0," +
                                        pageContent.getContentUUID());
                                break;

                            case R.id.full_screen:
                                if (System.currentTimeMillis() - lastClickTime >= 2000)
                                {//判断距离上次点击小于2秒
                                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                    headPlayerView.EnterFullScreen(ColumnPageActivity.this);
                                }
                                break;
                            case R.id.vip_pay:
                                if (pageContent != null && pageContent.getVipFlag() != null) {
                                    final int vipState = Integer.parseInt(pageContent.getVipFlag());
                                    if (UserStatus.isLogin()) {
                                        //1 单点包月  3vip  4单点
                                        if (vipState == 1) {
                                            UserCenterUtils.startVIP1(ColumnPageActivity.this,
                                                    pageContent, ACTION);
                                        } else if (vipState == 3) {
                                            UserCenterUtils.startVIP3(ColumnPageActivity.this,
                                                    pageContent, ACTION);
                                        } else if (vipState == 4) {
                                            UserCenterUtils.startVIP4(ColumnPageActivity.this,
                                                    pageContent, ACTION);
                                        }
                                    } else {
                                        UserCenterUtils.startLoginActivity(ColumnPageActivity
                                                .this, pageContent, ACTION, true);
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
                headPlayerView.resetSeriesInfo(pageContent);
            }

            @Override
            public void onChange(int index, boolean fromClick) {
                isFullScreenIng = true;
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
        if (isFullScreenIng && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (isFullScreen()) {
                isFullScreenIng = false;
            }
            return true;
        }
        return false;
    }
}
