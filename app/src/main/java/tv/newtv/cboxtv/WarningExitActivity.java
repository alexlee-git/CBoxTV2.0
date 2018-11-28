package tv.newtv.cboxtv;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.AdContract;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.BootGuide;
import com.newtv.libs.ad.ADHelper;
import com.newtv.libs.ad.AdEventContent;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.custom.RecycleImageView;


public class WarningExitActivity extends BaseActivity implements View.OnClickListener,
        View.OnFocusChangeListener, PageContract.ModelView {
    private RecycleImageView exit_image;
    private FrameLayout focus_layout;
    private Button okButton;
    private Button cancelButton;
    private PageContract.Presenter mPresenter;
    private AdContract.Presenter mAdPresenter;
    private boolean isAd = false;
    private String eventContent = "";
    private ModelResult<ArrayList<Page>> page;
    private Program program;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrontStage = true;
        setContentView(R.layout.activity_warning_exit);
        initView();
        mPresenter = new PageContract.ContentPresenter(this, this);
        mAdPresenter = new AdContract.AdPresenter(getApplicationContext(), null);
        String baseUrl = BootGuide.getBaseUrl(BootGuide.EXIT_CONTENTID);
        if (TextUtils.isEmpty(baseUrl)) {
            return;
        }
        mPresenter.getPageContent(baseUrl);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View focusView = getWindow().getDecorView().findFocus();
        if (focusView != null) {
            if (focusView instanceof FrameLayout) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (cancelButton != null)
                            cancelButton.requestFocus();
                        return true;
                    case KeyEvent.KEYCODE_DPAD_LEFT:

                        return true;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:

                        return true;
                    case KeyEvent.KEYCODE_DPAD_UP:

                        return true;
                }

            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView() {
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);
        exit_image = findViewById(R.id.exit_image);
        focus_layout = findViewById(R.id.focus_layout);
        cancelButton.requestFocus();
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        okButton.setOnFocusChangeListener(this);
        cancelButton.setOnFocusChangeListener(this);
        focus_layout.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.okButton) {
            setResult(RESULT_OK);
        } else if (id == R.id.cancelButton) {
            setResult(RESULT_CANCELED);
        } else if (id == R.id.focus_layout) {
            if (isAd) {
                if (!TextUtils.isEmpty(eventContent)) {
                    AdEventContent adEventContent = GsonUtil.fromjson(eventContent, AdEventContent
                            .class);
                    JumpUtil.activityJump(this, adEventContent.actionType, adEventContent
                                    .contentType,
                            adEventContent.contentUUID, adEventContent.actionURI);
                }

            } else {
                if (program != null) {
                    JumpUtil.activityJump(this, program.getL_actionType(), program
                            .getL_contentType(), program.getL_id(), program.getL_actionUri());
                }
            }

        }
        finish();
    }


    //焦点变化监听
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(v);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(v);
        }
    }

    /**
     * 退出广告
     */
    public void getAD() {
        mAdPresenter.getAdByType("quit", null, "", null, new AdContract.Callback() {
            @Override
            public void showAd(@Nullable String type, @Nullable String url, @Nullable HashMap<?,
                    ?> hashMap) {
                if (!TextUtils.isEmpty(url)) {
                    exit_image.setVisibility(View.VISIBLE);
                    Picasso.get().load(url).into(exit_image, new Callback() {
                        @Override
                        public void onSuccess() {
                            ADHelper.AD.ADItem item = mAdPresenter.getCurrentAdItem();
                            if (item!=null && !TextUtils.isEmpty(eventContent)){
                                eventContent = item.eventContent;
                            }
                            if (item != null && !TextUtils.isEmpty(item.mid) && !TextUtils
                                    .isEmpty(item.aid) && !TextUtils.isEmpty(item.id)) {
                                AdSDK.getInstance().report((item.mid + ""), item.aid + "", item
                                                .id + "",
                                        "", null, item.PlayTime + "", null);

                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                } else {
                    if (program != null && !TextUtils.isEmpty(program.getImg())) {
                        Picasso.get().load(program.getImg()).into(exit_image);
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
        if (mAdPresenter != null) {
            mAdPresenter.destroy();
            mAdPresenter = null;
        }
    }


    @Override
    public void onPageResult(@NotNull ModelResult<ArrayList<Page>> page) {
        this.page = page;
        program = page.getData().get(0).getPrograms().get(0);

        if (program != null && program.isAd() != 1) {
            if (!TextUtils.isEmpty(program.getImg())) {
                Picasso.get().load(program.getImg()).into(exit_image);
            }
        } else {
            isAd = true;
            getAD();

        }


    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {


    }

    @Override
    public void startLoading() {

    }

    @Override
    public void loadingComplete() {

    }
}
