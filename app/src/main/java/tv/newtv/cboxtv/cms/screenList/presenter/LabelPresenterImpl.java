package tv.newtv.cboxtv.cms.screenList.presenter;


import android.content.Context;

import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.cms.bean.FilterItem;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    public LabelPresenterImpl(Context context) {
        firstLabelModel = new FirstLabelModelImpl(context);
        secondLabelModel = new SecondLabelModelImpl(context );
        dataModel = new LabelDataModelImpl(context);
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
            public void sendFirstLabel(ModelResult<List<CategoryTreeNode>> modelResult) {
                if (iView == null) {
                    bind();
                }
                iView.showFirstMenuData(modelResult);
            }
        });


    }

    public void getSecondLabel() {
        if (iView == null) {
            bind();
        }
        String categoryId = iView.getCategoryId();
        secondLabelModel.requestSecondLabel(categoryId,new SecondLabelModel.SecondLabelCompleteListener() {
            @Override
            public void sendSecondLabel(ModelResult<List<FilterItem>> modelResult) {
                if (iView == null) {
                    bind();
                }
                iView.showSecondMenuData(modelResult);
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
            public void sendLabelData(ArrayList<SubContent> content,int total) {

                iView.showData(content ,  total);

            }
        });


    }


}
