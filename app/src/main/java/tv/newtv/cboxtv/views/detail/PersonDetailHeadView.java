package tv.newtv.cboxtv.views.detail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.uc.v2.listener.IFollowStatusCallback;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.utils.UserCenterUtils;
import tv.newtv.cboxtv.views.custom.DivergeView;
import tv.newtv.cboxtv.views.custom.FocusToggleView2;

/**
 * Created by linzy on 2018/10/11.
 *
 * 人物详情头部view
 */

public class PersonDetailHeadView extends RelativeLayout implements IEpisode,View.OnKeyListener,ContentContract.View{

    @BindView(R.id.id_detail_view)
    FrameLayout mDetailsImgView;
    @BindView(R.id.iv_detail_image_play)
    ImageView detailPlayIv;
    @BindView(R.id.iv_detail_image_focus)
    ImageView mFocusIv;
    @BindView(R.id.tv_detail_title)
    TextView detailTitleTv;
    @BindView(R.id.detail_tv_type)
    TextView detailTypeTv;
    @BindView(R.id.detail_tv_content)
    TextView detailContentTv;
    @BindView(R.id.detail_tv_star)
    TextView detailStarTv;
    @BindView(R.id.view_flower)
    DivergeView mFlowerView;
    @BindView(R.id.send_flower)
    FocusToggleView2 sendFlowerView;
    @BindView(R.id.attention)
    FocusToggleView2 attentionView;

    private final String TAG = "PersonDetailHeadView";
    private View view;
    private String contentUUID;
    private boolean isAttention = false;
    private long lastClickTime = 0;
    private Content dataInfo;
    private ContentContract.ContentPresenter mContentPresenter;

    private List<Long> requestIdList;


    public PersonDetailHeadView(Context context) {
        super(context);
    }

