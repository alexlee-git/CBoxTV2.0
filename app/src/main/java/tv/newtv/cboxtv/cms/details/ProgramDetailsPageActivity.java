package tv.newtv.cboxtv.cms.details;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.details.adapter.ColumnDetailsAdapter;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.details.model.VideoPlayInfo;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.details.view.VerticallRecyclerView;
import tv.newtv.cboxtv.cms.listPage.model.ScreenInfo;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.cms.util.Utils;
import tv.newtv.cboxtv.player.videoview.DivergeView;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.db.DBCallback;
import tv.newtv.cboxtv.uc.db.DBConfig;
import tv.newtv.cboxtv.uc.db.DataSupport;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.utils.ADHelper;
import tv.newtv.cboxtv.views.RecycleImageView;

//import tv.newtv.cboxtv.cms.net.ApiUtil;

/**
 * Created by gaoleichao on 2018/3/30.
 */

/**
 * 节目详情页
 */
public class ProgramDetailsPageActivity extends BaseActivity implements OnRecycleItemClickListener, View.OnKeyListener, IAdConstract.IADConstractView {

    @BindView(R.id.id_usercenter_fragment_root)
    VerticallRecyclerView mRecyclerView;


    @BindView(R.id.iv_detail_video)
    VideoPlayerView mVideoView;


    @BindView(R.id.detail_rel_image_collect)
    ImageView mCollectIv;
    @BindView(R.id.iv_detail_image_focus)
    ImageView mFocusIv;
    @BindView(R.id.tv_detail_title)
    TextView detailTitleTv;
    @BindView(R.id.detail_tv_type)
    TextView detailTypeTv;
    @BindView(R.id.detail_tv_content)
    TextView detailContentTv;
    @BindView(R.id.detail_tv_star)
    TextView detailStarTv;
    @BindView(R.id.detail_rel_image_big_screen)
    ImageView mBigScreenIv;
    @BindView(R.id.detail_rel_image_praise)
    ImageView mPraiseIv;
    @BindView(R.id.btn_detail_big_screen)
    RelativeLayout mBigScreenBtn;
    @BindView(R.id.btn_detail_collect)
    RelativeLayout mCollectBtn;
    @BindView(R.id.btn_detail_praise)
    RelativeLayout mPaiseBtn;
    @BindView(R.id.view_praise)
    DivergeView mPaiseView;
    @BindView(R.id.rl_player)
    RelativeLayout relativeLayoutPlayer;

    @BindView(R.id.column_detail_ad_fl)
    FrameLayout program_detail_ad_fl;
    @BindView(R.id.column_detail_ad_img)
    RecycleImageView program_detail_ad_img;

