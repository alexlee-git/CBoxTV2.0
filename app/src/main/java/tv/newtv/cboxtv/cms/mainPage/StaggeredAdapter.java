package tv.newtv.cboxtv.cms.mainPage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;

/**
 * Created by lixin on 2018/1/17.
 */

public class StaggeredAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mDatas;
    private FragmentManager fragmentManager;

    public StaggeredAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        return fragment;
    }

    public StaggeredAdapter(FragmentManager fm, List<BaseFragment> datas) {
        super(fm);
        fragmentManager = fm;
        this.mDatas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size(): 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
