package tv.newtv.cboxtv.views.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.libs.Constant;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ToastUtil;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         13:58
 * 创建人:           weihaichao
 * 创建日期:          2018/10/19
 */
public abstract class DetailPageActivity extends BaseActivity {

    private String contentUUID;
    private String childContentUUID;

    protected String mFocusId; //从推荐位进入的默认位置(选集) 推荐位优先级高于历史记录

    protected String getChildContentUUID() {
        return childContentUUID;
    }

    protected abstract void buildView(@Nullable Bundle savedInstanceState, String contentID);

    @Override
    protected boolean isDetail() {
        return true;
    }

    protected abstract boolean interruptDetailPageKeyEvent(KeyEvent event);

    protected abstract boolean isFull(KeyEvent event);

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("intent", getIntent());
    }

    @Override
    public boolean isFullScreen() {
        return super.isFullScreen();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = null;
        if (savedInstanceState == null) {
            intent = getIntent();

        } else {
            intent = savedInstanceState.getParcelable("intent");
        }

        if (intent != null) {
            if (intent.hasExtra(Constant.CONTENT_UUID))
                contentUUID = intent.getStringExtra(Constant.CONTENT_UUID);

            if (intent.hasExtra(Constant.CONTENT_CHILD_UUID))
                childContentUUID = intent.getStringExtra(Constant.CONTENT_CHILD_UUID);

            if (intent.hasExtra(Constant.FOCUS_ID)) {
                mFocusId = intent.getStringExtra(Constant.FOCUS_ID);
            }
        }

        if (TextUtils.isEmpty(contentUUID)) {
            ToastUtil.showToast(getApplicationContext(), "节目ID为空");
            finish();
            return;
        }

        buildView(savedInstanceState, contentUUID);
    }

    @Override
    protected void onDestroy() {
        ViewGroup viewGroup = findViewById(R.id.root_view);
        destroyViewGroup(viewGroup);
        BitmapUtil.recycleImageBitmap(viewGroup);

        super.onDestroy();
    }

    private void destroyViewGroup(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view instanceof IEpisode) {
                    ((IEpisode) view).destroy();
                    LogUtils.d("DetailPageActivity","destroy IEpisode ->" + view);
                } else if (view instanceof ViewGroup) {
                    destroyViewGroup((ViewGroup) view);
                }
            }
            if (viewGroup instanceof SmoothScrollView) {
                ((SmoothScrollView) viewGroup).destroy();
            }
        }
    }

    @SuppressWarnings({"ConstantConditions", "LoopConditionNotUpdatedInsideLoop"})
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        if (interruptDetailPageKeyEvent(event)) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            ViewGroup viewGroup = findViewById(R.id.root_view);
            if (viewGroup == null) {
                return super.dispatchKeyEvent(event);
            }
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view != null) {
                    if (!view.hasFocus()) {
                        continue;
                    }
                    if (view instanceof IEpisode
                            && view.getVisibility() == View.VISIBLE
                            && ((IEpisode) view).interruptKeyEvent(event)) {
                        return true;
                    } else {
                        View toView = null;
                        int pos = index;
                        int dir = 0;
                        boolean condition = false;
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                            dir = -1;
                            condition = true;
                        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                            if (isFull(event)) {
                                return true;
                            } else {
                                dir = 1;
                                condition = true;
                            }
                        }
                        while (condition) {
                            pos += dir;
                            if (pos < 0 || pos > viewGroup.getChildCount()) {
                                break;
                            }
                            toView = viewGroup.getChildAt(pos);
                            if (toView != null) {
                                if (toView instanceof IEpisode
                                        && toView.getVisibility() == View.VISIBLE
                                        && ((IEpisode) toView).interruptKeyEvent(event)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
