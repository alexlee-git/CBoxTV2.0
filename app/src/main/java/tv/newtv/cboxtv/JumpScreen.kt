package tv.newtv.cboxtv

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.text.TextUtils
import android.widget.Toast
import com.newtv.libs.Constant
import com.newtv.libs.util.LogUtils
import com.newtv.libs.util.NetworkManager
import tv.newtv.cboxtv.cms.details.*
import tv.newtv.cboxtv.cms.screenList.ScreenListActivity
import tv.newtv.cboxtv.cms.special.SpecialActivity
import java.util.*

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         10:55
 * 创建人:           weihaichao
 * 创建日期:          2018/11/15
 */

object JumpScreen {

    @JvmStatic
    fun jumpActivity(context: Context, contentId: String, actionType: String, contentType: String) {
        jumpPage(context, contentId, actionType, contentType)
    }

    @JvmStatic
    fun jumpDetailActivity(context: Context, contentId: String, contentType: String) {
        jumpPage(context, contentId, Constant.OPEN_DETAILS, contentType)
    }

    @JvmStatic
    fun jumpActivity(context: Context, contentId: String, actionType: String, contentType: String,
                     childUUID: String?) {
        jumpPage(context, contentId, actionType, contentType, childUUID = childUUID)
    }

    @JvmStatic
    private fun jumpPage(context: Context,
                         contentId: String,
                         actionType: String,
                         contentType: String,
                         actionUri: String? = "",
                         fromOuter: Boolean? = false,
                         isAdEntry: Boolean? = false,
                         focusParam: String? = "",
                         seriesSubUUID: String? = "",
                         childUUID: String? = ""
    ) {
        if (!NetworkManager.getInstance().isConnected) {
            Toast.makeText(context, R.string.net_error, Toast.LENGTH_SHORT).show()
            return
        }
        val jumpIntent: Intent? = makeIntent(context, actionType, contentType, contentId)
        jumpIntent?.let {
            it.putExtra(Constant.CONTENT_TYPE, contentType)
            it.putExtra(Constant.CONTENT_UUID, contentId)
            it.putExtra(Constant.PAGE_UUID, contentId)
            it.putExtra(Constant.ACTION_TYPE, actionType)
            it.putExtra(Constant.ACTION_URI, actionUri)
            it.putExtra(Constant.DEFAULT_UUID, seriesSubUUID)
            it.putExtra(Constant.FOCUSPARAM, focusParam)
            it.putExtra(Constant.CONTENT_CHILD_UUID, childUUID)
            it.putExtra(Constant.ACTION_FROM, fromOuter)
            it.putExtra(Constant.ACTION_AD_ENTRY, isAdEntry)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ActivityCompat.startActivity(context, it, null)
        }
    }


