package tv.newtv.cboxtv.uc.listener;

import android.view.View;

/**
 * Created by gaoleichao on 2018/3/29.
 */

public interface OnRecycleItemClickListener<T> {
    void onItemClick(View view, int Position, T object);

    void onItemFocusChange(View view, boolean hasFocus, int Position, T object);
}
