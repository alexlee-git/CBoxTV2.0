package tv.newtv.cboxtv.cms.search.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;

import com.newtv.libs.Constant;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.custom.NewTVSearchHotRecommend;
import tv.newtv.cboxtv.cms.search.custom.NewTVSearchResult;
import tv.newtv.cboxtv.cms.search.custom.SearchViewKeyboard;
import tv.newtv.cboxtv.cms.search.listener.OnGetKeyListener;
import tv.newtv.cboxtv.cms.search.listener.OnReturnInputString;


/**
 * 类描述：搜索页面
 * 创建人：wqs
 * 创建时间： 2018/3/6 0006 15:24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class SearchActivity extends BaseActivity {

    private float SearchViewKeyboardWidth = 655;
    private boolean keyWordChange = false;

    private SearchViewKeyboard mSearchViewKeyboard;
    private NewTVSearchResult mSearchResult;
    private NewTVSearchHotRecommend mHotRecommend;
    private View mRelativeLayout;

    //监听输入框值变化
    private OnGetKeyListener onGetKeyListener = new OnGetKeyListener() {
        @Override
        public void notifyKeywords(String key) {
            try {
                LogUploadUtils.uploadLog(Constant.LOG_NODE_SEARCH, key);
                mSearchResult.setKey(key);
                if (!TextUtils.isEmpty(key)) {
                    mSearchResult.setVisibility(View.VISIBLE);
                    mHotRecommend.setVisibility(View.GONE);
                } else {
                    mSearchResult.setVisibility(View.GONE);
                    mHotRecommend.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                mSearchResult.setKey("");
            }
        }
    };
    private OnReturnInputString onReturnInputString = new OnReturnInputString() {
        @Override
        public void onReturnInputString(String inputStr) {
            if (onGetKeyListener != null) {
                onGetKeyListener.notifyKeywords(inputStr);
                keyWordChange = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        init();

    }

    //对象的初始化
    private void init() {
        SearchViewKeyboardWidth = DisplayUtils.translate((int) SearchViewKeyboardWidth, 0);
        mSearchViewKeyboard.setOnReturnInputString(onReturnInputString);
        setOnKeyListener(onGetKeyListener);
    }

    //控件初始化
    private void initView() {
        mSearchViewKeyboard = findViewById(R.id.search_view_keyboard);
        mSearchResult = findViewById(R.id.search_result);
        mHotRecommend = findViewById(R.id.search_hot_recommend);
        mRelativeLayout = findViewById(R.id.RelativeLayout);
    }

    public void setOnKeyListener(OnGetKeyListener onGetKeyListener) {
        this.onGetKeyListener = onGetKeyListener;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent
                .ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return true;
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent
                .KEYCODE_DPAD_CENTER) {
            return super.dispatchKeyEvent(event);
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            View focusView = getWindow().getDecorView().findFocus();
            View nextFocus = null;
            String mode = "keyboard";
            boolean check = false;
            if (mHotRecommend.hasFocus()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(mHotRecommend, focusView,
                            View.FOCUS_LEFT);
                    check = true;
                    mode = "search";
                }
            } else if (mSearchResult.hasFocus()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(mSearchResult, focusView,
                            View.FOCUS_LEFT);
                    check = true;
                    mode = "search";
                }
            } else if (mSearchViewKeyboard.hasFocus()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    nextFocus = FocusFinder.getInstance().findNextFocus(mSearchViewKeyboard,
                            focusView, View.FOCUS_RIGHT);
                    check = true;
                    mode = "keyboard";
                }
            }
            if (nextFocus == null && check) {
                if ("keyboard".equals(mode)) {
                    if (mHotRecommend.getVisibility() == View.GONE){
                        if (!mSearchResult.isLoadComplete()) {
                            return true;
                        }
                        if (mSearchResult.mFragments != null && mSearchResult.mFragments.size() > 0) {
                            slideView(mRelativeLayout, 0, -SearchViewKeyboardWidth, false);
                        }
                    }else {
                        slideView(mRelativeLayout, 0, -SearchViewKeyboardWidth, false);
                    }
                } else if ("search".equals(mode)) {
                    slideView(mRelativeLayout, -SearchViewKeyboardWidth, 0, true);
                }
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    //键盘移动的位移动画，动画类型为属性动画
    public void slideView(View view, final float p1, final float p2, final boolean isOpen) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", p1, p2);
        objectAnimator.setDuration(500);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                if (isOpen) {
                    //TODO keyboard request focus
                    mSearchViewKeyboard.getLastFocusView().requestFocus();
                    mSearchResult.showLeftBackView(false);
                    mSearchResult.resetLoadingLayout(false);
                } else {
                    //TODO search result view request focus
                    mSearchResult.showLeftBackView(true);
                    mSearchResult.resetLoadingLayout(true);
                    if (mSearchResult.getVisibility() == View.VISIBLE) {
                        if (!keyWordChange) {
                            mSearchResult.requestDefaultFocus();
                        } else {
                            mSearchResult.requestFirstTab();
                            keyWordChange = false;
                        }
                    } else if (mHotRecommend.getVisibility() == View.VISIBLE) {
                        mHotRecommend.requestFocus();
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        objectAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mHotRecommend != null){
            mHotRecommend.destroy();
            mHotRecommend = null;
        }
        if(mSearchViewKeyboard != null){
            mSearchViewKeyboard = null;
        }
        if(mSearchResult != null){
            if (mSearchResult.mColumnFragment != null){
                mSearchResult.mColumnFragment.onDestroyPresenter();
            }
            if (mSearchResult.mDramaFragment != null){
                mSearchResult.mDramaFragment.onDestroyPresenter();
            }
            if (mSearchResult.mPersonFragment != null){
                mSearchResult.mPersonFragment.onDestroyPresenter();
            }
            mSearchResult = null;
        }
        if(mRelativeLayout != null){
            mRelativeLayout = null;
        }

    }
}
