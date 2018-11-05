package tv.newtv.cboxtv.cms.search.listener;

import tv.newtv.cboxtv.cms.search.fragment.BaseFragment;

/**
 * Created by linzy on 2018/10/26.
 */

public interface SearchResultDataInfo {
    void updateFragmentList(BaseFragment fragment, boolean isGone);
}
