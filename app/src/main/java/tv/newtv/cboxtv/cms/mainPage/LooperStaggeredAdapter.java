package tv.newtv.cboxtv.cms.mainPage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;

/**
 * Created by lixin on 2018/1/17.
 */

public class LooperStaggeredAdapter extends FragmentPagerAdapter {


    private static final String TAG = LooperStaggeredAdapter.class.getSimpleName();
    private static final int DIR_LEFT = -1;
    private static final int DIR_RIGHT = 1;
    private List<BaseFragment> mDatas;
    private int current;

    public LooperStaggeredAdapter(FragmentManager fm) {
        super(fm);
    }

    public LooperStaggeredAdapter(FragmentManager fm, List<BaseFragment> datas) {
        super(fm);
        this.mDatas = datas;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position % mDatas.size());
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mDatas.size() == 0) return;
        super.setPrimaryItem(container, position % mDatas.size(), object);
    }

    private int getNextInt(int value, int dir) {
        int to = value;
        if (dir == DIR_RIGHT) {
            to = value + 1;
            if (to > mDatas.size() - 1) {
                to = 0;
            }
        } else {
            to = value - 1;
            if (to < 0) {
                to = mDatas.size() - 1;
            }
        }
        return to;
    }

    public void setShowItem(int position) {
        if (mDatas.size() == 0) return;
        current = position % mDatas.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mDatas.size() == 0)
            return;

        int target = position % mDatas.size();
        int PosA = getNextInt(current, DIR_LEFT);
        int PosB = getNextInt(current, DIR_RIGHT);

        Log.e(TAG, "check current=" + current + " PosA=" + PosA + " PosB=" + PosB + " " +
                "target=" + target);

        if (target == current || target == PosA || target == PosB) {
            return;
        }

        Log.e(TAG, "destroyItem index=" + target);
        super.destroyItem(container, target, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mDatas.size() == 0) return null;
        int pos = position % mDatas.size();
        return (Fragment) super.instantiateItem(container, pos);
    }

    @Override
    public Fragment getItem(int position) {
        int target = position % mDatas.size();
        return mDatas.get(target);
    }

    @Override
    public int getCount() {
        return LooperUtil.MAX_VALUE;
    }
}
