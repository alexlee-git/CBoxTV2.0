package tv.newtv.cboxtv.cms.screenList.view;

import java.util.Map;

import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;
import tv.newtv.cboxtv.cms.screenList.bean.LabelDataBean;
import tv.newtv.cboxtv.cms.screenList.bean.TabBean;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public interface LabelView {
    void showFirstMenuData(TabBean bean);

    void showSecondMenuData(LabelBean bean);

    Map<String, Object> getMap();

    void showData(LabelDataBean dataBean);
}
