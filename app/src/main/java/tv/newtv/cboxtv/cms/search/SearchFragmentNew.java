package tv.newtv.cboxtv.cms.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.PageContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.search.view.SearchActivity;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * Created by linzy on 2018/11/26.
 *
 * 搜索主页面重构
 */

public class SearchFragmentNew extends BaseFragment implements PageContract.View {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Program> list = new ArrayList<>();

    private PageContract.Presenter mPresenter;

    private String param;
    private String contentId;
    private String actionType;
    private View view;

    private static final int SEARCH_EDIT_VIEW = 0;
    private static final int SEARCH_RECYCLE_VIEW = 1;
    private static final int SEARCH_RECYCLE_TITLE_VIEW = 2;

    public static SearchFragmentNew newInstance(Bundle paramBundle) {
        SearchFragmentNew fragment = new SearchFragmentNew();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                if (mRecyclerView != null){
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            param = bundle.getString("nav_text");
            contentId = bundle.getString("content_id");
            actionType = bundle.getString("actionType");
        }
        mPresenter = new PageContract.ContentPresenter(getContext(), this);
        mPresenter.getPageContent(contentId);

        LogUploadUtils.uploadLog(Constant.LOG_NODE_SEARCH, "");//进入搜索页
    }

    @Override
    public void onPageResult(@Nullable List<Page> page) {
        bindData(page);
    }

