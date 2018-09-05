package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.details.adapter.ColumnDetailsAdapter;
import tv.newtv.cboxtv.cms.details.adapter.EpisodeAdapter;
import tv.newtv.cboxtv.cms.details.adapter.ProgrameSeriesAdapter;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.details.view.VerticallRecyclerView;
import tv.newtv.cboxtv.cms.listPage.model.ScreenInfo;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.PageHelper;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.db.DBCallback;
import tv.newtv.cboxtv.uc.db.DBConfig;
import tv.newtv.cboxtv.uc.db.DataSupport;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.utils.PlayInfoUtil;
import tv.newtv.cboxtv.views.RecycleImageView;
import tv.newtv.cboxtv.views.detailpage.SmoothScrollView;

/**
 * Created by gaoleichao on 2018/4/8.
 * <p>
 * 节目集剧集选择详情页
 */

/**
 * 节目集剧集详情页(电视剧)
 */
public class ProgrameSeriesFragment extends BaseFragment implements
        OnRecycleItemClickListener, IAdConstract.IADConstractView,
        PlayerCallback {

    private static final String TAG = "ProgrameSeriesFragment";
    //    @BindView(R.id.id_detail_view)
//    RelativeLayout mDetailsImgView;
    @BindView(R.id.detail_rel_image_collect)
    ImageView mCollectIv;
    //    @BindView(R.id.iv_detail_image_focus)
//    ImageView mFocusIv;
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
    @BindView(R.id.btn_detail_big_screen)
    RelativeLayout mBigScreenBtn;
    @BindView(R.id.btn_detail_collect)
    RelativeLayout mCollectBtn;
    @BindView(R.id.id_series_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.id_series_list_menu)
    RecyclerView mMenuRecyclerView;
    @BindView(R.id.id_usercenter_fragment_root)
    VerticallRecyclerView verticallRecyclerView;

    @BindView(R.id.id_series_subtitle)
    TextView mSubTitleTv;
    @BindView(R.id.rl_series)
    RelativeLayout relativeLayout;

    VideoPlayerView mVideoView;

    @BindView(R.id.iv_detail_video_rl)
    FrameLayout iv_detail_video_rl;

    @BindView(R.id.program_detail_ad_fl)
    FrameLayout program_detail_ad_fl;
    @BindView(R.id.program_detail_ad_img)
    RecycleImageView program_detail_ad_img;
    @BindView(R.id.id_scroll_view)
    SmoothScrollView scrollView;
    LinearLayoutManager linearLayoutManager;
    private int historyposition = 0;
    private boolean isCollect = false;

    private String contentUUID, leftUUID, rightUUID;
    private ProgrameSeriesAdapter mSeriesAdapter;
    private EpisodeAdapter mMenuAdapter;
    private ColumnDetailsAdapter mAdapter;
    private PageHelper mPageDaoImpl;
    private int mIndex = 0;
    private int mPlayPosition = 0;
    private ProgramSeriesInfo dataInfo;
    private boolean isFirstStart = false;
    //   private Observable<VideoPlayInfo> mUpdateVideoInfoObservable;
    private Disposable mDisposable;
    private ADPresenter adPresenter;
    private List<ProgramSeriesInfo> dataList;
    private long lastClickTime = 0;
    //    private Handler handler = new Handler(this);
    private int mPosition;
    private Runnable enterKeyRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSeriesAdapter == null) {
                return;
            }
            mPageDaoImpl.setCurrentPage(mPosition + 1);
            mSeriesAdapter.clear();
            mSeriesAdapter.setCurrenPage(mPosition);
            mSeriesAdapter.appendToList(mPageDaoImpl.currentList());
            mSeriesAdapter.notifyDataSetChanged();
            mMenuAdapter.setCurrenPage(mPageDaoImpl.getCurrentPage(mIndex) - 1);
        }
    };
    private NewTVLauncherPlayerView.PlayerViewConfig defaultConfig;

    public static ProgrameSeriesFragment newInstance(String uuid) {
        ProgrameSeriesFragment fragment = new ProgrameSeriesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("content_uuid", uuid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (adPresenter != null) {
            adPresenter.destroy();
            adPresenter = null;
        }

//        mDetailsImgView = null;
        mCollectIv = null;
//        mFocusIv = null;
        detailTitleTv = null;
        detailTypeTv = null;
        detailContentTv = null;
        detailStarTv = null;
        mBigScreenIv = null;
        mBigScreenBtn = null;
        mCollectBtn = null;
        mRecyclerView = null;
        mMenuRecyclerView = null;
        verticallRecyclerView = null;
        mSubTitleTv = null;
        relativeLayout = null;
        if (mVideoView != null) {
            mVideoView.stopPlay();
            mVideoView.release();
            mVideoView.destory();
            mVideoView = null;
        }
        iv_detail_video_rl = null;
        program_detail_ad_fl = null;
        program_detail_ad_img = null;
        scrollView = null;


        linearLayoutManager = null;
        if (mSeriesAdapter != null) {
            mSeriesAdapter.destroy();
            mSeriesAdapter = null;
        }
        if (mMenuAdapter != null) {
            mMenuAdapter.destroy();
            mMenuAdapter = null;
        }

        if (mAdapter != null) {
            mAdapter.destroy();
            mAdapter = null;
        }

    }

    @Override
    public View getFirstFocusView() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        if (bundle != null) {
            contentUUID = bundle.getString("content_uuid");
            ADConfig.getInstance().setSeriesID(contentUUID);
            if (TextUtils.isEmpty(contentUUID)) {
                Toast.makeText(getContext().getApplicationContext(), "没有此剧集信息", Toast
                        .LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_details_programe, null, false);

        ButterKnife.bind(this, rootView);
        init();

        initData();
        initView();
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
                        if (!TextUtils.isEmpty(result)) {
                            Gson mGson = new Gson();
                            Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                            }.getType();
                            List<UserCenterPageBean.Bean> data = mGson.fromJson(result, type);
                            mPlayPosition = Integer.parseInt(data.get(0).playPosition);

                            String index = data.get(0).playIndex;
                            mIndex = TextUtils.isEmpty(index) ? 0 : Integer.parseInt(index);
                        }
                        requestData();
                    }
                }).excute();
    }

    private void init() {
        adPresenter = new ADPresenter(this);

        mAdapter = new ColumnDetailsAdapter(getContext().getApplicationContext(), this);
        dataList = new ArrayList<>();
        if (contentUUID.length() >= 2) {
            leftUUID = contentUUID.substring(0, 2);
            rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        } else {
            Toast.makeText(getContext().getApplicationContext(), "节目集信息有误", Toast.LENGTH_SHORT)
                    .show();
            getActivity().finish();
        }
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
        mSeriesAdapter = new ProgrameSeriesAdapter(getContext().getApplicationContext(), this);
        mMenuAdapter = new EpisodeAdapter(getContext().getApplicationContext(), new EpisodeAdapter
                .OnItemEnterKeyListener() {
            @Override
            public void onEnterKey(View v, int mPosition) {
                ProgrameSeriesFragment.this.mPosition = mPosition;
                scrollView.postDelayed(enterKeyRunnable, 200);
            }
        });

    }

    private void prepareMediaPlayer() {
        if (mVideoView != null && mVideoView.isReleased()) {
            mVideoView.release();
            mVideoView.destory();
            if (mVideoView.getParent() != null) {
                ((ViewGroup) mVideoView.getParent()).removeView(mVideoView);
            }
            mVideoView = null;
        }

        if (mVideoView == null) {
            if (defaultConfig == null) {
                mVideoView = new VideoPlayerView(getContext());
                mVideoView.setId(R.id.id_video_player);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
                        .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                mVideoView.setLayoutParams(layoutParams);
                iv_detail_video_rl.addView(mVideoView, layoutParams);

                mVideoView.setVisibility(View.VISIBLE);
                mVideoView.setPlayerCallback(this);

                if (dataInfo != null) {
                    mVideoView.setSeriesInfo(dataInfo);
                }
            } else {
                mVideoView = new VideoPlayerView(defaultConfig, getContext());
            }
        }
    }

    private void initView() {
        mBigScreenBtn.requestFocus();//bug系统31提出的要求，进入页面焦点默认在全屏按钮上
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext().getApplicationContext()
                , 15));
        mMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()
                .getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mMenuRecyclerView.setAdapter(mMenuAdapter);
        mRecyclerView.setAdapter(mSeriesAdapter);
        verticallRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ColumnDetailsAdapter(getContext().getApplicationContext(), this);
        verticallRecyclerView.setAdapter(mAdapter);

        mCollectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCollect();
            }
        });

        mBigScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doBigScreen();
            }
        });

        prepareMediaPlayer();
    }

    public boolean interruptKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int keycode = event.getKeyCode();
            View focusView = scrollView.findFocus();
            int dir = View.FOCUS_DOWN;
            switch (keycode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    dir = View.FOCUS_UP;
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (focusView != null && focusView.getId() == R.id.btn_detail_collect) {
                        return true;
                    }
                    dir = View.FOCUS_RIGHT;
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    dir = View.FOCUS_DOWN;
                    if (focusView.getId() == R.id.btn_detail_collect
                            || focusView.getId() == R.id.btn_detail_big_screen
                            || focusView.getId() == R.id.id_video_player) {
                        if (mRecyclerView != null && mRecyclerView.getChildAt(0) != null) {
                            mRecyclerView.getChildAt(0).requestFocus();
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (focusView == mVideoView) {
                        return true;
                    }
                    dir = View.FOCUS_LEFT;
                    break;
            }
            View targetView = FocusFinder.getInstance().findNextFocus(scrollView, scrollView
                    .findFocus(), dir);
            if (targetView != null) {
                if (dir == View.FOCUS_UP) {
                    switch (targetView.getId()) {
                        case R.id.btn_detail_collect:
                        case R.id.btn_detail_big_screen:
                        case R.id.id_video_player:
                            mVideoView.requestFocus();
                            scrollView.scrollToTop();
                            return true;
                        default:
                            break;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void requestData() {
        NetClient.INSTANCE.getDetailsPageApi().getInfo(Constant.APP_KEY, Constant.CHANNEL_ID,
                leftUUID, rightUUID, contentUUID)
                .subscribeOn(Schedulers.io())
                .compose(this.<ResponseBody>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody,
                        ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody data) throws
                            Exception {
                        try {
                            JSONObject object = new JSONObject(data.string());
                            if (object.getInt("errorCode") == 0) {
                                JSONObject obj = object.getJSONObject("data");
                                Gson gson = new Gson();
                                dataInfo = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                                if (dataInfo != null) {
                                    //              第一行是 地区、年代、一级分类  第二行是 主持人、导演、主演，所有人员名称就是
                                    // 连续一行显示，用竖线前后空格区分。

                                    if (!TextUtils.isEmpty(dataInfo.getDirector()) && !TextUtils
                                            .isEmpty(dataInfo.getActors())
                                            && !dataInfo.getDirector().equals("无")
                                            && !dataInfo.getActors().equals("无")) {
                                        detailStarTv.setText("导演:" + dataInfo.getDirector() + " |" +
                                                " " + "主演:" + dataInfo.getActors());
                                    } else {
                                        detailStarTv.setVisibility(View.GONE);
                                    }
                                    String mType = PlayInfoUtil.formatSplitInfo(dataInfo.getArea
                                            (), dataInfo.getAirtime(), dataInfo.getVideoType());
                                    detailTypeTv.setText(mType);
                                    if (isCollect) {
                                        mCollectIv.setImageResource(R.drawable
                                                .icon_details_collect_btn);
                                    } else {
                                        mCollectIv.setImageResource(R.drawable
                                                .icon_details_uncollect_btn);
                                    }
                                    detailContentTv.setText(dataInfo.getDescription());
                                    detailTitleTv.setText(dataInfo.getTitle());
                                    if (!TextUtils.isEmpty(dataInfo.getSeriesSum())) {
                                        int allSize = Integer.valueOf(dataInfo.getSeriesSum());
                                        mSubTitleTv.setText(allSize >= dataInfo.getData().size()
                                                ? "全剧集" : "更新中");
                                    }
                                    if (mPageDaoImpl == null) {
                                        mVideoView.setSeriesInfo(dataInfo);
                                        mPageDaoImpl = new PageHelper<ProgramSeriesInfo
                                                .ProgramsInfo>
                                                (dataInfo.getData(), 30);
                                    }
                                    for (int i = 1; i < mPageDaoImpl.getPageNum() + 1; i++) {
                                        mMenuAdapter.append(getPageText(dataInfo.getData(), i));
                                    }
                                }
                            } else {
                                Toast.makeText(getContext().getApplicationContext(), "没有此剧集信息",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                getActivity().finish();
                            }
                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                        } catch (IOException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        String videoType;
                        if (dataInfo != null) {
                            videoType = dataInfo.getVideoType();
                        } else {
                            videoType = "";
                        }
                        return NetClient.INSTANCE.getListPageApi()
                                .getScreenResult(videoType, Constant.APP_KEY, Constant
                                                .CHANNEL_ID, "PS", "",
                                        "", "", "0", "6").subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                }).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                mDisposable = disposable;
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String data = responseBody.string();
                    Gson mGson = new Gson();
                    ScreenInfo mScreenInfo = mGson.fromJson(data, ScreenInfo.class);
                    ProgramSeriesInfo info = new ProgramSeriesInfo();
                    info.layoutTitle = "相关推荐";
                    info.layoutId = 4;
                    List<ProgramSeriesInfo.ProgramsInfo> list = new ArrayList<>();
                    if (mScreenInfo != null && mScreenInfo.getResultList().size() > 0) {
                        for (int i = 0; i < mScreenInfo.getResultList().size(); i++) {
                            ScreenInfo.ResultListBean entity = mScreenInfo.getResultList().get(i);
                            list.add(new ProgramSeriesInfo.ProgramsInfo(entity.getUUID(), entity
                                    .getName(), entity.getContentType(), entity.getHpicurl(),
                                    entity.getHpicurl(), "", "", "", "", "", "", "", "", "",
                                    entity.getDesc()));
                        }
                        if (list.size() > 0) {
                            info.setData(list);
                            dataList.add(info);
                            mAdapter.appendToList(dataList);
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.setHasFixedSize(true);

                        }
                        adPresenter.getAD(Constant.AD_DESK, Constant.AD_DETAILPAGE_BANNER, Constant
                                .AD_DETAILPAGE_BANNER);//获取广告


                        mVideoView.playSingleOrSeries(mIndex, mPlayPosition);
                    }
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }

            }

            @Override
            public void onError(Throwable throwable) {
                getActivity().finish();
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    @Override
    public void onItemClick(View view, int position, Object object) {
        int targetIndex = mPageDaoImpl.getCurrentPosition(position);

        if (mIndex == targetIndex) {
            mVideoView.requestFocus();
            mVideoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mVideoView.EnterFullScreen(getActivity(), false);
                }
            }, 500);
            return;
        }
        mIndex = targetIndex;
        mPlayPosition = 0;

        prepareMediaPlayer();

        if (mVideoView == null) return;

        mVideoView.playSingleOrSeries(mIndex, mPlayPosition);
        mSeriesAdapter.setPlayerPosition(mIndex, true);
        mMenuAdapter.setCurrenPage(mPageDaoImpl.getCurrentPage(mIndex) - 1);
        mSeriesAdapter.notifyDataSetChanged();
        mMenuAdapter.notifyDataSetChanged();

        mVideoView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVideoView.EnterFullScreen(getActivity(), false);
            }
        }, 500);
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, Object object) {
        Log.d(TAG, "onItemFocusChange");
    }

    private void doCollect() {
        if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
            lastClickTime = System.currentTimeMillis();//记录这次点击时间
            if (isCollect) {
                LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "1," +
                        contentUUID);//取消收藏
                delCollect(contentUUID);

            } else {
                LogUploadUtils.uploadLog(Constant.LOG_NODE_COLLECT, "0," +
                        contentUUID);//收藏
                updateCollect(dataInfo);
            }
        }
    }

    private void doBigScreen() {
        mVideoView.EnterFullScreen(getActivity(), false);
    }

    private void delCollect(String contentUuId) {
        DBUtil.UnCollect(contentUuId, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            isCollect = false;
                            mCollectIv.setImageResource(R.drawable.icon_details_uncollect_btn);
                            Toast.makeText(getContext().getApplicationContext(), "取消收藏成功", Toast
                                    .LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });

    }

    private void updateCollect(ProgramSeriesInfo entity) {
        DBUtil.PutCollect(entity, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            isCollect = true;
                            mCollectIv.setImageResource(R.drawable.icon_details_collect_btn);
                            Toast.makeText(getContext().getApplicationContext(), R.string.collect_success, Toast
                                    .LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        unSubscribe();
//        NewTVLauncherPlayerViewManager.getInstance().release();
        super.onDestroyView();
        if (mVideoView != null) {
            //   mVideoView.stopPlay();
            mVideoView.release();
            mVideoView.destory();
            mVideoView = null;
        }

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
    public void onResume() {
        super.onResume();

        prepareMediaPlayer();

        if (isFirstStart && dataInfo != null) {
            mVideoView.playSingleOrSeries(mIndex, mPlayPosition);
            mSeriesAdapter.setPlayerPosition(mIndex, false);
            mPageDaoImpl.setCurrentPage(mPageDaoImpl.getCurrentPage(mIndex));
            mMenuAdapter.setCurrenPage(mPageDaoImpl.getCurrentPage(mIndex) - 1);
            mSeriesAdapter.notifyDataSetChanged();
            mMenuAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();


        addHistory();

        if (mVideoView != null) {
            defaultConfig = mVideoView.getDefaultConfig();

            mVideoView.stopPlay();
            mVideoView.release();
            mVideoView.destory();
            mVideoView = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mPlayPosition = mVideoView.getCurrentPosition();
        if (!isFirstStart) {
            isFirstStart = true;
        }
    }

    private void addHistory() {
        if (mVideoView.getCurrentPosition() > 0) {
            DBUtil.addHistory(dataInfo, historyposition, mVideoView.getCurrentPosition(), new
                    DBCallback<String>() {
                        @Override
                        public void onResult(int code, String result) {
                            if (code == 0) {
                                LogUploadUtils.uploadLog(Constant.LOG_NODE_HISTORY, "0," + dataInfo
                                        .getContentUUID());//添加历史记录
                                RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                            }
                        }
                    });

        }
    }

    @Override
    public void showAd(String imgUrl, String adType) {
        if (!TextUtils.isEmpty(imgUrl)) {
            if (program_detail_ad_fl != null) {
                program_detail_ad_fl.setVisibility(View.VISIBLE);
            }

            if (program_detail_ad_img != null) {
                program_detail_ad_img.hasCorner(true).load(imgUrl);
            }
        } else {
            if (program_detail_ad_fl != null && program_detail_ad_fl.getParent() != null) {
                ((ViewGroup) program_detail_ad_fl.getParent()).removeView(program_detail_ad_fl);
                program_detail_ad_fl = null;
            }
        }
    }

    @Override
    public void onEpisodeChange(int mIndex, int position) {
        this.historyposition = mIndex;
        if (mPageDaoImpl != null) {
            int currentPage = mPageDaoImpl.getCurrentPage(mIndex);
            mPageDaoImpl.setCurrentPage(currentPage);
            mSeriesAdapter.setCurrenPage(currentPage - 1);
            mMenuAdapter.setCurrenPage(currentPage - 1);
            mSeriesAdapter.replace(mPageDaoImpl.currentList());
            mSeriesAdapter.notifyDataSetChanged();
            mMenuAdapter.notifyDataSetChanged();
            mSeriesAdapter.setPlayerPosition(mIndex, false);
        }

        Log.d(TAG, "onEpisodeChange");
        Log.d(TAG, "index==" + mIndex);
        Log.d(TAG, "position" + position);
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        Log.d(TAG, "onPlayerClick");
        videoPlayerView.EnterFullScreen(getActivity(), false);
    }

    @Override
    public void AllPalyComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
        if (!isError) {
            mVideoView.onComplete();
        }
    }

    @Override
    public void ProgramChange() {
        mVideoView.setSeriesInfo(dataInfo);
        mVideoView.playSingleOrSeries(mIndex, mPosition);
    }


    public String getPageText(List<ProgramSeriesInfo.ProgramsInfo> allData, int mPage) {
        int allNum = allData.size();
        int pageNum = mPageDaoImpl.getPageNum();
        int perPage = 30;
        List<ProgramSeriesInfo.ProgramsInfo> data = new ArrayList<>();
        if (mPage == 1) {
            if (allNum <= perPage) {
                data = allData.subList(0, allNum);
            } else {
                data = allData.subList(0, perPage);
            }
            Integer indexfirst = Integer.valueOf(data.get(data.size() - 1).getPeriods());
            Integer indexlast = Integer.valueOf(data.get(0).getPeriods());
            if (indexfirst < indexlast) {
                return indexfirst + "-" + indexlast;
            } else {
                return indexlast + "-" + indexfirst;
            }
        } else if (mPage == pageNum) {
            data = allData.subList(perPage * (pageNum - 1), allNum);
            Integer indexfirst = Integer.valueOf(data.get(data.size() - 1).getPeriods());
            Integer indexlast = Integer.valueOf(data.get(0).getPeriods());
            if (indexfirst < indexlast) {
                return indexfirst + "-" + indexlast;
            } else {
                return indexlast + "-" + indexfirst;
            }
        } else {
            data = allData.subList(perPage * (mPage - 1), (perPage * mPage));
            Integer indexfirst = Integer.valueOf(data.get(data.size() - 1).getPeriods());
            Integer indexlast = Integer.valueOf(data.get(0).getPeriods());
            if (indexfirst < indexlast) {
                return indexfirst + "-" + indexlast;
            } else {
                return indexlast + "-" + indexfirst;
            }
        }
    }
}
