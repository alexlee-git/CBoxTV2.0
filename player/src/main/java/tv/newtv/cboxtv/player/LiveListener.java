package tv.newtv.cboxtv.player;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         20:29
 * 创建人:           weihaichao
 * 创建日期:          2018/11/5
 */
public interface LiveListener {
    void onTimeChange(String current,String end);
    void onComplete();
}
