package tv.newtv.cboxtv.cms.screenList.model;


import tv.newtv.cboxtv.cms.screenList.bean.TabBean;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public interface FirstLabelModel {
    void requestFirstLabel(FirstLabelCompleteListener completeListener);

    interface FirstLabelCompleteListener {
        void sendFirstLabel(TabBean tabBean);
    }


}
