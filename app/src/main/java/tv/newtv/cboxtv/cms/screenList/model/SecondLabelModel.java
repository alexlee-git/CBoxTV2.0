package tv.newtv.cboxtv.cms.screenList.model;

import com.newtv.cms.bean.FilterItem;
import com.newtv.cms.bean.ModelResult;

import java.util.List;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public interface SecondLabelModel {
    void requestSecondLabel(String categoryId, SecondLabelCompleteListener completeListener);

    interface SecondLabelCompleteListener {
        void sendSecondLabel(ModelResult<List<FilterItem>> modelResult);
    }
}
