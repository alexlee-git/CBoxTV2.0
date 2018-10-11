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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.RxBus;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
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
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.adapter.ColumnDetailsAdapter;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.ProgramsInfo;
import tv.newtv.cboxtv.cms.details.model.VideoPlayInfo;
import tv.newtv.cboxtv.cms.details.view.VerticallRecyclerView;
import tv.newtv.cboxtv.cms.listPage.model.ScreenInfo;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.Utils;
import tv.newtv.cboxtv.views.custom.DivergeView;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;


/**
 * Created by gaoleichao on 2018/3/30.
 */

/**
 * 节目集综艺详情页
 */
public class VarietyShowFragment extends BaseFragment implements OnRecycleItemClickListener, View
        .OnKeyListener {
    private static final String TAG = "VarietyShowFragment";

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
    private boolean isFirstStart = false;
    private ColumnDetailsAdapter mAdapter;
    private Disposable mDisposable;
    private boolean isCollect = false;
    private List<ProgramSeriesInfo> dataList;
    private ProgramSeriesInfo dataInfo;
    private String contentUUID, leftUUID, rightUUID;
    private Observable<VideoPlayInfo> mUpdateVideoInfoObservable;
    private int mPlayPosition = 0;
    private String videoType = "";
    private int mIndex = 0;
    private long lastClickTime = 0;

    public static VarietyShowFragment newInstance(String uuid) {
        VarietyShowFragment fragment = new VarietyShowFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content_uuid", uuid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View getFirstFocusView() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            contentUUID = bundle.getString("content_uuid");
            if (TextUtils.isEmpty(contentUUID)) {
                getActivity().finish();
            }
        }
        init();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_column_detail, null, false);
        ButterKnife.bind(this, rootView);
        init();
        initView();
        initData();
        return rootView;
    }

    private void initData() {
        DataSupport.search(DBConfig.HISTORY_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUUID)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        try {
                            if (!TextUtils.isEmpty(result)) {
                                Gson mGson = new Gson();
                                Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                }.getType();
                                List<UserCenterPageBean.Bean> data = mGson.fromJson(result, type);
                                mPlayPosition = Integer.valueOf(data.get(0).playPosition);
                                if (data.get(0).playIndex != null) {
                                    mIndex = Integer.valueOf(data.get(0).playIndex);
                                }
                            }
                        } catch (Exception e) {
                            LogUtils.e(e.toString());
                        }
                        requestData();
                    }
                }).excute();
    }

    private void init() {
        leftUUID = contentUUID.substring(0, 2);
        rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        mAdapter = new ColumnDetailsAdapter(getActivity(), this);
        dataList = new ArrayList<>();
        DataSupport.search(DBConfig.COLLECT_TABLE_NAME)
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
                        if (TextUtils.equals(data.uuid, contentUUID) && mIndex > 0) {
                            mIndex = data.index;
                            mPlayPosition = data.position;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mCollectBtn.setOnKeyListener(this);
        mVideoView.setOnKeyListener(this);
        mBigScreenBtn.setOnKeyListener(this);
//        mBigScreenBtn.requestFocus();//进入页面获取焦点
        mPaiseBtn.setOnKeyListener(this);
        mVideoView.requestFocus();
        mVideoView.setOnKeyListener(this);
        mFocusIv.setVisibility(View.VISIBLE);
        mVideoView.setPlayerCallback(new PlayerCallback() {
            @Override
            public void onEpisodeChange(int index, int position) {

            }

            @Override
            public void onPlayerClick(VideoPlayerView videoPlayerView) {

            }

            @Override
            public void AllPlayComplete(boolean isError, String info, VideoPlayerView
                    videoPlayerView) {
                if (!isError) {
                    mVideoView.onComplete();
                }
            }

            @Override
            public void ProgramChange() {
                //TODO 电视剧退出全屏，剧集发生改变，重新设置播放信息

                mVideoView.setSeriesInfo(dataInfo);
                mVideoView.playSingleOrSeries(mIndex, mPlayPosition);
            }
        });
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
        mPaiseView.setStartPoint(new PointF(getResources().getDimension(R.dimen.width_40px),
                getResources().getDimension(R.dimen.height_185px)));
        mPaiseView.setDivergeViewProvider(new DivergeView.DivergeViewProvider() {
            @Override
            public Bitmap getBitmap(Object obj) {
                return ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable
                        .icon_praise, null)).getBitmap();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoView.release();

    }

    private void requestData() {
        dataList.clear();
        NetClient.INSTANCE.getDetailsPageApi().getInfo(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID,
                leftUUID, rightUUID, contentUUID)
                .subscribeOn(Schedulers.io())
                .compose(this.<ResponseBody>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody value) throws
                            Exception {
                        String data = value.string();
                        JSONObject object = new JSONObject(data);
                        if (object.getInt("errorCode") == 0) {
                            JSONObject obj = object.getJSONObject("data");
                            Gson gson = new Gson();

                            dataInfo = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                            if (dataInfo != null) {
                                String playOrder = dataInfo.getPlayOrder();
                                if (playOrder.equals("0")) {
                                    List<ProgramsInfo> programsInfoList =
                                            dataInfo.getData();
                                    Collections.reverse(programsInfoList);// 反转List列表中元素的顺序
                                    dataInfo.setData(programsInfoList);
                                }
                                dataInfo.layoutId = 2;
                                dataInfo.layoutTitle = "播放列表";
                                dataList.add(dataInfo);
                                setHeadData(dataInfo);

                            }

                        } else {
                            Toast.makeText(getActivity(), "没有此栏目信息", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                        if (dataInfo != null) {
                            videoType = dataInfo.getVideoType();
                        }

                        return NetClient.INSTANCE.getListPageApi()
                                .getScreenResult(videoType, BuildConfig.APP_KEY, BuildConfig
                                                .CHANNEL_ID, "PS", "",
                                        "", "", 0 + "", 6 + "").subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                }).flatMap(new Function<ResponseBody, ObservableSource<List<ProgramSeriesInfo>>>() {
            @Override
            public ObservableSource<List<ProgramSeriesInfo>> apply(ResponseBody value) throws
                    Exception {
                addData(value.string(), 4, "相关推荐");
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
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (dataInfo == null) {
                            Toast.makeText(getActivity(), "没有此栏目信息", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setHeadData(ProgramSeriesInfo dataInfo) {
        String star = mAdapter.getInfalteContent(dataInfo.getDirector()) + mAdapter
                .getInfalteContent(dataInfo.getActors());
        String mStar = mAdapter.getsplit(star);
        detailStarTv.setText(mStar);
        String area = mAdapter.getInfalteContent(dataInfo.getArea()) + mAdapter.getInfalteContent
                (dataInfo.getAirtime())
                + mAdapter.getInfalteContent(dataInfo.getVideoType()) + mAdapter
                .getInfalteContent(dataInfo.getGrade());
        String mType = mAdapter.getsplit(area);
        detailTypeTv.setText(mType);
        if (isCollect) {
            mCollectIv.setImageResource(R.drawable.icon_details_collect_btn);
        } else {
            mCollectIv.setImageResource(R.drawable.icon_details_uncollect_btn);
        }

        mVideoView.setSeriesInfo(dataInfo);
        mVideoView.playSingleOrSeries(mIndex, mPlayPosition);

        mAdapter.setCurrentPlayUUID(mIndex, dataInfo.getData().get(mIndex).getContentUUID());

        detailContentTv.setText(dataInfo.getDescription());
        detailTitleTv.setText(dataInfo.getTitle());

    }

    private void addData(String data, int layoutId, String title) {
        if (title.equals("相关推荐")) {
            Gson mGson = new Gson();
            ScreenInfo mScreenInfo = mGson.fromJson(data, ScreenInfo.class);
            ProgramSeriesInfo infoRecommdend = new ProgramSeriesInfo();
            infoRecommdend.layoutTitle = "相关推荐";
            infoRecommdend.layoutId = 4;
            List<ProgramsInfo> list = new ArrayList<>();
            if (mScreenInfo != null && mScreenInfo.getResultList().size() > 0) {
                for (int i = 0; i < mScreenInfo.getResultList().size(); i++) {
                    ScreenInfo.ResultListBean entity = mScreenInfo.getResultList().get(i);
                    list.add(new ProgramsInfo(entity.getUUID(), entity.getName
                            (), entity.getContentType(), entity.getHpicurl(), entity.getHpicurl()
                            , "", "", "", "", "", "", "", "", "", entity.getDesc()));
                }
                if (list.size() != 0) {
                    infoRecommdend.setData(list);
                    dataList.add(infoRecommdend);
                }


            }

        }
    }

    @Override
    public void onDestroyView() {
        unSubscribe();
        RxBus.get().unregister(Constant.UPDATE_VIDEO_PLAY_INFO, mUpdateVideoInfoObservable);
        NewTVLauncherPlayerViewManager.getInstance().release();
        super.onDestroyView();
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

    private void delCollect(String contentUuId) {
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
                                    mCollectIv.setImageResource(R.drawable
                                            .icon_details_uncollect_btn);
                                    Toast.makeText(getActivity(), "取消收藏成功", Toast.LENGTH_SHORT)
                                            .show();
                                    mAdapter.notifyItemChanged(0);
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
        contentValues.put(DBConfig.IMAGEURL, entity.getvImage());
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
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
                                    mCollectIv.setImageResource(R.drawable
                                            .icon_details_collect_btn);
                                    Toast.makeText(getActivity(), R.string.collect_success, Toast.LENGTH_SHORT)
                                            .show();
                                    mAdapter.notifyItemChanged(0);
                                    RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                                }
                            });
                        }
                    }
                }).excute();
    }

    @Override
    public void onItemClick(View view, int position, Object object) {
        switch (view.getId()) {
            case R.id.id_colmn_detail_view:
                mVideoView.startOrPause();
                break;
            case R.id.id_module_8_view1:
            case R.id.id_module_8_view2:
            case R.id.id_module_8_view3:
            case R.id.id_module_8_view4:
            case R.id.id_module_8_view5:
            case R.id.id_module_8_view6:
            case R.id.id_module_8_view7:
            case R.id.id_module_8_view8:
                mIndex = position;
                mPlayPosition = 0;
                mVideoView.playSingleOrSeries(mIndex, mPlayPosition);
                break;
        }
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
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                onClick(v);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (v.getId() == R.id.btn_detail_praise) {
                    return true;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (v.getId() == R.id.iv_detail_video) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_big_screen:
                mPlayPosition = mVideoView.getCurrentPosition();
                if (dataList != null && dataList.size() != 0) {
                    mVideoView.EnterFullScreen(getActivity(), false);
                    // NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(getActivity()
                    //, dataList.get(0), mIndex, mPlayPosition);
                }
                break;
            case R.id.btn_detail_collect:
                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
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

            case R.id.btn_detail_praise:
                mPaiseView.startDiverges(0);
                break;
            case R.id.iv_detail_video:
                mPlayPosition = mVideoView.getCurrentPosition();
                if (dataList != null && dataList.size() != 0) {
                    mVideoView.EnterFullScreen(getActivity(), false);
                    //NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(getActivity()
                    // , dataList.get(0), mIndex, mPlayPosition);
                }
                break;
        }
    }

    private void playProgramSeries() {
        mPlayPosition = mVideoView.getCurrentPosition();
        if (dataList != null && dataList.size() > 0) {
            mVideoView.EnterFullScreen(getActivity(), false);
            //NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(getActivity(),
            // dataList.get(0), mIndex, mPlayPosition);
        } else {
            Log.e(TAG, "dataList cannot null");
        }
    }


    @Override
    protected void onInvisible() {
        super.onInvisible();
        addHistory();
        mVideoView.release();
    }

    private void addHistory() {
        if (dataInfo == null || dataInfo.getData() == null || dataInfo.getData().size() == 0) {
            Log.i(TAG, "dataInfo is null");
            return;
        }
        if (mVideoView.getCurrentPosition() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConfig.CONTENTUUID, dataInfo.getContentUUID());
            contentValues.put(DBConfig.CONTENTTYPE, dataInfo.getContentType());
            contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
            contentValues.put(DBConfig.IMAGEURL, dataInfo.getvImage());
            contentValues.put(DBConfig.TITLE_NAME, dataInfo.getTitle());
            contentValues.put(DBConfig.PLAYINDEX, mIndex + "");
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

                            LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," + dataInfo
                                    .getContentUUID());//添加历史记录
                            if (code == 0) {
                                RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                            }
                        }
                    }).excute();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayPosition = mVideoView.getCurrentPosition();
        isFirstStart = true;
        addHistory();
        mVideoView.release();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isFirstStart) {
            if (mVideoView.hasNext(mIndex)) {
                mVideoView.playSingleOrSeries(mIndex, mPlayPosition);
            } else {
                mVideoView.onComplete();
            }
        }

    }

    @Override
    protected void onVisible() {
        super.onVisible();

    }
}
