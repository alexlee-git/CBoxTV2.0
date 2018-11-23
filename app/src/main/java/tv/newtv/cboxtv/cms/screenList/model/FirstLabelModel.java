package tv.newtv.cboxtv.cms.screenList.model;


import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.cms.bean.ModelResult;

import java.util.List;

/**
 * Created by 冯凯 on 2018/9/30.
 */

public interface FirstLabelModel {
    void requestFirstLabel(FirstLabelCompleteListener completeListener);

    interface FirstLabelCompleteListener {
        void sendFirstLabel(ModelResult<List<CategoryTreeNode>> modelResult);

    }


}
