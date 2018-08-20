package tv.newtv.cboxtv.cms.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.cms.mainPage.model.ExtendAttr;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleItem;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.mainPage.model.SearchConditions;

/**
 * 类描述：解析数据的工具类
 * 创建人：wqs
 * 创建时间： 2018/2/1 0001 19:33
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class ModuleUtils {
    private static ModuleUtils mInstance;
    private final String TAG = this.getClass().getSimpleName();

    public static ModuleUtils getInstance() {
        if (mInstance == null) {
            synchronized (ModuleUtils.class) {
                if (mInstance == null) {
                    mInstance = new ModuleUtils();
                }
            }
        }
        return mInstance;
    }

    //组件数据解析
    public ModuleInfoResult parseJsonForModuleInfo(String dataString) {
//<<<<<<< HEAD
        Log.i(TAG, "parseJsonForModuleInfo: "+dataString);
        return GsonUtil.fromjson(dataString,ModuleInfoResult.class);
//        ModuleInfoResult mModuleInfoResult = new ModuleInfoResult();
//        List<ProgramInfo> mProgramInfoList;
//        List<ModuleItem> mModuleItemsList;
//        List<SearchConditions> mSearchConditionsList = new ArrayList<>();
//        if (dataString.equals("")) {
//            Log.e(TAG, "----下载的组件页面数据为空---");
//            return null;
//        }
//
//        ModuleItem mModuleItem = null;
//        try {
//
//            JSONObject mJsonObject = new JSONObject(dataString);
//            if (mJsonObject != null) {
//                mModuleInfoResult.setErrorCode(mJsonObject.optString("errorCode"));
//                mModuleInfoResult.setErrorMessage(mJsonObject.getString("errorMessage"));
//                mModuleInfoResult.setIsNav(mJsonObject.optInt("isNav"));
//
//                if (mModuleInfoResult.getIsNav() == ModuleInfoResult.NAV_PAGE
//                        || mModuleInfoResult.getIsNav() == ModuleInfoResult.SPECIAL_PAGE) {
//                    mModuleInfoResult.setPageTitle(mJsonObject.optString("pageTitle"));
//                    mModuleInfoResult.setSubTitle(mJsonObject.optString("subTitle"));
//                    mModuleInfoResult.setTemplateZT(mJsonObject.optString("templateZT"));
//                    mModuleInfoResult.setDescription(mJsonObject.optString("description"));
//                    mModuleInfoResult.setPageBackground(mJsonObject.optString("background"));
//                    mModuleInfoResult.setIsAd(mJsonObject.optInt("isAd"));
//                    mModuleInfoResult.setIsCollection(mJsonObject.optInt("isCollection"));
//                }
//
//                JSONArray jsonArray = mJsonObject.getJSONArray("data");
//                mModuleItemsList = new ArrayList<>();
//                if (jsonArray != null && jsonArray.length() > 0) {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        mModuleItem = new ModuleItem();
//                        JSONObject mJsonObjectModule = jsonArray.getJSONObject(i);
//                        mModuleItem.setBlockId(mJsonObjectModule.optString("blockId"));
//                        mModuleItem.setBlockTitle(mJsonObjectModule.optString("blockTitle"));
//                        mModuleItem.setBlockImg(mJsonObjectModule.optString("blockImg"));
//                        mModuleItem.setHaveContentTitle(mJsonObjectModule.optString
//                                ("haveContentTitle"));
//                        mModuleItem.setHaveContentSubTitle(mJsonObjectModule.optString
//                                ("haveContentSubTitle"));
//                        mModuleItem.setContentTitlePosition(mJsonObjectModule.optString
//                                ("contentTitlePosition"));
//                        mModuleItem.setHaveBlockTitle(mJsonObjectModule.optString
//                                ("haveBlockTitle"));
//                        mModuleItem.setRowNum(mJsonObjectModule.optString("rowNum"));
//                        mModuleItem.setColNum(mJsonObjectModule.optString("colNum"));
//                        mModuleItem.setBlockType(mJsonObjectModule.optString("blockType"));
//                        mModuleItem.setLayoutCode(mJsonObjectModule.optString("layoutCode"));
//
//                        ProgramInfo mProgramInfo = null;
//                        mProgramInfoList = new ArrayList<>();
//                        JSONArray mJsonArrayPrograms = mJsonObjectModule.getJSONArray("programs");
//                        if (mJsonArrayPrograms != null && mJsonArrayPrograms.length() > 0) {
//                            for (int p = 0; p < mJsonArrayPrograms.length(); p++) {
//                                mProgramInfo = new ProgramInfo();
//
//                                JSONObject mJsonObjectPrograms = mJsonArrayPrograms.getJSONObject
//                                        (p);
//                                if (mJsonObjectPrograms != null) {
//                                    mProgramInfo.setContentUUID(mJsonObjectPrograms.optString
//                                            ("contentUUID"));
//                                    mProgramInfo.setContentType(mJsonObjectPrograms.optString
//                                            ("contentType"));
//                                    mProgramInfo.setImg(mJsonObjectPrograms.optString("img"));
//                                    mProgramInfo.setTitle(mJsonObjectPrograms.optString("title"));
//                                    mProgramInfo.setSubTitle(mJsonObjectPrograms.optString
//                                            ("subTitle"));
//                                    mProgramInfo.setActionType(mJsonObjectPrograms.optString
//                                            ("actionType"));
//                                    mProgramInfo.setActionUri(mJsonObjectPrograms.optString
//                                            ("actionUri"));
//                                    mProgramInfo.setCellType(mJsonObjectPrograms.optString
//                                            ("cellType"));
//                                    mProgramInfo.setGrade(mJsonObjectPrograms.optString("grade"));
//                                    mProgramInfo.setlSuperScript(mJsonObjectPrograms.optString
//                                            ("lSuperScript"));
//                                    mProgramInfo.setrSuperScript(mJsonObjectPrograms.optString
//                                            ("rSuperScript"));
//                                    mProgramInfo.setlSubScript(mJsonObjectPrograms.optString
//                                            ("lSubScript"));
//                                    mProgramInfo.setrSubScript(mJsonObjectPrograms.optString
//                                            ("rSubScript"));
//                                    mProgramInfo.setIsAd(mJsonObjectPrograms.optInt("isAd"));
//
//                                    mProgramInfo.setColumnPoint(mJsonObjectPrograms.optString
//                                            ("columnPoint"));
//                                    mProgramInfo.setRowPoint(mJsonObjectPrograms.optString
//                                            ("rowPoint"));
//                                    mProgramInfo.setColumnLength(mJsonObjectPrograms.optString
//                                            ("columnLength"));
//                                    mProgramInfo.setRowLength(mJsonObjectPrograms.optString
//                                            ("rowLength"));
//                                    mProgramInfo.setCellCode(mJsonObjectPrograms.optString
//                                            ("cellCode"));
//                                    if(mJsonObjectModule.isNull("playUrl")){
//                                        Log.i(TAG, "parseJsonForModuleInfo: isNULL");
//                                    }
//                                    mProgramInfo.setPlayUrl(mJsonObjectModule.optString("playUrl"));
//                                    Log.i(TAG, "parseJsonForModuleInfo: "+mJsonObjectModule.optString("playUrl"));
//                                    if(mJsonObjectModule.isNull("liveLoopType")){
//                                        mProgramInfo.setLiveLoopType(mJsonObjectModule.optString
//                                                ("liveLoopType"));
//                                    }
//                                    if(mJsonObjectModule.isNull("liveParam")){
//                                        mProgramInfo.setLiveParam(mJsonObjectModule.optString("liveParam"));
//                                    }
//                                    if(mJsonObjectModule.isNull("playStartTime")){
//                                        mProgramInfo.setPlayStartTime(mJsonObjectModule.optString("playStartTime"));
//                                    }
//                                    if(mJsonObjectModule.isNull("playEndTime")){
//                                        mProgramInfo.setPlayEndTime(mJsonObjectModule.optString("playEndTime"));
//                                    }
//
//                                    if(!mJsonObjectPrograms.isNull("focusPageUUID")) {
//                                        mProgramInfo.setFocusPageUUID(mJsonObjectPrograms.optString
//                                                ("focusPageUUID"));
//                                    }
//
////                    String cellID = mJsonObjectPrograms.optString("cellID");
////                    Integer cellSpacing = mJsonObjectModule.optInt("cellSpacing");
////                    Integer cellPadding = mJsonObjectModule.optInt("cellPadding");
//
//                                    //当blocktype＝2时，搜索条件有数值需要解析
//                                    if (mJsonObjectModule.optInt("BlockType") != 0 &&
//                                            mJsonObjectModule.optInt("BlockType") == 2) {
//                                        JSONArray mJsonArraySearchConditions =
//                                                mJsonObjectPrograms.getJSONArray
//                                                        ("searchConditions");
//
//                                        SearchConditions mSearchConditions = null;
//
//                                        for (int s = 0; s < mJsonArraySearchConditions.length();
//                                             s++) {
//
//                                            JSONObject mJsonObjectSearchConditions =
//                                                    mJsonArraySearchConditions.getJSONObject(s);
//                                            if (mJsonObjectSearchConditions != null) {
//                                                mSearchConditions = new SearchConditions();
//                                                mSearchConditions.setSearchType
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("searchType"));
//
//                                                mSearchConditions.setCategory
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("category"));
//                                                mSearchConditions.setType
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("type"));
//                                                mSearchConditions.setYear
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("year"));
//                                                mSearchConditions.setArea
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("area"));
//                                                mSearchConditions.setClassType
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("classType"));
//
//                                                mSearchConditionsList.add(mSearchConditions);
//                                            } else {
//                                                Log.e(TAG, "------搜索条件数据为空或解析失败-------");
//                                            }
//
//                                        }
//=======
//        ModuleInfoResult mModuleInfoResult = new ModuleInfoResult();
//        List<ProgramInfo> mProgramInfoList;
//        List<ModuleItem> mModuleItemsList;
//        List<SearchConditions> mSearchConditionsList = new ArrayList<>();
//        if (dataString.equals("")) {
//            Log.e(TAG, "----下载的组件页面数据为空---");
//            return null;
//        }
//
//        ModuleItem mModuleItem = null;
//        try {
//
//            JSONObject mJsonObject = new JSONObject(dataString);
//            if (mJsonObject != null) {
//                mModuleInfoResult.setErrorCode(mJsonObject.optString("errorCode"));
//                mModuleInfoResult.setErrorMessage(mJsonObject.getString("errorMessage"));
//                mModuleInfoResult.setIsNav(mJsonObject.optInt("isNav"));
//
//                if (mModuleInfoResult.getIsNav() == ModuleInfoResult.NAV_PAGE
//                        || mModuleInfoResult.getIsNav() == ModuleInfoResult.SPECIAL_PAGE) {
//                    mModuleInfoResult.setPageTitle(mJsonObject.optString("pageTitle"));
//                    mModuleInfoResult.setSubTitle(mJsonObject.optString("subTitle"));
//                    mModuleInfoResult.setTemplateZT(mJsonObject.optString("templateZT"));
//                    mModuleInfoResult.setDescription(mJsonObject.optString("description"));
//                    mModuleInfoResult.setPageBackground(mJsonObject.optString("background"));
//                    mModuleInfoResult.setIsAd(mJsonObject.optInt("isAd"));
//                    mModuleInfoResult.setIsCollection(mJsonObject.optInt("isCollection"));
//                }
//
//                JSONArray jsonArray = mJsonObject.getJSONArray("data");
//                mModuleItemsList = new ArrayList<>();
//                if (jsonArray != null && jsonArray.length() > 0) {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        mModuleItem = new ModuleItem();
//                        JSONObject mJsonObjectModule = jsonArray.getJSONObject(i);
//                        mModuleItem.setBlockId(mJsonObjectModule.optString("blockId"));
//                        mModuleItem.setBlockTitle(mJsonObjectModule.optString("blockTitle"));
//                        mModuleItem.setBlockImg(mJsonObjectModule.optString("blockImg"));
//                        mModuleItem.setHaveContentTitle(mJsonObjectModule.optString
//                                ("haveContentTitle"));
//                        mModuleItem.setHaveContentSubTitle(mJsonObjectModule.optString
//                                ("haveContentSubTitle"));
//                        mModuleItem.setContentTitlePosition(mJsonObjectModule.optString
//                                ("contentTitlePosition"));
//                        mModuleItem.setHaveBlockTitle(mJsonObjectModule.optString
//                                ("haveBlockTitle"));
//                        mModuleItem.setRowNum(mJsonObjectModule.optString("rowNum"));
//                        mModuleItem.setColNum(mJsonObjectModule.optString("colNum"));
//                        mModuleItem.setBlockType(mJsonObjectModule.optString("blockType"));
//                        mModuleItem.setLayoutCode(mJsonObjectModule.optString("layoutCode"));
//
//                        ProgramInfo mProgramInfo = null;
//                        mProgramInfoList = new ArrayList<>();
//                        JSONArray mJsonArrayPrograms = mJsonObjectModule.getJSONArray("programs");
//                        if (mJsonArrayPrograms != null && mJsonArrayPrograms.length() > 0) {
//                            for (int p = 0; p < mJsonArrayPrograms.length(); p++) {
//                                mProgramInfo = new ProgramInfo();
//
//                                JSONObject mJsonObjectPrograms = mJsonArrayPrograms.getJSONObject
//                                        (p);
//                                if (mJsonObjectPrograms != null) {
//                                    mProgramInfo.setContentUUID(mJsonObjectPrograms.optString
//                                            ("contentUUID"));
//                                    mProgramInfo.setContentType(mJsonObjectPrograms.optString
//                                            ("contentType"));
//                                    mProgramInfo.setImg(mJsonObjectPrograms.optString("img"));
//                                    mProgramInfo.setTitle(mJsonObjectPrograms.optString("title"));
//                                    mProgramInfo.setSubTitle(mJsonObjectPrograms.optString
//                                            ("subTitle"));
//                                    mProgramInfo.setActionType(mJsonObjectPrograms.optString
//                                            ("actionType"));
//                                    mProgramInfo.setActionUri(mJsonObjectPrograms.optString
//                                            ("actionUri"));
//                                    mProgramInfo.setCellType(mJsonObjectPrograms.optString
//                                            ("cellType"));
//                                    mProgramInfo.setGrade(mJsonObjectPrograms.optString("grade"));
//                                    mProgramInfo.setlSuperScript(mJsonObjectPrograms.optString
//                                            ("lSuperScript"));
//                                    mProgramInfo.setrSuperScript(mJsonObjectPrograms.optString
//                                            ("rSuperScript"));
//                                    mProgramInfo.setlSubScript(mJsonObjectPrograms.optString
//                                            ("lSubScript"));
//                                    mProgramInfo.setrSubScript(mJsonObjectPrograms.optString
//                                            ("rSubScript"));
//                                    mProgramInfo.setIsAd(mJsonObjectPrograms.optInt("isAd"));
//
//                                    mProgramInfo.setColumnPoint(mJsonObjectPrograms.optString
//                                            ("columnPoint"));
//                                    mProgramInfo.setRowPoint(mJsonObjectPrograms.optString
//                                            ("rowPoint"));
//                                    mProgramInfo.setColumnLength(mJsonObjectPrograms.optString
//                                            ("columnLength"));
//                                    mProgramInfo.setRowLength(mJsonObjectPrograms.optString
//                                            ("rowLength"));
//                                    mProgramInfo.setCellCode(mJsonObjectPrograms.optString
//                                            ("cellCode"));
//                                    if(mJsonObjectModule.isNull("playUrl")){
//                                        mProgramInfo.setPlayUrl(mJsonObjectModule.optString("playUrl"));
//                                    }
//                                    if(mJsonObjectModule.isNull("liveLoopType")){
//                                        mProgramInfo.setLiveLoopType(mJsonObjectModule.optString
//                                                ("liveLoopType"));
//                                    }
//                                    if(mJsonObjectModule.isNull("liveParam")){
//                                        mProgramInfo.setLiveParam(mJsonObjectModule.optString("liveParam"));
//                                    }
//                                    if(mJsonObjectModule.isNull("playStartTime")){
//                                        mProgramInfo.setPlayStartTime(mJsonObjectModule.optString("playStartTime"));
//                                    }
//                                    if(mJsonObjectModule.isNull("playEndTime")){
//                                        mProgramInfo.setPlayEndTime(mJsonObjectModule.optString("playEndTime"));
//                                    }
//
//                                    if(!mJsonObjectPrograms.isNull("focusPageUUID")) {
//                                        mProgramInfo.setFocusPageUUID(mJsonObjectPrograms.optString
//                                                ("focusPageUUID"));
//                                    }
//
////                    String cellID = mJsonObjectPrograms.optString("cellID");
////                    Integer cellSpacing = mJsonObjectModule.optInt("cellSpacing");
////                    Integer cellPadding = mJsonObjectModule.optInt("cellPadding");
//
//                                    //当blocktype＝2时，搜索条件有数值需要解析
//                                    if (mJsonObjectModule.optInt("BlockType") != 0 &&
//                                            mJsonObjectModule.optInt("BlockType") == 2) {
//                                        JSONArray mJsonArraySearchConditions =
//                                                mJsonObjectPrograms.getJSONArray
//                                                        ("searchConditions");
//
//                                        SearchConditions mSearchConditions = null;
//
//                                        for (int s = 0; s < mJsonArraySearchConditions.length();
//                                             s++) {
//
//                                            JSONObject mJsonObjectSearchConditions =
//                                                    mJsonArraySearchConditions.getJSONObject(s);
//                                            if (mJsonObjectSearchConditions != null) {
//                                                mSearchConditions = new SearchConditions();
//                                                mSearchConditions.setSearchType
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("searchType"));
//
//                                                mSearchConditions.setCategory
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("category"));
//                                                mSearchConditions.setType
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("type"));
//                                                mSearchConditions.setYear
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("year"));
//                                                mSearchConditions.setArea
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("area"));
//                                                mSearchConditions.setClassType
//                                                        (mJsonObjectSearchConditions.optString
//                                                                ("classType"));
//
//                                                mSearchConditionsList.add(mSearchConditions);
//                                            } else {
//                                                Log.e(TAG, "------搜索条件数据为空或解析失败-------");
//                                            }
//
//                                        }
//                                        mProgramInfo.setSearchConditions(mSearchConditionsList);
//                                    }
//
//                                    //当blocktype＝2时，搜索条件有数值需要解析
//                                    if (!mJsonObjectPrograms.isNull("extendAttr")) {
//                                        List<ExtendAttr> mExtendAttrList = new ArrayList<>();
//                                        JSONArray mJsonExtendAttr = mJsonObjectPrograms.getJSONArray
//                                                ("extendAttr");
//                                        if (mJsonExtendAttr != null && mJsonExtendAttr.length() >
//                                                0) {
//                                            ExtendAttr mExtendAttr = null;
//
//                                            for (int s = 0; s < mJsonExtendAttr.length(); s++) {
//
//                                                JSONObject mJsonExtendAttrJSONObject =
//                                                        mJsonExtendAttr.getJSONObject(s);
//
//                                                if (mJsonExtendAttrJSONObject != null) {
//                                                    mExtendAttr = new ExtendAttr();
//                                                    mExtendAttr.setDraw(mJsonExtendAttrJSONObject
//                                                            .optString("draw"));
//                                                    mExtendAttr.setGoal(mJsonExtendAttrJSONObject
//                                                            .optString("goal"));
//                                                    mExtendAttr.setLost(mJsonExtendAttrJSONObject
//                                                            .optString("lost"));
//                                                    mExtendAttr.setMatch(mJsonExtendAttrJSONObject
//                                                            .optString("match"));
//                                                    mExtendAttr.setOrder(mJsonExtendAttrJSONObject
//                                                            .optString("order"));
//                                                    mExtendAttr.setScore(mJsonExtendAttrJSONObject
//                                                            .optString("score"));
//                                                    mExtendAttr.setWin(mJsonExtendAttrJSONObject
//                                                            .optString("win"));
//                                                    mExtendAttr.setSeriesSubUUID(mJsonExtendAttrJSONObject
//                                                            .optString("seriesSubUUID"));
//                                                    mExtendAttrList.add(mExtendAttr);
//                                                } else {
//                                                    Log.e(TAG, "------搜索条件数据为空或解析失败-------");
//                                                }
//
//                                            }
//                                            mProgramInfo.setExtendAttr(mExtendAttrList);
//                                        }
//                                    }
//>>>>>>> feature_fixbug
//                                        mProgramInfo.setSearchConditions(mSearchConditionsList);
//                                    }
//
//                                    //当blocktype＝2时，搜索条件有数值需要解析
//                                    if (!mJsonObjectPrograms.isNull("extendAttr")) {
//                                        List<ExtendAttr> mExtendAttrList = new ArrayList<>();
//                                        JSONArray mJsonExtendAttr = mJsonObjectPrograms.getJSONArray
//                                                ("extendAttr");
//                                        if (mJsonExtendAttr != null && mJsonExtendAttr.length() >
//                                                0) {
//                                            ExtendAttr mExtendAttr = null;
//
//                                            for (int s = 0; s < mJsonExtendAttr.length(); s++) {
//
//                                                JSONObject mJsonExtendAttrJSONObject =
//                                                        mJsonExtendAttr.getJSONObject(s);
//
//                                                if (mJsonExtendAttrJSONObject != null) {
//                                                    mExtendAttr = new ExtendAttr();
//                                                    mExtendAttr.setDraw(mJsonExtendAttrJSONObject
//                                                            .optString("draw"));
//                                                    mExtendAttr.setGoal(mJsonExtendAttrJSONObject
//                                                            .optString("goal"));
//                                                    mExtendAttr.setLost(mJsonExtendAttrJSONObject
//                                                            .optString("lost"));
//                                                    mExtendAttr.setMatch(mJsonExtendAttrJSONObject
//                                                            .optString("match"));
//                                                    mExtendAttr.setOrder(mJsonExtendAttrJSONObject
//                                                            .optString("order"));
//                                                    mExtendAttr.setScore(mJsonExtendAttrJSONObject
//                                                            .optString("score"));
//                                                    mExtendAttr.setWin(mJsonExtendAttrJSONObject
//                                                            .optString("win"));
//                                                    mExtendAttrList.add(mExtendAttr);
//                                                } else {
//                                                    Log.e(TAG, "------搜索条件数据为空或解析失败-------");
//                                                }
//
//                                            }
//                                            mProgramInfo.setExtendAttr(mExtendAttrList);
//                                        }
//                                    }
////                                        mProgramInfo.setSearchConditions(mSearchConditionsList);
//
//                                    mProgramInfoList.add(mProgramInfo);
//                                } else {
//                                    Log.e(TAG, "-----子组件数据为空或解析失败--------");
//                                }
//
//                            }
//                            mModuleItem.setDatas(mProgramInfoList);
//                        } else {
//                            Log.e(TAG, "------------子组件数据为空或解析失败-------");
//                        }
//
//                        mModuleItemsList.add(mModuleItem);
//                    }
//
//                } else {
//                    Log.e(TAG, "-----组件数据为空或解析失败------");
//                }
//                mModuleInfoResult.setmDatas(mModuleItemsList);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "------解析失败-------" + e.toString());
//        }
//        return mModuleInfoResult;
    }
}
