package tv.newtv.cboxtv.cms.search.listener;

import android.view.View;

/**
 * 项目名称： NewTVLauncher
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/9 0009 18:15
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface OnGetSearchHotRecommendFocus {
    void notifySearchHotRecommendFocus(boolean focus, int position, View view);
}
