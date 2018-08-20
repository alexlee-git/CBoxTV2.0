package tv.newtv.cboxtv.player;

import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;

/**
 * Created by TCP on 2018/5/2.
 */

public interface IPlayProgramsCallBackEvent {

    void onNext(ProgramSeriesInfo.ProgramsInfo info, int index, boolean isNext);
}
