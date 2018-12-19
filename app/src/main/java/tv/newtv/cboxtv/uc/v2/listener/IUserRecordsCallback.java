package tv.newtv.cboxtv.uc.v2.listener;

import java.util.List;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;

/**
 * 项目名称:         工委会Launcher
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午12:07
 * 创建人:           lixin
 * 创建日期:         2018/12/14
 */


public interface IUserRecordsCallback {
    void notifyUserRecords(List<UserCenterPageBean.Bean> datas);
}
