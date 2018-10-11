package tv.newtv.cboxtv.player.view;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tv.newtv.cboxtv.player.ActivityStacks;

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
    private FrameLayout mContainer;

    private ViewTreeObserver.OnScrollChangedListener mScrollChangeListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            resetLocation();
        }
    };

    private PlayerLocation() {
    }

    public static PlayerLocation build(NewTVLauncherPlayerView playerView, boolean bringFront) {
        return new PlayerLocation().attach(playerView, bringFront);
    }

    private PlayerLocation attach(NewTVLauncherPlayerView playerView, boolean bringFront) {
        mPlayerView = playerView;
        mBringToFront = bringFront;
        Activity activity = ActivityStacks.get().getCurrentActivity();
        mContainer = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        resetLocation();
        activity.getWindow().getDecorView().getViewTreeObserver().addOnScrollChangedListener(mScrollChangeListener);
        return this;
    }

    public void destroy() {
        Activity activity = ActivityStacks.get().getCurrentActivity();
        activity.getWindow().getDecorView().getViewTreeObserver().removeOnScrollChangedListener
                (mScrollChangeListener);
        mPlayerView = null;
        mContainer = null;
        mScrollChangeListener = null;
    }

    private void resetLocation() {
        if (mPlayerView == null || mContainer == null) return;
        Rect rect = new Rect();
        if (!mPlayerView.getLocalVisibleRect(rect)) return;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContainer
                .getLayoutParams();
        int[] location = new int[2];
        mPlayerView.getLocationInWindow(location);
        if(location[0] == 0 && location[1] == 0) return;
        Log.e(TAG, String.format("resetLocation: rect[ left=%d right=%d ]",location[0],
                location[1]));
        layoutParams.leftMargin = -location[0];
        layoutParams.topMargin = -location[1];
        mContainer.setLayoutParams(layoutParams);
    }
}
