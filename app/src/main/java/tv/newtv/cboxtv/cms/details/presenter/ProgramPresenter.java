package tv.newtv.cboxtv.cms.details.presenter;

import tv.newtv.cboxtv.cms.details.view.IProgramView;
import tv.newtv.cboxtv.cms.details.model.ProgramsModel;
import tv.newtv.cboxtv.cms.details.model.IProgramsModel;

/**
 * Created by lixin on 2018/1/15.
 */

public class ProgramPresenter implements IProgramPresenter {

    private IProgramView mIProgramView;
    private IProgramsModel mProgramsModel;

    public ProgramPresenter(IProgramView mIProgramView) {
        this.mIProgramView = mIProgramView;
        this.mProgramsModel = new ProgramsModel(this);
    }

    @Override
    public void inflateProgramSeries(String value) {
        if (mIProgramView != null) {
            mIProgramView.inflateProgramSeries(value);
        }
    }

    @Override
    public void requestProgramsData(String appkey, String channelId, String left, String right, String contentUUID) {
        if (mProgramsModel != null) {
            mProgramsModel.requestProgramsData(appkey, channelId, left, right, contentUUID);
        }
    }
}
