package tv.newtv.cboxtv.views.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import tv.newtv.cboxtv.LauncherApplication;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         12:08
 * 创建人:           weihaichao
 * 创建日期:          2018/5/4
 */
public class ResizeViewPager extends ViewPager {

    private static final String TAG = ResizeViewPager.class.getSimpleName();
    int height = 0;
    int lastHeight = 0;
    int currentPage = 0;
    boolean userResize = true;
    private int margin;
    /**
     * 保存position与对于的View
     */
    private boolean scrollble = true;
    private int beforeIndex = 0;
    private OnPageChange mOnPageChange;

    public ResizeViewPager(Context context) {
        super(context);
        setClipChildren(false);
        setClipToPadding(false);
        margin = context.getResources().getDimensionPixelOffset(com.newtv.libs.R.dimen.height_10px);
    }

    public ResizeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void destroy() {
        mOnPageChange = null;
    }

    public void addOnPageChange(OnPageChange change) {
        mOnPageChange = change;
    }

    private @Nullable
    AbsEpisodeFragment getFragment(int index) {
        if (getAdapter() != null && getAdapter().getCount() > index && index >= 0) {
            return ((EpisodeAdapter) getAdapter()).getItem(index);
        }
        return null;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        int count = adapter.getCount();
        if (mOnPageChange != null) {
            mOnPageChange.onChange(0, 0, count);
        }
    }

    private int getFragmentSize() {
        if (getAdapter() != null) {
            return ((EpisodeAdapter) getAdapter()).getCount();
        }
        return 0;
    }

    public void requestDefaultFocus() {
        if(getCurrentFragment() != null){
            getCurrentFragment().requestDefaultFocus();
        }
    }

    private AbsEpisodeFragment getCurrentFragment(){
        if (getAdapter() != null) {
            return ((EpisodeAdapter) getAdapter()).getCurrentFragment();
        }
        return null;
    }

    public void setCurrentItem(final int item, final int selectIndex) {
        super.setCurrentItem(item);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                AbsEpisodeFragment fragment = getCurrentFragment();
                if (getFragmentSize() < item || fragment == null) return;
                currentPage = item;
                if (fragment.getCurrentIndex() != selectIndex) {
                    fragment.setSelectIndex(selectIndex);
                }
            }
        }, 1000);
    }

    private int getFocusIndex(View focusView) {
        if (focusView != null && focusView.getTag() != null) {
            String tag = (String) focusView.getTag();
            if (!TextUtils.isEmpty(tag) && tag.contains("cell_008_")) {
                String index = tag.substring("cell_008_".length());
                Toast.makeText(LauncherApplication.AppContext, "index:" + index,
                        Toast.LENGTH_SHORT).show();
            }
        }
        return -1;
    }

    /**
     * 最后一页数据如果为单行时候，是否重置高度为单行高度
     *
     * @param value
     */
    public void setUseResize(boolean value) {
        userResize = value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getAdapter() != null) {
            if (beforeIndex != getCurrentItem()) {
                if (mOnPageChange != null) {
                    mOnPageChange.onChange(beforeIndex, getCurrentItem(), getAdapter().getCount());
                }

                AbsEpisodeFragment episodeFragment = (AbsEpisodeFragment) (
                        (FragmentStatePagerAdapter)
                        getAdapter())
                        .getItem(getCurrentItem());

                if (getCurrentItem() > beforeIndex) {
                    //move to right
                    if (episodeFragment != null && this.hasFocus()) {
                        episodeFragment.requestFirst();
                    }

                } else {
                    //move to left
                    if (episodeFragment != null && this.hasFocus()) {
                        episodeFragment.requestLast();
                    }
                }
                beforeIndex = getCurrentItem();
            }

            int total = getAdapter().getCount();
            int size = getChildCount();
            for (int i = 0; i < size; i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h == 0) continue;
                if (h < height) {
                    lastHeight = h;
                } else {
                    height = h;
                }
            }

            if (total == 1) {
                lastHeight = height;
            } else if (lastHeight == 0) {
                lastHeight = height;
            }

            if (getCurrentItem() == getAdapter().getCount() - 1) {
//                heightMeasureSpec = MeasureSpec.makeMeasureSpec((userResize ? lastHeight : height)
//                                + margin,
//                        MeasureSpec.EXACTLY);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(lastHeight + margin,
                        MeasureSpec.EXACTLY);
            } else {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(height + margin,
                        MeasureSpec.EXACTLY);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollble) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public boolean isScrollble() {
        return scrollble;
    }

    public void setScrollble(boolean scrollble) {
        this.scrollble = scrollble;
    }

    public interface OnPageChange {
        void onChange(int prePage, int currentPage, int totalPage);
    }
}
