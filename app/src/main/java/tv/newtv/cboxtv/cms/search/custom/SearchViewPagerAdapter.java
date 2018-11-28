package tv.newtv.cboxtv.cms.search.custom;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import tv.newtv.cboxtv.cms.search.fragment.SearchBaseFragment;


public class SearchViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<SearchBaseFragment> mDatas;

    public SearchViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public SearchViewPagerAdapter(FragmentManager fm, List<SearchBaseFragment> datas) {
        super(fm);
        this.mDatas = datas;
    }

    public SearchBaseFragment getFragmentByIndex(int index){
        return this.mDatas.get(index);
    }

    public void  upData(List<SearchBaseFragment> datas){
        if (this.mDatas!=null){
            this.mDatas.clear();
            if (datas!=null){
                this.mDatas.addAll(datas);
                notifyDataSetChanged();
            }
        }

    }
    @Override
    public Fragment getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
