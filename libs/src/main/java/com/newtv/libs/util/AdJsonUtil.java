package com.newtv.libs.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tv.icntv.icntvplayersdk.been.MaterialInfo;
import tv.icntv.icntvplayersdk.been.PlayerAdInfo;
import tv.icntv.icntvplayersdk.been.PlayerAdInfos;


public class AdJsonUtil {
    private static final String LOG_TAG = "adsdk";
    private static final String[] adTypes = new String[]{"topic","quit","before", "middle", "after", "float", "buffer", "pause", "open", "desk", "before_live"};

    public AdJsonUtil() {
    }

    public static List<PlayerAdInfos> parseAdInfo(String jsonInfo) {
        if (jsonInfo == null) {
            Log.e("adsdk", "parseAdInfo: param jsonInfo is null");
            return null;
        } else {
            ArrayList adInfosList = new ArrayList();

            try {
                JSONObject rootObject = new JSONObject(jsonInfo);
                String status = rootObject.optString("status");
                Log.d("adsdk", "status=" + status);
                if (!status.equals("1")) {
                    Log.e("adsdk", "status is not ok, return null");
                    return null;
                }

                JSONObject adspaceoObject = rootObject.optJSONObject("adspaces");
                if (adspaceoObject == null) {
                    Log.e("adsdk", "adspaceoObject is null");
                    return null;
                }

                for(int i = 0; i < adTypes.length; ++i) {
                    JSONArray adinfoArray = adspaceoObject.optJSONArray(adTypes[i]);
                    if (adinfoArray != null) {
                        Log.d("adsdk", adTypes[i] + " is not null");
                        PlayerAdInfos adInfos = new PlayerAdInfos();
                        adInfos.m_info = new ArrayList();
                        adInfos.m_type = adTypes[i];

                        for(int j = 0; j < adinfoArray.length(); ++j) {
                            Log.d("adsdk", "########j=" + j);
                            JSONObject adspaceObject = adinfoArray.optJSONObject(j);
                            if (adspaceObject != null) {
                                PlayerAdInfo adInfo = new PlayerAdInfo();
                                adInfo.m_material = new ArrayList();
                                adInfo.m_aid = adspaceObject.optString("aid");
                                adInfo.m_mid = adspaceObject.optString("mid");
                                adInfo.m_pos = adspaceObject.optString("pos");
                                adInfo.m_ext = adspaceObject.optString("ext");
                                Log.d("adsdk", "aid=" + adInfo.m_aid + "; mid=" + adInfo.m_mid + "; pos=" + adInfo.m_pos);
                                JSONArray materialArray = adspaceObject.optJSONArray("materials");
                                Log.d("adsdk", "materialArray.length = " + materialArray.length());

                                for(int k = 0; k < materialArray.length(); ++k) {
                                    Log.d("adsdk", "######k=" + k);
                                    JSONObject materialItem = materialArray.optJSONObject(k);
                                    if (materialItem != null) {
                                        MaterialInfo material = new MaterialInfo();
                                        material.m_id = materialItem.optString("id");
                                        material.m_type = materialItem.optString("type");
                                        material.m_name = materialItem.optString("name");
                                        if (!material.m_type.equals("video") && !material.m_type.equals("image")) {
                                            if (material.m_type.equals("text")) {
                                                material.m_fontContent = materialItem.optString("font_content");
                                                material.m_fontColor = materialItem.optString("font_color");
                                                material.m_fontSize = materialItem.optInt("font_size");
                                                material.m_fontStyle = materialItem.optString("font_style");
                                            }
                                        } else {
                                            material.m_fileName = materialItem.optString("file_name");
                                            material.m_fileSize = materialItem.optInt("file_size");
                                            material.m_filePath = materialItem.optString("file_path");
                                            material.m_playTime = materialItem.optInt("play_time");
                                        }

                                        material.m_eventType = materialItem.optString("event_type");
                                        material.m_eventContent = materialItem.optString("event_content");
                                        adInfo.m_material.add(material);
                                        Log.d("adsdk", "id=" + ((MaterialInfo)adInfo.m_material.get(k)).m_id);
                                    }
                                }

                                adInfos.m_info.add(adInfo);
                            }
                        }

                        adInfosList.add(adInfos);
                    }
                }
            } catch (Exception var15) {
                var15.printStackTrace();
            }

            return adInfosList;
        }
    }

    public static boolean parseReportInfo(String jsonInfo) {
        if (jsonInfo == null) {
            Log.e("adsdk", "parseReportInfo: param jsonInfo is null");
            return false;
        } else {
            try {
                JSONObject rootObject = new JSONObject(jsonInfo);
                String status = rootObject.optString("status");
                Log.d("adsdk", "status=" + status);
                if (!status.equals("1")) {
                    Log.e("adsdk", "status is not ok");
                    return false;
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            return true;
        }
    }
}

