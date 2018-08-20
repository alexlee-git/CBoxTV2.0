package tv.newtv.cboxtv.player.model;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tv.newtv.cboxtv.cms.util.CalendarUtil;
import tv.newtv.cboxtv.utils.CmsLiveUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.player.model
 * 创建事件:         13:56
 * 创建人:           weihaichao
 * 创建日期:          2018/5/2
 */
public class LiveInfo {
    private int StartTime;  //毫秒
    private int EndTime;    //毫秒
    private int PlayLength; //毫秒
    private int delay = 0;
    private int currentPosition = 0;
    private String mLiveUrl;
    private String key;
    private SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    private String contentUUID;
    /**
     * 是否时移 1为是，其他为否
     */
    private String isTimeShift;

    public String getIsTimeShift() {
        return isTimeShift;
    }

    public void setIsTimeShift(String isTimeShift) {
        this.isTimeShift = isTimeShift;
    }

    public boolean isTimeShift() {
        return "1".equals(isTimeShift);
    }

    public String getContentUUID() {
        return contentUUID;
    }

    public void setContentUUID(String contentUUID) {
        this.contentUUID = contentUUID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLiveUrl(String url) {
        mLiveUrl = url;
    }

    public String getmLiveUrl() {
        return mLiveUrl;
    }

    public boolean isEqualsUrl(String url) {
        return !TextUtils.isEmpty(mLiveUrl) || !mLiveUrl.equals(url);
    }

    private int getEndTime() {
        return EndTime;
    }

    public int getCurrentRealPosition() {
        int hour = CalendarUtil.getInstance().getHour();
        int min = CalendarUtil.getInstance().getMinute();
        int sec = CalendarUtil.getInstance().getSecond();
        int current = CmsLiveUtil.formatToSeconds(hour, min, sec) * 1000;
        return current - getStartTime();
    }

    public int getCurrentPosition() {
        int hour = CalendarUtil.getInstance().getHour();
        int min = CalendarUtil.getInstance().getMinute();
        int sec = CalendarUtil.getInstance().getSecond();
        int current = CmsLiveUtil.formatToSeconds(hour, min, sec) * 1000;
        return current - getStartTime() + getDelay();
    }

    public int getTimeDelay() {
        if (this.delay > 0) {
            this.delay = 0;
        }

        return delay;
    }

    public String getDefaultLiveUrl() {
        if (mLiveUrl != null && mLiveUrl.contains("beginTime")) {
            return mLiveUrl.substring(0, mLiveUrl.indexOf("?beginTime"));
        }
        return mLiveUrl;
    }

    public String setTimeDelay(int delayTime) {
        String defaultUrl = getDefaultLiveUrl();
        if (delayTime == 0) {
            this.delay = 0;
            return defaultUrl;
        }
        if (Math.abs(delayTime) < 20000) {
            return defaultUrl;
        }
        this.delay += delayTime;
        if (this.delay > 0) {
            this.delay = 0;
            return defaultUrl;
        }
        long milseconds = System.currentTimeMillis() + delay - 8 * 3600 * 1000;
        return defaultUrl + "?beginTime=" + time.format(new Date(milseconds));
    }

    private int getDelay() {
        return delay;
    }

    public int getPlayLength() {
        return PlayLength;
    }

    private int getStartTime() {
        return StartTime;
    }

    public void setPlayTimeInfo(int start, int end) {
        StartTime = start * 1000;
        EndTime = end * 1000;
        PlayLength = EndTime - StartTime;

        mLiveUrl = setTimeDelay(getCurrentRealPosition() * -1);
    }
}