package tv.newtv.cboxtv.views.detailpage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import java.util.List;

import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:51
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class EpisodeAdapter extends FragmentStatePagerAdapter {

    private List<EpisodeFragment> mParams;

    EpisodeAdapter(FragmentManager fm, List<EpisodeFragment> params) {
        super(fm);
        mParams = params;
    }

    @Override
    public EpisodeFragment getItem(int position) {
        return mParams.get(position);
    }

    @Override
    public int getCount() {
        return mParams != null ? mParams.size() : 0;
    }
}
