package tv.newtv.cboxtv.cms.search.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.search.custom.SearchRecyclerView;
import tv.newtv.cboxtv.cms.search.listener.OnGetSearchResultFocus;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;


/**
 * 类描述：搜索结果栏目页
 * 创建人：wqs
 * 创建时间： 2018/3/29 0029 11:40
 * 修改人：wqs
 * 修改时间：2018/4/30 0029 18:40
 * 修改备注：新增栏目页
 */
public class ColumnFragment extends BaseFragment {
    private final String TAG = this.getClass().getSimpleName();
    private SearchRecyclerView mRecyclerView;
    private TextView mEmptyView;
    private TextView mResultTotalView;
    private Context mContext;
    private List<SearchResultInfos.ResultListBean> mDatas;
    private ColumnAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private Gson mGson;
    private View mLabelView;
    private View mKeyboardLastFocusView;    //获得键盘最后一个获得焦点的view
    public boolean mDataStatus = false;//获取数据的状态
    public View mLastFocusView;//最后一个获取焦点的view
    public boolean mFocusStatus = false;
    public int mSearchTotalSum = 0;
    public int page = 1;
    private int size = 48;
    public boolean mLoadMore = false;
    public String mContentType;
    public String mInputString;
    private String contentType = "TV";
    private TextView mColumnLabelTitle;
    private View mLabelFocusView;
    private String mKeywordType = "name";
    private ColumnSizeListener columnSizeListener;

    public interface ColumnSizeListener {

        void ColumnSize(int size);
    }

    public void setColumnSizeListener(ColumnSizeListener columnSizeListener) {
        if (columnSizeListener != null) {
            this.columnSizeListener = columnSizeListener;
        }
    }

