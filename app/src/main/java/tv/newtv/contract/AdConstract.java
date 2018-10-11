package tv.newtv.contract;

import android.content.Context;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;

import org.jetbrains.annotations.NotNull;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.contract
 * 创建事件:         13:32
 * 创建人:           weihaichao
 * 创建日期:          2018/10/11
 */
public class AdConstract {
    public interface View extends ICmsView{

    }

    public interface Presenter extends ICmsPresenter{
        void getAd(String type);
    }

    public static class AdPresenter extends CmsServicePresenter<View> implements Presenter{

        public AdPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }


        @Override
        public void getAd(String type) {

        }
    }
}
