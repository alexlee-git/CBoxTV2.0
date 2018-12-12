package tv.newtv.cboxtv.cms.search.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.newtv.cms.bean.Page;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;
import tv.newtv.cboxtv.cms.special.util.ActivityUtils;

/**
 * Created by linzy on 2018/12/11.
 */

public class NewTVSearchHotRecommendNew extends FrameLayout {

    private FragmentManager mFragmentManager;

    public NewTVSearchHotRecommendNew(Context context) {
        this(context, null);
    }

    public NewTVSearchHotRecommendNew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewTVSearchHotRecommendNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public void setFragmentManager(FragmentManager fragmentManager){
        mFragmentManager = fragmentManager;
    }

    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.activity_hot_recommand_new_layout,this,true);
    }

    public void setUp(){
        String hotSearchId = Constant.getBaseUrl("HOTSEARCH_CONTENTID");
        if (!TextUtils.isEmpty(hotSearchId) && mFragmentManager != null){
            LogUtils.e("hotSearchId","hotSearchId : " + hotSearchId);
            Bundle bundle = new Bundle();
            bundle.putString("content_id",hotSearchId);
            ContentFragment fragment = ContentFragment.newInstance(bundle);
            fragment.setShowFirstTitle();
            fragment.setUseLoading(false);
            ActivityUtils.addFragmentToActivity(mFragmentManager,fragment,R.id.root_recommand);
        }
    }

    public void destroy(){
        mFragmentManager = null;
    }

}
