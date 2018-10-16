package tv.newtv.cboxtv.cms.details.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.util.DisplayUtils;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * Created by gaoleichao on 2018/4/4.
 */

public class DetailsHorizontaVerticallAdapter extends BaseRecyclerAdapter<SubContent,
        DetailsHorizontaVerticallAdapter.DetailsHorizontalViewHolder> {
    private static String TAG = "DetailsHorizontalAdapter";
    private Context context;
    private Interpolator mSpringInterpolator;
    private RecyclerView mRecyclerView;

    public DetailsHorizontaVerticallAdapter(Context context, Interpolator mSpringInterpolator, RecyclerView mRecyclerView) {
        this.context = context;
        this.mSpringInterpolator = mSpringInterpolator;
        this.mRecyclerView = mRecyclerView;
    }

    @Override
    public DetailsHorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DetailsHorizontalViewHolder viewHolder = new DetailsHorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_details_vertical_slide_layout, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DetailsHorizontalViewHolder holder, int position) {
        SubContent entity = mList.get(position);
        if (entity != null) {
            if (holder.posterIv != null) {
                if (!TextUtils.isEmpty(entity.getVImage())){
                    holder.posterIv.placeHolder(R.drawable.focus_240_360).hasCorner(true)
                            .load(entity.getVImage());
                }else {
                    holder.posterIv.placeHolder(R.drawable.focus_240_360).hasCorner(true)
                            .load(R.drawable.focus_240_360);
                }
                holder.mModuleView.setVisibility(View.VISIBLE);
            }

            if (holder.subTitleTv != null) {
                if (!TextUtils.isEmpty(entity.getTitle())) {
                    holder.subTitleTv.setVisibility(View.VISIBLE);
                    holder.subTitleTv.setText(entity.getTitle());
                }
            }
        }
    }

    class DetailsHorizontalViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener {
        private FrameLayout mModuleView;
        private RecycleImageView posterIv;
        private ImageView focusImageView;

        private TextView subTitleTv;

        public DetailsHorizontalViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            mModuleView = (FrameLayout) itemView.findViewById(R.id.id_module_view);

            mModuleView.setOnFocusChangeListener(this);
            mModuleView.setOnKeyListener(this);
            posterIv = (RecycleImageView) mModuleView.findViewWithTag("tag_poster_image");
            focusImageView = mModuleView.findViewWithTag("tag_img_focus");
            subTitleTv = (TextView) mModuleView.findViewWithTag("tag_poster_title");
            //适配
            DisplayUtils.adjustView(context,posterIv,focusImageView,R.dimen.width_17dp,R.dimen.width_17dp);
//            int space = context.getResources().getDimensionPixelOffset(R.dimen.width_0px);
//            FrameLayout.LayoutParams posterPara = new FrameLayout.LayoutParams(posterIv.getLayoutParams());
////            posterPara.topMargin = space;
//            posterPara.setMargins(space,space,0,0);
//            posterIv.setLayoutParams(posterPara);
//            posterIv.requestLayout();
//
//
//            ViewGroup.LayoutParams focusPara = focusImageView.getLayoutParams();
//            focusPara.width = posterPara.width+2*space;
//            focusPara.height = posterPara.height+2*space;
//            focusImageView.setLayoutParams(focusPara);
//            focusImageView.requestLayout();

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            View view = v.findViewWithTag("tag_poster_title");
            if (view!=null){
                view.setSelected(hasFocus);
            }

            if (hasFocus) {
                onItemGetFocus(v);
            } else {
                onItemLoseFocus(v);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return true;
            } else {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (getAdapterPosition() == (getItemCount() - 1)) {
                        return true;
                    } else {
                        mRecyclerView.scrollToPosition(getAdapterPosition() + 1);
                    }

                }else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    SubContent entity = mList.get(getAdapterPosition());
                    if (entity != null) {
//                        if (TextUtils.isEmpty(entity.getActionType())) {
//                            JumpUtil.detailsJumpActivity(context, entity.getContentType()
//                                    , entity
//                                            .getContentUUID());
//                        } else {
//                            JumpUtil.activityJump(context, entity.getActionType(), entity
//                                    .getContentType(), entity.getContentUUID(), entity
//                                    .getActionUri());
//                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
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
     //   sa.setInterpolator(mSpringInterpolator);
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
      //  sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }
}
