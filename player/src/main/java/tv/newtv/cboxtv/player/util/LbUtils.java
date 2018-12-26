package tv.newtv.cboxtv.player.util;

import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.util.PlayerTimeUtils;

import java.util.List;

import tv.newtv.cboxtv.menu.model.Program;

public class LbUtils {

    public static int binarySearch(List<Program> list, int defaultResult) {
        long current = System.currentTimeMillis();
        int start = 0;
        int end = list.size();
        while ((end - start) > 10) {
            int mid = (end + start) / 2;
            long midValue = parse(list.get(mid).getStartTime());
            if (midValue > current) {
                end = mid;
            } else {
                start = mid;
            }
        }

        for (int i = start; i < end; i++) {

            String starttime = list.get(i).getStartTime();
            String duration = list.get(i).getDuration();
            Log.i("LbUtils", "binarySearch: starttime: " + starttime);
            Log.i("LbUtils", "binarySearch: duration: " + duration);
            long startTime = 0;
            long endTime = 0;
            if (!TextUtils.isEmpty(starttime)) {
                startTime = parse(starttime);
            }
            if (!TextUtils.isEmpty(duration)) {
                endTime = startTime + Integer.parseInt(list.get(i).getDuration()) * 1000;
            }
            if (startTime < current && current < endTime) {
                return i;
            }
        }
        return defaultResult;
    }

    private static long parse(String time) {
        return PlayerTimeUtils.parseTime(time, "yyyy-MM-dd HH:mm:ss.S");
    }
}
