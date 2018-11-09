package tv.newtv.cboxtv.uc.v2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
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
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.utils.DBUtil;

/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2
 * 创建事件:     下午 6:47
 * 创建人:       caolonghe
 * 创建日期:     2018/9/28 0021
 */

public class UserBehaviorUtils {
    private final String TAG = "lx";
    private static UserBehaviorUtils mInstance;

    private final String REQUEST_OFFSET = "1";
    private final String REQUEST_LIMIT = "300";
    private String token = "";
    private String userId = "";

    public static UserBehaviorUtils getInstance() {
        if (mInstance == null) {
            synchronized (UserBehaviorUtils.class) {
                mInstance = new UserBehaviorUtils();
            }
        }
        return mInstance;
    }

    public UserBehaviorUtils() {
    }

    public void getUserBehaviorUtils(final Context context) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                getUserCenterRecordList(context);
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String value) throws Exception {
                        Log.e(TAG, "---getUserBehaviorUtils:sendBroadcast");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("action.uc.data.sync.complete"));
                    }
                });
    }

    private void getUserCenterRecordList(final Context context) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(context);
                Log.d(TAG, "---getUserCenterRecordList:isTokenRefresh:status:" + status);
                //获取登录状态
                String token = SharePreferenceUtils.getToken(context.getApplicationContext());
                if (!TextUtils.isEmpty(token)) {
                    e.onNext(token);
                } else {
                    e.onNext("");
                }
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s)) {
                    userId = SharePreferenceUtils.getUserId(context.getApplicationContext());
                    CollectRepository.getInstance(CollectRemoteDataSource.getInstance(context))
                            .getRemoteCollectList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(),
                                    REQUEST_OFFSET, REQUEST_LIMIT, new CollectDataSource.GetCollectListCallback() {
                                        @Override
                                        public void onCollectListLoaded(List<UserCenterPageBean.Bean> collectList, int totalSize) {
                                            Log.e(TAG, "---collectList");
                                            if (collectList != null) {
                                                Iterator<UserCenterPageBean.Bean> iterator = collectList.iterator();
                                                while (iterator.hasNext()) {
                                                    UserCenterPageBean.Bean bean = iterator.next();

                                                    Content info = new Content();
                                                    info.setContentUUID(bean.get_contentuuid());
                                                    info.setContentType(bean.get_contenttype());
                                                    info.setVImage(bean.get_imageurl());
                                                    info.setTitle(bean.get_title_name());
                                                    // TODO : 需要加入数据产生的时间
                                                    DBUtil.PutCollect(SharePreferenceUtils.getUserId(context), info, null, null, DBConfig.REMOTE_COLLECT_TABLE_NAME);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onDataNotAvailable() {
                                            Log.d(TAG, "---getUserCenterRecordList getRemoteCollectList onDataNotAvailable");
                                        }
                                    });

                    SubRepository.getInstance(SubRemoteDataSource.getInstance(context))
                            .getRemoteSubscribeList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(),
                                    REQUEST_OFFSET, REQUEST_LIMIT, new SubDataSource.GetSubscribeListCallback() {
                                        @Override
                                        public void onSubscribeListLoaded(List<UserCenterPageBean.Bean> subscribeList, int totalSize) {
                                            Log.e(TAG, "---subscribeList");

                                            if (subscribeList != null) {
                                                Iterator<UserCenterPageBean.Bean> iterator = subscribeList.iterator();
                                                while (iterator.hasNext()) {
                                                    UserCenterPageBean.Bean bean = iterator.next();

                                                    Content info = new Content();
                                                    info.setContentUUID(bean.get_contentuuid());
                                                    info.setContentType(bean.get_contenttype());
                                                    info.setVImage(bean.get_imageurl());
                                                    info.setTitle(bean.get_title_name());
                                                    info.setGrade(bean.getGrade());
                                                    // info.setrSuperScript(bean.getSuperscript());

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                                                    DBUtil.AddSubcribe(SharePreferenceUtils.getUserId(context), info, bundle,null, DBConfig.REMOTE_SUBSCRIBE_TABLE_NAME);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onDataNotAvailable() {
                                            Log.d(TAG, "---getUserCenterRecordList getRemoteSubscribeList onDataNotAvailable");
                                        }
                                    });

                    FollowRepository.getInstance(FollowRemoteDataSource.getInstance(context))
                            .getRemoteFollowList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(),
                                    REQUEST_OFFSET, REQUEST_LIMIT, new FollowDataSource.GetFollowListCallback() {
                                        @Override
                                        public void onFollowListLoaded(List<UserCenterPageBean.Bean> followList, int totalSize) {
                                            Log.e(TAG, "---FollowList");

                                            if (followList != null) {
                                                Iterator<UserCenterPageBean.Bean> iterator = followList.iterator();
                                                while (iterator.hasNext()) {
                                                    UserCenterPageBean.Bean bean = iterator.next();

                                                    Content info = new Content();
                                                    info.setContentUUID(bean.get_contentuuid());
                                                    info.setContentType(bean.get_contenttype());
                                                    info.setVImage(bean.get_imageurl());
                                                    info.setTitle(bean.get_title_name());

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                                                    DBUtil.addAttention(SharePreferenceUtils.getUserId(context), info, bundle,null, DBConfig.REMOTE_ATTENTION_TABLE_NAME);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onDataNotAvailable() {
                                            Log.d(TAG, "---getUserCenterRecordList getFollowList onDataNotAvailable");
                                        }
                                    });

                    HistoryRepository.getInstance(HistoryRemoteDataSource.getInstance(context))
                            .getRemoteHistoryList(token, userId, Libs.get().getAppKey(), Libs.get().getChannelId(),
                                    REQUEST_OFFSET, REQUEST_LIMIT,
                                    new HistoryDataSource.GetHistoryListCallback() {
                                        @Override
                                        public void onHistoryListLoaded(List<UserCenterPageBean.Bean> historyList, int totalSize) {
                                            Log.e(TAG, "---historyList");
                                            if (historyList != null) {
                                                Iterator<UserCenterPageBean.Bean> iterator = historyList.iterator();
                                                while (iterator.hasNext()) {
                                                    UserCenterPageBean.Bean bean = iterator.next();

                                                    Content info = new Content();
                                                    info.setContentUUID(bean.get_contentuuid());
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

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(DBConfig.PLAY_PROGRESS, bean.getProgress());
                                                    bundle.putString(DBConfig.PLAYPOSITION, bean.getPlayPosition());
                                                    bundle.putString(DBConfig.PLAYINDEX, bean.getPlayIndex());
                                                    bundle.putString(DBConfig.UPDATE_TIME, String.valueOf(bean.getUpdateTime()));
                                                    DBUtil.addHistory(SharePreferenceUtils.getUserId(context), info, bundle, null, DBConfig.REMOTE_HISTORY_TABLE_NAME);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onDataNotAvailable() {
                                            Log.d(TAG, "---getUserCenterRecordList getHistoryList onDataNotAvailable");
                                        }
                                    });
                } else {
                    userId = SystemUtils.getDeviceMac(context.getApplicationContext());
                }
            }
        });

    }


    public void reportUserBehaviorUtils(final Context context) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                reportUserCenterRecordList(context);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String value) throws Exception {

                    }
                });
    }

    private void reportUserCenterRecordList(final Context context) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                boolean status = TokenRefreshUtil.getInstance().isTokenRefresh(context);
                Log.d(TAG, "---reportUserCenterRecordList:isTokenRefresh:status:" + status);
                //获取登录状态
                String token = SharePreferenceUtils.getToken(context.getApplicationContext());
                if (!TextUtils.isEmpty(token)) {
                    e.onNext(token);
                } else {
                    e.onNext("");
                }
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if (!TextUtils.isEmpty(s)) {
                    int syncStatus = SharePreferenceUtils.getSyncStatus(context);
                    if (syncStatus == 0) {

                        UserCenterRecordManager.getInstance().getRemoteCollectionList(context, token, userId, "1", "1", new CollectDataSource.GetCollectListCallback() {
                            @Override
                            public void onCollectListLoaded(List<UserCenterPageBean.Bean> CollectList, int totalSize) {

                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        UserCenterRecordManager.getInstance().getRemoteHistoryList(context, token, userId, "1", "1", new HistoryDataSource.GetHistoryListCallback() {
                            @Override
                            public void onHistoryListLoaded(List<UserCenterPageBean.Bean> historyList, int totalSize) {

                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        UserCenterRecordManager.getInstance().getRemoteSubscribe(context, token, userId, "1", "1", new SubDataSource.GetSubscribeListCallback() {
                            @Override
                            public void onSubscribeListLoaded(List<UserCenterPageBean.Bean> subList, int totalSize) {

                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                        UserCenterRecordManager.getInstance().getRemoteFollowList(context, token, userId, "1", "1", new FollowDataSource.GetFollowListCallback() {
                            @Override
                            public void onFollowListLoaded(List<UserCenterPageBean.Bean> FollowList, int totalSize) {

                            }

                            @Override
                            public void onDataNotAvailable() {

                            }
                        });
                    } else if (syncStatus == 1) {

                    } else {

                    }
                }
            }
        });
    }
}
