package tv.newtv.cboxtv.player.model;

import android.text.TextUtils;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.LiveParam;
import com.newtv.cms.bean.Video;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.util.CalendarUtil;
import com.newtv.libs.util.CmsLiveUtil;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.player.model
 * 创建事件:         13:56
 * 创建人:           weihaichao
 * 创建日期:          2018/5/2
 */
public class LiveInfo {
    private LiveParam mLiveParam;      //直播循环参数
    private int delay = 0;

    private Date startDate;     //开始时间
    private Date endDate;       //结束时间
    private String mTitle;      //
    private String mLiveUrl;    //
    private String key;         //

    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
            .getDefault());

    private SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
    private String contentUUID;
    /**
     * 是否时移 1为是，其他为否
     */
    private String isTimeShift;

    public LiveInfo() {

    }

    public LiveInfo(@Nullable Content content) {
        if (content == null) return;
        mTitle = content.getTitle();
        contentUUID = content.getContentID();
        mLiveParam = CmsUtil.isLiveTime(content.getLiveParam());
        if (mLiveParam == null) return;
        parseLiveParam();
    }

    public LiveInfo(String title, @Nullable Video video) {
        if (video == null) return;
        if (TextUtils.isEmpty(video.getLiveUrl())) return;
        if (TextUtils.isEmpty(video.getContentId())) return;
        mTitle = title;
        setLiveUrl(video.getLiveUrl());
        setContentUUID(video.getContentId());

        mLiveParam = CmsUtil.isLive(video);
        if (mLiveParam == null) return;

        parseLiveParam();

    }

    private void parseLiveParam() {
        try {
            String startTime = mLiveParam.getPlayStartTime();
            String endTime = mLiveParam.getPlayEndTime();

            if (!TextUtils.isEmpty(mLiveParam.getLiveParam())) {
                /*
                 * 不带日期 如：18:00:00  日期默认为当天
                 *
                 * 因为不带年月日，所以开始结束时间全部转换为秒，如果结束时间秒数小于开始时间秒数的时候，即为跨天
                 *
                 *     如： start =  "22:00:00"  ->   79200 秒
                 *          end  =  "01:00:00" ->    3600  秒  结束时间小于开始时间 跨天
                 *
                 *     如：  start = "22:00:00"  ->  79200 秒
                 *           end  =  "23:30:00" ->   84600 秒  结束时间大于开始时间 当天
                 *
                 */
                int start = CmsLiveUtil.formatToSeconds(startTime);
                int end = CmsLiveUtil.formatToSeconds(endTime);
                String today = dayFormat.format(new Date());
                startDate = timeFormat.parse(String.format("%s %s", today, startTime));
                if (end < start) {
                    //跨天
                    Date temp = timeFormat.parse(String.format("%s %s", today, endTime));
                    endDate = new Date(temp.getTime() + 24 * 3600 * 1000);
                } else {
                    //当天
                    endDate = timeFormat.parse(String.format("%s %s", today, endTime));
                }
            } else {
                //带日期  如：2018-11-1 18:00:00
                startDate = timeFormat.parse(startTime);
                endDate = timeFormat.parse(endTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mLiveParam = null;
        }
    }

    @Override
    public String toString() {
        return String.format("[LiveInfo startTime=%s endTime=%s current=%s]", getStartTimeStr(),
                getEndTimeStr(), getCurrentTimeStr());
    }

    public String getTitle() {
        return mTitle;
    }

    public String getCurrentTimeStr() {
        return mTimeFormat.format(Calendar.getInstance().getTime());
    }

    public String getStartTimeStr() {
        return mTimeFormat.format(startDate);
    }

    public String getEndTimeStr() {
        return mTimeFormat.format(endDate);
    }

    public boolean isLiveTime() {
        return mLiveParam != null;
    }

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

    public String getLiveUrl() {
        return mLiveUrl;
    }

    public void setLiveUrl(String url) {
        mLiveUrl = url;
    }

    public boolean isEqualsUrl(String url) {
        return !TextUtils.isEmpty(mLiveUrl) || !mLiveUrl.equals(url);
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

    public boolean isComplete() {
        if (endDate == null) return true;
        return Calendar.getInstance().getTime().after(endDate);
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
        return 0;
    }

    private int getStartTime() {
        return 0;
    }

}