    @JvmStatic
    private fun makeIntent(context: Context,
                           actionType: String,
                           contentType: String,
                           contentUUID: String
    ): Intent? {
        var jumpIntent: Intent? = null
        try {
            LogUtils.i(Constant.TAG, "actionType : $actionType")
            LogUtils.i(Constant.TAG, "contentType : $contentType")
            LogUtils.i(Constant.TAG, "uuid:$contentUUID")
            if (TextUtils.isEmpty(actionType)) {
                Toast.makeText(LauncherApplication.AppContext, "ActionType值为空", Toast.LENGTH_SHORT)
                        .show()
                return null
            }
            if (TextUtils.isEmpty(contentType)) {
                Toast.makeText(LauncherApplication.AppContext, "ContentType值为空", Toast
                        .LENGTH_SHORT)
                        .show()
                return null
            }
            if (TextUtils.isEmpty(contentUUID)) {
                Toast.makeText(LauncherApplication.AppContext, "ContentUUID值为空", Toast
                        .LENGTH_SHORT)
                        .show()
                return null
            }
            if (Constant.OPEN_DETAILS.equals(actionType)) {
                if (Constant.CONTENTTYPE_PS.equals(contentType)) {
                    //TODO 打开节目集详情页
                    jumpIntent = Intent(context, ProgrameSeriesAndVarietyDetailActivity::class.java)
                } else if (Constant.CONTENTTYPE_CR.equals(contentType)
                        || Constant.CONTENTTYPE_FG.equals(contentType)) {
                    //TODO 打开人物详情页
                    jumpIntent = Intent(context, PersonsDetailsActivityNew::class.java)
                } else if (Constant.CONTENTTYPE_CL.equals(contentType)
                        || Constant.CONTENTTYPE_TV.equals(contentType)) {
                    //TODO 打开栏目详情页
                    jumpIntent = Intent(context, ColumnPageActivity::class.java)
                } else if (Constant.CONTENTTYPE_PG.equals(contentType)) {
                    //TODO 打开单节目详情页
                    jumpIntent = Intent(context, SingleDetailPageActivity::class.java)
                } else if (Constant.CONTENTTYPE_CP.equals(contentType)) {
                    //TODO 打开子节目
                    val bundle = Bundle()
                    bundle.putString(Constant.CONTENT_TYPE, contentType)
                    bundle.putString(Constant.CONTENT_UUID, contentUUID)
                    NewTVLauncherPlayerActivity.play(context, bundle)
                } else if (Constant.CONTENTTYPE_CG.equals(contentType)) {
                    //TODO 打开节目合集详情页
                    jumpIntent = Intent(context, ProgramCollectionActivity::class.java)
                } else if (Constant.CONTENTTYPE_CS.equals(contentType)) {
                    //TODO 打开节目集合集
                    Toast.makeText(context, "节目集合集正在开发中", Toast.LENGTH_SHORT).show()
                } else if (Constant.CONTENTTYPE_LB.equals(contentType)) {
                    //TODO 打开轮播详情页
                    if(Constant.canUseAlternate) {
                        jumpIntent = Intent(context, AlternateActivity::class.java)
                    }else{
                        Toast.makeText(context, "轮播正在开发中", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "$actionType:$contentType", Toast.LENGTH_SHORT)
                            .show()
                }
            } else if (Constant.OPEN_LINK.equals(actionType)) {
                //TODO 打开链接(not supported)
                Toast.makeText(context, R.string.no_link, Toast.LENGTH_LONG)
                        .show()
            } else if (Constant.OPEN_USERCENTER.equals(actionType)) {
                //TODO 打开用户中心(not supported)
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show()
            } else if (Constant.OPEN_APP_LIST.equals(actionType)) {
                //TODO 打开我的应用(not supported)
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show()
            } else if (Constant.OPEN_SPECIAL.equals(actionType)) {
                //TODO 打开专题页
                jumpIntent = Intent(context, SpecialActivity::class.java)
            } else if (Constant.OPEN_APK.equals(actionType)) {
                //TODO 打开apk(not supported)
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show()
            } else if (Constant.OPEN_PAGE.equals(actionType)) {
                //TODO 打开apk(not supported)
                Toast.makeText(context, R.string.not_support_direct_type, Toast.LENGTH_LONG)
                        .show()
            } else if (Constant.OPEN_VIDEO.equals(actionType)) {
                //TODO 打开视频
                val bundle = Bundle()
                bundle.putString(Constant.CONTENT_TYPE, contentType)
                bundle.putString(Constant.CONTENT_UUID, contentUUID)
                NewTVLauncherPlayerActivity.play(context, bundle)

            }
        } catch (e: Exception) {
            LogUtils.e(e)
        } finally {
            jumpIntent?.let { intent ->
                intent.putExtra(Constant.ACTION_TYPE, actionType)
                intent.putExtra(Constant.CONTENT_TYPE, contentType)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
        return jumpIntent
    }

    @JvmStatic
    fun jumpExternal(context: Context, action: String, param: String): Boolean {
        if (TextUtils.isEmpty(action)) return false
        val index = action.indexOf("|")
        val actionType = action.substring(0, index)
        val contentType = action.substring(index + 1, action.length)
        val params: HashMap<String, String> = parseParamMap(param)
        val jumpIntent = makeIntent(context, actionType, contentType,
                getParamValue(params, Constant.EXTERNAL_PARAM_CONTENT_UUID))
        jumpIntent?.let {
            it.putExtra(Constant.CONTENT_TYPE, contentType)
            it.putExtra(Constant.CONTENT_UUID, getParamValue(params,
                    Constant.EXTERNAL_PARAM_CONTENT_UUID))
            it.putExtra(Constant.PAGE_UUID, getParamValue(params,
                    Constant.EXTERNAL_PARAM_CONTENT_UUID))
            it.putExtra(Constant.ACTION_TYPE, actionType)
            it.putExtra(Constant.ACTION_URI, getParamValue(params,
                    Constant.EXTERNAL_PARAM_ACTION_URI))
            it.putExtra(Constant.DEFAULT_UUID, getParamValue(params,
                    Constant.EXTERNAL_PARAM_FOCUS_UUID))
            it.putExtra(Constant.ACTION_FROM, true)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ActivityCompat.startActivity(context, it, null)
            return true
        }
        return false
    }

    private fun parseParamMap(paramStr: String): HashMap<String, String> {
        val paramsMap = HashMap<String, String>()
        if (TextUtils.isEmpty(paramStr)) return paramsMap
        val params = paramStr.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (param in params) {
            val values = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            paramsMap[values[0]] = if (values.size > 1) values[1] else ""
        }
        return paramsMap
    }

    private fun getParamValue(hashMap: HashMap<String, String>?, key: String): String {
        if (hashMap == null) return ""
        return if (!hashMap.containsKey(key)) "" else hashMap.get(key)!!
    }
}
