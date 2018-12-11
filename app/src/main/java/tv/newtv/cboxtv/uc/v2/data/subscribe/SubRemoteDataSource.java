package tv.newtv.cboxtv.uc.v2.data.subscribe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.SharePreferenceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;


/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/27 0021
 */
public class SubRemoteDataSource implements SubDataSource {
    private static final String TAG = "lx";

    private static SubRemoteDataSource INSTANCE;
    private Context mContext;
    private Disposable mAddDisposable, mGetListDisposable, mDeleteDisposable;

    public static SubRemoteDataSource getInstance(Context mContext) {
        if (INSTANCE == null) {
            INSTANCE = new SubRemoteDataSource(mContext);
        }
        return INSTANCE;
    }

    public SubRemoteDataSource(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void addRemoteSubscribe(@NonNull UserCenterPageBean.Bean bean) {
        String mType = "0";
        String type = bean.get_contenttype();

        String parentId = "";
        String subId = "";

        if (Constant.CONTENTTYPE_CL.equals(type) || Constant.CONTENTTYPE_TV.equals(type)) {
            mType = "0";
            parentId = bean.get_contentuuid();
            subId = bean.getPlayId();
        }

        String Authorization = "Bearer " + SharePreferenceUtils.getToken(mContext);
        String User_id = SharePreferenceUtils.getUserId(mContext);

        Log.e(TAG, "report subscribe item user_id : " + User_id);

        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .addSubscribes(Authorization,
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
                        bean.getSuperscript(),
                        bean.get_contenttype(),
                        bean.getPlayIndex(),
                        bean.get_actiontype(),
                        bean.getContentId()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                        mAddDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }

                    @Override
                    public void onComplete() {
                        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
                    }
                });
    }

    @Override
    public void deleteRemoteSubscribe(@NonNull UserCenterPageBean.Bean bean) {
        String mType = "0";
        String type = bean.get_contenttype();

        if ("PS".equals(type) || "CG".equals(type) || "CS".equals(type)) {
            mType = "0";
        } else if ("PG".equals(type) || "CP".equals(type)) {
            mType = "1";
        }

        String Authorization = "Bearer " + SharePreferenceUtils.getToken(mContext);
        String User_id = SharePreferenceUtils.getUserId(mContext);
        Log.e("UserId", User_id + "");
        String[] programset_ids = new String[]{bean.get_contentuuid()};
        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .deleteSubscribes(Authorization,
                        User_id,
                        mType,
                        Libs.get().getChannelId(),
                        Libs.get().getAppKey(),
                        programset_ids)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                        mDeleteDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }

                    @Override
                    public void onComplete() {
                        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
                    }
                });
    }

    @Override
    public void getRemoteSubscribeList(String token, String userId, String appKey, String channelCode, String offset, String limit, final @NonNull GetSubscribeListCallback callback) {
        final String Authorization = "Bearer " + token;

        NetClient.INSTANCE
                .getUserCenterLoginApi()
                .getSubscribesList(Authorization, userId, "", Libs.get().getAppKey(), Libs.get().getChannelId(), offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                        mGetListDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            int totalSize = 0;
                            JSONObject jsonObject = new JSONObject(responseBody.string());
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
                                Log.d("sub", "getRemoteSubscribeList contentId : " + entity.get_contentuuid());
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
                                callback.onSubscribeListLoaded(infos, totalSize);
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
                        Log.e(TAG, "get Subscribe list error:" + e.toString());
                        if (callback != null) {
                            callback.onSubscribeListLoaded(null, 0);
                        }
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }

                    @Override
                    public void onComplete() {
                        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
                    }
                });
    }

    @Override
    public void releaseSubscribeResource() {
        INSTANCE = null;
        UserCenterRecordManager.getInstance().unSubscribe(mAddDisposable);
        UserCenterRecordManager.getInstance().unSubscribe(mDeleteDisposable);
        UserCenterRecordManager.getInstance().unSubscribe(mGetListDisposable);
    }
}
