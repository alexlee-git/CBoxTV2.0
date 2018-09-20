package tv.newtv.cboxtv.cms.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.jd.smartcloudmobilesdk.init.JDSmartSDK;
import com.jd.smartcloudmobilesdk.shopping.SmartBuyManager;
import com.jd.smartcloudmobilesdk.shopping.bean.ActivateAndBindDeviceRecv;
import com.jd.smartcloudmobilesdk.shopping.bean.ActivateAndBindDeviceSend;
import com.jd.smartcloudmobilesdk.shopping.bean.AddToCartSend;
import com.jd.smartcloudmobilesdk.shopping.bean.AuthQrcodeRecv;
import com.jd.smartcloudmobilesdk.shopping.bean.AuthQrcodeSend;
import com.jd.smartcloudmobilesdk.shopping.bean.AuthResultRecv;
import com.jd.smartcloudmobilesdk.shopping.bean.AuthResultSend;
import com.jd.smartcloudmobilesdk.shopping.bean.BindStatusRecv;
import com.jd.smartcloudmobilesdk.shopping.bean.SkuInfoRecv;
import com.jd.smartcloudmobilesdk.shopping.listener.NetDataHandler;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.ad.model.AdBean;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BaseRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BuyGoodsRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.SystemUtils;
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

        //初始化
        JDSmartSDK.getInstance().init(context,"", SystemUtils.getDeviceMac(context));
        //查询设备是否被绑定
        SmartBuyManager.checkTvBindStatus("",new NetDataHandler(){
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){
                    BindStatusRecv recv = (BindStatusRecv) outParam;
                    if(0 == recv.getIsBind()){
                        //未绑定
                        getQrcode();
                    }else if(1 == recv.getIsBind()){
                        //已绑定
                    }
                }
            }
        });
    }

    /**
     * 获取授权临时二维码
     */
    private void getQrcode(){
        AuthQrcodeSend authQrcodeSend = new AuthQrcodeSend();
        SmartBuyManager.getQrcode(authQrcodeSend, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){
                    AuthQrcodeRecv recv = (AuthQrcodeRecv) outParam;
                    //授权二维码信息
                    String authCode = recv.getAuthCode();
                    //超时时间
                    long expiresIn = recv.getExpiresIn();
                    getResult(authCode,expiresIn);
                }
            }
        });
    }

    /**
     * 获取二维码授权结果
     */
    private void getResult(String authCode,long expiresIn){
        AuthResultSend authResultSend = new AuthResultSend();
        authResultSend.setUserQrcode(authCode);
        authResultSend.setExpiresIn(expiresIn);
        SmartBuyManager.getResult(authResultSend, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 &&outParam != null){
                    AuthResultRecv recv = (AuthResultRecv) outParam;
                    //用户accessToken超时时间
                    String expiresIn = recv.getExpiresIn();
                    //时间
                    String time = recv.getTime();
                    //京东唯一的用户ID
                    String uid = recv.getUid();
                    //用户的昵称
                    String userNick = recv.getUserNick();
                    //用户头像
                    String avatar = recv.getAvatar();
                }
            }
        });
    }

    private void activateAndBindDevice(){
        ActivateAndBindDeviceSend send = new ActivateAndBindDeviceSend();
        //对应产品UUID
        send.setProductUuid("");
        //对应产品密钥
        send.setProductSecret("");
        SmartBuyManager.activateAndBindDevice(send, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){
                    ActivateAndBindDeviceRecv recv = (ActivateAndBindDeviceRecv) outParam;
                    //京东服务器系统时间
                    String serverTime = recv.getServerTime();
                    //京东设备唯一ID
                    String feedId = recv.getFeedId();
                    //京东设备密钥
                    String accessKey = recv.getAccessKey();
                }
            }
        });
    }

    private void addToCart(String feedId){
        AddToCartSend send = new AddToCartSend();
        //京东商品ID
        send.setSkuId("");
        //京东联盟ID
        send.setUnionId("");
        //电视在京东联盟创建的应用ID
        send.setSiteId("");
        //设备激活返回的FeedID
        send.setFeedId("");
        SmartBuyManager.addToCart(send, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){

                }
            }
        });
    }

    private void getSkuInfo(String skuId){
        SmartBuyManager.getSkuInfo(skuId, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                SkuInfoRecv recv = (SkuInfoRecv) outParam;
                //商品名称
                String skuName = recv.getSkuName();
                //商品图片地址
                String skuImg = recv.getSkuImg();
                //商品价格
                String skuPrice = recv.getSkuPrice();
            }
        });
    }

    @Override
    public void fail() {

    }
}
