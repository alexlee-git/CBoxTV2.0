package tv.newtv.cboxtv.cms.search.listener;

import tv.newtv.cboxtv.cms.search.fragment.SearchBaseFragment;

/**
 * Created by linzy on 2018/10/26.
 */

public interface SearchResultDataInfo {
    void updateFragmentList(SearchBaseFragment fragment, boolean isGone,String desc);
}