    public static ColumnFragment newInstance(Bundle paramBundle) {
        ColumnFragment fragment = new ColumnFragment();
        fragment.setArguments(paramBundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle bundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newtv_search_result_fragment_column, null, false);
        mRecyclerView = (SearchRecyclerView) view.findViewById(R.id.id_search_result_column_recyclerView);
        mResultTotalView = (TextView) view.findViewById(R.id.id_fragment_column_result_total);
        mEmptyView = (TextView) view.findViewById(R.id.id_search_result_column_empty);
        mLayoutManager = new StaggeredGridLayoutManager(6, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mGson = new Gson();
        return view;
    }

    public SearchRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    private String mSearchType, keyword, programType, keywordType, type, year, area, classType;

    //判断外部跳转参数
    public void setExternalParams(Bundle bundle) {
        try {
            mSearchType = bundle.getString("SearchType");
            if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("SearchListByKeyword")) {
                keyword = bundle.getString("keyword");
                if (TextUtils.isEmpty(keyword)) {
                    keyword = "";
                }
                programType = bundle.getString("programType");
                if (TextUtils.isEmpty(programType)) {
                    programType = "-1";
                }
                keywordType = bundle.getString("keywordType");
                if (TextUtils.isEmpty(keywordType)) {
                    keywordType = mKeywordType;
                }
                requestKeywordSearchResultData(keyword, keywordType, programType, 0, size);
            } else if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("RetrievalProgramSerialList")) {
                type = bundle.getString("type");
                if (TextUtils.isEmpty(type)) {
                    type = "-1";
                }
                year = bundle.getString("year");
                if (TextUtils.isEmpty(year)) {
                    year = "-1";
                }
                area = bundle.getString("area");
                if (TextUtils.isEmpty(area)) {
                    area = "-1";
                }
                classType = bundle.getString("classType");
                if (TextUtils.isEmpty(classType)) {
                    classType = "-1";
                }
                requestRetrievalSearchResultData(type, year, area, classType, 0, size);
            }
        } catch (Exception e) {
            Log.e(TAG, "---setExternalParams:Exception" + e.toString());
        }
    }

    public void setKey(String key) {
        try {
            mInputString = key;
            mLastFocusView = null;
            if (!TextUtils.isEmpty(mInputString)) {
                clearData();
                requestKeywordSearchResultData(mInputString, mKeywordType, "-1", 0, size);
            } else {
                clearData();
            }
        } catch (Exception e) {
            Log.e(TAG, "---setKey:Exception" + e.toString());
        }
    }

    public void loadMoreData(SearchResultInfos result, int positionStart) {
        try {
            if (result != null) {
                if (result.getResultList() != null && result.getResultList().size() > 0) {
                    mDatas.addAll(result.getResultList());
                    mAdapter.notifyItemRangeChanged(positionStart, positionStart + size);
                } else {
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "---loadMoreData:Exception" + e.toString());
        }
    }

    public void clearData() {
        Log.e(TAG, "-----clearData----");
        try {
            page = 1;
            if (mDatas != null && mDatas.size() > 0) {
                mDatas.clear();
            } else {
//                mDatas = new ArrayList<>();
            }
            mLoadMore = false;
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {
            Log.e(TAG, "---clearData:Exception--" + e.toString());
        }
    }


    public void setKeyboardLastFocusView(View view) {
        mKeyboardLastFocusView = view;
    }

    public void setColumnLabelTitle(TextView view) {
        mColumnLabelTitle = view;
    }

    public void setLabelFocusView(View view) {
        mLabelFocusView = view;
    }

    private OnGetSearchResultFocus mOnGetSearchResultFocus;

    public void setOnGetSearchResultFocus(OnGetSearchResultFocus onGetSearchResultFocus) {
        mOnGetSearchResultFocus = onGetSearchResultFocus;
    }

    public void setLabelView(View view) {
        mLabelView = view;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public void setData(SearchResultInfos result) {
        try {
            if (result != null) {
                if (result.getResultList() != null && result.getResultList().size() > 0) {
                    mResultTotalView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                    mDataStatus = true;
                    mSearchTotalSum = result.getTotal();
                    mResultTotalView.setText(result.getTotal() + "个结果");
                    mLoadMore = true;
                    ColumnAdapter adapter = (ColumnAdapter) mRecyclerView.getAdapter();
                    if (adapter == null) {
                        mDatas = result.getResultList();
                        adapter = new ColumnAdapter(mContext, mDatas);
//                adapter.setHasStableIds(true);
                        mAdapter = adapter;

                        mRecyclerView.setAdapter(mAdapter);

                    } else {
                        mDatas.clear();
                        mDatas.addAll(result.getResultList());
                        mAdapter.notifyDataSetChanged();

                    }
                } else {
                    mDataStatus = false;
                    mEmptyView.setVisibility(View.VISIBLE);
                    mResultTotalView.setVisibility(View.INVISIBLE);
                }
            } else {
                mDataStatus = false;
                mEmptyView.setVisibility(View.VISIBLE);
                mResultTotalView.setVisibility(View.INVISIBLE);
            }


        } catch (Exception e) {
            mDataStatus = false;
            mEmptyView.setVisibility(View.VISIBLE);
            mResultTotalView.setVisibility(View.INVISIBLE);
            Log.e(TAG, "---setData:Exception---" + e.toString());
        }
    }


    public void requestKeywordSearchResultData(String keyword, String keywordType, String programType, final Integer startNum, Integer size) {
        try {
            Log.e(TAG, "-----requestKeywordSearchResultData---" + keyword);
            NetClient.INSTANCE.getSearchResultApi().getKeywordSearchResultResponse(Constant.APP_KEY, Constant.CHANNEL_ID, contentType, keyword, keywordType, programType, startNum, size).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseBody value) {
                    try {
                        String result = value.string();
                        SearchResultInfos mSearchResultInfos = mGson.fromJson(result, SearchResultInfos.class);
                        if (columnSizeListener != null) {
                            columnSizeListener.ColumnSize(mSearchResultInfos.getResultList().size());
                        }
                        if (mLoadMore) {
                            loadMoreData(mSearchResultInfos, startNum);
                        } else {
                            setData(mSearchResultInfos);
                        }


                    } catch (Exception e) {
                        LogUtils.e(e.toString());
                        if (mLoadMore) {
                            loadMoreData(null, startNum);
                        } else {
                            setData(null);
                        }
                    }

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "--requestSearchResultData--onError-----");
                    setData(null);
                }

                @Override
                public void onComplete() {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "---requestKeywordSearchResultData:Exception" + e.toString());
        }
    }

    public void requestRetrievalSearchResultData(String type, String year, String area, String classType, final int startNum, int size) {
        try {
            Log.e(TAG, "-----requestRetrievalSearchResultData---" + type);
            NetClient.INSTANCE.getSearchResultApi().getRetrievalSearchResultResponse(Constant.APP_KEY, Constant.CHANNEL_ID, contentType, type, year, area, classType, startNum, size).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResponseBody>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ResponseBody value) {
                    try {
                        String result = value.string();
                        SearchResultInfos mSearchResultInfos = mGson.fromJson(result, SearchResultInfos.class);


                        if (mLoadMore) {
                            loadMoreData(mSearchResultInfos, startNum);
                        } else {
                            setData(mSearchResultInfos);
                        }


                    } catch (Exception e) {
                        LogUtils.e(e.toString());
                        if (mLoadMore) {
                            loadMoreData(null, startNum);
                        } else {
                            setData(null);
                        }
                    }

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "--requestSearchResultData--onError-----");
                    setData(null);
                }

                @Override
                public void onComplete() {

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "---requestRetrievalSearchResultData:Exception" + e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class ColumnAdapter extends RecyclerView.Adapter<ColumnViewHolder> {
        private Context mContext;
        private List<SearchResultInfos.ResultListBean> mDatas;

        public ColumnAdapter(Context context, List<SearchResultInfos.ResultListBean> list) {
            mContext = context;
            mDatas = list;
        }

        @Override
        public ColumnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.newtv_search_result_fragment_column_item, parent, false);
            ColumnViewHolder mColumnViewHolder = new ColumnViewHolder(view);
            return mColumnViewHolder;
        }

        @Override
        public void onBindViewHolder(final ColumnViewHolder holder, final int position) {
            try {
                if (mDatas == null || mDatas.size() <= 0) {
                    Log.e(TAG, "----搜索结果数据为空");
                    mEmptyView.setVisibility(View.VISIBLE);
                    mDataStatus = false;
                    return;
                } else {
                    mDataStatus = true;
                    mEmptyView.setVisibility(View.GONE);
                    String url = mDatas.get(position).getPicurl();
                    if(TextUtils.isEmpty(url)){
                        url = mDatas.get(position).getHpicurl();
                    }
                    Picasso.get().load(url).transform(new PosterCircleTransform(mContext, 4)).fit().memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.focus_240_360).error(R.drawable.focus_240_360).into(holder.mPosterImageView);
                    holder.mTxtTitle.setText(mDatas.get(position).getName());
                    holder.mPosterTitle.setText(mDatas.get(position).getSubTitle());
                    holder.mFrameLayoutResultList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {

                            if (hasFocus) {
                                onItemGetFocus(view);
                                mColumnLabelTitle.setTextColor(Color.parseColor("#13d6f9"));
                                mFocusStatus = true;
                                holder.mTxtTitle.setSelected(true);
                                mOnGetSearchResultFocus.notifySearchResultFocus(true, position, view);
                                holder.mFocusImageView.setVisibility(View.VISIBLE);
                                if (position > (mDatas.size() - 7) && mSearchTotalSum > mDatas.size()) {
                                    if (mLoadMore) {
                                        Log.e("RecyclerViewPage---", page + "---");
                                        page = page + 1;
                                        if ("SearchListByKeyword".equals(mSearchType) && !TextUtils.isEmpty(mSearchType)) {
                                            requestKeywordSearchResultData(keyword, keywordType, programType, size * (page - 1), size);
                                        } else if ("RetrievalProgramSerialList".equals(mSearchType) && !TextUtils.isEmpty(mSearchType)) {
                                            requestRetrievalSearchResultData(type, year, area, classType, size * (page - 1), size);
                                        } else {
                                            requestKeywordSearchResultData(mInputString, mKeywordType, "-1", size * (page - 1), size);
                                        }
                                        Toast.makeText(mContext, R.string.search_Load_reminding, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                holder.mTxtTitle.setSelected(false);
                                mFocusStatus = false;
                                mOnGetSearchResultFocus.notifySearchResultFocus(false, position, view);
                                mLastFocusView = view;
                                onItemLoseFocus(view);
                                holder.mFocusImageView.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                    holder.mFrameLayoutResultList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String UUID = mDatas.get(position).getUUID();
                            String contentType = mDatas.get(position).getContentType();
                            String actionUri = mDatas.get(position).getActionUri();

                            JumpUtil.activityJump(mContext, Constant.OPEN_DETAILS, contentType, UUID, actionUri);

//                            Intent intent = new Intent(mContext, DetailsPageActivity.class);
//                            intent.putExtra("content_type", contentType);
//                            intent.putExtra("content_uuid", UUID);
//                            Log.e(TAG, "----UUID---" + UUID + "---contentType--" + contentType);
//                            mContext.startActivity(intent);
                        }
                    });

                    holder.mFrameLayoutResultList.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                    if (position == mDatas.size() - 1) {
                                        return true;
                                    }
                                } else if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                                    if (position % 6 == 0) {
                                        if (mKeyboardLastFocusView != null) {
                                            mKeyboardLastFocusView.requestFocus();
                                            mColumnLabelTitle.setTextColor(Color.parseColor("#ededed"));
                                            mOnGetSearchResultFocus.notifySearchResultFocus(false, -1, null);
                                            return true;
                                        }
                                    } else {
                                    }


                                } else if (i == KeyEvent.KEYCODE_DPAD_UP) {
                                    if (position < 6) {
                                        if (mLabelView != null) {
                                            mLabelView.requestFocus();
                                            return true;
                                        }
                                    } else {
                                        return false;
                                    }
                                } else if (i == KeyEvent.KEYCODE_BACK) {
                                    mLabelView.requestFocus();
                                    mFocusStatus = false;
                                    mLastFocusView = null;
                                    mRecyclerView.smoothScrollToPosition(0);
                                    return true;
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

    class ColumnViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout mFrameLayoutResultList;
        public ImageView mPosterImageView, mFocusImageView;
        public TextView mTxtTitle, mPosterTitle;

        public ColumnViewHolder(View itemView) {
            super(itemView);
            mFrameLayoutResultList = (FrameLayout) itemView.findViewById(R.id.id_frameLayout_column_list);
            mTxtTitle = (TextView) itemView.findViewById(R.id.id_column_title);
            mPosterTitle = (TextView) itemView.findViewById(R.id.id_column_poster_title);
            mPosterImageView = (ImageView) itemView.findViewById(R.id.id_column_imageView_poster);
            mFocusImageView = (ImageView) itemView.findViewById(R.id.id_column_imageView_focus);
            DisplayUtils.adjustView(getContext(), mPosterImageView, mFocusImageView, R.dimen.width_16dp, R.dimen.width_16dp);
        }

    }

}
