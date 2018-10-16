package tv.newtv.cboxtv.cms.ad;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

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
import com.jd.smartcloudmobilesdk.utils.JLog;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.ad.model.AdBean;
import tv.newtv.cboxtv.cms.ad.model.BuyGoodsView;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BaseRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BuyGoodsRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.SPrefUtils;
import tv.newtv.cboxtv.cms.util.SystemUtils;
import tv.newtv.cboxtv.player.listener.ScreenListener;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
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
    private AdBean.AdspacesItem item;
    private MyScreenListener myScreenListener;
    private Disposable disposable;
    private boolean isShowQrCode = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if(buyGoodsView != null){
                        buyGoodsView.dismiss();
                    }
                    break;
                case 1:
                    if(!NewTVLauncherPlayerViewManager.getInstance().registerScreenListener(myScreenListener)){
                        handler.sendEmptyMessageDelayed(1,5 * 1000);
                    }
                    break;
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
        this.item = item;
        extMap = analyzeExt(item.ext);

        myScreenListener = new MyScreenListener();
        if(!NewTVLauncherPlayerViewManager.getInstance().registerScreenListener(myScreenListener)){
            handler.sendEmptyMessageDelayed(1,5 * 1000);
        }
//        getSkuInfo("1016913617");
    }

    private void show(){
        buyGoodsView = new BuyGoodsPopupWindow();
        buyGoodsView.setParamsMap(extMap);
        buyGoodsView.show(context,view);
        if(item.materials != null && item.materials.size() > 0){
//            buyGoodsView.setName(item.materials.get(0).name);
            skuId = item.materials.get(0).fontContent;
        }

        int duration = Integer.parseInt(TextUtils.isEmpty(extMap.get("duration")) ? "0" : extMap.get("duration"));
        if(duration <= 0){
            duration = DEFAULT_TIME;
        }
        handler.sendEmptyMessageDelayed(0,duration * 1000);

        //查询设备是否被绑定
        SmartBuyManager.checkTvBindStatus(PRODUCT_UUID,new NetDataHandler(){
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){
                    BindStatusRecv recv = (BindStatusRecv) outParam;
                    if(0 == recv.getIsBind()){
                        //未绑定
                        getQrcode();
                    }else if(1 == recv.getIsBind()){
                        //已绑定
                        feedId = (String) SPrefUtils.getValue(context, SPrefUtils.FEED_ID,"");
                        if(TextUtils.isEmpty(feedId)){
                            getQrcode();
                        }else {
                            buyGoodsView.setImageUrl(skuId);
                        }
                    }

                }else {
                    Log.i(TAG, "checkTvBindStatus: "+code);
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
                    buyGoodsView.showQrCode(authCode);
                    isShowQrCode = true;
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
                    if(TextUtils.isEmpty(feedId)){
                        showToast("请在手机上解除绑定后，重新绑定");
                    }else {
                        SPrefUtils.setValue(context,SPrefUtils.FEED_ID,feedId);
                        buyGoodsView.setImageUrl(skuId);
                        isShowQrCode = false;
                    }
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
                    MainLooper.get().post(new Runnable() {
                        @Override
                        public void run() {
                            addToCartSuccess();
                        }
                    });
                }
            }
        });
    }

    private void addToCartSuccess(){
        if(buyGoodsView != null){
            buyGoodsView.dismiss();
        }
        showToast("商品添加成功");
    }

    private void getSkuInfo(final String skuId){
        SmartBuyManager.getSkuInfo(skuId, new NetDataHandler() {
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                SkuInfoRecv recv = (SkuInfoRecv) outParam;
                if(recv != null){
                    //商品名称
                    String skuName = recv.getSkuName();
                    //商品图片地址
                    String skuImg = recv.getSkuImg();
                    //商品价格
                    String skuPrice = recv.getSkuPrice();
                    Log.i(TAG, "inParam:"+inParam+",name: "+skuName+",img:"+skuImg+",price:"+skuPrice);
                }else {
                    Log.i(TAG, "getSkuInfo: "+outParam);
                }

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
        NewTVLauncherPlayerViewManager.getInstance().unregisterScreenListener(myScreenListener);
        myScreenListener = null;

        handler.removeCallbacksAndMessages(null);
    }

    private Map<String,String> analyzeExt(String ext){
        Map<String,String> map = new HashMap<>();
        if(!TextUtils.isEmpty(ext)){
            String[] split = ext.split("\\|");
            for(String s : split){
                String[] split1 = s.split(":");
                if(split1.length > 1){
                    map.put(split1[0],split1[1]);
                }
            }
        }
        return map;
    }

    private void showToast(final String str){
        MainLooper.get().post(new Runnable() {
            @Override
            public void run() {
                if(context != null){
                    Toast.makeText(context,str,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int getIntValue(Map<String,String> map,String key){
        String value = map.get(key);
        return Integer.parseInt(TextUtils.isEmpty(value) ? "0" : value);
    }

    public class MyScreenListener implements ScreenListener{

        @Override
        public void enterFullScreen() {
            Observable.interval(0,1,TimeUnit.SECONDS)
                    .take(Integer.MAX_VALUE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(Long aLong) {
                            int currentPosition = NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition() / 1000;
                            int start = getIntValue(extMap, "start");
                            int duration  = getIntValue(extMap,"duration");
                            Log.i(TAG, "onNext: "+currentPosition+","+start+","+duration);
                            if(currentPosition >= start && currentPosition <= (start + duration)){
                                show();
                                disposable.dispose();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        @Override
        public void exitFullScreen() {
            if(disposable != null && !disposable.isDisposed()){
                disposable.dispose();
            }
            if(buyGoodsView != null){
                buyGoodsView.dismiss();
            }
        }
    }

    public boolean isShow(){
        if(buyGoodsView != null){
            return buyGoodsView.isShow();
        }
        return false;
    }

    public boolean isShowQrCode(){
        if(buyGoodsView != null && buyGoodsView.isShow()){
            return isShowQrCode;
        }
        return false;
    }

    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_UP){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(!isShowQrCode() && !TextUtils.isEmpty(feedId) && !TextUtils.isEmpty(skuId)){
                        addToCart(skuId);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if(buyGoodsView != null){
                        buyGoodsView.dismiss();
                        return true;
                    }
                    break;
            }
        }
        return false;
    }
}
