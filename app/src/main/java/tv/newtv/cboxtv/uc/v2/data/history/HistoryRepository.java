package tv.newtv.cboxtv.uc.v2.data.history;

import android.content.Context;
import android.support.annotation.NonNull;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;

/**
 * Created by lin on 2018/3/10.
 */

public class HistoryRepository implements HistoryDataSource {
    private static HistoryRepository INSTANCE = null;

    private HistoryDataSource mRemoteDataSource;
    private Context mContext;

    public HistoryRepository(HistoryDataSource remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }

    public static HistoryRepository getInstance(HistoryDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new HistoryRepository(remoteDataSource);
        }
        return INSTANCE;
    }

    public  void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void addRemoteHistory(@NonNull UserCenterPageBean.Bean entity) {
        mRemoteDataSource.addRemoteHistory(entity);
    }

    @Override
    public void deleteRemoteHistory(String token, @NonNull String userId, String contentType, String appKey, String channelCode, String contentuuids) {
        mRemoteDataSource.deleteRemoteHistory(token, userId, contentType, appKey, channelCode, contentuuids);
    }

    @Override
    public void getRemoteHistoryList(String token, String userId, String appKey, String channelCode, String offset, String limit, @NonNull GetHistoryListCallback callback) {
        mRemoteDataSource.getRemoteHistoryList(token, userId, appKey, channelCode, offset, limit, callback);
    }

    @Override
    public void releaseHistoryResource() {
        mRemoteDataSource.releaseHistoryResource();
    }
}
