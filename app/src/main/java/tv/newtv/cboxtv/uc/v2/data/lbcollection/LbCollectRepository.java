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

public class LbCollectRepository implements LbCollectDataSource {
    private static LbCollectRepository INSTANCE = null;

    private LbCollectDataSource mRemoteDataSource;

    public LbCollectRepository(LbCollectDataSource remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }

    public static LbCollectRepository getInstance(LbCollectDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new LbCollectRepository(remoteDataSource);
        }
        return INSTANCE;
    }

    public void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void addRemoteLbCollect(@NonNull UserCenterPageBean.Bean lbCollect) {
        mRemoteDataSource.addRemoteLbCollect(lbCollect);
    }

    @Override
    public void addRemoteLbCollectList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteLbCollectListCallback callback) {
        mRemoteDataSource.addRemoteLbCollectList(token, userID, beanList, callback);
    }

    @Override
    public void deleteRemoteLbCollect(@NonNull UserCenterPageBean.Bean Collect) {
        mRemoteDataSource.deleteRemoteLbCollect(Collect);
    }

    @Override
    public void getRemoteLbCollectList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetLbCollectListCallback callback) {
        mRemoteDataSource.getRemoteLbCollectList(token, userId, appKey, channelCode, offset, limit, callback);

    }

    @Override
    public void releaseLbCollectResource() {
        mRemoteDataSource.releaseLbCollectResource();
    }
}
