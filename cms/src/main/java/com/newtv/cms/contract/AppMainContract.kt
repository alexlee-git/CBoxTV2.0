package com.newtv.cms.contract

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.newtv.cms.CmsServicePresenter
import com.newtv.cms.DataObserver
import com.newtv.cms.ICmsPresenter
import com.newtv.cms.ICmsView
import com.newtv.cms.api.IClock
import com.newtv.cms.bean.Time
import com.newtv.libs.Constant
import com.newtv.libs.ServerTime
import com.newtv.libs.util.LogUploadUtils
import com.newtv.libs.util.LogUtils

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         10:25
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
class AppMainContract {
    interface View : ICmsView {
        fun syncServerTime(result: Long?)
    }

    interface Presenter : ICmsPresenter {
        fun syncServiceTime()
        fun onResume()
    }

    class MainPresenter(context: Context, view: View?) : CmsServicePresenter<View>(context, view)
            , Presenter {

        override fun onResume() {
            if(clockService != null && ServerTime.get().isNeedSyncTime) {
                syncServiceTime()
            }
        }

        //广播显示系统时间
        private val mTimeRefreshReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (Intent.ACTION_TIME_TICK == intent.action) {
                    if (ServerTime.get().isNeedSyncTime) {
                        syncServiceTime()
                    } else {
                        view?.syncServerTime(ServerTime.currentTimeMillis())
                    }
                }
            }
        }

        private var clockService: IClock? = null

        init {
            clockService = getService<IClock>(CmsServicePresenter.SERVICE_CLOCK)

            initLogUpload(context)
            syncServiceTime()
            registTimeSync(context)
        }

        override fun destroy() {
            super.destroy()

            if (mTimeRefreshReceiver != null) {
                context.unregisterReceiver(mTimeRefreshReceiver)
            }
        }

        override fun syncServiceTime() {
            clockService?.sync(object : DataObserver<Time> {
                override fun onResult(result: Time, requestCode: Long) {
                    if ("1" == result.statusCode) {
                        ServerTime.get().setServerTime(result.response)
                        view?.syncServerTime(result.response)
                    } else {
                        view?.syncServerTime(null)
                    }
                }

                override fun onError(desc: String?) {
                    view?.syncServerTime(null)
                }
            })
        }

        private fun registTimeSync(context: Context) {
            context.registerReceiver(mTimeRefreshReceiver, IntentFilter(Intent
                    .ACTION_TIME_TICK))
        }

        private fun initLogUpload(context: Context) {
            try {
                val dataBuff = StringBuilder(Constant.BUFFER_SIZE_32)
                val pckInfo = context.packageManager.getPackageInfo(context
                        .packageName, 0)

                dataBuff.append("0,")
                        .append(pckInfo.versionName)
                        .trimToSize()
                LogUploadUtils.uploadLog(Constant.LOG_NODE_SWITCH, dataBuff.toString())//进入应用


                dataBuff.delete(0, dataBuff.length)
                dataBuff.append(Build.MANUFACTURER)
                        .append(",")
                        .append(Build.MODEL)
                        .append(",")
                        .append(Build.VERSION.RELEASE)
                        .trimToSize() // 设备信息
                LogUploadUtils.uploadLog(Constant.LOG_NODE_DEVICE_INFO, dataBuff.toString())

                dataBuff.delete(0, dataBuff.length)

                dataBuff.append(pckInfo.applicationInfo.loadLabel(context.packageManager))
                        .append(",")
                        .append(pckInfo.versionName)
                        .append(",")
                        .trimToSize() // 版本信息

                LogUploadUtils.uploadLog(Constant.LOG_NODE_APP_VERSION, dataBuff.toString())
            } catch (e: Exception) {
                LogUtils.e(e.toString())
            }

        }
    }
}
