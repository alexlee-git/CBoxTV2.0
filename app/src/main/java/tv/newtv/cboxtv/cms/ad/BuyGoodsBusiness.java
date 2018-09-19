package tv.newtv.cboxtv.cms.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.ad.model.AdBean;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BaseRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BuyGoodsRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.views.BuyGoodsPopupWindow;

public class BuyGoodsBusiness implements IAdConstract.AdCommonConstractView<AdBean.Material>{
    private static final int DEFAULT_TIME = 25;
    private BaseRequestAdPresenter adPresenter;
    private BuyGoodsPopupWindow popupWindow;
    private Context context;
    private View view;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(popupWindow != null){
                popupWindow.dismiss();
            }
        }
    };

    public BuyGoodsBusiness(Context context, View view){
        this.context = context;
        this.view = view;
        adPresenter = new BuyGoodsRequestAdPresenter(this);
        adPresenter.getAD(Constant.AD_DESK,Constant.AD_BUY_GOODS);
    }

    @Override
    public void showAd(AdBean.Material adInfos) {
        if(adInfos.playTime <= 0){
            adInfos.playTime = DEFAULT_TIME;
        }
        popupWindow = new BuyGoodsPopupWindow();
        popupWindow.show(context,view);
        handler.sendEmptyMessageDelayed(0,adInfos.playTime * 1000);


    }

    @Override
    public void fail() {

    }
}
