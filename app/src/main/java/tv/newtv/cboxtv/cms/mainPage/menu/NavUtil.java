package tv.newtv.cboxtv.cms.mainPage.menu;

import android.support.v4.app.Fragment;

/**
 * Created by oldcwj@gmail.com on 2018/3/19.
 */

public class NavUtil {

    private static NavUtil navUtil;

    public Fragment navFragment;

    private NavUtil () {}

    public static NavUtil getNavUtil() {
        if (navUtil == null) {
            synchronized (NavUtil.class) {
                if (navUtil == null) {
                    navUtil = new NavUtil();
                }
            }
        }

        return navUtil;
    }


}
