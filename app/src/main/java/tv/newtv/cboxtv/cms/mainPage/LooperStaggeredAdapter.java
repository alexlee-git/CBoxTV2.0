package tv.newtv.cboxtv.cms.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.mainPage.view.ContentFragment;


public class LooperStaggeredAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = LooperStaggeredAdapter.class.getSimpleName();
    private static final int DIR_LEFT = -1;
    private static final int DIR_RIGHT = 1;

    private int current;

    private List<NavListPageInfoResult.NavInfo> navInfoList;
    String parentId;

    private BaseFragment currentFragment;

    public LooperStaggeredAdapter(FragmentManager fm, List<NavListPageInfoResult.NavInfo> datas, String parentId) {
        super(fm);
        this.navInfoList = datas;
        this.parentId = parentId;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position % navInfoList.size());
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
        if (navInfoList.size() == 0) return;
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    private int getNextInt(int value, int dir) {
        int to = value;
        if (dir == DIR_RIGHT) {
            to = value + 1;
            if (to > navInfoList.size() - 1) {
                to = 0;
            }
        } else {
            to = value - 1;
            if (to < 0) {
                to = navInfoList.size() - 1;
            }
        }
        return to;
    }

    public void setShowItem(int position) {
        if (navInfoList.size() == 0) return;
        current = position % navInfoList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (navInfoList.size() == 0)
            return;

        int target = position % navInfoList.size();
        int PosA = getNextInt(current, DIR_LEFT);
        int PosB = getNextInt(current, DIR_RIGHT);
        //mDatas.get(target).destroyItem();

        Log.e(TAG, "check current=" + current + " PosA=" + PosA + " PosB=" + PosB + " " +
                "target=" + target);

        if (target == current || target == PosA || target == PosB) {
            return;
        }

        Log.e(TAG, "destroyItem index=" + target);
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (navInfoList.size() == 0) return null;
//        int pos = position % navInfoList.size();
        return (Fragment) super.instantiateItem(container, position);
    }

    @Override
    public Fragment getItem(int position) {
        int target = position % navInfoList.size();
        Fragment fragment = createContentFragment(navInfoList.get(target));
        return fragment;
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public int getCount() {
        return LooperUtil.MAX_VALUE;
    }

    private Fragment createContentFragment(NavListPageInfoResult.NavInfo navInfo) {
        Bundle bundle = new Bundle();
        bundle.putString("nav_text", navInfo.getTitle());
        bundle.putString("nav_parent_contentid", parentId);//上级导航的id
        bundle.putString("content_id", getContentUUID(navInfo));

        ContentFragment fragment = ContentFragment.newInstance(bundle);
        //fragment.setNotifyNoPageDataListener(this);
        fragment.setUseHint(true);
        //fragment.setViewPager(this);

        return fragment;
    }

    private String getContentUUID(NavListPageInfoResult.NavInfo navInfo) {
        String result = "";
        if (Constant.OPEN_PAGE.equals(navInfo.getActionType())) {
            result = navInfo.getContentID();
        } else if (Constant.OPEN_CHANNEL.equals(navInfo.getActionType())) {
            result = navInfo.getActionURI();
        } else {
            result = navInfo.getContentID();
        }

        if (result == null) {
            result = "";
        }

        return result;
    }
}
