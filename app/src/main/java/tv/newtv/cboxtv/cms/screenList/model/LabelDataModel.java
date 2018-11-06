package tv.newtv.cboxtv.cms.screenList.model;


import java.util.Map;

import tv.newtv.cboxtv.cms.screenList.bean.LabelDataBean;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public interface LabelDataModel {

    void requestLabelData(Map<String, Object> map, DataCompleteListener listener);

    interface DataCompleteListener {
        void sendLabelData(LabelDataBean labelDataBean);
    }

}
