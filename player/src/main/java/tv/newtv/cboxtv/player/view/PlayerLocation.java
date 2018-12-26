package tv.newtv.cboxtv.player.view;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tv.newtv.cboxtv.player.Player;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         10:52
 * 创建人:           weihaichao
 * 创建日期:          2018/9/10
 */
class PlayerLocation {

    private static final String TAG = PlayerLocation.class.getCanonicalName();
    @SuppressWarnings("FieldCanBeLocal")
    private boolean mBringToFront = false;
    private NewTVLauncherPlayerView mPlayerView;
    private static PlayerLocation instance;

    private ViewTreeObserver.OnScrollChangedListener mScrollChangeListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            resetLocation();
        }
    };

    private PlayerLocation() { }

    public static PlayerLocation get() {
        if(instance == null){
            synchronized (PlayerLocation.class){
                if(instance == null) instance = new PlayerLocation();
            }
        }
        return instance;
    }

    void attach(NewTVLauncherPlayerView playerView, boolean bringFront) {
        mPlayerView = playerView;
        mBringToFront = bringFront;
        Activity activity = Player.get().getCurrentActivity();
        resetLocation();
        activity.getWindow().getDecorView().getViewTreeObserver().addOnScrollChangedListener(mScrollChangeListener);
    }

    void destroy() {
        Activity activity = Player.get().getCurrentActivity();
        activity.getWindow().getDecorView().getViewTreeObserver().removeOnScrollChangedListener
                (mScrollChangeListener);
        mPlayerView = null;
    }

    private void resetLocation() {
        if (mPlayerView == null) return;
        Rect rect = new Rect();
        if (!mPlayerView.getLocalVisibleRect(rect)) return;
        Activity activity = Player.get().getCurrentActivity();
        final ViewGroup mContainer = activity.getWindow().getDecorView().findViewById(android.R.id
                .content);
        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mContainer
                .getLayoutParams();
        int[] location = new int[2];
        mPlayerView.getLocationInWindow(location);
        if(location[0] == 0 && location[1] == 0) return;
        Log.e(TAG, String.format("resetLocation: rect[ left=%d right=%d ]",location[0],
                location[1]));
        layoutParams.leftMargin = -location[0];
        layoutParams.topMargin = -location[1];
        mContainer.post(new Runnable() {
            @Override
            public void run() {
                mContainer.setLayoutParams(layoutParams);
            }
        });

    }
}
