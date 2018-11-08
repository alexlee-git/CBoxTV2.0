package tv.newtv.cboxtv.cms.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newtv.cms.BuildConfig;
import com.newtv.cms.bean.Content;
import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.adapter.ColumnDetailsAdapter;
import tv.newtv.cboxtv.cms.details.view.VerticallRecyclerView;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.views.custom.FocusToggleView;
import tv.newtv.cboxtv.views.custom.RecycleImageView;
import tv.newtv.cboxtv.uc.v2.listener.ICollectionStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyLoginStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyMemberStatusCallback;
import tv.newtv.cboxtv.uc.v2.sub.QueryUserStatusUtil;
import tv.newtv.cboxtv.utils.UserCenterUtils;

/**
 * 节目合集详情页
 */
public class ProgramListDetailActiviy extends BaseActivity implements OnRecycleItemClickListener,
        AdContract.View, PlayerCallback, View.OnClickListener {

    private static final String ACTION = "tv.newtv.cboxtv.action.PROGRAMLIST";
    @BindView(R.id.id_usercenter_fragment_root)
    VerticallRecyclerView mRecyclerView;

    @BindView(R.id.tv_detail_title)
    TextView tvtitle;

    @BindView(R.id.tv_detail_content)
    TextView tvContent;

    @BindView(R.id.btn_detail_collect)
    FocusToggleView mCollectBtn;

    @BindView(R.id.full_screen)
    FocusToggleView mFullBtn;

    @BindView(R.id.btn_detail_vip_pay)
    FocusToggleView mVipPayBtn;

    @BindView(R.id.vip_pay_tip)
    TextView vipTip;

    @BindView(R.id.video_container)
    FrameLayout video_container;

    @BindView(R.id.program_list_detail_ad_fl)
    FrameLayout program_detail_ad_fl;

    @BindView(R.id.program_list_detail_ad_img)
    RecycleImageView program_detail_ad_img;

    private String contentUUID;

    private VideoPlayerView mVideoPlayer;

    private String leftUUID;
    private String rightUUID;
    private ColumnDetailsAdapter mAdapter;
    private List<Content> dataList;
    private boolean isCollect = false;
    private Content dataInfo;
    private boolean isLogin = false;
    private String memberStatus;
    private String expireTime;
    //private ProgramSeriesInfo dataInfo;
    private Disposable mDisposable;
    private AdContract.Presenter adPresenter;

    private NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_list_detail_activiy);
        ButterKnife.bind(this);
        adPresenter = new AdContract.AdPresenter(getApplicationContext(), this);
        init();
        initView();
        requestData();
    }

    @Override
    public void prepareMediaPlayer() {
        super.prepareMediaPlayer();

        if (mVideoPlayer != null) {
            ViewGroup parent = (ViewGroup) mVideoPlayer.getParent();
            if (parent != null) {
                if (mVideoPlayer.isReleased()) {
                    parent.removeView(mVideoPlayer);
                    mVideoPlayer = null;
                }
            } else {
                return;
            }
        }

        if (defaultConfig == null) {
            mVideoPlayer = new VideoPlayerView(getApplicationContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            mVideoPlayer.setLayoutParams(layoutParams);
            mVideoPlayer.setPlayerCallback(this);
            video_container.addView(mVideoPlayer, layoutParams);
        } else {
            mVideoPlayer = new VideoPlayerView(getApplicationContext());
            if (defaultConfig.defaultFocusView instanceof VideoPlayerView) {
                mVideoPlayer.requestFocus();
            }
        }

    }

    @Override
    public boolean hasPlayer() {
        return true;
    }

    private void initView() {
        dataList = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);

        mCollectBtn.setOnClickListener(this);
        mFullBtn.setOnClickListener(this);
        mVipPayBtn.setOnClickListener(this);

        prepareMediaPlayer();
    }

    private void init() {
        contentUUID = getIntent().getStringExtra("content_uuid");
        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "合集信息有误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ADConfig.getInstance().setSeriesID(contentUUID);
        adPresenter.getAdByChannel(Constant.AD_DESK, Constant.AD_DETAILPAGE_BANNER, Constant
                .AD_DETAILPAGE_BANNER, PlayerConfig.getInstance().getFirstChannelId(), PlayerConfig
                .getInstance().getSecondChannelId(), PlayerConfig.getInstance().getTopicId(),null);
        //获取广告
        if (contentUUID.length() >= 2) {
            leftUUID = contentUUID.substring(0, 2);
            rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        } else {
            Toast.makeText(this, "合集信息有误", Toast.LENGTH_SHORT).show();
            finish();
        }
        mAdapter = new ColumnDetailsAdapter(getApplicationContext(), this);
        dataList = new ArrayList<>();

    }

    //获取登陆状态
    private void initLoginCollectStatus() {
        UserCenterUtils.getCollectState(contentUUID, new ICollectionStatusCallback() {
            @Override
            public void notifyCollectionStatus(boolean status) {
                isCollect = status;
            }
        });

        UserCenterUtils.getLoginStatus(new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                isLogin = status;
            }
        });
        UserCenterUtils.getMemberStatus(new INotifyMemberStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(String status, Bundle memberBundle) {
                memberStatus = status;
                if (memberBundle != null) {
                    expireTime = (String) memberBundle.get(QueryUserStatusUtil.expireTime);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLoginCollectStatus();
    }

    @Override
    protected void onDestroy() {
        unSubscribe();
        super.onDestroy();
        if(adPresenter != null){
            adPresenter.destroy();
            adPresenter = null;
        }
    }

    private void requestData() {
        dataList.clear();
        NetClient.INSTANCE.getDetailsPageApi().getInfo(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID,
                leftUUID, rightUUID, contentUUID)
                .subscribeOn(Schedulers.io())
                .compose(this.<ResponseBody>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, ObservableSource<List<Content>>>() {
                    @Override
                    public ObservableSource<List<Content>> apply(ResponseBody value)
                            throws Exception {
                        String data = value.string();
                        JSONObject object = new JSONObject(data);
                        if (object.getInt("errorCode") == 0) {
                            JSONObject obj = object.getJSONObject("data");
                            Gson gson = new Gson();
                            dataInfo = gson.fromJson(obj.toString(), Content.class);
                            setHeadData(dataInfo);
//                            basicLoad(dataInfo.gethImage(), ivPage);
                            if (dataInfo.getData() != null && dataInfo.getData().size() > 0) {
                                Content lis = new Content();
//                                lis.layoutId = 3;
//                                lis.layoutTitle = "合集节目";
                                lis.setData(dataInfo.getData());
                                dataList.add(lis);
                            }
                        }
                        return Observable.just(dataList);

                    }

                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Content>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(List<Content> columnPageBean) {
                        mAdapter.appendToList(columnPageBean);
                        mAdapter.notifyDataSetChanged();

                        prepareMediaPlayer();
                        if (mVideoPlayer != null) {
                            mVideoPlayer.setSeriesInfo(columnPageBean.get(0));
                            mVideoPlayer.playSingleOrSeries(0, 0);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void addData(String data, int layoutId, String title) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.getInt("errorCode") == 0) {
                JSONObject obj = object.getJSONObject("data");
                Gson gson = new Gson();
                Content info = gson.fromJson(obj.toString(), Content.class);
//                info.layoutId = layoutId;
//                info.layoutTitle = title;
                dataList.add(info);
            }
        } catch (JSONException e) {

        }


    }

    private void basicLoad(final String url, ImageView posterIv) {
        if (!TextUtils.isEmpty(url)) {
            posterIv.setScaleType(ImageView.ScaleType.FIT_XY);
            posterIv.setVisibility(View.VISIBLE);
            RequestCreator picasso = Picasso.get()
                    .load(url)
                    .priority(Picasso.Priority.HIGH)
                    .stableKey(url)
                    .config(Bitmap.Config.RGB_565);
            picasso = picasso.placeholder(R.drawable.focus_528_296).error(R.drawable.focus_528_296);
            picasso.into(posterIv);

        } else {
            posterIv.setScaleType(ImageView.ScaleType.FIT_XY);
            posterIv.setVisibility(View.VISIBLE);
            RequestCreator picasso = Picasso.get()
                    .load(R.drawable.focus_528_296)
                    .priority(Picasso.Priority.HIGH)
                    .config(Bitmap.Config.RGB_565);
            picasso = picasso.placeholder(R.drawable.focus_528_296).error(R.drawable.focus_528_296);
            picasso.into(posterIv);
        }
    }

    private void setHeadData(Content dataInfo) {
        mCollectBtn.setSelect(isCollect);
        tvContent.setText(dataInfo.getDescription());
        tvtitle.setText(dataInfo.getTitle());
        if (dataInfo != null && dataInfo.getVipFlag() != null && Integer.parseInt(dataInfo.getVipFlag()) > 0) {
            mVipPayBtn.setVisibility(View.VISIBLE);
            mVipPayBtn.setSelect(true);
        }


        if (!TextUtils.isEmpty(memberStatus) && (memberStatus == QueryUserStatusUtil.SIGN_MEMBER_OPEN_GOOD)) {
           /* SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");//设置日期格式
            //System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            String data = df.format(new Date());*/
            vipTip.setVisibility(View.VISIBLE);
            vipTip.setText(String.format(vipTip.getText().toString(), expireTime));
        }
    }

    @Override
    public void onItemClick(View view, int Position, Object object) {

    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, Object object) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.full_screen:
                if (mVideoPlayer != null && mVideoPlayer.isReady()) {
                    mVideoPlayer.EnterFullScreen(ProgramListDetailActiviy.this, false);
                }
                break;
            case R.id.btn_detail_collect:
                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                    lastClickTime = System.currentTimeMillis();
                    if (isCollect) {
                        delCollect(contentUUID);
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "1," + contentUUID);
                        //取消收藏
                    } else {
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "0," + contentUUID);//收藏
                        updateCollect(dataInfo);

                    }
                }
                break;
            case R.id.btn_detail_vip_pay:
                if (dataInfo != null && dataInfo.getVipFlag() != null) {
                    final int vipState = Integer.parseInt(dataInfo.getVipFlag());
                    if (isLogin) {
                        //1 单点包月  3vip  4单点
                        if (vipState == 1) {
                            UserCenterUtils.startVIP1(ProgramListDetailActiviy.this, dataInfo, ACTION);
                        } else if (vipState == 3) {
                            UserCenterUtils.startVIP3(ProgramListDetailActiviy.this, dataInfo, ACTION);
                        } else if (vipState == 4) {
                            UserCenterUtils.startVIP4(ProgramListDetailActiviy.this, dataInfo, ACTION);
                        }
                    } else {
                        UserCenterUtils.startLoginActivity(ProgramListDetailActiviy.this, dataInfo, ACTION, true);
                    }
                }
                break;
            default:
                break;

        }
    }


    private void updateCollect(final Content entity) {
        UserCenterUtils.addCollect(dataInfo, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            isCollect = true;
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "0," + entity
                                    .getContentUUID());//收藏
                            //mCollectIv.setImageResource(R.drawable.icon_details_collect_btn);
                            mCollectBtn.setSelect(true);
                            Toast.makeText(getApplicationContext(), R.string.collect_success,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });
    }

    private void delCollect(final String contentUuId) {
        UserCenterUtils.deleteSomeCollect(dataInfo, contentUuId, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            isCollect = false;
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "1," +
                                    contentUuId);//取消收藏
                            //mCollectIv.setImageResource(R.drawable.icon_details_uncollect_btn);
                            mCollectBtn.setSelect(false);
                            Toast.makeText(getApplicationContext(), "取消收藏成功", Toast.LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });
    }

    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent
                .ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onEpisodeChange(int index, int position) {
        Toast.makeText(this, "播放下一集：" + index, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        mVideoPlayer.enterFullScreen(this);
    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {

    }

    @Override
    public void ProgramChange() {

    }

    @Override
    public void showAd(@Nullable String type, @Nullable String url, @Nullable HashMap<?, ?> hashMap) {
        if (!TextUtils.isEmpty(url)) {
            if (program_detail_ad_fl != null) {
                program_detail_ad_fl.setVisibility(View.VISIBLE);
            }
            if (program_detail_ad_img != null) {
                program_detail_ad_img.hasCorner(true).load(url);
            }
        }
    }

    @Override
    public void updateTime(int total, int left) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }
}
