package tv.newtv.cboxtv.views.detail;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:51
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class EpisodeAdapter extends FragmentStatePagerAdapter {

    private List<AbsEpisodeFragment> mParams;

    EpisodeAdapter(FragmentManager fm, List<AbsEpisodeFragment> params) {
        super(fm);
        mParams = params;
    }

    @Override
    public AbsEpisodeFragment getItem(int position) {
        return mParams.get(position);
    }

    @Override
    public int getCount() {
        return mParams != null ? mParams.size() : 0;
    }
}
