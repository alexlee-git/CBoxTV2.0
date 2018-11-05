package tv.newtv.cboxtv.cms.screenList.model;

import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public interface SecondLabelModel {
    void requestSecondLabel(SecondLabelCompleteListener completeListener);

    interface SecondLabelCompleteListener {
        void sendSecondLabel(LabelBean labelBean);
    }
}
