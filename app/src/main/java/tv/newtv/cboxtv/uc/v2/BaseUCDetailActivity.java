package tv.newtv.cboxtv.uc.v2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;


/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:30
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
@SuppressLint("Registered")
public abstract class BaseUCDetailActivity<T> extends BaseActivity implements CompoundButton
        .OnCheckedChangeListener, DetailCallback<T> {
    public static final int DETAIL_TYPE_NOTHING = -1;
    public static final int DETAIL_TYPE_HISTORY = 0;//历史记录
    public static final int DETAIL_TYPE_COLLECTION = 1;//收藏
    public static final int DETAIL_TYPE_ATTENTION = 2;//关注
    public static final int DETAIL_TYPE_SUBSCRIBE = 3;//订阅
    public static final int DETAIL_TYPE_MINE = 4;//关于我的
    private boolean mReady = false;
    private UCDetailPresenter<T> mPresenter;
    private TextView emptyTextView;
    private Fragment currentFragment;

    protected ImageView operationIcon;
    protected TextView operationText;

    private View defaultTab;
    private RadioGroup radioGroup;
    private int positionItem = 5;

    protected boolean getUsePresenter() {
        return true;
    }

    protected boolean requestDefaultTab() {
        if (radioGroup != null && radioGroup.getChildCount() > 0) {
            if (defaultTab != null) {
                defaultTab.requestFocus();
                return true;
            }
            defaultTab = radioGroup.getChildAt(0);
            defaultTab.requestFocus();
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        mReady = true;

        operationIcon = findViewById(R.id.id_operation_icon);
        operationText = findViewById(R.id.id_operation_tip);

        TextView titleTextView = findViewById(R.id.user_info_title);
        if (titleTextView != null) {
            if (!TextUtils.isEmpty(getHeadTitle())) {
                titleTextView.setText(getHeadTitle());
            } else {
                titleTextView.setVisibility(View.GONE);
            }
        }

        radioGroup = findViewById(R.id.menu_group);
        if (radioGroup != null) {
            String[] tabList = getTabList();
            if (tabList != null && tabList.length > 0) {
                int index = 0;
                for (String title : tabList) {
                    BottomLineRadioButton radioButton = (BottomLineRadioButton) LayoutInflater
                            .from(getApplicationContext
                                    ()).inflate(R.layout.userinfo_tab_item, radioGroup, false);
                    radioButton.setText(title);
                    radioButton.setId(radioGroup.getChildCount());
                    radioButton.setTag(String.format(Locale.getDefault(), "TAB_%d", index));
                    radioGroup.addView(radioButton);
                    radioButton.setOnCheckedChangeListener(this);
                    index++;
                }
            }
        }
        updateWidgets();
        if (getUsePresenter()) {
            mPresenter = new UCDetailPresenter<T>(getDetailType(), this);
            mPresenter.requestData();
        }
    }

    protected void ShowFragment(Class fragmentCls, int containerId) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentCls.getCanonicalName());
        try {
            if (fragment == null) {
                fragment = (Fragment) fragmentCls.newInstance();
            }
            FragmentTransaction transaction = manager.beginTransaction();
            if (!fragment.isAdded()) {
                transaction.add(containerId, fragment, fragmentCls.getCanonicalName());
            }
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.show(fragment);
            currentFragment = fragment;
            transaction.commitAllowingStateLoss();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(@NotNull List<T> results) {
//        View emptyView = findViewById(R.id.empty_container);
//        if (emptyView != null) {
//            if (emptyTextView == null) {
//                emptyTextView = emptyView.findViewById(R.id.empty_textview);
//            }
//            if (results.size() == 0 && !TextUtils.isEmpty(getEmptyTipMessage())) {
//                emptyTextView.setText(getEmptyTipMessage());
//                emptyTextView.setVisibility(View.VISIBLE);
//            } else {
//                emptyTextView.setVisibility(View.GONE);
//            }
//        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            defaultTab = buttonView;
            buttonView.setTextColor(Color.parseColor("#FFFFFFFF"));
            onTabChange(buttonView.getText().toString(), buttonView);
        } else {
            buttonView.setTextColor(Color.parseColor("#CCFFFFFF"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReady && mPresenter != null) {
            mPresenter.requestData();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPresenter != null) {
            mPresenter.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    protected abstract String[] getTabList();

    protected abstract String getHeadTitle();

    protected abstract int getLayoutID();

    protected abstract void updateWidgets();

    protected abstract String getEmptyTipMessage();

    protected abstract void onTabChange(String title, View view);

    protected @DetailType
    abstract int getDetailType();

    @SuppressWarnings("PointlessNullCheck")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        View focusView = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (focusView != null) {
                    ViewParent focusParent = focusView.getParent();
                    if (focusParent != null && focusParent instanceof RecyclerView) {
                        View view = ((RecyclerView) focusParent).findFocus();
                        if (view != null) {
                            int position = ((RecyclerView) focusParent).getChildAdapterPosition(view);
                            Log.i("---", "dispatchKeyEvent: positionItem" + positionItem);
                            Log.i("---", "dispatchKeyEvent: position" + position);
                            if (radioGroup != null && radioGroup.getChildCount() > 0) {
                                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(radioGroup.getCheckedRadioButtonId());
                                if (radioButton != null && TextUtils.equals(radioButton.getText(), getResources().getString(R.string.collect_lb))) {
                                    positionItem = 3;
                                } else {
                                    positionItem = 5;
                                }
                                if (position > positionItem) {
                                    return super.dispatchKeyEvent(event);
                                } else {
                                    View next = FocusFinder.getInstance().findNextFocus(
                                            (ViewGroup) this.getWindow().getDecorView(),
                                            focusView,
                                            View.FOCUS_UP);
                                    if (next != null && next instanceof RadioButton && next.getParent() instanceof
                                            RadioGroup) {
                                        if (!radioGroup.hasFocus()) {
                                            if (requestDefaultTab()) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (focusView != null && focusView instanceof RadioButton && focusView.getParent() instanceof
                        RadioGroup) {
                    View next = FocusFinder.getInstance().findNextFocus(
                            (ViewGroup) this.getWindow().getDecorView(),
                            focusView,
                            View.FOCUS_DOWN);
                    if (next != null) {
                        ViewParent focusParent = next.getParent();
                        if (focusParent instanceof RecyclerView) {
                            View view = ((RecyclerView) focusParent).getChildAt(0);
                            if (view != null) {
                                view.requestFocus();
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @IntDef({DETAIL_TYPE_NOTHING, DETAIL_TYPE_HISTORY, DETAIL_TYPE_COLLECTION,
            DETAIL_TYPE_ATTENTION, DETAIL_TYPE_SUBSCRIBE})
    @Retention(RetentionPolicy.SOURCE)
    @interface DetailType {
    }
}
