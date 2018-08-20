package tv.newtv.cboxtv.uc.db;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db
 * 创建事件:         15:04
 * 创建人:           weihaichao
 * 创建日期:          2018/2/24
 */

public interface DBCallback<T> {
    void onResult(int code, T result);
}
