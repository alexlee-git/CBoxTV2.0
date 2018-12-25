package tv.newtv.cboxtv.uc.v2.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.db.SqlCondition;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.data.collection.CollectDataSource;
import tv.newtv.cboxtv.uc.v2.data.collection.CollectRemoteDataSource;
import tv.newtv.cboxtv.uc.v2.data.collection.CollectRepository;
import tv.newtv.cboxtv.uc.v2.data.follow.FollowDataSource;
import tv.newtv.cboxtv.uc.v2.data.follow.FollowRemoteDataSource;
import tv.newtv.cboxtv.uc.v2.data.follow.FollowRepository;
import tv.newtv.cboxtv.uc.v2.data.history.HistoryDataSource;
import tv.newtv.cboxtv.uc.v2.data.history.HistoryRemoteDataSource;
import tv.newtv.cboxtv.uc.v2.data.history.HistoryRepository;
import tv.newtv.cboxtv.uc.v2.data.subscribe.SubDataSource;
import tv.newtv.cboxtv.uc.v2.data.subscribe.SubRemoteDataSource;
import tv.newtv.cboxtv.uc.v2.data.subscribe.SubRepository;
import tv.newtv.cboxtv.uc.v2.listener.ICarouselInfoCallback;
import tv.newtv.cboxtv.uc.v2.listener.ICollectionStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.IFollowStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.IHisoryStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyLoginStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.ISubscribeStatusCallback;
import tv.newtv.cboxtv.uc.v2.sub.QueryUserStatusUtil;
import tv.newtv.cboxtv.utils.DBUtil;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         上午10:21
 * 创建人:           lixin
 * 创建日期:         2018/9/27
 */


public class UserCenterRecordManager {

    private final int SYNC_SWITCH_ON = 0;  // 数据需要同步
    private final int SYNC_SWITCH_OFF = 1; // 数据无需同步

    public static final String REQUEST_RECORD_OFFSET = "1";
    public static final String REQUEST_RECORD_LIMIT = "100";
    public static final String REQUEST_LIST_PAGE_RECORD_LIMIT = "100";//各个记录列表页面获取数据上限
    public static final String REQUEST_HOME_PAGE_RECORD_LIMIT = "6";//我的首页推荐页面获取数据上限

    // private String tableName;
    private final String TAG = "UserCenterRecordManager";

    private HashMap<Long, CallbackForm> callbackHashMap;

    private static class CallbackForm {
        private Object callback;
        private Disposable mDisposable;

        public void destroy() {
            callback = null;
            if (mDisposable != null) {
                if (!mDisposable.isDisposed()) {
                    mDisposable.dispose();
                }
                mDisposable = null;
            }
        }
    }

    public enum USER_CENTER_RECORD_TYPE {
        TYPE_SUBSCRIBE,
        TYPE_COLLECT,
        TYPE_HISTORY,
        TYPE_FOLLOW,
        TYPE_LUNBO
    }

    @SuppressLint("UseSparseArrays")
    private UserCenterRecordManager() {
        callbackHashMap = new HashMap<>();
    }

    private static UserCenterRecordManager mInstance;

    public static UserCenterRecordManager getInstance() {
        if (mInstance == null) {
            synchronized (UserCenterRecordManager.class) {
                mInstance = new UserCenterRecordManager();
            }
        }
        return mInstance;
    }

