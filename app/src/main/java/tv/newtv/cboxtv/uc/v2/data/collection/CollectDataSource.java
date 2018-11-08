package tv.newtv.cboxtv.uc.v2.data.collection;

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
public interface CollectDataSource {
    interface GetCollectListCallback {

        void onCollectListLoaded(List<UserCenterPageBean.Bean> CollectList, final int totalSize);

        void onDataNotAvailable();
    }

    interface GetCollectCallback {

        void onCollectLoaded(UserCenterPageBean.Bean Collect);

        void onDataNotAvailable();
    }

    void addRemoteCollect(@NonNull UserCenterPageBean.Bean bean);

    void deleteRemoteCollect(@NonNull UserCenterPageBean.Bean bean);

    void getRemoteCollectList(String token, final String userId, String appKey, String channelCode, String offset, final String limit, @NonNull CollectRemoteDataSource.GetCollectListCallback callback);

}
