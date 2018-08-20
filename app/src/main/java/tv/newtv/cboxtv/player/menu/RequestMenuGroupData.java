package tv.newtv.cboxtv.player.menu;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.net.IMenuApi;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.GsonUtil;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.menu.model.LastMenuBean;

/**
 * Created by TCP on 2018/5/22.
 */

public class RequestMenuGroupData {
    private static final String TAG = "RequestMenuGroupData";

    private static Map<String,LastMenuBean> cacheBean = new HashMap<>();
    private static List<String> errorList = new ArrayList<>();

    public interface DataListener{

        void success(LastMenuBean lastMenuBean);
    }

    public static void getLastData(String contentUUID,DataListener listener){
        getLastData(contentUUID,1,listener);
        getLastData(contentUUID,2,listener);
    }

    private static void getLastData(final String contentUUID, int type, final DataListener listener) {
        if (TextUtils.isEmpty(contentUUID)){
            return;
        }

        String leftString = contentUUID.substring(0, 2);
        String rightString = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        IMenuApi menuApi = NetClient.INSTANCE.getMenuApi();
        Observable<ResponseBody> lastList = null;
        switch (type){
            case 1:
                lastList = menuApi.getLastList(Constant.APP_KEY, Constant.CHANNEL_ID, leftString, rightString, contentUUID);
                break;
            case 2:
                lastList = menuApi.getPsList(Constant.APP_KEY, Constant.CHANNEL_ID, leftString, rightString, contentUUID);
                break;
        }

        if(lastList == null){
            return;
        }

        lastList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "onNext: ");
                        try {
                            String result = responseBody.string();
                            if (!TextUtils.isEmpty(result)) {
                                LastMenuBean lastMenuBean = GsonUtil.fromjson(result,
                                        LastMenuBean.class);
                                if(cacheBean.get(contentUUID) != null){
                                    LastMenuBean resultMenuBean = null;
                                    LastMenuBean preMenuBean = cacheBean.get(contentUUID) ;
                                    /**
                                     * 2个接口返回的数据都正确，需要拼接数据
                                     * 哪个bean里面有Programs，那么就把这个Programs设置到另一个bean中
                                     */
                                    if("0".equals(lastMenuBean.getErrorCode()) && "0".equals(preMenuBean.getErrorCode())){
                                        if(lastMenuBean.getData() != null && lastMenuBean.getData().getPrograms() != null
                                                && preMenuBean.getData() != null){
                                            preMenuBean.getData().setPrograms(lastMenuBean.getData().getPrograms());
                                            resultMenuBean = preMenuBean;

                                        } else if(preMenuBean.getData() != null && preMenuBean.getData().getPrograms() != null
                                                && lastMenuBean.getData() != null){
                                            lastMenuBean.getData().setPrograms(preMenuBean.getData().getPrograms());
                                            resultMenuBean = lastMenuBean;
                                        }
                                    } else if("0".equals(lastMenuBean.getErrorCode())){
                                        resultMenuBean = lastMenuBean;
                                    }else if ("0".equals(preMenuBean.getErrorCode())){
                                        resultMenuBean = preMenuBean;
                                    }

                                    if(listener != null){
                                        cacheBean.remove(contentUUID);
                                        listener.success(resultMenuBean);
                                    }

                                } else if(errorList.contains(contentUUID)){
                                    if(listener != null){
                                        errorList.remove(contentUUID);
                                        listener.success(lastMenuBean);
                                    }
                                } else {
                                    cacheBean.put(contentUUID,lastMenuBean);
                                }
                            }else {
                                dealError(contentUUID,listener);
                            }
                        } catch (IOException e) {
                            dealError(contentUUID,listener);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        dealError(contentUUID,listener);
                        Log.i(TAG, "onError: "+e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static void dealError(final String contentUUID, final DataListener listener){
        if(cacheBean.get(contentUUID) != null && listener != null){
            listener.success(cacheBean.get(contentUUID));
            cacheBean.remove(contentUUID);
        }else {
            errorList.add(contentUUID);
        }
    }
}
