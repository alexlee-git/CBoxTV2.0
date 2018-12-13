package tv.newtv.cboxtv.cms.screenList.model;



import android.content.Context;

import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.contract.CategoryContract;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * Created by 冯凯 on 2018/9/30.
 */
public class FirstLabelModelImpl implements FirstLabelModel  ,CategoryContract.View{
    private CategoryContract.CategoryPresenter  categoryPresenter;
    private FirstLabelCompleteListener completeListener;

    public FirstLabelModelImpl(Context context) {
        categoryPresenter  = new CategoryContract.CategoryPresenter(context ,this);
    }

    @Override
    public void requestFirstLabel(final FirstLabelCompleteListener completeListener) {
        this.completeListener =  completeListener;
        categoryPresenter.getCategory();
    }

    @Override
    public void onCategoryResult(@NotNull Context context, @NotNull ModelResult<List<CategoryTreeNode>> result) {
        if (completeListener!=null&& result!=null){
            completeListener.sendFirstLabel(result);
        }

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {

    }
}
