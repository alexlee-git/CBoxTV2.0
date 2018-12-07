package com.newtv.libs.uc;

import android.text.TextUtils;

import com.newtv.libs.Libs;
import com.newtv.libs.util.SharePreferenceUtils;

public class UserStatus {
    public static final String SIGN_MEMBER_OPEN_NOT = "member_open_not";//未开通会员
    public static final String SIGN_MEMBER_OPEN_CLOSE = "member_open_lose";//已开通，但失效
    public static final String SIGN_MEMBER_OPEN_GOOD = "member_open_good";//已开通，有效
    private static boolean isLogin = false;
    private static String memberSatus;
    private static String userId;

    public static boolean isLogin() {
        return isLogin;
    }

    public static void setIsLogin(boolean isLogin) {
        UserStatus.isLogin = isLogin;
        if(isLogin){
            userId = SharePreferenceUtils.getUserId(Libs.get().getContext());
        } else {
            userId = "";
        }
    }

    public static String getUserId() {
        return userId;
    }

    public static String getMemberSatus() {
        return memberSatus;
    }

    public static void setMemberSatus(String memberSatus) {
        UserStatus.memberSatus = memberSatus;
    }

    public static boolean isVip(){
        if(isLogin() && !TextUtils.isEmpty(memberSatus) && TextUtils.equals(memberSatus,SIGN_MEMBER_OPEN_GOOD)){
            return true;
        }
        return false;
    }

}
