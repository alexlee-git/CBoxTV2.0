package tv.newtv.cboxtv.cms.util;

import android.content.Context;


public class ImageUtils {

    public static int getProperPlaceHolderResId(Context context, int width, int height) {
        int imageWidth = DisplayUtils.reTranslate(width, 0);
        int imageHeight = DisplayUtils.reTranslate(height, 1);
        String name = "focus_" + imageWidth + "_" + imageHeight;
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}
