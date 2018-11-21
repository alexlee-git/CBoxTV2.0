package tv.newtv.cboxtv.cms.screenList.model;


import com.newtv.cms.bean.SubContent;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public interface LabelDataModel {

    void requestLabelData(Map<String, Object> map, DataCompleteListener listener);

    interface DataCompleteListener {
        void sendLabelData(ArrayList<SubContent> contents, int total);
    }

}
