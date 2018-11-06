package tv.newtv.cboxtv.cms.screenList.model;



import com.google.gson.Gson;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.screenList.api.LabelApi;
import tv.newtv.cboxtv.cms.screenList.bean.TabBean;
import tv.newtv.cboxtv.cms.screenList.manager.RetrofitManager;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public class FirstLabelModelImpl implements FirstLabelModel {
    @Override
    public void requestFirstLabel(final FirstLabelCompleteListener completeListener) {

        RetrofitManager retrofitManager = RetrofitManager.getRetrofitManager();
        Observable<ResponseBody> observable = retrofitManager.create(LabelApi.class).getFirstMenu(1);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Gson gson = new Gson();
                        if (responseBody!=null){
                            TabBean tabBean = gson.fromJson(responseBody.string(), TabBean.class);
                            completeListener.sendFirstLabel(tabBean);
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });


    }

}