    public PersonDetailHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initListener();
    }

    public PersonDetailHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initListener();
    }

    private void init(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.person_detail_head_view_item,this,false);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(view);
        requestIdList = new ArrayList<>();
        ButterKnife.bind(this);
    }

    public void setTopView(){
        LinearLayout upTop = view.findViewById(R.id.up_top);
        ImageView arrowsDark = view.findViewById(R.id.nav_arrows_dark);
        ImageView navTitle = view.findViewById(R.id.nav_title);
        hintAnimator(upTop,arrowsDark,navTitle);

    }

    private void hintAnimator(LinearLayout upTop, ImageView arrowsDark,ImageView navTitle) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(arrowsDark, "alpha", 0.1f, 1.0f, 0.1f, 1.0f, 0.1f,
                1.0f,0.1f,1.0f,0.1f,1.0f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(arrowsDark, "TranslationY", 0,12,0,12,0,12,0,12,0,12);
        ObjectAnimator alphaY = ObjectAnimator.ofFloat(navTitle, "alpha", 1.0f, 0.1f);
        alphaY.setStartDelay(4000);
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(translationX,translationY);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(5000);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                upTop.setVisibility(View.GONE);
            }
        });
    }

    private void initListener(){
        mContentPresenter = new ContentContract.ContentPresenter(getContext(),this);

        sendFlowerView.setOnKeyListener(this);
        attentionView.setOnKeyListener(this);
        mDetailsImgView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ScaleUtils.getInstance().onItemGetFocus(v);
                } else {
                    ScaleUtils.getInstance().onItemLoseFocus(v);
                }
            }
        });
        mFlowerView.setEndPoint(new PointF(mFlowerView.getMeasuredWidth() / 2, 0));
        mFlowerView.setStartPoint(new PointF(getResources().getDimension(R.dimen.width_45px),
                getResources().getDimension(R.dimen.height_185px)));
        mFlowerView.setDivergeViewProvider(new DivergeView.DivergeViewProvider() {
            @Override
            public Bitmap getBitmap(Object obj) {
                return ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.drawable
                        .icon_flower, null)).getBitmap();
            }
        });
    }

    public void setContentUUID(String contentUUIDs){
        contentUUID = contentUUIDs;

        final Long id = UserCenterUtils.getAttentionState(getContentUUID(), new IFollowStatusCallback() {
            @Override
            public void notifyFollowStatus(boolean status,Long reqId) {
                isAttention = status;
                requestIdList.remove(reqId);
            }
        });
        requestIdList.add(id);

        //获取人物信息
        mContentPresenter.getContent(contentUUIDs,false);
    }

    @Override
    public String getContentUUID() {
        return contentUUID;
    }

    @Override
    public void destroy() {
        if (mContentPresenter != null){
            mContentPresenter.destroy();
            mContentPresenter = null;
        }

        mDetailsImgView = null;
        detailPlayIv = null;
        mFocusIv = null;
        detailTitleTv = null;
        detailTypeTv = null;
        detailContentTv = null;
        detailStarTv = null;
        sendFlowerView = null;
        attentionView = null;

        if(requestIdList != null && requestIdList.size() > 0){
            for(Long id : requestIdList){
                UserCenterRecordManager.getInstance().removeCallback(id);
            }
            requestIdList.clear();
        }
        requestIdList = null;

        if(mFlowerView != null){
            mFlowerView.release();
        }
        mFlowerView = null;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                onClickView(view);
            }
        }
        return false;
    }


    public void onClickView(View view) {
        switch (view.getId()){
            case R.id.attention:
                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                    if (isAttention) {
                        delAttention(contentUUID);
                    } else {
                        updateAttention(dataInfo);
                    }
                }
                break;
            case R.id.send_flower:
                mFlowerView.startDiverges(0);
                break;
        }
    }

    private void delAttention(final String contentUuId) {

        UserCenterUtils.deleteSomeAttention(dataInfo, contentUuId, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    attentionView.post(new Runnable() {
                        @Override
                        public void run() {
                            isAttention = false;
                            attentionView.setSelect(false);
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_ATTENTION,"1,"+contentUuId);

                            Toast.makeText(getContext().getApplicationContext(), "取消关注成功", Toast.LENGTH_SHORT).show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });
    }

    private void updateAttention(final Content entity) {
        if (entity == null) {
            LogUtils.e("update Attention is null");
            return;
        }
        UserCenterUtils.addAttention(entity, new DBCallback<String>() {
            @Override
            public void onResult(int code, final String result) {
                if (code == 0) {
                    attentionView.post(new Runnable() {
                        @Override
                        public void run() {
                            isAttention = true;
                            attentionView.setSelect(true);
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_ATTENTION,"0,"+contentUUID);
                            Toast.makeText(getContext().getApplicationContext(), R.string.attention_success, Toast
                                    .LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });
    }

    private void setHeadData(Content dataInfo) {
        String img = dataInfo.getVImage();
        detailTypeTv.setText(String.format("%s | %s", dataInfo.getDistrict(), dataInfo.getCountry()));

        Long id = UserCenterUtils.getAttentionState(contentUUID, new IFollowStatusCallback() {
            @Override
            public void notifyFollowStatus(boolean status,Long reqId) {
                if (status) {
                    attentionView.setSelect(true);
                } else {
                    attentionView.setSelect(false);
                }
                isAttention = status;
                requestIdList.remove(reqId);
            }
        });
        requestIdList.add(id);

        Picasso.get().load(img).transform(new PosterCircleTransform
                (getContext().getApplicationContext(), 8)).fit().memoryPolicy(MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.focus_240_360).error(R.drawable.focus_240_360).into
                (detailPlayIv);

        detailContentTv.setText(dataInfo.getDescription());
        detailTitleTv.setText(dataInfo.getTitle());

        if (detailPlayIv != null) {
            if (!TextUtils.isEmpty(img)) {
                detailPlayIv.setScaleType(ImageView.ScaleType.FIT_XY);
                detailPlayIv.setVisibility(View.VISIBLE);
                RequestCreator picasso = Picasso.get()
                        .load(img)
                        .transform(new PosterCircleTransform(getContext().getApplicationContext(), 4))
                        .priority(Picasso.Priority.HIGH)
                        .stableKey(img)
                        .config(Bitmap.Config.RGB_565);
                picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                        .focus_240_360);
                picasso.into(detailPlayIv);
            } else {
                detailPlayIv.setScaleType(ImageView.ScaleType.FIT_XY);
                detailPlayIv.setVisibility(View.VISIBLE);
                RequestCreator picasso = Picasso.get()
                        .load(R.drawable.focus_240_360)
                        .priority(Picasso.Priority.HIGH)
                        .config(Bitmap.Config.RGB_565);
                picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                        .focus_240_360);
                picasso.into(detailPlayIv);
            }
        }
        if (dataInfo.getDescription() != null) {
            detailContentTv.setText(dataInfo.getDescription());
        }
        if (dataInfo.getTitle() != null) {
            detailTitleTv.setText(dataInfo.getTitle());
        }

    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        View focusView = findFocus();

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_RIGHT);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_LEFT);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                View view = FocusFinder.getInstance().findNextFocus(this, focusView, View
                        .FOCUS_UP);
                if (view != null) {
                    view.requestFocus();
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (content != null){
            dataInfo = content;
            setHeadData(content);
        }else {
            LogUtils.e(TAG,"content data is null!");
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {
    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }
}
