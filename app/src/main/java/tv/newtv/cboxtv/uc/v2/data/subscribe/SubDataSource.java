package tv.newtv.cboxtv.uc.v2.data.subscribe;

import android.support.annotation.NonNull;

import java.util.List;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;


/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/27 0021
 */
public interface SubDataSource {
    interface GetSubscribeListCallback {

        void onSubscribeListLoaded(List<UserCenterPageBean.Bean> subList, final int totalSize);

        void onDataNotAvailable();
    }

    interface AddRemoteSubscribeListCallback {

        void onAddRemoteSubscribeListComplete(int totalSize);

    }

    interface GetSubscribeCallback {

        void onSubscribeLoaded(UserCenterPageBean.Bean bean);

        void onDataNotAvailable();
    }

    void addRemoteSubscribe(@NonNull UserCenterPageBean.Bean bean);

    void addRemoteSubscribeList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, SubRemoteDataSource.AddRemoteSubscribeListCallback callback);

    void deleteRemoteSubscribe(@NonNull UserCenterPageBean.Bean bean);

    void getRemoteSubscribeList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetSubscribeListCallback callback);

    //释放资源
    void releaseSubscribeResource();
}
