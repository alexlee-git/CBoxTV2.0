package tv.newtv.cboxtv.cms.mainPage.view;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.trello.rxlifecycle2.components.support.RxFragment;

import tv.newtv.cboxtv.BackGroundManager;
import tv.newtv.cboxtv.LauncherApplication;

/**
 * Created by lixin on 2018/1/23.
 */

public abstract class BaseFragment extends RxFragment {
    private static final String TAG = "BaseFragment";
    private static final int POST_DELAY = 500;
    protected boolean isVisible = false;
    private boolean useHint = false;
    private View contentView;

    protected abstract String getContentUUID();

    private Runnable lazyRunnable = new Runnable() {
        @Override
        public void run() {
            if(getUserVisibleHint()) {
                BackGroundManager.getInstance().setCurrentPageId(getContext(), getContentUUID(),
                        false,"",true);
            }
            lazyLoad();
        }
    };
    private RecyclerView animRecyclerView;
    private boolean isFinshAnim = true;

    protected boolean interruptKeyEvent(){
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getUserVisibleHint()){
            onVisible();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        animRecyclerView = null;
        if (contentView != null) {
            contentView.removeCallbacks(lazyRunnable);
            lazyRunnable = null;
            contentView = null;
        }
    }

    public void setUseHint(boolean value) {
        useHint = value;
    }

    public boolean isNoTopView() {
        return false;
    }

    public abstract View getFirstFocusView();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        contentView = view;
        super.onViewCreated(view, savedInstanceState);

//        if(getUserVisibleHint()){
            invokeLazyLoad();
//        }
    }

    private void invokeLazyLoad() {
        if (contentView != null) {
            contentView.postDelayed(lazyRunnable, POST_DELAY);
        }
    }

    private void clearLazyLoad() {
        if (contentView != null) {
            contentView.removeCallbacks(lazyRunnable);
        }
    }

    @Override
    public Context getContext() {
        Context context = super.getContext();
        if (context == null) {
            return LauncherApplication.AppContext;
        }
        return context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!useHint) {
            isVisible = true;
            onVisible();
        }
    }

    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (!useHint) {
            isVisible = false;
            onInvisible();
        }
    }

    public boolean isFragmentVisible() {
        return isVisible;
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    public void onEnterComplete() {

    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if (useHint) {
            isVisible = isVisibleToUser;
            if (isVisible) {
                onVisible();
            } else {
                onInvisible();
            }
        }
    }

    protected void onVisible() {

        invokeLazyLoad();
    }

    protected void lazyLoad() {

        Log.d(BaseFragment.class.getSimpleName(), "lazyload()");
    }

    protected void onInvisible() {
        clearLazyLoad();
    }

    public boolean dispatchKeyEvent(KeyEvent event) {

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //recyclerView已经滚动到底部 并且当前选中的是最底下的view  执行动画
                if (animRecyclerView != null && animRecyclerView.hasFocus() && event.getAction()
                        == KeyEvent.ACTION_DOWN) {
                    boolean canScrollVertically = animRecyclerView.canScrollVertically(1);
                    if (!canScrollVertically) {
                        View focusedChild = animRecyclerView.getFocusedChild();
                        if (focusedChild != null) {
                            int position = animRecyclerView.getChildAdapterPosition(focusedChild);
                            Log.i(TAG, "position = " + position + ",count=" + animRecyclerView
                                    .getAdapter().getItemCount());
                            if (++position == animRecyclerView.getAdapter().getItemCount()) {
                                startAnim();
                            }
                        }
                    }
                }
                break;
        }
        return false;
    }

    private void startAnim() {
        if (isFinshAnim) {
            isFinshAnim = false;
        } else {
            return;
        }
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(animRecyclerView, "translationY",
                0, -50, 0);
        objectAnimator.setDuration(400);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isFinshAnim = true;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isFinshAnim = false;
            }
        });
    }

    public void setAnimRecyclerView(RecyclerView recyclerView) {
        this.animRecyclerView = recyclerView;
    }

    public void destroyItem() {
    }
}
