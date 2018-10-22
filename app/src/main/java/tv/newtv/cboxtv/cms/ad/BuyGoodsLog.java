package tv.newtv.cboxtv.cms.ad;

import android.util.Log;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;

public class BuyGoodsLog {
    private static final String TAG = "BuyGoodsLog";
    /**
     * 商品展示type
     */
    private static final String TYPE_SHOW_GOODS = "0";
    /**
     * 加入商品到购物车
     */
    private static final String TYPE_ADD_TO_CART = "1";
    /**
     * 二维码展示
     */
    private static final String TYPE_SHOW_QR_CODE = "2";
    /**
     * 用户绑定
     */
    private static final String TYPE_BIND = "3";
    /**
     * 二维码类型
     */
    private static final String QR_CODE_TYPE_AUTH = "1";

    private long startShowGoodsTime = 0;
    private long startShowQrCodeTime = 0;

    public void showGoodsLog(String skuId,String x,String y,int defaultShowTime,String productName){
        if(startShowGoodsTime == 0){
            Log.i(TAG, "商品实际展示时长不正确");
            return;
        }

        long duration = System.currentTimeMillis() - startShowGoodsTime;
        startShowGoodsTime = 0;
        uploadLog(buildContent(TYPE_SHOW_GOODS,skuId,x,y,defaultShowTime*1000+"",duration+"",productName));
    }

    public void addToCart(String skuId,String productName){
        uploadLog(buildContent(TYPE_ADD_TO_CART,skuId,productName));
    }

    public void showQrCode(String skuId,String x,String y,int defaultShowTime,String productName){
        if(startShowQrCodeTime == 0){
            Log.i(TAG, "二维码展示时长不正确");
            return;
        }

        long duration = System.currentTimeMillis() - startShowQrCodeTime;
        startShowQrCodeTime = 0;
        uploadLog(buildContent(TYPE_SHOW_QR_CODE,skuId,x,y,defaultShowTime*1000+"",duration+"",productName,QR_CODE_TYPE_AUTH));
    }

    public void bind(String skuId,String mac,String userId,String productName){
        uploadLog(buildContent(TYPE_BIND,skuId,mac,userId,productName));
    }

    public void startShowQrCode(){
        startShowQrCodeTime = System.currentTimeMillis();
    }

    public void startShowGoods(){
        startShowGoodsTime = System.currentTimeMillis();
    }

    private static String buildContent(String... params){
        StringBuilder sb = new StringBuilder();
        for(String content : params){
            sb.append(content)
                    .append(",");
        }
        if(sb.length() > 0){
            sb.delete(sb.length()-1,sb.length());
        }
        return sb.toString();
    }

    private void uploadLog(String content){
        LogUploadUtils.uploadLog(Constant.LOG_BUY_GOODS,content);
    }
}
