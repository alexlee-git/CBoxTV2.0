package tv.newtv.cboxtv.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.util.StringUtils;

import tv.newtv.cboxtv.LauncherApplication;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         20:30
 * 创建人:           weihaichao
 * 创建日期:          2018/4/27
 */
public final class PlayInfoUtil {

    public static String formatSplitInfo(String... args) {
        StringBuilder result = new StringBuilder();
        for (String str : args) {
            if (StringUtils.isEmpty(str)) {
                continue;
            }
            if (result.length() != 0) result.append(" | ");
            result.append(str);
        }
        return result.toString();
    }

    public static void getPlayInfo(final String uuid, final ProgramSeriesInfoCallback callback) {
        if (TextUtils.isEmpty(uuid)) {
            Toast.makeText(LauncherApplication.AppContext, "UUID为空", Toast.LENGTH_SHORT).show();
//            callback.onResult(null);
            return;
        }

//        Log.i("PlayInfoUtil", "uuid:"+uuid);
//        final String leftUUID = uuid.substring(0, 2);
//        final String rightUUID = uuid.substring(uuid.length() - 2, uuid.length());
//
//
//        NetClient.INSTANCE.getDetailsPageApi().getInfo(Libs.get().getAppKey(), Libs.get().getChannelId(),
//                leftUUID, rightUUID, uuid).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            String result = responseBody.string();
//                            Log.i("PlayInfoUtil", "onNext: "+result);
//                            JSONObject object = new JSONObject(result);
//                            if (object.getInt("errorCode") == 0) {
//                                JSONObject obj = object.getJSONObject("data");
//                                Gson gson = new Gson();
//
//                                final ProgramSeriesInfo entity = gson.fromJson(obj.toString(),
//                                        ProgramSeriesInfo.class);
//
//                                MainLooper.get().post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (callback != null)
//                                            callback.onResult(entity);
//                                    }
//                                });
//                            }
//                        } catch (Exception e) {
//                            LogUtils.e(e.toString());
//                            if (callback != null)
//                                callback.onResult(null);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtils.e(e.toString());
//                        if (callback != null)
//                            callback.onResult(null);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    public static void getInfo(final String uuid,String contentType,final
                               ProgramSeriesInfoCallback callback){
        if(Constant.CONTENTTYPE_PAGE.equals(contentType)){
            getPageInfo(uuid, callback);
        }else if(Constant.CONTENTTYPE_TV.equals(contentType)){
            getColumnInfo(uuid,callback);
        }else{
            getPlayInfo(uuid, callback);
        }
    }

    public static void getColumnInfo(String uuid, final ProgramSeriesInfoCallback callback){
        if(TextUtils.isEmpty(uuid)){
//            callback.onResult(null);
            return;
        }
        String leftUUID = uuid.substring(0, 2);
        String rightUUID = uuid.substring(uuid.length() - 2, uuid.length());
//        Observable<ResponseBody> observable = EpisodeHelper.GetInterface(TYPE_COLUMN_DETAIL, leftUUID,rightUUID,uuid);
//        if(observable != null){
//            observable.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Observer<ResponseBody>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody responseBody) {
//                            try {
//                                String result = responseBody.string();
//                                Log.i("PlayInfoUtil", "onNext: "+result);
//                                JSONObject object = new JSONObject(result);
//                                if (object.getInt("errorCode") == 0) {
//                                    JSONObject obj = object.getJSONObject("data");
//                                    Gson gson = new Gson();
//
//                                    final ProgramSeriesInfo entity = gson.fromJson(obj.toString(),
//                                            ProgramSeriesInfo.class);
//                                    if (callback != null)
//                                        callback.onResult(entity);
//                                }
//                            } catch (Exception e) {
//                                LogUtils.e(e.toString());
//                                if (callback != null)
//                                    callback.onResult(null);
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//                        }
//                    });
//        }
    }


    public static void getPageInfo(final String uuid, final ProgramSeriesInfoCallback callback) {
        if (TextUtils.isEmpty(uuid)) {
            Toast.makeText(LauncherApplication.AppContext, "UUID为空", Toast.LENGTH_SHORT).show();
//            callback.onResult(null);
            return;
        }

//        NetClient.INSTANCE.getPageDataApi().getPageData(Constant.APP_KEY,Constant.CHANNEL_ID,uuid)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody responseBody) {
//                        try {
//                            String result = responseBody.string();
//                            Log.i("PlayInfoUtil", "onNext: "+result);
//                            JSONObject object = new JSONObject(result);
//                            if (object.getInt("errorCode") == 0) {
//                                JSONArray jsonArray = object.getJSONArray("data");
//                                Gson gson = new Gson();
//
//                                final ProgramSeriesInfo entity = gson.fromJson(jsonArray.get(0).toString(),
//                                        ProgramSeriesInfo.class);
//
//                                MainLooper.get().post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (callback != null)
//                                            callback.onResult(entity);
//                                    }
//                                });
//                            }
//                        } catch (Exception e) {
//                            LogUtils.e(e.toString());
//                            if (callback != null)
//                                callback.onResult(null);
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtils.e(e.toString());
//                        if (callback != null)
//                            callback.onResult(null);
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }

    public interface ProgramSeriesInfoCallback {
//        void onResult(ProgramSeriesInfo info);
    }
}
