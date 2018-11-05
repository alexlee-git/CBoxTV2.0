package tv.newtv.cboxtv.cms.details.presenter.adpresenter;

import android.annotation.SuppressLint;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADHelper;

import tv.newtv.cboxtv.LauncherApplication;

/**
 * Created by Administrator on 2018/4/28.
 */

public class ADPresenter extends BaseRequestAdPresenter implements IAdConstract.IADPresenter{
    private static final String TAG = "ADPresenter";

    private IAdConstract.IADConstractView adConstractView;
    private ADHelper.AD mAD;
    private String flag;

    public ADPresenter(IAdConstract.IADConstractView adConstractView) {
        this.adConstractView = adConstractView;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getAD(final String adType, final String adLoc, final String flag) {
        this.flag = flag;
        super.getAD(adType,adLoc);
    }

    @Override
    public void dealResult(String result) {
        mAD = ADHelper.getInstance().parseADString
                (LauncherApplication.AppContext, result);

        if (adConstractView == null) {
            return;
        }
        if (mAD == null) {
            ADHelper.AD.ADItem adItem = new ADHelper.AD.ADItem(adType);
            adConstractView.showAd(adItem);
            return;
        }

        Log.e("AdHelper", "显示:" + mAD.toString());
        mAD.setCallback(new ADHelper.ADCallback() {
            @Override
            public void showAd(String type, String url) {
//                if(adConstractView == null) return;
//                    adConstractView.showAd(url, type);
            }

            @Override
            public void showAdItem(ADHelper.AD.ADItem adItem) {
                if(adConstractView != null){
                    adConstractView.showAd(adItem);
                }
            }

            @Override
            public void updateTime(int total, int left) {

            }

            @Override
            public void complete() {
                if (Constant.AD_DETAILPAGE_BANNER.equals(flag)) {
                    if(mAD != null) {
                        mAD.checkStart(false);
                    }
                }
            }
        }).start();
    }

    public void cancel() {
        if (mAD != null) {
            mAD.cancel();
        }
    }

    @Override
    public void destroy(){
        cancel();
        mAD = null;
        adConstractView = null;
    }


}
