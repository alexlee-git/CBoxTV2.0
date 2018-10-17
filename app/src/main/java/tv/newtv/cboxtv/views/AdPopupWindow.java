package tv.newtv.cboxtv.views;

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

import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.R;
import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.util.ScreenUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;


public class AdPopupWindow extends PopupWindow implements AdContract.View, PopupWindow
        .OnDismissListener {
    private AdContract.Presenter adPresenter;
    private ImageView imageView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    public void show(Context context, View parent) {
        View popView = LayoutInflater.from(context).inflate(R.layout.layout_ad_pop, null);
        imageView = popView.findViewById(R.id.image);
        setContentView(popView);

        int width = 300;
        int height = 150;
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        showAtLocation(parent, Gravity.NO_GRAVITY, ScreenUtils.getScreenW() - width,
                ScreenUtils.getScreenH() - height);

        adPresenter = new AdContract.AdPresenter(context, this);
        adPresenter.getAdByType(Constant.AD_DESK, Constant.AD_GLOBAL_POPUP, "", null);

        setOnDismissListener(this);
    }

    @Override
    public void onDismiss() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        if(adPresenter != null) {
            adPresenter.destroy();
            adPresenter = null;
        }
    }

    @Override
    public void showAd(@Nullable String type, @Nullable String url, @Nullable HashMap<?, ?>
            hashMap) {
        ADHelper.AD.ADItem item = adPresenter.getAdItem();
        if (item == null) return;
        if (item.PlayTime <= 0) {
            item.PlayTime = 5;
        }
        if (!TextUtils.isEmpty(item.AdUrl)) {
            Picasso.get().load(item.AdUrl).into(imageView);
            handler.sendEmptyMessageDelayed(0, item.PlayTime * 1000);
        } else {
            dismiss();
        }
    }

    @Override
    public void updateTime(int total, int left) {

    }

    @Override
    public void complete() {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }
}
