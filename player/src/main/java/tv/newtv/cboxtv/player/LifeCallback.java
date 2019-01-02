package tv.newtv.cboxtv.player;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         15:58
 * 创建人:           weihaichao
 * 创建日期:          2018/12/26
 */
public interface LifeCallback {
    void onPlayerRelease();
    void onLifeError(String code,String message);
}
