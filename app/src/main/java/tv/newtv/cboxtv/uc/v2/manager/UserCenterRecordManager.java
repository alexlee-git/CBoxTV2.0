package tv.newtv.cboxtv.uc.v2.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import tv.newtv.cboxtv.LauncherApplication;
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

    private boolean collectStatusInLocal;
    private boolean collectStatusInRemote;
    private boolean collectStatusLocalReqComp;
    private boolean collectStatusRemoteReqComp;




    private final int MSG_NOTIFY_COLLECT_STATUS   = 10071;
    private final int MSG_NOTIFY_SUBSCRIBE_STATUS = 10072;
    private final int MSG_NOTIFY_FOLLOW_STATUS = 10073;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_NOTIFY_COLLECT_STATUS) {
                Log.d("col", "接收到 MSG_NOTIFY_COLLECT_STATUS 消息");
                ICollectionStatusCallback callback = (ICollectionStatusCallback) msg.obj;
                if (callback == null) {
                    return;
                }

                mHandler.removeMessages(MSG_NOTIFY_COLLECT_STATUS);

                if (collectStatusLocalReqComp && collectStatusRemoteReqComp) {
                    if (collectStatusInRemote || collectStatusInLocal) {
                        Log.d(TAG, "通知该片已订阅");
                        callback.notifyCollectionStatus(true);
                    } else {
                        Log.d(TAG, "通知该片未订阅");
                        callback.notifyCollectionStatus(false);
                    }
                } else {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(MSG_NOTIFY_COLLECT_STATUS, 100);
                    }
                }
            } else if (msg.what == MSG_NOTIFY_SUBSCRIBE_STATUS) {
                ISubscribeStatusCallback callback = (ISubscribeStatusCallback) msg.obj;
                if (callback == null) {
                    return;
                }

                mHandler.removeMessages(MSG_NOTIFY_SUBSCRIBE_STATUS);

                if (collectStatusLocalReqComp && collectStatusRemoteReqComp) {
                    if (collectStatusInRemote || collectStatusInLocal) {
                        Log.d(TAG, "通知该片已收藏");
                        callback.notifySubScribeStatus(true);
                    } else {
                        Log.d(TAG, "通知该片未收藏");
                        callback.notifySubScribeStatus(false);
                    }
                } else {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(MSG_NOTIFY_SUBSCRIBE_STATUS, 100);
                    }
                }
            } else if (msg.what == MSG_NOTIFY_FOLLOW_STATUS) {
                Log.d("follow", "接收到 MSG_NOTIFY_FOLLOW_STATUS 消息");
                IFollowStatusCallback callback = (IFollowStatusCallback) msg.obj;
                if (callback == null) {
                    return;
                }

                mHandler.removeMessages(MSG_NOTIFY_FOLLOW_STATUS);

                if (collectStatusLocalReqComp && collectStatusRemoteReqComp) {
                    if (collectStatusInRemote || collectStatusInLocal) {
                        Log.d(TAG, "通知人物已关注");
                        callback.notifyFollowStatus(true);
                    } else {
                        Log.d(TAG, "通知该人物未关注");
                        callback.notifyFollowStatus(false);
                    }
                } else {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(MSG_NOTIFY_FOLLOW_STATUS, 100);
                    }
                }
            } else {
                Log.d(TAG, "unresolved msg : " + msg.what);
            }
        }
    };

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

    public void deleteRecord(USER_CENTER_RECORD_TYPE type, Context context, String contentuuids, String contentType, String dataUserId, DBCallback<String> dbCallback) {
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
            procDeleteHistoryRecord(dataUserId, context, contentuuids, contentType, dbCallback);
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
    public void procDeleteHistoryRecord(String dataUserId, Context context, String contentuuids, String contentType, DBCallback<String> callback) {
        Log.d(TAG, "删除 dataUserId : " + dataUserId + ", contentuuids : " + contentuuids);
        if (TextUtils.isEmpty(contentuuids)) {
            return;
        }

        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        // String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            // tableName = DBConfig.HISTORY_TABLE_NAME;
        } else {
            // tableName = DBConfig.REMOTE_HISTORY_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context))
                        .deleteRemoteHistory("Bearer " + token, userId,
                                contentType,
                                Libs.get().getAppKey(),
                                Libs.get().getChannelId(),
                                contentuuids);
            }
        }


        if (TextUtils.equals(contentuuids, "clean")) {
            DataSupport.delete(DBConfig.HISTORY_TABLE_NAME)
                    .condition()
                    .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                    .build()
                    .withCallback(callback).excute();

            DataSupport.delete(DBConfig.REMOTE_HISTORY_TABLE_NAME)
                    .condition()
                    .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                    .build()
                    .withCallback(callback).excute();
        } else {
            if (TextUtils.equals(dataUserId, SystemUtils.getDeviceMac(LauncherApplication.AppContext))) {
                DataSupport.delete(DBConfig.HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, dataUserId)
                        .eq(DBConfig.CONTENTUUID, contentuuids)
                        .build()
                        .withCallback(callback).excute();
                Log.d(TAG, "单点删除本地数据, dataUserId : " + dataUserId + ", contentuuid : " + contentuuids);
            }

            if (TextUtils.equals(dataUserId, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))) {
                DataSupport.delete(DBConfig.REMOTE_HISTORY_TABLE_NAME)
                        .condition()
                        .eq(DBConfig.USERID, dataUserId)
                        .eq(DBConfig.CONTENTUUID, contentuuids)
                        .build()
                        .withCallback(callback).excute();
                Log.d(TAG, "单点删除远程数据, dataUserId : " + dataUserId + ", contentuuid : " + contentuuids);
            }
        }
        Log.d(TAG, "procDeleteHistoryRecord delete history complete, userId : " + userId + ", id : " + contentuuids);
    }

    private void procDeleteCollectionRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String contentuuid = bundle.getString(DBConfig.CONTENTUUID);

        Log.d("lxl", "userId : " + userId + ", contentuuid : " + contentuuid);

        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            DBUtil.UnCollect(userId, contentuuid, callback, DBConfig.COLLECT_TABLE_NAME);
        } else {
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context)).deleteRemoteCollect(packageData(bundle));
                DBUtil.UnCollect(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentuuid, callback, DBConfig.COLLECT_TABLE_NAME);
            }

            Log.d("lxl", "登录用户, 两个表的数据都删除, contentuuid : " + contentuuid);
            DBUtil.UnCollect(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentuuid, callback, DBConfig.REMOTE_COLLECT_TABLE_NAME);
        }
    }

    private void procDeleteFollowRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String contentuuid = bundle.getString(DBConfig.CONTENTUUID);

        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            DBUtil.delAttention(userId, contentuuid, callback, DBConfig.ATTENTION_TABLE_NAME);
        } else {
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context)).deleteRemoteFollow(packageData(bundle));
                DBUtil.UnCollect(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentuuid, callback, DBConfig.ATTENTION_TABLE_NAME);
            }

            DBUtil.UnCollect(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentuuid, callback, DBConfig.REMOTE_ATTENTION_TABLE_NAME);
        }
    }

    private void procDeleteSubscribeRecord(Context context, Bundle bundle, DBCallback<String> callback) {
        String userId = SharePreferenceUtils.getUserId(context);
        if (TextUtils.isEmpty(userId)) {
            userId = SystemUtils.getDeviceMac(context);
        }

        String contentuuid = bundle.getString(DBConfig.CONTENTUUID);
        // String tableName = "";
        String token = SharePreferenceUtils.getToken(context);
        if (TextUtils.isEmpty(token)) {
            // tableName = DBConfig.SUBSCRIBE_TABLE_NAME;
            DBUtil.UnSubcribe(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentuuid, callback, DBConfig.SUBSCRIBE_TABLE_NAME);
        } else {
            // tableName = DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME;
            if (SYNC_SWITCH_ON == SharePreferenceUtils.getSyncStatus(context)) {
                SubRepository.getInstance(SubRemoteDataSource.getInstance(context)).deleteRemoteSubscribe(packageData(bundle));
                DBUtil.UnSubcribe(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentuuid, callback, DBConfig.SUBSCRIBE_TABLE_NAME);
            }

            DBUtil.UnSubcribe(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentuuid, callback, DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME);
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
                collectStatusInLocal       = false;
                collectStatusInRemote      = false;
                collectStatusLocalReqComp  = false;
                collectStatusRemoteReqComp = false;
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
                        if (!TextUtils.isEmpty(s)) {
                            if (SharePreferenceUtils.getSyncStatus(LauncherApplication.AppContext) == 0) {
                                Log.d("col", "需要两个表都查");
                                DataSupport.search(DBConfig.SUBSCRIBE_TABLE_NAME)
                                        .condition()
                                        .eq(DBConfig.CONTENTUUID, contentUUid)
                                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                                        .OrderBy(DBConfig.ORDER_BY_TIME)
                                        .build()
                                        .withCallback(new DBCallback<String>() {
                                            @Override
                                            public void onResult(int code, String result) {
                                                if (code == 0) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        collectStatusInLocal = true;
                                                    }
                                                }

                                                Log.d("col", "查完本地表, 结果为 : " + collectStatusInLocal);

                                                collectStatusLocalReqComp = true;

                                                if (mHandler != null) {
                                                    Message message = Message.obtain();
                                                    message.what = MSG_NOTIFY_SUBSCRIBE_STATUS;
                                                    message.obj = callback;
                                                    mHandler.sendMessage(message);
                                                }
                                            }
                                        }).excute();

                                DataSupport.search(DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME)
                                        .condition()
                                        .eq(DBConfig.CONTENTUUID, contentUUid)
                                        .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                                        .OrderBy(DBConfig.ORDER_BY_TIME)
                                        .build()
                                        .withCallback(new DBCallback<String>() {
                                            @Override
                                            public void onResult(int code, String result) {
                                                if (code == 0) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        collectStatusInRemote = true;
                                                    }

                                                    Log.d("col", "查完远程表, 结果为 : " + collectStatusInRemote);
                                                    collectStatusRemoteReqComp = true;

                                                    if (mHandler != null) {
                                                        Message message = Message.obtain();
                                                        message.what = MSG_NOTIFY_SUBSCRIBE_STATUS;
                                                        message.obj = callback;
                                                        mHandler.sendMessage(message);
                                                    }
                                                }
                                            }
                                        }).excute();
                            } else {
                                querySubscribeStatusByDB(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentUUid, DBConfig.REMOTE_COLLECT_TABLE_NAME, callback);
                            }
                        } else {
                            querySubscribeStatusByDB(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentUUid, DBConfig.COLLECT_TABLE_NAME, callback);
                        }
                        unSubscribe(mCollectionDisposable);
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

                collectStatusInLocal       = false;
                collectStatusInRemote      = false;
                collectStatusLocalReqComp  = false;
                collectStatusRemoteReqComp = false;

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
                        if (!TextUtils.isEmpty(s)) {
                            if (SharePreferenceUtils.getSyncStatus(LauncherApplication.AppContext) == 0) {
                                Log.d("follow", "需要两个表都查");
                                DataSupport.search(DBConfig.ATTENTION_TABLE_NAME)
                                        .condition()
                                        .eq(DBConfig.CONTENTUUID, contentUUid)
                                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                                        .OrderBy(DBConfig.ORDER_BY_TIME)
                                        .build()
                                        .withCallback(new DBCallback<String>() {
                                            @Override
                                            public void onResult(int code, String result) {
                                                if (code == 0) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        collectStatusInLocal = true;
                                                    }
                                                }

                                                Log.d("follow", "查完本地表, 结果为 : " + collectStatusInLocal);

                                                collectStatusLocalReqComp = true;

                                                if (mHandler != null) {
                                                    Message message = Message.obtain();
                                                    message.what = MSG_NOTIFY_FOLLOW_STATUS;
                                                    message.obj = callback;
                                                    mHandler.sendMessage(message);
                                                }
                                            }
                                        }).excute();

                                DataSupport.search(DBConfig.REMOTE_ATTENTION_TABLE_NAME)
                                        .condition()
                                        .eq(DBConfig.CONTENTUUID, contentUUid)
                                        .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                                        .OrderBy(DBConfig.ORDER_BY_TIME)
                                        .build()
                                        .withCallback(new DBCallback<String>() {
                                            @Override
                                            public void onResult(int code, String result) {
                                                if (code == 0) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        collectStatusInRemote = true;
                                                    }

                                                    Log.d("follow", "查完远程表, 结果为 : " + collectStatusInRemote);
                                                    collectStatusRemoteReqComp = true;

                                                    if (mHandler != null) {
                                                        Message message = Message.obtain();
                                                        message.what = MSG_NOTIFY_FOLLOW_STATUS;
                                                        message.obj = callback;
                                                        mHandler.sendMessage(message);
                                                    }
                                                }
                                            }
                                        }).excute();
                            } else {
                                queryFollowStatusByDB(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentUUid, DBConfig.REMOTE_ATTENTION_TABLE_NAME, callback);
                            }
                        } else {
                            queryFollowStatusByDB(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentUUid, DBConfig.ATTENTION_TABLE_NAME, callback);
                        }
                        unSubscribe(mCollectionDisposable);
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
                collectStatusInLocal       = false;
                collectStatusInRemote      = false;
                collectStatusLocalReqComp  = false;
                collectStatusRemoteReqComp = false;
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
                        if (!TextUtils.isEmpty(s)) {
                            if (SharePreferenceUtils.getSyncStatus(LauncherApplication.AppContext) == 0) {
                                Log.d("col", "需要两个表都查");
                                DataSupport.search(DBConfig.COLLECT_TABLE_NAME)
                                        .condition()
                                        .eq(DBConfig.CONTENTUUID, contentUUid)
                                        .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                                        .OrderBy(DBConfig.ORDER_BY_TIME)
                                        .build()
                                        .withCallback(new DBCallback<String>() {
                                            @Override
                                            public void onResult(int code, String result) {
                                                if (code == 0) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        collectStatusInLocal = true;
                                                    }
                                                }

                                                Log.d("col", "查完本地表, 结果为 : " + collectStatusInLocal);

                                                collectStatusLocalReqComp = true;

                                                if (mHandler != null) {
                                                    Message message = Message.obtain();
                                                    message.what = MSG_NOTIFY_COLLECT_STATUS;
                                                    message.obj = callback;
                                                    mHandler.sendMessage(message);
                                                }
                                            }
                                        }).excute();

                                DataSupport.search(DBConfig.REMOTE_COLLECT_TABLE_NAME)
                                        .condition()
                                        .eq(DBConfig.CONTENTUUID, contentUUid)
                                        .eq(DBConfig.USERID, SharePreferenceUtils.getUserId(LauncherApplication.AppContext))
                                        .OrderBy(DBConfig.ORDER_BY_TIME)
                                        .build()
                                        .withCallback(new DBCallback<String>() {
                                            @Override
                                            public void onResult(int code, String result) {
                                                if (code == 0) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        collectStatusInRemote = true;
                                                    }

                                                    Log.d("col", "查完远程表, 结果为 : " + collectStatusInRemote);
                                                    collectStatusRemoteReqComp = true;

                                                    if (mHandler != null) {
                                                        Message message = Message.obtain();
                                                        message.what = MSG_NOTIFY_COLLECT_STATUS;
                                                        message.obj = callback;
                                                        mHandler.sendMessage(message);
                                                    }
                                                }
                                            }
                                        }).excute();
                            } else {
                                queryCollectStatusByDB(SharePreferenceUtils.getUserId(LauncherApplication.AppContext), contentUUid, DBConfig.REMOTE_COLLECT_TABLE_NAME, callback);
                            }
                        } else {
                            queryCollectStatusByDB(SystemUtils.getDeviceMac(LauncherApplication.AppContext), contentUUid, DBConfig.COLLECT_TABLE_NAME, callback);
                        }
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

    private void queryCollectStatusByDB(String userId, String contentuuid, String tableName, final ICollectionStatusCallback callback) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentuuid)
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
    }

    private void querySubscribeStatusByDB(String userId, String contentuuid, String tableName, final ISubscribeStatusCallback callback) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentuuid)
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
    }

    private void queryFollowStatusByDB(String userId, String contentuuid, String tableName, final IFollowStatusCallback callback) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentuuid)
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
