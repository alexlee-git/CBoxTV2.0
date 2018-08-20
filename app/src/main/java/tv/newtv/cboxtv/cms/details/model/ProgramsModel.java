package tv.newtv.cboxtv.cms.details.model;

import android.util.Log;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.details.presenter.IProgramPresenter;
//import tv.newtv.cboxtv.cms.net.ApiUtil;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * Created by lixin on 2018/1/15.
 */

public class ProgramsModel implements IProgramsModel {

    private IProgramPresenter mPresenter;

    public ProgramsModel(IProgramPresenter programPresenter) {
        mPresenter = programPresenter;
    }

    @Override
    public void requestProgramsData(String appkey, String channelId, String left, String right, String contentUUID) {
        try {
            NetClient.INSTANCE.getProgramSeriesInfoApi()
                    .getProgramResponse(appkey, channelId, left, right, contentUUID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }
                        @Override
                        public void onNext(ResponseBody value) {
                            if (mPresenter != null) {
                                try {
                                    String data = value.string();
                                    mPresenter.inflateProgramSeries(data);
                                } catch (IOException e) {
                                    LogUtils.e(e);
                                    mPresenter.inflateProgramSeries(null);
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("detail----model", "获取详情页 onError");
                            if (mPresenter != null) {
                                mPresenter.inflateProgramSeries(null);
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.e("detail----model请求详情页数据出现异常");
        }
    }
}
