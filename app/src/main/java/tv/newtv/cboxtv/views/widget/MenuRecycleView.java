package tv.newtv.cboxtv.views.widget;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.cms.details.ColumnPageActivity;
import tv.newtv.cboxtv.cms.mainPage.LooperUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv
 * 创建事件:         12:09
 * 创建人:           weihaichao
 * 创建日期:          2018/4/2
 */

public class MenuRecycleView extends RecyclerView {

    private static final String TAG = MenuRecycleView.class.getSimpleName();
    private static boolean eatKeyEvent = false;
//    private static List<NavInfoResult.NavInfo> mNavInfos;//一级导航数据
//    private static List<NavListPageInfoResult.NavInfo> mNavResultInfos;//二级导航数据
    public View mCurrentCenterChildView;
    private boolean mIsScrolling = false;
    //默认第一次选中第一个位置
    private int mCurrentFocusPosition = 0;
    private int mCircleOffset = 40;
    private MenuFactory mSelectedListener;
    private Boolean mAutoFit = false;
    private boolean TransPostion = true;
    private int menuSelectPos = 0;
    private View mFocusView;
    private long lastTime = 0;


    public void destroy(){
        if(getAdapter() != null && getAdapter() instanceof MenuAdapter){
            ((MenuAdapter) getAdapter()).destroy();
        }
        setAdapter(null);
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        mCurrentCenterChildView = null;
        mSelectedListener = null;
        mFocusView = null;
    }

    @SuppressWarnings("unchecked")
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 0x998:
                    int position = message.arg1;
                    if (mSelectedListener != null && menuSelectPos != position) {
                        menuSelectPos = position;
                        mSelectedListener.onItemSelected(position, message.obj);
                    }
                    break;
                case 0x997:
                    menuSelectPos = -1;
                    if (getAdapter() != null){
                        ((MenuAdapter) getAdapter()).requestFocus(message.arg1, (Boolean) message.obj);
                    }
                    break;

                case 0x996:
                    position = message.arg1;
                    if (mSelectedListener != null) {
                        mSelectedListener.focusChanged((Boolean) message.obj, position);
                    }

