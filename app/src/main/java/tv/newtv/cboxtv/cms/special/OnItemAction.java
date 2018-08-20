package tv.newtv.cboxtv.cms.special;

import android.view.View;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special
 * 创建事件:         09:58
 * 创建人:           weihaichao
 * 创建日期:          2018/4/29
 */
public interface OnItemAction<T> {
    void onItemFocus(View item);
    void onItemClick(T item,int index);
    void onItemChange(int before,int current);
}
