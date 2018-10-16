package tv.newtv.cboxtv.cms.util;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import tv.newtv.cboxtv.player.util.PlayInfoUtil;

import java.util.HashMap;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.ColumnPageActivity;
import tv.newtv.cboxtv.cms.details.PersonsDetailsActivity;
import tv.newtv.cboxtv.cms.details.ProgramCollectionActivity;
import tv.newtv.cboxtv.cms.details.ProgrameSeriesAndVarietyDetailActivity;
import tv.newtv.cboxtv.cms.details.SingleDetailPageActivity;
import tv.newtv.cboxtv.cms.listPage.ListPageActivity;
import tv.newtv.cboxtv.cms.mainPage.menu.MainNavManager;
import tv.newtv.cboxtv.cms.special.SpecialActivity;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;

public class JumpUtil {

    private static HashMap<String, String> parseParamMap(String paramStr) {
        HashMap<String, String> paramsMap = new HashMap<>();
        if (TextUtils.isEmpty(paramStr)) return paramsMap;
        String[] params = paramStr.split("&");
        for (String param : params) {
            String[] values = param.split("=");
            paramsMap.put(values[0], values.length > 1 ? values[1] : "");
        }
        return paramsMap;
    }

    private static String getParamValue(HashMap<String, String> hashMap, String key) {
        if (hashMap == null) return "";
        if (!hashMap.containsKey(key)) return "";
        return hashMap.get(key);
    }

    public static boolean parseExternalJump(Context context,String action,String params){
        if(TextUtils.isEmpty(action)) return false;
        int index = action.indexOf("|");
        String actionType = action.substring(0, index);
        String contentType = action.substring(index + 1, action.length());
        JumpUtil.activityJump(context, actionType, contentType,
                parseParamMap(params), true);
        Log.e("Splash", "SplashActivity---> onCreate 接收到外部应用跳转需求, action : "
                + action + " param : " + params);
        return true;
    }

    public static void activityJump(Context context, Program info) {
        // fix bug LETVYSYY-51
        if (!NetworkManager.getInstance().isConnected()) {
            Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent jumpIntent = getIntent(context, info.getL_actionType(), info.getL_contentType(), info
                .getL_uuid(), info.getSeriesSubUUID());
        if (jumpIntent != null) {
            jumpIntent.putExtra(Constant.CONTENT_TYPE, info.getL_contentType());
            jumpIntent.putExtra(Constant.CONTENT_UUID, info.getL_uuid());
            jumpIntent.putExtra(Constant.PAGE_UUID, info.getContentId());
            jumpIntent.putExtra(Constant.ACTION_TYPE, info.getL_actionType());
            jumpIntent.putExtra(Constant.ACTION_URI, info.getL_actionUri());
            jumpIntent.putExtra(Constant.DEFAULT_UUID, info.getL_focusId());
            jumpIntent.putExtra(Constant.FOCUSPARAM, info.getL_focusParam());

            jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, jumpIntent, null);
        }
    }

    public static void activityJump(Context context, String actionType, String contentType,
                                    String contentUUID, String actionUri) {
        activityJump(context, actionType, contentType, contentUUID, actionUri, "");
    }

