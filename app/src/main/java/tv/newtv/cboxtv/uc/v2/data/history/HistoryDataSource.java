package tv.newtv.cboxtv.uc.v2.data.history;

import android.support.annotation.NonNull;

import java.util.List;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.data.History;


/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/21 0021
 */
public interface HistoryDataSource {
    interface GetHistoryListCallback {

        void onHistoryListLoaded(List<UserCenterPageBean.Bean> historyList, final int totalSize);

        void onError(String error);

    }

    interface AddRemoteHistoryListCallback {

        void onAddRemoteHistoryListComplete(int totalSize);

    }

    interface GetHistoryCallback {

        void onHistoryLoaded(History history);

        void onDataNotAvailable();
    }

    void addRemoteHistory(@NonNull UserCenterPageBean.Bean entity);

    void addRemoteHistoryList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, HistoryRemoteDataSource.AddRemoteHistoryListCallback callback);

    void deleteRemoteHistory(String token, @NonNull String userId, String contentType, String appKey, String channelCode, String contentuuids);

    void getRemoteHistoryList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetHistoryListCallback callback);

    //释放资源
    void releaseHistoryResource();
}
