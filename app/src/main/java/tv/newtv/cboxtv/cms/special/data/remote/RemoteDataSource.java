package tv.newtv.cboxtv.cms.special.data.remote;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
//import tv.newtv.cboxtv.cms.net.ApiUtil;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.special.data.SpecialDataSource;

/**
 * Created by lin on 2018/3/7.
 */

public class RemoteDataSource implements SpecialDataSource {
    private static RemoteDataSource INSTANCE;

    @Override
    public void getPageData(final GetPageDataCallback getPageDataCallback, String appkey, String channelId,
                            String pageUUID) {
        // 从服务端去数据
        NetClient.INSTANCE.getSpecialApi().getPageData(appkey, channelId, pageUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        getPageDataCallback.onDataLoaded(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getPageDataCallback.onDataNotAvailable();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }
}
