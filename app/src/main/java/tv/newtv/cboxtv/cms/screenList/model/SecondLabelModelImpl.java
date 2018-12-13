package tv.newtv.cboxtv.cms.screenList.model;

import android.content.Context;
import com.newtv.cms.bean.FilterItem;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.contract.FilterContract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;


/**
 * Created by 冯凯 on 2018/9/30.
 */

public class SecondLabelModelImpl implements SecondLabelModel, FilterContract.View {
    private FilterContract.FilterPresenter filterPresenter ;
    private SecondLabelCompleteListener completeListener;

    public SecondLabelModelImpl(Context context) {
        filterPresenter = new FilterContract.FilterPresenter(context,this);
    }

    @Override
        public void requestSecondLabel(String categoryId, final SecondLabelCompleteListener completeListener) {
            this.completeListener = completeListener;
            filterPresenter.getFilter(categoryId);
    }

    @Override
    public void onFilterResult(@NotNull Context context, @NotNull ModelResult<List<FilterItem>> result) {
        if (completeListener!=null&&result!=null){
            completeListener.sendSecondLabel(result);
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {

    }
}
