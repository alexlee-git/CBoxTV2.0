package tv.newtv.cboxtv.cms.screenList.model;


import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.screenList.api.LabelApi;
import tv.newtv.cboxtv.cms.screenList.bean.LabelDataBean;
import tv.newtv.cboxtv.cms.screenList.common.Common;
import tv.newtv.cboxtv.cms.screenList.manager.RetrofitManager;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public class LabelDataModelImpl implements LabelDataModel {


    @Override
    public void requestLabelData(Map<String, Object> map, final DataCompleteListener listener) {


        RetrofitManager manager = RetrofitManager.getRetrofitManager(Common.BASE_DATA_URL);
        Observable<ResponseBody> observable = manager.create(LabelApi.class).getData(map);


        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                        Gson gson = new Gson();
                        if (responseBody!=null){
                            LabelDataBean dataBean = gson.fromJson(responseBody.string(), LabelDataBean.class);
                            Log.d("DataModelImpl2", "pageDataBean:" + dataBean);
                            if (dataBean != null)
                                listener.sendLabelData(dataBean);
                        }


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }
}
