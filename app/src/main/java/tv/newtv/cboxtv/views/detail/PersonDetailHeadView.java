package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.views.custom.DivergeView;

/**
 * Created by linzy on 2018/10/11.
 *
 * 人物详情子view
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

    @BindView(R.id.detail_rel_image_send_flower)
    ImageView mBigScreenIv;
    @BindView(R.id.detail_rel_image_attention)
    ImageView mAttentionIv;
    @BindView(R.id.detail_rel_image_search_programe)
    ImageView mSearchProIv;
    @BindView(R.id.btn_detail_send_flower)
    RelativeLayout mSendflowerBtn;
    @BindView(R.id.btn_detail_attention)
    RelativeLayout mAttentionBtn;
    @BindView(R.id.btn_detail_search_programe)
    RelativeLayout mSearchProBtn;
    @BindView(R.id.view_flower)
    DivergeView mFlowerView;

    private final String TAG = "PersonDetailHeadView";
    private View view;
    private String contentUUID;
    private boolean isAttention;
    private Context mContext;
    private long lastClickTime = 0;
    private Content dataInfo;
    private ContentContract.ContentPresenter mContentPresenter;


    public PersonDetailHeadView(Context context) {
        super(context);
        this.mContext = context;
    }

    public PersonDetailHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context);
        initListener();
    }

    public PersonDetailHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context);
        initListener();
    }

    private void init(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.person_detail_head_view_item,this,false);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(view);
        ButterKnife.bind(this);
    }

    public void setTopView(){
        final LinearLayout upTop = view.findViewById(R.id.up_top);
            new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    upTop.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    upTop.setVisibility(View.GONE);
                }
            }.start();
    }

    private void initListener(){
        mContentPresenter = new ContentContract.ContentPresenter(getContext(),this);

        mAttentionBtn.setOnKeyListener(this);
        mSendflowerBtn.setOnKeyListener(this);
        mSearchProBtn.setOnKeyListener(this);
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

        DataSupport.search(DBConfig.ATTENTION_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUUID)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (TextUtils.isEmpty(result)) {
                            isAttention = false;
                        } else {
                            isAttention = true;
                        }
                    }
                }).excute();


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
        mBigScreenIv = null;
        mAttentionIv = null;
        mSearchProIv = null;
        mSendflowerBtn = null;
        mAttentionBtn = null;
        mSearchProBtn = null;
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
            case R.id.btn_detail_attention:
                if (System.currentTimeMillis() - lastClickTime >= 2000) {//判断距离上次点击小于2秒
                    lastClickTime = System.currentTimeMillis();//记录这次点击时间
                    if (isAttention) {
                        delAttention(contentUUID);
                    } else {
                        updateAttention(dataInfo);
                    }
                }
                break;
            case R.id.btn_detail_send_flower:
                mFlowerView.startDiverges(0);
                break;
        }
    }

    private void delAttention(String contentUuId) {
        DBUtil.delAttention(contentUuId, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mAttentionBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            isAttention = false;
                            mAttentionIv.setImageResource(R.drawable.icon_details_unattention_btn);
                            Toast.makeText(mContext, "取消关注成功", Toast.LENGTH_SHORT).show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });
    }

    private void updateAttention(Content entity) {
        if (entity == null) {
            LogUtils.e("update Attention is null");
            return;
        }
        DBUtil.addAttention(entity, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mAttentionBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            isAttention = true;
                            mAttentionIv.setImageResource(R.drawable.icon_details_attention_btn);
                            Toast.makeText(mContext, R.string.attention_success, Toast.LENGTH_SHORT).show();
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
        if (isAttention) {
            mAttentionIv.setImageResource(R.drawable.icon_details_attention_btn);
        } else {
            mAttentionIv.setImageResource(R.drawable.icon_details_unattention_btn);
        }

        Picasso.get().load(img).transform(new PosterCircleTransform
                (mContext, 8)).fit().memoryPolicy(MemoryPolicy.NO_STORE)
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
                        .transform(new PosterCircleTransform(mContext, 4))
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
    public void onError(@NotNull Context context, @Nullable String desc) {
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }
}
