package tv.newtv.cboxtv.cms.listPage.model;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.BaseRequestModel;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.listPage.presenter.IListPagePresenter;
//import tv.newtv.cboxtv.cms.net.ApiUtil;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * Created by caolonghe on 2018/3/6 0006.
 */

public class ListPageModel extends BaseRequestModel implements IListPageModel {
    private static final String LISTPAGE_URL = "%s%s/%s/%s/%s.json";
    private IListPagePresenter mPresenter;

    public ListPageModel(IListPagePresenter mPresenter, Context context) {
        super(context);
        this.mPresenter = mPresenter;
    }

    @Override
    public void requestPageListNav(String uuid) {
        try {

            String url = String.format(LISTPAGE_URL, Constant.BASE_URL_CMS + Constant.CMS_URL, Constant
                    .BASE_URL_LISTPAGE, Constant.APP_KEY, Constant.CHANNEL_ID, uuid);

            requestData(uuid, url);
//            ApiUtil.getInstance().getiListPageApi()
//                    .getListPageResponse(url)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<ResponseBody>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody value) {
//                            if (mPresenter != null) {
//                                try {
//                                    String data = value.string();
//                                    mPresenter.inflateListPageNav(data);
//                                } catch (IOException e) {
//                                    LogUtils.e(e);
//                                    mPresenter.inflateListPageNav(null);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            LogUtils.e(Constant.TAG, "获取导航信息 onError");
//                            if (mPresenter != null) {
//                                mPresenter.inflateListPageNav(null);
//                            }
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    });
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.e("请求详情页数据出现异常");
        }
    }

    private void requestData(final String uuid, final String url) {

        Observable
                .create(new ObservableOnSubscribe<NavListPageInfoResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<NavListPageInfoResult> e) throws
                            Exception {
                        Log.d(ListPageModel.class.getSimpleName(), "get Local...");
                        String value = loadDataFromJsonFile(getContext(), uuid + ".json");

                        NavListPageInfoResult mResult = null;

                        if (!TextUtils.isEmpty(value)) {
                            mResult = getGson().fromJson(value, new
                                    TypeToken<NavListPageInfoResult>() {
                                    }.getType());
                        } else {
                            mResult = new NavListPageInfoResult();
                            mResult.setErrCode("-1");
                        }
                        e.onNext(mResult);
                    }
                })
                .concatMap(
                        new Function<NavListPageInfoResult,
                                ObservableSource<ResponseBody>>() {
                            @Override
                            public ObservableSource<ResponseBody>
                            apply(final NavListPageInfoResult
                                          listNavInfoResult) throws Exception {
                                if (listNavInfoResult != null && !"-1".equals(listNavInfoResult
                                        .getErrCode())) {
                                    Log.d(ListPageModel.class.getSimpleName(), "notify local data" +
                                            " ..." +
                                            listNavInfoResult);
                                    if (mPresenter != null) {
                                        MainLooper.get().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mPresenter.inflateListPageNav(listNavInfoResult,
                                                        "local");
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(ListPageModel.class.getSimpleName(), "notify local " +
                                            "data null...");
                                }
                                Log.d(ListPageModel.class.getSimpleName(), "request server...");
                                return NetClient.INSTANCE.getListPageApi()
                                        .getListPageResponse(url);
                            }
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {

                        NavListPageInfoResult result = null;
                        try {
                            String response = value.string();
                            result = getGson().fromJson(response, new
                                    TypeToken<NavListPageInfoResult>() {
                                    }
                                    .getType());
                            Log.d(ListPageModel.class.getSimpleName(), "onNext ..." + result);
                            if (result != null) {
                                if (mPresenter != null)
                                    mPresenter.inflateListPageNav(result, "server");
                                saveDataToJsonFile(getContext(), response, uuid + ".json");
                            }
                        } catch (Exception e) {
                            LogUtils.e(e.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(ListPageModel.class.getSimpleName(), "onError ..." + e
                                .getMessage());
                        if(mPresenter != null){
                            mPresenter.onFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d(ListPageModel.class.getSimpleName(), "onComplete ...");
                    }
                });

    }
}
