package tv.newtv.cboxtv.views.custom;

import tv.newtv.cboxtv.player.listener.ScreenListener;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.custom
 * 创建事件:         11:50
 * 创建人:           weihaichao
 * 创建日期:          2018/11/21
 */
public interface ICustomPlayer {
    void destroy();
    void onWindowVisibleChange(int visible);
    void attachScreenListener(ScreenListener listener);
    void detachScreenListener(ScreenListener listener);
}
