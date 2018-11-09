package tv.newtv.cboxtv.uc.v2.member;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;


/**
 * 项目名称： CBoxTV2.0
 * 类描述：用于会员中心滑动
 * 创建人：wqs
 * 创建时间： 2018/9/11 0011 18:06
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class MemberCenterRecyclerView extends RecyclerView {
    private static final String TAG = "SearchRecyclerview";
    private int event_code;

    public MemberCenterRecyclerView(Context context) {
        super(context);
    }

    public MemberCenterRecyclerView(Context context, @Nullable AttributeSet attrs) {
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
                Log.e(TAG, "view tag : " + cellCode + ", height : " + focusView.getHeight());

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
                        event_code = 0;
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        Log.i(TAG, " downView is null:" + (downView == null));
                        if (downView != null) {
                            downView.requestFocus();
                            return true;
                        } else {
                            this.smoothScrollBy(0, focusView.getHeight());
                            event_code = 1;
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_UP:
                        event_code = 0;
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
                                event_code = 2;
//                                View view = getChildAt(0);
//                                if (view == null) {
//                                    Log.e(TAG, "view is null");
//                                } else {
//                                    Log.e(TAG, "view is not null");
//                                    return true;
//                                }
//                                return false;
                                return true;
                            }

                        }
                    case KeyEvent.KEYCODE_BACK:
                        return super.dispatchKeyEvent(event);
                }

            }

        }
        return result;
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE) {
            if (event_code == 1) {
                View focusView = this.findFocus();
                View downView2 = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                Log.i(TAG, " event_code == 1:downView2 is null:" + (downView2 == null));
                if (downView2 != null) {
                    downView2.requestFocus();
                }
            } else if (event_code == 2) {
                View focusView = this.findFocus();
                View downView2 = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                Log.i(TAG, " event_code == 2:downView2 is null:" + (downView2 == null));
                if (downView2 != null) {
                    downView2.requestFocus();
                }
            } else if (event_code == 3) {

            }
        }
        super.onScrollStateChanged(state);
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
        if (position == 0) {
            Log.d(TAG, "---smoothScrollToPosition:position:" + position);
            event_code = 0;
        }
    }
    public void setFocusStatus(boolean status) {
        //如果其他控件获取焦点，设置此属性，防止焦点错乱
        if (!status) {
            event_code = 0;
        }
    }

}