    private ColumnDetailsAdapter mAdapter;
    private Disposable mDisposable;
    private boolean isCollect = false;
    private List<ProgramSeriesInfo> dataList;
    private ProgramSeriesInfo dataProgramSeriesInfoInfo;
    private String contentUUID, leftUUID, rightUUID;
    private Observable<VideoPlayInfo> mUpdateVideoInfoObservable;
    private int mPlayPosition;
    private ProgramSeriesInfo dataInfo;
    private IAdConstract.IADPresenter adPresenter;
    private String sqContentuuid;
    private boolean isFirstStart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_detail);
        ButterKnife.bind(this);
        adPresenter = new ADPresenter(this);
        mCollectIv.setBackgroundResource(R.drawable.icon_details_uncollect_btn);
        init();
        initView();
        initData();
        LogUploadUtils.uploadLog(Constant.LOG_NODE_ONE__DETAIL, contentUUID);
    }

    private void initData() {
        DataSupport.search(DBConfig.HISTORY_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUUID)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (!TextUtils.isEmpty(result)) {
                            Gson mGson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                            }.getType();
                            List<UserCenterPageBean.Bean> data = mGson.fromJson(result, type);
                            mPlayPosition = Integer.valueOf(data.get(0).playPosition);
                            sqContentuuid = data.get(0)._contentuuid;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                requestData();
                            }
                        });
                    }
                }).excute();
    }

    private void init() {
        contentUUID = getIntent().getStringExtra("content_uuid");
        if (TextUtils.isEmpty(contentUUID)) {
            contentUUID = "be87b1d92b1947848ab785dd930b55ca";
        }

        ADConfig.getInstance().setSeriesID(contentUUID);
        adPresenter.getAD(Constant.AD_DESK, Constant.AD_DETAILPAGE_BANNER, Constant.AD_DETAILPAGE_BANNER);//获取广告
        leftUUID = contentUUID.substring(0, 2);
        rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        mAdapter = new ColumnDetailsAdapter(getApplicationContext(), this);
        dataList = new ArrayList<>();
        DataSupport.search(DBConfig.SUBSCRIBE_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUUID)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (TextUtils.isEmpty(result)) {
                            isCollect = false;
                        } else {
                            isCollect = true;
                        }
                    }
                }).excute();


        mUpdateVideoInfoObservable = RxBus.get().register(Constant.UPDATE_VIDEO_PLAY_INFO);
        mUpdateVideoInfoObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VideoPlayInfo>() {
                    @Override
                    public void accept(VideoPlayInfo data) throws Exception {
                        if (TextUtils.equals(data.uuid, contentUUID)) {
                            mPlayPosition = data.position;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication()) {
        @Override
        public boolean canScrollVertically() {
            return false;
        }
    };

    private void initView() {
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mCollectBtn.setOnKeyListener(this);
        mVideoView.setOnKeyListener(this);
        mVideoView.setPlayerCallback(new PlayerCallback() {
            @Override
            public void onEpisodeChange(int index, int position) {

            }

            @Override
            public void onPlayerClick(VideoPlayerView videoPlayerView) {
                videoPlayerView.EnterFullScreen(ProgramDetailsPageActivity.this, false);
            }

            @Override
            public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
                mVideoView.release();
            }

            @Override
            public void ProgramChange() {

            }
        });
        mBigScreenBtn.setOnKeyListener(this);
        mPaiseBtn.setOnKeyListener(this);
        relativeLayoutPlayer.setOnKeyListener(this);
        mVideoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mFocusIv.setVisibility(View.VISIBLE);
                } else {
                    mFocusIv.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPaiseView.setEndPoint(new PointF(mPaiseView.getMeasuredWidth() / 2, 0));
        mPaiseView.setStartPoint(new PointF(getResources().getDimension(R.dimen.width_40px), getResources().getDimension(R.dimen.height_185px)));
        mPaiseView.setDivergeViewProvider(new DivergeView.DivergeViewProvider() {
            @Override
            public Bitmap getBitmap(Object obj) {
                return ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable.icon_praise, null)).getBitmap();
            }
        });
    }


    private void requestData() {
        dataList.clear();
        NetClient.INSTANCE.getDetailsPageApi().getInfo(Constant.APP_KEY, Constant.CHANNEL_ID, leftUUID, rightUUID, contentUUID)
                .subscribeOn(Schedulers.io())
                .compose(this.<ResponseBody>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody value) throws Exception {
                        String data = value.string();
                        JSONObject object = new JSONObject(data);
                        if (object.getInt("errorCode") == 0) {
                            JSONObject obj = object.getJSONObject("data");
                            Gson gson = new Gson();
                            dataInfo = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                            setHeadData(dataInfo);
                        } else {
                            Toast.makeText(getApplicationContext(), "没有此节目信息", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        return NetClient.INSTANCE.getListPageApi()
                                .getScreenResult(dataInfo.getVideoType(), Constant.APP_KEY, Constant.CHANNEL_ID, "PS;CG", "",
                                        "", "", 0 + "", 6 + "").subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                }).flatMap(new Function<ResponseBody, ObservableSource<List<ProgramSeriesInfo>>>() {
            @Override
            public ObservableSource<List<ProgramSeriesInfo>> apply(ResponseBody value) throws Exception {
                try {
                    String data = value.string();
                    Gson mGson = new Gson();
                    ScreenInfo mScreenInfo = mGson.fromJson(data, ScreenInfo.class);
                    ProgramSeriesInfo info = new ProgramSeriesInfo();
                    info.layoutTitle = "相关推荐";
                    info.layoutId = 4;
                    List<ProgramSeriesInfo.ProgramsInfo> list = new ArrayList<>();
                    if (mScreenInfo.getResultList().size() > 0) {
                        for (int i = 0; i < mScreenInfo.getResultList().size(); i++) {
                            ScreenInfo.ResultListBean entity = mScreenInfo.getResultList().get(i);
                            list.add(new ProgramSeriesInfo.ProgramsInfo(entity.getUUID(), entity.getName(), entity.getContentType(), entity.getHpicurl(), entity.getHpicurl(), "", "", "", "", "", "", "", "", "", entity.getDesc()));
                        }
                        info.setData(list);
                        dataList.add(info);
                    }

                } catch (Exception e) {
                    LogUtils.e(e);
                }
                return Observable.just(dataList);

            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProgramSeriesInfo>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(List<ProgramSeriesInfo> columnPageBean) {
                        mAdapter.appendToList(columnPageBean);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.setHasFixedSize(true);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (dataInfo == null) {
                            Toast.makeText(getApplicationContext(), "没有此栏目信息", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void setHeadData(ProgramSeriesInfo dataInfo) {
        String star = mAdapter.getInfalteContent(dataInfo.getDirector()) + mAdapter.getInfalteContent(dataInfo.getActors());
        String mStar = mAdapter.getsplit(star);
        detailStarTv.setText(mStar);
        String area = mAdapter.getInfalteContent(dataInfo.getArea()) + mAdapter.getInfalteContent(dataInfo.getAirtime())
                + mAdapter.getInfalteContent(dataInfo.getVideoType()) + mAdapter.getInfalteContent(dataInfo.getGrade());
        String mType = mAdapter.getsplit(area);

        detailTypeTv.setText(mType);
        if (isCollect) {
            mCollectIv.setImageResource(R.drawable.icon_details_collect_btn);
        } else {
            mCollectIv.setImageResource(R.drawable.icon_details_uncollect_btn);
        }
        detailContentTv.setText(dataInfo.getDescription());
        detailTitleTv.setText(dataInfo.getTitle());
        mVideoView.setSeriesInfo(dataInfo);
        mVideoView.playSingleOrSeries(
                0, mPlayPosition);

    }

    private void addData(String data, int layoutId, String title) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.getInt("errorCode") == 0) {
                JSONObject obj = object.getJSONObject("data");
                Gson gson = new Gson();
                ProgramSeriesInfo info = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                info.layoutId = layoutId;
                info.layoutTitle = title;
                dataList.add(info);

            }
        } catch (JSONException e) {

        }
    }

    @Override
    protected void onDestroy() {
        unSubscribe();
        RxBus.get().unregister(Constant.UPDATE_VIDEO_PLAY_INFO, mUpdateVideoInfoObservable);
        super.onDestroy();
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


    private void delCollect(final String contentUuId) {
        DataSupport.delete(DBConfig.COLLECT_TABLE_NAME).condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    isCollect = false;
                                    LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT,"1,"+contentUuId);
                                    mCollectIv.setImageResource(R.drawable.icon_details_uncollect_btn);
                                    Toast.makeText(getApplicationContext(), "取消收藏成功", Toast.LENGTH_SHORT).show();
                                    RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                                }
                            });
                        }

                    }
                }).excute();
    }

    private void updateCollect(final ProgramSeriesInfo entity) {
        //TODO 写入本地数据库 历史记录
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfig.CONTENTUUID, entity.getContentUUID());
        contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.IMAGEURL, entity.getvImage());
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
        Log.e("MM", "收藏=" + entity.toString());
        contentValues.put(DBConfig.UPDATE_TIME, Utils.getSysTime());
        DataSupport.insertOrReplace(DBConfig.COLLECT_TABLE_NAME)
                .withValue(contentValues)
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    isCollect = true;
                                    LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT,"0,"+entity.getContentUUID());
                                    mCollectIv.setImageResource(R.drawable.icon_details_collect_btn);
                                    Toast.makeText(getApplicationContext(), R.string.collect_success, Toast.LENGTH_SHORT).show();
                                    RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                                }
                            });
                        }
                    }
                }).excute();
    }

    @Override
    public void onItemClick(View view, int position, Object object) {
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, Object object) {
        switch (view.getId()) {
            case R.id.iv_detail_video:
                break;
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return false;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            onClick(v);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (v.getId() == R.id.btn_detail_collect) {
                return true;
            }
        }
        return false;
    }

    private long lastClickTime = 0;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_big_screen:
                mPlayPosition = mVideoView.getCurrentPosition();
                NewTVLauncherPlayerViewManager.getInstance().playProgramSingle(this, dataInfo,
                        mPlayPosition,true);
                break;
            case R.id.btn_detail_collect:
                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                    if (isCollect) {
                        delCollect(contentUUID);
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "1," + contentUUID);//取消收藏
                    } else {
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "0," + contentUUID);//收藏
                        updateCollect(dataInfo);
                    }
                }
                break;

            case R.id.btn_detail_praise:
                mPaiseView.startDiverges(0);
                break;
            case R.id.iv_detail_video:
                mPlayPosition = mVideoView.getCurrentPosition();
                NewTVLauncherPlayerViewManager.getInstance().playProgramSingle(this, dataInfo,
                        mPlayPosition,true);
                break;
        }
    }

    @Override
    protected void onStop() {
        upDateHistory();
        mVideoView.release();
        super.onStop();
    }

    private void upDateHistory() {
        if (mVideoView.getCurrentPosition() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConfig.CONTENTUUID, dataInfo.getContentUUID());
            contentValues.put(DBConfig.CONTENTTYPE, dataInfo.getContentType());
            contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
            contentValues.put(DBConfig.IMAGEURL, dataInfo.gethImage());
            contentValues.put(DBConfig.TITLE_NAME, dataInfo.getTitle());
            contentValues.put(DBConfig.PLAYPOSITION, mVideoView.getCurrentPosition());
            contentValues.put(DBConfig.UPDATE_TIME, Utils.getSysTime());
            DataSupport.insertOrUpdate(DBConfig.HISTORY_TABLE_NAME)
                    .condition()
                    .eq(DBConfig.CONTENTUUID, dataInfo.getContentUUID())
                    .build()
                    .withValue(contentValues)
                    .withCallback(new DBCallback<String>() {
                        @Override
                        public void onResult(int code, String result) {
                            if (code == 0) {
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," + dataInfo.getContentUUID());//添加历史记录
                                RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                            }
                        }
                    }).excute();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUploadUtils.uploadLog(Constant.LOG_COLUMN_INTO, "0," + contentUUID);//进入栏目列表
        if (isFirstStart) {
            mVideoView.playSingleOrSeries(0, mPlayPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFirstStart) {
            isFirstStart = true;
        }
    }

    @Override
    public void showAd(ADHelper.AD.ADItem result) {
        if (!TextUtils.isEmpty(result.AdUrl)) {
            if (program_detail_ad_fl != null) {
                program_detail_ad_fl.setVisibility(View.VISIBLE);
            }
            if (program_detail_ad_img != null) {
                program_detail_ad_img.hasCorner(true).load(result.AdUrl);
            }
        }
    }
}
