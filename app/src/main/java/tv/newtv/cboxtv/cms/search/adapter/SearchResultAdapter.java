package tv.newtv.cboxtv.cms.search.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
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
    private SearchHolderAction mSearchHolderAction;

    public SearchResultAdapter(Context context, ArrayList<SubContent> data, View labelView,
                               SearchRecyclerView recyclerView) {
        this.mContext = context;
        this.dataList = data;
        this.mLabelView = labelView;
        this.mRecyclerView = recyclerView;
    }

    public View getlastFocusView() {
        return lastFocusView;
    }

    public void setSearchHolderAction(SearchHolderAction action) {
        mSearchHolderAction = action;
    }

    @NonNull
    @Override
    public ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_adapter_item,
                null, false);
        ResultHolder resultHolder = new ResultHolder(view);
        return resultHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ResultHolder holder, final int position) {
        if (dataList != null && dataList.size() > 0) {

            final SubContent subContent = dataList.get(position);
            Picasso.get().load(dataList.get(position).getVImage())
                    .transform(new
                    PosterCircleTransform(mContext, 4))
                    .fit()
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.focus_240_360)
                    .error(R.drawable.focus_240_360)
                    .into(holder.mPosterImageView);

            SuperScriptManager.getInstance().processSuperscript(
                    holder.itemView.getContext(),
                    "layout_008",
                    ((ViewGroup) holder.itemView).indexOfChild(holder.mPosterImageView),
                    subContent,
                    (ViewGroup) holder.mPosterImageView.getParent()
            );

            SuperScriptManager.getInstance().processVipSuperScript(
                    holder.itemView.getContext(),
                    subContent,
                    "layout_008",
                    ((ViewGroup) holder.itemView).indexOfChild(holder.mPosterImageView),
                    (ViewGroup) holder.mPosterImageView.getParent()
            );

            holder.mPosterTitle.setText(dataList.get(position).getTitle());
            holder.mFocusImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    lastFocusView = view;

                    if (hasFocus) {
                        holder.mPosterTitle.setSelected(true);
                        onItemGetFocus(holder.allRootView);
                    } else {
                        holder.mPosterTitle.setSelected(false);
                        onItemLoseFocus(holder.allRootView);
                    }
                }
            });

            holder.mFocusImageView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {

                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        if (position < 6 && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                            if (mSearchHolderAction != null)
                                mSearchHolderAction.onFocusToTop();
                            return true;
                        }
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER || keyEvent
                                .getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                            if (mSearchHolderAction != null)
                                mSearchHolderAction.onItemClick(position, subContent);
                            return true;
                        }
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
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
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    private void onItemLoseFocus(View view) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    public interface SearchHolderAction {
        void onItemClick(int position, SubContent content);

        void onFocusToTop();
    }
}

class ResultHolder extends RecyclerView.ViewHolder {

    public ImageView mPosterImageView;
    public View mFocusImageView;
    public TextView mPosterTitle;
    public FrameLayout allRootView;

    public ResultHolder(View itemView) {
        super(itemView);
        allRootView = itemView.findViewById(R.id.id_module_view_search);
        mPosterImageView = itemView.findViewWithTag("cell_poster");
        mFocusImageView = itemView.findViewWithTag("cell_focus");
        mPosterTitle = itemView.findViewWithTag("cell_title");

//            适配
//        DisplayUtils.adjustView(LauncherApplication.AppContext, mPosterImageView,
//                mFocusImageView, R.dimen.width_16dp, R.dimen.width_16dp);
    }

}
