package tv.newtv.cboxtv.uc.v2.data.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.utils.BaseObserver;


/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/21 0021
 */
public class HistoryRemoteDataSource implements HistoryDataSource {
    private static final String TAG = "HistoryRemoteDataSource";

    private static HistoryRemoteDataSource INSTANCE;
    private Context mContext;
    private Disposable mAddDisposable, mGetListDisposable, mDeleteDisposable;

    public static HistoryRemoteDataSource getInstance(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = new HistoryRemoteDataSource(mContext);
        }
        return INSTANCE;
    }

    public HistoryRemoteDataSource(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void addRemoteHistory(final @NonNull UserCenterPageBean.Bean entity) {
//        uploadEnterLog(history.getProgramset_id());

        String mType = "0";
        String parentId = "";
        String subId = "";

        String type = entity.get_contenttype();
        if (Constant.CONTENTTYPE_PG.equals(type) || Constant.CONTENTTYPE_CP.equals(type)) {
            mType = "1";
            subId = entity.get_contentuuid();
        } else {
            mType = "0";
            parentId = entity.get_contentuuid();
            subId = entity.getPlayId();
        }

        String Authorization = "Bearer " + SharePreferenceUtils.getToken(mContext);
        String userId = SharePreferenceUtils.getUserId(mContext);

        Log.d(TAG, "report history item, user_id " + userId + ", content_id : " + entity.getContentId());

        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .addHistory(Authorization,
                        userId,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        /*entity.get_contentuuid()*/parentId,
                        entity.get_title_name(),
                        mType,
                        entity.get_imageurl(),
                        entity.getProgress(),
                        null,
                        entity.getDuration(),
                        entity.getPlayPosition(),
                        false,
                        true,
                        /*entity.getPlayId()*/subId,
                        entity.getGrade(),
                        entity.getVideoType(),
                        entity.getTotalCnt(),
                        entity.getSuperscript(),
                        entity.get_contenttype(),
                        entity.getPlayIndex(),
                        entity.get_actiontype(),
                        entity.getProgramChildName(),
                        entity.getContentId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "addRemoteHistory onSubscribe: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                        mAddDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "addRemoteHistory onNext: ");
                        try {
                            String responseString = responseBody.string();
                            checkUserOffline(responseString);
                            Log.d(TAG, "add History result : " + getServerResultMessage(responseString));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "addRemoteHistory onError: ");
                        Log.d(TAG, "add history onError:e:" + e.toString());
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void dealwithUserOffline() {
                        Log.i(TAG, "addRemoteHistory dealwithUserOffline: ");


                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "addRemoteHistory onComplete: ");

                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }
                });
    }

    @Override
    public void addRemoteHistoryList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteHistoryListCallback callback) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                try {
                    if (!TextUtils.isEmpty(userID)) {
                        if (beanList != null && beanList.size() > 0) {
                            for (int i = 0; i < beanList.size(); i++) {
                                addRemoteHistoryRecord(token, userID, beanList.get(i));
                            }
                            e.onNext(beanList.size());
                        } else {
                            Log.e(TAG, "wqs:addRemoteHistoryList:beanList==null||beanList.size==0");
                            e.onNext(0);
                        }
                    } else {
                        Log.e(TAG, "wqs:addRemoteHistoryList:userID==null");
                        e.onNext(0);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.e(TAG, "wqs:addRemoteHistoryList:Exception:" + exception.toString());
                    e.onNext(0);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer size) throws Exception {
                        if (callback != null) {
                            callback.onAddRemoteHistoryListComplete(size);
                        }
                    }
                });
    }

