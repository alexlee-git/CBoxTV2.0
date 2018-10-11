package com.newtv.libs.util;

import android.view.KeyEvent;

import com.newtv.libs.Libs;

import java.lang.reflect.Field;


public class XunMaKeyUtils {

    public static void key(KeyEvent keyEvent){
        if(Libs.get().getFlavor().equals(DeviceUtil.XUN_MA) && keyEvent.getKeyCode() == KeyEvent
                .KEYCODE_ESCAPE){
            try {
                Field mKeyCode = keyEvent.getClass().getDeclaredField("mKeyCode");
                mKeyCode.setAccessible(true);
                mKeyCode.set(keyEvent,KeyEvent.KEYCODE_BACK);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
