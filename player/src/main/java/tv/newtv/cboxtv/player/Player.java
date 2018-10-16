package tv.newtv.cboxtv.player;

import com.newtv.cms.bean.Content;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         15:00
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public class Player implements PlayerObserver {

    private static Player instance = null;
    private List<PlayerObserver> observerList;

    private Player() {

    }

    public static Player get() {
        if (instance == null) {
            synchronized (Player.class) {
                if (instance == null) instance = new Player();
            }
        }
        return instance;
    }

    public void attachObserver(PlayerObserver observer) {
        synchronized (this) {
            if (observerList == null) {
                observerList = new ArrayList<>();
            }
            observerList.add(observer);
        }
    }

    @Override
    public void onFinish(Content playInfo, int index, int position) {
        if (observerList == null) return;
        synchronized (this) {
            for (PlayerObserver observer : observerList) {
                observer.onFinish(playInfo, index, position);
            }
        }
    }

    @Override
    public void onExitApp() {
        if (observerList == null) return;
        synchronized (this) {
            for (PlayerObserver observer : observerList) {
                observer.onExitApp();
            }
        }
    }
}
