package tv.newtv.cboxtv.player.util;

import android.text.TextUtils;

import com.newtv.libs.util.PlayerTimeUtils;

import java.util.List;

import tv.newtv.cboxtv.menu.model.Program;

public class LbUtils {

    public static int binarySearch(List<Program> list,int defaultResult){
        try {
            int start = 0;
            if(TextUtils.isEmpty(list.get(0).getStartTime())){
                start = 1;
            }
            return binarySearch(list,start,list.size(),defaultResult);
        }catch (Exception e){ }
        return defaultResult;
    }

    public static int binarySearch(List<Program> list,int start,int end,int defaultResult) {
        long current = System.currentTimeMillis();
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
            long startTime = parse(list.get(i).getStartTime());
            long endTime = startTime + Integer.parseInt(list.get(i).getDuration()) *1000;
            if(startTime < current && current < endTime){
                return i;
            }
        }
        return defaultResult;
    }

    private static long parse(String time) {
        return PlayerTimeUtils.parseTime(time, "yyyy-MM-dd HH:mm:ss.S");
    }
}
