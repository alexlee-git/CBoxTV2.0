package tv.newtv.cboxtv.cms;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;
import tv.newtv.cboxtv.cms.net.NetClient;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms
 * 创建事件:         10:01
 * 创建人:           weihaichao
 * 创建日期:          2018/6/6
 */
public class DataCenter {

    private static DataCenter instance;

    public NavInfoResult<List<NavInfoResult.NavInfo>> FirstnavInfoResult;

    public static DataCenter getInstance() {
        if (instance == null) {
            synchronized (DataCenter.class) {
                if (instance == null) instance = new DataCenter();
            }
        }
        return instance;
    }

    public void preloadNavigation() {
        NetClient.INSTANCE.getNavInfoApi()
                .getNavInfo(Constant.APP_KEY, Constant.CHANNEL_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NavInfoResult<List<NavInfoResult.NavInfo>>
                                               listNavInfoResult) {
                        FirstnavInfoResult = listNavInfoResult;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