    private void bindData(List<Page> pageList) {
//测试使用
//        for (Program program : pageList.get(0).getPrograms()) {
//            list.add(program);
//            list.add(program);
//            list.add(program);
//        }
        list = pageList.get(0).getPrograms();

        SearchContentAdapter hotSearchAdapter = (SearchContentAdapter) mRecyclerView.getAdapter();
        if (hotSearchAdapter == null) {
            hotSearchAdapter = new SearchContentAdapter(list);
            mRecyclerView.setAdapter(hotSearchAdapter);
        } else {
            hotSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void startLoading() {
    }

    @Override
    public void loadingComplete() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search_new, container, false);
            mRecyclerView = view.findViewById(R.id.search_recycleview_root);

            mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
        }
        return view;
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }

    @Override
    protected String getContentUUID() {
        return null;
    }


    @Override
    public View getFirstFocusView() {
        View firstFocusView = null;
        if (mRecyclerView != null){
            firstFocusView = ((SearchContentAdapter) mRecyclerView.getAdapter()).getFirstFocusView();
        }
        return firstFocusView;
    }

    class SearchContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Program> mDataList;
        private SearchEditHolder mSearchViewHolder;

        public View getFirstFocusView() {
            View focusView = null;
            if (mSearchViewHolder != null){
                focusView = mSearchViewHolder.mSearchView;
            }
            return focusView;
        }

        public SearchContentAdapter(List<Program> dataList) {
            this.mDataList = dataList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            if (viewType == SEARCH_EDIT_VIEW) {
                holder = new SearchEditHolder(LayoutInflater.from(getContext()).inflate(R.layout.search_edit_holder_item, parent, false));
                mSearchViewHolder = (SearchEditHolder) holder;
            } else if (viewType == SEARCH_RECYCLE_TITLE_VIEW) {
                holder = new SearchRecycleTitleHolder(LayoutInflater.from(getContext()).inflate(R.layout.search_recycleview_title_holder_item, parent, false));
            } else {
                holder = new SearchRecycleHolder(LayoutInflater.from(getContext()).inflate(R.layout.search_layout_item, parent, false));
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            try {
                if (holder instanceof SearchEditHolder) {
                    SearchEditHolder searchEditHolder = (SearchEditHolder) holder;
                }
                if (holder instanceof SearchRecycleTitleHolder) {
                    SearchRecycleTitleHolder searchRecycleTitleHolder = (SearchRecycleTitleHolder) holder;
                }
                if (holder instanceof SearchRecycleHolder) {
                    final SearchRecycleHolder searchRecycleHolder = ((SearchRecycleHolder) holder);
                    int pos = searchRecycleHolder.getAdapterPosition() - 2;
                    List<Program> programs = mDataList.subList(pos * 6, (pos + 1) * 6);
                    searchRecycleHolder.update(programs);
                }
            } catch (Exception error) {
                LogUtils.e("error", "error : " + error.getMessage());
            }

        }

        @Override
        public int getItemCount() {
            if (mDataList == null || mDataList.size() == 0) {
                return 1;
            }
            int count = (int) Math.floor(mDataList.size() / 6);
            return count + 2;
        }


        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return SEARCH_EDIT_VIEW;
            }
            if (position == 1) {
                return SEARCH_RECYCLE_TITLE_VIEW;
            }
            return SEARCH_RECYCLE_VIEW;
        }

        class SearchEditHolder extends RecyclerView.ViewHolder {

            private FrameLayout mSearchView;
            private ImageView imgSearch, focusView;

            public SearchEditHolder(View itemView) {
                super(itemView);
                if (itemView != null) {
                    LogUtils.e("itemView", "itemView : " + itemView);
                    mSearchView = itemView.findViewById(R.id.search_layout);
                    imgSearch = itemView.findViewById(R.id.search_view);
                    focusView = itemView.findViewById(R.id.focus_view);

                    DisplayUtils.adjustView(getActivity(), imgSearch, focusView, R.dimen.width_17dp, R.dimen.height_16dp);

                    mSearchView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), SearchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        }
                    });
                    mSearchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                focusView.setVisibility(View.VISIBLE);
                                ScaleUtils.getInstance().onItemGetFocus(v);
                            } else {
                                focusView.setVisibility(View.INVISIBLE);
                                ScaleUtils.getInstance().onItemLoseFocus(v);
                            }
                        }
                    });

                }
            }
        }

        class SearchRecycleTitleHolder extends RecyclerView.ViewHolder {

            public SearchRecycleTitleHolder(View itemView) {
                super(itemView);

            }
        }

        class SearchRecycleHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {
            private List<RecycleImageView> iconMaps;
            private List<TextView> titleMaps;
            private List<FrameLayout> focusMaps;
            private List<FrameLayout> actionMaps;

            @SuppressLint("UseSparseArrays")
            SearchRecycleHolder(View itemView) {
                super(itemView);
                if (itemView != null) {
                    for (int index = 1; index <= 6; index++) {
                        if (iconMaps == null) {
                            iconMaps = new ArrayList<>(6);
                        }
                        if (titleMaps == null) {
                            titleMaps = new ArrayList<>(6);
                        }
                        if (focusMaps == null){
                            focusMaps = new ArrayList<>(6);
                        }
                        if (actionMaps == null){
                            actionMaps = new ArrayList<>(6);
                        }

                        String parentTag = String.format(Locale.getDefault(),"id_module_8_view%d",index);
                        int parentID = itemView.getContext().getResources().getIdentifier(parentTag,"id",itemView.getContext().getPackageName());
                        String posterTag = String.format(Locale.getDefault(), "cell_008_%d_poster", index);
                        String titleTag = String.format(Locale.getDefault(), "cell_008_%d_title", index);
                        String focusTag = String.format(Locale.getDefault(),"cell_008_%d_focus",index);
                        iconMaps.add((RecycleImageView) itemView.findViewWithTag(posterTag));
                        titleMaps.add((TextView) itemView.findViewWithTag(titleTag));
                        focusMaps.add((FrameLayout) itemView.findViewWithTag(focusTag));
                        actionMaps.add((FrameLayout) itemView.findViewById(parentID));
                    }
                }
            }

            public void update(List<Program> programs) {
                int index = 0;
                for (final Program program : programs) {
                    FrameLayout imgfoused = actionMaps.get(index);
                    RecycleImageView poster = iconMaps.get(index);
                    TextView title = titleMaps.get(index);

                    title.setText(program.getTitle());
                    String url = program.getImg();
                    if (!TextUtils.isEmpty(url)) {
                        Picasso.get().load(url)
                                .transform(new PosterCircleTransform(getContext(), 4))
                                .placeholder(R.drawable.focus_240_360).into(poster);
                    }

                    //适配
//                    DisplayUtils.adjustView(getContext(), poster, imgfoused, R.dimen.width_16dp, R.dimen.width_16dp);

                    focusMaps.get(index).setOnFocusChangeListener(this);
                    focusMaps.get(index).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JumpUtil.activityJump(LauncherApplication.AppContext, program);
                        }
                    });
                    index++;
                }
            }

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                int index = focusMaps.indexOf(v);
                titleMaps.get(index).setSelected(hasFocus);
                if(hasFocus) {
                    ScaleUtils.getInstance().onItemGetFocus(actionMaps.get(index));
                }else{
                    ScaleUtils.getInstance().onItemLoseFocus(actionMaps.get(index));
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.destroy();
            mPresenter = null;
        }
        mRecyclerView = null;
    }
}
