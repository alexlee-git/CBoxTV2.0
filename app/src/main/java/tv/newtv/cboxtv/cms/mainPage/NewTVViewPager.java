package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.Scroller;

import com.newtv.libs.util.LogUtils;

import java.lang.reflect.Field;

/**
 * Created by lixin on 2018/3/17.
 */

public class NewTVViewPager extends ViewPager {
    public boolean mScrollable;

    public Scroller mScroller;

    public NewTVViewPager(Context context) {
        this(context, null);
    }

    public void destroy(){
        mScroller = null;
    }


    public NewTVViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean scrollable) {
        this.mScrollable = scrollable;
    }

    public boolean isScrolling(){
        return mScroller.computeScrollOffset();
    }

    public void setCustomScroller(Scroller scroller) {
        mScroller = scroller;

        Field fieldScroller;
        try {
            fieldScroller = ViewPager.class.getDeclaredField("mScroller");
            fieldScroller.setAccessible(true);
            fieldScroller.set(this, mScroller);
        } catch (NoSuchFieldException e) {
            LogUtils.e(e);
        } catch (IllegalAccessException e) {
            LogUtils.e(e);
        } catch (IllegalArgumentException e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (mScrollable) {
            super.scrollTo(x, y);
        }
    }
}
