package tv.newtv.cboxtv.utils;

import android.util.Log;

import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tv.icntv.been.IcntvPlayerInfo;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.util.GsonUtil;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.PlayerNetworkRequestUtils;
import tv.newtv.cboxtv.player.model.LivePermissionCheckBean;
import tv.newtv.cboxtv.player.model.PlayCheckRequestBean;
import tv.newtv.cboxtv.player.model.VideoDataStruct;

/**
 * Created by TCP on 2018/5/11.
 */

public class LivePermissionCheckUtil {
    private static final String TAG = "LivePermissionCheckUtil";

    public interface MyPermissionCheckListener{

        void onSuccess(LivePermissionCheckBean result);

        void onFail();
    }

    public static abstract class PermissionCheck implements MyPermissionCheckListener{

        @Override
        public void onSuccess(LivePermissionCheckBean result) {

        }

        @Override
        public void onFail() {

        }
    }

    public static PlayCheckRequestBean createPlayCheckRequest(ProgramInfo liveProgramInfo) {
        return createPlayCheckRequest(liveProgramInfo.getContentUUID(),liveProgramInfo.getPlayUrl());
    }

    public static PlayCheckRequestBean createPlayCheckRequest(String contentUUID, String url) {
        PlayCheckRequestBean playCheckRequestBean = new PlayCheckRequestBean();
        playCheckRequestBean.setAppKey(Constant.APP_KEY);
        playCheckRequestBean.setChannelId(Constant.CHANNEL_ID);
        playCheckRequestBean.setSource("NEWTV");
        playCheckRequestBean.setId(contentUUID);
        playCheckRequestBean.setPid(url);

        Log.i(TAG, "contentUUID："+contentUUID+",url:"+url);
        return playCheckRequestBean;
    }


    public static void startPlayPermissionsCheck(final PlayCheckRequestBean playCheckRequestBean, final MyPermissionCheckListener listener) {
        try {
            Gson gson = new Gson();
            String requestJson = gson.toJson(playCheckRequestBean);
            PlayerNetworkRequestUtils.getInstance().playPermissionCheck(requestJson, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    Log.i(TAG, "onResponse: "+Encryptor.decrypt(Constant.APPSECRET,"+XzokFPWGc3tXlyLrh4C11iJwrkzUe+i0XHZ4l4B8uyvT2h8WpPvhT5RK970pwRI"));
                    try {
                        if (response == null || response.body() == null) {
                            Log.i(TAG, "调用鉴权接口后没有返回数据:"+playCheckRequestBean);
                            if(listener != null){
                                listener.onFail();
                            }
                            return;
                        }
                        String responseStr = response.body().string();
                        Log.i(TAG, "onResponse: " + responseStr);
                        Log.i(TAG, "onResponse: "+playCheckRequestBean);
                        LivePermissionCheckBean livePermissionCheck = GsonUtil.fromjson(responseStr, LivePermissionCheckBean.class);
                        if ("0".equals(livePermissionCheck.getErrorCode())) {
                            if(listener != null){
                                listener.onSuccess(livePermissionCheck);
                            }
                        } else {
                            if(listener != null){
                                listener.onFail();
                            }
                            Log.i(TAG, "鉴权失败:"+playCheckRequestBean);
                        }
                    } catch (Exception e) {
                        LogUtils.e(e.toString());
                        if(listener != null){
                            listener.onFail();
                        }
                        Log.i(TAG, "鉴权异常:"+playCheckRequestBean);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(listener != null){
                        listener.onFail();
                    }
                    Log.i(TAG, "鉴权异常:"+playCheckRequestBean);
                }
            });
        } catch (Exception e) {
            LogUtils.e(e.toString());
            if(listener != null){
                listener.onFail();
            }
        }
    }

    public static void setPermissionCheckToInfo(LivePermissionCheckBean livePermissionCheck, IcntvPlayerInfo icntvPlayerInfo){
        if(livePermissionCheck != null && livePermissionCheck.getData() != null && livePermissionCheck.getData().isEncryptFlag()){
            String result = Encryptor.decrypt(Constant.APPSECRET,livePermissionCheck.getData().getDecryptKey());
            Log.i(TAG, "livePermissionCheck.getData().getDecryptKey()："+livePermissionCheck.getData().getDecryptKey()
                    +",解密结果："+result);
            icntvPlayerInfo.setKey(result);
        }
    }
    public static void setPermissionCheckToInfo(LivePermissionCheckBean livePermissionCheck, VideoDataStruct dataStruct){
        if(livePermissionCheck != null && livePermissionCheck.getData() != null && livePermissionCheck.getData().isEncryptFlag()){
            String result = Encryptor.decrypt(Constant.APPSECRET,livePermissionCheck.getData().getDecryptKey());
            Log.i(TAG, "livePermissionCheck.getData().getDecryptKey()："+livePermissionCheck.getData().getDecryptKey()
                    +",解密结果："+result);
            dataStruct.setKey(result);
        }
    }
}
