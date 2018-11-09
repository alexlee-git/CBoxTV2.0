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

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;


/**
 * 项目名称:     CBoxTV2.0
 * 包名:         tv.newtv.cboxtv.uc.v2.data
 * 创建事件:     下午 2:40
 * 创建人:       caolonghe
 * 创建日期:     2018/9/21 0021
 */
public class HistoryRemoteDataSource implements HistoryDataSource {
    private static final String TAG = "lx";

    private static HistoryRemoteDataSource INSTANCE;
    private Context mContext;

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
        if (Constant.CONTENTTYPE_PS.equals(type) || Constant.CONTENTTYPE_CG.equals(type) || Constant.CONTENTTYPE_CS.equals(type)) {
            mType = "0";
            parentId = entity.get_contentuuid();
            subId = entity.getPlayId();
        } else if (Constant.CONTENTTYPE_PG.equals(type) || Constant.CONTENTTYPE_CP.equals(type)) {
            mType = "1";
            subId = entity.get_contentuuid();
        }

        String Authorization = "Bearer " + SharePreferenceUtils.getToken(mContext);
        String userId = SharePreferenceUtils.getUserId(mContext);

        Log.d(TAG, "report history item, user_id " + userId);

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
                        entity.get_actiontype())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.d(TAG, "add History result : " + getServerResultMessage(responseBody));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "add history onError");
                    }

                    @Override
                    public void onComplete() {
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
    public void deleteRemoteHistory(String token, @NonNull String userId, String contentType, String appKey, String channelCode, String contentuuids) {
        String isProgram = null;
        String program_child = null;
        String contentUUid = null;
        Log.e(TAG, "contentType: " + contentType);
        if (!"clean".equals(contentuuids)) {
            isProgram = (TextUtils.equals(contentType, Constant.CONTENTTYPE_CS)
                    || TextUtils.equals(contentType, Constant.CONTENTTYPE_PS)
                    || TextUtils.equals(contentType, Constant.CONTENTTYPE_CG)) ? "0" : "1";
            if (TextUtils.equals(isProgram, "0")) {
                program_child = "";
                contentUUid = contentuuids;
            } else if (TextUtils.equals(isProgram, "1")) {
                program_child = contentuuids;
                contentUUid = "";
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
                        contentUUid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        Log.d(TAG, "remove history record result : " + getServerResultMessage(responseBody));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "delete history occur error");
                    }

                    @Override
                    public void onComplete() {
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
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

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
                                } else if (Constant.CONTENTTYPE_PS.equals(contentType) || Constant.CONTENTTYPE_CG.equals(contentType) || Constant.CONTENTTYPE_CS.equals(contentType)) {
                                    entity.set_contentuuid(item.optString("programset_id"));
                                } else {
                                    Log.d(TAG, "invalid contentType : " + contentType);
                                }

                                entity.set_contenttype(contentType);

                                // entity.set_contentuuid(item.optString("programset_id"));
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "get history list error:" + e.toString());
                        if (callback != null) {
                            callback.onHistoryListLoaded(null, 0);
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private String getServerResultMessage(ResponseBody responseBody) {
        String result = "";
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.optString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            result = "parse json occur error";
        } catch (IOException e) {
            e.printStackTrace();
            result = "parse responseBody occur error";
        }
        return result;
    }
}
