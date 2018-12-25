package tv.newtv.cboxtv.player;

import com.newtv.cms.bean.Alternate;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         19:55
 * 创建人:           weihaichao
 * 创建日期:          2018/11/30
 */
public interface AlternateCallback {
    void onAlternateResult(String alternateId, @Nullable List<Alternate> result);
    void onError(String code,String desc);
    void onPlayIndexChange(int index);
}
