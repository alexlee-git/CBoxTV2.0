package tv.newtv.cboxtv.player;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.newtv.cms.bean.Content;
import com.newtv.libs.db.DBCallback;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         15:05
 * 创建人:           weihaichao
 * 创建日期:          2018/10/10
 */
public interface PlayerObserver {
    void onFinish(Content playInfo, int index, int position, int duration);

    void onExitApp();

    Activity getCurrentActivity();

    Intent getPlayerActivityIntent();

    boolean isVip();

    void addLBHistory(String alternateID);

    void activityJump(Context context, String actionType, String contentType, String contentUUID, String actionUri);

    void addLbCollect(Bundle bundle, DBCallback<String> dbCallback);

    void deleteLbCollect(String contentUUID, DBCallback<String> dbCallback);

    void detailsJumpActivity(Context context, String contentType, String contentUUID, String seriesSubUUID);
}