    public void addRecord(final USER_CENTER_RECORD_TYPE type,
                          final Context context,
                          final Bundle bundle,
                          final Content info,
                          final DBCallback<String> dbCallback) {
        if (context == null) {
            return;
        }

        if (bundle == null) {
            return;
        }

        if (type != USER_CENTER_RECORD_TYPE.TYPE_LUNBO && info == null) {
            return;
        }

        Log.d(TAG, "addRecord, type : " + type + ", name  : " + bundle.getString(DBConfig.TITLE_NAME));

        Observable.create(new ObservableOnSubscribe<Bundle>() {
            @Override
            public void subscribe(ObservableEmitter<Bundle> e) throws Exception {
                TokenRefreshUtil.getInstance().isTokenRefresh(context);
                String userId = "";
                String token = SharePreferenceUtils.getToken(context);
                if (!TextUtils.isEmpty(token)) {
                    userId = SharePreferenceUtils.getUserId(context);
                } else {
                    userId = SystemUtils.getDeviceMac(context);
                }

                bundle.putString("user_id", userId);
                bundle.putString("token", token);
                e.onNext(bundle);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bundle>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "addRecord onSubscribe: ");
                    }

                    @Override
                    public void onNext(Bundle bundle) {
                        Log.i(TAG, "addRecord onNext: ");

                        if (bundle == null) {
                            return;
                        }

                        String token = bundle.getString("token");
                        String userId = bundle.getString("user_id");

                        if (type == USER_CENTER_RECORD_TYPE.TYPE_COLLECT) {
                            procAddCollection(userId, token, context, bundle, info, dbCallback);
                        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_FOLLOW) {
                            procAddFollow(userId, token, context, bundle, info, dbCallback);
                        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_SUBSCRIBE) {
                            procAddSubscribe(userId, token, context, bundle, info, dbCallback);
                        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_HISTORY) {
                            procAddHistoryRecord(userId, context, token, bundle, info, dbCallback);
                        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_LUNBO) {
                            procAddCarouselPlayRecord(userId, token, context, bundle, dbCallback);
                        } else {
                            Log.e(TAG, "unresolved record type : " + type);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "addRecord onError: ");

                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "addRecord onComplete: ");

                    }
                });
    }

    /**
     * @param type
     * @param context
     * @param contentID   节目的唯一标识
     * @param contentType
     * @param dataUserId
     * @param dbCallback
     */
    public void deleteRecord(USER_CENTER_RECORD_TYPE type, Context context, String contentID, String contentType, String dataUserId, DBCallback<String> dbCallback) {
        if (context == null) {
            return;
        }

        if (TextUtils.isEmpty(contentID)) {
            return;
        }

        if (type == USER_CENTER_RECORD_TYPE.TYPE_COLLECT) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENT_ID, contentID);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteCollectionRecord(context, bundle, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_FOLLOW) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENT_ID, contentID);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteFollowRecord(context, bundle, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_SUBSCRIBE) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENT_ID, contentID);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteSubscribeRecord(context, bundle, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_HISTORY) {
            procDeleteHistoryRecord(dataUserId, context, contentID, contentType, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_LUNBO) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENT_ID, contentID);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteCarouselChannelRecord(context, bundle, dbCallback);
        }
    }

    /**
     * 添加历史记录
     *
     * @param context
     * @param token
     * @param bundle
     * @param info
     */
    private void procAddHistoryRecord(String userId, Context context, String token, Bundle bundle, Content info, DBCallback<String> callback) {
        try {
            // 计算百分比形式的播放进度
            long position = Long.parseLong(bundle.getString(DBConfig.PLAYPOSITION));
            long duration = Long.parseLong(bundle.getString(DBConfig.CONTENT_DURATION));

            //2018.10.23 wqs 避免duration为0导致的除数为0的异常
            String progress = "";
            if (duration > 0) {
                //2018.11.25 wqs 规避由于详情页传的进度误差导致的进度没有达到100%问题，误差范围暂定为2000毫秒
                if (duration > 2000 && position >= duration - 2000) {
                    progress = "100";
                } else {
                    progress = String.valueOf(position * 100 / duration);
                }

            } else {
                progress = "0";
            }
            bundle.putString(DBConfig.PLAY_PROGRESS, progress);
            //2018.12.21 wqs 防止插入时间有误，统一插入时间
            bundle.putString(DBConfig.UPDATE_TIME, TimeUtil.getInstance().getCurrentTimeInMillis() + "");
            String tableName = "";
            if (TextUtils.isEmpty(token)) { // 如果用户未登录
                tableName = DBConfig.HISTORY_TABLE_NAME;
            } else {
                tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
                if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) { // 依据同步开关状态判断是否需上报用户中心服务端
                    HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context)).addRemoteHistory(packageData(bundle));
                }
            }

            DBUtil.addHistory(userId, info, bundle, callback, tableName);
            Log.d(TAG, "procAddHistoryRecord add history complete, tableName : " + tableName + ", userId : " + userId + ", name : " + info.getTitle() + ", progress : " + progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procAddSubscribe(String userId, String token, Context context, Bundle bundle, Content info, DBCallback<String> callback) {
        String tableName = "";
        if (TextUtils.isEmpty(token)) { // 如果用户未登录
            tableName = DBConfig.SUBSCRIBE_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) { // 依据同步开关状态判断是否需上报用户中心服务端
                SubRepository.getInstance(SubRemoteDataSource.getInstance(context)).addRemoteSubscribe(packageData(bundle));
            }
        }

        DBUtil.AddSubcribe(userId, info, bundle, callback, tableName);
        Log.d(TAG, "procAddSubscribe add subscribe complete, tableName : " + tableName + ", userId : " + userId + ", name : " + info.getTitle());
    }

    private void procAddCollection(String userId, String token, Context context, Bundle bundle, Content info, DBCallback<String> callback) {
        String tableName = "";
        if (TextUtils.isEmpty(token)) { // 如果用户未登录
            tableName = DBConfig.COLLECT_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_COLLECT_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) { // 依据同步开关状态判断是否需上报用户中心服务端
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).addRemoteCollect("0", packageData(bundle));
            }
        }

        DBUtil.PutCollect(userId, info, bundle, callback, tableName);
        Log.d(TAG, "proAddCollection add collection complete, tableName : " + tableName + ", userId : " + userId + ", name : " + info.getTitle());
    }

    private void procAddFollow(String userId, String token, Context context, Bundle bundle, Content info, DBCallback<String> callback) {
        String tableName = "";
        if (TextUtils.isEmpty(token)) {
            tableName = DBConfig.ATTENTION_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_ATTENTION_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context))
                        .addRemoteFollow(packageData(bundle));
            }
        }

        DBUtil.addAttention(userId, info, bundle, callback, tableName);
        Log.d(TAG, "proAddFollow add follow complete, tableName : " + tableName + ", userId : " + userId + ", name : " + info.getTitle());
    }

    private void procAddCarouselPlayRecord(String userId, String token, Context context, Bundle bundle, DBCallback<String> callback) {
        String tableName = "";
        if (TextUtils.isEmpty(token)) { // 如果用户未登录
            tableName = DBConfig.LB_COLLECT_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_LB_COLLECT_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) { // 依据同步开关状态判断是否需上报用户中心服务端
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).addRemoteCollect("1", packageData(bundle));
            }
        }

        DBUtil.addCarouselChannelRecord(userId, tableName, bundle, callback);
        Log.d(TAG, "procAddCarouselPlayRecord add collection complete, tableName : " + tableName + ", userId : " + userId);
    }


    /**
     * 删除历史记录数据
     *
     * @param context
     * @param contentID   待删除的历史记录的contentID值,如果是全部则传"clean", 如果是多个则用逗号将id隔开
     * @param contentType
     */
    public void procDeleteHistoryRecord(String dataUserId, Context context, String contentID, String contentType, DBCallback<String> callback) {
        Log.d(TAG, "删除 dataUserId : " + dataUserId + ", contentID : " + contentID);
        if (TextUtils.isEmpty(contentID)) {
            return;
        }

        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        // String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            if (TextUtils.equals(contentID, "clean")) {
                DataSupport.delete(DBConfig.HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                        .build()
                        .withCallback(callback).excute();
            } else {
                DataSupport.delete(DBConfig.HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                        .eq(DBConfig.CONTENT_ID, contentID)
                        .build()
                        .withCallback(callback).excute();
            }
        } else { // 已登录分支
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context))
                        .deleteRemoteHistory("Bearer " + token, userId,
                                contentType,
                                Libs.get().getAppKey(),
                                Libs.get().getChannelId(),
                                contentID);
            }

            if (TextUtils.equals(contentID, "clean")) {
                DataSupport.delete(DBConfig.REMOTE_HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                        .build()
                        .withCallback(callback).excute();
                Log.d(TAG, "删除全部历史的远程数据, dataUserId : " + dataUserId + ", contentID : " + contentID);
            } else {
                DataSupport.delete(DBConfig.REMOTE_HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                        .eq(DBConfig.CONTENT_ID, contentID)
                        .build()
                        .withCallback(callback).excute();
                Log.d(TAG, "单点删除远程数据, dataUserId : " + dataUserId + ", contentID : " + contentID);
            }
        }

        Log.d(TAG, "procDeleteHistoryRecord delete history complete, userId : " + userId + ", id : " + contentID);
    }

    private void procDeleteCarouselChannelRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String contentID = bundle.getString(DBConfig.CONTENT_ID);
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            DBUtil.UnCollect(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentID, callback, DBConfig.LB_COLLECT_TABLE_NAME);
        } else {
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).deleteRemoteCollect("1", packageData(bundle));
            }

            Log.d(TAG, "登录用户, 删除远程收藏表数据, contentID : " + contentID);
            DBUtil.deleteCarouselChannelRecord(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentID, callback, DBConfig.REMOTE_LB_COLLECT_TABLE_NAME);
        }
    }

    private void procDeleteCollectionRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String contentID = bundle.getString(DBConfig.CONTENT_ID);
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            DBUtil.UnCollect(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentID, callback, DBConfig.COLLECT_TABLE_NAME);
        } else {
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).deleteRemoteCollect("0", packageData(bundle));
            }

            Log.d(TAG, "登录用户, 删除远程表数据, contentID : " + contentID);
            DBUtil.UnCollect(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentID, callback, DBConfig.REMOTE_COLLECT_TABLE_NAME);
        }
    }

    private void procDeleteFollowRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String contentID = bundle.getString(DBConfig.CONTENT_ID);

        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            DBUtil.delAttention(userId, contentID, callback, DBConfig.ATTENTION_TABLE_NAME);
        } else {
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context)).deleteRemoteFollow(packageData(bundle));
            }

            DBUtil.UnCollect(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentID, callback, DBConfig.REMOTE_ATTENTION_TABLE_NAME);
        }
    }

    private void procDeleteSubscribeRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String contentID = bundle.getString(DBConfig.CONTENT_ID);
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) { // 如果是未登录用户,则删除本地表
            DBUtil.UnSubcribe(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentID, callback, DBConfig.SUBSCRIBE_TABLE_NAME);
        } else { // 如果是登录用户,则删除远程表
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                SubRepository.getInstance(SubRemoteDataSource.getInstance(context)).deleteRemoteSubscribe(packageData(bundle));
            }

            DBUtil.UnSubcribe(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentID, callback, DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME);
        }

        Log.d(TAG, "procDeleteSubscribeRecord delete subscribe complete, userId : " + userId + ", name : " + bundle.getString(DBConfig.TITLE_NAME));
    }


    /**
     * 从服务端获取历史数据记录列表
     *
     * @param context
     * @param callback
     */
    public void getRemoteHistoryList(Context context, String token, String userId, String offset, String limit, @NonNull HistoryDataSource.GetHistoryListCallback callback) {

        HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context))
                .getRemoteHistoryList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit, callback);
    }

    public void getRemoteCollectionList(String collectType, Context context, String token, String userId, String offset, String limit, @NonNull CollectRemoteDataSource.GetCollectListCallback callback) {
        CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context))
                .getRemoteCollectList(collectType, token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit, callback);
    }

    public void getRemoteFollowList(Context context, String token, String userId, String offset, String limit, FollowDataSource.GetFollowListCallback callback) {
        FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context))
                .getRemoteFollowList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit, callback);
    }

    public void getRemoteSubscribe(Context context, String token, String userId, String offset, String limit, SubDataSource.GetSubscribeListCallback callback) {
        SubRepository.getInstance(SubRemoteDataSource.getInstance(context))
                .getRemoteSubscribeList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit, callback);
    }

    /**
     * 批量将本地数据库数据上报至远程服务器
     *
     * @param context  上下文
     * @param token    token值
     * @param userId   用户ID
     * @param beanList 批量上传的list
     * @param callback 回调结果
     */
    public void addRemoteHistoryList(Context context, String token, String userId, @NonNull List<UserCenterPageBean.Bean> beanList, @NonNull HistoryDataSource.AddRemoteHistoryListCallback callback) {

        HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context))
                .addRemoteHistoryList(token, userId, beanList, callback);
    }

    public void addRemoteCollectList(String collectType, Context context, String token, String userId, @NonNull List<UserCenterPageBean.Bean> beanList, CollectDataSource.AddRemoteCollectListCallback callback) {
        CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context))
                .addRemoteCollectList(collectType, token, userId, beanList, callback);
    }

    public void addRemoteFollowList(Context context, String token, String userId, @NonNull List<UserCenterPageBean.Bean> beanList, FollowRemoteDataSource.AddRemoteFollowListCallback callback) {
        FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context))
                .addRemoteFollowList(token, userId, beanList, callback);
    }

    public void addRemoteSubscribeList(Context context, String token, String userId, @NonNull List<UserCenterPageBean.Bean> beanList, SubRemoteDataSource.AddRemoteSubscribeListCallback callback) {
        SubRepository.getInstance(SubRemoteDataSource.getInstance(context))
                .addRemoteSubscribeList(token, userId, beanList, callback);
    }

    /**
     * 批量插入数据至数据库
     */
    public void addCollectToDataBase(final String userId, final List<UserCenterPageBean.Bean> list, final DBCallback<String> callback, final String tableName) {
        try {
            if (list != null) {
                DBUtil.clearTableAll(tableName, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.d(TAG, "wqs:addCollectToDataBase:clear:code:" + code);
                        Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            UserCenterPageBean.Bean bean = iterator.next();
                            Content info = new Content();
                            info.setContentID(bean.getContentId());
                            info.setContentUUID(bean.get_contentuuid());
                            info.setContentType(bean.get_contenttype());
                            info.setVImage(bean.get_imageurl());
                            info.setTitle(bean.get_title_name());
                            // info.setrSubScript(bean.getSuperscript());
                            info.setGrade(bean.getGrade());
                            info.setRecentNum(bean.getEpisode_num());
                            info.setSeriesSum(bean.getTotalCnt());
                            info.setVideoType(bean.getVideoType());
                            info.setRecentMsg(bean.getRecentMsg());
                            Bundle bundle = new Bundle();
                            bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                            DBUtil.PutCollect(userId, info, bundle, callback, tableName);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:addCollectToDataBase:Exception:" + e.toString());
        }
    }

    /**
     * 批量插入数据至数据库
     */
    public void addLbCollectToDataBase(final String userId, final List<UserCenterPageBean.Bean> list, final DBCallback<String> callback, final String tableName) {
        try {
            if (list != null) {
                DBUtil.clearTableAll(tableName, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.d(TAG, "wqs:addCollectToDataBase:clear:code:" + code);
                        Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            UserCenterPageBean.Bean bean = iterator.next();
                            Bundle bundle = new Bundle();
                            bundle.putString(DBConfig.CONTENTUUID, bean.get_contentuuid());
                            bundle.putString(DBConfig.CONTENT_ID, bean.getContentId());
                            bundle.putString(DBConfig.TITLE_NAME, bean.get_title_name());
                            bundle.putString(DBConfig.IS_FINISH, bean.getIs_finish());
                            bundle.putString(DBConfig.REAL_EXCLUSIVE, bean.getReal_exclusive());
                            bundle.putString(DBConfig.ISSUE_DATE, bean.getIssue_date());
                            bundle.putString(DBConfig.LAST_PUBLISH_DATE, bean.getLast_publish_date());
                            bundle.putString(DBConfig.SUB_TITLE, bean.getSub_title());
                            bundle.putString(DBConfig.V_IMAGE, bean.getV_image());
                            bundle.putString(DBConfig.H_IMAGE, bean.getH_image());
                            bundle.putString(DBConfig.VIP_FLAG, bean.getVip_flag());
                            bundle.putString(DBConfig.CONTENTTYPE, bean.get_contenttype());
                            bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                            DBUtil.addCarouselChannelRecord(userId, tableName, bundle, callback);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:addCollectToDataBase:Exception:" + e.toString());
        }
    }

    public void addSubscribeToDataBase(final String userId, final List<UserCenterPageBean.Bean> list, final DBCallback<String> callback, final String tableName) {
        try {
            if (list != null) {
                DBUtil.clearTableAll(tableName, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.d(TAG, "wqs:addSubscribeToDataBase:clear:code:" + code);
                        Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            UserCenterPageBean.Bean bean = iterator.next();
                            Content info = new Content();
                            Log.d("sub", "addSubscribeToDataBase contentid : " + bean.getContentId() + ", contentuuid : " + bean.get_contentuuid());
                            info.setContentID(bean.getContentId());
                            info.setContentUUID(bean.get_contentuuid());
                            info.setContentType(bean.get_contenttype());
                            info.setVImage(bean.get_imageurl());
                            info.setTitle(bean.get_title_name());
                            info.setGrade(bean.getGrade());
                            info.setRecentNum(bean.getEpisode_num());
                            info.setSeriesSum(bean.getTotalCnt());
                            info.setVideoType(bean.getVideoType());
                            info.setRecentMsg(bean.getRecentMsg());
                            // info.setrSuperScript(bean.getSuperscript());
                            Bundle bundle = new Bundle();
                            bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                            DBUtil.AddSubcribe(userId, info, bundle, callback, tableName);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:addSubscribeToDataBase:Exception:" + e.toString());
        }
    }

    public void addFollowToDataBase(final String userId, final List<UserCenterPageBean.Bean> list, final DBCallback<String> callback, final String tableName) {
        try {
            if (list != null) {
                DBUtil.clearTableAll(tableName, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.d(TAG, "wqs:addFollowToDataBase:clear:code:" + code);
                        Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            UserCenterPageBean.Bean bean = iterator.next();
                            Content info = new Content();
                            info.setContentID(bean.getContentId());
                            info.setContentUUID(bean.get_contentuuid());
                            info.setContentType(bean.get_contenttype());
                            info.setVImage(bean.get_imageurl());
                            info.setTitle(bean.get_title_name());
                            info.setRecentMsg(bean.getRecentMsg());
                            // info.setrSubScript(bean.getSuperscript());
                            info.setGrade(bean.getGrade());
                            Bundle bundle = new Bundle();
                            bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                            DBUtil.addAttention(userId, info, bundle, callback, tableName);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:addFollowToDataBase:Exception:" + e.toString());
        }
    }

    public void addHistoryToDataBase(final String userId, final List<UserCenterPageBean.Bean> list, final DBCallback<String> callback, final String tableName) {
        try {
            if (list != null) {
                DBUtil.clearTableAll(tableName, new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.d(TAG, "wqs:addHistoryToDataBase:clear:code:" + code);
                        Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            UserCenterPageBean.Bean bean = iterator.next();
                            Content info = new Content();
                            info.setContentID(bean.getContentId());
                            info.setContentUUID(bean.get_contentuuid());
                            info.setContentType(bean.get_contenttype());
                            info.setVImage(bean.get_imageurl());
                            info.setTitle(bean.get_title_name());
                            // info.setrSuperScript(bean.getSuperscript());
                            info.setGrade(bean.getGrade());
                            info.setRecentNum(bean.getEpisode_num());
                            info.setSeriesSum(bean.getTotalCnt());
                            info.setVideoType(bean.getVideoType());
                            info.setRecentMsg(bean.getRecentMsg());
                            if (TextUtils.isEmpty(bean.getPlayId())) {
                                List<SubContent> programsInfos = new ArrayList<>(Constant.BUFFER_SIZE_4);
                                SubContent item = new SubContent();
                                item.setContentID(bean.getPlayId());
                                item.setPeriods(bean.getPlayIndex());
                                programsInfos.add(item);
                                info.setData(programsInfos);
                            }
                            String playIndex = bean.getPlayIndex();
                            String playPos = bean.getPlayPosition();
                            int playIdx = 0;
                            int playPosition = 0;
                            if (!TextUtils.isEmpty(playIndex)) {
                                playIdx = Integer.parseInt(playIndex.trim());
                            }
                            if (!TextUtils.isEmpty(playPos)) {
                                playPosition = Integer.parseInt(playPos.trim());
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString(DBConfig.PLAY_PROGRESS, bean.getProgress());
                            bundle.putString(DBConfig.PLAYINDEX, String.valueOf(playIndex));
                            bundle.putString(DBConfig.PLAYPOSITION, String.valueOf(playPosition));
                            bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                            bundle.putString(DBConfig.CONTENT_DURATION, bean.getDuration());
                            DBUtil.addHistory(userId, info, bundle, callback, tableName);
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:addHistoryToDataBase:Exception:" + e.toString());
        }
    }

    public int currentHistoryIndex = 1;//历史记录当前插入数据完成的个数
    public int currentSubIndex = 1;//订阅列表当前插入数据完成的个数
    public int currentCollectIndex = 1;//收藏列表当前插入数据完成的个数
    public int currentLbCollectIndex = 1;//轮播收藏列表当前插入数据完成的个数
    public int currentFollowIndex = 1;//关注列表当前插入数据完成的个数
    private boolean getHistoryRecordComplete = false;//获取历史记录完成状态
    private boolean getCollectionRecordComplete = false;//获取收藏记录完成状态
    private boolean getLbCollectionRecordComplete = false;//获取轮播收藏记录完成状态
    private boolean getFollowRecordComplete = false;//获取关注记录完成状态
    private boolean getSubscribeRecordComplete = false;//获取订阅记录完成状态

    /**
     * 批量获取云端数据库数据并同步到本地数据库中
     *
     * @param context
     * @param offset
     * @param limit
     */
    public void getUserBehavior(final Context context, final String offset, final String limit) {
        currentHistoryIndex = 1;
        currentSubIndex = 1;
        currentCollectIndex = 1;
        currentFollowIndex = 1;
        currentLbCollectIndex = 1;
        getHistoryRecordComplete = false;
        getCollectionRecordComplete = false;
        getFollowRecordComplete = false;
        getSubscribeRecordComplete = false;
        getLbCollectionRecordComplete = false;
        QueryUserStatusUtil.getInstance().getLoginStatus(context, new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                if (status) {
                    String token = SharePreferenceUtils.getToken(context);
                    final String userId = SharePreferenceUtils.getUserId(context);
                    if (!TextUtils.isEmpty(token)) {
                        getRemoteHistoryList(context, token, userId, offset, limit, new HistoryDataSource.GetHistoryListCallback() {
                            @Override
                            public void onHistoryListLoaded(List<UserCenterPageBean.Bean> historyList, final int totalSize) {
                                Log.d(TAG, "wqs:historyList.size():" + totalSize);
                                if (historyList != null && historyList.size() > 0) {
                                    addHistoryToDataBase(userId, historyList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "wqs:currentHistoryIndex:" + currentHistoryIndex);
                                            if (currentHistoryIndex == totalSize) {
                                                getHistoryRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentHistoryIndex++;
                                        }
                                    }, DBConfig.REMOTE_HISTORY_TABLE_NAME);
                                } else {
                                    getHistoryRecordComplete = true;
                                    getUserBehaviorComplete(context);
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteSubscribe(context, token, userId, offset, limit, new SubDataSource.GetSubscribeListCallback() {
                            @Override
                            public void onSubscribeListLoaded(List<UserCenterPageBean.Bean> subList, final int totalSize) {
                                Log.d(TAG, "wqs:subList.size():" + totalSize);
                                if (subList != null && subList.size() > 0) {
                                    addSubscribeToDataBase(userId, subList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "wqs:currentSubIndex:" + currentSubIndex);
                                            if (currentSubIndex == totalSize) {
                                                getSubscribeRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentSubIndex++;
                                        }
                                    }, DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME);
                                } else {
                                    getSubscribeRecordComplete = true;
                                    getUserBehaviorComplete(context);
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteCollectionList("0", context, token, userId, offset, limit, new CollectDataSource.GetCollectListCallback() {
                            @Override
                            public void onCollectListLoaded(List<UserCenterPageBean.Bean> CollectList, final int totalSize) {
                                Log.d(TAG, "wqs:CollectList.size():" + totalSize);
                                if (CollectList != null && CollectList.size() > 0) {
                                    addCollectToDataBase(userId, CollectList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "wqs:currentCollectIndex:" + currentCollectIndex);
                                            if (currentCollectIndex == totalSize) {
                                                getCollectionRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentCollectIndex++;
                                        }
                                    }, DBConfig.REMOTE_COLLECT_TABLE_NAME);
                                } else {
                                    getCollectionRecordComplete = true;
                                    getUserBehaviorComplete(context);
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteFollowList(context, token, userId, offset, limit, new FollowDataSource.GetFollowListCallback() {
                            @Override
                            public void onFollowListLoaded(List<UserCenterPageBean.Bean> FollowList, final int totalSize) {
                                Log.d(TAG, "wqs:FollowList.size():" + totalSize);
                                if (FollowList != null && FollowList.size() > 0) {
                                    addFollowToDataBase(userId, FollowList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "wqs:currentFollowIndex:" + currentFollowIndex);
                                            if (currentFollowIndex == totalSize) {
                                                getFollowRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentFollowIndex++;
                                        }
                                    }, DBConfig.REMOTE_ATTENTION_TABLE_NAME);
                                } else {
                                    getFollowRecordComplete = true;
                                    getUserBehaviorComplete(context);
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteCollectionList("1", context, token, userId, offset, limit, new CollectDataSource.GetCollectListCallback() {
                            @Override
                            public void onCollectListLoaded(List<UserCenterPageBean.Bean> CollectList, final int totalSize) {
                                Log.d(TAG, "wqs:LbCollectList.size():" + totalSize);
                                if (CollectList != null && CollectList.size() > 0) {
                                    addLbCollectToDataBase(userId, CollectList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "wqs:currentLbCollectIndex:" + currentLbCollectIndex);
                                            if (currentLbCollectIndex == totalSize) {
                                                getLbCollectionRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentLbCollectIndex++;
                                        }
                                    }, DBConfig.REMOTE_LB_COLLECT_TABLE_NAME);
                                } else {
                                    getLbCollectionRecordComplete = true;
                                    getUserBehaviorComplete(context);
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                    } else {
                        Log.e(TAG, "wqs:getUserBehavior:token==null");
                    }
                } else {
                    Log.e(TAG, "wqs:getUserBehavior:loginStatus:" + status);
                }
            }
        });
    }

    //数据获取完成，向用户中心首页发送广播
    private void getUserBehaviorComplete(Context context) {
        if (getHistoryRecordComplete && getCollectionRecordComplete
                && getFollowRecordComplete && getSubscribeRecordComplete && getLbCollectionRecordComplete) {
            Log.d(TAG, "wqs:getUserBehaviorComplete:sendBroadcast");
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("action.uc.data.sync.complete"));
        } else {
            Log.e(TAG, "wqs:getUserBehaviorComplete:error");
        }

    }

    //远端数据库获取获取流程取消订阅关系，防止数据错乱与内存泄漏
    public void releaseUserBehavior(Context context) {
        Log.e(TAG, "wqs:releaseUserBehavior");
        HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context)).releaseHistoryResource();
        CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).releaseCollectResource();
        SubRepository.getInstance(SubRemoteDataSource.getInstance(context)).releaseSubscribeResource();
        FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context)).releaseFollowResource();
    }

    private boolean AddHistoryRecordComplete = false;//批量上报历史记录数据至远程数据库完成状态
    private boolean AddCollectionRecordComplete = false;//批量上报收藏记录数据至远程数据库完成状态
    private boolean AddLbCollectionRecordComplete = false;//批量上报轮播收藏记录数据至远程数据库完成状态
    private boolean AddFollowRecordComplete = false;//批量上报关注记录数据至远程数据库完成状态
    private boolean AddSubscribeRecordComplete = false;//批量上报订阅记录数据至远程数据库完成状态

    //数据批量上报完成，下一步获取云端数据库数据同步到本地数据库中
    private void AddUserBehaviorListComplete(Context context) {
        if (AddHistoryRecordComplete && AddCollectionRecordComplete
                && AddFollowRecordComplete && AddSubscribeRecordComplete && AddLbCollectionRecordComplete) {
            Log.d(TAG, "wqs:AddUserBehaviorListComplete:complete");
            getUserBehavior(context, UserCenterRecordManager.REQUEST_RECORD_OFFSET, UserCenterRecordManager.REQUEST_RECORD_LIMIT);
        } else {
            Log.e(TAG, "wqs:AddUserBehaviorListComplete:error");
        }

    }

    /**
     * 同步本地数据库与远程数据库数据
     *
     * @param context
     */
    public void synchronizationUserBehavior(final Context context) {
        //订阅数据表表名
        final String tableNameSubscribe = DBConfig.SUBSCRIBE_TABLE_NAME;
        //收藏数据表表名
        final String tableNameCollect = DBConfig.COLLECT_TABLE_NAME;
        //轮播收藏数据表表名
        final String tableNameLbCollect = DBConfig.LB_COLLECT_TABLE_NAME;
        //历史记录数据表表名
        final String tableNameHistory = DBConfig.HISTORY_TABLE_NAME;
        //关注数据表表名
        final String TableNameAttention = DBConfig.ATTENTION_TABLE_NAME;
        QueryUserStatusUtil.getInstance().getLoginStatus(context, new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                if (status) {
                    final String token = SharePreferenceUtils.getToken(context);
                    final String userId = SharePreferenceUtils.getUserId(context);
                    if (!TextUtils.isEmpty(token)) {
                        queryDataBase(tableNameHistory, new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> beanList = mGson.fromJson(result, type);
                                    addRemoteHistoryList(context, token, userId, beanList, new HistoryDataSource.AddRemoteHistoryListCallback() {
                                        @Override
                                        public void onAddRemoteHistoryListComplete(int totalSize) {
                                            Log.e(TAG, "wqs:onAddRemoteHistoryListComplete:size:" + totalSize);
                                            AddHistoryRecordComplete = true;
                                            AddUserBehaviorListComplete(context);
                                        }
                                    });
                                }
                            }
                        });
                        queryDataBase(tableNameCollect, new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> beanList = mGson.fromJson(result, type);
                                    addRemoteCollectList("0", context, token, userId, beanList, new CollectDataSource.AddRemoteCollectListCallback() {
                                        @Override
                                        public void onAddRemoteCollectListComplete(int totalSize) {
                                            Log.e(TAG, "wqs:onAddRemoteCollectListComplete:size:" + totalSize);
                                            AddCollectionRecordComplete = true;
                                            AddUserBehaviorListComplete(context);
                                        }
                                    });
                                }
                            }
                        });
                        queryDataBase(tableNameSubscribe, new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> beanList = mGson.fromJson(result, type);
                                    addRemoteSubscribeList(context, token, userId, beanList, new SubDataSource.AddRemoteSubscribeListCallback() {
                                        @Override
                                        public void onAddRemoteSubscribeListComplete(int totalSize) {
                                            Log.e(TAG, "wqs:onAddRemoteSubscribeListComplete:size:" + totalSize);
                                            AddSubscribeRecordComplete = true;
                                            AddUserBehaviorListComplete(context);
                                        }
                                    });
                                }
                            }
                        });
                        queryDataBase(TableNameAttention, new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> beanList = mGson.fromJson(result, type);
                                    addRemoteFollowList(context, token, userId, beanList, new FollowDataSource.AddRemoteFollowListCallback() {
                                        @Override
                                        public void onAddRemoteFollowListComplete(int totalSize) {
                                            Log.e(TAG, "wqs:onAddRemoteFollowListComplete:size:" + totalSize);
                                            AddFollowRecordComplete = true;
                                            AddUserBehaviorListComplete(context);
                                        }
                                    });
                                }
                            }
                        });
                        queryDataBase(tableNameLbCollect, new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (code == 0) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> beanList = mGson.fromJson(result, type);
                                    addRemoteCollectList("1", context, token, userId, beanList, new CollectDataSource.AddRemoteCollectListCallback() {
                                        @Override
                                        public void onAddRemoteCollectListComplete(int totalSize) {
                                            Log.e(TAG, "wqs:onAddLbRemoteCollectListComplete:size:" + totalSize);
                                            AddLbCollectionRecordComplete = true;
                                            AddUserBehaviorListComplete(context);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 查询某一个表的全量数据
     *
     * @param tableName
     */
    public void queryDataBase(String tableName, DBCallback<String> callback) {
        DataSupport.search(tableName)
                .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME_ASC)
                .build().withCallback(callback).excute();
    }


    private UserCenterPageBean.Bean packageData(Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        UserCenterPageBean.Bean pageBean = new UserCenterPageBean.Bean();
        pageBean.set_contentuuid(bundle.getString(DBConfig.CONTENTUUID));
        pageBean.set_title_name(bundle.getString(DBConfig.TITLE_NAME));
        pageBean.set_imageurl(bundle.getString(DBConfig.IMAGEURL));
        pageBean.setProgress(bundle.getString(DBConfig.PLAY_PROGRESS));
        pageBean.setDuration(bundle.getString(DBConfig.CONTENT_DURATION));
        pageBean.setPlayPosition(bundle.getString(DBConfig.PLAYPOSITION));
        pageBean.setGrade(bundle.getString(DBConfig.CONTENT_GRADE));
        pageBean.setVideoType(bundle.getString(DBConfig.VIDEO_TYPE));
        pageBean.setTotalCnt(bundle.getString(DBConfig.TOTAL_CNT));
        pageBean.setSuperscript(bundle.getString(DBConfig.SUPERSCRIPT));
        pageBean.set_contenttype(bundle.getString(DBConfig.CONTENTTYPE));
        pageBean.setPlayIndex(bundle.getString(DBConfig.PLAYINDEX));
        pageBean.set_actiontype(Constant.OPEN_DETAILS);
        pageBean.setPlayId(bundle.getString(DBConfig.PLAYID));
        pageBean.setProgramChildName(bundle.getString(DBConfig.PROGRAM_CHILD_NAME));
        pageBean.setContentId(bundle.getString(DBConfig.CONTENT_ID));
        pageBean.setIs_finish(bundle.getString(DBConfig.IS_FINISH));
        pageBean.setReal_exclusive(bundle.getString(DBConfig.REAL_EXCLUSIVE));
        pageBean.setIssue_date(bundle.getString(DBConfig.ISSUE_DATE));
        pageBean.setLast_publish_date(bundle.getString(DBConfig.LAST_PUBLISH_DATE));
        pageBean.setSub_title(bundle.getString(DBConfig.SUB_TITLE));
        pageBean.setV_image(bundle.getString(DBConfig.V_IMAGE));
        pageBean.setH_image(bundle.getString(DBConfig.H_IMAGE));
        pageBean.setVip_flag(bundle.getString(DBConfig.VIP_FLAG));
        pageBean.setAlternate_number(bundle.getString(DBConfig.ALTERNATE_NUMBER));
        return pageBean;
    }

    /**
     * 查询节目是否被订阅
     *
     * @param context   上下文
     * @param contentID 节目的唯一标识
     * @param callback  查询状态回调
     */
    public Long queryContentSubscribeStatus(final Context context, final String contentID,
                                            ISubscribeStatusCallback callback) {

        final Long callbackId = System.currentTimeMillis();
        final CallbackForm callbackForm = new CallbackForm();
        callbackForm.callback = callback;
        callbackHashMap.put(callbackId, callbackForm);

        io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(context);
                Log.d(TAG, "---queryContentSubscribeStatus:isTokenRefresh:status:" + status);
                //获取登录状态
                String mLoginTokenString = SharePreferenceUtils.getToken(context);
                if (!TextUtils.isEmpty(mLoginTokenString)) {
                    e.onNext(mLoginTokenString);
                } else {
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callbackForm.mDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            querySubscribeStatusByDB(SharePreferenceUtils.getUserId
                                    (LauncherApplication.AppContext), contentID, DBConfig
                                    .REMOTE_SUBSCRIBE_TABLE_NAME, callbackId);
                        } else {
                            querySubscribeStatusByDB(SystemUtils.getDeviceMac(LauncherApplication
                                    .AppContext), contentID, DBConfig.SUBSCRIBE_TABLE_NAME, callbackId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "---queryContentSubscribeStatus:onError:" + e.toString());
                        CallbackForm sendCallbackForm = callbackHashMap.get(callbackId);
                        if (sendCallbackForm != null) {
                            if (sendCallbackForm.callback != null) {
                                ((ISubscribeStatusCallback) sendCallbackForm.callback)
                                        .notifySubScribeStatus(false, callbackId);
                            }
                            if (sendCallbackForm.mDisposable != null) {
                                unSubscribe(sendCallbackForm.mDisposable);
                            }
                            removeCallback(callbackId);
                        }
                    }

                    @Override
                    public void onComplete() {
                        CallbackForm sendCallbackForm = callbackHashMap.get(callbackId);
                        if (sendCallbackForm != null) {
                            if (sendCallbackForm.mDisposable != null) {
                                unSubscribe(sendCallbackForm.mDisposable);
                            }
                            removeCallback(callbackId);
                        }
                    }
                });
        return callbackId;
    }

    /**
     * 查询节目是否被关注
     *
     * @param context   上下文
     * @param contentID 节目的唯一标识
     * @param callback  查询状态回调
     */
    public Long queryContentFollowStatus(final Context context, final String contentID, IFollowStatusCallback callback) {
        final Long callbackId = System.currentTimeMillis();
        final CallbackForm callbackForm = new CallbackForm();
        callbackForm.callback = callback;
        callbackHashMap.put(callbackId, callbackForm);
        io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(context);
                Log.d(TAG, "---queryContentFollowStatus:isTokenRefresh:status:" + status);
                //获取登录状态
                String mLoginTokenString = SharePreferenceUtils.getToken(context);
                if (!TextUtils.isEmpty(mLoginTokenString)) {
                    e.onNext(mLoginTokenString);
                } else {
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
//                        unSubscribe(mFollowDisposable);
//                        mFollowDisposable = d;
                        callbackForm.mDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            queryFollowStatusByDB(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentID, DBConfig.REMOTE_ATTENTION_TABLE_NAME, callbackId);
                        } else {
                            queryFollowStatusByDB(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentID, DBConfig.ATTENTION_TABLE_NAME, callbackId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "---queryContentFollowStatus:onError:" + e.toString());
                        CallbackForm sendCallback = callbackHashMap.get(callbackId);
                        if (sendCallback != null && sendCallback.callback != null) {
                            ((IFollowStatusCallback) sendCallback.callback).notifyFollowStatus(false, callbackId);
                            if (sendCallback.mDisposable != null) {
                                unSubscribe(sendCallback.mDisposable);
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        CallbackForm sendCallback = callbackHashMap.get(callbackId);
                        if (sendCallback != null) {
                            unSubscribe(sendCallback.mDisposable);
                        }
                    }
                });

        return callbackId;
    }

    /**
     * 查询节目是否被收藏
     *
     * @param context   上下文
     * @param contentID 节目的唯一标识
     * @param callback  查询状态回调
     */
    public Long queryContentCollectionStatus(final Context context, final String contentID, ICollectionStatusCallback callback) {
        final Long callbackId = System.currentTimeMillis();
        final CallbackForm callbackForm = new CallbackForm();
        callbackForm.callback = callback;
        callbackHashMap.put(callbackId, callbackForm);
        io.reactivex.Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(context);
                Log.d(TAG, "---queryContentCollectionStatus:isTokenRefresh:status:" + status);
                //获取登录状态
                String mLoginTokenString = SharePreferenceUtils.getToken(context);
                if (!TextUtils.isEmpty(mLoginTokenString)) {
                    e.onNext(mLoginTokenString);
                } else {
                    e.onNext("");
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callbackForm.mDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        if (!TextUtils.isEmpty(s)) {
                            queryCollectStatusByDB(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentID, DBConfig.REMOTE_COLLECT_TABLE_NAME, callbackId);
                        } else {
                            queryCollectStatusByDB(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentID, DBConfig.COLLECT_TABLE_NAME, callbackId);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "---queryContentCollectionStatus:onError:" + e.toString());
                        CallbackForm sendCallbackForm = callbackHashMap.get(callbackId);
                        if (sendCallbackForm != null) {
                            if (sendCallbackForm.callback != null) {
                                ((ICollectionStatusCallback) sendCallbackForm.callback)
                                        .notifyCollectionStatus(false, callbackId);
                            }
                            if (sendCallbackForm.mDisposable != null) {
                                unSubscribe(sendCallbackForm.mDisposable);
                            }
                        }
                        removeCallback(callbackId);

                    }

                    @Override
                    public void onComplete() {
                        CallbackForm sendCallbackForm = callbackHashMap.get(callbackId);
                        if (sendCallbackForm != null) {
                            if (sendCallbackForm.mDisposable != null) {
                                unSubscribe(sendCallbackForm.mDisposable);
                            }
                        }
                        removeCallback(callbackId);
                    }
                });
        return callbackId;

    }

    private void queryCollectStatusByDB(String userId, String contentID, String tableName, final Long callback) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            CallbackForm callbackForm = callbackHashMap.get(callback);
                            if (!TextUtils.isEmpty(result)) {
                                if (callbackForm != null && callbackForm.callback != null) {
                                    ((ICollectionStatusCallback) callbackForm.callback)
                                            .notifyCollectionStatus(true, callback);
                                }
                            } else {
                                if (callbackForm != null && callbackForm.callback != null) {
                                    ((ICollectionStatusCallback) callbackForm.callback)
                                            .notifyCollectionStatus(false, callback);
                                }
                            }
                        }

                        removeCallback(callback);
                    }
                }).excute();
    }

    private void querySubscribeStatusByDB(String userId, String contentID, String tableName, final Long callbackId) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        CallbackForm callbackForm = callbackHashMap.get(callbackId);
                        if (code == 0) {
                            if (!TextUtils.isEmpty(result)) {
                                if (callbackForm != null && callbackForm.callback != null) {
                                    ((ISubscribeStatusCallback) callbackForm.callback)
                                            .notifySubScribeStatus(true, callbackId);
                                }
                            } else {
                                if (callbackForm != null && callbackForm.callback != null) {
                                    ((ISubscribeStatusCallback) callbackForm.callback)
                                            .notifySubScribeStatus(false, callbackId);
                                }
                            }
                        }
                    }
                }).excute();
    }

    private void queryFollowStatusByDB(String userId, String contentID, String tableName, final Long callbackId) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        CallbackForm callbackFrom = callbackHashMap.get(callbackId);
                        if (code == 0) {
                            if (!TextUtils.isEmpty(result)) {
                                if (callbackFrom != null && callbackFrom.callback != null) {
                                    ((IFollowStatusCallback) callbackFrom.callback).notifyFollowStatus(true, callbackId);
                                    removeCallback(callbackId);
                                }
                            } else {
                                if (callbackFrom != null && callbackFrom.callback != null) {
                                    ((IFollowStatusCallback) callbackFrom.callback).notifyFollowStatus(false, callbackId);
                                    removeCallback(callbackId);
                                }
                            }
                        }


                    }
                }).excute();
    }

    public Long queryCarouselInfos(final String orderBy, ICarouselInfoCallback callback) {
        final Long callbackId = System.currentTimeMillis();
        final CallbackForm callbackForm = new CallbackForm();
        callbackForm.callback = callback;
        callbackHashMap.put(callbackId, callbackForm);

        DataSupport.search(DBConfig.LB_COLLECT_TABLE_NAME)
                .condition()
                .OrderBy(orderBy)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        CallbackForm callbackFrom = callbackHashMap.get(callbackId);
                        if (code == 0) {
                            if (!TextUtils.isEmpty(result)) {
                                if (callbackFrom != null && callbackFrom.callback != null) {
                                    Log.d(TAG, "carousel info : " + result);
                                    ((ICarouselInfoCallback) callbackFrom.callback).notifyCarouselInfos(null, callbackId);
                                    removeCallback(callbackId);
                                }
                            } else {
                                if (callbackFrom != null && callbackFrom.callback != null) {
                                    ((ICarouselInfoCallback) callbackFrom.callback).notifyCarouselInfos(null, callbackId);
                                    removeCallback(callbackId);
                                }
                            }
                        }


                    }
                }).excute();
        return callbackId;
    }

    public void removeCallback(Long id) {
        CallbackForm callbackForm = callbackHashMap.get(id);
        if (callbackForm != null) {
            callbackForm.destroy();
            callbackHashMap.remove(id);
        }
    }


    /**
     * 查询节目是否被收藏
     *
     * @param field    搜索的字段名
     * @param value    搜索内容
     * @param callback 查询状态回调
     */
    public void queryContentHistoryStatus(final Context context, final String field, final String value, final String order, final IHisoryStatusCallback callback) {
        QueryUserStatusUtil.getInstance().getLoginStatus(context, new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                String tableName;
                if (status) {
                    tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
                } else {
                    tableName = DBConfig.HISTORY_TABLE_NAME;
                }
                DataSupport.search(tableName)
                        .condition()
                        .eq(field, value)
                        .OrderBy(order)
                        .build()
                        .withCallback(new DBCallback<String>() {
                            @Override
                            public void onResult(int code, String result) {
                                if (!TextUtils.isEmpty(result)) {
                                    Gson mGson = new Gson();
                                    Type type = new TypeToken<List<UserCenterPageBean.Bean>>() {
                                    }.getType();
                                    List<UserCenterPageBean.Bean> data = mGson.fromJson(result, type);
                                    if (null != callback) {
                                        callback.getHistoryStatus(data.get(0));
                                    }
                                } else {
                                    callback.onError();
                                }
                            }
                        }).excute();

            }
        });
    }


    //解决数据订阅关系
    public void unSubscribe(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }

    }

    /**
     * 获取观看记录进度文本，暂时只有用户中心首页观看记录区块与观看记录页面使用
     *
     * @param positionStr
     * @param durationStr
     * @return
     */
    public String getWatchProgress(String positionStr, String durationStr) {
        String result = "";
        long resultTmp = -1;
        if (!TextUtils.isEmpty(positionStr) && !TextUtils.equals(positionStr, "null")
                && !TextUtils.isEmpty(durationStr) && !TextUtils.equals(durationStr, "null")) {
            long position = Long.parseLong(positionStr);
            long duration = Long.parseLong(durationStr);
            //2018.10.23 wqs 避免duration为0导致的除数为0的异常
            if (duration > 0) {
                resultTmp = position * 100 / duration;
            }

            if (position == 0 && duration == 0) {
                result = "观看不足1%";
            } else {
                if (resultTmp < 1) {
                    result = "观看不足1%";
                } else if (position < duration) {
                    result = "已观看" + resultTmp + "%";
                }
            }
        }

        Log.d(TAG, "pos : " + positionStr + ", duration : " + durationStr + ", resultTmp : " + resultTmp);

        return result;
    }

    /**
     * 获取用户数据接口函数
     *
     * @param type       类型参数
     * @param dbCallback 数据回调函数
     */
    public void getUserRecords(String type, DBCallback<String> dbCallback) {
        String tableName;
        String userId;
        if (TextUtils.equals(type, "collect")) {
            if (TextUtils.isEmpty(SharePreferenceUtils.getToken(LauncherApplication.AppContext))) {
                tableName = DBConfig.COLLECT_TABLE_NAME;
                userId = SystemUtils.getDeviceMac(LauncherApplication.AppContext);
            } else {
                tableName = DBConfig.REMOTE_COLLECT_TABLE_NAME;
                userId = SharePreferenceUtils.getUserId(LauncherApplication.AppContext);
            }
        } else if (TextUtils.equals(type, "subscribe")) {
            if (TextUtils.isEmpty(SharePreferenceUtils.getToken(LauncherApplication.AppContext))) {
                tableName = DBConfig.SUBSCRIBE_TABLE_NAME;
                userId = SystemUtils.getDeviceMac(LauncherApplication.AppContext);
            } else {
                tableName = DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME;
                userId = SharePreferenceUtils.getUserId(LauncherApplication.AppContext);
            }
        } else if (TextUtils.equals(type, "follow")) {
            if (TextUtils.isEmpty(SharePreferenceUtils.getToken(LauncherApplication.AppContext))) {
                tableName = DBConfig.ATTENTION_TABLE_NAME;
                userId = SystemUtils.getDeviceMac(LauncherApplication.AppContext);
            } else {
                tableName = DBConfig.REMOTE_ATTENTION_TABLE_NAME;
                userId = SharePreferenceUtils.getUserId(LauncherApplication.AppContext);
            }
        } else if (TextUtils.equals(type, "history")) {
            if (TextUtils.isEmpty(SharePreferenceUtils.getToken(LauncherApplication.AppContext))) {
                tableName = DBConfig.HISTORY_TABLE_NAME;
                userId = SystemUtils.getDeviceMac(LauncherApplication.AppContext);
            } else {
                tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
                userId = SharePreferenceUtils.getUserId(LauncherApplication.AppContext);
            }
        } else {
            return;
        }

        Log.d(TAG, "tableName : " + tableName + ", userId : " + userId);

        SqlCondition sqlCondition = DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME);
        if (TextUtils.equals(type, "history")) {
            sqlCondition.noteq(DBConfig.CONTENTTYPE, Constant.CONTENTTYPE_LB);
        }
        sqlCondition.build().withCallback(dbCallback).excute();
    }

    //用户中心扩展字段转json格式的String数据
    public String setExtendJsonString(int versionCode, UserCenterPageBean.Bean bean) {
        try {
            String extend = "";
            Gson gson = new Gson();
            UserCenterPageBean.ExtendBean userCenterExtendBean = new UserCenterPageBean.ExtendBean();
            if (versionCode > 0) {
                userCenterExtendBean.setVersionCode(versionCode + "");
            } else {
                userCenterExtendBean.setVersionCode("");
            }
            if (bean != null) {
                userCenterExtendBean.setIs_finish(bean.getIs_finish());
                userCenterExtendBean.setReal_exclusive(bean.getReal_exclusive());
                userCenterExtendBean.setIssue_date(bean.getIssue_date());
                userCenterExtendBean.setLast_publish_date(bean.getLast_publish_date());
                userCenterExtendBean.setSub_title(bean.getSub_title());
                userCenterExtendBean.setV_image(bean.getV_image());
                userCenterExtendBean.setH_image(bean.getH_image());
                userCenterExtendBean.setVip_flag(bean.getVip_flag());
                userCenterExtendBean.setAlternate_number(bean.getAlternate_number());
            }
            if (userCenterExtendBean != null) {
                extend = gson.toJson(userCenterExtendBean);
            }
            return extend;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setExtendJsonString:Exception:" + e.toString());
            return "";
        }

    }

    public int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:getAppVersionCode:Exception:" + e.toString());
            return versionCode;
        }

    }

    public String getAppVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:getAppVersionCode:Exception:" + e.toString());
            return "";
        }
    }
}
