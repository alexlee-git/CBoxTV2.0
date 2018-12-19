package tv.newtv.cboxtv.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;

public class SpannableBuilderUtils {

    //RecentMsg Builder
    public static SpannableStringBuilder builderMsg(String msg){
        SpannableStringBuilder mStrMsg = new SpannableStringBuilder(msg);
        if (msg.length() > 4 && msg.contains
                (LauncherApplication.AppContext.getResources().getString(R.string.user_poster_program_update_title_left_being))) {
            mStrMsg.setSpan(new ForegroundColorSpan(LauncherApplication.AppContext.getResources().getColor(R.color.colorWhite)), 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mStrMsg.setSpan(new ForegroundColorSpan(Color.parseColor("#FFF5A623")), 3, msg.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mStrMsg.setSpan(new ForegroundColorSpan(LauncherApplication.AppContext.getResources().getColor(R.color.colorWhite)), msg.length() - 1, msg.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        } else {
            mStrMsg.setSpan(new ForegroundColorSpan(Color.parseColor("#FFF5A623")), 0, msg.length() - 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mStrMsg.setSpan(new ForegroundColorSpan(LauncherApplication.AppContext.getResources().getColor(R.color.colorWhite)), msg.length() - 2, msg.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return mStrMsg;
    }

    //RecentNum Builder
    public static SpannableStringBuilder builderNum(String num){
        SpannableStringBuilder mStrNum = new SpannableStringBuilder(LauncherApplication.AppContext.getResources().getString(R.string.user_poster_program_update_title_left_being)
                + num + LauncherApplication.AppContext.getResources().getString(R.string.user_tag_poster_program_update_title_right));
        if (!TextUtils.isEmpty(num) && !TextUtils.equals(num,"1")) {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FFF5A623"));
            mStrNum.setSpan(colorSpan, 3, mStrNum.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }else if(TextUtils.equals(num,"1")){
            return new SpannableStringBuilder(num);
        }
        return mStrNum;
    }
}
