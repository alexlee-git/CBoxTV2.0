package tv.newtv.cboxtv.uc.v2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午5:29
 * 创建人:           lixin
 * 创建日期:         2018/9/20
 */


public class TimeUtil {

    private long timeDiff;

    private static TimeUtil mInstance;
    private static final String TAG = "user2nd";

    public static TimeUtil getInstance() {
        if (mInstance == null) {
            synchronized (TimeUtil.class) {
                if (mInstance == null) {
                    mInstance = new TimeUtil();
                }
            }
        }
        return mInstance;
    }

    private TimeUtil() {

    }

    /**
     * 与时钟同步接口进行系统时间的同步
     */
    public void synchronizeTime() {
        NetClient.INSTANCE.getClockSyncApi().getClockData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        try {
                            String timeStr = new JSONObject(responseBody.string()).optString("response");
                            timeDiff = Long.parseLong(timeStr) - System.currentTimeMillis();
                            Log.d(TAG, "time diff : " + timeDiff);
                            Log.d(TAG, "server time : " + timeStr + ", sys.curtime : " + System.currentTimeMillis());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 获取与服务器时间校正后的系统时间
     *
     * @return
     */
    public long getCurrentTimeInMillis() {
        long sysTime = System.currentTimeMillis();
        Log.d(TAG, "sys.currTime: " + sysTime);
        Log.d(TAG, "maintaintime: " + (sysTime + timeDiff));
        return sysTime + timeDiff;
    }

    /**
     * 日期转换成毫秒数
     */
    public long getSecondsFromDate(String expireDate) {
        if (expireDate == null || expireDate.trim().equals(""))
            return 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(expireDate);
            return (long) (date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * 毫秒数转化为日期
     */
    public  String getDateFromSeconds(String seconds) {
        if (seconds == null)
            return " ";
        else {
            Date date = new Date();
            try {
                date.setTime(Long.parseLong(seconds));
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            return sdf.format(date);
        }
    }
}
