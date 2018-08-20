package tv.newtv.cboxtv.cms.details.presenter;

/**
 * Created by lixin on 2018/1/15.
 */

public interface IProgramPresenter {

    void inflateProgramSeries(String value);

    void requestProgramsData(String appkey ,String channelId ,String left,String right,String contentUUID);

}
