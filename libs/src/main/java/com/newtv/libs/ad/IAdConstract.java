package com.newtv.libs.ad;


/**
 * Created by Administrator on 2018/4/28.
 */

public interface IAdConstract {


    interface IADPresenter{
//        void getAD(String adType,String contentId);

        void getAD(String adType, String contentId, String adFlag,String
                firstChannel, String secondChannel,String topicId);
        void destroy();
    }

    interface IADConstractView{

//        void showAd(String imgUrl);
//        void showAd(String imgUrl,String adType);
        void showAd(ADHelper.AD.ADItem item);
    }
}
