package tv.newtv.cboxtv.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Map;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.model.BuyGoodsView;
import tv.newtv.cboxtv.utils.ScreenUtils;

public class BuyGoodsPopupWindow extends PopupWindow implements BuyGoodsView{

    private ImageView imageView;
    private TextView textView;
    private int x;
    private int y;
    private int width;
    private int height;

    public void show(Context context,View parent){
        View popView = LayoutInflater.from(context).inflate(R.layout.layout_ad_pop,null);
        imageView = popView.findViewById(R.id.image);
        textView = popView.findViewById(R.id.text);
        setContentView(popView);

        int width = this.width;
        int height = this.height;
        if(width <= 0){
            width = 300;
        }
        if(height <= 0){
            height = 150;
        }
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());

        if(x <=0 || y <= 0){
            showAtLocation(parent, Gravity.NO_GRAVITY,ScreenUtils.getScreenW() - width,
                    ScreenUtils.getScreenH() - height);
        } else {
          showAtLocation(parent,Gravity.NO_GRAVITY,x,y);
        }

    }

    @Override
    public void setImageUrl(String url){

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
}
