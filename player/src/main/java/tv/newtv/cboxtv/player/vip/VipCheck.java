package tv.newtv.cboxtv.player.vip;

import android.text.TextUtils;

import tv.newtv.cboxtv.player.ChkPlayResult;

public class VipCheck {

    private static boolean isVip(){
        return false;
    }

    private static boolean isBuy(String id){
        return false;
    }

    public static boolean isTrySee(ChkPlayResult result){
        if(result == null){
            return false;
        }
        return isTrySee(result.getVipFlag(),result.getContentUUID());
    }

    public static boolean isTrySee(String vipFlag,String programId){
        if(TextUtils.isEmpty(vipFlag)){
            return false;
        }

        if("0".equals(vipFlag)){
            return false;
        } else if("1".equals(vipFlag)){
            if(isVip()){
                return false;
            }
        } else if("2".equals(vipFlag)){
            if(isBuy(programId)){
                return false;
            }
        }
        return true;
    }
}
