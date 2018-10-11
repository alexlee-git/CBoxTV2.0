package tv.newtv.contract;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IUpVersion;
import com.newtv.cms.bean.Oriented;
import com.newtv.cms.bean.UpVersion;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SystemUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import tv.newtv.cboxtv.BuildConfig;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         11:26
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class VersionUpdateContract {
    public interface View extends ICmsView {
        void versionCheckResult(@Nullable UpVersion versionBeen, boolean isForce);
    }

    public interface Presenter extends ICmsPresenter {
        void checkVerstionUpdate(Context context);
    }

    public static class UpdatePresenter extends CmsServicePresenter<View> implements Presenter {

        public UpdatePresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }

        @Override
        public void checkVerstionUpdate(final Context context) {
            IUpVersion upVersion = getService(SERVICE_UPVERSTION);
            if (upVersion != null) {
                final HashMap<String, String> params = createOrientedParam(context);
                upVersion.getIsOriented(params, new DataObserver<Oriented>() {
                    @Override
                    public void onResult(Oriented result) {
                        checkUpVersion(context, Integer.parseInt(params.get("versionCode")),
                                "enable".equals(result.getOriented()) ? params.get
                                        ("hardwareCode") : "");
                    }

                    @Override
                    public void onError(@Nullable String desc) {
                    }
                });
            }
        }

        public void checkUpVersion(final Context context, final int versionCode, String
                hardwareCode) {
            IUpVersion upVersion = getService(SERVICE_UPVERSTION);
            if (upVersion != null) {
                final HashMap<String, String> params = createUpVersionParam(context, versionCode,
                        hardwareCode);
                upVersion.getUpVersion(params, new DataObserver<UpVersion>() {
                    @Override
                    public void onResult(UpVersion result) {
                        if (TextUtils.isEmpty(result.getVersionCode()) || "null".equals(result
                                .getVersionCode())) {
                            return;
                        }
                        if (Integer.parseInt(result.getVersionCode()) > versionCode && !TextUtils
                                .isEmpty(result.getVersionName())) {
                            if (!TextUtils.isEmpty(result.getPackageMD5())) {
                                SharedPreferences mSharedPreferences = context.getSharedPreferences
                                        ("VersionMd5", Context.MODE_PRIVATE);
                                mSharedPreferences.edit().putString("versionmd5", result
                                        .getPackageMD5()).apply();
                            }
                            if ("1".equals(result.getUpgradeType())) {
                                //强制升级
                                getView().versionCheckResult(result, true);
                            } else {
                                //非强制升级
                                getView().versionCheckResult(result, false);
                            }
                        }
                    }

                    @Override
                    public void onError(@Nullable String desc) {
                    }
                });
            }
        }

        /**
         * @param context
         * @param versionCode
         * @param hardwareCode
         * @return
         */
        private HashMap<String, String> createUpVersionParam(Context context, int versionCode,
                                                             String hardwareCode) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("appKey", BuildConfig.APP_KEY);
            hashMap.put("channelCode", BuildConfig.CHANNEL_ID);
            hashMap.put("versionCode", Integer.toString(versionCode));
            if (!TextUtils.isEmpty(hardwareCode)) {
                hashMap.put("hardwareCode", "" + hardwareCode);
            }
            hashMap.put("uuid", Constant.UUID);
            hashMap.put("mac", SystemUtils.getMac(context));
            return hashMap;
        }

        /**
         * @param context
         * @return
         */
        private HashMap<String, String> createOrientedParam(Context context) {
            int versionCode = 0;
            final String hardwareCode = SystemUtils.getMac(context);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("appKey", BuildConfig.APP_KEY);
            hashMap.put("channelCode", BuildConfig.CHANNEL_ID);
            try {
                versionCode = context.getPackageManager().
                        getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                LogUtils.e(e.toString());
            }
            hashMap.put("versionCode", Integer.toString(versionCode));
            hashMap.put("uuid", Constant.UUID);
            hashMap.put("mac", hardwareCode);
            hashMap.put("hardwareCode", hardwareCode);
            return hashMap;
        }
    }
}
