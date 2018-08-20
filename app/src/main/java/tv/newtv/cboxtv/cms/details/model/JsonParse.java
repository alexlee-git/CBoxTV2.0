package tv.newtv.cboxtv.cms.details.model;

import android.util.Log;

import org.json.JSONObject;

import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * Created by Administrator on 2018/2/7 0007.
 */

public class JsonParse {

    private final String LOG_TAG = "JsonParse--";

    public JsonParse() {
    }

    public ProgramDetailInfo getProgramDetailInfo(String jsonInfo) {
        ProgramDetailInfo mProgramDetailInfo = new ProgramDetailInfo();
        if (jsonInfo == null || jsonInfo.length() < 0) {
            Log.e("JsonParse--", "parseAdInfo: param jsonInfo is null");
            return null;
        }
        try {
            JSONObject rootObject = new JSONObject(jsonInfo);
            String errorCode = rootObject.optString("errorCode");
            Log.d(LOG_TAG, "errorCode=" + errorCode);
            if (!errorCode.equals("0")) {
                Log.e(LOG_TAG, "errorCode is not ok, return null");
                return null;
            }
            JSONObject dataObject = rootObject.getJSONObject("data");
            if (dataObject == null) {
                Log.e(LOG_TAG, "adspaceoObject is null");
                return null;
            }
            mProgramDetailInfo.setContentUUID(dataObject.optString("contentUUID"));
            mProgramDetailInfo.setMAMID(dataObject.optString("MAMID"));
            mProgramDetailInfo.setTitle(dataObject.optString("title"));
            mProgramDetailInfo.setContentType(dataObject.optString("contentType"));
            mProgramDetailInfo.setContentUrl(dataObject.optString("contentUrl"));
            mProgramDetailInfo.setSubTitle(dataObject.optString("subTitle"));
            mProgramDetailInfo.setDuration(dataObject.optString("duration"));
            mProgramDetailInfo.setVideoType(dataObject.optString("videoType"));
            mProgramDetailInfo.setVideoClass(dataObject.optString("videoClass"));
            mProgramDetailInfo.setTags(dataObject.optString("tags"));
            mProgramDetailInfo.setVipFlag(dataObject.optString("vipFlag"));
            mProgramDetailInfo.setVipProductId(dataObject.optString("vipProductId"));
            mProgramDetailInfo.setMovieLevel(dataObject.optString("movieLevel"));
            mProgramDetailInfo.setDefinition(dataObject.optString("definition"));
            mProgramDetailInfo.setVipNumber(dataObject.optString("vipNumber"));
            mProgramDetailInfo.setSortType(dataObject.optString("sortType"));
            mProgramDetailInfo.setPlayOrder(dataObject.optString("playOrder"));
            mProgramDetailInfo.setPermiereTime(dataObject.optString("premiereTime"));
            mProgramDetailInfo.setPermiereChannel(dataObject.optString("premiereChannel"));
            mProgramDetailInfo.setPrize(dataObject.optString("prize"));
            mProgramDetailInfo.setIssueDate(dataObject.optString("issueDate"));
            mProgramDetailInfo.setLeadingRole(dataObject.optString("leadingRole"));
            mProgramDetailInfo.setAudiences(dataObject.optString("audiences"));
            mProgramDetailInfo.setPresenter(dataObject.optString("presenter"));
            mProgramDetailInfo.setProducer(dataObject.optString("producer"));
            mProgramDetailInfo.setTopic(dataObject.optString("topic"));
            mProgramDetailInfo.setGuest(dataObject.optString("guest"));
            mProgramDetailInfo.setReporter(dataObject.optString("reporter"));
            mProgramDetailInfo.setCompetition(dataObject.optString("competition"));
            mProgramDetailInfo.setSubject(dataObject.optString("subject"));
            mProgramDetailInfo.setClassPeriod(dataObject.optString("classPeriod"));
            mProgramDetailInfo.setSinger(dataObject.optString("singer"));
            mProgramDetailInfo.setScreenwriter(dataObject.optString("screenwriter"));
            mProgramDetailInfo.setEnName(dataObject.optString("enName"));
            mProgramDetailInfo.setArea(dataObject.optString("area"));
            mProgramDetailInfo.setDirector(dataObject.optString("director"));
            mProgramDetailInfo.setActors(dataObject.optString("actors"));
            mProgramDetailInfo.setLanguage(dataObject.optString("language"));
            mProgramDetailInfo.setAirtime(dataObject.optString("airtime"));
            mProgramDetailInfo.sethImage(dataObject.optString("hImage"));
            mProgramDetailInfo.setvImage(dataObject.optString("vImage"));
            mProgramDetailInfo.setUUID(dataObject.optString("UUID"));
            mProgramDetailInfo.setSeriesSum(dataObject.optString("seriesSum"));
            mProgramDetailInfo.setGrade(dataObject.optString("grade"));
            //
            mProgramDetailInfo.setlSuperScript(dataObject.optString("lSuperscript"));
            mProgramDetailInfo.setrSuperScript(dataObject.optString("rSupersctipt"));
            mProgramDetailInfo.setlSubScript(dataObject.optString("lSubScript"));
            mProgramDetailInfo.setrSubScript(dataObject.optString("rSubScript"));
            //
            mProgramDetailInfo.setCpCode(dataObject.optString("cpCode"));
            mProgramDetailInfo.setVideoSize(dataObject.optString("videoSize"));
            //
            mProgramDetailInfo.setFreeDuration(dataObject.optString("FreeDuration"));
            mProgramDetailInfo.setBitrateStream(dataObject.optString("bitrateStream"));
            mProgramDetailInfo.setPeriods(dataObject.optString("periods"));

//            Log.e(LOG_TAG, "--------------5---------");
//            if (mProgramDetailInfo.getMAMID().equals("tx")) {
//                mProgramDetailInfo.setData(null);
//            } else {
//                JSONArray cdnArray = rootObject.getJSONArray("CDNURL");
//
//                if (cdnArray != null && cdnArray.length() > 0) {
//                    for (int i = 0; i < cdnArray.length(); i++) {
//                        MediaCDNInfo mediaCDNInfo = new MediaCDNInfo();
//                        JSONObject cdnObject = cdnArray.getJSONObject(i);
//                        int cdnid = Integer.parseInt(cdnObject.optString("CDNId"));
//                        JSONArray mediaArray = cdnObject.getJSONArray("media");
//                        if (mediaArray != null && mediaArray.length() > 0) {
//                            for (int j = 0; j < mediaArray.length(); j++) {
//
//                                JSONObject mediaObject = mediaArray.getJSONObject(j);
//                                mediaCDNInfo.setCDNId(cdnid);
//                                mediaCDNInfo.setMediaType(mediaObject.optString("mediaType"));
//                                mediaCDNInfo.setPlayURL(mediaObject.optString("playUrl"));
//                                mediaCDNInfoList.add(mediaCDNInfo);
//                                Log.e("", "" + mediaCDNInfo.toString());
//                            }
//                        }
//                    }
//                }
//                mProgramDetailInfo.setData(mediaCDNInfoList);
//            }
            mProgramDetailInfo.setData(null);
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }

        Log.i("LOG_TAG", mProgramDetailInfo.toString() + "--");
        return mProgramDetailInfo;
    }


}
