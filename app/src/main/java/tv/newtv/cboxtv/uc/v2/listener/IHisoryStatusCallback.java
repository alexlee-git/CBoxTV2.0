package tv.newtv.cboxtv.uc.v2.listener;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;

/**
 * 项目名称： CBoxTV2.0
 * 类描述：查询节目是否存在历史记录
 * 创建人：ywy
 * 创建时间： 2018/10/16
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface IHisoryStatusCallback {
     void getHistoryStatus(UserCenterPageBean.Bean bean);
}
