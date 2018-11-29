package tv.newtv.cboxtv.player.util;

import android.util.Log;

import com.newtv.cms.bean.CdnUrl;
import com.newtv.libs.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import tv.newtv.cboxtv.player.ChkPlayResult;
import tv.newtv.cboxtv.player.model.Program;

/**
 * Created by wangkun on 2018/2/23.
 */

public class PlayerNetworkRequestUtils {
    private static final String TAG = "PlayerNetwork";
    private static PlayerNetworkRequestUtils mPlayerNetworkRequestUtils;

    private PlayerNetworkRequestUtils() {
    }

    public static PlayerNetworkRequestUtils getInstance() {
        if (mPlayerNetworkRequestUtils == null) {
            synchronized (PlayerNetworkRequestUtils.class) {
                if (mPlayerNetworkRequestUtils == null) {
                    mPlayerNetworkRequestUtils = new PlayerNetworkRequestUtils();
                }
            }
        }
        return mPlayerNetworkRequestUtils;
    }

    public static String getErrorCode(String response) {
        String errorCode = "";
        try {
            JSONObject rootObject = new JSONObject(response);
            errorCode = rootObject.optString("errorCode");
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

        return errorCode;
    }

    public void playPermissionCheck(String requestJson, Callback<ResponseBody> callback) {
        Log.i(TAG, "playPermissionCheck: ");
        RequestBody requestBody = RequestBody.create(MediaType.parse("Content-Type, " +
                "application/json"), requestJson);

//        Call<ResponseBody> call = NetClient.INSTANCE.getPlayPermissionCheckApi().getCheckResult
// (requestBody);
//        Log.e("permissionCheck",call.request().toString());
//        call.enqueue(callback);
    }

    public ChkPlayResult parsePlayPermissionCheckResult(String jsonInfo) {
        ChkPlayResult mProgramDetailInfo = new ChkPlayResult();
        List<CdnUrl> mediaCDNInfoList = new ArrayList<>();
        Log.e(TAG, jsonInfo + "--");
        if (jsonInfo == null || jsonInfo.length() < 0) {
            Log.i(TAG, "parseAdInfo: param jsonInfo is null");
            return null;
        }
        try {
            JSONObject rootObject = new JSONObject(jsonInfo);
            String errorCode = rootObject.optString("errorCode");
            Log.i(TAG, "errorCode=" + errorCode);
            if (!errorCode.equals("0")) {
                Log.e(TAG, "errorCode is not ok, return null");
                return null;
            }
            JSONObject dataObject = rootObject.getJSONObject("data");
            if (dataObject == null) {
                Log.i(TAG, "adspaceoObject is null");
                return null;
            }
            mProgramDetailInfo.setSubject(dataObject.optString("subject"));
            mProgramDetailInfo.setCompetition(dataObject.optString("competition"));
            mProgramDetailInfo.setLanguage(dataObject.optString("language"));
            mProgramDetailInfo.setCpCode(dataObject.optString("cpcode"));
            mProgramDetailInfo.setSubTitle(dataObject.optString("subTitle"));
            mProgramDetailInfo.setvImage(dataObject.optString("vImage"));
            mProgramDetailInfo.setVipNumber(dataObject.optString("vipNumber"));
            mProgramDetailInfo.setContentType(dataObject.optString("contentType"));
            mProgramDetailInfo.setlSubScript(dataObject.optString("ISubScript"));
            mProgramDetailInfo.setArea(dataObject.optString("area"));
            mProgramDetailInfo.setlSuperScript(dataObject.optString("ISuperscript"));
            mProgramDetailInfo.setPresenter(dataObject.optString("presenter"));
            mProgramDetailInfo.setVideoType(dataObject.optString("videoType"));
            mProgramDetailInfo.setDirector(dataObject.optString("director"));
            mProgramDetailInfo.setBitrateStream(dataObject.optString("bitrateStream"));
            mProgramDetailInfo.setTags(dataObject.optString("tags"));
            mProgramDetailInfo.setActors(dataObject.optString("actors"));
            mProgramDetailInfo.setGrade(dataObject.optString("grade"));
            mProgramDetailInfo.setPermiereChannel(dataObject.optString("premiereChannel"));
            mProgramDetailInfo.setProducer(dataObject.optString("producer"));
            mProgramDetailInfo.setTopic(dataObject.optString("topic"));
            mProgramDetailInfo.setGuest(dataObject.optString("guest"));
            mProgramDetailInfo.setVideoClass(dataObject.optString("videoClass"));
            mProgramDetailInfo.setVipFlag(dataObject.optString("vipFlag"));
            mProgramDetailInfo.setSinger(dataObject.optString("singer"));
            mProgramDetailInfo.setTitle(dataObject.optString("title"));
            mProgramDetailInfo.setPrize(dataObject.optString("prize"));
            mProgramDetailInfo.setPlayOrder(dataObject.optString("playOrder"));
            mProgramDetailInfo.setDuration(dataObject.optString("duration"));
            mProgramDetailInfo.setLeadingRole(dataObject.optString("leadingRole"));
            mProgramDetailInfo.setMovieLevel(dataObject.optString("movieLevel"));
            mProgramDetailInfo.setSeriesSum(dataObject.optString("seriesSum"));
            mProgramDetailInfo.setEnName(dataObject.optString("enName"));
            mProgramDetailInfo.setAudiences(dataObject.optString("audiences"));
            mProgramDetailInfo.setPeriods(dataObject.optString("periods"));
            mProgramDetailInfo.setDefinition(dataObject.optString("definition"));
            mProgramDetailInfo.setVipProductId(dataObject.optString("vipProductId"));
            mProgramDetailInfo.setIssueDate(dataObject.optString("issueDate"));
            mProgramDetailInfo.setScreenwriter(dataObject.optString("screenwriter"));
            mProgramDetailInfo.setrSubScript(dataObject.optString("rSubScript"));
            mProgramDetailInfo.setReporter(dataObject.optString("reporter"));
            mProgramDetailInfo.setClassPeriod(dataObject.optString("classPeriod"));
            mProgramDetailInfo.setVideoSize(dataObject.optString("videoSize"));
            mProgramDetailInfo.setAirtime(dataObject.optString("airtime"));
            mProgramDetailInfo.setContentUrl(dataObject.optString("contentUrl"));
            mProgramDetailInfo.setrSuperScript(dataObject.optString("rSupersctipt"));
            mProgramDetailInfo.setSortType(dataObject.optString("sortType"));
            mProgramDetailInfo.setContentUUID(dataObject.optString("contentUUID"));
            mProgramDetailInfo.setPermiereTime(dataObject.optString("premiereTime"));
            mProgramDetailInfo.sethImage(dataObject.optString("hImage"));
            mProgramDetailInfo.setMAMID(dataObject.optString("mamid"));
            mProgramDetailInfo.setUUID(dataObject.optString("uuid"));
            mProgramDetailInfo.setFreeDuration(dataObject.optString("freeDuration"));
            mProgramDetailInfo.setProgramSeriesUUIDs(dataObject.optString("programSeriesUUIDs"));

            mProgramDetailInfo.setCategoryIds(dataObject.optString("categoryIds"));
            mProgramDetailInfo.setEncryptFlag(dataObject.optBoolean("encryptFlag"));
            mProgramDetailInfo.setDecryptKey(dataObject.optString("decryptKey"));

            try {
                JSONArray cdnArray = dataObject.getJSONArray("cdnurl");
                if (cdnArray != null && cdnArray.length() > 0) {
                    for (int i = 0; i < cdnArray.length(); i++) {
                        JSONObject cdnObject = cdnArray.getJSONObject(i);
                        String cdnid = cdnObject.optString("cdnid");
                        JSONArray mediaArray = cdnObject.getJSONArray("media");
                        if (mediaArray != null && mediaArray.length() > 0) {
                            for (int j = 0; j < mediaArray.length(); j++) {
                                CdnUrl mediaCDNInfo = new CdnUrl();
                                JSONObject mediaObject = mediaArray.getJSONObject(j);
                                mediaCDNInfo.setCNDId(cdnid);
                                mediaCDNInfo.setMediaType(mediaObject.optString("mediaType"));
                                mediaCDNInfo.setPlayURL(mediaObject.optString("playUrl"));
                                mediaCDNInfoList.add(mediaCDNInfo);
                                Log.i(TAG, "" + mediaCDNInfo.toString());
                            }
                        }
                    }
                }

                List<Program> programsList = new ArrayList<>();
                JSONArray programs = dataObject.getJSONArray("programs");
                if(programs != null && programs.length() > 0){
                    for(int i = 0; i < programs.length(); i++){
                        JSONObject obj = programs.getJSONObject(i);
                        Program program = new Program();
                        program.setProgramSeriesUUID(obj.optString("programSeriesUUID"));
                        program.setProgramSeriesId(obj.optString("programSeriesId"));
                        program.setVipNumber(obj.optString("vipNumber"));
                        program.setTitle(obj.optString("title"));
                        program.setVipFlag(obj.optString("vipFlag"));
                        program.setIsVip(obj.optString("isVip"));
                        programsList.add(program);
                    }
                }
                mProgramDetailInfo.setPrograms(programsList);
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }

            mProgramDetailInfo.setData(mediaCDNInfoList);

        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }


        return mProgramDetailInfo;

    }
}
