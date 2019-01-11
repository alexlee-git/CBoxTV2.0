package tv.newtv.cboxtv.player.ad;

import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;

import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.player.model.RequestAdParameter;

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
    /**
     * 产品类型
     */
    private static final String PRODUCT_TYPE = "1";

    private long startShowGoodsTime = 0;
    private long startShowQrCodeTime = 0;
    private long startTime = 0;

    public void showGoodsLog(String skuId,String x,String y,int defaultShowTime,String productName){
        if(startShowGoodsTime == 0){
            Log.i(TAG, "商品实际展示时长不正确");
            return;
        }

        long duration = System.currentTimeMillis() - startShowGoodsTime;
        startShowGoodsTime = 0;
        uploadLog(buildContent(TYPE_SHOW_GOODS,skuId,x,y,defaultShowTime*1000+"",duration+"",productName,PRODUCT_TYPE));
    }

    public void addToCart(String skuId,String productName){
        uploadLog(buildContent(TYPE_ADD_TO_CART,skuId,productName,PRODUCT_TYPE));
    }

    public void showQrCode(String skuId,String x,String y,int defaultShowTime,String productName){
        if(startShowQrCodeTime == 0){
            Log.i(TAG, "二维码展示时长不正确");
            return;
        }

        long duration = System.currentTimeMillis() - startShowQrCodeTime;
        startShowQrCodeTime = 0;
        uploadLog(buildContent(TYPE_SHOW_QR_CODE,skuId,x,y,defaultShowTime*1000+"",duration+"",productName,QR_CODE_TYPE_AUTH,PRODUCT_TYPE));
    }

    public void bind(String skuId,String mac,String userId,String productName){
        uploadLog(buildContent(TYPE_BIND,skuId,mac,userId,productName,PRODUCT_TYPE));
    }

    public void startShowQrCode(){
        startShowQrCodeTime = System.currentTimeMillis();
    }

    public void startShowGoods(){
        startShowGoodsTime = System.currentTimeMillis();
        startTime = startShowGoodsTime;
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

    public void uploadAdLog(String mid, String aid, String mtid, RequestAdParameter requestAdParameter){
        if(startTime != 0){
            long duration = System.currentTimeMillis() - startTime;
            startTime = 0;
            AdSDK.getInstance().report(mid,aid,mtid,requestAdParameter.getSeriesId(),requestAdParameter.getProgram(),
                    (duration/1000)+"",requestAdParameter.getExtend());
        }
    }

    public long getStartShowGoodsTime() {
        return startShowGoodsTime;
    }

    public long getStartShowQrCodeTime() {
        return startShowQrCodeTime;
    }
}
