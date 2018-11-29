package tv.newtv.cboxtv.cms.search.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;

/**
 * Created by linzy on 2018/10/25.
 * <p>
 * des 搜索结果适配器
 */

public class SearchResultAdapter extends RecyclerView.Adapter<ResultHolder> {

    private ArrayList<SubContent> dataList;
    private Context mContext;
    private View mLabelView;
    private SearchRecyclerView mRecyclerView;

    private View lastFocusView;

    public View getlastFocusView(){
        return lastFocusView;
    }

    private SearchHolderAction mSearchHolderAction;

    public interface SearchHolderAction {
        void onItemClick(int position,SubContent content);

        void onFocusToTop();
    }

    public void setSearchHolderAction(SearchHolderAction action){
        mSearchHolderAction = action;
    }

    public SearchResultAdapter(Context context, ArrayList<SubContent> data, View labelView, SearchRecyclerView recyclerView) {
        this.mContext = context;
        this.dataList = data;
        this.mLabelView = labelView;
        this.mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_adapter_item, null, false);
        ResultHolder resultHolder = new ResultHolder(view);
        return resultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultHolder holder, final int position) {
        if (dataList != null && dataList.size() > 0) {

            final SubContent subContent = dataList.get(position);
            Picasso.get().load(dataList.get(position).getVImage()).transform(new PosterCircleTransform(mContext, 4))
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.focus_240_360)
                    .error(R.drawable.focus_240_360)
                    .into(holder.mPosterImageView);

            if (!TextUtils.isEmpty(subContent.getContentType()) && subContent.getContentType().equals("PS")){

                if (!TextUtils.isEmpty(subContent.getRecentNum()) && !subContent.getRecentNum().equals("0")){
                    holder.updateLayout.setVisibility(View.VISIBLE);
                    holder.updateLeft.setVisibility(View.VISIBLE);
                    holder.updateRight.setVisibility(View.VISIBLE);
                    holder.recentNumTv.setText(subContent.getRecentNum());
                }
                if (!TextUtils.isEmpty(subContent.getVipFlag()) && subContent.getVipFlag().equals(0)){
                    holder.isVipImg.setVisibility(View.VISIBLE);
                }
            }

            holder.mTxtTitle.setText(dataList.get(position).getTitle());
            holder.mPosterTitle.setText(dataList.get(position).getSubTitle());
            holder.mLayoutResultList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    lastFocusView = view;

                    if (hasFocus) {
                        onItemGetFocus(view);
                        holder.mTxtTitle.setSelected(true);
                        holder.mFocusImageView.setVisibility(View.VISIBLE);

                    } else {
                        onItemLoseFocus(view);
                        holder.mTxtTitle.setSelected(false);
                        holder.mFocusImageView.setVisibility(View.INVISIBLE);
                    }
                }
            });

            holder.mLayoutResultList.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {

                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (position < 6 && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                            if (mSearchHolderAction != null)
                                mSearchHolderAction.onFocusToTop();
                            return true;
                        }
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER || keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER){
                            if (mSearchHolderAction != null)
                                mSearchHolderAction.onItemClick(position,subContent);
                            return true;
                        }
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK){
                            mLabelView.requestFocus();
                            mRecyclerView.smoothScrollToPosition(0);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null && dataList.size() > 0 ? dataList.size() : 0;
    }

    private void onItemGetFocus(View view) {
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    private void onItemLoseFocus(View view) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }
}

class ResultHolder extends RecyclerView.ViewHolder {

    public LinearLayout updateLayout;
    public FrameLayout mLayoutResultList;
    public ImageView mPosterImageView, mFocusImageView,isVipImg;
    public TextView mTxtTitle, mPosterTitle,recentNumTv,updateLeft,updateRight;

    public ResultHolder(View itemView) {
        super(itemView);
        updateLayout = itemView.findViewById(R.id.update_layout);
        mLayoutResultList = itemView.findViewById(R.id.search_result_rl);
        mPosterImageView = itemView.findViewById(R.id.result_image_default);
        mFocusImageView = itemView.findViewById(R.id.result_image_focus);
        mTxtTitle = itemView.findViewById(R.id.result_title);
        mPosterTitle = itemView.findViewById(R.id.result_poster_title);
        recentNumTv = itemView.findViewById(R.id.recentNumTv);
        isVipImg = itemView.findViewById(R.id.isVipImg);
        updateLeft = itemView.findViewById(R.id.update_left);
        updateRight = itemView.findViewById(R.id.update_right);

//            适配
        DisplayUtils.adjustView(LauncherApplication.AppContext, mPosterImageView, mFocusImageView, R.dimen.width_16dp, R.dimen.width_16dp);
    }

}
