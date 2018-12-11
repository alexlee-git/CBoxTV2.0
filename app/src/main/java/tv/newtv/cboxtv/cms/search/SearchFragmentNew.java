package tv.newtv.cboxtv.cms.search;

import android.os.Bundle;
import android.text.TextUtils;

import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;

/**
 * Created by linzy on 2018/11/26.
 *
 * 搜索主页面重构
 */

public class SearchFragmentNew extends ContentFragment {

    public static SearchFragmentNew newInstance(Bundle paramBundle) {
        SearchFragmentNew fragment = new SearchFragmentNew();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

        if (TextUtils.isEmpty(contentId)){
            ArrayList<Page> pages = new ArrayList<>();
            Page searchPage = new Page(null,"","","","",
                    "","","search","search");
            pages.add(searchPage);
        }
    }

    @Override
    public void onPageResult(@NotNull ModelResult<ArrayList<Page>> page) {
        Page searchPage = new Page(null,"","","","",
                "","","search","search");
        if(page.getData() == null){
            page.setData(new ArrayList<>());
        }
        page.getData().add(0,searchPage);
        super.onPageResult(page);
    }
}
