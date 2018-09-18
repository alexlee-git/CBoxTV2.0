package tv.newtv.cboxtv.utils;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tv.newtv.cboxtv.cms.util.CalendarUtil;
import tv.newtv.cboxtv.views.RecycleImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.util
 * 创建事件:         10:07
 * 创建人:           weihaichao
 * 创建日期:          2018/5/2
 */
public final class CmsLiveUtil {

    /**
     * 1：按日循环
     * 2：按周循环
     * 3：一次性直播
     * <p>
     * 直播参数 liveParam
     * 1、当liveLoopType为1时，该字段为空
     * 2、当liveLoopType为2时，该字段为星期值，内容格式：中间用竖线分割如（1|2）
     * 3、当liveLoopType为3时，该字段为日期，格式yyyy-mm-dd
     **/

    private static final int PLAY_TYPE_DAY_LOOP = 1;
    private static final int PLAY_TYPE_WEEK_LOOP = 2;
    private static final int PLAY_TYPE_NO_LOOP = 3;

    private CmsLiveUtil() {
    }

    public static int formatToSeconds(int hour, int min, int sec) {
        int result = 0;
        result += 3600 * hour;
        result += 60 * min;
        result += sec;
        return result;
    }

    public static int formatToSeconds(String timeFormat) {
        if (timeFormat == null) {
            return 0;
        }
        String[] times = timeFormat.split(":");
        int result = 0;
        for (int index = 0; index < 3; index++) {
            if (times.length >= index + 1) {
                String value = times[index];
                if (!TextUtils.isEmpty(value)) {
                    switch (index) {
                        case 0:
                            result += 3600 * Integer.parseInt(value);
                            break;
                        case 1:
                            result += 60 * Integer.parseInt(value);
                            break;
                        default:
                            result += Integer.parseInt(value);
                            break;
                    }
                }
            }
        }
        return result;
    }

    private static boolean isTodayInWeeks(String weeks) {
        String day = Integer.toString(CalendarUtil.getInstance().getWeek() - 1);
        return !TextUtils.isEmpty(weeks) && weeks.contains(day);
    }

    @SuppressWarnings("UnnecessaryBoxing")
    public static boolean isInTime(String startTime, String endTime){
        Long start = Long.valueOf(formatToSeconds(startTime));
        Long end = Long.valueOf(formatToSeconds(endTime));

        return isInTime(start,end);
    }

    private static boolean isInTime(Long timeStart, Long timeEnd) {
        long hour = CalendarUtil.getInstance().getHour();
        long minute = CalendarUtil.getInstance().getMinute();
        long seconds = CalendarUtil.getInstance().getSecond();
        long current = hour * 3600 + minute * 60 + seconds;
        return timeStart <= current && current < timeEnd;
    }

    private static boolean isToday(String format) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String current = df.format(new Date());
        return format.equals(current);
    }


    public static boolean isInPlay(String loopTypeStr, String param, String start, String end,
                                   final ViewGroup parent) {

        boolean show = false;

        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(end)) {

            long playStart = formatToSeconds(start);
            long playEnd = formatToSeconds(end);
            int loopType = 0;

            if (!TextUtils.isEmpty(loopTypeStr)) {
                loopType = Integer.parseInt(loopTypeStr);
            }
            switch (loopType) {
                case PLAY_TYPE_DAY_LOOP:
                    show = isInTime(playStart, playEnd);
                    break;
                case PLAY_TYPE_WEEK_LOOP:
                    show = isTodayInWeeks(param) && isInTime(playStart, playEnd);
                    break;
                case PLAY_TYPE_NO_LOOP:
                    show = isToday(param) && isInTime(playStart, playEnd);
                    break;
                default:
                    show = false;
                    break;
            }

            if (parent == null) {
                return show;
            }
            RecycleImageView targetView = getRecycleImage(parent);
            if (targetView != null) {
                targetView.setIsPlaying(show);
            }


        }
        return show;
    }

    private static RecycleImageView getRecycleImage(ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            if (childAt instanceof RecycleImageView) {
                return (RecycleImageView) childAt;
            } else if (childAt instanceof ViewGroup) {
                View resultView = getRecycleImage((ViewGroup) childAt);
                if (resultView != null) {
                    return (RecycleImageView) resultView;
                }
            }
        }
        return null;
    }
}
