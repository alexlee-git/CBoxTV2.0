package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.SearchContract;
import com.newtv.cms.contract.SuggestContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         17:10
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public class SuggestView extends RelativeLayout implements IEpisode, SuggestContract.View,
        SearchContract.View {

    public static final int TYPE_COLUMN_SEARCH = 0;     //搜索
    public static final int TYPE_COLUMN_SUGGEST = 1;    //相关栏目
    public static final int TYPE_COLUMN_FIGURES = 2;    //栏目相关主持人
    public static final int TYPE_PERSON_FIGURES = 3;    //人物详情（主持人）相关主持人

    private static final String INFO_TEXT_TAG = "info_text";
    private String contentUUID;
    private View currentFocus;
    private SuggestContract.Presenter mSuggestPresenter;
    private SearchContract.Presenter mSearchPresenter;
    private View controlView;

    public SuggestView(Context context) {
        this(context, null);
    }

    public SuggestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuggestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize();
    }

    public static String getTitleByType(int type) {
        switch (type) {
            case TYPE_COLUMN_FIGURES:
                return "名人堂";
            case TYPE_COLUMN_SEARCH:
            case TYPE_COLUMN_SUGGEST:
                return "相关推荐";
            case TYPE_PERSON_FIGURES:
                return "TA 相关的名人";
            default:
                return "内容列表";
        }
    }

    @Override
    public void destroy() {
        controlView = null;
        currentFocus = null;
        removeAllViews();

        if (mSearchPresenter != null) {
            mSearchPresenter.destroy();
            mSearchPresenter = null;
        }

        if (mSuggestPresenter != null) {
            mSuggestPresenter.destroy();
            mSuggestPresenter = null;
        }
    }

    private void ShowInfoTextView(String text) {
        TextView infoText = findViewWithTag(INFO_TEXT_TAG);
        if (infoText == null) {
            infoText = new TextView(getContext());
            infoText.setTag(INFO_TEXT_TAG);
            infoText.setTextAppearance(getContext(), R.style.ModuleTitleStyle);
            infoText.setText(text);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                    .WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            infoText.setLayoutParams(layoutParams);
            addView(infoText, layoutParams);
        } else {
            infoText.setText(text);
        }
    }

    private void initalize() {
        mSuggestPresenter = new SuggestContract.SuggestPresenter(getContext(), this);
        mSearchPresenter = new SearchContract.SearchPresenter(getContext(), this);
    }

    @Nullable
    public View getDefaultFocus() {
        if (getChildCount() <= 0) return this;
        if (getChildAt(0) instanceof AiyaRecyclerView) {
            return ((AiyaRecyclerView) getChildAt(0)).getDefaultFocusView();
        } else {
            if(currentFocus!=null) {
                return currentFocus;
            }
            View view = findViewWithTag("cell_008_1");
            if(view != null){
                return view;
            }
        }
        return null;
    }

    public void setContentUUID(int type, @Nullable Content content, View controllView, String contentType) {
        if(content == null || TextUtils.isEmpty(content.getContentID())){
            onError(getContext(), "" , "数据ID不正确");
            return;
        }

        controlView = controllView;
        contentUUID = content.getContentID();

        switch (type) {
            case TYPE_COLUMN_FIGURES:
                mSuggestPresenter.getColumnFigures(contentUUID);
                break;
            case TYPE_COLUMN_SUGGEST:
                mSuggestPresenter.getColumnSuggest(contentUUID);
                break;
            case TYPE_COLUMN_SEARCH:
                String conType = content.getContentType();
                if (!TextUtils.isEmpty(contentType)) {
                    conType = contentType;
                }
                SearchContract.SearchCondition searchCondition = SearchContract.SearchCondition
                        .Builder()
                        .setRows("6")
                        .setContentType(conType)
                        .setVideoType(content.getVideoType());
                mSearchPresenter.search(searchCondition);
                break;
            case TYPE_PERSON_FIGURES:
                mSuggestPresenter.getPersonFigureList(contentUUID);
            default:

                break;
        }
    }

    public void setContentUUID(int type, @Nullable Content content, View controllView) {
        setContentUUID(type, content, controllView, null);

    }

    private void buildUI(List<SubContent> infos, int type) {
        switch (type) {
            case EpisodeHelper.TYPE_PROGRAME_XG:
                buildRecycleView(infos);
                break;
            case TYPE_COLUMN_SEARCH:
            case TYPE_COLUMN_SUGGEST:
            case TYPE_COLUMN_FIGURES:
            case TYPE_PERSON_FIGURES:
                buildListView(infos, type);
                break;
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (controlView != null) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            controlView.getLayoutParams();
                    layoutParams.height = (int) getResources().getDimension(R.dimen.height_83px);
                    controlView.setLayoutParams(layoutParams);
                    controlView.setVisibility(View.VISIBLE);
                }
                setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    private SubContent getItem(List<SubContent> infos, int
            index) {
        if (infos == null || infos.size() <= index) {
            return null;
        }
        return infos.get(index);
    }

    private void buildListView(List<SubContent> infos, int type) {
        if (infos.size() > 0) {
            removeAllViews();

            View view = LayoutInflater.from(getContext()).inflate(R.layout
                    .episode_six_content_v2, this, false);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                    .WRAP_CONTENT);
            view.findViewWithTag("module_008_title_icon").setVisibility(View.VISIBLE);
            TextView titleview = view.findViewWithTag("module_008_title");
            if (titleview != null) {
                titleview.setVisibility(View.VISIBLE);
                titleview.setText(getTitleByType(type));
            }
            view.setLayoutParams(layoutParams);
            addView(view, layoutParams);

            for (int index = 0; index < 8; index++) {
                final View target = view.findViewWithTag("cell_008_" + (index + 1));
                SubContent itemInfo = getItem(infos, index);
                new SuggestViewHolder(target, index, itemInfo);
            }
        } else {
            onLoadError("暂时没有数据");
        }
    }


    private void buildRecycleView(List<SubContent> infos) {
        if (infos.size() > 0) {
            removeAllViews();

            AiyaRecyclerView aiyaRecyclerView = new AiyaRecyclerView(getContext());
            aiyaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            aiyaRecyclerView.setAlign(AiyaRecyclerView.ALIGN_AUTO);
            int margin = getContext().getResources().getDimensionPixelOffset(R.dimen.width_78px);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                    .WRAP_CONTENT);
            layoutParams.leftMargin = margin;
            layoutParams.rightMargin = margin;
            aiyaRecyclerView.setLayoutParams(layoutParams);
            addView(aiyaRecyclerView, layoutParams);

            AiyaAdapter aiyaAdapter = new AiyaAdapter(infos);
            aiyaRecyclerView.setAdapter(aiyaAdapter);
        } else {
            onLoadError("暂时没有数据");
        }
    }

    private void onLoadError(String message) {

        if (getParent() != null) {
            ViewGroup parentView = (ViewGroup) getParent();
            parentView.removeView(this);
            if (controlView != null) {
                parentView.removeView(controlView);
            }
        }
    }

    @Override
    public String getContentUUID() {
        return contentUUID;
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        if (getChildAt(0) == null
                || !(getChildAt(0) instanceof ViewGroup) || ((ViewGroup) getChildAt
                (0)).getChildCount() == 0) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(getChildCount() > 0) {
                if(getChildAt(0) instanceof RecyclerView){
                    AiyaRecyclerView recyclerView = (AiyaRecyclerView) getChildAt(0);
                    return recyclerView.dispatchKeyEvent(event);
                }else {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent
                            .KEYCODE_DPAD_DOWN) {
                        if (!getChildAt(0).hasFocus()) {
                            View focusView = getDefaultFocus();
                            if(focusView != null) {
                                focusView.requestFocus();
                                return true;
                            }
                        }
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                        View leftView = FocusFinder.getInstance().findNextFocus(this, findFocus()
                                , View
                                .FOCUS_LEFT);
                        if (leftView != null) {
                            leftView.requestFocus();
                        }
                        return true;
                    } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        View rightView = FocusFinder.getInstance().findNextFocus(this, findFocus
                                (), View
                                .FOCUS_RIGHT);
                        if (rightView != null) {
                            rightView.requestFocus();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        if (child != null) {
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int height = child.getMeasuredHeight();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public void columnSuggestResult(ArrayList<SubContent> result) {
        if (result == null || result.size() <= 0) {
            onLoadError("获取结果为空");
            return;
        }

        buildUI(result, TYPE_COLUMN_SUGGEST);
    }

    @Override
    public void columnFiguresResult(ArrayList<SubContent> result) {
        if (result == null || result.size() <= 0) {
            onLoadError("获取结果为空");
            return;
        }

        buildUI(result, TYPE_COLUMN_FIGURES);
    }

    @Override
    public void columnPersonFiguresResult(ArrayList<SubContent> result) {
        if (result == null || result.size() <= 0) {
            onLoadError("获取结果为空");
            return;
        }

        buildUI(result, TYPE_PERSON_FIGURES);
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {
        LogUtils.e("SuggestView",desc);
    }

    @Override
    public void searchResult(long requestID, @Nullable ArrayList<SubContent> result, @Nullable Integer total) {
        if (result == null || result.size() <= 0) {
            onLoadError("获取结果为空");
            return;
        }

        buildUI(result, TYPE_COLUMN_SEARCH);
    }

    private static class AiyaAdapter extends RecyclerView.Adapter<AiyaViewHolder> {
        private List<SubContent> mData;

        private AiyaAdapter(List<SubContent> data) {
            mData = data;
        }

        @Override
        public AiyaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .item_details_horizontal_layout, parent,
                    false);
            return new AiyaViewHolder(view);
        }

        private SubContent getItem(int postion) {
            if (mData == null) return null;
            if (mData.size() < postion) return null;
            if (postion < 0) return null;
            return mData.get(postion);
        }

        @Override
        public void onBindViewHolder(AiyaViewHolder holder, int position) {
            if (holder != null) {
                SubContent data = getItem(position);
                holder.setData(data);
            }
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }
    }

    private static class AiyaViewHolder extends RecyclerView.ViewHolder {

        SubContent infoData;
        private ImageView Poster;
        private TextView Title;
        private View FocusView;

        AiyaViewHolder(View itemView) {
            super(itemView);
            Poster = itemView.findViewWithTag("tag_poster_image");
            Title = itemView.findViewWithTag("tag_poster_title");
            FocusView = itemView.findViewWithTag("tag_img_focus");

            DisplayUtils.adjustView(itemView.getContext().getApplicationContext(), Poster,
                    FocusView, R.dimen.width_16dp, R.dimen.width_16dp);//view适配
        }

        public void setData(SubContent data) {
            if (data == null) return;
            infoData = data;
            Picasso.get()
                    .load(data.getHImage())
                    .resize(390, 214)
                    .into(Poster);

            Title.setText(data.getTitle());
            itemView.findViewById(R.id.id_module_view).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO 界面跳转
                    JumpUtil.detailsJumpActivity(view.getContext(),
                            infoData.getContentType(), infoData.getContentUUID());
                }
            });
            itemView.findViewById(R.id.id_module_view).setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    View viewMove = view.findViewWithTag("tag_poster_title");
                    if (viewMove != null) {
                        viewMove.setSelected(b);
                    }
                    FocusView.setVisibility(b ? View.VISIBLE : View.GONE);
                    if (b) {
                        ScaleUtils.getInstance().onItemGetFocus(itemView.findViewById(R.id
                                .id_module_view));
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(itemView.findViewById(R.id
                                .id_module_view));
                    }
                }
            });
        }
    }

    private class SuggestViewHolder {
        SubContent itemInfo;

        SuggestViewHolder(final View target, int index, SubContent info) {
            itemInfo = info;
            if (target != null) {
                if (itemInfo != null) {
                    target.setVisibility(View.VISIBLE);
                    ImageView poster = target.findViewWithTag("tag_poster_image");
                    final View focusView = target.findViewWithTag("tag_img_focus");
                    TextView title = target.findViewWithTag("tag_poster_title");

                    //适配
//                    DisplayUtils.adjustView(getContext(), poster, focusView, R.dimen.width_16dp,
//                            R.dimen.width_16dp);//适配

                    if (!TextUtils.isEmpty(itemInfo.getVImage())) {
                        poster.setScaleType(ImageView.ScaleType.FIT_XY);
                        RequestCreator picasso = Picasso.get()
                                .load(itemInfo.getVImage())
                                .transform(new PosterCircleTransform(target.getContext(), 4))
                                .priority(Picasso.Priority.HIGH)
                                .stableKey(itemInfo.getVImage())
                                .resize(240, 360)
                                .config(Bitmap.Config.RGB_565);
                        picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                                .focus_240_360);
                        picasso.into(poster);
                    } else {
                        poster.setScaleType(ImageView.ScaleType.FIT_XY);
                        RequestCreator picasso = Picasso.get()
                                .load(R.drawable.focus_240_360)
                                .priority(Picasso.Priority.HIGH)
                                .transform(new PosterCircleTransform(target.getContext(), 4))
                                .resize(240, 360)
                                .config(Bitmap.Config.RGB_565);
                        picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                                .focus_240_360);
                        picasso.into(poster);
                    }

//                    Picasso.with(target.getContext())
//                            .load(itemInfo.getvImage())
//                            .resize(240, 360)
//                            .into(poster);

                    title.setText(itemInfo.getTitle());

                    target.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO 界面跳转
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL_SUGGESt, itemInfo
                                    .getContentID());
                            JumpUtil.detailsJumpActivity(view.getContext(),
                                    itemInfo.getContentType(), itemInfo.getContentID());
                        }
                    });

                    target.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {

                            View viewMove = view.findViewWithTag("tag_poster_title");
                            if (viewMove != null) {
                                viewMove.setSelected(b);
                            }

                            if(focusView !=null) {
                                focusView.setVisibility(b ? View.VISIBLE : View.GONE);
                                if (b) {
                                    ScaleUtils.getInstance().onItemGetFocus(target);
                                } else {
                                    ScaleUtils.getInstance().onItemLoseFocus(target);
                                }
                            }
                        }
                    });

                    if (currentFocus == null && index == 0) {
                        currentFocus = target;
                    }
                } else {
                    target.setVisibility(View.GONE);
                }
            }
        }
    }


}

