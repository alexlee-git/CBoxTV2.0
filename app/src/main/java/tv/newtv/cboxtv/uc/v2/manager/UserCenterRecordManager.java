package tv.newtv.cboxtv.uc.v2.manager;

import android.content.Context;
import android.content.Intent;
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
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;

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
    public static final String REQUEST_RECORD_LIMIT = "300";

    // private String tableName;
    private final String TAG = "UserCenterRecordManager";
    private Disposable mSubscribeDisposable;
    private Disposable mFollowDisposable;
    private Disposable mCollectionDisposable;

    public enum USER_CENTER_RECORD_TYPE {
        TYPE_SUBSCRIBE,
        TYPE_COLLECT,
        TYPE_HISTORY,
        TYPE_FOLLOW
    }

    private UserCenterRecordManager() {
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

    public void addRecord(final USER_CENTER_RECORD_TYPE type, final Context context, final Bundle bundle, final Content info, final DBCallback<String> dbCallback) {
        if (context == null) {
            return;
        }

        if (bundle == null) {
            return;
        }

        if (info == null) {
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
                    }

                    @Override
                    public void onNext(Bundle bundle) {
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
                        } else {
                            Log.e(TAG, "unresolved record type : " + type);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deleteRecord(USER_CENTER_RECORD_TYPE type, Context context, String contentuuids, String contentType, DBCallback<String> dbCallback) {
        if (context == null) {
            return;
        }

        if (TextUtils.isEmpty(contentuuids)) {
            return;
        }

        if (type == USER_CENTER_RECORD_TYPE.TYPE_COLLECT) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENTUUID, contentuuids);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteCollectionRecord(context, bundle, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_FOLLOW) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENTUUID, contentuuids);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteFollowRecord(context, bundle, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_SUBSCRIBE) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENTUUID, contentuuids);
            bundle.putString(DBConfig.CONTENTTYPE, contentType);
            procDeleteSubscribeRecord(context, bundle, dbCallback);
        } else if (type == USER_CENTER_RECORD_TYPE.TYPE_HISTORY) {
            procDeleteHistoryRecord(context, contentuuids, contentType, dbCallback);
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
                progress = String.valueOf(position * 100 / duration);
            } else {
                progress = "0";
            }
            bundle.putString(DBConfig.PLAY_PROGRESS, progress);

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
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).addRemoteCollect(packageData(bundle));
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


    /**
     * 删除历史记录数据
     *
     * @param context
     * @param contentuuids 待删除的历史记录的content_uuid值,如果是全部则传"clean", 如果是多个则用逗号将id隔开
     * @param contentType
     */
    public void procDeleteHistoryRecord(Context context, String contentuuids, String contentType, DBCallback<String> callback) {
        if (TextUtils.isEmpty(contentuuids)) {
            return;
        }

        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            tableName = DBConfig.HISTORY_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
//                String contentTypeParam = "1";
//                if (TextUtils.equals(contentType, "PG")) {
//                    contentTypeParam = "0";
//                }

                HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context))
                        .deleteRemoteHistory("Bearer " + token, userId,
                                contentType,
                                Libs.get().getAppKey(),
                                Libs.get().getChannelId(),
                                contentuuids);
            }
        }

        // DBUtil.delHistory(userId, contentuuids, callback, tableName);
        // if (callback != null) {
        //    callback.onResult(0, "OK");
        // }

        if (TextUtils.equals(contentuuids, "clean")) {
            DataSupport.delete(tableName).condition()
                    .eq(DBConfig.USERID, userId)
                    .build()
                    .withCallback(callback).excute();
        } else {
            DataSupport.delete(tableName).condition()
                    .eq(DBConfig.USERID, userId)
                    .eq(DBConfig.CONTENTUUID, contentuuids)
                    .build()
                    .withCallback(callback).excute();
        }
        Log.d(TAG, "procDeleteHistoryRecord delete history complete, tableName : " + tableName + ", userId : " + userId + ", id : " + contentuuids);
    }

    private void procDeleteCollectionRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            tableName = DBConfig.COLLECT_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_COLLECT_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).deleteRemoteCollect(packageData(bundle));
            }
        }

        String contentuuid = bundle.getString(DBConfig.CONTENTUUID);
        DBUtil.UnCollect(userId, contentuuid, callback, tableName);

        Log.d(TAG, "procDeleteCollectionRecord delete collection complete, tableName : " + tableName + ", userId : " + userId + ", name : " + bundle.getString(DBConfig.TITLE_NAME));
    }

    private void procDeleteFollowRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            tableName = DBConfig.ATTENTION_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_ATTENTION_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context)).
                        deleteRemoteFollow(packageData(bundle));
            }
        }

        String contentuuid = bundle.getString(DBConfig.CONTENTUUID);
        DBUtil.delAttention(userId, contentuuid, callback, tableName);

        Log.d(TAG, "procDeleteFollowRecord delete follow complete, tableName : " + tableName + ", userId : " + userId + ", name : " + bundle.get(DBConfig.TITLE_NAME));
    }

    private void procDeleteSubscribeRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            tableName = DBConfig.SUBSCRIBE_TABLE_NAME;
        } else {
            tableName = DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                SubRepository.getInstance(SubRemoteDataSource.getInstance(context)).deleteRemoteSubscribe(packageData(bundle));
            }
        }

        String contentuuid = bundle.getString(DBConfig.CONTENTUUID);
        DBUtil.UnSubcribe(userId, contentuuid, callback, tableName);

        Log.d(TAG, "procDeleteSubscribeRecord delete subscribe complete, tableName : " + tableName + ", userId : " + userId + ", name : " + bundle.getString(DBConfig.TITLE_NAME));
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

    public void getRemoteCollectionList(Context context, String token, String userId, String offset, String limit, @NonNull CollectRemoteDataSource.GetCollectListCallback callback) {
        CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context))
                .getRemoteCollectList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit, callback);
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
     * 批量插入数据至数据库
     */
    public void addCollectToDataBase(String userId, List<UserCenterPageBean.Bean> list, DBCallback<String> callback, String tableName) {
        if (list != null) {
            Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
            while (iterator.hasNext()) {
                UserCenterPageBean.Bean bean = iterator.next();

                Content info = new Content();
                info.setContentID(bean.get_contentuuid());
                info.setContentType(bean.get_contenttype());
                info.setVImage(bean.get_imageurl());
                info.setTitle(bean.get_title_name());
                // info.setrSubScript(bean.getSuperscript());
                info.setGrade(bean.getGrade());

                Bundle bundle = new Bundle();
                bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                DBUtil.PutCollect(userId, info, bundle, callback, tableName);
            }
        }
    }

    public void addSubscribeToDataBase(String userId, List<UserCenterPageBean.Bean> list, DBCallback<String> callback, String tableName) {
        if (list != null) {
            Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
            while (iterator.hasNext()) {
                UserCenterPageBean.Bean bean = iterator.next();

                Content info = new Content();

                Log.d("sub", "addSubscribeToDataBase contentid : " + bean.get_contentuuid());

                info.setContentID(bean.get_contentuuid());
                info.setContentType(bean.get_contenttype());
                info.setVImage(bean.get_imageurl());
                info.setTitle(bean.get_title_name());
                info.setGrade(bean.getGrade());
                // info.setrSuperScript(bean.getSuperscript());

                Bundle bundle = new Bundle();
                bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                DBUtil.AddSubcribe(userId, info, bundle, callback, tableName);
            }
        }
    }

    public void addFollowToDataBase(String userId, List<UserCenterPageBean.Bean> list, DBCallback<String> callback, String tableName) {
        if (list != null) {
            Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
            while (iterator.hasNext()) {
                UserCenterPageBean.Bean bean = iterator.next();

                Content info = new Content();
                info.setContentID(bean.get_contentuuid());
                info.setContentType(bean.get_contenttype());
                info.setVImage(bean.get_imageurl());
                info.setTitle(bean.get_title_name());
                // info.setrSubScript(bean.getSuperscript());
                info.setGrade(bean.getGrade());

                Bundle bundle = new Bundle();
                bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                DBUtil.addAttention(userId, info, bundle, callback, tableName);
            }
        }
    }

    public void addHistoryToDataBase(String userId, List<UserCenterPageBean.Bean> list, DBCallback<String> callback, String tableName) {
        if (list != null) {
            Iterator<UserCenterPageBean.Bean> iterator = list.iterator();
            while (iterator.hasNext()) {
                UserCenterPageBean.Bean bean = iterator.next();

                Content info = new Content();
                info.setContentID(bean.get_contentuuid());
                info.setContentType(bean.get_contenttype());
                info.setVImage(bean.get_imageurl());
                info.setTitle(bean.get_title_name());
                // info.setrSuperScript(bean.getSuperscript());
                info.setGrade(bean.getGrade());
                info.setVideoType(bean.getVideoType());

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
    }

    public int currentHistoryIndex = 1;//历史记录当前插入数据完成的个数
    public int currentSubIndex = 1;//订阅列表当前插入数据完成的个数
    public int currentCollectIndex = 1;//收藏列表当前插入数据完成的个数
    public int currentFollowIndex = 1;//关注列表当前插入数据完成的个数
    private boolean getHistoryRecordComplete = false;//获取历史记录完成状态
    private boolean getCollectionRecordComplete = false;//获取收藏记录完成状态
    private boolean getFollowRecordComplete = false;//获取关注记录完成状态
    private boolean getSubscribeRecordComplete = false;//获取订阅记录完成状态

    /**
     * 批量获取云端数据库数据并同步到本地数据库中
     *
     * @param context
     * @param offset
     * @param limit
     */
    public void getUserBehaviorUtils(final Context context, final String offset, final String limit) {
        currentHistoryIndex = 1;
        currentSubIndex = 1;
        currentCollectIndex = 1;
        currentFollowIndex = 1;
        getHistoryRecordComplete = false;
        getCollectionRecordComplete = false;
        getFollowRecordComplete = false;
        getSubscribeRecordComplete = false;
        QueryUserStatusUtil.getInstance().getLoginStatus(context, new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                if (status) {
                    String token = SharePreferenceUtils.getToken(context);
                    final String userId = SharePreferenceUtils.getUserId(context);
                    if (token != null) {
                        getRemoteHistoryList(context, token, userId, offset, limit, new HistoryDataSource.GetHistoryListCallback() {
                            @Override
                            public void onHistoryListLoaded(List<UserCenterPageBean.Bean> historyList, final int totalSize) {
                                Log.d(TAG, "---historyList.size():" + totalSize);
                                if (historyList != null && historyList.size() > 0) {
                                    addHistoryToDataBase(userId, historyList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "---currentHistoryIndex:" + currentHistoryIndex);
                                            if (currentHistoryIndex == totalSize) {
                                                getHistoryRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentHistoryIndex++;
                                        }
                                    }, DBConfig.REMOTE_HISTORY_TABLE_NAME);
                                } else {
                                    getHistoryRecordComplete = true;
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteSubscribe(context, token, userId, offset, limit, new SubDataSource.GetSubscribeListCallback() {
                            @Override
                            public void onSubscribeListLoaded(List<UserCenterPageBean.Bean> subList, final int totalSize) {
                                Log.d(TAG, "---subList.size():" + totalSize);
                                if (subList != null && subList.size() > 0) {
                                    addSubscribeToDataBase(userId, subList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "---currentSubIndex:" + currentSubIndex);
                                            if (currentSubIndex == totalSize) {
                                                getSubscribeRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentSubIndex++;
                                        }
                                    }, DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME);
                                } else {
                                    getSubscribeRecordComplete = true;
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteCollectionList(context, token, userId, offset, limit, new CollectDataSource.GetCollectListCallback() {
                            @Override
                            public void onCollectListLoaded(List<UserCenterPageBean.Bean> CollectList, final int totalSize) {
                                Log.d(TAG, "---CollectList.size():" + totalSize);
                                if (CollectList != null && CollectList.size() > 0) {
                                    addCollectToDataBase(userId, CollectList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "---currentCollectIndex:" + currentCollectIndex);
                                            if (currentCollectIndex == totalSize) {
                                                getCollectionRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentCollectIndex++;
                                        }
                                    }, DBConfig.REMOTE_COLLECT_TABLE_NAME);
                                } else {
                                    getCollectionRecordComplete = true;
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        getRemoteFollowList(context, token, userId, offset, limit, new FollowDataSource.GetFollowListCallback() {
                            @Override
                            public void onFollowListLoaded(List<UserCenterPageBean.Bean> FollowList, final int totalSize) {
                                Log.d(TAG, "---FollowList.size():" + totalSize);
                                if (FollowList != null && FollowList.size() > 0) {
                                    addFollowToDataBase(userId, FollowList, new DBCallback<String>() {
                                        @Override
                                        public void onResult(int code, String result) {
                                            Log.d(TAG, "---currentFollowIndex:" + currentFollowIndex);
                                            if (currentFollowIndex == totalSize) {
                                                getFollowRecordComplete = true;
                                                getUserBehaviorComplete(context);
                                            }
                                            currentFollowIndex++;
                                        }
                                    }, DBConfig.REMOTE_ATTENTION_TABLE_NAME);
                                } else {
                                    getFollowRecordComplete = true;
                                }
                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                    } else {
                        Log.e(TAG, "---getUserBehaviorUtils:token==null");
                    }
                } else {
                    Log.e(TAG, "---getUserBehaviorUtils:loginStatus:" + status);
                }
            }
        });
    }

    //数据获取完成，向用户中心首页发送广播
    private void getUserBehaviorComplete(Context context) {
        Log.d(TAG, "----getUserBehaviorComplete");
        if (getHistoryRecordComplete && getCollectionRecordComplete
                && getFollowRecordComplete && getSubscribeRecordComplete) {
            Log.e(TAG, "---getUserBehaviorUtils:getUserBehaviorComplete:sendBroadcast");
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("action.uc.data.sync.complete"));
        } else {
            Log.e(TAG, "----getUserBehaviorComplete:error");
        }

    }

    private UserCenterPageBean.Bean packageData(Bundle bundle) {
        if (bundle == null) {
            return null;
        }

        UserCenterPageBean.Bean pageBean = new UserCenterPageBean.Bean();
        pageBean.set_contentuuid(bundle.getString(DBConfig.CONTENTUUID));
        Log.d("sub", "packageData contentID : " + bundle.get(DBConfig.CONTENTUUID));
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
        return pageBean;
    }

    /**
     * 查询节目是否被订阅
     *
     * @param context     上下文
     * @param contentUUid 节目id
     * @param callback    查询状态回调
     */
    public void queryContentSubscribeStatus(final Context context, final String contentUUid, final ISubscribeStatusCallback callback) {
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
                        unSubscribe(mSubscribeDisposable);
                        mSubscribeDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        String tableName = DBConfig.SUBSCRIBE_TABLE_NAME;
                        String userId;
                        if (!TextUtils.isEmpty(s)) {
                            userId = SharePreferenceUtils.getUserId(context);
                            tableName = DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME;
                        } else {
                            userId = SystemUtils.getDeviceMac(context);
                        }
                        DataSupport.search(tableName)
                                .condition()
                                .eq(DBConfig.CONTENTUUID, contentUUid)
                                .eq(DBConfig.USERID, userId)
                                .OrderBy(DBConfig.ORDER_BY_TIME)
                                .build()
                                .withCallback(new DBCallback<String>() {
                                    @Override
                                    public void onResult(int code, String result) {
                                        if (code == 0) {
                                            if (!TextUtils.isEmpty(result)) {
                                                if (callback != null) {
                                                    callback.notifySubScribeStatus(true);
                                                }
                                            } else {
                                                if (callback != null) {
                                                    callback.notifySubScribeStatus(false);
                                                }
                                            }
                                        }
                                    }
                                }).excute();
                        unSubscribe(mSubscribeDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "---queryContentSubscribeStatus:onError:" + e.toString());
                        if (callback != null) {
                            callback.notifySubScribeStatus(false);
                        }
                        unSubscribe(mSubscribeDisposable);
                    }

                    @Override
                    public void onComplete() {
                        unSubscribe(mSubscribeDisposable);
                    }
                });
    }

    /**
     * 查询节目是否被关注
     *
     * @param context     上下文
     * @param contentUUid 节目id
     * @param callback    查询状态回调
     */
    public void queryContentFollowStatus(final Context context, final String contentUUid, final IFollowStatusCallback callback) {
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
                        unSubscribe(mFollowDisposable);
                        mFollowDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        String tableName = DBConfig.ATTENTION_TABLE_NAME;
                        String userId;
                        if (!TextUtils.isEmpty(s)) {
                            userId = SharePreferenceUtils.getUserId(context);
                            tableName = DBConfig.REMOTE_ATTENTION_TABLE_NAME;
                        } else {
                            userId = SystemUtils.getDeviceMac(context);
                        }
                        DataSupport.search(tableName)
                                .condition()
                                .eq(DBConfig.CONTENTUUID, contentUUid)
                                .eq(DBConfig.USERID, userId)
                                .OrderBy(DBConfig.ORDER_BY_TIME)
                                .build()
                                .withCallback(new DBCallback<String>() {
                                    @Override
                                    public void onResult(int code, String result) {
                                        if (code == 0) {
                                            if (!TextUtils.isEmpty(result)) {
                                                if (callback != null) {
                                                    callback.notifyFollowStatus(true);
                                                }
                                            } else {
                                                if (callback != null) {
                                                    callback.notifyFollowStatus(false);
                                                }
                                            }
                                        }
                                    }
                                }).excute();
                        unSubscribe(mFollowDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "---queryContentFollowStatus:onError:" + e.toString());
                        if (callback != null) {
                            callback.notifyFollowStatus(false);
                        }
                        unSubscribe(mFollowDisposable);
                    }

                    @Override
                    public void onComplete() {
                        unSubscribe(mFollowDisposable);
                    }
                });
    }

    /**
     * 查询节目是否被收藏
     *
     * @param context     上下文
     * @param contentUUid 节目id
     * @param callback    查询状态回调
     */
    public void queryContentCollectionStatus(final Context context, final String contentUUid, final ICollectionStatusCallback callback) {
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
                        unSubscribe(mCollectionDisposable);
                        mCollectionDisposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        String tableName = DBConfig.COLLECT_TABLE_NAME;
                        String userId;
                        if (!TextUtils.isEmpty(s)) {
                            userId = SharePreferenceUtils.getUserId(context);
                            tableName = DBConfig.REMOTE_COLLECT_TABLE_NAME;
                        } else {
                            userId = SystemUtils.getDeviceMac(context);
                        }
                        DataSupport.search(tableName)
                                .condition()
                                .eq(DBConfig.CONTENTUUID, contentUUid)
                                .eq(DBConfig.USERID, userId)
                                .OrderBy(DBConfig.ORDER_BY_TIME)
                                .build()
                                .withCallback(new DBCallback<String>() {
                                    @Override
                                    public void onResult(int code, String result) {
                                        if (code == 0) {
                                            if (!TextUtils.isEmpty(result)) {
                                                if (callback != null) {
                                                    callback.notifyCollectionStatus(true);
                                                }
                                            } else {
                                                if (callback != null) {
                                                    callback.notifyCollectionStatus(false);
                                                }
                                            }
                                        }
                                    }
                                }).excute();
                        unSubscribe(mCollectionDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "---queryContentCollectionStatus:onError:" + e.toString());
                        if (callback != null) {
                            callback.notifyCollectionStatus(false);
                        }
                        unSubscribe(mCollectionDisposable);
                    }

                    @Override
                    public void onComplete() {
                        unSubscribe(mCollectionDisposable);
                    }
                });
    }

    /**
     * 查询节目是否被收藏
     *
     * @param field    搜索的字段名
     * @param value    搜索内容
     * @param callback 查询状态回调
     */
    public void queryContenthistoryStatus(final Context context, final String field, final String value, final String order, final IHisoryStatusCallback callback) {
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
                                }
                            }
                        }).excute();

            }
        });
    }


    //解决数据订阅关系
    private void unSubscribe(Disposable disposable) {
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
            if (resultTmp < 1) {
                result = "观看不足1%";
            } else {
                if (position < duration) {
                    result = "已观看" + resultTmp + "%";
                } else {
                    result = "已看完";
                }
            }
        }

        Log.d(TAG, "getWatchProgress, pos : " + positionStr + ", duration : " + durationStr + ", resultTmp : " + resultTmp);

        return result;
    }
}
