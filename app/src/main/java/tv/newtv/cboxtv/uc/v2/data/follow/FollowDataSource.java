package tv.newtv.cboxtv.uc.v2.data.follow;

import android.support.annotation.NonNull;

import java.util.List;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;


/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/26 0021
 */
public interface FollowDataSource {
    interface GetFollowListCallback {

        void onFollowListLoaded(List<UserCenterPageBean.Bean> FollowList, final int totalSize);

        void onDataNotAvailable();
    }

    interface GetFollowCallback {

        void onFollowLoaded(UserCenterPageBean.Bean Collect);

        void onDataNotAvailable();
    }

    void addRemoteFollow(@NonNull UserCenterPageBean.Bean bean);

    void deleteRemoteFollow(@NonNull UserCenterPageBean.Bean bean);

    void getRemoteFollowList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetFollowListCallback callback);

    //释放资源
    void releaseFollowResource();
}