                    break;
            }
            return false;
        }
    });

    public MenuRecycleView(Context context) {
        super(context);
    }

    public MenuRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MenuRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setItemAnimator(null);
        this.setFocusable(false);
        setFocusableInTouchMode(false);

        setRecyclerListener(new RecyclerListener() {
            @Override
            public void onViewRecycled(ViewHolder holder) {
                Log.e(MenuRecycleView.class.getSimpleName(), "onViewRecycled:" + holder
                        .getAdapterPosition());
            }
        });
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            mIsScrolling = false;
        } else {
            mIsScrolling = true;
        }
    }

    @Override
    public boolean hasFocus() {
        if (mCurrentCenterChildView != null) {
            return mCurrentCenterChildView.hasFocus();
        }
        return super.hasFocus();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (getAdapter() != null) {
            requestDefaultFocus(false);
        }
    }

    public void requestDefaultFocus(boolean autoFocus) {
        if (isInEditMode()) return;
        if (getAdapter() != null) {
            ((MenuAdapter) getAdapter()).refreshDefaultPosition(autoFocus);
        }
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    public void setFocusView(View focusView) {
        mFocusView = focusView;
    }

    @Override
    public void requestChildFocus(View child, View focused) {

        if (mCurrentCenterChildView != null) {
            mCurrentCenterChildView.setTag(MenuAdapter.TAG_EMPTY);
        }

        mCurrentCenterChildView = child;
        mCurrentCenterChildView.setTag(MenuAdapter.TAG_CENTER);

        refreshFocusItemToCenter(child, false);

        super.requestChildFocus(child, focused);//执行过super.requestChildFocus之后hasFocus会变成true
        mCurrentFocusPosition = getChildViewHolder(child).getAdapterPosition();
    }

    @Override
    public void clearChildFocus(View child) {
        super.clearChildFocus(child);
    }

    public void setNeedTransPosition(boolean value) {
        TransPostion = value;
    }

    public Boolean getAutoFocus() {
        return mAutoFit;
    }

    public void setAutoFocus(Boolean auto) {
        mAutoFit = auto;
    }

    public void setMenuFactory(MenuFactory selectedListener) {
        mSelectedListener = selectedListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof MenuAdapter) {
            ((MenuAdapter) adapter).attachHandler(mHandler);
            ((MenuAdapter) adapter).attachFactory(mSelectedListener);
        }
    }

    public void refreshFocusItemToCenter(final View focusView, boolean autoFocus) {
        if (focusView == null) {
            return;
        }
        mCurrentCenterChildView = focusView;
        int[] tPosition = new int[2];
        focusView.getLocationInWindow(tPosition);
        int tDes = (int) ((this.getX() + this.getWidth() / 2) - focusView.getWidth() * focusView
                .getScaleX() / 2);

        if (!focusView.hasFocus() && autoFocus) {
            post(new Runnable() {
                @Override
                public void run() {
                    focusView.requestFocus(View.FOCUS_DOWN);
                }
            });
        }

        if (tPosition[0] != tDes) {
            this.smoothScrollBy(tPosition[0] - tDes, 0);
            postInvalidate();
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mIsScrolling) return true;
        if(event.getAction() == KeyEvent.ACTION_UP) return true;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER) {
                Log.e("Menu", "return super center back");
                return super.dispatchKeyEvent(event);
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mSelectedListener == null) return true;
                final View focusView = mSelectedListener.getNextFocusView();
                if (focusView != null) {
                    if (!(focusView instanceof RecyclerView)) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                focusView.requestFocus();
                            }
                        },200);
                        if (mFocusView != null) {
                            mFocusView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Log.e("Menu", "找不到下方移动的焦点");
                    return true;
                }
                Log.e("Menu", "return true");
                return true;
            }
        }
        Log.e("Menu", "return super other");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        refreshDataViewLocation();
    }

    public void refreshDataViewLocation() {
        int count = getChildCount();
        for (int index = 0; index < count; index++) {
            View view = getChildAt(index);
            resetViewLocation(view);
        }
    }

    public void resetViewLocation(View v) {
        float halfWidth = v.getWidth() * 0.5f;
        float parentHalfHeight = getWidth() * 0.5f;
        float y = v.getX();
        float rot = parentHalfHeight - halfWidth - y;

        float mTranslationRatio = 0.09f;
        float mDegToRad = 1.0f / 180.0f * (float) Math.PI;
        float pos = (float) (-Math.cos(rot * mTranslationRatio * mDegToRad) + 1);
        float sin = pos * getHeight() / 2;

        v.setTranslationY(sin);

        if (mCurrentCenterChildView == v) {
            v.setAlpha(1f);
//            v.setScaleX(1.3f);
//            v.setScaleY(1.3f);
        } else {
            v.setAlpha(0.7f - pos);
//            v.setScaleX(1);
//            v.setScaleY(1);
        }
    }

    public interface IHidden {
        boolean isHidden(int position);
    }

    public interface MenuFactory<T> {
        void onItemSelected(int position, T value);

        ViewHolder createViewHolder(ViewGroup parent, final int viewType);

        void onBindView(ViewHolder holder, T value, int position);

        boolean isDefaultTabItem(T value, int position);

        boolean autoRequestFocus();

        View getNextFocusView();

        void focusChanged(boolean hasFocus, int position);

    }

    public static abstract class MenuViewHolder extends ViewHolder {

        protected boolean isHidden = false;
        private IHidden mHiddenCallback;

        public MenuViewHolder(View itemView) {
            super(itemView);
        }

        public void setHiddenCallback(IHidden hiddenCallback) {
            mHiddenCallback = hiddenCallback;
        }

        void checkHidden() {
            if (mHiddenCallback != null) {
                isHidden = mHiddenCallback.isHidden(getAdapterPosition());
                setItemVisible(!isHidden);
            }
        }

        protected abstract void setItemVisible(boolean show);

    }

    public static class MenuAdapter<T> extends RecyclerView.Adapter<ViewHolder> implements IHidden {
        private static final String TAG_CENTER = "center";
        private static final String TAG_EMPTY = "";
        MenuScrollLinearLayoutManager linearLayoutManager;
        private List<T> menuItems;
        private WeakReference<MenuRecycleView> mRecyclerView;
        private int selectPos = 0;
        private int MiddleValue;
        private int mLayoutRes;
        private Handler mHandler;
        private MenuFactory mMenuFactory;
        private View currentFocus = null;
        private boolean isInitalized = true;
        private Interpolator mSpringInterpolator;
        private AnimatorSet mScaleAnimator;

        public void destroy(){
            mMenuFactory = null;
            if(mHandler != null){
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            if(mRecyclerView != null) {
                mRecyclerView.clear();
                mRecyclerView = null;
            }
            if(menuItems != null){
                menuItems.clear();
                menuItems = null;
            }
            if(linearLayoutManager != null) {
                linearLayoutManager.destroy();
                linearLayoutManager = null;
            }

        }

        public MenuAdapter(Context context, MenuRecycleView recyclerView, int layout, boolean
                autoResize) {

            mSpringInterpolator = new OvershootInterpolator(2.2f);
            mRecyclerView = new WeakReference<MenuRecycleView>(recyclerView);
            mLayoutRes = layout;

            linearLayoutManager = new MenuScrollLinearLayoutManager(recyclerView.getContext());
            linearLayoutManager.setAutoResize(autoResize);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (menuItems == null) return;
                        sendFocusToHandler(MenuAdapter.this.selectPos);
                        Log.e("logsdk", "state,,,,selectPos=" + MenuAdapter.this.selectPos);
                    }
                }
            });
        }

        void attachFactory(MenuFactory menuFactory) {
            mMenuFactory = menuFactory;
        }

        void attachHandler(Handler handler) {
            mHandler = handler;
        }

        public void setMenuItems(final List<T> values, final int defaultIndex) {
            setMenuItems(values, defaultIndex, values.size());
        }

        public void setMenuItems(final List<T> values, final int defaultIndex, int displaySize) {
            if (menuItems == null) {
                menuItems = new ArrayList<>();
            }
            menuItems.clear();
            menuItems.addAll(values);

            isInitalized = true;

            MiddleValue = LooperUtil.getMiddleValue(menuItems.size());
            selectPos = defaultIndex + MiddleValue;
            Log.e("logsdk", selectPos + "-----");
            notifyDataSetChanged();

            refreshPosition(selectPos, false);
            linearLayoutManager.setDataSize(displaySize);
        }


        public void refreshDefaultPosition(boolean autoFocus) {
            refreshPosition(selectPos, autoFocus);
        }

        void refreshPosition(int position, boolean autoFocus) {
            int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();

            if (position < first || position > last) {
                mRecyclerView.get().scrollToPosition(position);
            }

            if (mHandler != null) {
                mHandler.removeMessages(0x997);
                Message message = mHandler.obtainMessage();
                message.what = 0x997;
                message.arg1 = position;
                message.obj = autoFocus;
                mHandler.sendMessageDelayed(message, 100);
            }
        }

        public void requestFocus(int position, boolean autoFocus) {
            int first = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();

            if (first == position) {
                mRecyclerView.get().refreshFocusItemToCenter(mRecyclerView.get().getChildAt(0),
                        autoFocus);
            } else if (position > first && position <= last) {
                int index = position - first + 1;
                mRecyclerView.get().refreshFocusItemToCenter(mRecyclerView.get().getChildAt
                        (index), autoFocus);
            } else {
                Log.e(MenuRecycleView.class.getSimpleName(), "未找到匹配的Position");
            }
        }

        private void sendFocusToHandler(int position) {
            if (position == -1) {
                return;
            }
            Message message = mHandler.obtainMessage();
            message.what = 0x998;
            int to = mRecyclerView.get().TransPostion ? position % menuItems.size() : position;
            message.arg1 = to;
            message.obj = menuItems.get(mRecyclerView.get().TransPostion ? to : to % menuItems
                    .size());
            mHandler.sendMessage(message);
        }

        //焦点变化
        private void sendFocusChangeToHandler(boolean hasFocus, int position) {
            Message message = mHandler.obtainMessage();
            message.what = 0x996;

            message.arg1 = position % menuItems.size();
            message.obj = hasFocus;
            Log.e("logsdk", "selectPos=" + position + ",  arg1=" + message.arg1);
            mHandler.sendMessage(message);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            final ViewHolder viewHolder = mMenuFactory.createViewHolder(parent, viewType);
            viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    mRecyclerView.get().mFocusView.setVisibility(hasFocus ? View.VISIBLE : View
                            .GONE);
                    sendFocusChangeToHandler(hasFocus, viewHolder.getAdapterPosition());//焦点变化，上传日志
                    focusChange(view, hasFocus, viewHolder);
                }
            });
            viewHolder.itemView.setFocusable(true);
            if (viewHolder instanceof MenuViewHolder) {
                ((MenuViewHolder) viewHolder).setHiddenCallback(this);
            }

            return viewHolder;
        }


        private void focusChange(View view, boolean hasFocus, ViewHolder viewHolder) {
//            int position = viewHolder.getPosition();
            if (hasFocus) {
//                if(menuItems.size()==mNavInfos.size()){
//                    Log.e("gao","一级导航：get foces："+mNavInfos.get(position%menuItems.size())
// .getIcon1());
//                    ((RecycleImageView)viewHolder.itemView.findViewById(R.id.title_icon_nav))
// .load(mNavInfos.get(position%menuItems.size()).getIcon1());
//                }
//                if(menuItems.size()==mNavResultInfos.size()){
//                    Log.e("gao","二级导航：get foces："+mNavResultInfos.get(position%menuItems.size()
// ).getIcon1());
//                    ((RecycleImageView)viewHolder.itemView.findViewById(R.id.title_icon_list))
// .load(mNavResultInfos.get(position%menuItems.size()).getIcon1());
//                }
                selectPos = viewHolder.getAdapterPosition();
                view.bringToFront();
//                selectPos = viewHolder.getAdapterPosition();
                currentFocus = view;
                view.setAlpha(1f);

                //直接放大view
                ScaleAnimation sa = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f, Animation
                        .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setFillAfter(true);
                sa.setDuration(400);
                sa.setInterpolator(mSpringInterpolator);
                view.bringToFront();
                view.startAnimation(sa);
            } else {
//                if(menuItems.size()==mNavInfos.size()){
//                    Log.e("gao","一级导航：失去foces："+mNavInfos.get(position%menuItems.size())
// .getIcon());
//                    ((RecycleImageView)viewHolder.itemView.findViewById(R.id.title_icon_nav))
// .load(mNavInfos.get(position%menuItems.size()).getIcon());
//                }
//                if(menuItems.size()==mNavResultInfos.size()){
//                    Log.e("gao","二级导航：失去foces："+mNavResultInfos.get(position%menuItems.size())
// .getIcon());
//                    ((RecycleImageView)viewHolder.itemView.findViewById(R.id.title_icon_list))
// .load(mNavResultInfos.get(position%menuItems.size()).getIcon());
//                }

                viewHolder.itemView.setAlpha(1);
                // 直接缩小view
                ScaleAnimation sa = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f, Animation
                        .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setFillAfter(true);
                sa.setDuration(400);
                sa.setInterpolator(mSpringInterpolator);
                view.startAnimation(sa);
            }

//            if (mScaleAnimator != null) mScaleAnimator.end();
//            if (hasFocus) {
//                view.bringToFront();
//                selectPos = viewHolder.getAdapterPosition();
//                currentFocus = view;
//                view.setAlpha(1f);
//                ObjectAnimator animX = ObjectAnimator.ofFloat(view, "ScaleX",
//                        new float[]{1.0F, 1.3F}).setDuration(400);
//                ObjectAnimator animY = ObjectAnimator.ofFloat(view, "ScaleY",
//                        new float[]{1.0F, 1.3F}).setDuration(400);
//                mScaleAnimator = new AnimatorSet();
//                mScaleAnimator.playTogether(new Animator[]{animX, animY});
//                mScaleAnimator.setInterpolator(mSpringInterpolator);
//                mScaleAnimator.start();
//
//            } else {
//                viewHolder.itemView.setAlpha(1);
////                view.setScaleX(1.0f);
////                view.setScaleY(1.0f);
//                ObjectAnimator animX = ObjectAnimator.ofFloat(view, "ScaleX",
//                        new float[]{1.3F, 1.0F}).setDuration(400);
//                ObjectAnimator animY = ObjectAnimator.ofFloat(view, "ScaleY",
//                        new float[]{1.3F, 1.0F}).setDuration(400);
//                mScaleAnimator = new AnimatorSet();
//                mScaleAnimator.playTogether(new Animator[]{animX, animY});
//                mScaleAnimator.setInterpolator(mSpringInterpolator);
//                mScaleAnimator.start();
//            }

        }

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(final ViewHolder holder, int pos) {
            int position = holder.getAdapterPosition();
            if (menuItems == null) return;
            T menuItem = menuItems.get(position % menuItems.size());
            if (isInitalized) {
                if (mMenuFactory.isDefaultTabItem(menuItem, holder.getAdapterPosition()) &&
                        holder.getAdapterPosition() >= MiddleValue) {
                    selectPos = position;
                    if (mMenuFactory.autoRequestFocus()) {
                        holder.itemView.requestFocus();
                    }
                    isInitalized = false;
                }
            }
            if (menuItem != null) {
                mMenuFactory.onBindView(holder, menuItem, position);
            }

            if(mRecyclerView != null && mRecyclerView.get() != null && mRecyclerView.get()
                    .getChildCount() > 0) {
                int count = mRecyclerView.get().getChildCount();
                for (int index = 0; index < count; index++) {
                    View target = mRecyclerView.get().getChildAt(index);
                    linearLayoutManager.changeHolderList(mRecyclerView.get().getChildViewHolder(target));

                }
            }
        }

        @Override
        public int getItemCount() {
            return LooperUtil.MAX_VALUE;
        }

        @Override
        public boolean isHidden(int position) {
            return linearLayoutManager.isHidden(selectPos, position);
        }
    }

//    public void setOneMenuData(List<NavInfoResult.NavInfo> mNavInfos){
//        this.mNavInfos=mNavInfos;
//    }

//    public void setTwoMenuData(List<NavListPageInfoResult.NavInfo> mNavResultInfos){
//        this.mNavResultInfos=mNavResultInfos;
//    }

}
