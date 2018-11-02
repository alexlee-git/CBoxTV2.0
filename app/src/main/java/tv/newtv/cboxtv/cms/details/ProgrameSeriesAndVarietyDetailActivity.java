package tv.newtv.cboxtv.cms.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.menu.Utils;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.videoview.DivergeView;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.BitmapUtil;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.views.detailpage.EpisodeHelper;
import tv.newtv.cboxtv.views.detailpage.EpisodePageView;
import tv.newtv.cboxtv.views.detailpage.HeadPlayerView;
import tv.newtv.cboxtv.views.detailpage.IEpisode;
import tv.newtv.cboxtv.views.detailpage.SmoothScrollView;
import tv.newtv.cboxtv.views.detailpage.SuggestView;


/**
 * Created by gaoleichao on 2018/4/28.
 * 相关推荐详情页
 */
@BuyGoodsAD
public class ProgrameSeriesAndVarietyDetailActivity extends BaseActivity {
    private static final String TAG = "ProgrameSeriesAndVariet";
    private String leftUUID, rightUUID;
    private String contentUUID;
    private HeadPlayerView headPlayerView;
    private DivergeView mPaiseView;
    private EpisodePageView playListView;
    private SmoothScrollView scrollView;
    private ProgrameSeriesFragment fragment;
    private String videoType;
    private Disposable mDisposable;
    private long lastClickTime;
    private FragmentTransaction transaction;
    private FrameLayout frameLayout;
    private boolean isADEntry = false;
    private int currentIndex = -1;

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    public void prepareMediaPlayer() {
        super.prepareMediaPlayer();

        if(headPlayerView != null){
            headPlayerView.prepareMediaPlayer();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            contentUUID = getIntent().getStringExtra("content_uuid");
            isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY, false);
        } else {
            contentUUID = savedInstanceState.getString("content_uuid");
        }
        if (!TextUtils.isEmpty(contentUUID) && contentUUID.length() >= 2) {
            leftUUID = contentUUID.substring(0, 2);
            rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
            LogUploadUtils.uploadLog(Constant.LOG_COLUMN_INTO, "0," + contentUUID);
            ADConfig.getInstance().setSeriesID(contentUUID);
            requestData();
        } else {
            Toast.makeText(ProgrameSeriesAndVarietyDetailActivity.this, "节目集信息有误", Toast
                    .LENGTH_SHORT).show();
            ProgrameSeriesAndVarietyDetailActivity.this.finish();

        }

    }
    //请求数据
    private void requestData() {
        NetClient.INSTANCE.getDetailsPageApi().getInfo(Constant.APP_KEY, Constant.CHANNEL_ID,
                leftUUID, rightUUID, contentUUID)
                .subscribeOn(Schedulers.io())
                .compose(this.<ResponseBody>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody data) {
                        try {
                            JSONObject object = new JSONObject(data.string());
                            if (object.getInt("errorCode") == 0) {
                                JSONObject obj = object.getJSONObject("data");
                                Gson gson = new Gson();
                                ProgramSeriesInfo dataInfo = gson.fromJson(obj.toString(),
                                        ProgramSeriesInfo.class);
                                if (dataInfo != null) {
                                    videoType = dataInfo.getVideoType();
                                }
                                Log.i(TAG,"videoType--->"+videoType);
                                //这里节目详情页 要换成电视剧
                                if (!videoType()) {
                                    setContentView(R.layout.activity_details_programe_series);
                                    fragment = new ProgrameSeriesFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("content_uuid", contentUUID);
                                    fragment.setArguments(bundle);
                                    //初始化Fragment
                                    initFragment();
                                } else {
                                    setContentView(R.layout.fragment_new_variety_show);
                                    //初始化View
                                    initView();
                                    // fragment = NewVarietyShowFragment.newInstance(contentUUID);

                                }

                            } else {
                                Utils.showToast(getApplicationContext(), "没有节目集信息");
                                finish();
                            }
                        } catch (IOException e) {
                            Utils.showToast(getApplicationContext(), "读取异常");
                            finish();
                        } catch (JSONException j) {
                            Utils.showToast(getApplicationContext(), "解析失败");
                            finish();
                        }

                    }


                    @Override
                    public void onError(Throwable e) {
                        Utils.showToast(getApplicationContext(), "没有节目集信息");
                        finish();

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void initView() {
        playListView = findViewById(R.id.play_list);
        scrollView = findViewById(R.id.root_view);
        final SuggestView suggestView = findViewById(R.id.suggest);
        headPlayerView = ((HeadPlayerView) findViewById(R.id.header_video));
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.variety_item_head)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView.Builder
                        .DB_TYPE_COLLECT))
                .SetPlayerId(R.id.video_container)
