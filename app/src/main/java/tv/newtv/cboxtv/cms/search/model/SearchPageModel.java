package tv.newtv.cboxtv.cms.search.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.search.bean.SearchHotInfo;
import tv.newtv.cboxtv.cms.search.presenter.ISearchPagePresenter;
import tv.newtv.cboxtv.cms.util.LogUtils;
//import tv.newtv.cboxtv.cms.net.ApiUtil;

/**
 * 类描述：搜索页面获取数据类
 * 创建人：wqs
 * 创建时间： 2018/3/6 0006 14:37
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchPageModel implements ISearchPageModel {
    private final String TAG = this.getClass().getSimpleName();
    private ISearchPagePresenter mSearchPagePresenter;
    private Context mContext;
    private Gson mGson;
    private Disposable mDisposable;

    public SearchPageModel(ISearchPagePresenter searchPagePresenter, Context context) {
        this.mSearchPagePresenter = searchPagePresenter;
        this.mContext = context;
        mGson = new Gson();
    }


    @Override
    public void requestPageRecommendData(String appKey, String channelId) {
        NetClient.INSTANCE.getSearchRecommendApi()
                .getRecommendResponse(appKey, channelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            String result = value.string();
                            SearchHotInfo mSearchHotInfo = mGson.fromJson(result, SearchHotInfo.class);
//                            ModuleInfoResult moduleData = ModuleUtils.getInstance().parseJsonForModuleInfo(result);
                            mSearchPagePresenter.inflatePageRecommendData(mSearchHotInfo);
                        } catch (Exception e) {
                            LogUtils.e(e.toString());
                        }

                        unSubscribe();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(Constant.TAG, "-----requestPageRecommendData---onError-----");
                        mSearchPagePresenter.inflatePageRecommendData(null);
                        unSubscribe();
                    }


                    @Override
                    public void onComplete() {
                        unSubscribe();
                    }
                });
    }



    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

}
