package tv.newtv.cboxtv.uc.v2.sub;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.Libs;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.uc.v2.TokenRefreshUtil;
import tv.newtv.cboxtv.uc.v2.listener.INotifyLoginStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyMemberStatusCallback;
import tv.newtv.cboxtv.utils.BaseObserver;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         上午10:54
 * 创建人:           lixin
 * 创建日期:         2018/9/29
 */


public class QueryUserStatusUtil {
    private Disposable mMemberInfoDisposable;
    private static String sign_member_open_not = "member_open_not";//未开通会员
    private static String sign_member_open_lose = "member_open_lose";//已开通，但失效
    private static String sign_member_open_good = "member_open_good";//已开通，有效

    public static final String SIGN_MEMBER_OPEN_NOT = sign_member_open_not;
    public static final String SIGN_MEMBER_OPEN_CLOSE = sign_member_open_lose;
    public static final String SIGN_MEMBER_OPEN_GOOD = sign_member_open_good;

    public static final String id = "id";//id
    public static final String userId = "userId";//用户Id
    public static final String productId = "productId";//产品id
    public static final String appKey = "appKey";//应用key
    public static final String expireTime = "expireTime";//到期时间

    private QueryUserStatusUtil() {
    }

    private static QueryUserStatusUtil mInstance;
    private final String TAG = "lx";

    public static QueryUserStatusUtil getInstance() {
        if (mInstance == null) {
            synchronized (QueryUserStatusUtil.class) {
                if (mInstance == null) {
                    mInstance = new QueryUserStatusUtil();
                }
            }
        }
        return mInstance;
    }

    public void getLoginStatus(final Context context, final INotifyLoginStatusCallback callback) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                TokenRefreshUtil.getInstance().isTokenRefresh(context);
                String token = SharePreferenceUtils.getToken(context);
                if (TextUtils.isEmpty(token)) {
                    e.onNext(false);
                } else {
                    e.onNext(true);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean status) {
                        if (callback != null) {
                            callback.notifyLoginStatusCallback(status);
                            Log.d(TAG, "get login status : " + status);
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


    //获取会员状态
    public void getMemberStatus(final Context context, final String UUid, final INotifyMemberStatusCallback callback) {
        try {
            final Bundle mMemberBundle = new Bundle();
            getLoginStatus(context, new INotifyLoginStatusCallback() {
                @Override
                public void notifyLoginStatusCallback(boolean status) {
                    if (status) {
                        String token = SharePreferenceUtils.getToken(context);
                        NetClient.INSTANCE.getUserCenterMemberInfoApi().getMemberInfo("Bearer " + token, "", Libs.get().getAppKey(),UUid).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseObserver<ResponseBody>() {

                            @Override
                            public void onSubscribe(Disposable d) {
                                unSubscribe(mMemberInfoDisposable);
                                mMemberInfoDisposable = d;
                            }

                            @Override
                            public void onNext(ResponseBody responseBody) {
                                String memberInfo = null;
                                try {
                                    memberInfo = responseBody.string();
                                    checkUserOffline(memberInfo);
                                    Log.e(TAG, "---getMemberStatus:onNext:" + memberInfo);
                                    JSONArray jsonArray = new JSONArray(memberInfo);
                                    if (jsonArray != null && jsonArray.length() > 0) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                                        mMemberBundle.putInt(id, jsonObject.optInt(id));
                                        mMemberBundle.putString(appKey, jsonObject.optString(appKey));
                                        mMemberBundle.putInt(userId, jsonObject.optInt(userId));
                                        mMemberBundle.putInt(productId, jsonObject.optInt(productId));
                                        String expireTimeDate = jsonObject.optString(expireTime);
                                        mMemberBundle.putString(expireTime, expireTimeDate);
                                        if (!TextUtils.isEmpty(expireTimeDate)) {
                                            //有效期截止时间毫秒数
                                            long expireTimeInMillis = TimeUtil.getInstance().getSecondsFromDate(expireTimeDate);
                                            //与当前时间进行对比，判断会员是否到期
                                            long currentTimeInMillis = TimeUtil.getInstance().getCurrentTimeInMillis();
                                            if (expireTimeInMillis >= currentTimeInMillis) {
                                                //用户会员有效
                                                if (callback != null) {
                                                    callback.notifyLoginStatusCallback(sign_member_open_good, mMemberBundle);
                                                }
                                            } else {
                                                //用户会员无效
                                                if (callback != null) {
                                                    callback.notifyLoginStatusCallback(sign_member_open_lose, mMemberBundle);
                                                }
                                            }
                                        } else {
                                            if (callback != null) {
                                                callback.notifyLoginStatusCallback(sign_member_open_not, mMemberBundle);
                                            }
                                        }
                                    } else {
                                        if (callback != null) {
                                            callback.notifyLoginStatusCallback(sign_member_open_not, mMemberBundle);
                                        }
                                    }
                                    unSubscribe(mMemberInfoDisposable);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "---getMemberStatus:onError:" + e.toString());
                                unSubscribe(mMemberInfoDisposable);
                            }

                            @Override
                            public void dealwithUserOffline() {
                                Log.i(TAG, "dealwithUserOffline: ");
                            }

                            @Override
                            public void onComplete() {
                                unSubscribe(mMemberInfoDisposable);
                            }
                        });
                    } else {
                        if (callback != null) {
                            callback.notifyLoginStatusCallback(sign_member_open_not, mMemberBundle);
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            unSubscribe(mMemberInfoDisposable);
            Log.e(TAG, "---getMemberStatus:Exception:" + e.toString());
        }
    }

    /**
     * 解除数据订阅关系
     *
     * @param disposable
     */
    private void unSubscribe(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }
}

