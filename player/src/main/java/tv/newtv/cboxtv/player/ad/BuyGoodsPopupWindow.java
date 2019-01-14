package tv.newtv.cboxtv.player.ad;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.letv.LetvDeviceUtil;
import com.newtv.libs.Libs;
import com.newtv.libs.MainLooper;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.player.R;

public class BuyGoodsPopupWindow extends PopupWindow implements BuyGoodsView{

    private Context context;
    private View parent;

    private View rootView;
    private ImageView imageView;
    private ImageView qrCodeImage;
    private TextView textView;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean isDestroy;

    @Override
    public void init(Context context,View parent){
        this.context = context;
        this.parent = parent;
        if (LetvDeviceUtil.isLetvDevice() || Libs.get().getFlavor().equals(DeviceUtil.XIONG_MAO)) {
            rootView = LayoutInflater.from(context).inflate(R.layout.layout_lstv_buy_goods_pop,null);
        } else{
            rootView = LayoutInflater.from(context).inflate(R.layout.layout_buy_goods_pop,null);
        }

        imageView = rootView.findViewById(R.id.image);
        textView = rootView.findViewById(R.id.text);
        qrCodeImage = rootView.findViewById(R.id.qr_code_image);
        setContentView(rootView);
    }

    @Override
    public void setImageUrl(final String url){
        MainLooper.get().post(new Runnable() {
            @Override
            public void run() {
                qrCodeImage.setVisibility(View.GONE);
                if(TextUtils.isEmpty(url)){
                    Picasso.get().load(R.drawable.skuimage).into(imageView);
                }else {
                    Picasso.get().load(url).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if(NewTVLauncherPlayerViewManager.getInstance().isFullScreen()){
                                show();
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void setName(String name) {
        textView.setText(name);
    }

    @Override
    public void setParamsMap(Map<String, String> map) {
        x = getIntValue(map,"x");
        y = getIntValue(map,"y");
        width = getIntValue(map,"w");
        height = getIntValue(map,"h");
    }

    private int getIntValue(Map<String,String> map,String key){
        String value = map.get(key);
        return Integer.parseInt(TextUtils.isEmpty(value) ? "0" : value);
    }

    @Override
    public void showQrCode(final String authCode){
        MainLooper.get().post(new Runnable() {
            @Override
            public void run() {
                qrCodeImage.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.qrcode_bg);

                QrcodeUtil qrcodeUtil = new QrcodeUtil();
                qrcodeUtil.createQRImage(authCode,qrCodeImage,context.getResources().getDimensionPixelOffset(R.dimen.width_167px),context.getResources().getDimensionPixelOffset(R.dimen.width_167px));
                showQrCode();
            }
        });
    }

    @Override
    public void showQrCode(){
        show(context.getResources().getDimensionPixelOffset(R.dimen.width_500px),context.getResources().getDimensionPixelOffset(R.dimen.width_370px));
    }

    private void show(){
        if(width <= 0){
            width = context.getResources().getDimensionPixelOffset(R.dimen.width_500px);
        }
        if(height <= 0){
            height = context.getResources().getDimensionPixelOffset(R.dimen.width_370px);
        }
        show(width,height,x,y);
    }

    private void show(int width,int height){
        show(width,height,0,0);
    }

    private void show(int width,int height,int x,int y){
        if(isDestroy){
            return;
        }
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        if(isShowing()){
            dismiss();
        }

        if(x <=0 || y <= 0){
            showAtLocation(parent, Gravity.NO_GRAVITY, ScreenUtils.getScreenW() - width + context.getResources().getDimensionPixelOffset(R.dimen.width_50px),
                    ScreenUtils.getScreenH() - height + context.getResources().getDimensionPixelOffset(R.dimen.width_50px));
        } else {
            showAtLocation(parent,Gravity.NO_GRAVITY,x,y);
        }
        startAnim(width,0);
    }

    private void startAnim(int fromX,int toX){
        ObjectAnimator translationX = new ObjectAnimator().ofFloat(rootView, "translationX", fromX, toX);
        translationX.setDuration(400);
        translationX.start();
    }

    @Override
    public boolean isShow(){
        return isShowing();
    }

    @Override
    public void onDestroy() {
        isDestroy = true;
    }
}
