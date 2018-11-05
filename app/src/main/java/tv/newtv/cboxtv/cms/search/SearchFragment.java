package tv.newtv.cboxtv.cms.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.BuildConfig;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleItem;
import tv.newtv.cboxtv.cms.mainPage.view.BaseFragment;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.search.view.SearchActivity;
import tv.newtv.cboxtv.cms.util.ModuleUtils;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.cms.util.Utils;

//import tv.newtv.cboxtv.cms.net.ApiUtil;


public class SearchFragment extends BaseFragment {
    private static final String TAG = SearchFragment.class.getName();
    FrameLayout mSearchView;
    ImageView imgSearch, focusView;
    private String param;
    private String contentId;
    private String actionType;
    private HorizontalRecyclerView hotSearchRecyclerView;
    private List<SubContent> mPrograms = new ArrayList<>();

    private Interpolator mSpringInterpolator;
    private Disposable mDisposable;
    private View contentView;

    public static SearchFragment newInstance(Bundle paramBundle) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(paramBundle);
        return fragment;
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

        LogUploadUtils.uploadLog(Constant.LOG_NODE_SEARCH, "");//进入搜索页
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_search, container, false);
            hotSearchRecyclerView = (HorizontalRecyclerView) contentView.findViewById(R.id.hot_search_list);
            mSearchView = (FrameLayout) contentView.findViewById(R.id.search_layout);
            imgSearch = contentView.findViewById(R.id.search_view);
            focusView = contentView.findViewById(R.id.focus_view);

            DisplayUtils.adjustView(getActivity(), imgSearch, focusView, R.dimen.width_17dp, R.dimen.height_16dp);

//            int offSize = getResources().getDimensionPixelOffset(R.dimen.width_3dp);
//            FrameLayout.LayoutParams posterPara = new FrameLayout.LayoutParams(imgSearch.getLayoutParams());
//            posterPara.setMargins(0,0,0,0);
//            imgSearch.setLayoutParams(posterPara);
//            imgSearch.requestLayout();

//            ViewGroup.LayoutParams focusPara = focusView.getLayoutParams();
//            focusPara.width = posterPara.width+2*offSize;
//            focusPara.height = posterPara.height+2*offSize;
//            focusView.setLayoutParams(focusPara);
//            focusView.requestLayout();

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
                        onItemGetFocus(v);
                    } else {
                        focusView.setVisibility(View.INVISIBLE);
                        onItemLoseFocus(v);
                    }
                }
            });

            mSpringInterpolator = new OvershootInterpolator(2.2f);
        }

        return contentView;
    }

    @Override
    public View getFirstFocusView() {
        return mSearchView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getData();
    }

    /*
    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        getData();
    }
    */

    private void getData() {
        NetClient.INSTANCE
                .getPageDataApi()
                .getPageData(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID, contentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {

                            String result = value.string();
                            ModuleInfoResult moduleData = ModuleUtils.getInstance()
                                    .parseJsonForModuleInfo(result);
                            changeBG(moduleData,contentId);
                            bindData(moduleData.getDatas());
                        } catch (Exception e) {
                            LogUtils.e(e);
                        }

                        unSubscribe();

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(Constant.TAG, "-----requestPageDataFromServer---onError-----");
                        unSubscribe();
                    }

                    @Override
                    public void onComplete() {
                        unSubscribe();
                    }
                });
    }

    private void bindData(List<ModuleItem> moduleItems) {
        if (moduleItems != null && moduleItems.size() > 0) {
            mPrograms.clear();
            for (ModuleItem moduleItem : moduleItems) {
                mPrograms.addAll(moduleItem.getDatas());
            }
        }

        HotSearchAdapter hotSearchAdapter = (HotSearchAdapter) hotSearchRecyclerView.getAdapter();
        if (hotSearchAdapter == null) {
            hotSearchAdapter = new HotSearchAdapter();
            hotSearchRecyclerView.setAdapter(hotSearchAdapter);
        } else {
            hotSearchAdapter.notifyDataSetChanged();
        }
    }

    private void onItemGetFocus(View view) {
//        ImageView focusImageView = (ImageView) view.findViewById(R.id.focus_view);
//        focusImageView.setVisibility(View.VISIBLE);

        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    private void onItemLoseFocus(View view) {
//        ImageView focusImageView = (ImageView) view.findViewById(R.id.focus_view);
//        focusImageView.setVisibility(View.INVISIBLE);

        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
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
            final SubContent programInfo = mPrograms.get(position);
            holder.tv_name.setText(programInfo.getTitle());
            String url = programInfo.getVImage();
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
                        SubContent program = mPrograms.get(position);
//                        Intent intent = new Intent(getActivity(), DetailsPageActivity.class);
//                        intent.putExtra("content_type", program.getContentType());
//                        intent.putExtra("content_uuid", program.getContentUUID());
//                        startActivity(intent);
//                        JumpUtil.activityJump(LauncherApplication.AppContext, program);

                    } catch (Exception e) {
                        LogUtils.e(e);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPrograms != null ? mPrograms.size() : 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView img;
            ImageView imgfoused;
            TextView tv_name;

            MyViewHolder(View view) {
                super(view);
                img = (ImageView) view.findViewById(R.id.item_detail_img);
                imgfoused = (ImageView) view.findViewById(R.id.item_detail_img_foused);
                tv_name = (TextView) view.findViewById(R.id.item_detail_tv_name);

                //适配
                DisplayUtils.adjustView(getContext(), img, imgfoused, R.dimen.width_16dp, R.dimen.width_16dp);
            }
        }
    }
}
