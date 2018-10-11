package tv.newtv.contract;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IBootGuide;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.SPrefUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         13:26
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class SplashContract {
    public interface View extends ICmsView {
        void bootGuildResult();
    }

    public static class SplashPresenter extends CmsServicePresenter<View> {

        public SplashPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
            getBootGuide();
        }

        @SuppressWarnings("ConstantConditions")
        void getBootGuide() {
            if (!DeviceUtil.CBOXTEST.equals(tv.newtv.cboxtv.BuildConfig.FLAVOR)) {
                IBootGuide bootGuide = getService(SERVICE_BOOT_GUIDE);
                if (bootGuide != null) {
                    String platform = BuildConfig.APP_KEY + BuildConfig.CHANNEL_ID;
                    bootGuide.getBootGuide(platform, new DataObserver<String>() {
                        @Override
                        public void onResult(String result) {
                            String cacheValue = (String) SPrefUtils.getValue(getContext(),
                                    SPrefUtils.KEY_SERVER_ADDRESS, "");
                            if (!TextUtils.equals(cacheValue, result)) {
                                SPrefUtils.setValue(getContext(), SPrefUtils
                                        .KEY_SERVER_ADDRESS, result);
                                Constant.parseServerAddress(result);
                            }
                            getView().bootGuildResult();
                        }

                        @Override
                        public void onError(@Nullable String desc) {
                        }
                    });
                }
            } else {
                getView().bootGuildResult();
            }
        }
    }
}
