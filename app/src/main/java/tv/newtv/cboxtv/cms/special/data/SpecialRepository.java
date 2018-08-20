package tv.newtv.cboxtv.cms.special.data;

import okhttp3.ResponseBody;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

//import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by lin on 2018/3/7.
 */

public class SpecialRepository implements SpecialDataSource {
    private static SpecialRepository INSTANCE = null;

    private SpecialDataSource mRemoteDataSource;
    private SpecialDataSource mLocalDataSource;

    @Override
    public void getPageData(final GetPageDataCallback getPageDataCallback, String appkey, String channelId,
                            String pageUUID) {
        mRemoteDataSource.getPageData(new GetPageDataCallback() {

            @Override
            public void onDataLoaded(ResponseBody value) {
                getPageDataCallback.onDataLoaded(value);
            }

            @Override
            public void onDataNotAvailable() {
                getPageDataCallback.onDataNotAvailable();
            }
        }, appkey, channelId, pageUUID);
    }

    private SpecialRepository(SpecialDataSource remoteDataSource, SpecialDataSource localDataSource) {
        mRemoteDataSource = checkNotNull(remoteDataSource);
        mLocalDataSource = checkNotNull(localDataSource);
    }

    public static SpecialRepository getInstance(SpecialDataSource remoteDataSource, SpecialDataSource localDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new SpecialRepository(remoteDataSource, localDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
