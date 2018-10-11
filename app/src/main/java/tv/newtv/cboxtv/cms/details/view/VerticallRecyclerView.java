package tv.newtv.cboxtv.cms.details.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.libs.Constant;


/**
 * Created by gaoleichao on 2018/4/24.
 */

public class VerticallRecyclerView extends RecyclerView {

    private static final String TAG = "VerticallRecyclerView";


    public VerticallRecyclerView(Context context) {
        super(context);
        setFocusable(false);
    }

    public VerticallRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
    }

    public VerticallRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (mInterceptLister != null && mInterceptLister.onIntercept(event)) {
//            return true;
//        }

        boolean result = super.dispatchKeyEvent(event);
        View focusView = this.findFocus();

        if (focusView == null) {
            return result;
        } else {
            int dy = 0;
            int dx = 0;
            if (getChildCount() > 0) {
                View firstView = this.getChildAt(0);
                dy = firstView.getHeight();
                dx = firstView.getWidth();
            }
            if (event.getAction() == KeyEvent.ACTION_UP) {
                // 放行back键
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return super.dispatchKeyEvent(event);
                }
                return true;
            } else {
                String cellCode = (String) focusView.getTag();
                Log.e(Constant.TAG, "view tag : " + cellCode + ", height : " + focusView.getHeight());

                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                            return true;
                        } else {
                            //this.smoothScrollBy(0, focusView.getHeight());
                            return super.dispatchKeyEvent(event);
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                        Log.i(TAG, "upView is null:" + (upView == null));
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            View view = getChildAt(0);
                            if (view != null && view.isFocused()) {
                                Log.i(TAG, "upView is null : " + "action up");
                            }
                            return super.dispatchKeyEvent(event);
                        } else {
                            if (upView != null) {
                                upView.requestFocus();
                                return true;
                            } else {
                                this.smoothScrollBy(0, -focusView.getHeight());

                                View view = getChildAt(0);
                                if (view == null) {
                                    Log.e(TAG, "view is null");
                                } else {
                                    Log.e(TAG, "view is not null");
                                    return false;
                                }
                                return false;
                            }

                        }
                    case KeyEvent.KEYCODE_BACK:
                        return super.dispatchKeyEvent(event);
                }
            }
        }
        return result;
    }
}
