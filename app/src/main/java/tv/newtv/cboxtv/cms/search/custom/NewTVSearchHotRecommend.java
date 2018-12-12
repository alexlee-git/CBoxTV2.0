package tv.newtv.cboxtv.cms.search.custom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.Constant;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;


/**
 * 项目名称： NewTVLauncher
 * 类描述：热门搜索页
 * 创建人：wqs
 * 创建时间： 2018/3/9 0009 12:57
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NewTVSearchHotRecommend extends RelativeLayout implements PageContract.View {

    private final String TAG = this.getClass().getSimpleName();
    private StaggeredGridLayoutManager mLayoutManager;
    private PageContract.ContentPresenter mContentPresenter;
    private SearchHotRecommendAdapter mAdapter;
    private SearchRecyclerView mRecyclerView;
    public TextView mEmptyTextView;
    private ImageView mLeftArrow;

    private Context mContext;
    private List<Program> mDatas;
    public boolean keyBoardFocusStatus = false;

    public void destroy(){
        if (mContentPresenter != null) {
            mContentPresenter.destroy();
            mContentPresenter = null;
        }
    }

    public NewTVSearchHotRecommend(Context context) {
        super(context);
        initLayout(context);
    }

    public NewTVSearchHotRecommend(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public NewTVSearchHotRecommend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    //填充布局
    private void initLayout(Context context) {
        mContext = context;
        View view = View.inflate(mContext, R.layout.newtv_search_result_hot_recommend, this);
        initView(view);
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.id_search_recyclerView_hot_recommend);
        mLeftArrow = view.findViewById(R.id.id_result_left_arrow);
        mEmptyTextView = view.findViewById(R.id.id_search_hot_recommend_empty);
        mLayoutManager = new StaggeredGridLayoutManager(6, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mDatas = new ArrayList<>();

        mContentPresenter = new PageContract.ContentPresenter(getContext(), this);
        String hotSearchId = Constant.getBaseUrl("HOTSEARCH_CONTENTID");
        if (!TextUtils.isEmpty(hotSearchId)){
            mContentPresenter.getPageContent(hotSearchId);
        }

    }

    public SearchRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setData(List<Program> result) {
        try {
            if (result != null && result.size() > 0) {
                mEmptyTextView.setVisibility(View.GONE);
            } else {
                mEmptyTextView.setVisibility(View.VISIBLE);
            }
            SearchHotRecommendAdapter adapter = (SearchHotRecommendAdapter) mRecyclerView.getAdapter();
            if (adapter == null) {
                mDatas = result;
                adapter = new SearchHotRecommendAdapter(mContext);
                mAdapter = adapter;
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mDatas.clear();
                mDatas.addAll(result);
                mAdapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            Log.e(TAG, "---setData:Exception---" + e.toString());
        }
    }

    @Override
    public void startLoading() {

    }

    @Override
    public void loadingComplete() {

    }

    class SearchHotRecommendAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {
        private Context mContext;

        public SearchHotRecommendAdapter(Context context) {
            mContext = context;
        }

        @Override
        public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.newtv_search_result_hot_recommend_item, parent, false);
            SearchResultViewHolder mSearchResultViewHolder = new SearchResultViewHolder(view);
            return mSearchResultViewHolder;
        }

        @Override
        public void onBindViewHolder(final SearchResultViewHolder holder, final int position) {
            try {
                if (mDatas == null || mDatas.size() <= 0) {
                    return;
                } else {
                    Picasso.get().load(mDatas.get(position).getImg()).transform(new PosterCircleTransform(mContext, 4)).memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.focus_240_360).error(R.drawable.focus_240_360).into(holder.mPosterImageView);
                    holder.mPosterTitle.setText(mDatas.get(position).getTitle());

                    SuperScriptManager.getInstance().processSuperscript(
                            holder.itemView.getContext(),
                            "layout_008",
                            ((ViewGroup) holder.itemView).indexOfChild(holder.mPosterImageView),
                            mDatas.get(position),
                            (ViewGroup) holder.mPosterImageView.getParent()
                    );

                    holder.mFocusView.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {

                            if (hasFocus) {
                                keyBoardFocusStatus = false;
                                mLeftArrow.setVisibility(View.VISIBLE);
                                onItemGetFocus(holder.itemView);
                                holder.mPosterTitle.setSelected(true);
                            } else {
                                holder.mPosterTitle.setSelected(false);
                                mLeftArrow.setVisibility(View.INVISIBLE);
                                onItemLoseFocus(holder.itemView);
                            }
                        }
                    });
                    holder.mFocusView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String contentUUID = mDatas.get(position).getL_id();
                            String contentType = mDatas.get(position).getL_contentType();
                            String actionType = mDatas.get(position).getL_actionType();
                            String actionUrl = mDatas.get(position).getL_actionUri();

                            JumpUtil.activityJump(mContext, actionType, contentType, contentUUID, actionUrl);
                        }
                    });

                    holder.mFocusView.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                    if (position == mDatas.size() - 1) {
                                        holder.itemView.requestFocus();
//                                        holder.mFrameLayoutHotRecommend.setNextFocusRightId(R.id.id_frameLayout_hot_recommend_list);
                                        return true;
                                    }
                                } else if (i == KeyEvent.KEYCODE_DPAD_UP) {
                                    if (position < 6) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            return false;
                        }
                    });
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }

        @Override
        public int getItemCount() {
            return mDatas != null ? mDatas.size() : 0;
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

    @Override
    public void onPageResult(@Nullable List<Page> page) {
        if (page != null && page.size() > 0) {
            setData(page.get(0).getPrograms());
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {

    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout mFocusView;
        public ImageView mPosterImageView;
        public TextView mPosterTitle;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            mFocusView = itemView.findViewById(R.id.ai_sou_focus_view);
            mPosterTitle = itemView.findViewById(R.id.id_hot_recommend_title);
            mPosterImageView = itemView.findViewById(R.id.id_hot_recommend_imageView_poster);
            //适配
//            DisplayUtils.adjustView(getContext(),mPosterImageView,mFocusImageView,R.dimen.width_16dp,R.dimen.width_16dp);
        }
    }
}
