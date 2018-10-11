package tv.newtv.cboxtv.menu;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.libs.MainLooper;
import com.newtv.libs.util.SystemUtils;


/**
 * Created by TCP on 2018/4/19.
 */

public class MenuRecyclerView extends RecyclerView{
    public static final int MAX_LEVEL = Integer.MAX_VALUE;
    private int level;
    private OnKeyEvent keyEventListener;

    public MenuRecyclerView(Context context) {
        this(context,null);
    }

    public MenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MenuRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    interface OnKeyEvent{
        /**
         *
         * @param level MenuRecyclerView的level
         * @param keyCode
         * @param focusViewPositionInAdapter 当前焦点所在adapter中的位置
         */
        void keyEvent(int level, int keyCode, int focusViewPositionInAdapter, View focusView);
    }

    public void setKeyEvent(OnKeyEvent keyEventListener) {
        this.keyEventListener = keyEventListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if(SystemUtils.isFastDoubleClick(100)){
            return true;
        }

        if(getParent() instanceof MenuGroup){
            MenuGroup menuGroup = (MenuGroup) getParent();
            if(!menuGroup.isFinshAnim()){
                return false;
            }
        }

        boolean result = super.dispatchKeyEvent(event);
        View focusView = this.getFocusedChild();
        if (focusView == null) {
            return result;
        } else {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            } else {
                int position = getChildAdapterPosition(focusView);
                int dy = 0;
                int dx = 0;
                if (getChildCount() > 0) {
                    View firstView = this.getChildAt(0);
                    dy = firstView.getHeight();
                    dx = firstView.getWidth();
                }
                switch (event.getKeyCode()){
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if(level == 0){
                            return true;
                        }else{
                            keyEventListener.keyEvent(level,KeyEvent.KEYCODE_DPAD_LEFT,position,focusView);
                            return true;
                        }
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if(level != MAX_LEVEL){
                            keyEventListener.keyEvent(level,KeyEvent.KEYCODE_DPAD_RIGHT,position,focusView);
                        }
                        return true;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        View upView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_UP);
                        if (upView != null) {
                            upView.requestFocus();
                            if(level != MAX_LEVEL){//最后一级不需要刷新数据
                                keyEventListener.keyEvent(level,KeyEvent.KEYCODE_DPAD_UP,position,focusView);
                            }
                        }else {
                            this.smoothScrollBy(0, -dy);
                            if(!(event instanceof MyKeyEvent)){
                                postDispatch(event);
                            }
                            return true;
                        }
                        return true;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        View downView = FocusFinder.getInstance().findNextFocus(this, focusView, View.FOCUS_DOWN);
                        if (downView != null) {
                            downView.requestFocus();
                            if(level != MAX_LEVEL){//最后一级不需要刷新数据
                                keyEventListener.keyEvent(level,KeyEvent.KEYCODE_DPAD_DOWN,position,focusView);
                            }
                        }else {
                            this.smoothScrollBy(0, dy);
                            if(!(event instanceof MyKeyEvent)){
                                postDispatch(event);
                            }
                            return true;
                        }
                        return true;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if(level == MAX_LEVEL){
                            keyEventListener.keyEvent(level,KeyEvent.KEYCODE_DPAD_CENTER,position,focusView);
                        }
                        break;
                }
            }
        }
        return false;
    }

    private void postDispatch(final KeyEvent event){
        MainLooper.get().postSingleDelayed(new Runnable() {
            @Override
            public void run() {
                dispatchKeyEvent(new MyKeyEvent(event));
            }
        },100);
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public class MyKeyEvent extends KeyEvent{

        public MyKeyEvent(KeyEvent origEvent) {
            super(origEvent);
        }
    }

}
