package tv.newtv.cboxtv.cms.details.presenter.adpresenter;

import tv.newtv.cboxtv.utils.ADHelper;

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
//        void showAd(String imgUrl,String adType);
        void showAd(ADHelper.AD.ADItem item);
    }

    interface AdCommonConstractView<T>{

        void showAd(T adInfos);

        void fail();
    }
}
