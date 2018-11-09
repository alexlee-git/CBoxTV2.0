package tv.newtv.cboxtv.uc.v2.listener;

import android.os.Bundle;

/**
 * 项目名称： CBoxTV2.0
 * 类描述：会员状态回调接口
 * 创建人：wqs
 * 创建时间： 2018/9/29 17:40
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public interface INotifyMemberStatusCallback {
    //    /**
//     * 会员状态分三种类型
//     *
//     * @param status     member_open_not：非会员
//     *                   member_open_lose：是会员，会员已失效
//     *                   member_open_good、是会员，会员有效
//     * @param expireTime 会员有效期时间
//     */
//    void notifyLoginStatusCallback(String status, String expireTime);

    /**
     * 会员状态分三种类型
     *
     * @param status       member_open_not：非会员
     *                     member_open_lose：是会员，会员已失效
     *                     member_open_good、是会员，会员有效
     * @param memberBundle 会员信息数据bundle
     */
    void notifyLoginStatusCallback(String status, Bundle memberBundle);
}
