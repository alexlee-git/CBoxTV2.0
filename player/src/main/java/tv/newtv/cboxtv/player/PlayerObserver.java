package tv.newtv.cboxtv.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.newtv.cms.bean.Content;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         15:05
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public interface PlayerObserver {
    void onFinish(Content playInfo, int index, int position);
    void onExitApp();
    Activity getCurrentActivity();
    Intent getPlayerActivityIntent();
}
