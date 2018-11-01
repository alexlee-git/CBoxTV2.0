package tv.newtv.cboxtv.cms.details.presenter.adpresenter;

import android.text.TextUtils;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.ad.model.AdBean;
import tv.newtv.cboxtv.cms.util.GsonUtil;

public class BuyGoodsRequestAdPresenter extends BaseRequestAdPresenter{
    private static final String TAG = "BuyGoodsRequestAdPresen";
    private IAdConstract.AdCommonConstractView adCallback;
    private ADConfig.ColumnListener myColumnListener = new MyColumnListener();

    public BuyGoodsRequestAdPresenter(IAdConstract.AdCommonConstractView adTextConstractView){
        this.adCallback = adTextConstractView;
    }

    @Override
    public void dealResult(String result) {
        ADConfig.getInstance().registerListener(myColumnListener);
        if(adCallback == null){
            return;
        }
        if(TextUtils.isEmpty(result)){
            adCallback.fail();
            return;
        }

        AdBean bean = GsonUtil.fromjson(result, AdBean.class);
        if(bean.adspaces != null && bean.adspaces.buygoods != null && bean.adspaces.buygoods.size() > 0){
            AdBean.AdspacesItem adspacesItem = bean.adspaces.buygoods.get(0);
            if(adspacesItem.materials != null && adspacesItem.materials.size() > 0 ){
                adCallback.showAd(adspacesItem);
                return;
            }
        }

        adCallback.fail();
    }

    private class MyColumnListener implements ADConfig.ColumnListener{

        @Override
        public void receive() {
            getAD(Constant.AD_BUY_GOODS,"");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ADConfig.getInstance().registerListener(myColumnListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        ADConfig.getInstance().removeListener(myColumnListener);
    }

    @Override
    public void destroy() {
        super.destroy();
        ADConfig.getInstance().removeListener(myColumnListener);
    }
}
