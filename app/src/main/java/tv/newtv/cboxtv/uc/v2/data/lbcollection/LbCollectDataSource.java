package tv.newtv.cboxtv.uc.v2.data.lbcollection;

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
public interface LbCollectDataSource {
    interface GetLbCollectListCallback {

        void onLbCollectListLoaded(List<UserCenterPageBean.Bean> lbCollectList, final int totalSize);

        void onError(String error);

    }

    interface AddRemoteLbCollectListCallback {

        void onAddRemoteLbCollectListComplete(int totalSize);

    }

    interface GetLbCollectCallback {

        void onLbCollectLoaded(UserCenterPageBean.Bean Collect);

        void onDataNotAvailable();
    }

    void addRemoteLbCollect( @NonNull UserCenterPageBean.Bean bean);

    void addRemoteLbCollectList( String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteLbCollectListCallback callback);

    void deleteRemoteLbCollect(@NonNull UserCenterPageBean.Bean bean);

    void getRemoteLbCollectList(String token, final String userId, String appKey, String channelCode, String offset, final String limit, @NonNull LbCollectDataSource.GetLbCollectListCallback callback);

    //释放资源
    void releaseLbCollectResource();
}
