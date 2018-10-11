package tv.newtv.cboxtv.cms.search.custom;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.search.listener.INotifySearchHotRecommendData;
import tv.newtv.cboxtv.cms.search.listener.OnGetSearchHotRecommendFocus;
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
public class NewTVSearchHotRecommend extends RelativeLayout {
    private final String TAG = this.getClass().getSimpleName();
    private StaggeredGridLayoutManager mLayoutManager;
    private SearchHotRecommendAdapter mAdapter;
    private SearchRecyclerView mRecyclerView;
    private Context mContext;
    private OnGetSearchHotRecommendFocus mOnGetSearchHotRecommendFocus;
    private SearchResultInfos mSearchResultInfos;
    private List<SearchResultInfos.ResultListBean> mDatas;
    public TextView mEmptyTextView;
    private View mLastItemView;
    public boolean keyBoardFocusStatus = false;
    private ImageView mLeftArrow;

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
        mRecyclerView = (SearchRecyclerView) view.findViewById(R.id.id_search_recyclerView_hot_recommend);
        mLeftArrow = (ImageView) view.findViewById(R.id.id_result_left_arrow);
        mEmptyTextView = (TextView) view.findViewById(R.id.id_search_hot_recommend_empty);
        mLayoutManager = new StaggeredGridLayoutManager(6, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mSearchResultInfos = new SearchResultInfos();
        mDatas = new ArrayList<>();
    }


    public SearchRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setData(SearchResultInfos result) {
        try {
            if (result.getResultList() != null && result.getResultList().size() > 0) {
                mEmptyTextView.setVisibility(View.GONE);
                mINotifySearchHotRecommendData.INotifySearchHotRecommendData(true);
            } else {
                mEmptyTextView.setVisibility(View.VISIBLE);
                mINotifySearchHotRecommendData.INotifySearchHotRecommendData(false);
            }
            SearchHotRecommendAdapter adapter = (SearchHotRecommendAdapter) mRecyclerView.getAdapter();
            if (adapter == null) {
                mDatas = result.getResultList();
                adapter = new SearchHotRecommendAdapter(mContext, result.getResultList());
//                adapter.setHasStableIds(true);
                mAdapter = adapter;
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mDatas.clear();
                mDatas.addAll(result.getResultList());
                mAdapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(TAG, "---setData:Exception---" + e.toString());
        }
    }

    public View setLastItemView() {
        return mLastItemView;
    }

    //获得键盘最后一个获得焦点的view
    private View mKeyboardLastFocusView;

    public void setKeyboardLastFocusView(View view) {
        mKeyboardLastFocusView = view;
    }

    public void setOnGetSearchHotRecommendFocus(OnGetSearchHotRecommendFocus onGetSearchHotRecommendFocus) {
        mOnGetSearchHotRecommendFocus = onGetSearchHotRecommendFocus;
    }


    private INotifySearchHotRecommendData mINotifySearchHotRecommendData;

    public void setINotifySearchHotRecommendData(INotifySearchHotRecommendData iNotifySearchHotRecommendData) {
        mINotifySearchHotRecommendData = iNotifySearchHotRecommendData;
    }

    class SearchHotRecommendAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {
        private Context mContext;


        public SearchHotRecommendAdapter(Context context, List<SearchResultInfos.ResultListBean> data) {
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
                    LogUtils.e(TAG, "----热搜结果数据为空");
                    return;
                } else {
                    Picasso.get().load(mDatas.get(position).getHpicurl()).transform(new PosterCircleTransform(mContext, 4)).memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.focus_240_360).error(R.drawable.focus_240_360).into(holder.mPosterImageView);
//                    Picasso.with(mContext).load(mDatas.get(position).getHpicurl()).transform(new PosterCircleTransform(mContext, 8)).memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.focus_240_360).error(R.drawable.focus_240_360).into(holder.mPosterImageView);
                    holder.mTxtTitle.setText(mDatas.get(position).getName());
                    holder.mFrameLayoutHotRecommend.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {

                            if (hasFocus) {
                                keyBoardFocusStatus = false;
                                mLeftArrow.setVisibility(View.VISIBLE);
                                onItemGetFocus(view);
                                holder.mTxtTitle.setSelected(true);
                                holder.mFocusImageView.setVisibility(View.VISIBLE);
                                mOnGetSearchHotRecommendFocus.notifySearchHotRecommendFocus(true, position, holder.mFrameLayoutHotRecommend);
                            } else {
                                holder.mTxtTitle.setSelected(false);
                                mLeftArrow.setVisibility(View.INVISIBLE);
                                mOnGetSearchHotRecommendFocus.notifySearchHotRecommendFocus(false, position, holder.mFrameLayoutHotRecommend);
                                onItemLoseFocus(view);
                                mLastItemView = view;
                                holder.mFocusImageView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    final String UUID = mDatas.get(position).getUUID();
                    final String contentType = mDatas.get(position).getContentType();
                    holder.mFrameLayoutHotRecommend.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String contentUUID = mDatas.get(position).getUUID();
                            String contentType = mDatas.get(position).getContentType();
                            String actionType = mDatas.get(position).getType();
                            String actionUrl = mDatas.get(position).getActionUri();

                            JumpUtil.activityJump(mContext, actionType, contentType, contentUUID, actionUrl);

//                            Intent intent = new Intent(mContext, DetailsPageActivity.class);
//                            intent.putExtra("content_type", contentType);
//                            intent.putExtra("content_uuid", UUID);
//                            Log.e(TAG, "----UUID---" + UUID + "---contentType--" + contentType);
//                            mContext.startActivity(intent);
                        }
                    });

                    holder.mFrameLayoutHotRecommend.setOnKeyListener(new OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                    if (position == mDatas.size() - 1) {
                                        holder.mFrameLayoutHotRecommend.setNextFocusRightId(R.id.id_frameLayout_hot_recommend_list);
                                        return true;
                                    }
                                } else if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                                    if (position % 6 == 0) {
                                        if (mKeyboardLastFocusView != null) {
                                            mKeyboardLastFocusView.requestFocus();
                                            mOnGetSearchHotRecommendFocus.notifySearchHotRecommendFocus(false, position, null);
                                            return true;
                                        }
                                    }
                                } else if (i == KeyEvent.KEYCODE_DPAD_UP) {
                                    if (position < 6) {
                                        return true;
                                    } else {
                                    }
                                }
                                return false;
                            }

                            return false;
                        }
                    });
                }

            } catch (Exception e) {
                Log.i(TAG, "------SearchResult:onBindViewHolder:Exception" + e.toString());
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

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout mFrameLayoutHotRecommend;
        public ImageView mPosterImageView, mFocusImageView;
        public TextView mTxtTitle, mPosterTitle;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            mFrameLayoutHotRecommend = (FrameLayout) itemView.findViewById(R.id.id_frameLayout_hot_recommend_list);
            mTxtTitle = (TextView) itemView.findViewById(R.id.id_hot_recommend_title);
            mPosterTitle = (TextView) itemView.findViewById(R.id.id_hot_recommend_poster_title);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.id_hot_recommend_imageView_poster);
            mFocusImageView = (ImageView) itemView.findViewById(R.id.id_hot_recommend_imageView_focus);
            //适配
            DisplayUtils.adjustView(getContext(),mPosterImageView,mFocusImageView,R.dimen.width_16dp,R.dimen.width_16dp);
        }
    }
}
