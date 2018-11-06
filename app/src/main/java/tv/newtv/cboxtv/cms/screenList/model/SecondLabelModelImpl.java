package tv.newtv.cboxtv.cms.screenList.model;

import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.screenList.api.LabelApi;
import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;
import tv.newtv.cboxtv.cms.screenList.manager.RetrofitManager;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public class SecondLabelModelImpl implements SecondLabelModel {
    @Override
    public void requestSecondLabel(final SecondLabelCompleteListener completeListener) {


        RetrofitManager retrofitManager = RetrofitManager.getRetrofitManager();
        Observable<ResponseBody> observable = retrofitManager.create(LabelApi.class).getSecondMenu();


        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        Gson gson = new Gson();
                        if (responseBody!=null){
                            LabelBean labelBean = gson.fromJson(responseBody.string(), LabelBean.class);

                            completeListener.sendSecondLabel(labelBean);
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

    }
}
