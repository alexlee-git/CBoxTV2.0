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
import com.jd.smartcloudmobilesdk.shopping.bean.AddToCartRecv;
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
import tv.newtv.cboxtv.cms.ad.model.GoodsBean;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BaseRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.BuyGoodsRequestAdPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.util.GsonUtil;
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

    public static final int DISMISS_MSG = 0;

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
    private int duration = DEFAULT_TIME;
    private int start;
    private BuyGoodsLog log;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_MSG:
                    dismiss();
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
        log = new BuyGoodsLog();

        if(!isInit){
            //初始化
            JDSmartSDK.getInstance().init(context,APP_KEY, SystemUtils.getDeviceMac(context));
            isInit = true;
        }
    }

    @Override
    public void showAd(AdBean.AdspacesItem item) {
        Log.i(TAG, "showAd: ");
        if(item.materials == null || item.materials.size() == 0){
            Log.i(TAG, "数据不合法");
            return;
        }
        this.item = item;
        extMap = analyzeExt(item.ext);
        GoodsBean goodsBean = GsonUtil.fromjson(item.materials.get(0).eventContent, GoodsBean.class);
        skuId = goodsBean.sku;
        start = goodsBean.start;

        int playTime = item.materials.get(0).playTime;
        if(playTime > 0){
            duration = playTime;
        }

        if(myScreenListener == null){
            myScreenListener = new MyScreenListener();
            NewTVLauncherPlayerViewManager.getInstance().registerScreenListener(myScreenListener);
        }
        if(NewTVLauncherPlayerViewManager.getInstance().isFullScreen()){
            myScreenListener.enterFullScreen();
        }

//        getSkuInfo("1016913617");
    }

    private void show(){
        dismiss();
        buyGoodsView = new BuyGoodsPopupWindow();
        buyGoodsView.setParamsMap(extMap);
        buyGoodsView.init(context,view);

        showGoods();
    }

    private void showGoods(){
        buyGoodsView.setImageUrl(item.materials.get(0).filePath);
        log.startShowGoods();
        sendCloseMessage(duration);
        isShowQrCode = false;
    }

    private void checkTvBindStatus(){
        //查询设备是否被绑定
        SmartBuyManager.checkTvBindStatus(PRODUCT_UUID,new NetDataHandler(){
            @Override
            public void netDataCallback(int code, Object inParam, Object outParam) {
                if(code == 0 && outParam != null){
                    BindStatusRecv recv = (BindStatusRecv) outParam;
                    if(0 == recv.getIsBind()){
                        //未绑定
                        getQrcode(false);
                    }else if(1 == recv.getIsBind()){
                        //已绑定
                        feedId = (String) SPrefUtils.getValue(context, SPrefUtils.FEED_ID,"");
                        if(TextUtils.isEmpty(feedId)){
                            getQrcode(false);
                        }else {
                            addToCart(skuId);
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
    private void getQrcode(final boolean refresh){
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
                    if(!refresh){
                        logShowGoods();
                        log.startShowQrCode();
                        sendCloseMessage(duration);
                    }
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
                    if (TextUtils.equals(recv.getCode(), "200")) {
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
                            activateAndBindDevice(uid);
                        }
                    } else {
                        switch (recv.getCode()){
                            case "30010":
                            case "30012":
                            case "30015":
                                getQrcode(true);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void activateAndBindDevice(final String uid){
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
                        getQrcode(true);
                    }else {
                        SPrefUtils.setValue(context,SPrefUtils.FEED_ID,feedId);
                        addToCart(skuId);
//                        buyGoodsView.setImageUrl(item.materials.get(0).filePath);
//                        isShowQrCode = false;

//                        log.startShowGoods();
                        log.bind(skuId,SystemUtils.getDeviceMac(context),uid,item.materials.get(0).name);
//                        logShowQrCode();
//                        sendCloseMessage(duration);
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
            public void netDataCallback(int code, Object inParam, final Object outParam) {
                if(code == 0 && outParam != null){
                    MainLooper.get().post(new Runnable() {
                        @Override
                        public void run() {
                            AddToCartRecv addToCartRecv = (AddToCartRecv) outParam;
                            if(TextUtils.equals(addToCartRecv.getCode(),"2003")){
                                dismiss();
                                showToast("3分钟内不允许重复添加商品");
                                return;
                            }
                            addToCartSuccess();
                        }
                    });
                }
            }
        });
    }

    private void addToCartSuccess(){
        dismiss();
        showToast("商品添加成功");
        log.addToCart(skuId,item.materials.get(0).name);
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
//        MainLooper.get().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showAd(new AdBean.AdspacesItem());
//            }
//        },3000);
    }

    public void onDestroy(){
        dismiss();
        handler.removeCallbacksAndMessages(null);
        NewTVLauncherPlayerViewManager.getInstance().unregisterScreenListener(myScreenListener);
        myScreenListener = null;
        adPresenter.destroy();
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
            if(disposable != null ){
                disposable.dispose();
            }
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
            dismiss();
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
                    if(!SystemUtils.isFastDoubleClick(3* 1000)){
                        checkTvBindStatus();
                    }
                    return true;
//                    if(!isShowQrCode() && !TextUtils.isEmpty(feedId) && !TextUtils.isEmpty(skuId)){
//                        addToCart(skuId);
//                        return true;
//                    }
//                    break;
                case KeyEvent.KEYCODE_BACK:
                    if(dismiss()){
                        return true;
                    }
                    break;
            }
        }

        switch (event.getKeyCode()){
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
        }
        return false;
    }

    private boolean dismiss(){
        if(buyGoodsView != null && buyGoodsView.isShow()){
            buyGoodsView.dismiss();

            handler.removeCallbacksAndMessages(null);
            if(isShowQrCode){
                logShowQrCode();
            }else {
                logShowGoods();
            }

            return true;
        }
        return false;
    }

    private void logShowQrCode(){
        log.showQrCode(skuId,extMap.get("x"),extMap.get("y"),duration,item.materials.get(0).name);
    }

    private void logShowGoods(){
        log.showGoodsLog(skuId,extMap.get("x"),extMap.get("y"),duration,item.materials.get(0).name);
    }

    private void sendCloseMessage(int duration){
        handler.removeMessages(DISMISS_MSG);
        handler.sendEmptyMessageDelayed(DISMISS_MSG,duration * 1000);
    }
}
