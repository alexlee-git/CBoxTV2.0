package tv.newtv.cboxtv.cms.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;

/**
 * Created by lixin on 2018/1/17.
 */

public class StaggeredAdapter extends FragmentStatePagerAdapter {


    private List<NavInfoResult.NavInfo> navInfoList;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);

        return fragment;
    }

    public StaggeredAdapter(FragmentManager fm, List<NavInfoResult.NavInfo> datas) {
        super(fm);
        this.navInfoList = datas;
    }

    @Override
    public Fragment getItem(int position) {
        NavInfoResult.NavInfo navInfo = navInfoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("nav_text", navInfo.getTitle());
        bundle.putString("content_id", navInfo.getContentID());

        BaseFragment fragment = ContentFragment.newInstance(bundle);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return navInfoList != null ? navInfoList.size(): 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
