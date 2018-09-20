package tv.newtv.cboxtv.player.adplayer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.adplayer
 * 创建事件:         13:46
 * 创建人:           weihaichao
 * 创建日期:          2018/9/17
 */
public interface IADPlayer {
    void setPlaceHolder(int res);
    void setPlaceHolder(Drawable drawable);
    void setPlaceHolder(Bitmap bitmap);
    void setDataSource(Object source);
    void play();
    void pause();
    void change(View view);
    void stop();
    void resume();
    void release();
    boolean isPlaying();
}
