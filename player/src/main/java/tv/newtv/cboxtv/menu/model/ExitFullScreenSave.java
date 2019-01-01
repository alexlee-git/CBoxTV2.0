package tv.newtv.cboxtv.menu.model;

import android.text.TextUtils;

public class ExitFullScreenSave {
    public String contentId;
    public boolean isLive;
    public boolean isAlternate;

    public boolean equals(String contentId,boolean isLive,boolean isAlternate){
        return TextUtils.equals(this.contentId,contentId) && this.isLive == isLive
                && this.isAlternate == isAlternate;
    }

    public void reset(){
        contentId = "";
        isLive = false;
        isAlternate = false;
    }
}
