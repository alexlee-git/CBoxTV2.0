package tv.newtv.cboxtv.cms.mainPage.viewholder;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.viewholder
 * 创建事件:         14:09
 * 创建人:           weihaichao
 * 创建日期:          2018/11/22
 */
public interface IPageChange {
    void onVisibleChange(boolean show);
    void onDestroy();
    void onRecycleItemVisibleChange(int first,int last);
}
