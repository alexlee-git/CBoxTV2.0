package tv.newtv.cboxtv.exit.model;

import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.screenList.manager.RetrofitManager;
import tv.newtv.cboxtv.exit.api.RecommendApi;
import tv.newtv.cboxtv.exit.bean.RecommendBean;

public class RecommendModelImpl implements RecommendModel {
    @Override
    public void requestRecommendData(final CompleteListener listener) {

        RetrofitManager retrofitManager = RetrofitManager.getRetrofitManager();
        Observable<ResponseBody> observable = retrofitManager.create(RecommendApi.class).getRecommendData();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Gson gson = new Gson();
                        if (responseBody != null) {
                            RecommendBean recommendBean = gson.fromJson(responseBody.string(), RecommendBean.class);
                            listener.sendRecommendData(recommendBean);
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
