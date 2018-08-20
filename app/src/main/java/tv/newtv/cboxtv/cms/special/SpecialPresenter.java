package tv.newtv.cboxtv.cms.special;


import java.io.IOException;

import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.special.data.SpecialRepository;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.ModuleUtils;
import tv.newtv.cboxtv.cms.special.data.SpecialDataSource;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
//import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by lin on 2018/3/7.
 */

public class SpecialPresenter implements SpecialContract.Presenter {
    private static final String TAG = SpecialPresenter.class.getSimpleName();

    private SpecialRepository mSpecialRepository;
    private SpecialContract.View mSpecialView;

    public SpecialPresenter(SpecialRepository specialRepository,
                                SpecialContract.View specialView) {
        mSpecialRepository = checkNotNull(specialRepository);
        mSpecialView = checkNotNull(specialView);

        mSpecialView.setPresenter(this);
    }

    public void destroy(){
        mSpecialView = null;
        mSpecialRepository = null;
    }

    @Override
    public void getPageData(String appkey, String channelId, String pageUUID) {
        mSpecialRepository.getPageData(new SpecialDataSource.GetPageDataCallback() {
            @Override
            public void onDataLoaded(ResponseBody value) {
                // The view may not be able to handle UI updates anymore
                if(mSpecialView == null){
                    return;
                }
                if (!mSpecialView.isActive()) {
                    return;
                }

                String result = null;
                try {
                    result = value.string();
                } catch (IOException e) {
                    LogUtils.e(e);
                }

                LogUtils.i(TAG, "onDataLoaded: result = " + result);

                ModuleInfoResult moduleData = ModuleUtils.getInstance().parseJsonForModuleInfo(result);

                mSpecialView.showPageContent(moduleData);
            }

            @Override
            public void onDataNotAvailable() {
                if (mSpecialView != null) {
                    mSpecialView.showPageContent(null);
                }
            }
        }, appkey, channelId, pageUUID);
    }

    @Override
    public void start(String appkey, String channelId, String pageUUID) {
        getPageData(appkey, channelId, pageUUID);
    }

    @Override
    public void start() {

    }
}
