package tv.newtv.cboxtv.uc;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Corner;
import com.newtv.libs.Constant;
import com.newtv.libs.bean.AdBean;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2
 * 类描述：我的页面dapter
 * 创建人：gaoleichao
 * 创建时间：
 * 创建日期：2018/3/27
 * 修改人：wqs
 * 修改时间：15:25
 * 修改备注：2018/9/4
 * 修改备注：二期需求修改
 */
public class UserCenterAdapter extends BaseRecyclerAdapter<UserCenterPageBean, RecyclerView
        .ViewHolder> {
    private final String TAG = "UserCenterAdapter";
    private static final int TYPE_HEAD = 1001;
    private static final int TYPE_AD = 1002;
    private final String TAG_POSTER_FOCUS = "tag_poster_focus";//海报焦点
    private final String TAG_POSTER_IMAGE = "tag_poster_image";//海报图
    private final String TAG_POSTER_HEAD_IMAGE = "tag_poster_head_image";//用户头像
    private final String TAG_POSTER_MEMBER_MARK_IMAGE = "tag_poster_member_mark_image";//用户会员标识
    private final String TAG_POSTER_PROMPT_LOGIN = "tag_poster_prompt_login";//提示登录
    private final String TAG_POSTER_PROMPT_MEMBER = "tag_poster_prompt_member";//提示开通会员
    private final String TAG_POSTER_MAIN_TITLE = "tag_poster_main_title";//海报下方主标题
    private final String TAG_POSTER_SUB_TITLE = "tag_poster_sub_title";//海报下方副标题
    private final String TAG_POSTER_RIGHT_TOP_MARK = "tag_poster_right_top_mark";//右上角角标
    private final String TAG_POSTER_PROGRAM_UPDATE_ROOT = "tag_poster_program_update_root";//海报图节目更新状态标题
    private final String TAG_POSTER_PROGRAM_UPDATE_TITLE_LEFT = "tag_poster_program_update_title_left";//海报图节目更新状态标题左边部分
    private final String TAG_POSTER_PROGRAM_UPDATE_TITLE_CENTER = "tag_poster_program_update_title_center";//海报图节目更新状态标题中间变颜色部分
    private final String TAG_POSTER_PROGRAM_UPDATE_TITLE_RIGHT = "tag_poster_program_update_title_right";//海报图节目更新状态标题右边部分
    private final String TAG_POSTER_SCORE_TITLE = "tag_poster_score_title";//海报图评分标题
    private Context context;
    private Interpolator mSpringInterpolator;
    private OnRecycleItemClickListener<UserCenterPageBean.Bean> listener;
    private View firstView;
    private FrameLayout mBtnlogin, mBtnMember, mBtnVersion;
    private FrameLayout mBannerADImage;//加载广告图片的载体
    private AdBean.Material mBannerAdData;//广告数据
    private String mLoginTokenString;//登录token,用于判断登录状态
    private TextView mAdTitle;
    private ImageView mAdTitleIcon;
    private String memberStatusString = "member_open_not";
    private String sign_member_open_not = "member_open_not";//未开通会员
    private String sign_member_open_lose = "member_open_lose";//已开通，但失效
    private String sign_member_open_good = "member_open_good";//已开通，有效

    UserCenterAdapter(Context context, OnRecycleItemClickListener<UserCenterPageBean.Bean>
            listener) {
        this.context = context;
        this.listener = listener;
        mSpringInterpolator = new OvershootInterpolator(2.2f);
    }

    public View getFirstView() {
        return firstView;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        try {
            if (viewType == TYPE_HEAD) {
                viewHolder = new HeadViewHolder(LayoutInflater.from(context).inflate(R.layout
                        .fragment_usercenter_btn_v2, parent, false));
            } else if (viewType == TYPE_AD) {
                viewHolder = new ADViewHolder(LayoutInflater.from(context).inflate(R.layout
                        .fragment_usercenter_ad_banner_v2, parent, false));
            } else {
                viewHolder = new ContentViewHolder(LayoutInflater.from(context).inflate(R.layout
                        .fragment_usercenter_content_v2, parent, false));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onCreateViewHolder:Exception:" + e.toString());
        }
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        try {
            if (position == 0) {
                return TYPE_HEAD;
            } else if (position == 2) {
                return TYPE_AD;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:getItemViewType:Exception:" + e.toString());
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mList != null && mList.size() > 0) {
            return mList.size() + 2;
        }
        return 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof HeadViewHolder) {
                HeadViewHolder viewHolder = (HeadViewHolder) holder;
                mBtnlogin = viewHolder.mBtnLogin;
                mBtnMember = viewHolder.mBtnMember;
                mBtnVersion = viewHolder.mBtnVersion;
                //获取登录状态
                setLoginStatus(mLoginTokenString);
                //获取版本升级状态
                setVersionUpdate(Constant.VERSION_UPDATE);
                //获取用户会员状态
                setMemberStatus(memberStatusString);
                setPosterData(viewHolder.mBtnHistory, R.drawable.uc_history_v2, null);
                setPosterData(viewHolder.mBtnSubscribe, R.drawable.uc_subscribe_v2, null);
                setPosterData(viewHolder.mBtnCollect, R.drawable.uc_collection_v2, null);
                setPosterData(viewHolder.mBtnAttention, R.drawable.uc_attention_v2, null);
                setPosterData(viewHolder.mBtnAbout, R.drawable.uc_about_v2, null);
                setPosterData(viewHolder.mBtnLogin, R.drawable.uc_login_normal_v2, null);
                setPosterData(viewHolder.mBtnSetting, R.drawable.uc_setting_v2, null);
                setPosterData(viewHolder.mBtnOrder, R.drawable.uc_order_v2, null);

            } else if (holder instanceof ADViewHolder) {
                ADViewHolder viewHolder = (ADViewHolder) holder;
                mAdTitle = viewHolder.titleTv;
                mAdTitleIcon = viewHolder.titleIconIv;
                mBannerADImage = viewHolder.mAdBanner;
                if (mBannerAdData != null && !TextUtils.isEmpty(mBannerAdData.filePath)) {
                    setADPosterData(mBannerADImage, mBannerAdData.filePath);
                    showView(mBannerADImage);
                } else {
                    goneView(mBannerADImage);
                }
            } else if (holder instanceof ContentViewHolder) {
                ContentViewHolder viewHolder = (ContentViewHolder) holder;
                UserCenterPageBean moduleItem = null;
                if (position == 1) {
                    moduleItem = mList.get(position - 1);
                } else {
                    moduleItem = mList.get(position - 2);
                }
                setDataList(position, viewHolder, moduleItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onBindViewHolder:Exception:" + e.toString());
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
            ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.4f, Animation.RELATIVE_TO_SELF, 0.4f);
            sa.setFillAfter(true);
            sa.setDuration(150);
            view.startAnimation(sa);
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
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.4f, Animation.RELATIVE_TO_SELF, 0.4f);
            sa.setFillAfter(true);
            sa.setDuration(150);
            view.bringToFront();
            view.startAnimation(sa);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:onItemGetFocus:Exception:" + e.toString());
        }
    }

    private void setDataList(int position, ContentViewHolder viewHolder, UserCenterPageBean
            entity) {
        try {
            showView(viewHolder.titleIconIv);
            showView(viewHolder.titleTv);
            if (viewHolder.titleTv != null) {
                viewHolder.titleTv.setText(entity.title);
            }
            List<UserCenterPageBean.Bean> data = entity.data;
            int startIndex = 1;
            if (data == null || data.size() == 0) {
                //暂无数据
                switch (position) {
                    case 1:
                        startIndex = 1;
                        setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_history, null);
                        break;
                    case 3:
                        startIndex = 1;
                        setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_subscribe, null);
                        break;
                    case 4:
                        startIndex = 1;
                        setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_collect, null);
                        break;
                    case 5:
                        startIndex = 1;
                        setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_attention, null);
                        break;
                    case 6:
                        hideView(viewHolder.titleIconIv);
                        if (viewHolder.titleTv != null) {
                            viewHolder.titleTv.setText(entity.title);
                        }
                        hideView(viewHolder.titleTv);
                        setPosterData(viewHolder.viewList.get(0), null, null);
                        startIndex = 0;
                        break;
                    default:
                        break;

                }
                setViewGone(startIndex, viewHolder.viewList);
            } else {
                for (int i = 0; i <= 5; i++) {
                    if (data.size() > i) {
                        setViewGVisible(viewHolder.viewList.get(i));
                        Object imageUrl = null;
                        if ((!TextUtils.isEmpty(data.get(i).get_imageurl()) && !TextUtils.equals(data.get(i).get_imageurl(), "null"))) {
                            imageUrl = data.get(i).get_imageurl();
                        } else {
                            imageUrl = R.drawable.deful_user;
                        }
                        setPosterData(viewHolder.viewList.get(i), imageUrl, data.get(i));
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

    /**
     * 判断应用版本是否有更新
     *
     * @param status
     */
    public void setVersionUpdate(boolean status) {
        try {
            Log.d(TAG, "wqs:setVersionUpdate:" + status);
            ImageView posterImageView = null;
            if (mBtnVersion != null) {
                posterImageView = (ImageView) mBtnVersion.findViewWithTag(TAG_POSTER_IMAGE);

            } else {
                Log.d(TAG, "wqs:mBtnVersion==null");
            }
            if (status) {
                if (posterImageView != null) {
                    posterImageView.setBackgroundResource(R.drawable.uc_version_select_v2);
                }
            } else {
                if (posterImageView != null) {
                    posterImageView.setBackgroundResource(R.drawable.uc_version_normal_v2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setVersionUpdate:Exception:" + e.toString());
        }
    }

    /**
     * 判断登录状态
     *
     * @param tokenString
     */
    public void setLoginStatus(String tokenString) {
        try {
            mLoginTokenString = tokenString;
            ImageView mHeadImage = null;
            TextView mPromptTextView = null;
            if (mBtnlogin != null) {
                mHeadImage = mBtnlogin.findViewWithTag(TAG_POSTER_HEAD_IMAGE);
                mPromptTextView = mBtnlogin.findViewWithTag(TAG_POSTER_PROMPT_LOGIN);
                if (mHeadImage != null && mPromptTextView != null) {
                    if (!TextUtils.isEmpty(tokenString)) {
                        mHeadImage.setBackgroundResource(R.drawable.uc_head_login_v2);
                        mPromptTextView.setText(context.getResources().getString(R.string.user_already_login));
                    } else {
                        Log.d(TAG, "wqs:setLoginStatus:tokenString==null");
                        mHeadImage.setBackgroundResource(R.drawable.uc_head_not_login_v2);
                        mPromptTextView.setText(context.getResources().getString(R.string.user_prompt_login));
                    }
                } else {
                    Log.d(TAG, "wqs:setLoginStatus:mHeadImage==null:" + (mHeadImage == null));
                    Log.d(TAG, "wqs:setLoginStatus:mPromptTextView==null:" + (mPromptTextView == null));
                }
            } else {
                Log.d(TAG, "wqs:setLoginStatus:mBtnlogin==null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setLoginStatus:Exception:" + e.toString());
        }
    }

    /**
     * 判断会员状态
     *
     * @param memberStatus
     */
    public void setMemberStatus(String memberStatus) {
        try {
            Log.d(TAG, "wqs:setMemberStatus:" + memberStatus);
            memberStatusString = memberStatus;
            ImageView mMemberImage = null;
            ImageView mMarkImage = null;
            TextView mPromptTextView = null;
            if (mBtnMember != null) {
                mMemberImage = mBtnMember.findViewWithTag(TAG_POSTER_IMAGE);
                mPromptTextView = mBtnMember.findViewWithTag(TAG_POSTER_PROMPT_MEMBER);
                mMarkImage = mBtnlogin.findViewWithTag(TAG_POSTER_MEMBER_MARK_IMAGE);
                if (mMemberImage != null && mPromptTextView != null) {
                    if (TextUtils.equals(memberStatus, sign_member_open_good)) {
                        mMemberImage.setBackgroundResource(R.drawable.uc_open_member_select_v2);
                        mPromptTextView.setText(context.getResources().getString(R.string.user_already_member));
                        if (mMarkImage != null) {
                            mMarkImage.setBackgroundResource(R.drawable.uc_head_member_mark_v2);
                            mMarkImage.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(TAG, "wqs:setMemberStatus:mMarkImage == null");
                        }
                    } else if (TextUtils.equals(memberStatus, sign_member_open_lose)) {
                        mMemberImage.setBackgroundResource(R.drawable.uc_open_member_normal_v2);
                        mPromptTextView.setText(context.getResources().getString(R.string.user_prompt_member));
                        if (mMarkImage != null) {
                            mMarkImage.setBackgroundResource(R.drawable.uc_head_not_member_mark_v2);
                            mMarkImage.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(TAG, "wqs:setMemberStatus:mMarkImage == null");
                        }
                    } else {
                        mMemberImage.setBackgroundResource(R.drawable.uc_open_member_normal_v2);
                        mPromptTextView.setText(context.getResources().getString(R.string.user_prompt_member));
                        if (mMarkImage != null) {
                            mMarkImage.setBackgroundResource(R.drawable.uc_head_not_member_mark_v2);
                            mMarkImage.setVisibility(View.GONE);
                        } else {
                            Log.d(TAG, "wqs:setMemberStatus:mMarkImage == null");
                        }
                    }
                } else {
                    Log.d(TAG, "wqs:setMemberStatus:mMemberImage==null:" + (mMemberImage == null));
                    Log.d(TAG, "wqs:setMemberStatus:mPromptTextView==null:" + (mPromptTextView == null));
                }
            } else {
                Log.d(TAG, "wqs:setMemberStatus:mBtnlogin==null");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setMemberStatus:Exception:" + e.toString());
        }
    }


    /**
     * 获取广告数据
     *
     * @param bannerAdData
     */
    public void setAdData(AdBean.Material bannerAdData) {
        try {
            if (bannerAdData != null && !TextUtils.isEmpty(bannerAdData.filePath)) {
                mBannerAdData = bannerAdData;
//                setADPosterData(mBannerADImage, bannerAdData.filePath);
//                showView(mBannerADImage);
            } else {
//                goneView(mBannerADImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setAdData:Exception:" + e.toString());
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
                goneView(container.getChildAt(index));
            }
            goneView(container);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setViewGone:Exception:" + e.toString());
        }
    }

    private void setViewGVisible(FrameLayout container) {
        try {
            int count = container.getChildCount();
            for (int index = 0; index < count; index++) {
                showView(container.getChildAt(index));
            }
            ImageView focusImageview = container.findViewWithTag(TAG_POSTER_FOCUS);

            if (container.hasFocus()) {
                showView(focusImageview);//显示焦点框
            } else {
                goneView(focusImageview);//隐藏焦点框
            }
            showView(container);
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
                    goneView(frameLayout.getChildAt(index));
                }
                goneView(frameLayout);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setViewGone:Exception:" + e.toString());
        }
    }

    private void setADPosterData(View mModuleView, String imgUrl) {
        try {
            if (mModuleView != null) {
                RecycleImageView posterImageview = (RecycleImageView) mModuleView.findViewWithTag
                        (TAG_POSTER_IMAGE);
                if (!TextUtils.isEmpty(imgUrl)) {
                    hideView(mAdTitle);
                    hideView(mAdTitleIcon);
                    posterImageview.placeHolder(R.drawable.default_member_center_1680_320_v2)
                            .errorHolder(R.drawable.default_member_center_1680_320_v2)
                            .hasCorner(true)
                            .load(imgUrl);
                } else {
                    hideView(mAdTitle);
                    hideView(mAdTitleIcon);
                    posterImageview.placeHolder(R.drawable.default_member_center_1680_320_v2)
                            .errorHolder(R.drawable.default_member_center_1680_320_v2)
                            .hasCorner(true)
                            .load(R.drawable.default_member_center_1680_320_v2);
                }
            } else {
                Log.d(TAG, "wqs:setADPosterData:mModuleView == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setADPosterData:Exception:" + e.toString());
        }
    }

    private void setPosterData(View mModuleView, Object img, UserCenterPageBean.Bean bean) {
        try {
            String mainTitle = null;
            String subTitle = null;
            String scoreTitle = null;
            if (mModuleView != null) {
                mModuleView.setVisibility(View.VISIBLE);
                RecycleImageView posterImageview = (RecycleImageView) mModuleView.findViewWithTag
                        (TAG_POSTER_IMAGE);
                ImageView focusImageview = (ImageView) mModuleView.findViewWithTag(TAG_POSTER_FOCUS);
                //海报下方主标题
                TextView mainTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_MAIN_TITLE);
                //海报下方副标题
                TextView subTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_SUB_TITLE);
                //右上角角标
                RecycleImageView rightTopMarkImageView = (RecycleImageView) mModuleView.findViewWithTag(TAG_POSTER_RIGHT_TOP_MARK);
                //海报图节目更新状态标题父布局
                RelativeLayout programUpdateRoot = (RelativeLayout) mModuleView.findViewWithTag(TAG_POSTER_PROGRAM_UPDATE_ROOT);
                //海报图节目更新状态标题左边部分
                TextView programUpdateLeftTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_PROGRAM_UPDATE_TITLE_LEFT);
                //海报图节目更新状态标题中间变色部分
                TextView programUpdateCenterTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_PROGRAM_UPDATE_TITLE_CENTER);
                //海报图节目更新状态标题中间变色部分
                TextView programUpdateRightTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_PROGRAM_UPDATE_TITLE_RIGHT);
                //海报图评分标题
                TextView scoreTitleTextView = (TextView) mModuleView.findViewWithTag(TAG_POSTER_SCORE_TITLE);
                if (posterImageview != null && img != null) {
                    if (img instanceof String) {
                        if (!TextUtils.isEmpty((String) img)) {
                            disPlayItemData(posterImageview, img);
                        } else {
                            disPlayItemData(posterImageview, img);
                        }
                    } else {
                        disPlayItemData(posterImageview, img);
                    }
                }
                if (bean != null) {
                    mainTitle = bean.get_title_name();
                    subTitle = UserCenterRecordManager.getInstance().getWatchProgress(bean.getPlayPosition(), bean.getDuration());
                    scoreTitle = bean.getGrade();
                    setTitleView(mainTitleTextView, mainTitle);
                    setTitleView(subTitleTextView, subTitle);
                    setTitleView(scoreTitleTextView, scoreTitle);
                    if (programUpdateLeftTitleTextView != null && programUpdateCenterTitleTextView != null) {
                        if (TextUtils.isEmpty(bean.getEpisode_num()) || TextUtils.equals(bean.getEpisode_num(), "null")) {
                            hideView(programUpdateRoot);
                        } else {
                            showView(programUpdateRoot);
                            programUpdateCenterTitleTextView.setText(bean.getEpisode_num());
                            if (Integer.parseInt(bean.getEpisode_num()) < Integer.parseInt(bean.getTotalCnt())) {
                                programUpdateLeftTitleTextView.setText(context.getResources().getString(R.string.user_poster_program_update_title_left_being));
                            } else {
                                programUpdateLeftTitleTextView.setText(context.getResources().getString(R.string.user_poster_program_update_title_left_end));
                            }
                        }
                    }
                    String isUpdate = bean.getIsUpdate();
                    String superscript = bean.getSuperscript();
                    if (TextUtils.equals(isUpdate, "1")) {
                        rightTopMarkImageView.load(R.drawable.superscript_update_episode);
                    } else {
                        if (!TextUtils.isEmpty(superscript) && !TextUtils.equals(superscript, "null")) {
                            Corner superscriptInfo;
                            superscriptInfo = SuperScriptManager.getInstance().getSuperscriptInfoById(superscript);
                            if (superscriptInfo != null && !TextUtils.isEmpty(superscriptInfo.getCornerImg()) && !TextUtils.equals(superscriptInfo.getCornerImg(), "null")) {
                                rightTopMarkImageView.load(superscriptInfo.getCornerImg());
                            } else {
                                hideView(rightTopMarkImageView);
                            }
                        } else {
                            hideView(rightTopMarkImageView);
                        }
                    }


                } else {
                    //由于数据为空，令各个显示的view隐藏，副标题gone
                    hideView(mainTitleTextView);
                    goneView(subTitleTextView);
                    hideView(scoreTitleTextView);
                    hideView(rightTopMarkImageView);
                    hideView(programUpdateRoot);
                }
            } else {
                Log.d(TAG, "wqs:setPosterData:mModuleView == null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "wqs:setPosterData:Exception:" + e.toString());
        }
    }

    private void disPlayItemData(RecycleImageView imageView, Object img) {
        if (img instanceof String) {
            imageView.placeHolder(R.drawable.default_member_center_240_360_v2)
                    .errorHolder(R.drawable.default_member_center_240_360_v2)
                    .hasCorner(true)
                    .load((String) img);
        } else {
            imageView.placeHolder(R.drawable.default_member_center_240_360_v2)
                    .errorHolder(R.drawable.default_member_center_240_360_v2)
                    .hasCorner(true)
                    .load((int) img);
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
            if (hasFocus) {
                onItemGetFocus(view);
            } else {
                onItemLoseFocus(view);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
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
                            default:
                                break;
                        }
                        if (mList != null && mList.size() != 0) {
                            int index;
                            if (getAdapterPosition() == 1) {
                                index = getAdapterPosition() - 1;
                            } else {
                                index = getAdapterPosition() - 2;
                            }
                            if (mList.get(index).data != null && mList.get
                                    (index).data.size() != 0) {

                                listener.onItemClick(v, position, mList.get(index)
                                        .data.get(position));
                            } else {
                                listener.onItemClick(v, getAdapterPosition(), null);
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
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "wqs:ContentViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder implements
            View.OnKeyListener, View.OnFocusChangeListener {

        private FrameLayout mBtnLogin, mBtnMember, mBtnOrder, mBtnHistory, mBtnSubscribe, mBtnCollect, mBtnAttention, mBtnVersion,
                mBtnAbout, mBtnSetting;

        public HeadViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            mBtnHistory = (FrameLayout) itemView.findViewById(R.id.id_user_btn_history);
            mBtnSubscribe = (FrameLayout) itemView.findViewById(R.id.id_user_btn_subscribe);
            mBtnCollect = (FrameLayout) itemView.findViewById(R.id.id_user_btn_collect);
            mBtnAttention = (FrameLayout) itemView.findViewById(R.id.id_user_btn_attention);
            mBtnVersion = (FrameLayout) itemView.findViewById(R.id.id_user_btn_version);
            mBtnAbout = (FrameLayout) itemView.findViewById(R.id.id_user_btn_about);
            mBtnLogin = (FrameLayout) itemView.findViewById(R.id.id_user_btn_login);
            mBtnMember = (FrameLayout) itemView.findViewById(R.id.id_user_btn_member);
            mBtnSetting = (FrameLayout) itemView.findViewById(R.id.id_user_btn_setting);
            mBtnOrder = (FrameLayout) itemView.findViewById(R.id.id_user_btn_order);

            mBtnHistory.setOnKeyListener(this);
            mBtnSubscribe.setOnKeyListener(this);
            mBtnCollect.setOnKeyListener(this);
            mBtnAttention.setOnKeyListener(this);
            mBtnVersion.setOnKeyListener(this);
            mBtnAbout.setOnKeyListener(this);
            mBtnLogin.setOnKeyListener(this);
            mBtnMember.setOnKeyListener(this);
            mBtnSetting.setOnKeyListener(this);
            mBtnOrder.setOnKeyListener(this);


            mBtnHistory.setOnFocusChangeListener(this);
            mBtnSubscribe.setOnFocusChangeListener(this);
            mBtnCollect.setOnFocusChangeListener(this);
            mBtnAttention.setOnFocusChangeListener(this);
            mBtnVersion.setOnFocusChangeListener(this);
            mBtnAbout.setOnFocusChangeListener(this);
            mBtnLogin.setOnFocusChangeListener(this);
            mBtnMember.setOnFocusChangeListener(this);
            mBtnSetting.setOnFocusChangeListener(this);
            mBtnOrder.setOnFocusChangeListener(this);

            firstView = mBtnLogin;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (listener != null) {
                        listener.onItemClick(v, 0, null);
                    }

                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "wqs:HeadViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(view);
            } else {
                onItemLoseFocus(view);
            }
        }
    }

    class ADViewHolder extends RecyclerView.ViewHolder implements
            View.OnKeyListener, View.OnFocusChangeListener {

        private FrameLayout mAdBanner;
        private TextView titleTv;
        private View mAdSpace;
        private ImageView titleIconIv;

        public ADViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            mAdBanner = (FrameLayout) itemView.findViewById(R.id.id_user_ad_banner);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_1_title);
            mAdSpace = (View) itemView.findViewById(R.id.id_user_ad_space);
            mAdBanner.setOnFocusChangeListener(this);
            mAdBanner.setOnKeyListener(this);

        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(view);
            } else {
                onItemLoseFocus(view);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            try {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (listener != null) {
                        listener.onItemClick(v, 2, null);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "wqs:ADViewHolder:onKey:Exception:" + e.toString());
            }
            return false;
        }
    }
}
