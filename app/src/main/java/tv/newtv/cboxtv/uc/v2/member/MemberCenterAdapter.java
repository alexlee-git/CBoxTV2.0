package tv.newtv.cboxtv.uc.v2.member;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.SharePreferenceUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.uc.bean.MemberInfoBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.TimeUtil;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2
 * 类描述：会员中心adapter
 * 创建人：wqs
 * 创建时间：11:30
 * 创建日期：2018/9/11
 * 修改人：wqs
 * 修改时间：
 * 修改备注：
 * 修改备注：
 */
public class MemberCenterAdapter extends BaseRecyclerAdapter<Page, RecyclerView
        .ViewHolder> {
    private final String TAG = "MemberCenterAdapter";
    private static final int TYPE_MEMBER_INFO = 1001;
    private static final int TYPE_MEMBER_RECOMMEND_PROMOTION = 1002;//会员促销推荐位
    private static final int TYPE_MEMBER_RECOMMEND_INTERESTS = 1003;//会员权益推荐位
    private final String TAG_POSTER_FOCUS = "tag_poster_focus";//海报焦点
    private final String TAG_POSTER_IMAGE = "tag_poster_image";//海报图
    private final String TAG_POSTER_HEAD_IMAGE = "tag_poster_head_image";//用户头像
    private final String TAG_POSTER_PROMPT_LOGIN = "tag_poster_prompt_login";//提示登录
    private final String TAG_POSTER_PROMPT_MEMBER = "tag_poster_prompt_member";//提示开通会员
    private final String TAG_POSTER_MAIN_TITLE = "tag_poster_main_title";//海报下方主标题
    private final String TAG_POSTER_SUB_TITLE = "tag_poster_sub_title";//海报下方副标题
    private final String TAG_POSTER_RIGHT_TOP_MARK = "tag_poster_right_top_mark";//右上角角标
    private final String TAG_POSTER_PROGRAM_UPDATE_TITLE = "tag_poster_program_recent_title";//海报图节目更新状态标题
    private final String TAG_POSTER_SCORE_TITLE = "tag_poster_score_title";//海报图评分标题
    private final String TAG_BUTTON_TEXT = "tag_member_center_btn_text";//会员中心按钮文本
    private final String TAG_COENER_CONTAINER = "tag_corner_container";//会员中心添加评分、更新剧集、角标的控件
    private Context mContext;
    private Interpolator mSpringInterpolator;
    private OnRecycleItemClickListener<Program> listener;
    private ImageView mMemberHead, mMemberMark;
    private TextView mMemberName, mMemberTime;
    private FrameLayout mBtnLogin, mBtnOpen, mBtnExchange, mBtnOrder, mBtnDrama, mPromotionRecommend;
    private MemberInfoBean mMemberInfoBean;
    private String mMobileString;
    private String mLoginTokenString;//登录token,用于判断登录状态
    public boolean BtnLoginFocusStatus = false;//登录按钮焦点状态
    private ImageView mRecommendQrCodeImageView;
    private QrcodeUtil mQrcodeUtil;
    private Bitmap mBitmap;

    MemberCenterAdapter(Context context, OnRecycleItemClickListener<Program>
            listener) {
        this.mContext = context;
        this.listener = listener;
        mSpringInterpolator = new OvershootInterpolator(2.2f);
        mQrcodeUtil = new QrcodeUtil();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_MEMBER_INFO) {
            viewHolder = new InfoViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                    .activity_usercenter_member_center_info_v2, parent, false));
        } else if (viewType == TYPE_MEMBER_RECOMMEND_PROMOTION) {
            viewHolder = new PromotionRecommendViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                    .activity_usercenter_member_center_recommend_promotion_v2, parent, false));
        } else if (viewType == TYPE_MEMBER_RECOMMEND_INTERESTS) {
            viewHolder = new InterestsRecommendViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                    .activity_usercenter_member_center_recommend_interests_v2, parent, false));
        } else {
            viewHolder = new ContentViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                    .activity_usercenter_member_center_content_v2, parent, false));
        }
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_MEMBER_INFO;
        } else if (position == 1) {
            return TYPE_MEMBER_RECOMMEND_PROMOTION;
        } else if (position == 2) {
            return TYPE_MEMBER_RECOMMEND_INTERESTS;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof InfoViewHolder) {
                InfoViewHolder viewHolder = (InfoViewHolder) holder;
                mMemberHead = viewHolder.mMemberHead;
                mMemberMark = viewHolder.mMemberMark;
                mMemberName = viewHolder.mMemberName;
                mMemberTime = viewHolder.mMemberTime;
                mBtnLogin = viewHolder.mBtnLogin;
                mBtnOpen = viewHolder.mBtnOpen;
                //设置会员信息
                setMemberStatus(mMobileString, mMemberInfoBean);
            } else if (holder instanceof PromotionRecommendViewHolder) {
                PromotionRecommendViewHolder viewHolder = (PromotionRecommendViewHolder) holder;
                mRecommendQrCodeImageView = viewHolder.mQrCodeImageView;
                mPromotionRecommend = viewHolder.mPromotionRecommend;
                Page page = mList.get(0);
                Log.e(TAG, "wqs:PromotionRecommendViewHolder:" + page.toString());
                setRecommendPosterData(viewHolder.mPromotionRecommend, page);
            } else if (holder instanceof InterestsRecommendViewHolder) {
                InterestsRecommendViewHolder viewHolder = (InterestsRecommendViewHolder) holder;
                Page page = mList.get(1);
                setRecommendPosterData(viewHolder.mInterestsIntroduce, page);
                Log.e(TAG, "wqs:InterestsRecommendViewHolder:" + page.toString());
            } else if (holder instanceof ContentViewHolder) {
                ContentViewHolder viewHolder = (ContentViewHolder) holder;
                Page page = null;
                page = mList.get(position - 1);
                setDataList(position, viewHolder, page);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onBindViewHolder:Exception:" + e.toString());
        }
    }

    /**
     * 传递用户会员信息数据
     *
     * @param memberInfoBean
     */
    public void setMemberStatus(String mobileString, MemberInfoBean memberInfoBean) {
        String expireFormatTime = "";//格式化之后的会员有效期时间
        try {
            //获取登录状态
            mLoginTokenString = SharePreferenceUtils.getToken(mContext);
            mMobileString = mobileString;
            TextView mBtnTextView = mBtnOpen.findViewWithTag(TAG_BUTTON_TEXT);
            if (!TextUtils.isEmpty(mLoginTokenString)) {
                if (mMemberHead != null) {
                    mMemberHead.setBackgroundResource(R.drawable.member_head_login_v2);
                }
                if (mMemberName != null) {
                    if (!TextUtils.isEmpty(mobileString) || !TextUtils.equals(mobileString, "null")) {
                        mMemberName.setText(mobileString);
                    } else {
                        mMemberName.setText("");
                    }

                }
                //控制开通会员按钮的向左焦点，防止焦点乱跑
                if (mBtnOpen != null) {
                    mBtnOpen.setNextFocusLeftId(R.id.id_member_center_btn_open);
                }
                if (mBtnLogin != null) {
                    if (BtnLoginFocusStatus && mBtnLogin.getVisibility() == View.VISIBLE) {
                        //只有当登录按钮获得焦点，登录成功，登录按钮消失，强制让推荐位获取焦点
                        if (mPromotionRecommend != null) {
                            mPromotionRecommend.requestFocus();
                        }
                        BtnLoginFocusStatus = false;
                    }
                }
                goneView(mBtnLogin);
                if (memberInfoBean != null) {
                    mMemberInfoBean = memberInfoBean;
                    if (mMemberHead != null) {
                        mMemberHead.setBackgroundResource(R.drawable.member_head_login_v2);
                    }

                    //控制开通会员按钮的向左焦点，防止焦点乱跑
                    if (mBtnOpen != null) {
                        mBtnOpen.setNextFocusLeftId(R.id.id_member_center_btn_open);

                        if (mBtnTextView != null) {
                            mBtnTextView.setText(mContext.getResources().getString(R.string.user_already_member));
                        } else {
                            Log.e(TAG, "--setMemberStatus:-memberInfoBean != null:mBtnTextView == null");
                        }
                    }
                    goneView(mBtnLogin);
                    showView(mMemberName);
                    showView(mMemberTime);
                    String expireTimeDate = memberInfoBean.getExpireTime();
                    if (!TextUtils.isEmpty(expireTimeDate)) {
                        //有效期截止时间毫秒数
                        long expireTimeInMillis = TimeUtil.getInstance().getSecondsFromDate(expireTimeDate);
                        //与当前时间进行对比，判断会员是否到期
                        long currentTimeInMillis = TimeUtil.getInstance().getCurrentTimeInMillis();
                        Log.d(TAG, "wqs:setMemberStatus:expireTimeInMillis:" + expireTimeInMillis);
                        Log.d(TAG, "wqs:setMemberStatus:currentTimeInMillis:" + currentTimeInMillis);
                        expireFormatTime = TimeUtil.getInstance().getDateFromSeconds(expireTimeInMillis + "");
                        if (expireTimeInMillis >= currentTimeInMillis) {
                            //用户会员有效
                            if (mMemberMark != null) {
                                showView(mMemberMark);
                                mMemberMark.setBackgroundResource(R.drawable.uc_head_member_mark_v2);
                            }
                        } else {
                            //用户会员失效
                            if (mMemberMark != null) {
                                showView(mMemberMark);
                                mMemberMark.setBackgroundResource(R.drawable.uc_head_not_member_mark_v2);
                            }
                        }
                    } else {
                        hideView(mMemberMark);
                    }
                    if (mMemberTime != null) {
                        mMemberTime.setText("会员有效期： " + expireFormatTime);
                    }
                } else {
                    hideView(mMemberMark);
                    Log.e(TAG, "wqs:setMemberStatus:memberInfoBean==null");
                    if (mMemberTime != null) {
                        mMemberTime.setText("");
                    }
                    if (mBtnOpen != null) {
                        if (mBtnTextView != null) {
                            mBtnTextView.setText(mContext.getResources().getString(R.string.member_center_btn_open));
                        } else {
                            Log.e(TAG, "wqs:setMemberStatus:memberInfoBean == null:mBtnTextView == null");
                        }
                    }
                    showView(mMemberName);
                    goneView(mMemberTime);
                }

            } else {
                hideView(mMemberMark);
                if (mMemberHead != null) {
                    mMemberHead.setBackgroundResource(R.drawable.member_head_not_login_v2);
                }
                //控制开通会员按钮的向左焦点，防止焦点乱跑
                if (mBtnOpen != null) {
                    mBtnOpen.setNextFocusLeftId(R.id.id_member_center_btn_login);
                }
                if (mBtnTextView != null) {
                    mBtnTextView.setText(mContext.getResources().getString(R.string.member_center_btn_open));
                } else {
                    Log.e(TAG, "wqs:setMemberStatus:memberInfoBean == null:mBtnTextView == null");
                }
                showView(mBtnLogin);
                Log.e(TAG, "wqs:setMemberStatus:mLoginTokenString==null");

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setMemberStatus:Exception:" + e.toString());
        }
    }

    private void onItemLoseFocus(View view) {
        try {
            ImageView focusImageView = (ImageView) view.findViewWithTag(TAG_POSTER_FOCUS);
            if (focusImageView != null) {
                focusImageView.setVisibility(View.GONE);
            }
            TextView titleView = (TextView) view.findViewWithTag(TAG_POSTER_MAIN_TITLE);
            if (titleView != null) {
                titleView.setSelected(false);
            }

            // 直接缩小view
//            ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
//                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            sa.setFillAfter(true);
//            sa.setDuration(400);
//            sa.setInterpolator(mSpringInterpolator);
//            view.startAnimation(sa);
            ScaleUtils.getInstance().onItemLoseFocus(view);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onItemLoseFocus:Exception:" + e.toString());
        }
    }

    private void onItemGetFocus(View view) {
        try {
            ImageView focusImageView = (ImageView) view.findViewWithTag(TAG_POSTER_FOCUS);
            if (focusImageView != null) {
                focusImageView.setVisibility(View.VISIBLE);
            }
            TextView titleView = (TextView) view.findViewWithTag(TAG_POSTER_MAIN_TITLE);
            if (titleView != null) {
                titleView.setSelected(true);
            }
            //直接放大view
//            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
//                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            sa.setFillAfter(true);
//            sa.setDuration(400);
//            sa.setInterpolator(mSpringInterpolator);
//            view.bringToFront();
//            view.startAnimation(sa);
            ScaleUtils.getInstance().onItemGetFocus(view);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onItemGetFocus:Exception:" + e.toString());
        }
    }

    private void setDataList(int position, ContentViewHolder viewHolder, Page entity) {
        try {
            if (entity == null) {
                return;
            }
            viewHolder.titleIconIv.setVisibility(View.VISIBLE);
            viewHolder.titleTv.setVisibility(View.VISIBLE);
            viewHolder.titleTv.setText(entity.getBlockTitle());
            List<Program> data = entity.getPrograms();
            if (data == null || data.size() == 0) {
                //暂无数据
                viewHolder.titleTv.setText("");
                viewHolder.titleIconIv.setVisibility(View.GONE);
                viewHolder.titleTv.setVisibility(View.GONE);
                setViewGone(0, viewHolder.viewList);
            } else {
                for (int i = 0; i <= 5; i++) {
                    if (data.size() > i) {
                        setViewGVisible(viewHolder.viewList.get(i));
                        if (TextUtils.isEmpty(data.get(i).getImg())) {
                            setPosterData(viewHolder.viewList.get(i), R.drawable.default_member_center_240_360_v2, data.get(i));
                        } else {
                            setPosterData(viewHolder.viewList.get(i), data.get(i).getImg(), data.get(i));
                        }
                    } else {
                        if (viewHolder.viewList.get(i).hasFocus()) {
                            View view = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                            viewHolder.itemView,
                                    viewHolder.viewList.get(i), View.FOCUS_LEFT);
                            if (view != null) {
                                view.requestFocus();
                            } else {
                                view = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                                viewHolder.itemView,
                                        viewHolder.viewList.get(i), View.FOCUS_RIGHT);
                                if (view != null) {
                                    view.requestFocus();
                                }
                            }
                        }
                        setViewGone(viewHolder.viewList.get(i));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setDataList:Exception:" + e.toString());
        }
    }


    public void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void goneView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    private void setViewGone(FrameLayout container) {
        try {
            int count = container.getChildCount();
            for (int index = 0; index < count; index++) {
                container.getChildAt(index).setVisibility(View.GONE);
            }
            container.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setViewGone:Exception:" + e.toString());
        }
    }

    private void setViewGVisible(FrameLayout container) {
        try {
            int count = container.getChildCount();
            for (int index = 0; index < count; index++) {
                container.getChildAt(index).setVisibility(View.VISIBLE);
            }
            ImageView img = container.findViewWithTag(TAG_POSTER_FOCUS);

            if (container.hasFocus()) {
                img.setVisibility(View.VISIBLE);//隐藏焦点框
            } else {
                img.setVisibility(View.GONE);//隐藏焦点框
            }
            container.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setViewGVisible:Exception:" + e.toString());
        }
    }

    private void setViewGone(int start, List<FrameLayout> viewList) {
        try {
            int size = viewList.size();
            for (int i = start; i < size; i++) {
                FrameLayout frameLayout = viewList.get(i);
                int count = frameLayout.getChildCount();
                for (int index = 0; index < count; index++) {
                    frameLayout.getChildAt(index).setVisibility(View.GONE);
                }
                frameLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setViewGone:Exception:" + e.toString());
        }
    }

    /**
     * 设置二维码图片imageView
     *
     * @param qrCodeString
     */
    public void setQrCodeImageView(String qrCodeString) {
        try {
            Log.d(TAG, "wqs:setQrCodeImageView");
            mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            if (!TextUtils.isEmpty(qrCodeString)) {
                mQrcodeUtil.createQRImage(qrCodeString, mContext.getResources().getDimensionPixelOffset(R.dimen.height_617px),
                        mContext.getResources().getDimensionPixelOffset(R.dimen.height_617px), mBitmap, mRecommendQrCodeImageView);
            } else {
                mRecommendQrCodeImageView.setBackgroundResource(R.drawable.default_member_center_qr_code_v2);
            }
        } catch (Exception e) {
            Log.e(TAG, "wqs:setQrCodeImageView:Exception:" + e.toString());
        }
    }

    //设置会员推荐位海报
    private void setRecommendPosterData(View mModuleView, Page page) {
        try {
            String imageUrl = null;
            int defaultHolder = 0;
            ImageView posterImageView = (ImageView) mModuleView.findViewWithTag
                    (TAG_POSTER_IMAGE);
            if (page != null) {
                if (page.getPrograms() != null && page.getPrograms().size() > 0) {
                    imageUrl = page.getPrograms().get(0).getImg();
                } else {
                    Log.e(TAG, "wqs:setRecommendPosterData:UserCenterPageBean.data == null");
                }
            } else {
                Log.e(TAG, "wqs:setRecommendPosterData:UserCenterPageBean == null");
            }
            //设置会员促销推荐位海报
            if (mModuleView.getId() == R.id.id_member_center_promotion_recommend) {
                defaultHolder = R.drawable.default_member_center_1296_400_v2;
            }
            //设置会员权益介绍推荐位海报
            else if (mModuleView.getId() == R.id.id_member_center_interests_introduce) {
                defaultHolder = R.drawable.default_member_center_1680_200_v2;
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.get().load(imageUrl).fit().placeholder(defaultHolder).error(defaultHolder).into(posterImageView);
            } else {
                Picasso.get().load(defaultHolder).fit().placeholder(defaultHolder).error(defaultHolder).into(posterImageView);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setRecommendPosterData:Exception:" + e.toString());
        }
    }

    //设置textView显示title
    public void setTitleView(TextView textView, String title) {
        if (textView != null) {
            if (TextUtils.isEmpty(title) || TextUtils.equals(title, "null")) {
                goneView(textView);
                textView.setText("");
            } else {
                showView(textView);
                textView.setText(title);
            }
        }
    }

    private void setPosterData(View mModuleView, Object img, Program bean) {
        try {
            if (mModuleView != null) {
                mModuleView.setVisibility(View.VISIBLE);
                RecycleImageView posterImageview = (RecycleImageView) mModuleView.findViewWithTag
                        (TAG_POSTER_IMAGE);
                ImageView focusImageview = (ImageView) mModuleView.findViewWithTag(TAG_POSTER_FOCUS);
                //海报下方主标题
                TextView mainTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_MAIN_TITLE);
                //右上角角标
                RecycleImageView rightTopMarkImageView = (RecycleImageView) mModuleView.findViewWithTag(TAG_POSTER_RIGHT_TOP_MARK);
                //海报图节目更新状态标题
                TextView recentTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_PROGRAM_UPDATE_TITLE);
                hideView(recentTitleTextView);
                //海报图评分标题
                TextView scoreTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_SCORE_TITLE);
                hideView(scoreTitleTextView);
                FrameLayout superScriptFrameLayout = mModuleView.findViewWithTag(TAG_COENER_CONTAINER);
                if (posterImageview != null && img != null) {
                    if (img instanceof String) {
                        if (!TextUtils.isEmpty((String) img)) {
                            posterImageview.placeHolder(R.drawable.default_member_center_240_360_v2)
                                    .errorHolder(R.drawable.default_member_center_240_360_v2)
                                    .hasCorner(true)
                                    .load((String) img);
                        } else {
                            posterImageview.placeHolder(R.drawable.default_member_center_240_360_v2)
                                    .errorHolder(R.drawable.default_member_center_240_360_v2)
                                    .hasCorner(true)
                                    .load(R.drawable.default_member_center_240_360_v2);
                        }
                    } else {
                        posterImageview.placeHolder(R.drawable.default_member_center_240_360_v2)
                                .errorHolder(R.drawable.default_member_center_240_360_v2)
                                .hasCorner(true)
                                .load((int) img);
                    }
                }
                if (bean != null) {
                    setTitleView(mainTitleTextView, bean.getTitle());
                    SuperScriptManager.getInstance().processSuperscript(
                            mModuleView.getContext(),
                            "layout_008",
                            ((ViewGroup) mModuleView).indexOfChild(posterImageview),
                            bean,
                            (ViewGroup) superScriptFrameLayout);
                } else {
                    //由于数据为空，令各个显示的view隐藏，副标题gone
                    hideView(mainTitleTextView);
                    hideView(scoreTitleTextView);
                    hideView(rightTopMarkImageView);
                    hideView(recentTitleTextView);
                }
            } else {
                Log.e(TAG, "wqs:setPosterData:mModuleView == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setPosterData:Exception:" + e.toString());
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener, View.OnKeyListener {
        private ImageView titleIconIv;
        private TextView titleTv;
        private FrameLayout mModuleView1, mModuleView2, mModuleView3, mModuleView4, mModuleView5,
                mModuleView6;
        private List<FrameLayout> viewList;

        public ContentViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_8_title);
            mModuleView1 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view1);
            mModuleView2 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view2);
            mModuleView3 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view3);
            mModuleView4 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view4);
            mModuleView5 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view5);
            mModuleView6 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view6);
            viewList = new ArrayList<>();
            viewList.add(mModuleView1);
            viewList.add(mModuleView2);
            viewList.add(mModuleView3);
            viewList.add(mModuleView4);
            viewList.add(mModuleView5);
            viewList.add(mModuleView6);
            mModuleView1.setOnFocusChangeListener(this);
            mModuleView2.setOnFocusChangeListener(this);
            mModuleView3.setOnFocusChangeListener(this);
            mModuleView4.setOnFocusChangeListener(this);
            mModuleView5.setOnFocusChangeListener(this);
            mModuleView6.setOnFocusChangeListener(this);
            mModuleView1.setOnKeyListener(this);
            mModuleView2.setOnKeyListener(this);
            mModuleView3.setOnKeyListener(this);
            mModuleView4.setOnKeyListener(this);
            mModuleView5.setOnKeyListener(this);
            mModuleView6.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            try {
                if (hasFocus) {
                    onItemGetFocus(view);
                } else {
                    onItemLoseFocus(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:ContentViewHolder:onFocusChange:Exception:" + e.toString());
            }

        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (listener != null) {
                            int position = 0;
                            switch (v.getId()) {
                                case R.id.id_module_8_view1:
                                    position = 0;
                                    break;
                                case R.id.id_module_8_view2:
                                    position = 1;
                                    break;
                                case R.id.id_module_8_view3:
                                    position = 2;
                                    break;
                                case R.id.id_module_8_view4:
                                    position = 3;
                                    break;
                                case R.id.id_module_8_view5:
                                    position = 4;
                                    break;
                                case R.id.id_module_8_view6:
                                    position = 5;
                                    break;
                            }
                            if (mList != null && mList.size() != 0) {
                                if (mList.get(getAdapterPosition() - 1).getPrograms() != null && mList.get
                                        (getAdapterPosition() - 1).getPrograms().size() != 0) {

                                    listener.onItemClick(v, position, mList.get(getAdapterPosition() - 1)
                                            .getPrograms().get(position));
                                }
                            }

                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        View rightView = FocusFinder.getInstance().findNextFocus((ViewGroup) v.getParent
                                        ().getParent(), v,
                                View.FOCUS_RIGHT);
                        if (rightView == null) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:ContentViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }
    }

    class InfoViewHolder extends RecyclerView.ViewHolder implements
            View.OnKeyListener, View.OnFocusChangeListener {

        private ImageView mMemberHead, mMemberMark;
        private TextView mMemberName, mMemberTime;
        private FrameLayout mBtnLogin, mBtnOpen, mBtnExchange, mBtnOrder, mBtnDrama;

        public InfoViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            //会员信息控件
            mMemberHead = (ImageView) itemView.findViewById(R.id.id_member_center_head);
            mMemberMark = (ImageView) itemView.findViewById(R.id.id_member_center_mark);
            mMemberName = (TextView) itemView.findViewById(R.id.id_member_center_name);
            mMemberTime = (TextView) itemView.findViewById(R.id.id_member_center_time);
            mBtnLogin = (FrameLayout) itemView.findViewById(R.id.id_member_center_btn_login);
            //按钮列表
            mBtnOpen = (FrameLayout) itemView.findViewById(R.id.id_member_center_btn_open);
            mBtnExchange = (FrameLayout) itemView.findViewById(R.id.id_member_center_btn_exchange);
            mBtnOrder = (FrameLayout) itemView.findViewById(R.id.id_member_center_btn_order);
            mBtnDrama = (FrameLayout) itemView.findViewById(R.id.id_member_center_btn_drama_library);
//            setBoldText(mBtnLogin);
//            setBoldText(mBtnOpen);
//            setBoldText(mBtnExchange);
//            setBoldText(mBtnOrder);
//            setBoldText(mBtnDrama);
            mBtnLogin.setOnKeyListener(this);
            mBtnOpen.setOnKeyListener(this);
            mBtnExchange.setOnKeyListener(this);
            mBtnOrder.setOnKeyListener(this);
            mBtnDrama.setOnKeyListener(this);

            mBtnLogin.setOnFocusChangeListener(this);
            mBtnOpen.setOnFocusChangeListener(this);
            mBtnExchange.setOnFocusChangeListener(this);
            mBtnOrder.setOnFocusChangeListener(this);
            mBtnDrama.setOnFocusChangeListener(this);
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (listener != null) {
                            listener.onItemClick(v, TYPE_MEMBER_INFO, null);
                        }
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:InfoViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }

        //将中文字体设为粗体
        public void setBoldText(View view) {
            TextView mBtnText = view.findViewWithTag(TAG_BUTTON_TEXT);
            mBtnText.getPaint().setFakeBoldText(true);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            try {
                TextView mBtnText = view.findViewWithTag(TAG_BUTTON_TEXT);
                if (hasFocus) {
                    switch (view.getId()) {
                        case R.id.id_member_center_btn_open:
                        case R.id.id_member_center_btn_exchange:
                        case R.id.id_member_center_btn_order:
                        case R.id.id_member_center_btn_drama_library:
                            mBtnText.setTextColor(R.color.color_member_center_btn);
                            view.setBackgroundResource(R.drawable.member_center_btn_focus_v2);
                            break;
                        case R.id.id_member_center_btn_login:
                            mBtnText.setTextColor(R.color.color_member_center_btn);
                            view.setBackgroundResource(R.drawable.member_center_btn_focus_v2);
                            BtnLoginFocusStatus = true;
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (view.getId()) {
                        case R.id.id_member_center_btn_open:
                        case R.id.id_member_center_btn_exchange:
                        case R.id.id_member_center_btn_order:
                        case R.id.id_member_center_btn_drama_library:
                            mBtnText.setTextColor(Color.parseColor("#99FFFFFF"));
                            view.setBackgroundResource(R.drawable.member_center_btn_normal_v2);
                            break;
                        case R.id.id_member_center_btn_login:
                            mBtnText.setTextColor(Color.parseColor("#99FFFFFF"));
                            view.setBackgroundResource(R.drawable.member_center_btn_normal_v2);
                            BtnLoginFocusStatus = false;
                            break;
                        default:
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:InfoViewHolder:onFocusChange:Exception:" + e.toString());
            }


        }
    }

    //会员促销item
    class PromotionRecommendViewHolder extends RecyclerView.ViewHolder implements
            View.OnKeyListener, View.OnFocusChangeListener {

        private ImageView mQrCodeImageView, mQrScanIconImageview;
        private FrameLayout mQrRoot, mPromotionRecommend;

        public PromotionRecommendViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            //二维码
            mQrRoot = (FrameLayout) itemView.findViewById(R.id.id_member_center_qr_root);
            mQrCodeImageView = (ImageView) itemView.findViewById(R.id.id_member_center_qr_code);
            mQrScanIconImageview = (ImageView) itemView.findViewById(R.id.id_member_center_qr_scan_icon);
            //会员促销推荐位
            mPromotionRecommend = (FrameLayout) itemView.findViewById(R.id.id_member_center_promotion_recommend);
            mQrRoot.setOnKeyListener(this);
            mPromotionRecommend.setOnKeyListener(this);

            mQrRoot.setOnFocusChangeListener(this);
            mPromotionRecommend.setOnFocusChangeListener(this);
            if (mPromotionRecommend != null) {
                mPromotionRecommend.requestFocus();
            }
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            try {
                if (hasFocus) {
                    onItemGetFocus(view);
                    if (view.getId() == R.id.id_member_center_promotion_recommend) {
                        listener.onItemFocusChange(view, true, 1, null);
                    }
                } else {
                    onItemLoseFocus(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:RecommendViewHolder:onFocusChange:Exception:" + e.toString());
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (listener != null) {
                            listener.onItemClick(v, TYPE_MEMBER_RECOMMEND_PROMOTION, null);
                        }
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:RecommendViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }
    }

    //会员权益介绍item
    class InterestsRecommendViewHolder extends RecyclerView.ViewHolder implements
            View.OnKeyListener, View.OnFocusChangeListener {

        private FrameLayout mInterestsIntroduce;

        public InterestsRecommendViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            //会员权益介绍
            mInterestsIntroduce = (FrameLayout) itemView.findViewById(R.id.id_member_center_interests_introduce);
            mInterestsIntroduce.setOnKeyListener(this);
            mInterestsIntroduce.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            try {
                if (hasFocus) {
                    onItemGetFocus(view);
                } else {
                    onItemLoseFocus(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:RecommendViewHolder:onFocusChange:Exception:" + e.toString());
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (listener != null) {
                            listener.onItemClick(v, TYPE_MEMBER_RECOMMEND_INTERESTS, null);
                        }
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:RecommendViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }
    }

    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