    public static void activityJump(Context context, String actionType, String contentType,
                                    HashMap<String, String> params, boolean fromOuter) {
        Intent jumpIntent = getIntent(context, actionType, contentType, getParamValue
                (params,
                Constant.EXTERNAL_PARAM_CONTENT_UUID), getParamValue(params, Constant
                .EXTERNAL_PARAM_SERIES_SUB_UUID));
        if (jumpIntent != null) {
            jumpIntent.putExtra(Constant.CONTENT_TYPE, contentType);
            jumpIntent.putExtra(Constant.CONTENT_UUID, getParamValue(params,
                    Constant.EXTERNAL_PARAM_CONTENT_UUID));
            jumpIntent.putExtra(Constant.PAGE_UUID, getParamValue(params,
                    Constant.EXTERNAL_PARAM_CONTENT_UUID));
            jumpIntent.putExtra(Constant.ACTION_TYPE, actionType);
            jumpIntent.putExtra(Constant.ACTION_URI, getParamValue(params,
                    Constant.EXTERNAL_PARAM_ACTION_URI));
            jumpIntent.putExtra(Constant.DEFAULT_UUID, getParamValue(params,
                    Constant.EXTERNAL_PARAM_FOCUS_UUID));
            jumpIntent.putExtra(Constant.ACTION_FROM, fromOuter);
//            jumpIntent.putExtra(Constant.FOCUSPARAM, info.getFocusParam());

            jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, jumpIntent, null);
        }
    }


    public static void activityJump(Context context, String actionType, String contentType,
                                    String contentUUID, String actionUri, boolean fromOuter) {
        activityJump(context, actionType, contentType, contentUUID, actionUri, "", fromOuter);
    }

    public static void activityJump(Context context, String actionType, String contentType,
                                    String contentUUID, String actionUri, String seriesSubUUID) {
        activityJump(context, actionType, contentType, contentUUID, actionUri, seriesSubUUID,
                false);
    }

    public static void activityJump(Context context, String actionType, String contentType,
                                    String contentUUID, String actionUri, String seriesSubUUID,
                                    boolean fromOuter) {
        // fix bug LETVYSYY-51
        if (!NetworkManager.getInstance().isConnected()) {
            Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent jumpIntent = getIntent(context, actionType, contentType, contentUUID, seriesSubUUID);
        if (jumpIntent != null) {
            jumpIntent.putExtra(Constant.CONTENT_TYPE, contentType);
            jumpIntent.putExtra(Constant.CONTENT_UUID, contentUUID);
            jumpIntent.putExtra(Constant.PAGE_UUID, contentUUID);
            jumpIntent.putExtra(Constant.ACTION_TYPE, actionType);
            jumpIntent.putExtra(Constant.ACTION_URI, actionUri);
            jumpIntent.putExtra(Constant.ACTION_FROM, fromOuter);
            jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, jumpIntent, null);
        }
    }

    private static Intent getIntent(final Context context, String actionType, String contentType,
                                    final String contentUUID, String seriesSubUUID) {
        Intent jumpIntent = null;
        try {
            LogUtils.i(Constant.TAG, "actionType : " + actionType);
            LogUtils.i(Constant.TAG, "contentType : " + contentType);
            LogUtils.i(Constant.TAG, "uuid:" + contentUUID);
            if (TextUtils.isEmpty(actionType)) {
                Toast.makeText(LauncherApplication.AppContext, "ActionType值为空", Toast.LENGTH_SHORT)
                        .show();
                return null;
            }
            if (TextUtils.isEmpty(contentType)) {
                Toast.makeText(LauncherApplication.AppContext, "ContentType值为空", Toast
                        .LENGTH_SHORT)
                        .show();
                return null;
            }
            if (TextUtils.isEmpty(contentUUID)) {
                Toast.makeText(LauncherApplication.AppContext, "ContentUUID值为空", Toast
                        .LENGTH_SHORT)
                        .show();
                return null;
            }
            if (Constant.OPEN_DETAILS.equals(actionType)) { // 打开详情
                if (Constant.CONTENTTYPE_PS.equals(contentType)) { // 节目集
                    jumpIntent = new Intent(context, ProgrameSeriesAndVarietyDetailActivity.class);
                } else if (Constant.CONTENTTYPE_CR.equals(contentType)
                        || Constant.CONTENTTYPE_FG.equals(contentType)) {   //人物
                    jumpIntent = new Intent(context, PersonsDetailsActivity.class);
                } else if (Constant.CONTENTTYPE_CL.equals(contentType)
                        || Constant.CONTENTTYPE_TV.equals(contentType)) {  //栏目
                    jumpIntent = new Intent(context, ColumnPageActivity.class);
                } else if (Constant.CONTENTTYPE_PG.equals(contentType)) {  //单节目
                    jumpIntent = new Intent(context, SingleDetailPageActivity.class);
                } else if (Constant.CONTENTTYPE_CP.equals(contentType)) {  // 子节目
                    if (TextUtils.isEmpty(seriesSubUUID)) {
                        openCPVideo(context, contentUUID);
                    } else {
                        openCPVideo(context, contentUUID, seriesSubUUID);
                    }
                } else if (Constant.CONTENTTYPE_CG.equals(contentType)) {
//                    jumpIntent = new Intent(context, ProgramListDetailActiviy.class);
                    jumpIntent = new Intent(context, ProgramCollectionActivity.class);
                } else if (Constant.CONTENTTYPE_CS.equals(contentType)) {  //节目集合集
                    Toast.makeText(context, "节目集合集正在开发中", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, actionType + ":" + contentType, Toast.LENGTH_SHORT)
                            .show();
                }
            } else if (Constant.OPEN_LISTPAGE.equals(actionType)) { // 打开列表页
                jumpIntent = new Intent(context, ListPageActivity.class);
            } else if (Constant.OPEN_LINK.equals(actionType)) { // 打开链接
                Toast.makeText(context, R.string.no_link, Toast.LENGTH_LONG)
                        .show();
            } else if (Constant.OPEN_USERCENTER.equals(actionType)) { // 打开用户中心
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show();
            } else if (Constant.OPEN_APP_LIST.equals(actionType)) { // 打开我的应用
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show();
            } else if (Constant.OPEN_SPECIAL.equals(actionType)) { // 打开专题页
                jumpIntent = new Intent(context, SpecialActivity.class);
            } else if (Constant.OPEN_APK.equals(actionType)) { // 打开apk
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show();
            } else if (Constant.OPEN_PAGE.equals(actionType)) { // 打开apk
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show();
            } else if (Constant.OPEN_VIDEO.equals(actionType)) { //打开视频
                // TODO 后面需要直接播放视频

                PlayInfoUtil.getPlayInfo(contentUUID, new PlayInfoUtil.ProgramSeriesInfoCallback() {
                    @Override
                    public void onResult(Content info) {
                        if (info == null) {
                            return;
                        }
                        Context tmpContext = context;
                        if (MainNavManager.getInstance().getCurrentFragment() != null) {
                            tmpContext = MainNavManager.getInstance().getCurrentFragment()
                                    .getActivity();
                        }
                        NewTVLauncherPlayerViewManager.getInstance().playProgramSingle(tmpContext,
                                info, 0, true);
                    }
                });

            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            if (jumpIntent != null) {
                jumpIntent.putExtra("action_type", actionType);
                jumpIntent.putExtra("content_type", contentType);
                jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        return jumpIntent;
    }

    public static void detailsJumpActivity(Context context, String contentType,
                                           String contentUUID) {
        detailsJumpActivity(context, contentType, contentUUID, "");
    }

    //跳转详情页
    public static void detailsJumpActivity(Context context, String contentType,
                                           String contentUUID, String seriesSubUUID) {
        // fix bug LETVYSYY-51
        if (!NetworkManager.getInstance().isConnected()) {
            Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent jumpIntent = null;
        if (Constant.CONTENTTYPE_PS.equals(contentType)) { // 节目集
            jumpIntent = new Intent(context, ProgrameSeriesAndVarietyDetailActivity.class);
        } else if (Constant.CONTENTTYPE_CR.equals(contentType)
                || Constant.CONTENTTYPE_FG.equals(contentType)) {   //人物
            jumpIntent = new Intent(context, PersonsDetailsActivity.class);
        } else if (Constant.CONTENTTYPE_CL.equals(contentType)
                || Constant.CONTENTTYPE_TV.equals(contentType)) {  //栏目
            jumpIntent = new Intent(context, ColumnPageActivity.class);
        } else if (Constant.CONTENTTYPE_PG.equals(contentType)) {  //单节目
            jumpIntent = new Intent(context, SingleDetailPageActivity.class);
        } else if (Constant.CONTENTTYPE_CP.equals(contentType)) {  // 子节目
            if (TextUtils.isEmpty(seriesSubUUID)) {
                openCPVideo(context, contentUUID);
            } else {
                openCPVideo(context, contentUUID, seriesSubUUID);
            }
        } else if (Constant.CONTENTTYPE_CG.equals(contentType)) {
//            jumpIntent = new Intent(context, ProgramListDetailActiviy.class);
            jumpIntent = new Intent(context, ProgramCollectionActivity.class);
        } else if (Constant.CONTENTTYPE_CS.equals(contentType)) {  //节目集合集
            Toast.makeText(context, "节目集合集正在开发中", Toast.LENGTH_SHORT).show();
        } else {
        }

        if (jumpIntent != null) {
            jumpIntent.putExtra("content_type", contentType);
            jumpIntent.putExtra("content_uuid", contentUUID);
            jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityCompat.startActivity(context, jumpIntent, null);
        }
    }

    /**
     * 如果推荐位上是子节目，则直接播放
     *
     * @param context
     * @param contentUUID
     */
    private static void openCPVideo(final Context context, final String contentUUID) {
        PlayInfoUtil.getPlayInfo(contentUUID, new PlayInfoUtil.ProgramSeriesInfoCallback() {
            @Override
            public void onResult(Content info) {
//                if (info == null || TextUtils.isEmpty(info.getProgramSeriesUUIDs())) {
//                    Toast.makeText(LauncherApplication.AppContext, "子节目缺少节目集ID",
//                            Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Context tmpContext = getMainContext(context);
                NewTVLauncherPlayerViewManager.getInstance().playProgramSingle(
                        tmpContext, info, 0, true);
            }
        });
    }

    /**
     * 如果推荐位上是子节目，那么就获取子节目属于的节目集信息
     * 并播放节目集中对应的集数
     */
    private static void openCPVideo(final Context context, final String contentUUID, String
            seriesSubUUID) {
        PlayInfoUtil.getPlayInfo(seriesSubUUID, new PlayInfoUtil.ProgramSeriesInfoCallback() {
            @Override
            public void onResult(Content info) {
                if (info != null && info.getData() != null && info.getData().size() > 0) {
                    //获取在节目集中的集数
                    int index = 0;
                    for (int i = 0; i < info.getData().size(); i++) {
                        SubContent programsInfo = info.getData().get(i);
                        if (contentUUID.equals(programsInfo.getContentUUID())) {
                            index = i;
                            break;
                        }
                    }

                    //播放
                    Context tmpContext = getMainContext(context);
                    NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(
                            tmpContext, info, true, index, 0);
                } else {
                    Toast.makeText(context, "获取节目集信息有误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static Context getMainContext(Context context) {
        Context tmpContext = context;
        if (MainNavManager.getInstance().getCurrentFragment() != null) {
            tmpContext = MainNavManager.getInstance().getCurrentFragment().getActivity();
        }
        return tmpContext;
    }
}
