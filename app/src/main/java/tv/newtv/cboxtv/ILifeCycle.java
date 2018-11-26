package tv.newtv.cboxtv;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         14:06
 * 创建人:           weihaichao
 * 创建日期:          2018/11/21
 */
public interface ILifeCycle {
    void onActivityStop();
    void onActivityResume();
    void onActivityPause();
    void onActivityDestroy();
}
