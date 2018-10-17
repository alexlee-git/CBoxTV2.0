package tv.newtv.cboxtv;

import android.view.View;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         17:50
 * 创建人:           weihaichao
 * 创建日期:          2018/6/14
 */
public abstract class MultipleClickListener implements View.OnClickListener {

    private static final int DEFAULT_CLICK_DURATION = 3000;
    private long lastClickTime;

    protected abstract void onMultipleClick(View view);

    @Override
    public void onClick(View view) {
        long curClickTime = System.currentTimeMillis();
        if((curClickTime - lastClickTime) >= DEFAULT_CLICK_DURATION) {
            lastClickTime = curClickTime;
            onMultipleClick(view);
        }
    }
}
