package tv.newtv.cboxtv.cms.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.newtv.cms.CmsErrorCode;
import com.newtv.cms.bean.Alternate;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.JumpScreen;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.AlternateCallback;
import tv.newtv.cboxtv.player.LifeCallback;
import tv.newtv.cboxtv.views.detail.AlterHeaderView;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeAdView;
import tv.newtv.cboxtv.views.detail.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.onEpisodeItemClick;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         12:50
 * 创建人:           weihaichao
 * 创建日期:          2018/11/13
 */
public class AlternateActivity extends DetailPageActivity implements
        AlternateCallback, onEpisodeItemClick<Alternate>, ContentContract.LoadingView,LifeCallback {
    private String contentUUID;
    private SmoothScrollView scrollView;
    private AlterHeaderView headerView;
    private EpisodeHorizontalListView mPlayListView;

    private String currentUUID = "";
    private long currentRequest = 0L;
    private ContentContract.Presenter mPresenter;

    @Override
    public void prepareMediaPlayer() {
        super.prepareMediaPlayer();

        if (headerView != null) {
            headerView.prepareMediaPlayer();
        }
    }

    @Override
    protected boolean isDetail() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (headerView != null) {
            headerView.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (headerView != null) {
            headerView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }

        headerView = null;
    }

    @Override
    public boolean hasPlayer() {
        return true;
    }

    @Override
    protected void buildView(@Nullable Bundle savedInstanceState, String contentID) {
        setContentView(R.layout.activity_alternate_layout);
        contentUUID = contentID;

        if (TextUtils.isEmpty(contentUUID)) {
            ToastUtil.showToast(getApplicationContext(), "节目ID为空");
            finish();
            return;
        }
        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "0," + contentUUID);
        setUp();
    }

    @Override
    protected boolean interruptDetailPageKeyEvent(KeyEvent event) {
        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && headerView != null &&
                headerView.isFullScreen()) {
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
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setUp() {
        scrollView = findViewById(R.id.root_view);
        headerView = findViewById(R.id.header_view);
        mPlayListView = findViewById(R.id.play_list);
        mPlayListView.setOnItemClick(this);

        headerView.setCallback(this);
        headerView.setLifeCallback(this);
        headerView.setContentUUID(contentUUID);

        mPlayListView.setContentUUID(contentUUID);
    }

    @Override
    public void onAlternateResult(String alternateId, @org.jetbrains.annotations.Nullable List<Alternate> result) {
        if(!TextUtils.equals(alternateId,contentUUID)) return;

        ADConfig.getInstance().setCarousel(alternateId);
        if (mPlayListView != null) {
            mPlayListView.onAlternateResult(alternateId, result);
        }

        EpisodeAdView adView = findViewById(R.id.ad_view);
        if (adView != null) {
            adView.requestAD();
        }
    }

    @Override
    public void onPlayerRelease() {

    }

    @Override
    public void onLifeError(String code, String desc) {
        if (CmsErrorCode.ALTERNATE_ERROR_PLAYLIST_EMPTY.equals(code) || CmsErrorCode
                .CMS_NO_ONLINE_CONTENT.equals(code)) {
            if (fromOuter){
                ToastUtil.showToast(getApplicationContext(), "节目走丢了，即将进入应用首页");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("action", "");
                intent.putExtra("params", "");
                startActivity(intent);
            }else {
                ToastUtil.showToast(getApplicationContext(), "节目走丢了，即将返回");
            }
            finish();
        }
    }

    @Override
    public void onPlayIndexChange(int index) {
        if (mPlayListView != null) {
            mPlayListView.setCurrentPlay(index);
        }
    }

    @Override
    public boolean onItemClick(int position, Alternate data) {
        if (Constant.CONTENTTYPE_PG.equals(data.getContentType())) {
            JumpScreen.jumpActivity(getApplicationContext(), data.getContentID(), Constant
                    .OPEN_DETAILS, Constant.CONTENTTYPE_PG);
        } else {
            if (mPresenter == null) {
                mPresenter = new ContentContract.ContentPresenter(getApplicationContext(), this);
            }
            if (currentRequest != 0L) {
                mPresenter.cancel(currentRequest);
                currentRequest = 0L;
            }
            currentUUID = data.getContentID();
            currentRequest = mPresenter.getContent(data.getContentID(), false);
        }
        return true;
    }

    private String getFormatId(String ids) {
        String[] idArr = ids.split("\\|");
        if (idArr.length >= 1) {
            return idArr[0];
        }
        return "";
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (content != null && TextUtils.equals(currentUUID, uuid)) {
            String tvIds = content.getTvContentIDs();//电视栏目ID
            String csIds = content.getCsContentIDs();//节目集ID
            String cgIds = content.getCgContentIDs();//节目合集ID
            if (!TextUtils.isEmpty(tvIds)) {
                //打开电视栏目详情页
                JumpScreen.jumpActivity(getApplicationContext(),
                        getFormatId(tvIds),
                        Constant.OPEN_DETAILS,
                        Constant.CONTENTTYPE_TV, content.getContentUUID());
            } else if (!TextUtils.isEmpty(csIds)) {
                //打开节目集详情页
                JumpScreen.jumpActivity(getApplicationContext(),
                        getFormatId(csIds),
                        Constant.OPEN_DETAILS,
                        Constant.CONTENTTYPE_PS, content.getContentUUID());
            } else if (!TextUtils.isEmpty(cgIds)) {
                //打开节目合集详情页
                JumpScreen.jumpActivity(getApplicationContext(),
                        getFormatId(cgIds),
                        Constant.OPEN_DETAILS,
                        Constant.CONTENTTYPE_CG, content.getContentUUID());
            }
            currentRequest = 0L;
            currentUUID = "";
        }
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @org.jetbrains.annotations.Nullable
            ArrayList<SubContent> result) {


    }

    @Override
    public void onLoading() {

    }

    @Override
    public void loadComplete() {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @org.jetbrains
            .annotations.Nullable String desc) {

    }
}
