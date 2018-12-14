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

public class SubRepository implements SubDataSource {
    private static SubRepository INSTANCE = null;

    private SubDataSource mRemoteDataSource;

    public SubRepository(SubDataSource remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }

    public static SubRepository getInstance(SubDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SubRepository(remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void addRemoteSubscribe(@NonNull UserCenterPageBean.Bean Bean) {
        mRemoteDataSource.addRemoteSubscribe(Bean);
    }

    @Override
    public void addRemoteSubscribeList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteSubscribeListCallback callback) {
        mRemoteDataSource.addRemoteSubscribeList(token, userID, beanList, callback);
    }

    @Override
    public void deleteRemoteSubscribe(@NonNull UserCenterPageBean.Bean Bean) {
        mRemoteDataSource.deleteRemoteSubscribe(Bean);
    }

    @Override
    public void getRemoteSubscribeList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetSubscribeListCallback callback) {
        mRemoteDataSource.getRemoteSubscribeList(token, userId, appKey, channelCode, offset, limit, callback);
    }

    @Override
    public void releaseSubscribeResource() {
        mRemoteDataSource.releaseSubscribeResource();
    }
}
