package tv.newtv.cboxtv.views.detail;


import tv.newtv.cboxtv.views.custom.CurrentPlayImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         14:38
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public interface EpisodeChange {
    void updateUI(IEpisodePlayChange playChange, int index);
    void onChange(IEpisodePlayChange playChange, int index, boolean fromClick);
}
