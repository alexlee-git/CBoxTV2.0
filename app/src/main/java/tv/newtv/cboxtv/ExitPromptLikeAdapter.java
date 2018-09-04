package tv.newtv.cboxtv;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.util.JumpUtil;

/**
 * Created by slp on 2018/4/19.
 */

public class ExitPromptLikeAdapter extends BaseRecyclerAdapter<SearchResultInfos.ResultListBean, RecyclerView.ViewHolder> {


    private Context mContext;
    private Interpolator mSpringInterpolator;
    private int selectPostion = 0;
    SearchResultInfos.ResultListBean entity = null;

    public ExitPromptLikeAdapter(Context context){
        this.mContext = context;
        mSpringInterpolator = new OvershootInterpolator(2.2f);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        viewHolder = new ExitPromptLikeAdapter.LikeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_exit_guess_like, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ExitPromptLikeAdapter.LikeViewHolder) {
            ExitPromptLikeAdapter.LikeViewHolder viewHolder = (ExitPromptLikeAdapter.LikeViewHolder) holder;

            if (mList != null && mList.size() != 0) {
                entity = mList.get(position);

                viewHolder.mTitleTv.setText(entity.getName());

                Picasso.get().load(entity.getHpicurl()).into(viewHolder.mImageIv);
            }


            viewHolder.mModuleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    entity = mList.get(position);

                    ProgramInfo info = new ProgramInfo();
                    info.setActionType(entity.getType());
                    info.setContentUUID(entity.getUUID());
                    info.setActionUri(entity.getActionUri());
                    info.setContentType(entity.getContentType());
                    JumpUtil.activityJump(mContext,info);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mList.size() ==0||mList.size()>3) {
            return 3;
        } else {
            return mList.size();
        }
    }

    class LikeViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {
        private RelativeLayout mModuleView;
        private TextView mTitleTv;
        private ImageView mFocusIv;
        private ImageView mImageIv;


        public LikeViewHolder(View itemView) {
            super(itemView);
            mTitleTv = (TextView) itemView.findViewById(R.id.item_exit_guess_like_name);
            mImageIv = (ImageView) itemView.findViewById(R.id.item_exit_guess_like_poster);
            mFocusIv = (ImageView) itemView.findViewWithTag("tag_img_focus");
            mModuleView =  itemView.findViewById(R.id.root_exit);
            mModuleView.setOnFocusChangeListener(this);


        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(v, mFocusIv,getAdapterPosition());
            } else {
                onItemLoseFocus(v, mFocusIv);
            }
        }
    }


    private void onItemLoseFocus(View view, ImageView focusImageView) {
        if (focusImageView != null) {
            focusImageView.setVisibility(View.INVISIBLE);
        }
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    private void onItemGetFocus(View view, ImageView focusImageView,int postion) {
        if (focusImageView != null) {
            focusImageView.setVisibility(View.VISIBLE);
        }
        selectPostion = postion;
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

}