//    private void uploadEnterLog(String contentUUID) {
//        //添加历史记录
//        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
//        dataBuff.append(0 + ",")
//                .append(contentUUID)
//                .trimToSize();
//
//        LogUtil.getInstance().logUpLoad(Constant.LOG_NODE_HISTORY, dataBuff.toString());
//    }

    @Override
    public void deleteRemoteHistory(String token, @NonNull String userId, String contentType, String appKey, String channelCode, String contentID) {
        String isProgram = null;
        String program_child = null;
        String content_id = null;
        Log.e(TAG, "contentType: " + contentType);
        if (!"clean".equals(contentID)) {
            isProgram = (TextUtils.equals(contentType, Constant.CONTENTTYPE_CS)
                    || TextUtils.equals(contentType, Constant.CONTENTTYPE_PS)
                    || TextUtils.equals(contentType, Constant.CONTENTTYPE_CG)) ? "0" : "1";
            if (TextUtils.equals(isProgram, "0")) {
                program_child = "";
                content_id = contentID;
            } else if (TextUtils.equals(isProgram, "1")) {
                program_child = contentID;
                content_id = "";
            }
        } else {
            // 如果是clean, 则isProgram要传null
        }

        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .deleteHistory(token,
                        isProgram,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        program_child,
                        "", content_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "deleteRemoteHistory onSubscribe: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                        mDeleteDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "deleteRemoteHistory onNext: ");
                        try {
                            String responseString = responseBody.string();
                            checkUserOffline(responseString);
                            Log.d(TAG, "add History result : " + getServerResultMessage(responseString));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "deleteRemoteHistory onError: ");
                        Log.d(TAG, "delete history occur error:" + e.toString());
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }

                    @Override
                    public void dealwithUserOffline() {
                        Log.i(TAG, "deleteRemoteHistory dealwithUserOffline: ");


                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "deleteRemoteHistory onComplete: ");

                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }
                });
    }

    @Override
    public void getRemoteHistoryList(String token, final String userId, String appKey, String channelCode, String offset, final String limit, @NonNull final GetHistoryListCallback callback) {
        String Authorization = "Bearer " + token;
        NetClient.INSTANCE.getUserCenterLoginApi()
                .getHistoryList(Authorization,
                        Libs.get().getAppKey(),
                        Libs.get().getChannelId(),
                        userId,
                        offset,
                        limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "getRemoteHistoryList onSubscribe: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                        mGetListDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "getRemoteHistoryList onNext: ");
                        try {
                            int totalSize = 0;
                            String responseString = responseBody.string();
                            checkUserOffline(responseString);
                            JSONObject jsonObject = new JSONObject(responseString);
                            JSONObject data = jsonObject.getJSONObject("data");
                            JSONArray list = data.optJSONArray("list");
                            totalSize = data.optInt("end");
                            List<UserCenterPageBean.Bean> infos = new ArrayList<>();
                            int size = list.length();
                            JSONObject item = null;
                            UserCenterPageBean.Bean entity = null;
                            for (int i = 0; i < size; ++i) {
                                item = list.optJSONObject(i);
                                entity = new UserCenterPageBean.Bean();
                                String contentType = item.optString("content_type");

                                if (TextUtils.equals(Constant.CONTENTTYPE_CP, contentType) || TextUtils.equals(Constant.CONTENTTYPE_PG, contentType)) {
                                    entity.set_contentuuid(item.optString("program_child_id"));
                                } else {
                                    entity.set_contentuuid(item.optString("programset_id"));
                                }
                                entity.setContentId(item.optString("content_id"));
                                entity.set_contenttype(contentType);

                                entity.setPlayId(item.optString("program_child_id"));
                                entity.set_title_name(item.optString("programset_name"));
                                entity.setIs_program(item.optString("is_program"));
                                entity.set_actiontype(item.optString("action_type"));
                                entity.set_imageurl(item.optString("poster"));
                                entity.setGrade(item.optString("score"));
                                entity.setProgress(item.optString("program_progress"));
                                entity.setVideoType(item.optString("video_type"));
                                entity.setSuperscript(item.optString("superscript"));
                                entity.setTotalCnt(item.optString("total_count"));
                                entity.setPlayIndex(item.optString("latest_episode"));
                                entity.setEpisode_num(item.optString("episode_num"));
                                entity.setIsUpdate(item.optString("update_superscript"));
                                entity.setUpdateTime(Long.parseLong(item.optString("program_watch_date")) / 1000);
                                entity.setDuration(String.valueOf(item.optLong("program_dur")));
                                entity.setPlayPosition(String.valueOf(item.optLong("program_watch_dur")));
                                entity.setRecentMsg(item.optString("recent_msg"));
                                infos.add(entity);
                            }

                            if (callback != null) {
                                callback.onHistoryListLoaded(infos, totalSize);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (callback != null) {
                            callback.onDataNotAvailable();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "getRemoteHistoryList onError: ");
                        if (callback != null) {
                            callback.onHistoryListLoaded(null, 0);
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }

                    @Override
                    public void dealwithUserOffline() {
                        Log.i(TAG, "getRemoteHistoryList dealwithUserOffline: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "getRemoteHistoryList onComplete: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }
                });
    }

    @Override
    public void releaseHistoryResource() {
        INSTANCE = null;
        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
    }

    private String getServerResultMessage(String str) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(str);
            return jsonObject.optString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            result = "parse json occur error";
        } catch (Exception e) {
            e.printStackTrace();
            result = "parse responseBody occur error";
        }
        return result;
    }

    private void addRemoteHistoryRecord(String token, String userID, @NonNull UserCenterPageBean.Bean bean) {
        String mType = "0";
        String parentId = "";
        String subId = "";

        String type = bean.get_contenttype();
        if (Constant.CONTENTTYPE_PG.equals(type) || Constant.CONTENTTYPE_CP.equals(type)) {
            mType = "1";
            subId = bean.get_contentuuid();
        } else {
            mType = "0";
            parentId = bean.get_contentuuid();
            subId = bean.getPlayId();
        }

        String Authorization = "Bearer " + token;
        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .addHistory(Authorization,
                        userID,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        /*bean.get_contentuuid()*/parentId,
                        bean.get_title_name(),
                        mType,
                        bean.get_imageurl(),
                        bean.getProgress(),
                        null,
                        bean.getDuration(),
                        bean.getPlayPosition(),
                        false,
                        true,
                        /*bean.getPlayId()*/subId,
                        bean.getGrade(),
                        bean.getVideoType(),
                        bean.getTotalCnt(),
                        bean.getSuperscript(),
                        bean.get_contenttype(),
                        bean.getPlayIndex(),
                        bean.get_actiontype(),
                        bean.getProgramChildName(),
                        bean.getContentId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                        mAddDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String responseString = responseBody.string();
                            Log.d(TAG, "wqs:addRemoteHistoryRecord onNext result : " + responseString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "wqs:addRemoteHistoryRecord onError result : " + e.toString());
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "wqs:addRemoteHistoryRecord onComplete: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }
                });
    }
}
