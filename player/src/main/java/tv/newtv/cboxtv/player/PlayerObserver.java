package tv.newtv.cboxtv.player;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         15:05
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public interface PlayerObserver {
    void onFinish(ProgramSeriesInfo playInfo, int index, int position);
    void onExitApp();
}
