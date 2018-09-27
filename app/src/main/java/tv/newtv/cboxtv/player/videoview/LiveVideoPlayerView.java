package tv.newtv.cboxtv.player.videoview;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.LinkedHashMap;

import tv.icntv.been.IcntvPlayerInfo;
import tv.icntv.icntvplayersdk.IcntvLive;
import tv.icntv.icntvplayersdk.iICntvPlayInterface;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.special.fragment.BallPlayerFragment;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.player.videoview
 * 创建事件:         17:22
 * 创建人:           weihaichao
 * 创建日期:          2018/4/27
 */
public class LiveVideoPlayerView extends FrameLayout {

    private IcntvLive icntvLive;
    private FrameLayout container;

    public LiveVideoPlayerView(@NonNull Context context) {
        super(context);
        initalize();
    }

    public LiveVideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initalize();
    }

    public LiveVideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int
            defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize();
    }

    private void showProgress(){
        Animation animation = AnimationUtils.loadAnimation(LauncherApplication.AppContext,R.anim
                .rotate_animation);
    }

    private void cancelProgress(){

    }

    public void play(Activity activity, String contentUUID, String url) {

        IcntvPlayerInfo icntvPlayerInfo = new IcntvPlayerInfo();
        icntvPlayerInfo.setAppKey(Constant.APP_KEY);
        icntvPlayerInfo.setChannalId(Constant.CHANNEL_ID);
        icntvPlayerInfo.setCdnDispatchUrl(Constant.BASE_URL_CDN);
        icntvPlayerInfo.setDynamicKeyUrl(Constant.DYNAMIC_KEY);
        icntvPlayerInfo.setProgramID(contentUUID);
        icntvPlayerInfo.setPlayUrl(url);
        icntvPlayerInfo.setDeviceID(Constant.UUID);
        icntvLive = new IcntvLive(activity, container,
                icntvPlayerInfo, new iICntvPlayInterface() {
            @Override
            public void onPrepared(LinkedHashMap<String, String> linkedHashMap) {
                Log.e(BallPlayerFragment.class.getSimpleName(), "onPrepared = " + linkedHashMap
                        .toString());
            }

            @Override
            public void onCompletion(int type) {
                Log.e(BallPlayerFragment.class.getSimpleName(), "onCompletion()");
            }

            @Override
            public void onBufferStart(String s) {
                Log.e(BallPlayerFragment.class.getSimpleName(), "onBufferStart = " + s);
                showProgress();
            }

            @Override
            public void onBufferEnd(String s) {
                Log.e(BallPlayerFragment.class.getSimpleName(), "onBufferEnd = " + s);
                cancelProgress();
            }

            @Override
            public void onError(int i, int i1, String s) {
                Log.e(BallPlayerFragment.class.getSimpleName(), "onCompletion()");
            }

            @Override
            public void onTimeout(int i) {
                Log.e(BallPlayerFragment.class.getSimpleName(), "onTimeout()" + i);
            }
        });
    }

    private void initalize() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams
                .MATCH_PARENT);

        container = new FrameLayout(getContext());
        container.setLayoutParams(layoutParams);
        addView(container,0);

        ProgressView progressView = new ProgressView(getContext());

        progressView.setLayoutParams(layoutParams);
        addView(progressView,layoutParams);
    }
}
