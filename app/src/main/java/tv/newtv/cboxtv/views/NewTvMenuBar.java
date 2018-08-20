package tv.newtv.cboxtv.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import java.util.List;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv
 * 创建事件:         13:33
 * 创建人:           weihaichao
 * 创建日期:          2018/4/18
 */
@SuppressWarnings({"unchecked", "unused"})
public class NewTvMenuBar extends RecyclerView implements NewTvMenuAdapter.onFocusChange {

    private View currentFocus;
    private ScrollSpeedLinearLayoutManger linearLayoutManager;
    private OnFocusChange mOnFocusChangeListener;

    public NewTvMenuBar(Context context) {
        super(context);
    }

    public NewTvMenuBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NewTvMenuBar(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSelection(int position) {
        moveToPosition(position);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        currentFocus = child;
    }

    private void moveToPosition(int position) {
        int toPosition = position;
        if (mOnFocusChangeListener != null && !mOnFocusChangeListener.TransPos) {
            toPosition = getAdapter().getItemCount() / 2 + position;
        }
        scrollToPosition(toPosition);

        final int finalToPosition = toPosition;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (first == finalToPosition) {
                    refreshFocusItemToCenter(NewTvMenuBar.this, getChildAt(0));
                } else if (first < finalToPosition && finalToPosition <= last) {
                    int index = finalToPosition - first + 1;
                    refreshFocusItemToCenter(NewTvMenuBar.this, getChildAt(index));
                }
            }
        },100);

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {

        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        validateLocation();
    }

    private void validateLocation() {
        int count = getChildCount();
        int mid = count / 2;
        for (int index = 0; index < count; index++) {
            resetViewLocation(getChildAt(index));
        }
    }

    public void resetViewLocation(View v) {
//        float halfWidth = v.getWidth() * 0.5f;
//        float parentHalfHeight = getWidth() * 0.5f;
//        float y = v.getX();
//        float rot = parentHalfHeight - halfWidth - y;
//
//        float mTranslationRatio = 0.09f;
//        float mDegToRad = 1.0f / 180.0f * (float) Math.PI;
//        float pos = (float) (-Math.cos(rot * mTranslationRatio * mDegToRad) + 1);
//        float sin = pos * getHeight() / 2;
////        v.setRotation(pos * 5 * (v.getX() > parentHalfHeight ? 1 : -1));
//        v.setTranslationY(sin);
//
//        v.setAlpha(0.7f - pos);

    }

    public void refreshFocusItemToCenter(RecyclerView recyclerView, View focusView) {
        if (focusView == null) {
            return;
        }
        int[] tPosition = new int[2];
        focusView.getLocationInWindow(tPosition);
        int tDes = (int) ((recyclerView.getX() + recyclerView.getWidth() / 2) - focusView
                .getWidth() * focusView
                .getScaleX() / 2);
        currentFocus = focusView;
        if (tPosition[0] != tDes) {
            recyclerView.smoothScrollBy(tPosition[0] - tDes, 0);
            recyclerView.postInvalidate();
        }

        focusView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                View view = FocusFinder.getInstance().findNextFocus(this, currentFocus,
                        View.FOCUS_LEFT);
                if (view != null) {
                    view.requestFocus();
                    refreshFocusItemToCenter(this, view);
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View view = FocusFinder.getInstance().findNextFocus(this, currentFocus,
                        View.FOCUS_RIGHT);
                if (view != null) {
                    view.requestFocus();
                    refreshFocusItemToCenter(this, view);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public <T extends RecyclerView.ViewHolder, W> void build(int layoutId, NewTvMenuAdapter
            .MenuCreator<T, W> creator) {
        linearLayoutManager = new ScrollSpeedLinearLayoutManger(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(linearLayoutManager);
        NewTvMenuAdapter<T, W> adapter = new NewTvMenuAdapter<T, W>(getContext(), creator,
                layoutId);
        adapter.setFocusChange(this);
        setAdapter(adapter);
    }


    public <T> void setData(List<T> values, int defaultIndex) {
        if (getAdapter() != null && getAdapter() instanceof NewTvMenuAdapter) {
            ((NewTvMenuAdapter) getAdapter()).setData(values);
            setSelection(defaultIndex);
            validateLocation();
        }
    }

    @Override
    public void onFocus(View target, boolean hasFocus, int position) {
        if (hasFocus && mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onChange(mOnFocusChangeListener.TransPos ? position % (
                    (NewTvMenuAdapter<ViewHolder, Object>)
                            getAdapter()).getDataSize()
                    : position);
        }
        if (!hasFocus) {
            target.animate().scaleX(1).scaleY(1).setDuration(200).start();
        }
    }

    public void setOnFocusListener(OnFocusChange onFocusListener, boolean transPos) {
        onFocusListener.TransPos = transPos;
        mOnFocusChangeListener = onFocusListener;
    }

    public abstract static class OnFocusChange {
        boolean TransPos = false;

        public abstract void onChange(int position);
    }
}
