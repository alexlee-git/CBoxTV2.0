package tv.newtv.cboxtv.cms.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.search.view.SearchActivity;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.cms.util.Utils;

public class SearchFragment extends BaseFragment implements PageContract.View{

    FrameLayout mSearchView;
    ImageView imgSearch, focusView;
    private String param;
    private String contentId;
    private String actionType;
    private HorizontalRecyclerView hotSearchRecyclerView;
    private List<Page> mPrograms = new ArrayList<>();

    private View contentView;

    private PageContract.Presenter mPresenter;

    public static SearchFragment newInstance(Bundle paramBundle) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(paramBundle);
        return fragment;
    }

    @Override
    protected String getContentUUID() {
        return contentId;
    }

    @Override
    public boolean isNoTopView() {
        View focus = null;
        if (contentView != null) {
            focus = contentView.findFocus();
        }
        return focus == mSearchView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_search, container, false);
            hotSearchRecyclerView = contentView.findViewById(R.id.hot_search_list);
            mSearchView = contentView.findViewById(R.id.search_layout);
            imgSearch = contentView.findViewById(R.id.search_view);
            focusView = contentView.findViewById(R.id.focus_view);

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

        return contentView;
    }

    @Override
    public View getFirstFocusView() {
        return mSearchView;
    }


    private void bindData(List<Page> pageList) {
        mPrograms = pageList;

        HotSearchAdapter hotSearchAdapter = (HotSearchAdapter) hotSearchRecyclerView.getAdapter();
        if (hotSearchAdapter == null) {
            hotSearchAdapter = new HotSearchAdapter();
            hotSearchRecyclerView.setAdapter(hotSearchAdapter);
        } else {
            hotSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {
    }

    @Override
    public void onError(@NotNull Context context, @org.jetbrains.annotations.Nullable String desc) {
    }

    @Override
    public void onPageResult(List<Page> page) {
//        LogUtils.e("onPageResult11","onPageResult page : " + page);
//        changeBG(moduleData,contentId);
        bindData(page);

    }

    @Override
    public void startLoading() {

    }

    @Override
    public void loadingComplete() {

    }

    class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from
                    (getActivity()).inflate(R.layout.search_item_hot, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            if (mPrograms == null || mPrograms.size() <= 0) {
                return;
            }
            Program programData = mPrograms.get(0).getPrograms().get(position);

            holder.tv_name.setText(programData.getTitle());
            String url = programData.getImg();
            if (!TextUtils.isEmpty(url)) {
                Picasso.get().load(url)
                        .transform(new PosterCircleTransform(getContext(), 4))
                        .placeholder(R.drawable.focus_240_360).into(holder.img);
            }
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Utils.zoomByFactor(v, 1.1f, 200);
                        ScaleUtils.getInstance().onItemGetFocus(v, holder.imgfoused);
//                        holder.imgfoused.setVisibility(View.VISIBLE);
                        holder.tv_name.setSelected(true);

                    } else {
                        holder.tv_name.setSelected(false);
                        ScaleUtils.getInstance().onItemLoseFocus(v, holder.imgfoused);
//                        Utils.scaleToOriginalDimension(v, 1.1f, 200);
//                        holder.imgfoused.setVisibility(View.INVISIBLE);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        // TODO 添加类型判断进行跳转
                        Program program = mPrograms.get(0).getPrograms().get(position);
//                        Intent intent = new Intent(getActivity(), DetailsPageActivity.class);
//                        intent.putExtra("content_type", program.getContentType());
//                        intent.putExtra("content_uuid", program.getContentUUID());
//                        startActivity(intent);
                        JumpUtil.activityJump(LauncherApplication.AppContext, program);

                    } catch (Exception e) {
                        LogUtils.e(e);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPrograms != null ? mPrograms.get(0).getPrograms().size() : 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView img;
            ImageView imgfoused;
            TextView tv_name;

            MyViewHolder(View view) {
                super(view);
                img = view.findViewById(R.id.item_detail_img);
                imgfoused =  view.findViewById(R.id.item_detail_img_foused);
                tv_name = view.findViewById(R.id.item_detail_tv_name);

                //适配
                DisplayUtils.adjustView(getContext(), img, imgfoused, R.dimen.width_16dp, R.dimen.width_16dp);
            }
        }
    }
}
