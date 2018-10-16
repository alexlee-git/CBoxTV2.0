package tv.newtv.cboxtv.cms.ad.model;

import android.content.Context;
import android.view.View;

import java.util.Map;

public interface BuyGoodsView {

    void show(Context context, View parent);

    void dismiss();

    void setParamsMap(Map<String,String> map);

    void setImageUrl(String url);

    void setName(String name);

    void showQrCode(String authCode);

    boolean isShow();
}
