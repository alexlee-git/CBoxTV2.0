package tv.newtv.cboxtv.cms.mainPage.model;

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
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.BaseRequestModel;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.mainPage.presenter.IContentPagePresenter;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUtils;
//import tv.newtv.cboxtv.cms.net.ApiUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.mainPage.model
 * 创建事件:         14:24
 * 创建人:           weihaichao
 * 创建日期:          2018/4/12
 */
public class ContentPageModelImpl extends BaseRequestModel implements IContentPageModel {

    private IContentPagePresenter mPresenter;
    private Disposable mDisposable;

    public ContentPageModelImpl(Context context, IContentPagePresenter pagePresenter) {
        super(context);
        mPresenter = pagePresenter;
    }

    @Override
    public void requestContentData(final String uuid) {

        Observable
                .create(new ObservableOnSubscribe<ModuleInfoResult>() {
                    @Override
                    public void subscribe(ObservableEmitter<ModuleInfoResult> e) throws
                            Exception {
                        ModuleInfoResult mResult = null;
                        try {
                            Log.d(ContentPageModelImpl.class.getSimpleName(), "get Local...");
                            String value = loadDataFromJsonFile(getContext(), uuid + ".json");

                            if (!TextUtils.isEmpty(value)) {
                                mResult = getGson().fromJson(value, new
                                        TypeToken<ModuleInfoResult>() {
                                        }.getType());
                            }

                        }catch (Exception exp){
                            LogUtils.e(exp.toString());
                        }finally {
                            if(mResult  == null){
                                mResult = new ModuleInfoResult();
                                mResult.setErrorCode("-1");
                            }
                            e.onNext(mResult);
                        }
                    }
                })
                .concatMap(
                        new Function<ModuleInfoResult,
                                ObservableSource<ResponseBody>>() {
                            @Override
                            public ObservableSource<ResponseBody>
                            apply(final ModuleInfoResult
                                          listNavInfoResult) throws Exception {
                                if (listNavInfoResult != null && !"-1".equals(listNavInfoResult
                                        .getErrorCode())) {
                                    Log.d(ContentPageModelImpl.class.getSimpleName(), "notify local data" +
                                            " ..." +
                                            listNavInfoResult);
                                    if (mPresenter != null) {
                                        MainLooper.get().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mPresenter != null) {
                                                    mPresenter.inflateContentView(listNavInfoResult,
                                                            "local");
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Log.d(ContentPageModelImpl.class.getSimpleName(), "notify local " +
                                            "data null...");
                                }
                                Log.d(ContentPageModelImpl.class.getSimpleName(), "request server...");
                                return NetClient.INSTANCE
                                        .getPageDataApi()
                                        .getPageData(Constant.APP_KEY, Constant.CHANNEL_ID,
                                                uuid);
                            }
                        })
                .subscribeOn(Schedulers.io())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(throwable.toString());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody value) {

                        ModuleInfoResult result = null;
                        try {
                            String response = value.string();
                            result = getGson().fromJson(response, new
                                    TypeToken<ModuleInfoResult>() {}
                                    .getType());
                            Log.d(ContentPageModelImpl.class.getSimpleName(), "onNext ..." + result);
                            if (result != null) {
                                if (mPresenter != null)
                                    mPresenter.inflateContentView(result, "server");
                                saveDataToJsonFile(getContext(), response, uuid + ".json");
                            }
                        } catch (Exception e) {
                            LogUtils.e(e.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(ContentPageModelImpl.class.getSimpleName(), "onError ..." + e.getMessage());
                        if(mPresenter != null)
                            mPresenter.onFailed(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(ContentPageModelImpl.class.getSimpleName(), "onComplete ...");
                    }
                });
    }

    @Override
    public void destroy() {
        super.destroy();
        mPresenter = null;
        if(mDisposable != null){
            if(!mDisposable.isDisposed()){
                mDisposable.dispose();
            }
            mDisposable = null;
        }
    }
}
