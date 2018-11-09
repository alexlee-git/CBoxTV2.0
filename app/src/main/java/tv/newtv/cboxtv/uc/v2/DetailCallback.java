package tv.newtv.cboxtv.uc.v2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:37
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
public interface DetailCallback<T> {
    void onResult(@NotNull List<T> results);
}
