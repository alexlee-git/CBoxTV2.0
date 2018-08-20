package tv.newtv.cboxtv.uc;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;

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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout
                .item_all_history, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryViewHolder) {
            HistoryViewHolder viewHolder = (HistoryViewHolder) holder;
            viewHolder = (HistoryViewHolder) holder;
            viewHolder.mImageIv.setVisibility(View.VISIBLE);
            viewHolder.mModuleView.setVisibility(View.VISIBLE);
            if (mList != null && mList.size() != 0) {
                UserCenterPageBean.Bean entity = mList.get(position);
                if (!TextUtils.isEmpty(entity._title_name)) {
                    viewHolder.mTitleTv.setVisibility(View.VISIBLE);
                    viewHolder.mTitleTv.setText(entity._title_name);
                } else {
                    viewHolder.mTitleTv.setText("");
                }

                if (!TextUtils.isEmpty(entity._imageurl) && entity._imageurl.startsWith("http")) {
                    viewHolder.mImageIv.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewHolder.mImageIv.setVisibility(View.VISIBLE);
                    RequestCreator picasso = Picasso.with(LauncherApplication.AppContext)
                            .load(entity._imageurl)
                            .priority(Picasso.Priority.HIGH)
                            .stableKey(entity._imageurl)
                            .config(Bitmap.Config.RGB_565);
                    picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                            .deful_user);
                    picasso.transform(new PosterCircleTransform(context, 4))
                            .into(viewHolder.mImageIv);

                } else {
                    viewHolder.mImageIv.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewHolder.mImageIv.setVisibility(View.VISIBLE);
                    RequestCreator picasso = Picasso.with(LauncherApplication.AppContext)
                            .load(R.drawable.deful_user)
                            .priority(Picasso.Priority.HIGH)
                            .config(Bitmap.Config.RGB_565);
                    picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                            .deful_user);
                    picasso.transform(new PosterCircleTransform(context, 4)).into(viewHolder
                            .mImageIv);
                }

            } else {
                Picasso.with(context).load(defaultIcon).into(viewHolder.mImageIv);
                viewHolder.mTitleTv.setText("");
            }

            Log.e("MM", "selectPostion=" + selectPostion + ",position=" + position + ",size=" +
                    mList.size());
            if (position == selectPostion || (selectPostion == mList.size() && position == mList
                    .size() - 1)) {
                Log.e("MM", "if###########selectPostion=" + selectPostion + ",position=" +
                        position + ",size=" + mList.size());
                viewHolder.itemView.requestFocus();
            } else {
                viewHolder.itemView.clearFocus();
                viewHolder.mFocusIv.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() == 0) {
            return 1;
        } else {
            return mList.size();
        }
    }

    private void onItemLoseFocus(View view, ImageView focusImageView) {
        if (focusImageView != null) {
            focusImageView.setVisibility(View.INVISIBLE);
        }

        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        if (titleView != null) {
            titleView.setSelected(false);
        }

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

        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        if (titleView != null) {
            titleView.setSelected(true);
        }

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

    class HistoryViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener, View.OnKeyListener {
        private FrameLayout mModuleView;
        private TextView mTitleTv;
        private ImageView mFocusIv;
        private ImageView mImageIv;


        public HistoryViewHolder(View itemView) {
            super(itemView);

            mModuleView = (FrameLayout) itemView.findViewById(R.id.id_module_view);
            mTitleTv = (TextView) itemView.findViewById(R.id.tv_title);
            mImageIv = (ImageView) itemView.findViewById(R.id.iv_image);
            mFocusIv = (ImageView) itemView.findViewById(R.id.iv_focus);
            mModuleView.setOnFocusChangeListener(this);
            mModuleView.setOnKeyListener(this);

            DisplayUtils.adjustView(context, mImageIv, mFocusIv, R.dimen.width_17dp, R.dimen.width_17dp);//UI适配
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            Log.e("MM", "focus=" + hasFocus + ",mSllowlost=" + mAllowLost);
            if (hasFocus) {
                currentFocusView = mModuleView;
                onItemGetFocus(v, mFocusIv, getAdapterPosition());
                mTitleTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            } else {
                if (mAllowLost) {
                    onItemLoseFocus(v, mFocusIv);
                }
                mTitleTv.setEllipsize(null);
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
