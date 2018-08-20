package tv.newtv.cboxtv.cms.details.model;

/**
 * Created by caolonghe on 2018/2/2.
 */

public interface IProgramsModel {

    void requestProgramsData(String appkey ,String channelId ,String left,String right,String contentUUID);
}
