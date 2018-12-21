package tv.newtv.cboxtv.menu;

import android.text.TextUtils;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.LogUploadUtils;

import tv.icntv.icntvplayersdk.Constants;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.player.vip.VipCheck;

public class MenuGroupLogUpload {

    public static void goneLogUpload(){
        uploadLog("15");
    }

    public static void showLogUpload(){
        uploadLog("6");
    }

    private static void uploadLog(String number){
        try {
            Content programSeriesInfo = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
            String definition = programSeriesInfo.getDefinition();
            if (!TextUtils.isEmpty(definition)) {
                if (TextUtils.equals(definition, "SD")) {
                    definition = "1";
                } else if (TextUtils.equals(definition, "HD")) {
                    definition = "0";
                }
            } else {
                definition = "1";
            }

            boolean isPay = VipCheck.isPay(ADConfig.getInstance().getVipFlag());
            String chargeType = isPay ? "1": "0";

            String content = number + "," + ADConfig.getInstance().getSeriesUUID() + ","
                    + ADConfig.getInstance().getProgramId() + "," + chargeType + "," + definition + ","
                    + ADConfig.getInstance().getIntMillisDuration() + "," + NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition() + "," + Constants.vodPlayId;
            LogUploadUtils.uploadLog(Constant.FLOATING_LAYER, content);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
