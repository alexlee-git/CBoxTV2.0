package tv.newtv.cboxtv.cms.special.data.local;


import tv.newtv.cboxtv.cms.special.data.SpecialDataSource;

/**
 * Created by lin on 2018/3/7.
 */

public class LocalDataSource implements SpecialDataSource {
    private static LocalDataSource INSTANCE;

    @Override
    public void getPageData(GetPageDataCallback getPageDataCallback, String appkey, String channelId,
                            String pageUUID) {
        // 从本地数据库中取数据
    }

    public static LocalDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource();
        }
        return INSTANCE;
    }
}
