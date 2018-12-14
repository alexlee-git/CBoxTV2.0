package tv.newtv.cboxtv;

import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         16:12
 * 创建人:           weihaichao
 * 创建日期:          2018/9/4
 *
 *
 * 导航切换通知管理
 * 当切换导航的时候会通知注册监听的View
 */
public class Navigation {

    private static volatile Navigation instance;
    private String currentUUID;
    private List<NavigationChange> navigationChangeList;

    public static Navigation get() {
        if (instance == null) {
            synchronized (Navigation.class) {
                if (instance == null) instance = new Navigation();
            }
        }
        return instance;
    }

    public boolean isCurrentPage(String uuid){
        return TextUtils.equals(currentUUID,uuid);
    }

    public void detach(NavigationChange change){
        if(navigationChangeList == null || navigationChangeList.size() == 0) return;
        if(navigationChangeList.contains(change)){
            navigationChangeList.remove(change);
        }
    }

    public void attach(NavigationChange change) {
        if (navigationChangeList == null) {
            navigationChangeList = new ArrayList<>();
        }
        if (navigationChangeList.contains(change)) {
            return;
        }
        navigationChangeList.add(change);

        dispatchChange();
    }

    public void setCurrentUUID(String currentUUID) {
        this.currentUUID = currentUUID;
        dispatchChange();
    }

    private void dispatchChange() {
        if (navigationChangeList == null || navigationChangeList.size() == 0) return;
        for (NavigationChange change : navigationChangeList) {
            change.onChange(currentUUID);
        }
    }

    public interface NavigationChange {
        void onChange(String uuid);
    }
}
