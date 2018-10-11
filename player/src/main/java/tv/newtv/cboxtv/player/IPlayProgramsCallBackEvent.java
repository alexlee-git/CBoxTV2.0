package tv.newtv.cboxtv.player;


/**
 * Created by TCP on 2018/5/2.
 */

public interface IPlayProgramsCallBackEvent {

    void onNext(ProgramsInfo info, int index, boolean isNext);
}
