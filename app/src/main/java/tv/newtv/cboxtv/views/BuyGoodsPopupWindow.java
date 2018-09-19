package tv.newtv.cboxtv.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.utils.ScreenUtils;

public class BuyGoodsPopupWindow extends PopupWindow{

    private ImageView imageView;

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

    }

}
