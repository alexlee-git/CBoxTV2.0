package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Alternate;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.SystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.ActivityStacks;
import tv.newtv.cboxtv.DetailTextPopuView;
import tv.newtv.cboxtv.MultipleClickListener;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.AlternateCallback;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.views.custom.FocusToggleView2;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         14:21
 * 创建人:           weihaichao
 * 创建日期:          2018/11/13
 */
public class AlterHeaderView extends FrameLayout implements IEpisode, ContentContract.View,
        AlternateCallback, PlayerCallback {

    private Content mContent;
    private String mContentUUID;
    private ContentContract.Presenter mPresenter;

    private ViewGroup viewContainer;
    private TextView alternateIdText;
    private TextView alternateFromText;
    private TextView alternateDescText;
    private VideoPlayerView alternateView;
    private ViewStub mMoreView;

    private FocusToggleView2 mCollect;

    private View fullScreenBtn;

    private boolean mIsCollect = false;

    private NewTVLauncherPlayerView.PlayerViewConfig playerViewConfig;
    private AlternateCallback mAlternateCallback;

    public AlterHeaderView(Context context) {
        this(context, null);
    }

    public AlterHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlterHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void stop() {
        if (alternateView != null) {
            if (!alternateView.isReleased()) {
                playerViewConfig = alternateView.getDefaultConfig();
            }
            viewContainer.removeView(alternateView);
            alternateView.release();
            alternateView.destory();
            alternateView = null;
        }
    }

    public boolean isFullScreen() {
        if (alternateView != null) {
            alternateView.isFullScreen();
        }
        return false;
    }

    public void setCallback(AlternateCallback callback) {
        mAlternateCallback = callback;
    }

    public void prepareMediaPlayer() {
        if (alternateView != null && alternateView.isReleased()) {
            stop();
        }

        if (alternateView == null) {
            if (playerViewConfig != null) {
                alternateView = new VideoPlayerView(playerViewConfig, getContext());
            } else {
                alternateView = new VideoPlayerView(getContext());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams
                        .MATCH_PARENT, LayoutParams.MATCH_PARENT);
                alternateView.setLayoutParams(layoutParams);
                viewContainer.addView(alternateView, layoutParams);
            }

            alternateView.setPlayerCallback(this);
        }
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.alternate_head_layout, this, true);

        viewContainer = findViewById(R.id.video_container);
        alternateIdText = findViewById(R.id.id_detail_title);
        alternateFromText = findViewById(R.id.id_detail_from);
        alternateDescText = findViewById(R.id.id_detail_desc);
        alternateView = findViewById(R.id.video_player);
        mMoreView = findViewById(R.id.more_view_stub);

        mCollect = findViewById(R.id.collect);
        if (mCollect != null) {
            mCollect.setOnClickListener(new MultipleClickListener() {
                @Override
                protected void onMultipleClick(View view) {
                    onViewClick(view);
                }
            });
        }
        alternateView.setPlayerCallback(this);
        alternateView.setAlternateCallback(this);

        fullScreenBtn = findViewById(R.id.full_screen);

        fullScreenBtn.setOnClickListener(new MultipleClickListener() {
            @Override
            protected void onMultipleClick(View view) {
                onViewClick(view);
            }
        });


        mPresenter = new ContentContract.ContentPresenter(getContext(), this);

        updateUI();
    }

    @Override
    public String getContentUUID() {
        return mContentUUID;
    }

    public void setContentUUID(String contentUUID) {
        mContentUUID = contentUUID;

        checkIsRecord();
        if (mPresenter == null) {
            mPresenter = new ContentContract.ContentPresenter(getContext(), this);
        }
        mPresenter.getContent(contentUUID, false);
    }

    private void checkIsRecord() {
        DataSupport.search(DBConfig.LB_COLLECT_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENT_ID, mContentUUID)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            mIsCollect = !TextUtils.isEmpty(result);
                        } else {
                            mIsCollect = false;
                        }
                        updateUI();
                    }
                }).excute();
    }

    private void updateUI() {
        if (mCollect != null) {
            mCollect.setSelect(mIsCollect);
        }
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        View focusView = findFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (!hasFocus() && alternateView != null) {
                    alternateView.requestFocus();
                    return true;
                } else return hasFocus();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                return alternateView != null && alternateView.hasFocus();
            }if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_RIGHT);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
        mAlternateCallback = null;
        if (alternateView != null) {
            alternateView.destroy();
        }
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (content == null) {
            return;
        }

        mContent = content;

        if (alternateIdText != null) {
            alternateIdText.setText(String.format("%s %s", content.getAlternateNumber(), content
                    .getTitle()));
        }
        if (alternateFromText != null) {

        }

        if (alternateDescText != null) {
            alternateDescText.setText(content.getDescription().replace("\r\n", ""));
            int ellipsisCount = alternateDescText.getLayout().getEllipsisCount(alternateDescText.getLineCount
                    () - 1);
            if (ellipsisCount > 0 && mMoreView != null) {
                final View view = mMoreView.inflate();
                view.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            view.setBackgroundResource(R.drawable.more_hasfocus);
                        } else {
                            view.setBackgroundResource(R.drawable.more_nofocus);
                        }
                    }
                });

                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DetailTextPopuView navPopuView = new DetailTextPopuView();
                        navPopuView.showPopup(getContext(), getRootView(), content.getTitle(), content.getDescription());
                    }
                });
            }

        }

        if (alternateView != null) {
            alternateView.setSeriesInfo(content);
            alternateView.setAlternateCallback(this);
            alternateView.playAlternate(mContentUUID, content.getTitle(), content
                    .getAlternateNumber());
        }
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }


    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.full_screen:
                alternateView.enterFullScreen(ActivityStacks.get().getCurrentActivity());
                break;
            case R.id.collect:
                if (mContent == null) return;
                if (!mIsCollect) {
                    Bundle bundle = new Bundle();
                    bundle.putString(DBConfig.CONTENTUUID, mContent.getContentUUID());
                    bundle.putString(DBConfig.CONTENT_ID, mContent.getContentID());
                    bundle.putString(DBConfig.TITLE_NAME, mContent.getTitle());
                    bundle.putString(DBConfig.IS_FINISH, mContent.isFinish());
                    bundle.putString(DBConfig.REAL_EXCLUSIVE, mContent.getNew_realExclusive());
//                bundle.putString(DBConfig.ISSUE_DATE, lastNode.issuedate);
//                bundle.putString(DBConfig.LAST_PUBLISH_DATE, mContent.get);
                    bundle.putString(DBConfig.SUB_TITLE, mContent.getSubTitle());
                    bundle.putString(DBConfig.UPDATE_TIME, System.currentTimeMillis() + "");
                    bundle.putString(DBConfig.USERID, SystemUtils.getDeviceMac(getContext()));
                    bundle.putString(DBConfig.V_IMAGE, mContent.getVImage());
                    bundle.putString(DBConfig.H_IMAGE, mContent.getHImage());
                    bundle.putString(DBConfig.VIP_FLAG, mContent.getVipFlag());
                    bundle.putString(DBConfig.CONTENTTYPE, mContent.getContentType());
                    UserCenterRecordManager.getInstance().addRecord(UserCenterRecordManager
                                    .USER_CENTER_RECORD_TYPE.TYPE_LUNBO,
                            getContext(),
                            bundle,
                            mContent,
                            new DBCallback<String>() {
                                @Override
                                public void onResult(int code, String result) {
                                    if (code == 0) {
                                        Toast.makeText(getContext(), "收藏成功", Toast.LENGTH_SHORT)
                                                .show();

                                        mIsCollect = true;
                                        updateUI();
                                    }
                                }
                            });
                } else {
                    UserCenterRecordManager.getInstance().deleteRecord(UserCenterRecordManager
                                    .USER_CENTER_RECORD_TYPE.TYPE_LUNBO, getContext(),
                            getContentUUID(),
                            mContent.getContentType(),
                            SystemUtils.getDeviceMac(getContext()),
                            new DBCallback<String>() {
                                @Override
                                public void onResult(int code, String result) {
                                    if (code == 0) {
                                        Toast.makeText(getContext(), "取消收藏成功", Toast.LENGTH_SHORT)
                                                .show();
                                        mIsCollect = false;
                                        updateUI();
                                    }
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAlternateResult(@Nullable List<Alternate> result) {
        if (mAlternateCallback != null) {
            mAlternateCallback.onAlternateResult(result);
        }
    }

    @Override
    public void onPlayIndexChange(int index) {
        if (mAlternateCallback != null) {
            mAlternateCallback.onPlayIndexChange(index);
        }
    }

    @Override
    public void onEpisodeChange(int index, int position) {

    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoPlayerView.enterFullScreen(ActivityStacks.get().getCurrentActivity());
    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {

    }

    @Override
    public void ProgramChange() {
        if (TextUtils.isEmpty(mContentUUID)) return;
        if (mContent != null) {
            alternateView.playAlternate(mContentUUID, mContent.getTitle(), mContent
                    .getAlternateNumber());
        } else {
            setContentUUID(mContentUUID);
        }
    }

    public void onResume() {
        if(mContent != null && !TextUtils.isEmpty(mContentUUID)){
            onContentResult(mContentUUID,mContent);
        }
    }
}
