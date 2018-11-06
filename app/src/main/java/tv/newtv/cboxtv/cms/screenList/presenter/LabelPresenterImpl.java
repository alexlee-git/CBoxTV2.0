package tv.newtv.cboxtv.cms.screenList.presenter;


import java.util.Map;

import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;
import tv.newtv.cboxtv.cms.screenList.bean.LabelDataBean;
import tv.newtv.cboxtv.cms.screenList.bean.TabBean;
import tv.newtv.cboxtv.cms.screenList.model.FirstLabelModel;
import tv.newtv.cboxtv.cms.screenList.model.FirstLabelModelImpl;
import tv.newtv.cboxtv.cms.screenList.model.LabelDataModel;
import tv.newtv.cboxtv.cms.screenList.model.LabelDataModelImpl;
import tv.newtv.cboxtv.cms.screenList.model.SecondLabelModel;
import tv.newtv.cboxtv.cms.screenList.model.SecondLabelModelImpl;
import tv.newtv.cboxtv.cms.screenList.view.LabelView;


/**
 * Created by 冯凯 on 2018/1/16.
 */

public class LabelPresenterImpl extends LabelPresenter<LabelView> {

    private FirstLabelModelImpl firstLabelModel;
    private SecondLabelModelImpl secondLabelModel;
    private LabelDataModelImpl dataModel;
    private LabelView iView;


    public LabelPresenterImpl() {
        firstLabelModel = new FirstLabelModelImpl();
        secondLabelModel = new SecondLabelModelImpl();
        dataModel = new LabelDataModelImpl();
    }


    @Override
    void bind() {
        if (isLive()) {
            iView = getIView();
        }
    }

    public void getFirstLabel() {
        firstLabelModel.requestFirstLabel(new FirstLabelModel.FirstLabelCompleteListener() {
            @Override
            public void sendFirstLabel(TabBean tabBean) {
                if (iView == null) {
                    bind();
                }
                iView.showFirstMenuData(tabBean);
            }
        });


    }

    public void getSecondLabel() {

        secondLabelModel.requestSecondLabel(new SecondLabelModel.SecondLabelCompleteListener() {
            @Override
            public void sendSecondLabel(LabelBean labelBean) {
                if (iView == null) {
                    bind();
                }
                iView.showSecondMenuData(labelBean);
            }
        });


    }

    public void getLabelData() {
        if (iView == null) {
            bind();
        }
        Map<String, Object> map = iView.getMap();
        dataModel.requestLabelData(map, new LabelDataModel.DataCompleteListener() {
            @Override
            public void sendLabelData(LabelDataBean labelDataBean) {

                iView.showData(labelDataBean);

            }
        });


    }


}
