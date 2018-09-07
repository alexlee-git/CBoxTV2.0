package tv.newtv.cboxtv.views;

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

import com.squareup.picasso.Picasso;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.utils.ADHelper;
import tv.newtv.cboxtv.utils.ScreenUtils;

public class AdPopupWindow extends PopupWindow implements IAdConstract.IADConstractView {
    private ADPresenter adPresenter;
    private ImageView imageView;
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
        adPresenter.getAD(Constant.AD_DESK,Constant.AD_GLOBAL_POPUP,"");
    }

    @Override
    public void showAd(ADHelper.AD.ADItem item) {
        if(!TextUtils.isEmpty(item.AdUrl) &&item.PlayTime > 0){
            Picasso.get().load(item.AdUrl).into(imageView);
            handler.sendEmptyMessageDelayed(0,item.PlayTime * 1000);
        }else {
            dismiss();
        }
    }
}
