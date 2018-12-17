package tv.newtv.cboxtv.uc.v2.data.collection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONArray;
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
 * 创建日期:     2018/9/26 0021
 */
public class CollectRemoteDataSource implements CollectDataSource {
    private static final String TAG = "CollectRemoteDataSource";

    private static CollectRemoteDataSource INSTANCE;
    private Context mContext;
    private Disposable mAddDisposable, mGetListDisposable, mDeleteDisposable;

    public static CollectRemoteDataSource getInstance(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = new CollectRemoteDataSource(mContext);
        }
        return INSTANCE;
    }

    public CollectRemoteDataSource(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void addRemoteCollect(@NonNull UserCenterPageBean.Bean bean) {
        String mType = "0";
        String parentId = "";
        String subId = "";
        String type = bean.get_contenttype();

        if (Constant.CONTENTTYPE_PS.equals(type) || Constant.CONTENTTYPE_CG.equals(type) || Constant.CONTENTTYPE_CS.equals(type)) {
            mType = "0";
            parentId = bean.get_contentuuid();
            subId = bean.getPlayId();
        } else if (Constant.CONTENTTYPE_PG.equals(type) || Constant.CONTENTTYPE_CP.equals(type)) {
            mType = "1";
            subId = bean.get_contentuuid();
        }

        String Authorization = "Bearer " + SharePreferenceUtils.getToken(mContext);
        String User_id = SharePreferenceUtils.getUserId(mContext);

        Log.e(TAG, "report collect item user_id " + User_id);

        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .addCollect(Authorization,
                        User_id,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        parentId,
                        bean.get_title_name(),
                        mType,
                        bean.get_imageurl(),
                        subId,
                        bean.getGrade(),
                        bean.getVideoType(),
                        bean.getTotalCnt(),
                        bean.getTotalCnt(),
                        bean.get_contenttype(),
                        bean.getPlayIndex(),
                        bean.get_actiontype(),
                        bean.getProgramChildName(),
                        bean.getContentId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "addRemoteCollect onSubscribe: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                        mAddDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "addRemoteCollect onNext: ");
                        try {
                            String responseString = responseBody.string();
                            checkUserOffline(responseString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "addRemoteCollect onError: ");

                        e.printStackTrace();
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void dealwithUserOffline() {
                        Log.i(TAG, "addRemoteCollect dealwithUserOffline: ");
                    }

                    @Override
                    public void onComplete() {

                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }
                });
    }

    @Override
    public void addRemoteCollectList(String token, String userID, @NonNull List<UserCenterPageBean.Bean> beanList, AddRemoteCollectListCallback callback) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                try {
                    if (!TextUtils.isEmpty(userID)) {
                        if (beanList != null && beanList.size() > 0) {
                            for (int i = 0; i < beanList.size(); i++) {
                                addRemoteCollectRecord(token, userID, beanList.get(i));
                            }
                            e.onNext(beanList.size());
                        } else {
                            Log.e(TAG, "wqs:addRemoteCollectList:beanList==null||beanList.size==0");
                            e.onNext(0);
                        }
                    } else {
                        Log.e(TAG, "wqs:addRemoteCollectList:userID==null");
                        e.onNext(0);
                    }

                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.e(TAG, "wqs:addRemoteCollectList:Exception:" + exception.toString());
                    e.onNext(0);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer size) throws Exception {
                        if (callback != null) {
                            callback.onAddRemoteCollectListComplete(size);
                        }
                    }
                });
    }

    @Override
    public void deleteRemoteCollect(@NonNull UserCenterPageBean.Bean Collect) {
        String mType = "0";
        String type = Collect.get_contenttype();

        if (Constant.CONTENTTYPE_PS.equals(type) || Constant.CONTENTTYPE_CG.equals(type) || Constant.CONTENTTYPE_CS.equals(type)) {
            mType = "0";
        } else if (Constant.CONTENTTYPE_PG.equals(type) || Constant.CONTENTTYPE_CP.equals(type)) {
            mType = "1";
        }

        String Authorization = "Bearer " + SharePreferenceUtils.getToken(mContext);
        String User_id = SharePreferenceUtils.getUserId(mContext);
        Log.e("UserId", User_id + "");
        String[] programset_ids = new String[]{Collect.get_contentuuid()};
        String contentID = Collect.getContentId();
        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .deleteCollect(Authorization,
                        User_id,
                        mType,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        "", contentID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "deleteRemoteCollect onSubscribe: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                        mDeleteDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "deleteRemoteCollect onNext: ");
                        try {
                            String responseString = responseBody.string();
                            checkUserOffline(responseString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "deleteRemoteCollect onError: ");

                        e.printStackTrace();
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }

                    @Override
                    public void dealwithUserOffline() {
                        Log.i(TAG, "deleteRemoteCollect dealwithUserOffline: ");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "deleteRemoteCollect onComplete: ");

                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }
                });
    }

    @Override
    public void getRemoteCollectList(String token, final String userId, String appKey, String channelCode, String offset, final String limit, @NonNull final CollectRemoteDataSource.GetCollectListCallback callback) {
        String Authorization = "Bearer " + token;
        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .getCollectList(Authorization, userId, "", Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "getRemoteCollectList onSubscribe: ");
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                        mGetListDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.i(TAG, "getRemoteCollectList onNext: ");

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
                                entity.setVideoType(item.optString("video_type"));
                                entity.setSuperscript(item.optString("superscript"));
                                entity.setTotalCnt(item.optString("total_count"));
                                entity.setPlayIndex(item.optString("latest_episode"));
                                entity.setEpisode_num(item.optString("episode_num"));
                                entity.setIsUpdate(item.optString("update_superscript"));
                                entity.setPlayIndex(item.optString("episode_num"));
                                entity.setUpdateTime(Long.parseLong(item.optString("create_time")) / 1000);
                                infos.add(entity);
                            }

                            if (callback != null) {
                                callback.onCollectListLoaded(infos, totalSize);
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
                        Log.i(TAG, "getRemoteCollectList onError: ");

                        if (callback != null) {
                            callback.onCollectListLoaded(null, 0);
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }

                    @Override
                    public void dealwithUserOffline() {
                        Log.i(TAG, "getRemoteCollectList dealwithUserOffline: ");

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "getRemoteCollectList onComplete: ");

                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }
                });
    }

    @Override
    public void releaseCollectResource() {
        INSTANCE = null;
        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
    }

    private void addRemoteCollectRecord(String token, String userID, @NonNull UserCenterPageBean.Bean bean) {
        String mType = "0";
        String parentId = "";
        String subId = "";
        String type = bean.get_contenttype();

        if (Constant.CONTENTTYPE_PS.equals(type) || Constant.CONTENTTYPE_CG.equals(type) || Constant.CONTENTTYPE_CS.equals(type)) {
            mType = "0";
            parentId = bean.get_contentuuid();
            subId = bean.getPlayId();
        } else if (Constant.CONTENTTYPE_PG.equals(type) || Constant.CONTENTTYPE_CP.equals(type)) {
            mType = "1";
            subId = bean.get_contentuuid();
        }

        String Authorization = "Bearer " + token;

        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .addCollect(Authorization,
                        userID,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        parentId,
                        bean.get_title_name(),
                        mType,
                        bean.get_imageurl(),
                        subId,
                        bean.getGrade(),
                        bean.getVideoType(),
                        bean.getTotalCnt(),
                        bean.getTotalCnt(),
                        bean.get_contenttype(),
                        bean.getPlayIndex(),
                        bean.get_actiontype(),
                        bean.getProgramChildName(),
                        bean.getContentId())
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
                            Log.d(TAG, "wqs:addRemoteCollectRecord onNext result : " + responseString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "wqs:addRemoteCollectRecord onError result : " + e.toString());
                        e.printStackTrace();
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onComplete() {
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }
                });
    }

}
