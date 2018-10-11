package com.newtv.libs.ad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.newtv.libs.Constant;
import com.newtv.libs.R;
import com.newtv.libs.util.ScreenUtils;
import com.squareup.picasso.Picasso;


public class AdPopupWindow extends PopupWindow implements IAdConstract.IADConstractView,PopupWindow.OnDismissListener {
    private ADPresenter adPresenter;
    private ImageView imageView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    public void show(Context context,View parent){
        View popView = LayoutInflater.from(context).inflate(R.layout.layout_ad_pop,null);
        imageView = popView.findViewById(R.id.image);
        setContentView(popView);

        int width = 300;
        int height = 150;
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        showAtLocation(parent, Gravity.NO_GRAVITY,ScreenUtils.getScreenW() - width,
                ScreenUtils.getScreenH() - height);

        adPresenter = new ADPresenter(this);
//        adPresenter.getAD(Constant.AD_DESK,Constant.AD_GLOBAL_POPUP,"");

        setOnDismissListener(this);
    }

    @Override
    public void showAd(ADHelper.AD.ADItem item) {
        if(item.PlayTime <= 0){
            item.PlayTime = 5;
        }
        if(!TextUtils.isEmpty(item.AdUrl)){
            Picasso.get().load(item.AdUrl).into(imageView);
            handler.sendEmptyMessageDelayed(0,item.PlayTime * 1000);
        }else {
            dismiss();
        }
    }

    @Override
    public void onDismiss() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        adPresenter.destroy();
        adPresenter = null;
    }
}
