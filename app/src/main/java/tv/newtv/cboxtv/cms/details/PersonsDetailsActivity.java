package tv.newtv.cboxtv.cms.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.details.adapter.ColumnDetailsAdapter;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.ADPresenter;
import tv.newtv.cboxtv.cms.details.presenter.adpresenter.IAdConstract;
import tv.newtv.cboxtv.cms.details.view.VerticallRecyclerView;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.NewSmoothVorizontalScrollView;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.search.view.SearchActivity;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.player.videoview.DivergeView;
import tv.newtv.cboxtv.uc.db.DBCallback;
import tv.newtv.cboxtv.uc.db.DBConfig;
import tv.newtv.cboxtv.uc.db.DataSupport;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.utils.DBUtil;
import tv.newtv.cboxtv.utils.DeviceUtil;
import tv.newtv.cboxtv.views.RecycleImageView;

/**
 * Created by gaoleichao on 2018/4/2.
 */

/**
 * 人物详情页
 */
public class PersonsDetailsActivity extends BaseActivity implements OnRecycleItemClickListener,
        View.OnKeyListener, IAdConstract.IADConstractView, View.OnFocusChangeListener {


    @BindView(R.id.id_usercenter_fragment_root)
    VerticallRecyclerView mRecyclerView;


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

    @BindView(R.id.person_detail_ad_fl)
    FrameLayout program_detail_ad_fl;
    @BindView(R.id.person_detail_ad_img)
    RecycleImageView program_detail_ad_img;
    @BindView(R.id.id_scroll_view)
    NewSmoothVorizontalScrollView scrollView;
    List<ProgramSeriesInfo> dataList;
    private ColumnDetailsAdapter mAdapter;
    private Disposable mDisposable;
    private boolean isAttention;
    private Interpolator mSpringInterpolator;
    private ProgramSeriesInfo dataInfo;
    private String contentUUID;
    private String leftUUID, rightUUID;
    private IAdConstract.IADPresenter adPresenter;
    private String mTitleString;//title值，用于跳转搜索页使用 2018.4.30 wangquansheng
    private long lastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        ButterKnife.bind(this);
        adPresenter = new ADPresenter(this);
        init();
        initView();
        requestData();
        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "2," + contentUUID);
    }

    private void init() {

        mAdapter = new ColumnDetailsAdapter(this, this);
        contentUUID = getIntent().getStringExtra("content_uuid");
        ADConfig.getInstance().setSeriesID(contentUUID);
        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "人物信息有误", Toast.LENGTH_SHORT).show();
            return;
        }


        if (contentUUID.length() >= 2) {
            leftUUID = contentUUID.substring(0, 2);
            rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        }
        mSpringInterpolator = new OvershootInterpolator(2.2f);
        dataList = new ArrayList<>();
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

    }

    @Override
    protected void onDestroy() {
        unSubscribe();
        uninit();

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
        program_detail_ad_fl = null;
        program_detail_ad_img = null;
        scrollView = null;

        super.onDestroy();
    }

    private void uninit() {
        if (adPresenter != null) {
            adPresenter.destroy();
        }
        if (mAdapter != null) {
            mAdapter.destroy();
            mAdapter = null;
        }
        if (dataList != null) {
            dataList.clear();
            dataList = null;
        }


    }

    private void initView() {
        program_detail_ad_fl.setOnFocusChangeListener(this);
        mAttentionBtn.setOnKeyListener(this);
        mSendflowerBtn.setOnKeyListener(this);
        mSearchProBtn.setOnKeyListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
        mDetailsImgView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onItemGetFocus(mDetailsImgView);
                } else {
                    onItemLoseFocus(mDetailsImgView);
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
        mFlowerView.setOnFocusChangeListener(this);
        mAttentionBtn.setOnFocusChangeListener(this);
        mSendflowerBtn.setOnFocusChangeListener(this);

    }

    private void requestData() {
        dataList.clear();
        NetClient.INSTANCE.getDetailsPageApi().getInfo(Constant.APP_KEY, Constant.CHANNEL_ID,
                leftUUID, rightUUID, contentUUID)
                .subscribeOn(Schedulers.io())
                .compose(this.<ResponseBody>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
                    @Override
                    public ObservableSource<ResponseBody> apply(ResponseBody value) throws
                            Exception {
                        String data = value.string();
                        JSONObject object = new JSONObject(data);
                        if (object.getInt("errorCode") == 0) {
                            JSONObject obj = object.getJSONObject("data");
                            Gson gson = new Gson();
                            dataInfo = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                            setHeadData(dataInfo);
                        } else {
                            Toast.makeText(getApplicationContext(), "没有此人物信息", Toast
                                    .LENGTH_SHORT).show();
                            finish();
                        }
                        return NetClient.INSTANCE.getDetailsPageApi().getColumnsByPersons
                                (Constant.APP_KEY, Constant.CHANNEL_ID, leftUUID, rightUUID,
                                        contentUUID).subscribeOn(Schedulers.io());
                    }
                }).flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(ResponseBody responseBody) throws
                    Exception {
                addData(responseBody.string(), 5, "TA 主持的CCTV+栏目");
                return NetClient.INSTANCE.getDetailsPageApi().getProgramList(Constant.APP_KEY,
                        Constant.CHANNEL_ID, leftUUID, rightUUID, contentUUID);
            }
        }).flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(ResponseBody value) throws Exception {
                addData(value.string(), 3, "TA 的节目");
                return NetClient.INSTANCE.getDetailsPageApi().getCharacterlist(Constant.APP_KEY,
                        Constant.CHANNEL_ID, leftUUID, rightUUID, contentUUID);
            }
        }).flatMap(new Function<ResponseBody, ObservableSource<List<ProgramSeriesInfo>>>() {
            @Override
            public ObservableSource<List<ProgramSeriesInfo>> apply(ResponseBody value) throws
                    Exception {
                addData(value.string(), 4, "TA 相关的名人");
                if(dataList == null){
                    dataList = new ArrayList<>();
                }
                return Observable.just(dataList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ProgramSeriesInfo>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(List<ProgramSeriesInfo> columnPageBean) {
                        if(mAdapter != null && adPresenter != null){
                            mAdapter.appendToList(columnPageBean);
                            mAdapter.notifyDataSetChanged();
                            adPresenter.getAD(Constant.AD_DESK, Constant.AD_DETAILPAGE_BANNER, Constant
                                    .AD_DETAILPAGE_BANNER);//获取广告
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (dataInfo == null) {
                            Toast.makeText(getApplicationContext(), "没有此人物信息", Toast
                                    .LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void addData(String data, int layoutId, String title) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.getInt("errorCode") == 0) {
                JSONObject obj = object.getJSONObject("data");
                Gson gson = new Gson();
                ProgramSeriesInfo info = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                if (info.getData() != null && info.getData().size() != 0 && dataList != null) {
                    info.layoutId = layoutId;
                    info.layoutTitle = title;
                    dataList.add(info);
                }

            }
        } catch (JSONException e) {
            LogUtils.e(e.toString());
        }


    }

    private void setHeadData(ProgramSeriesInfo dataInfo) {
        String img = dataInfo.getvImage();
        String des = dataInfo.discription;
        detailTypeTv.setText(dataInfo.district + " | " + dataInfo.country);
        if (isAttention) {
            mAttentionIv.setImageResource(R.drawable.icon_details_attention_btn);
        } else {
            mAttentionIv.setImageResource(R.drawable.icon_details_unattention_btn);
        }

//        Picasso.with(getApplicationContext()).load(img).into(detailPlayIv);
        Picasso.get().load(img).transform(new PosterCircleTransform
                (getApplicationContext(), 8)).fit().memoryPolicy(MemoryPolicy.NO_STORE)
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
                        .transform(new PosterCircleTransform(this, 4))
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

        mTitleString = dataInfo.getTitle();
    }

    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    private void delAttention(String contentUuId) {
        DBUtil.delAttention(contentUuId, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            isAttention = false;
                            mAttentionIv.setImageResource(R.drawable.icon_details_unattention_btn);
                            Toast.makeText(getApplicationContext(), "取消关注成功", Toast.LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });
    }

    private void updateAttention(ProgramSeriesInfo entity) {
        if (entity == null) {
            LogUtils.e("update Attention is null");
            return;
        }
        DBUtil.addAttention(entity, new DBCallback<String>() {
            @Override
            public void onResult(int code, String result) {
                if (code == 0) {
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            isAttention = true;
                            mAttentionIv.setImageResource(R.drawable.icon_details_attention_btn);
                            Toast.makeText(getApplicationContext(), "关注成功", Toast.LENGTH_SHORT)
                                    .show();
                            RxBus.get().post(Constant.UPDATE_UC_DATA, true);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onItemClick(View view, int position, Object object) {
        ProgramSeriesInfo.ProgramsInfo entity = ((ProgramSeriesInfo) object).getData().get
                (position);
        Intent intent = new Intent();
        Class clazz = null;
        switch (view.getId()) {
            case R.id.id_module_8_view1:
            case R.id.id_module_8_view2:
            case R.id.id_module_8_view3:
            case R.id.id_module_8_view4:
            case R.id.id_module_8_view5:
            case R.id.id_module_8_view6:
//                intent.putExtra("content_uuid", entity.getContentUUID());
//                if (TextUtils.equals("CR", entity.getContentType()) || TextUtils.equals
// (Constant.CONTENTTYPE_FG, entity.getContentType())) {
//                    clazz = PersonsDetailsActivity.class;
//                } else if (TextUtils.equals(Constant.CONTENTTYPE_TV, entity.getContentType())
// || TextUtils.equals(Constant.CONTENTTYPE_CL, entity.getContentType())) {
//                    clazz = ColumnDetailsPageActivity.class;
//                } else if (TextUtils.equals(Constant.CONTENTTYPE_PG, entity.getContentType())) {
//                    clazz = DetailsPageActivity.class;
//                }
//                break;
//        }
//        if (clazz == null) {
//            return;
//        }
//        intent.setClass(getApplicationContext(), clazz);
//        startActivity(intent);
        }
    }

    private void onItemLoseFocus(View view) {
        ImageView focusImageView = (ImageView) view.findViewWithTag("tag_img_focus");
        if (focusImageView != null) {
            focusImageView.setVisibility(View.INVISIBLE);
        }
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    private void onItemGetFocus(View view) {
        ImageView focusImageView = (ImageView) view.findViewWithTag("tag_img_focus");
        if (focusImageView != null) {
            focusImageView.setVisibility(View.VISIBLE);
        }
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    private View getParentView(View view) {
        if (view == null || view.getParent() == null) {
            return null;
        }
        if (view.getParent() instanceof AiyaRecyclerView
                || view.getParent() instanceof VerticallRecyclerView
                || view.getParent() instanceof NewSmoothVorizontalScrollView) {
            return (View) view.getParent();
        }
        if (view.getParent() instanceof View) {
            View parent = getParentView((View) view.getParent());
            if (parent != null) {
                return parent;
            }
        }

        return null;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {

            if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent.ACTION_UP) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_ESCAPE:
                        finish();
                        return super.dispatchKeyEvent(event);
                }
            }
            View focus = scrollView.findFocus();
            if (focus == null) {
                return super.dispatchKeyEvent(event);
            }
            View ParentView = getParentView(focus);
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (ParentView!=null&&ParentView instanceof NewSmoothVorizontalScrollView) {
                        return false;
                    } else if (ParentView!=null&&ParentView instanceof AiyaRecyclerView) {
                        View upView = FocusFinder.getInstance().findNextFocus(mRecyclerView, focus, View.FOCUS_UP);
                        if (upView == null) {
                            //song hua
                            mSendflowerBtn.requestFocus();
                        } else {
                            //判断下一个FocusView父级是什么
                            View upParent = getParentView(upView);
                            if (upParent!=null){
                                if (upParent instanceof AiyaRecyclerView) {
                                    View defaultFocusView = ((AiyaRecyclerView) upParent).getDefaultFocusView();
                                    if (defaultFocusView!=null){
                                        defaultFocusView .requestFocus();
                                        return true;
                                    }

                                }
                            }

                        }
                    } else if (ParentView!=null&&ParentView instanceof VerticallRecyclerView) {
                        View upView = FocusFinder.getInstance().findNextFocus(mRecyclerView, focus, View.FOCUS_UP);
                        if (upView != null) {
                            View upParent = getParentView(upView);
                            if (upParent!=null){
                                if (upParent instanceof AiyaRecyclerView) {
                                    View defaultFocusView = ((AiyaRecyclerView) upParent).getDefaultFocusView();
                                    if (defaultFocusView!=null){
                                        defaultFocusView .requestFocus();
                                        return true;
                                    }

                                }
                            }

                        }
                    }


                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (ParentView!=null&&ParentView instanceof NewSmoothVorizontalScrollView) {
                        View nextFocus = FocusFinder.getInstance().findNextFocus(scrollView, focus, View.FOCUS_DOWN);
                        if (nextFocus != null) {
                            View parentView = getParentView(nextFocus);
                            if (parentView != null) {
                                if (parentView instanceof AiyaRecyclerView) {
                                    View defaultFocusView = ((AiyaRecyclerView) parentView).getDefaultFocusView();
                                    if (defaultFocusView!=null){
                                        defaultFocusView .requestFocus();
                                        return true;

                                    }


                                }
                            }

                        }
                    } else if (ParentView!=null&&ParentView instanceof AiyaRecyclerView) {
                        View upView = FocusFinder.getInstance().findNextFocus(mRecyclerView, focus, View.FOCUS_DOWN);
                        if (upView == null) {
                            return super.dispatchKeyEvent(event);
                        } else {
                            //判断下一个FocusView父级是什么
                            View upParent = getParentView(upView);
                            if (upParent!=null){
                                if (upParent instanceof AiyaRecyclerView) {
                                    View defaultFocusView = ((AiyaRecyclerView) upParent).getDefaultFocusView();
                                    if (defaultFocusView!=null){
                                        defaultFocusView .requestFocus();
                                        return true;
                                    }

                                }
                            }


                        }


                    } else if (ParentView!=null&&ParentView instanceof VerticallRecyclerView) {

                        return super.dispatchKeyEvent(event);
                    }


                    break;

            }


        }


        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onItemFocusChange(View view, boolean hasFocus, int Position, Object object) {

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                onClick(v);
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                switch (v.getId()) {
                    case R.id.btn_detail_attention:

                        return true;


                }

            }

        }
        return false;
    }

    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.btn_detail_search_programe:
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                //2018.4.30 wangquansheng
                Bundle bundle = new Bundle();
                bundle.putString("SearchType", "SearchListByKeyword");
                bundle.putString("keyword", mTitleString);
                bundle.putString("programType", "-1");
                bundle.putString("keywordType", "name");
                intent.putExtra("person", bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void showAd(String imgUrl, String adType) {
        if (!TextUtils.isEmpty(imgUrl)) {
            if (program_detail_ad_fl != null) {
                program_detail_ad_fl.setVisibility(View.VISIBLE);
            }
            if (program_detail_ad_img != null) {
                program_detail_ad_img.hasCorner(true).load(imgUrl);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.view_flower:
            case R.id.btn_detail_send_flower:
            case R.id.btn_detail_attention:
                // scrollView.setScrollTop();
                break;

            case R.id.person_detail_ad_fl:
                if (hasFocus) {
                    onItemGetFocus(v);
                } else {
                    onItemLoseFocus(v);
                }

                break;
        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        return super.dispatchKeyEvent(event);
//    }
}
