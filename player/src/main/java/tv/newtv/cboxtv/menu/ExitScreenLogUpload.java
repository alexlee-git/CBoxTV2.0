package tv.newtv.cboxtv.menu;

import android.text.TextUtils;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.LogUploadUtils;

import tv.icntv.icntvplayersdk.Constants;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.player.vip.VipCheck;

public class ExitScreenLogUpload {

    public static void fullScreenLogUpload(String definition,String vipFlag){
        uploadLog(definition,vipFlag,"1");
    }

    public static void exitScreenLogUpload(String definition,String vipFlag){
        uploadLog(definition,vipFlag,"0");
    }

    private static void uploadLog(String definition,String vipFlag,String isFullScreen){
        try {
            if (!TextUtils.isEmpty(definition)) {
                if (TextUtils.equals(definition, "SD")) {
                    definition = "1";
                } else if (TextUtils.equals(definition, "HD")) {//高清
                    definition = "0";
                }
            } else {
                definition = "1";
            }
            if (!TextUtils.isEmpty(vipFlag)){
                if (vipFlag.equals("1")||vipFlag.equals("3")||vipFlag.equals("4")){
                    vipFlag = "1";
                }
            }

            LogUploadUtils.uploadLog(Constant.FLOATING_LAYER, "17,"
                    +ADConfig.getInstance().getSeriesID() + ","
                    + ADConfig.getInstance().getProgramId()
                    + "," + vipFlag + ","+ definition
                    + ","+ADConfig.getInstance().getIntMillisDuration()
                    + ","+ NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition() + ","+isFullScreen + ","
                    + Constants.vodPlayId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
