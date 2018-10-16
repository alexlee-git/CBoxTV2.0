package tv.newtv.cboxtv.player;


import com.newtv.cms.bean.SubContent;

/**
 * Created by TCP on 2018/5/2.
 */

public interface IPlayProgramsCallBackEvent {

    void onNext(SubContent info, int index, boolean isNext);
}
