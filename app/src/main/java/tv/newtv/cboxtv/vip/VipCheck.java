package tv.newtv.cboxtv.vip;

import android.text.TextUtils;

public class VipCheck {

    private static boolean isVip(){
        return false;
    }

    private static boolean isBuy(String id){
        return false;
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
