package tv.newtv.cboxtv.exit.presenter;


import tv.newtv.cboxtv.exit.bean.RecommendBean;
import tv.newtv.cboxtv.exit.model.RecommendModel;
import tv.newtv.cboxtv.exit.model.RecommendModelImpl;
import tv.newtv.cboxtv.exit.view.RecommendView;


/**
 * Created by 冯凯 on 2018/1/16.
 */

public class RecommendPresenterImpl extends RecommendPresenter<RecommendView> {

    private RecommendModelImpl recommendModel;
    private RecommendView iView;


    public RecommendPresenterImpl() {
        recommendModel = new RecommendModelImpl();
    }


    @Override
    void bind() {
        if (isLive()) {
            iView = getIView();
        }
    }

    public void getRecommendData() {
        recommendModel.requestRecommendData(new RecommendModel.CompleteListener() {
            @Override
            public void sendRecommendData(RecommendBean recommendBean) {
                if (iView==null){
                    bind();
                }
                iView.showData(recommendBean);
            }
        });


    }




}
