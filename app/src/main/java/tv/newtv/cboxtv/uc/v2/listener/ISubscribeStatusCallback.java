package tv.newtv.cboxtv.uc.v2.listener;

/**
 * 项目名称： CBoxTV2.0
 * 类描述：查询节目是否被订阅
 * 创建人：wqs
 * 创建时间： 2018/9/28 22:35
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface ISubscribeStatusCallback {
    void notifySubScribeStatus(boolean status,Long id);
}
