package tv.newtv.cboxtv.player.contract;

import android.content.Context;
import android.view.KeyEvent;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;

import org.jetbrains.annotations.NotNull;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.contract
 * 创建事件:         16:51
 * 创建人:           weihaichao
 * 创建日期:          2018/10/12
 */
public class PlayerContract {

    public interface View extends ICmsView{

    }

    public interface Presenter extends ICmsPresenter{
        void onKey(KeyEvent keyEvent);
    }

    public static class WidgetPresenter extends CmsServicePresenter<View> implements Presenter{

        public WidgetPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }

        @Override
        public void onKey(KeyEvent keyEvent) {

        }
    }
}
