package tv.newtv.cboxtv.cms.screenList.view;

import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.cms.bean.FilterItem;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public interface LabelView {
    void showFirstMenuData(ModelResult<List<CategoryTreeNode>> bean);

    void showSecondMenuData(ModelResult<List<FilterItem>> bean);

    Map<String, Object> getMap();
    String getCategoryId();

    void showData(ArrayList<SubContent> contents, int total);
}
