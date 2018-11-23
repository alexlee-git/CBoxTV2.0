package tv.newtv.cboxtv.uc;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Corner;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.jetbrains.annotations.NotNull;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;

/**
 * Created by gaoleichao on 2018/3/29.
 */

public class HistoryAdapter extends BaseRecyclerAdapter<UserCenterPageBean.Bean, RecyclerView
        .ViewHolder> {

    private Context context;
    private Interpolator mSpringInterpolator;

    private int defaultIcon;
    private int selectPostion = 0;
    private boolean mAllowLost = true;
    private View currentFocusView;
    private OnRecycleItemClickListener<UserCenterPageBean.Bean> listener;

    public HistoryAdapter(Context context, int defaultIcon,
                          OnRecycleItemClickListener<UserCenterPageBean.Bean> listener) {
        this.context = context;
        this.defaultIcon = defaultIcon;
        this.listener = listener;
        mSpringInterpolator = new OvershootInterpolator(2.2f);
    }

    public void removeItem(int position) {
        if (mList != null && mList.size() != 0 && position < mList.size()) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public int getSelectPostion() {
        return selectPostion;
    }

    public void setSelectPostion(int selectPostion) {
        this.selectPostion = selectPostion;
    }

    public void requestDefaultFocus() {
        if (currentFocusView != null) {
            currentFocusView.requestFocus();
        }
    }

    public void setAllowLostFocus(boolean allow) {
        mAllowLost = allow;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout
                .item_usercenter_universal, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryViewHolder) {
            HistoryViewHolder viewHolder = (HistoryViewHolder) holder;
            viewHolder.mImageIv.setVisibility(View.VISIBLE);
            viewHolder.mModuleView.setVisibility(View.VISIBLE);

            UserCenterPageBean.Bean entity = mList.get(position);

            // 标题
            if (!TextUtils.isEmpty(entity._title_name)) {
                viewHolder.mTitleTv.setVisibility(View.VISIBLE);
                viewHolder.mTitleTv.setText(entity._title_name);
            } else {
                viewHolder.mTitleTv.setText("");
            }

            Log.d("time", "name : " + entity.get_title_name() + ", time : " + entity
                    .getUpdateTime());

            // 评分
            String score = entity.getGrade();
            if (!TextUtils.isEmpty(score) && !TextUtils.equals(score, "null")) {
                viewHolder.mScore.setText(entity.getGrade());
            } else {
                viewHolder.mScore.setText("0.0");
            }
            // 观看进度
            viewHolder.mSubTitle.setText(UserCenterRecordManager.getInstance().getWatchProgress
                    (entity.getPlayPosition(), entity.getDuration()));

            // 更新剧集
            String episode = entity.getEpisode_num();
            if (!TextUtils.isEmpty(episode) && !TextUtils.equals("null", episode)) {
                String totalCnt = entity.getTotalCnt();

                int cnt = Integer.parseInt(totalCnt);
                int episodeNum = Integer.parseInt(episode);

                String videoType = entity.getVideoType();
                if (TextUtils.equals(videoType, "电视剧")) {
                    if (episodeNum < cnt) {
                        viewHolder.mEpisode.setText(String.format("更新至 %s 集", episode));
                    } else {
                        viewHolder.mEpisode.setText(String.format("%s 集全", episode));
                    }
                } else if (TextUtils.equals(videoType, "综艺")) {
                    if (episodeNum < cnt) {
                        viewHolder.mEpisode.setText(String.format("更新至 %s 期", episode));
                    } else {
                        viewHolder.mEpisode.setText(String.format("%s 期全", episode));
                    }
                }
            }

            // 角标
            if (viewHolder.mSuperscript != null) {
                if (!TextUtils.isEmpty(entity.getSuperscript())) {
                    loadSuperscript(viewHolder.mSuperscript, entity.getSuperscript());
                } else {
                    if (TextUtils.equals("1", entity.getIsUpdate())) {
                        Picasso.get().load(R.drawable.superscript_update_episode).into(viewHolder
                                .mSuperscript);
                    }
                }
            }

            // 海报
            if (!TextUtils.isEmpty(entity._imageurl) && entity._imageurl.startsWith("http")) {
                viewHolder.mImageIv.setScaleType(ImageView.ScaleType.FIT_XY);
                viewHolder.mImageIv.setVisibility(View.VISIBLE);
                RequestCreator picasso = Picasso.get()
                        .load(entity._imageurl)
                        .priority(Picasso.Priority.HIGH)
                        .stableKey(entity._imageurl)
                        .config(Bitmap.Config.ARGB_8888);
                picasso = picasso.placeholder(R.drawable.default_member_center_240_360_v2).error
                        (R.drawable.deful_user);
                picasso.transform(new PosterCircleTransform(context, 4)).into(viewHolder.mImageIv);

            } else {
                viewHolder.mImageIv.setScaleType(ImageView.ScaleType.FIT_XY);
                viewHolder.mImageIv.setVisibility(View.VISIBLE);
                RequestCreator picasso = Picasso.get()
                        .load(R.drawable.deful_user)
                        .priority(Picasso.Priority.HIGH)
                        .config(Bitmap.Config.ARGB_8888);
                picasso = picasso.placeholder(R.drawable.default_member_center_240_360_v2).error
                        (R.drawable.deful_user);
                picasso.transform(new PosterCircleTransform(context, 4)).into(viewHolder.mImageIv);
            }

//            Log.e("MM", "selectPostion=" + selectPostion + ",position=" + position + ",size=" +
// mList.size());
//            if (position == selectPostion || (selectPostion == mList.size() && position ==
// mList.size() - 1)) {
//                Log.e("MM", "if###########selectPostion=" + selectPostion + ",position=" +
// position + ",size=" + mList.size());
//                viewHolder.itemView.requestFocus();
//            } else {
//                viewHolder.itemView.clearFocus();
//                viewHolder.mFocusIv.setVisibility(View.INVISIBLE);
//            }

        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    private void onItemLoseFocus(View view, ImageView focusImageView) {
        if (focusImageView != null) {
            focusImageView.setVisibility(View.INVISIBLE);
        }

//        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
//        if (titleView != null) {
//            titleView.setSelected(false);
//        }

        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    private void onItemGetFocus(View view, ImageView focusImageView, int postion) {
        if (focusImageView != null) {
            focusImageView.setVisibility(View.VISIBLE);
        }

//        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
//        if (titleView != null) {
//            titleView.setSelected(true);
//        }
        currentFocusView = focusImageView;
        selectPostion = postion;

        if (!mAllowLost) return;

        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    private void loadSuperscript(ImageView target, String superscriptId) {
        Corner info = SuperScriptManager.getInstance().getSuperscriptInfoById(superscriptId);
        if (info != null) {
            String superUrl = info.getCornerImg();
            if (superUrl != null) {
                Picasso.get().load(superUrl).into(target);
            }
        }
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener, View.OnKeyListener {
        private View mModuleView;
        private TextView mTitleTv; // 主标
        private ImageView mFocusIv; // 焦点
        private ImageView mImageIv; // 海报
        private TextView mSubTitle; // 副标
        private TextView mScore;    // 评分
        private ImageView mSuperscript; // 角标
        private TextView mEpisode; // 剧集

        HistoryViewHolder(View itemView) {
            super(itemView);

            mModuleView = itemView.findViewById(R.id.id_module_view);
            mTitleTv = (TextView) itemView.findViewById(R.id.id_title);
            mImageIv = (ImageView) itemView.findViewById(R.id.id_poster);
            mFocusIv = (ImageView) itemView.findViewById(R.id.id_focus);
            mSubTitle = itemView.findViewById(R.id.id_subtitle);
            mScore = itemView.findViewById(R.id.id_score);
            mEpisode = itemView.findViewById(R.id.id_episode_data);
            mSuperscript = itemView.findViewById(R.id.id_superscript);

            mModuleView.setOnFocusChangeListener(this);
            mModuleView.setOnKeyListener(this);

            // DisplayUtils.adjustView(context, mImageIv, mFocusIv, R.dimen.width_27px, R.dimen
            // .height_27px);//UI适配
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.e("MM", "focus=" + hasFocus + ",mSllowlost=" + mAllowLost);
            if (hasFocus) {
                currentFocusView = mModuleView;
                onItemGetFocus(v, mFocusIv, getAdapterPosition());
                mTitleTv.setSelected(true);
            } else {
                if (mAllowLost) {
                    onItemLoseFocus(v, mFocusIv);
                }
                mTitleTv.setSelected(false);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (mList.size() > 0) {
                    listener.onItemClick(v, getAdapterPosition(), mList.get(getAdapterPosition()));
                    return true;
                }
            }
            return false;
        }
    }
}
