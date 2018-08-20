package tv.newtv.cboxtv.cms.mainPage.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.BaseRequestModel;
import tv.newtv.cboxtv.cms.DataCenter;
import tv.newtv.cboxtv.cms.mainPage.presenter.IMainPagePresenter;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUtils;


/**
 * Created by lixin on 2018/1/16.
 */

public class MainPageModel extends BaseRequestModel implements IMainPageModel {

    private IMainPagePresenter mPresenter;
    private Context mContext;
    private Gson mGson;


    public MainPageModel(IMainPagePresenter presenter, Context context) {
        super(context);
        mPresenter = presenter;
        mContext = context;
        mGson = new Gson();
    }

    @Override
    public void requestNavBarData() {
        try {
            requestData("page_nav");
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.e("请求导航栏数据出现异常");
        }
    }


    private void requestData(final String uuid) {

        if(DataCenter.getInstance().FirstnavInfoResult != null){
            if (mPresenter != null)
                mPresenter.inflateNavigationBar(DataCenter.getInstance().FirstnavInfoResult, "server");

            String result = mGson.toJson(DataCenter.getInstance().FirstnavInfoResult, NavInfoResult.class);
            saveDataToJsonFile(mContext, result, uuid + ".json");
            return;
        }

        LogUtils.d(MainPageModel.class.getSimpleName(), "requestNavData()...");
        Observable
                .create(new ObservableOnSubscribe<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                    @Override
                    public void subscribe(ObservableEmitter<NavInfoResult<List<NavInfoResult
                            .NavInfo>>> e) throws Exception {
                        LogUtils.d(MainPageModel.class.getSimpleName(), "get Local...");
                        NavInfoResult<List<NavInfoResult.NavInfo>> mNavInfoResult = new NavInfoResult<>();
                        mNavInfoResult.setErrCode("-1");
                        e.onNext(mNavInfoResult);
                    }
                })
                .concatMap(
                        new Function<NavInfoResult<List<NavInfoResult.NavInfo>>,
                                ObservableSource<NavInfoResult<List<NavInfoResult.NavInfo>>>>() {
                            @Override
                            public ObservableSource<NavInfoResult<List<NavInfoResult.NavInfo>>>
                            apply(NavInfoResult<List<NavInfoResult.NavInfo>>
                                          listNavInfoResult) throws Exception {
                                if (listNavInfoResult != null && !"-1".equals(listNavInfoResult
                                        .getErrCode())) {
                                    LogUtils.d(MainPageModel.class.getSimpleName(), "notify local " +
                                            "data" +
                                            " ..." +
                                            listNavInfoResult);
                                    if (mPresenter != null)
                                        mPresenter.inflateNavigationBar(listNavInfoResult, "local");
                                } else {
                                    LogUtils.d(MainPageModel.class.getSimpleName(), "notify local " +
                                            "data null...");
                                }
                                LogUtils.d(MainPageModel.class.getSimpleName(), "request server...");
                                return NetClient.INSTANCE.getNavInfoApi()
                                        .getNavInfo(Constant.APP_KEY, Constant.CHANNEL_ID);
                            }
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.e(d.toString());
                    }

                    @Override
                    public void onNext(NavInfoResult<List<NavInfoResult.NavInfo>> value) {
                        LogUtils.d(MainPageModel.class.getSimpleName(), "onNext ..." + value);
                        if (value != null) {
                            if (mPresenter != null)
                                mPresenter.inflateNavigationBar(value, "server");

                            String result = mGson.toJson(value, NavInfoResult.class);
                            saveDataToJsonFile(mContext, result, uuid + ".json");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d(MainPageModel.class.getSimpleName(), "onError ..." + e.getMessage());
                        if (mPresenter != null) {
                            mPresenter.onFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.d(MainPageModel.class.getSimpleName(), "onComplete ...");
                    }
                });

    }

    private void getNavDataFromLocal() {
        Observable.create(new ObservableOnSubscribe<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
            @Override
            public void subscribe(ObservableEmitter<NavInfoResult<List<NavInfoResult.NavInfo>>> e) {
                String value = loadDataFromJsonFile(mContext, "page_nav.json");
                NavInfoResult<List<NavInfoResult.NavInfo>> mNavInfoResult = null;
                if (value != null && !value.equals("")) {
                    mNavInfoResult = mGson.fromJson(value, new
                            TypeToken<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                            }.getType());
                    e.onNext(mNavInfoResult);
                } else {
                    try {
                        String fileName = "page_nav.json";
                        InputStream is = mContext.getAssets().open(fileName);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder buffer = new StringBuilder(Constant.BUFFER_SIZE_1K);
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        LogUtils.e(Constant.TAG, "---buffer.toString()------" + buffer.toString());
                        mNavInfoResult = mGson.fromJson(buffer.toString(), new
                                TypeToken<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                                }.getType());
                        LogUtils.e(Constant.TAG, "------mNavInfoResult---" + mNavInfoResult.getErrMsg());
                        e.onNext(mNavInfoResult);
                    } catch (Exception exception) {
                        LogUtils.e(exception);
                        LogUtils.e("--exception--" + exception.toString());
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.e(d.toString());
                    }

                    @Override
                    public void onNext(NavInfoResult<List<NavInfoResult.NavInfo>> value) {
                        if (mPresenter != null) {
                            mPresenter.inflateNavigationBar(value, "local");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mPresenter != null) {
                            mPresenter.onFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void getNavDataFromServer() {
        NetClient.INSTANCE.getNavInfoApi()
                .getNavInfo(Constant.APP_KEY, Constant.CHANNEL_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NavInfoResult<List<NavInfoResult.NavInfo>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.e(d.toString());
                    }

                    @Override
                    public void onNext(NavInfoResult<List<NavInfoResult.NavInfo>> value) {
                        if (mPresenter != null) {
                            mPresenter.inflateNavigationBar(value, "server");
                        }
                        String result = mGson.toJson(value, NavInfoResult.class);

                        saveDataToJsonFile(mContext, result, "page_nav.json");
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(Constant.TAG, "获取导航信息 onError");
                        if (mPresenter != null) {
                            mPresenter.onFailed(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
