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

public class FollowRepository implements FollowDataSource {
    private static FollowRepository INSTANCE = null;

    private FollowDataSource mRemoteDataSource;

    public FollowRepository(FollowDataSource remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }

    public static FollowRepository getInstance(FollowDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new FollowRepository(remoteDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void addRemoteFollow(@NonNull UserCenterPageBean.Bean bean) {
        mRemoteDataSource.addRemoteFollow(bean);
    }

    @Override
    public void addRemoteFollowList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteFollowListCallback callback) {
        mRemoteDataSource.addRemoteFollowList(token, userID, beanList, callback);
    }

    @Override
    public void deleteRemoteFollow(@NonNull UserCenterPageBean.Bean bean) {
        mRemoteDataSource.deleteRemoteFollow(bean);
    }

    @Override
    public void getRemoteFollowList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetFollowListCallback callback) {
        mRemoteDataSource.getRemoteFollowList(token, userId, appKey, channelCode, offset, limit, callback);
    }

    @Override
    public void releaseFollowResource() {
        mRemoteDataSource.releaseFollowResource();
    }
}
