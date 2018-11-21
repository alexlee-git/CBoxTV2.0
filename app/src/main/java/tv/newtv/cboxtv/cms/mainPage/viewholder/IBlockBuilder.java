package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.view.View;
import android.view.ViewGroup;

import com.newtv.cms.bean.Page;

import org.jetbrains.annotations.NotNull;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.viewholder
 * 创建事件:         16:37
 * 创建人:           weihaichao
 * 创建日期:          2018/11/19
 */
public interface IBlockBuilder {
     UniversalViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType);
    void build(final Page moduleItem, View itemView, int position);
    void destroy();
    void setPlayerUUID(String id);
    void setPicassoTag(String id);
    void showFirstLineTitle(boolean show);
    int getItemViewType(int position, Page item);
}
