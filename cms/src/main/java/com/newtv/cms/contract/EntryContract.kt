package com.newtv.cms.contract

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.gridsum.tracker.GridsumWebDissector
import com.gridsum.videotracker.VideoTracker
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IBootGuide
import com.newtv.libs.BootGuide
import com.newtv.libs.Constant
import com.newtv.libs.Libs
import com.newtv.libs.util.CNTVLogUtils
import com.newtv.libs.util.SPrefUtils

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         13:26
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
class EntryContract {
    interface View : ICmsView {
        fun bootGuildResult()
    }

    interface Presenter : ICmsPresenter {
        fun initCNTVLog(application: Application)
    }

    class EntryPresenter(context: Context, view: View) : CmsServicePresenter<View>(context, view), Presenter {

        private var bootSerice: IBootGuide? = null

        init {
            bootSerice = getService<IBootGuide>(CmsServicePresenter.SERVICE_BOOT_GUIDE)
            getBootGuide()
        }

        // 央视网日志初始化
        override fun initCNTVLog(application: Application) {
            // TODO Auto-generated method stub
            // 央视网日志初始化
            val urls = arrayOf("http://wdrecv.app.cntvwb.cn/gs.gif")
            GridsumWebDissector.getInstance().setUrls(urls)
            GridsumWebDissector.getInstance().setApplication(application)
            Log.i(TAG, "---入口activity$application")
            val AppVersionName = CNTVLogUtils.getVersionName(application.applicationContext)
            Log.i(TAG, "---版本号" + AppVersionName!!)
            Log.i(TAG, "---渠道号" + Libs.get().channelId)
            GridsumWebDissector.getInstance().setAppVersion(AppVersionName)// 设置App版本号
            GridsumWebDissector.getInstance().setServiceId("GWD-005100")// 设置统计服务ID
            GridsumWebDissector.getInstance().setChannel(Libs.get().channelId)//
            // 设置来源渠道（不适用于多渠道打包）
            // 央视网日志： （传入设备型号，如：MI 2S）
            VideoTracker.setMfrs(android.os.Build.MODEL)
            // 央视网日志：（传入播放平台，如：Android）
            VideoTracker.setDevice("Android")
            // 央视网日志：（传入操作系统，如：Android_4.4.4）
            VideoTracker.setChip(android.os.Build.VERSION.RELEASE)
        }

        internal fun getBootGuide() {
            /** 测试用
            if (Libs.get().isDebug) {
            view?.bootGuildResult()
            return
            }
             */
            bootSerice?.let { boot ->
                val platform = Libs.get().appKey + Libs.get().channelId
                boot.getBootGuide(platform, object : DataObserver<String> {
                    override fun onResult(result: String, requestCode: Long) {
                        val cacheValue = SPrefUtils.getValue(context,
                                SPrefUtils.KEY_SERVER_ADDRESS, "") as String
                        if (!TextUtils.equals(cacheValue, result)) {
                            SPrefUtils.setValue(context, SPrefUtils
                                    .KEY_SERVER_ADDRESS, result)
                            Constant.parseServerAddress(result)
                            BootGuide.parse(result)
                        }
                        view?.bootGuildResult()
                    }

                    override fun onError(code: String?, desc: String?) {
                        view?.onError(context, code, desc)
                    }
                })
            }
        }

        companion object {

            private val TAG = "EntryPresenter"
        }
    }
}
