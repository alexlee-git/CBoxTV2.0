package tv.newtv.cboxtv.views.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.ToastUtil;

import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.MainActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.PersonsDetailsActivityNew;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         13:58
 * 创建人:           weihaichao
 * 创建日期:          2018/10/19
 */
public abstract class DetailPageActivity extends BaseActivity {

    private String contentUUID;
    protected boolean isADEntry = false;

    protected abstract void buildView(@Nullable Bundle savedInstanceState,String contentID);

    @Override
    protected boolean isDetail() {
        return true;
    }

    protected abstract boolean interruptDetailPageKeyEvent(KeyEvent event);

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("intent", getIntent());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            contentUUID = getIntent().getStringExtra(Constant.CONTENT_UUID);
            isADEntry = getIntent().getBooleanExtra(Constant.ACTION_AD_ENTRY, false);
        } else {
            Intent intent = savedInstanceState.getParcelable("intent");
            if (intent != null) {
                if (intent.hasExtra(Constant.CONTENT_UUID))
                    contentUUID = intent.getStringExtra(Constant.CONTENT_UUID);
            }
        }

        if(TextUtils.isEmpty(contentUUID)){
            ToastUtil.showToast(getApplicationContext(),"节目ID为空");
            finish();
            return;
        }

        buildView(savedInstanceState,contentUUID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ViewGroup viewGroup = findViewById(R.id.root_view);
        destroyViewGroup(viewGroup);
        BitmapUtil.recycleImageBitmap(viewGroup);
    }

    private void destroyViewGroup(ViewGroup viewGroup) {
        if (viewGroup != null) {
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view instanceof IEpisode) {
                    ((IEpisode) view).destroy();
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
        if (Libs.get().getFlavor().equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent
                .ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return super.dispatchKeyEvent(event);
            }
        }
        if (interruptDetailPageKeyEvent(event)) {
            return true;
        }

        if(event.getAction() == KeyEvent.ACTION_UP){
            if (isBackPressed(event)) {
                if (isADEntry) {
                    Intent intent = new Intent();
                    intent.setClass(LauncherApplication.AppContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LauncherApplication.AppContext.getApplicationContext().startActivity(intent);
                    isADEntry = false;
                }
                finish();
                return true;
            }
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
                    if (view instanceof IEpisode && ((IEpisode) view).interruptKeyEvent
                            (event)) {
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
                            dir = 1;
                            condition = true;
                        }
                        while (condition) {
                            pos += dir;
                            if (pos < 0 || pos > viewGroup.getChildCount()) {
                                break;
                            }
                            toView = viewGroup.getChildAt(pos);
                            if (toView != null) {
                                if (toView instanceof IEpisode && ((IEpisode) toView)
                                        .interruptKeyEvent
                                                (event)) {
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
