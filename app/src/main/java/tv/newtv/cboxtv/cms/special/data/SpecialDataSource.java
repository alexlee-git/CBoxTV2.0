package tv.newtv.cboxtv.cms.special.data;

import okhttp3.ResponseBody;

/**
 * Created by lin on 2018/3/7.
 */

public interface SpecialDataSource {
    interface GetPageDataCallback {

        void onDataLoaded(ResponseBody value);

        void onDataNotAvailable();
    }

    void getPageData(GetPageDataCallback getPageDataCallback, String appkey, String channelId,
                     String pageUUID);
}
