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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.views.custom.DivergeView;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;

import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ToastUtil;

import tv.newtv.cboxtv.views.detail.EpisodeHelper;
import tv.newtv.cboxtv.views.detail.EpisodePageView;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;
import tv.newtv.cboxtv.views.detail.IEpisode;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;


/**
 * Created by gaoleichao on 2018/4/28.
 */

public class ProgrameSeriesAndVarietyDetailActivity extends BaseActivity implements
        ContentContract.View {

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

    @Override
    protected void FocusToTop() {
        Toast.makeText(getApplicationContext(), "ProgrameSeriesAndVarietyDetailActivity 到顶了",
                Toast.LENGTH_LONG).show();
    }

    private ContentContract.Presenter mContentPresenter;

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
        } else {
            contentUUID = savedInstanceState.getString("content_uuid");
        }

        contentUUID = "4329022";

        if (!TextUtils.isEmpty(contentUUID) && contentUUID.length() >= 2) {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "0," + contentUUID);

            mContentPresenter = new ContentContract.ContentPresenter(getApplicationContext(),this);
            mContentPresenter.getContent(contentUUID);
        } else {
            onError(getApplicationContext(),"节目集信息有误");
        }
    }

    Content pageContent;


    private void initView() {
        playListView = findViewById(R.id.play_list);
        scrollView = findViewById(R.id.root_view);
        final SuggestView suggestView = findViewById(R.id.suggest);

        headPlayerView = ((HeadPlayerView) findViewById(R.id.header_video));
        headPlayerView.Build(HeadPlayerView.Builder.build(R.layout.variety_item_head)
                .CheckFromDB(new HeadPlayerView.CustomFrame(R.id.collect, HeadPlayerView.Builder
                        .DB_TYPE_COLLECT))
                .SetPlayerId(R.id.video_container)
                .SetDefaultFocusID(R.id.full_screen)
                .SetClickableIds(R.id.full_screen, R.id.add)
                .SetContentUUID(contentUUID)
                .SetOnInfoResult(new HeadPlayerView.InfoResult() {
                    @Override
                    public void onResult(Content info) {
                        pageContent = info;
                        suggestView.setContentUUID(SuggestView.TYPE_COLUMN_SEARCH, info,null);
                        playListView.setContentUUID(EpisodeHelper.TYPE_VARIETY_SHOW,
                                getSupportFragmentManager(),
                                contentUUID, null);
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
                            videoPlayerView.EnterFullScreen(ProgrameSeriesAndVarietyDetailActivity
                                    .this, false);
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
                                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                                    headPlayerView.EnterFullScreen
                                            (ProgrameSeriesAndVarietyDetailActivity.this);
                                }

                                break;
                        }
                    }
                }));


        playListView.setOnEpisodeChange(new EpisodePageView.OnEpisodeChange() {
            @Override
            public void onGetProgramSeriesInfo(List<SubContent> seriesInfo) {
                if (seriesInfo != null) {
                    ArrayList<SubContent> contents = new ArrayList<>(seriesInfo);
                    pageContent.setData(contents);
                    headPlayerView.setProgramSeriesInfo(pageContent);
                }
            }

            @Override
            public void onChange(int index,boolean fromClick) {
                headPlayerView.Play(index, 0, fromClick);
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mContentPresenter != null){
            mContentPresenter.destroy();
            mContentPresenter = null;
        }

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
        finish();
    }

    @Override
    public void onContentResult(@Nullable Content content) {
        if (content != null) {
            videoType = content.getVideoType();
        }
        //这里节目详情页 要换成电视剧
        if (!videoType()) {
            setContentView(R.layout.activity_details_programe_series);
            fragment = new ProgrameSeriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("content_uuid", contentUUID);
            fragment.setArguments(bundle);
            initFragment();
        } else {
            setContentView(R.layout.fragment_new_variety_show);
            ADConfig.getInstance().setSeriesID(contentUUID);
            initView();
        }
    }

    @Override
    public void onSubContentResult(@Nullable ArrayList<SubContent> result) {

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
}
