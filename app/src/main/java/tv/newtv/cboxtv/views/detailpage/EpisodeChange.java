package tv.newtv.cboxtv.views.detailpage;

import tv.newtv.cboxtv.views.CurrentPlayImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         14:38
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public interface EpisodeChange {
    void onChange(CurrentPlayImageView imageView,int index,boolean fromClick);
}
