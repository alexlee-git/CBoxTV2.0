package tv.newtv.cboxtv.cms.search.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.libs.Constant;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;


/**
 * 项目名称： NewTVLauncher
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/17 0017 20:31
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchRecyclerView extends RecyclerView {
    private static final String TAG = "SearchRecyclerview";

    public SearchRecyclerView(Context context) {
        super(context);
    }

    public SearchRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

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
                    case KeyEvent.KEYCODE_DPAD_RIGHT:

                        View rightView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_RIGHT);
                        Log.i(TAG, "rightView is null:" + (rightView == null));
                        if (rightView != null) {
                            rightView.requestFocus();
                            return true;
                        } else {
//                            this.smoothScrollBy(dx, 0);
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        View leftView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_LEFT);
                        Log.i(TAG, "leftView is null:" + (leftView == null));
                        if (leftView != null) {
                            leftView.requestFocus();
                            return true;
                        } else {
//                            this.smoothScrollBy(-dx, 0);
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                            return true;
                        } else {


                            int offsetY = getResources().getDimensionPixelSize(R.dimen.width_12px);
                            this.smoothScrollBy(0, focusView.getHeight()-offsetY);

                            if (!(event instanceof MyKeyEvent)){
                                postDispatch(event);
                            }
//                            this.smoothScrollBy(0, focusView.getHeight());
                            return true;
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
                                    return true;
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


    private void postDispatch(final KeyEvent event){
        MainLooper.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                dispatchKeyEvent(new MyKeyEvent(event));
            }
        },100);
    }

    public class MyKeyEvent extends KeyEvent{

        public MyKeyEvent(KeyEvent origEvent) {
            super(origEvent);
        }
    }

}
