package tv.newtv.cboxtv.player;

import android.util.Log;

import tv.icntv.icntvplayersdk.Constants;
import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         17:46
 * 创建人:           weihaichao
 * 创建日期:          2018/5/17
 */
public class PlayerConfig {

    public static final String TAG = PlayerConfig.class.getSimpleName();
    private volatile static PlayerConfig instance;
    private boolean jumpAD = false;
    private boolean screenChange = false;

    private String firstChannelId; //一级频道
    private String secondChannelId; // 二级频道
    private String topicId; //专题
    private String columnId; //一级栏目 用于区分栏目树点击
    private String secondColumnId; //二级栏目 用于区分栏目树点击

    public static PlayerConfig getInstance() {
        if (instance == null) {
            synchronized (PlayerConfig.class) {
                if (instance == null) instance = new PlayerConfig();
            }
        }
        return instance;
    }

    public int getJumpAD() {
        return jumpAD ? Constants.AD_MODEL_WITHOUT_BEFORE : Constants.AD_MODEL_ALL;
    }

    public void setJumpAD(boolean jumpAD) {
        Log.e(TAG, "set JumpAd = " + jumpAD);
        this.jumpAD = jumpAD;
    }

    public boolean isScreenChange() {
        return screenChange;
    }

    public void setScreenChange(boolean screenChange) {
        this.screenChange = screenChange;
    }

    public String getFirstChannelId() {
        return firstChannelId;
    }

    public void setFirstChannelId(String firstChannelId) {
        this.firstChannelId = firstChannelId;
    }

    public String getSecondChannelId() {
        return secondChannelId;
    }

    public void setSecondChannelId(String secondChannelId) {
        this.secondChannelId = secondChannelId;
    }

    public void cleanChannelId() {
        this.firstChannelId = null;
        this.secondChannelId = null;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getSecondColumnId() {
        return secondColumnId;
    }

    public void setSecondColumnId(String secondColumnId) {
        this.secondColumnId = secondColumnId;
    }

    public void cleanColumnId() {
        LogUtils.i(TAG, "cleanColumnId");
        this.columnId = null;
        this.secondColumnId = null;
    }
}
