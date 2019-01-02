package tv.newtv.cboxtv.views.detail;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.newtv.cms.bean.SubContent;

import java.util.List;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:51
 * 创建人:           weihaichao
 * 创建日期:          2018/5/3
 */
public class EpisodeAdapter extends FragmentStatePagerAdapter {

    private List<SubContent> data;
    private int pageSize;
    private boolean tvSeries;
    private ResizeViewPager listPager;
    private EpisodeChange change;
    private AbsEpisodeFragment currentFragment;

    EpisodeAdapter(FragmentManager fm, List<SubContent> data, int pageSize, boolean tvSeries, ResizeViewPager listPager, EpisodeChange change) {
        super(fm);
        this.data = data;
        this.pageSize = pageSize;
        this.tvSeries = tvSeries;
        this.listPager = listPager;
        this.change = change;
    }

    @Override
    public AbsEpisodeFragment getItem(int position) {
        int startNumber = position * pageSize;
        int endNumber = startNumber + pageSize;
        if (endNumber > data.size()) {
            endNumber = data.size();
        }
        AbsEpisodeFragment fragment = null;
        if (tvSeries) {
            fragment = new TvEpisodeFragment();
        } else {
            fragment = new SeriesEpisodeFragment();
        }
        fragment.setData(data.subList(startNumber, endNumber));
        fragment.setViewPager(listPager, position, change);
        return fragment;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (data != null && data.size() > 0) {
            count = data.size() % pageSize == 0 ? data.size() / pageSize : data.size() / pageSize + 1;
        }
        return count;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (AbsEpisodeFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    public AbsEpisodeFragment getCurrentFragment() {
        return currentFragment;
    }
}
