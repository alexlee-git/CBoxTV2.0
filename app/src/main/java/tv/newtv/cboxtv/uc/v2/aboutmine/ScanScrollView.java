package tv.newtv.cboxtv.uc.v2.aboutmine;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2.aboutmine
 * 类描述：实现滑动到底部的监听功能
 * 创建人：wqs
 * 创建时间：16:37
 * 创建日期：2018/9/5 0005
 * 修改人：
 * 修改时间：
 * 修改日期：2018/9/5 0005
 * 修改备注：
 */
public class ScanScrollView extends ScrollView {
    private boolean isScrolledToTop = true;// 初始化的时候设置一下值
    private boolean isScrolledToBottom = false;

    public ScanScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private IScanScrollChangedListener mIScanScrollChangedListener;

    /**
     * 定义监听滑动变化接口
     */
    public interface IScanScrollChangedListener {
        void onScrolledToBottom();

        void onScrolledChange();

        void onScrolledToTop();
    }

    public void setScanScrollChangedListener(IScanScrollChangedListener scanScrollChangedListener) {
        mIScanScrollChangedListener = scanScrollChangedListener;
    }

//    @Override
//    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
//        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
//        Log.e("scr", "----onOverScrolled");
//        if (scrollY == 0) {
//            isScrolledToTop = clampedY;
//            isScrolledToBottom = false;
//        } else {
//            isScrolledToTop = false;
//            isScrolledToBottom = clampedY;
//        }
//        notifyScrollChangedListeners();
//    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (getScrollY() == 0) {
            isScrolledToTop = true;
            isScrolledToBottom = false;
        } else if (getScrollY() + getHeight() - getPaddingTop() - getPaddingBottom() == getChildAt(0).getHeight()) {
            isScrolledToBottom = true;
            isScrolledToTop = false;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }
        notifyScrollChangedListeners();

    }

    private void notifyScrollChangedListeners() {
        if (isScrolledToTop) {
            if (mIScanScrollChangedListener != null) {
                mIScanScrollChangedListener.onScrolledToTop();
            }
        } else if (isScrolledToBottom) {
            if (mIScanScrollChangedListener != null) {
                mIScanScrollChangedListener.onScrolledToBottom();
            }
        } else {
            if (mIScanScrollChangedListener != null) {
                mIScanScrollChangedListener.onScrolledChange();
            }
        }
    }

    public boolean isScrolledToTop() {
        return isScrolledToTop;
    }

    public boolean isScrolledToBottom() {
        return isScrolledToBottom;
    }
}