//                .SetPlayerFocusId(R.id.video_focus)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add)
                .SetContentUUID(contentUUID)
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(ProgramSeriesInfo info) {
//                        headPlayerView.findViewUseId(R.id.video_container).requestFocus();
                        suggestView.setContentUUID(EpisodeHelper.TYPE_SEARCH, contentUUID, "",
                                info.getVideoType(), null);
                        playListView.setContentUUID(EpisodeHelper.TYPE_VARIETY_SHOW,
                                getSupportFragmentManager(),
                                contentUUID, null);
                    }
                })
                .SetPlayerCallback(new PlayerCallback() {
                    @Override
                    public void onEpisodeChange(int index, int position) {
                        Log.i("ProgrameSeries","当前显示index---->"+index);

                        currentIndex = index;

                        if (index >= 0) {
                            playListView.setCurrentPlayIndex(index);
                        }
                    }

                    @Override
                    public void ProgramChange() {
                        Log.i("ProgrameSeries","当前节目改变");
                        if (playListView != null) {
                            playListView.resetProgramInfo();
                        }
                    }



                    @Override
                    public void onPlayerClick(VideoPlayerView videoPlayerView) {
                        Log.i("ProgrameSeries","当前点击播放节目");

                        if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                            lastClickTime = System.currentTimeMillis();//记录这次点击时间
                            videoPlayerView.EnterFullScreen(ProgrameSeriesAndVarietyDetailActivity
                                    .this, false);
                        }
                    }

                    @Override
                    public void AllPlayComplete(boolean isError, String info, VideoPlayerView
                            videoPlayerView) {
                        Log.i("ProgrameSeries","当前节目播放完毕");
                        if (!isError) {
                            videoPlayerView.onComplete();
                        }
                    }
                })
                .SetVideoExitFullScreenCallBack(new VideoExitFullScreenCallBack() {
                    @Override
                    public void videoEitFullScreen() {
                        if (currentIndex>8){
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
                                Log.i("ProgrameSeries","点击显示全屏播放");
                                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                    headPlayerView.EnterFullScreen
                                            (ProgrameSeriesAndVarietyDetailActivity.this);
                                }

                                break;
                        }
                    }
                }));


        //当前电视剧播放改变
        playListView.setOnEpisodeChange(new EpisodePageView.OnEpisodeChange() {
            @Override
            public void onGetProgramSeriesInfo(ProgramSeriesInfo seriesInfo) {
                Log.i("ProgrameSeries","onGetProgramSeriesInfo-->"+seriesInfo.toString());
                if (seriesInfo != null) {
                    headPlayerView.setProgramSeriesInfo(seriesInfo);
                }
            }

            @Override
            public void onChange(int index,boolean fromClick) {
                Log.i("ProgrameSeries","onChange-->"+index);
                headPlayerView.Play(index, 0, fromClick);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unSubscribe();

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
            BitmapUtil.recycleImageBitmap(viewGroup);
        }
        if (fragment != null) {
            if (transaction != null) {
                transaction.remove(fragment);
                transaction = null;
            }
        }
        mPaiseView = null;
        fragment = null;
        frameLayout = null;
        // UsefulBitmapFactory.recycle();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return super.dispatchKeyEvent(event);
            }
        }


        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && headPlayerView != null &&
                headPlayerView.hasFocus()) {
            if (event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                return true;
            }
        }
        if (videoType()) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {

                ViewGroup viewGroup = findViewById(R.id.root_view);
                if (viewGroup == null) {
                    return super.dispatchKeyEvent(event);
                }
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
        }else{
            Log.i(TAG,"走Fragment的按键操作");
            if(fragment != null && fragment.interruptKeyEvent(event)){
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (videoType()) {
            if (headPlayerView != null) {
                headPlayerView.onActivityPause();
            }
        }
    }

    @Override
    protected void onStop() {
        if (videoType()) {
            if (headPlayerView != null) {
                headPlayerView.onActivityStop();
            }
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoType()) {
            if (headPlayerView != null) {
                headPlayerView.onActivityResume();
            }
        }
    }

    private boolean videoType(){
        if(!TextUtils.isEmpty(videoType) && (TextUtils.equals(videoType,"电视剧")
                || TextUtils.equals(videoType,"动漫"))){
            return false;
        }
        return true;
    }

    private void initFragment() {
        frameLayout = ((FrameLayout) findViewById(R.id.details_content));
        transaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            transaction.replace(R.id.details_content, fragment);
            transaction.commitAllowingStateLoss();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("content_uuid", contentUUID);
        super.onSaveInstanceState(outState);
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
    public void onBackPressed() {
        if (isADEntry) {
            startActivity(new Intent(ProgrameSeriesAndVarietyDetailActivity.this, MainActivity.class));
            isADEntry = false;
        }
        finish();
    }
}
