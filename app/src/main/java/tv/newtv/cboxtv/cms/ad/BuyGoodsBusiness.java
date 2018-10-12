package tv.newtv.cboxtv.cms.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
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

import java.util.HashMap;
import java.util.Map;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.ad.model.AdBean;
import tv.newtv.cboxtv.cms.ad.model.BuyGoodsView;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BaseRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BuyGoodsRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.SPrefUtils;
import tv.newtv.cboxtv.cms.util.SystemUtils;
import tv.newtv.cboxtv.views.BuyGoodsPopupWindow;

public class BuyGoodsBusiness implements IAdConstract.AdCommonConstractView<AdBean.AdspacesItem>{
    private static final int DEFAULT_TIME = 25;
    private static final String APP_KEY = "PSKAATT8IDSKXRSE3TP22ZAZ3265VV4D";
    private static final String APP_SECRET = "hzudu8fs3xpia2fe2qn2nm7p6iujj9vu";
    /**
     * 京东联盟ID
     */
    private static final String UNION_ID = "1001001488";
    /**
     * 电视在京东联盟创建的应用ID
     */
    private static final String SITE_ID = "1479901399";
    private static final String TAG = "BuyGoodsBusiness";
    /**
     * 产品id
     */
    private static final String PRODUCT_UUID = "497FFA";
    /**
     * 产品密钥
     */
    private static final String PRODUCT_SECRET = "fnsNR5bRk3zCEZof0WGP47uDeX6VQE6i1t9zZof8mxn4O2jh";

    private static boolean isInit = false;
    private BaseRequestAdPresenter adPresenter;
    private BuyGoodsView buyGoodsView;
    private Context context;
    private View view;
    private Map<String,String> extMap;
    private String skuId;
    private String feedId;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(buyGoodsView != null){
                buyGoodsView.dismiss();
            }
        }
    };

    public BuyGoodsBusiness(Context context, View view){
        Log.i(TAG, "BuyGoodsBusiness: ");
        this.context = context;
        this.view = view;
        adPresenter = new BuyGoodsRequestAdPresenter(this);
        adPresenter.getAD(Constant.AD_BUY_GOODS,"");

        if(!isInit){
            //初始化
            JDSmartSDK.getInstance().init(context,APP_KEY, SystemUtils.getDeviceMac(context));
            isInit = true;
        }
    }

    @Override
    public void showAd(AdBean.AdspacesItem item) {
        Log.i(TAG, "showAd: ");
        extMap = analyzeExt(item.ext);
        int duration = Integer.parseInt(TextUtils.isEmpty(extMap.get("duration")) ? "0" : extMap.get("duration"));
        if(duration <= 0){
            duration = DEFAULT_TIME;
        }

        buyGoodsView = new BuyGoodsPopupWindow();
        buyGoodsView.setParamsMap(extMap);
        buyGoodsView.show(context,view);
        if(item.materials != null && item.materials.size() > 0){
            buyGoodsView.setName(item.materials.get(0).name);
            skuId = item.materials.get(0).fontContent;
        }

        //TODO 监听视频播放时间，到了显示商品图片 等待添加
        feedId = (String) SPrefUtils.getValue(context, SPrefUtils.FEED_ID,"");
        if(TextUtils.isEmpty(feedId)){
            getQrcode();
        }else {
            buyGoodsView.setImageUrl(skuId);
        }

        handler.sendEmptyMessageDelayed(0,duration * 1000);

        //查询设备是否被绑定
//        SmartBuyManager.checkTvBindStatus(APP_KEY,new NetDataHandler(){
//            @Override
//            public void netDataCallback(int code, Object inParam, Object outParam) {
//                if(code == 0 && outParam != null){
//                    BindStatusRecv recv = (BindStatusRecv) outParam;
//                    if(0 == recv.getIsBind()){
//                        Log.i(TAG, "netDataCallback: 1");
//                        //未绑定
//                        getQrcode();
//                    }else if(1 == recv.getIsBind()){
//                        //已绑定
//                    }
//                }else {
//                    Log.i(TAG, "netDataCallback: "+code);
//                }
//            }
//        });
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
                    buyGoodsView.showQrCode(authCode);
                    getResult(authCode,expiresIn);
                }
            }
        });
    }

    /**
     * 获取二维码授权结果
     */
    private void getResult(String authCode,long expiresIn){
        Log.i(TAG, "getResult: "+authCode+","+expiresIn);
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
                    Log.i(TAG, "expiresIn: "+expiresIn+",time:"+time+",uid:"+uid+",userNick:"+userNick+",avatar:"+avatar);
                    if(!TextUtils.isEmpty(uid)){
                        activateAndBindDevice();
                    }
                }
            }
        });
    }

    private void activateAndBindDevice(){
        ActivateAndBindDeviceSend send = new ActivateAndBindDeviceSend();
        //对应产品UUID
        send.setProductUuid(PRODUCT_UUID);
        //对应产品密钥
        send.setProductSecret(PRODUCT_SECRET);
        SmartBuyManager.activateAndBindDevice(send, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){
                    ActivateAndBindDeviceRecv recv = (ActivateAndBindDeviceRecv) outParam;
                    //京东服务器系统时间
                    String serverTime = recv.getServerTime();
                    //京东设备唯一ID
                    feedId = recv.getFeedId();
                    //京东设备密钥
                    String accessKey = recv.getAccessKey();
                    Log.i(TAG, "serverTime："+serverTime+",feedId:"+feedId+",accessKey:"+accessKey);
                    SPrefUtils.setValue(context,SPrefUtils.FEED_ID,feedId);
                    buyGoodsView.setImageUrl(skuId);
//                    addToCart(skuId);
                }
            }
        });
    }

    private void addToCart(String skuId){
        AddToCartSend send = new AddToCartSend();
        //京东商品ID
        send.setSkuId(skuId);
        //京东联盟ID
        send.setUnionId(UNION_ID);
        //电视在京东联盟创建的应用ID
        send.setSiteId(SITE_ID);
        //设备激活返回的FeedID
        send.setFeedId(feedId);
        SmartBuyManager.addToCart(send, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){

                }
            }
        });
    }

    private void getSkuInfo(final String skuId){
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
                Log.i(TAG, "inParam:"+inParam+",name: "+skuName+",img:"+skuImg+",price:"+skuPrice);
            }
        });
    }

    @Override
    public void fail() {
        Log.i(TAG, "fail: ");
        MainLooper.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                showAd(new AdBean.AdspacesItem());
            }
        },3000);
    }

    public void onDestroy(){
        if(buyGoodsView != null){
            buyGoodsView.dismiss();
        }
    }

    private Map<String,String> analyzeExt(String ext){
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(ext)){
            String[] split = ext.split("|");
            for(String s : split){
                String[] split1 = s.split(":");
                if(split1.length > 1){
                    map.put(split[0],split[1]);
                }
            }
        }
        return map;
    }
}
