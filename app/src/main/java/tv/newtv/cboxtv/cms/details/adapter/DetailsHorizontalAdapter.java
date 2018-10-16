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

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * Created by gaoleichao on 2018/4/4.
 */

public class DetailsHorizontalAdapter extends BaseRecyclerAdapter<SubContent, DetailsHorizontalAdapter
        .DetailsHorizontalViewHolder> {
    private static String TAG = "DetailsHorizontalAdapter";
    private Context context;
    private Interpolator mSpringInterpolator;
    private RecyclerView mRecyclerView;

    public DetailsHorizontalAdapter(Context context, Interpolator mSpringInterpolator, RecyclerView mRecyclerView) {
        this.context = context;
        this.mSpringInterpolator = mSpringInterpolator;
        this.mRecyclerView = mRecyclerView;
    }

    @Override
    public DetailsHorizontalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DetailsHorizontalViewHolder viewHolder = new DetailsHorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.item_details_horizontal_layout, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DetailsHorizontalViewHolder holder, int position) {
        SubContent entity = mList.get(position);
        if (entity != null) {
            if (holder.posterIv != null) {
                holder.posterIv.placeHolder(R.drawable.focus_384_216).hasCorner(true)
                        .load(entity.getHImage());
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
            int space = context.getResources().getDimensionPixelOffset(R.dimen.width_17dp);
            FrameLayout.LayoutParams posterPara = new FrameLayout.LayoutParams(posterIv.getLayoutParams());
//            posterPara.topMargin = space;
            posterPara.setMargins(space,space,0,0);
            posterIv.setLayoutParams(posterPara);
            posterIv.requestLayout();


            ViewGroup.LayoutParams focusPara = focusImageView.getLayoutParams();
            focusPara.width = posterPara.width+2*space;
            focusPara.height = posterPara.height+2*space;
            focusImageView.setLayoutParams(focusPara);
            focusImageView.requestLayout();

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            View view = v.findViewWithTag("tag_poster_title");
            if (hasFocus) {
                onItemGetFocus(v);


                if (view!=null){
                    view.setSelected(true);
                }
            } else {
                if (view!=null){
                    view.setSelected(false);
                }
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

                } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    SubContent entity = mList.get(getAdapterPosition());
                    if (entity != null) {
//                        if (TextUtils.isEmpty(entity.getActionType())) {
//                            JumpUtil.detailsJumpActivity(context, entity.getContentType(),
//                                    entity.getContentUUID(),entity.getSeriesSubUUID());
//                        } else {
//                            JumpUtil.activityJump(context, entity.getActionType(), entity.getContentType(),
//                                    entity.getContentUUID(), entity.getActionUri(),entity.getSeriesSubUUID());
//                        }
                    }
                    return true;
                }else if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                    if (getAdapterPosition() ==0) {
                        return true;
                    }

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
}
