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

public class CollectRepository implements CollectDataSource {
    private static CollectRepository INSTANCE = null;

    private CollectDataSource mRemoteDataSource;

    public CollectRepository(CollectDataSource remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }

    public static CollectRepository getInstance(CollectDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CollectRepository(remoteDataSource);
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void addRemoteCollect(@NonNull UserCenterPageBean.Bean Collect) {
        mRemoteDataSource.addRemoteCollect(Collect);
    }

    @Override
    public void addRemoteCollectList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteCollectListCallback callback) {
        mRemoteDataSource.addRemoteCollectList(token, userID, beanList, callback);
    }

    @Override
    public void deleteRemoteCollect(@NonNull UserCenterPageBean.Bean Collect) {
        mRemoteDataSource.deleteRemoteCollect(Collect);
    }

    @Override
    public void getRemoteCollectList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetCollectListCallback callback) {
        mRemoteDataSource.getRemoteCollectList(token, userId, appKey, channelCode, offset, limit, callback);
    }

    @Override
    public void releaseCollectResource() {
        mRemoteDataSource.releaseCollectResource();
    }
}
