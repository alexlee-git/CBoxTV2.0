package tv.newtv.cboxtv.cms.ad;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import tv.newtv.cboxtv.cms.ad.model.AdInfo;
import tv.newtv.cboxtv.cms.ad.model.MaterialInfo;
import tv.newtv.cboxtv.cms.ad.model.AdInfos;
import tv.newtv.cboxtv.cms.util.LogUtils;

public class JsonParse
{
	private static final String LOG_TAG = "adsdk";
	private static final String[] adTypes = {"before", "middle", "after", "float", "buffer", "pause", "open", "desk","topic","buygoods"};
	
	public JsonParse() {}
	
	public static List<AdInfos> parseAdInfo(String jsonInfo) {
		if (TextUtils.isEmpty(jsonInfo)) {
			Log.e(LOG_TAG, "parseAdInfo: param jsonInfo is null");
			return null;
		}


		List<AdInfos> adInfosList = new ArrayList<AdInfos>();
		try {
			JSONObject rootObject = new JSONObject(jsonInfo);
			
			//if status is not 1, return
			String status = rootObject.optString("status");
			Log.d(LOG_TAG, "status=" + status);
			if (!status.equals("1")) {
				Log.e(LOG_TAG, "status is not ok, return null");
				return null;
			}
			
			//find adspaces
			JSONObject adspaceoObject = rootObject.optJSONObject("adspaces");
			if (adspaceoObject == null) {
				Log.e(LOG_TAG, "adspaceoObject is null");
				return null;
			}
			
			for (int i = 0; i < adTypes.length; i++) {
				JSONArray adinfoArray = adspaceoObject.optJSONArray(adTypes[i]);
				if (adinfoArray != null) {
					Log.d(LOG_TAG, adTypes[i] + " is not null");
					AdInfos adInfos = new AdInfos();
					adInfos.m_info = new ArrayList<AdInfo>();    //AdInfos -> m_info
					
					adInfos.m_type = adTypes[i];                 //AdInfos -> m_type
					
					for (int j = 0; j < adinfoArray.length(); j++) {
						Log.d(LOG_TAG, "########j=" + j);
						JSONObject adspaceObject = adinfoArray.optJSONObject(j);
						if (adspaceObject != null) {
							AdInfo adInfo = new AdInfo();
							adInfo.m_material = new ArrayList<MaterialInfo>();
							
							adInfo.m_aid = adspaceObject.optInt("aid");  //aid
							adInfo.m_mid = adspaceObject.optInt("mid");  //mid
							adInfo.m_pos = adspaceObject.optString("pos");  //pos
							
							Log.d(LOG_TAG, "aid=" + adInfo.m_aid + "; mid=" + adInfo.m_mid + "; pos=" + adInfo.m_pos);
							
							//parse materials[]
							JSONArray materialArray = adspaceObject.optJSONArray("materials");
							Log.d(LOG_TAG, "materialArray.length = " + materialArray.length());
							for (int k = 0; k < materialArray.length(); k++) {
								Log.d(LOG_TAG, "######k=" + k);
								JSONObject materialItem = materialArray.optJSONObject(k);
								if (materialItem != null) {
									MaterialInfo material = new MaterialInfo();
									material.m_id = materialItem.optInt("id");
									material.m_type = materialItem.optString("type");
									material.m_name = materialItem.optString("name");
									
									if (material.m_type.equals("video") || material.m_type.equals("image")) {
										material.m_fileName = materialItem.optString("file_name");
										material.m_fileSize = materialItem.optInt("file_size");
										material.m_filePath = materialItem.optString("file_path");
										material.m_playTime = materialItem.optInt("play_time");
									} else if (material.m_type.equals("text")) {
										material.m_fontContent = materialItem.optString("font_content");
										material.m_fontColor = materialItem.optString("font_color");
										material.m_fontSize = materialItem.optInt("font_size");
										material.m_fontStyle = materialItem.optString("font_style");
									}
									
									material.m_eventType = materialItem.optString("event_type");
									material.m_eventContent = materialItem.optString("event_content");
									
									adInfo.m_material.add(material);
									Log.d(LOG_TAG, "id=" + adInfo.m_material.get(k).m_id);
								}
							}
							
							adInfos.m_info.add(adInfo);
						}
					}
					
					adInfosList.add(adInfos);
				}
			}
		} catch (Exception e) {
			LogUtils.e(e);
		}
		
		return adInfosList;
	}
	
	public static boolean parseReportInfo(String jsonInfo) {
		if (jsonInfo == null) {
			Log.e(LOG_TAG, "parseReportInfo: param jsonInfo is null");
			return false;
		}
		
		try {
			JSONObject rootObject = new JSONObject(jsonInfo);
			
			String status = rootObject.optString("status");
			Log.d(LOG_TAG, "status=" + status);
			if (!status.equals("1")) {
				LogUtils.i("status is not ok");
				return false;
			}
			
		} catch (Exception e) {
			LogUtils.e(e);
		}
		
		return true;
	}
}