package tv.newtv.cboxtv.cms.details.presenter.adpresenter;

import android.view.View;
import android.widget.ImageView;

import tv.newtv.cboxtv.cms.ad.model.AdInfo;

/**
 * Created by Administrator on 2018/4/28.
 */

public interface IAdConstract {


    interface IADPresenter{
//        void getAD(String adType,String contentId);

        void getAD(String adType, String contentId,String adFlag);
        void destroy();
    }

    interface IADConstractView{

//        void showAd(String imgUrl);
        void showAd(String imgUrl,String adType);
    }
}
