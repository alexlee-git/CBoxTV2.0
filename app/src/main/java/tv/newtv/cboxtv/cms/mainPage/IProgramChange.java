package tv.newtv.cboxtv.cms.mainPage;

import android.view.View;

import com.newtv.cms.bean.Program;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage
 * 创建事件:         13:52
 * 创建人:           weihaichao
 * 创建日期:          2018/11/20
 */
public interface IProgramChange {
    void onChange(Program data, int position);
}
