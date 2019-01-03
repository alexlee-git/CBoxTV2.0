package tv.newtv.cboxtv.player.view;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

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
    private static PlayerLocation instance;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean mBringToFront = false;
    private NewTVLauncherPlayerView mPlayerView;
    private ViewTreeObserver.OnGlobalLayoutListener mScrollChangeListener = new ViewTreeObserver
            .OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            resetLocation();
        }
    };

    private PlayerLocation() {
    }

    public static PlayerLocation get() {
        if (instance == null) {
            synchronized (PlayerLocation.class) {
                if (instance == null) instance = new PlayerLocation();
            }
        }
        return instance;
    }

    void attach(NewTVLauncherPlayerView playerView, boolean bringFront) {
        mPlayerView = playerView;
        mBringToFront = bringFront;
        Activity activity = Player.get().getCurrentActivity();
        Log.e(TAG, "attach listen ->" + activity);
        resetLocation();
        if (activity != null) {
            activity.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener
                    (mScrollChangeListener);
        }
    }

    void destroy() {
        Activity activity = Player.get().getCurrentActivity();
        Log.e(TAG, "detach listen ->" + activity);
        if (activity != null) {
            activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener
                    (mScrollChangeListener);
        }

        mPlayerView = null;
    }

    private void resetLocation() {
        if (mPlayerView == null) return;
        Rect rect = new Rect();
        if (!mPlayerView.getLocalVisibleRect(rect)) return;
        Activity activity = Player.get().getCurrentActivity();
        if (activity == null) {
            return;
        }
        final ViewGroup mContainer = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id
                .content);
        final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mContainer
                .getLayoutParams();
        int[] location = new int[2];
        mPlayerView.getLocationInWindow(location);
        Log.e(TAG, "reset location ->" + activity);
        Log.e(TAG, String.format("resetLocation: rect[ left=%d right=%d width=%d height=%d ]",
                location[0], location[1],mContainer.getMeasuredWidth(),mContainer.getMeasuredHeight()));
        if (location[0] == 0 && location[1] == 0) return;
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
