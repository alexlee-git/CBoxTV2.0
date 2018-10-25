package tv.newtv.cboxtv.views.detail;

import android.view.KeyEvent;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         19:20
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public interface IEpisode {
    String getContentUUID();
    boolean interruptKeyEvent(KeyEvent event);
    void destroy();
}
