package tv.newtv.cboxtv.cms.details.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.PageHelper;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.views.CurrentPlayImageView;
import tv.newtv.cboxtv.views.RecycleImageView;

/**
 * Created by gaoleichao on 2018/4/1.
 */

public class ColumnDetailsAdapter extends BaseRecyclerAdapter<ProgramSeriesInfo, RecyclerView
        .ViewHolder> {

    public static final int HEAD_COLUMN = 1001;
    public static final int HEAD_PERSON = 1005;
    public static final int CONTENT_VERTICAL = 1002;
    public static final int CONTENT_HORIZONTAL = 1003;
    public static final int PLAY_LIST = 1004;
    public static final int VERTICAL_SlIDE = 1005;

    private Context context;
    private Interpolator mSpringInterpolator;
    private WeakReference<OnRecycleItemClickListener> listener;

    private String currentPlayUUID;
    private int PageIndex = 0;

    private int index = 0;
    private int defultFocusPosition = -1;
    private PosterCircleTransform mPosterCircleTransform;

    public ColumnDetailsAdapter(Context context, OnRecycleItemClickListener listener) {
        this.context = context;
        this.listener = new WeakReference<>(listener);
        mSpringInterpolator = new OvershootInterpolator(2.2f);
        mPosterCircleTransform = new PosterCircleTransform(context, 4);
    }

    public static String getInfalteContent(String a) {
        String content = "";
        if (a == null || a.length() <= 0 || a.equalsIgnoreCase("null")) {
            content = "";
        } else {
            content = a + " | ";
        }
        return content;
    }

    public static String getsplit(String content) {
        String str = "";
        if (content == null || content.length() <= 0) {
            return "";
        }
        if (content.substring(content.length() - 1, content.length()).equalsIgnoreCase("|")) {
            str = content.substring(0, content.length() - 1);
        } else {
            str = content;
        }
        return str;
    }

    public void destroy() {
        context = null;
        listener = null;
        mSpringInterpolator = null;
        mPosterCircleTransform = null;
    }

    @Override
    public void appendToList(List<ProgramSeriesInfo> list) {
        super.appendToList(list);

        if (list != null && list.size() > 0) {
            PageIndex = (int) Math.ceil(index / 8d);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == CONTENT_VERTICAL) {
            Log.e("gao", "CONTENT_VERTICAL");
            viewHolder = new ContentVericalViewHolder(LayoutInflater.from(context).inflate(R
                    .layout.fragment_usercenter_content, null));
        } else if (viewType == CONTENT_HORIZONTAL) {
            Log.e("gao", "CONTENT_HORIZONTAL");
            viewHolder = new ContentHorizontalViewHolder(LayoutInflater.from(context).inflate(R
                    .layout.item_detail_list_horizontal, null));
        } else if (viewType == PLAY_LIST) {
            Log.e("gao", "PLAY_LIST");
            viewHolder = new PlayListViewHolder(LayoutInflater.from(context).inflate(R.layout
                    .item_detail_play, null));
        } else if (viewType == VERTICAL_SlIDE) {
            viewHolder = new VerticalSlideHolder(LayoutInflater.from(context).inflate(R.layout
                    .item_detail_list_slide, null));
        }
        return viewHolder;
    }

    public void setPlayInDex(int inDex) {
        this.index = inDex;
    }

    public void setCurrentPlayUUID(int index, String uuid) {
        setPlayInDex(index);
        currentPlayUUID = uuid;
        notifyDataSetChanged();
    }

    //layoutId 2为播放列表布局,4竖海报、
    @Override
    public int getItemViewType(int position) {
        ProgramSeriesInfo info = mList.get(position);
        if (info.layoutId == 2) {
            return PLAY_LIST;
        } else if (info.layoutId == 3) {
            return CONTENT_HORIZONTAL;
        } else if (info.layoutId == 4) {
            return CONTENT_VERTICAL;
        } else if (info.layoutId == 5) {
            return VERTICAL_SlIDE;
        }
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {


        if (holder instanceof ContentVericalViewHolder) {
            ContentVericalViewHolder viewHolder = (ContentVericalViewHolder) holder;
            if (mList != null && mList.size() > 0) {
                ProgramSeriesInfo entity = mList.get(position);
                if (entity != null) {
                    viewHolder.titleIconIv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setText(entity.layoutTitle);
                    for (int i = 0; i < entity.getData().size(); i++) {
                        ProgramSeriesInfo.ProgramsInfo programsInfo = entity.getData().get(i);
                        setPosterData(viewHolder.viewList.get(i),
                                programsInfo.getvImage(), programsInfo.getTitle(),
                                CONTENT_VERTICAL, programsInfo.getContentUUID());
                        if (i == 5) {
                            return;
                        }
                    }
                }

            }
        } else if (holder instanceof ContentHorizontalViewHolder) {
            final ContentHorizontalViewHolder viewHolder = (ContentHorizontalViewHolder) holder;
            if (mList != null && mList.size() > 0) {
                final ProgramSeriesInfo entity = mList.get(position);
                if (entity != null) {
                    viewHolder.titleIconIv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setText(entity.layoutTitle);
                    viewHolder.mRecycleView.removeAllViews();

                    viewHolder.ivLeft.setVisibility(View.INVISIBLE);
                    viewHolder.mAdapter.clear();
                    if (entity.getData().size()>4){
                        viewHolder.ivRight.setVisibility(View.VISIBLE);
                    }else {
                        viewHolder.ivRight.setVisibility(View.INVISIBLE);
                    }
                    viewHolder.mAdapter.appendToList(entity.getData());
                    viewHolder.mAdapter.notifyDataSetChanged();
                }
            }
        } else if (holder instanceof VerticalSlideHolder) {

            final VerticalSlideHolder viewHolder = (VerticalSlideHolder) holder;
            if (mList != null && mList.size() > 0) {
                final ProgramSeriesInfo entity = mList.get(position);
                if (entity != null) {
                    if (entity.getData().size() > 6) {
                        viewHolder.ivRight.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.ivRight.setVisibility(View.INVISIBLE);
                    }
                    viewHolder.titleIconIv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setText(entity.layoutTitle);
                    viewHolder.aiyaRecyclerView.removeAllViews();
                    viewHolder.mAdapter.clear();
                    viewHolder.mAdapter.appendToList(entity.getData());
                    viewHolder.mAdapter.notifyDataSetChanged();
                }
            }

        } else if (holder instanceof PlayListViewHolder) {
            final PlayListViewHolder viewHolder = (PlayListViewHolder) holder;
            if (mList != null && mList.size() > 0) {
                final ProgramSeriesInfo entity = mList.get(position);
                if (entity != null) {
                    viewHolder.titleIconIv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setVisibility(View.VISIBLE);
                    viewHolder.titleTv.setText(entity.layoutTitle);

                    if (viewHolder.mPageDaoImpl == null) {
                        viewHolder.mPageDaoImpl = new PageHelper<ProgramSeriesInfo.ProgramsInfo>
                                (entity.getData(), 8);
                        viewHolder.dataList.clear();
                        viewHolder.mPageDaoImpl.setCurrentPage(viewHolder.mPageDaoImpl
                                .getCurrentPage(index));
                        viewHolder.dataList.addAll(viewHolder.mPageDaoImpl.currentList());
                    }

                    if (viewHolder.mAdapter == null) {
                        viewHolder.mAdapter = new PlayerSelectPageAdapter(context, new
                                PlayerSelectPageAdapter.OnItemEnterKeyListener() {
                                    @Override
                                    public void onEnterKey(View v, int mPosition) {
                                        viewHolder.mPageDaoImpl.setCurrentPage(mPosition + 1);
                                        viewHolder.dataList.clear();
                                        viewHolder.dataList.addAll(viewHolder.mPageDaoImpl
                                                .currentList());
                                        defultFocusPosition = 0;
                                        notifyDataSetChanged();
                                    }
                                });

                        viewHolder.mBtnEpisodeView.setAdapter(viewHolder.mAdapter);
                        viewHolder.mBtnEpisodeView.removeAllViews();
                        viewHolder.mAdapter.clear();
                        for (int i = 0; i < viewHolder.mPageDaoImpl.getPageNum(); i++) {
                            viewHolder.mAdapter.append(viewHolder.mPageDaoImpl.getPageText(i + 1));
                        }
                        viewHolder.mAdapter.notifyDataSetChanged();
                        if (viewHolder.mPageDaoImpl.hasNextPage()) {
                            viewHolder.mRightIv.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.mRightIv.setVisibility(View.GONE);
                        }
                        if (viewHolder.mPageDaoImpl.hasPrePage()) {
                            viewHolder.mLeftIv.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.mLeftIv.setVisibility(View.GONE);
                        }
                    }

                    for (int i = 0; i < viewHolder.viewList.size(); i++) {
                        FrameLayout view = viewHolder.viewList.get(i);
                        if (i < viewHolder.dataList.size()) {
                            ProgramSeriesInfo.ProgramsInfo programsInfo = viewHolder.dataList.get
                                    (i);
                            setPosterData(view, programsInfo.gethImage(), programsInfo.getTitle()
                                    , PLAY_LIST, programsInfo.getContentUUID());

                        } else {
                            view.setVisibility(View.GONE);
                        }

                    }
                    viewHolder.mAdapter.setSelectIndex(viewHolder.mPageDaoImpl.getCurrentPage() -
                            1);
                    if (defultFocusPosition > -1) {
                        if (viewHolder.dataList.size() > defultFocusPosition) {
                            viewHolder.viewList.get(defultFocusPosition).requestFocus();
                        } else {
                            viewHolder.viewList.get(0).requestFocus();
                        }
                    }
                }

            }
        }
    }

    private void setPosterData(FrameLayout mModuleView, String img, String title, int type, String
            uuid) {
        RecycleImageView posterIv = (RecycleImageView) mModuleView.findViewWithTag
                ("tag_poster_image");
        ImageView focusIv = mModuleView.findViewWithTag("tag_img_focus");


        DisplayUtils.adjustView(context, posterIv, focusIv,R.dimen.width_16dp,R.dimen.width_16dp);//适配


        TextView subTitleTv = (TextView) mModuleView.findViewWithTag("tag_poster_title");
        if (type == 1002) {
            if (posterIv != null) {
                posterIv.placeHolder(R.drawable.focus_240_360).hasCorner(true).load(img);
            }

        } else {
            if (posterIv != null) {
                posterIv.placeHolder(R.drawable.focus_384_216).hasCorner(true).load(img);
                if (posterIv instanceof CurrentPlayImageView) {
                    if (!TextUtils.isEmpty(uuid)) {
                        ((CurrentPlayImageView) posterIv).setIsPlaying(uuid.equals
                                (currentPlayUUID));
                    }
                }
            }


        }

        mModuleView.setVisibility(View.VISIBLE);


        if (subTitleTv != null) {
            if (!TextUtils.isEmpty(title)) {
                subTitleTv.setVisibility(View.VISIBLE);
                subTitleTv.setText(title);
            }
        }
    }

    private void onItemLoseFocus(View view) {
        ImageView focusImageView = (ImageView) view.findViewWithTag("tag_img_focus");
        if (focusImageView != null) {
            focusImageView.setVisibility(View.INVISIBLE);
        }
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    private void onItemGetFocus(View view) {
        ImageView focusImageView = (ImageView) view.findViewWithTag("tag_img_focus");
        ImageView posterImageView = (ImageView) view.findViewWithTag("tag_poster_image");

        if (focusImageView != null) {
            focusImageView.setVisibility(View.VISIBLE);
        }
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    class ContentVericalViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener, View.OnKeyListener {
        private ImageView titleIconIv;
        private TextView titleTv;
        private List<FrameLayout> viewList;
        private FrameLayout mModuleView1, mModuleView2, mModuleView3, mModuleView4, mModuleView5,
                mModuleView6, Onk;

        public ContentVericalViewHolder(View itemView) {
            super(itemView);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_8_title);
            mModuleView1 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view1);
            mModuleView2 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view2);
            mModuleView3 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view3);
            mModuleView4 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view4);
            mModuleView5 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view5);
            mModuleView6 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view6);
            viewList = new ArrayList<>();
            viewList.add(mModuleView1);
            viewList.add(mModuleView2);
            viewList.add(mModuleView3);
            viewList.add(mModuleView4);
            viewList.add(mModuleView5);
            viewList.add(mModuleView6);
            mModuleView1.setOnFocusChangeListener(this);
            mModuleView2.setOnFocusChangeListener(this);
            mModuleView3.setOnFocusChangeListener(this);
            mModuleView4.setOnFocusChangeListener(this);
            mModuleView5.setOnFocusChangeListener(this);
            mModuleView6.setOnFocusChangeListener(this);
            mModuleView1.setOnKeyListener(this);
            mModuleView2.setOnKeyListener(this);
            mModuleView3.setOnKeyListener(this);
            mModuleView4.setOnKeyListener(this);
            mModuleView5.setOnKeyListener(this);
            mModuleView6.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            v.findViewWithTag("tag_poster_title").setSelected(hasFocus);
            if (hasFocus) {
                onItemGetFocus(v);
            } else {
                onItemLoseFocus(v);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent
                    .KEYCODE_ENTER) {
                if (listener != null) {
                    int position = -1;
                    switch (v.getId()) {
                        case R.id.id_module_8_view1:
                            position = 0;
                            break;
                        case R.id.id_module_8_view2:
                            position = 1;
                            break;
                        case R.id.id_module_8_view3:
                            position = 2;
                            break;
                        case R.id.id_module_8_view4:
                            position = 3;
                            break;
                        case R.id.id_module_8_view5:
                            position = 4;
                            break;
                        case R.id.id_module_8_view6:
                            position = 5;
                            break;
                    }
                    ProgramSeriesInfo.ProgramsInfo entity = getItem(getAdapterPosition()).getData
                            ().get(position);
                    if (entity != null) {

                        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL_SUGGESt,entity.getContentUUID());
                        if (TextUtils.isEmpty(entity.getActionType())) {
                            JumpUtil.detailsJumpActivity(context, entity.getContentType(), entity
                                    .getContentUUID());
                        } else {
                            JumpUtil.activityJump(context, entity.getActionType(), entity
                                    .getContentType(), entity.getContentUUID(), entity
                                    .getActionUri());
                        }
                    }

                }

                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int size = mList.get(getAdapterPosition()).getData().size();
                if (size >= 6) {
                    if (v.getId() == viewList.get(5).getId()) {
                        return true;
                    }
                } else {
                    if (v.getId() == viewList.get(size - 1).getId()) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    class VerticalSlideHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener {

        private final ImageView titleIconIv;
        private final TextView titleTv;
        private final AiyaRecyclerView aiyaRecyclerView;
        private final DetailsHorizontaVerticallAdapter mAdapter;

        private final ImageView ivRight;
        private final ImageView ivLeft;

        public VerticalSlideHolder(View itemView) {
            super(itemView);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_8_title);
            aiyaRecyclerView = (AiyaRecyclerView) itemView.findViewById(R.id.air_side);
            aiyaRecyclerView.setAlign(AiyaRecyclerView.ALIGN_AUTO_TWO);
            aiyaRecyclerView.setFocusable(false);
            ivRight = (ImageView) itemView.findViewById(R.id.iv_right);
            ivLeft = (ImageView) itemView.findViewById(R.id.iv_left);
            mAdapter = new DetailsHorizontaVerticallAdapter(context, mSpringInterpolator,
                    aiyaRecyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false);
            aiyaRecyclerView.setLayoutManager(linearLayoutManager);
            aiyaRecyclerView.setAdapter(mAdapter);
            aiyaRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener
                    () {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastPosition = ((LinearLayoutManager) recyclerView
                                .getLayoutManager()).findLastVisibleItemPosition();
                        int firstVisibleItemPosition = ((LinearLayoutManager)
                                recyclerView.getLayoutManager())
                                .findFirstVisibleItemPosition();
                        if (firstVisibleItemPosition == 0) {
                            ivLeft.setVisibility(View.INVISIBLE);
                        } else {
                            ivLeft.setVisibility(View.VISIBLE);
                        }
                        if (lastPosition == mList.get(getAdapterPosition()).getData().size() - 1) {

                            ivRight.setVisibility(View.INVISIBLE);

                        } else {
                            ivRight.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(v);
            } else {
                onItemLoseFocus(v);
            }
        }
    }

    class ContentHorizontalViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener {
        private AiyaRecyclerView mRecycleView;
        private ImageView titleIconIv;
        private TextView titleTv;
        private ImageView ivRight;
        private ImageView ivLeft;
        private DetailsHorizontalAdapter mAdapter;

        public ContentHorizontalViewHolder(View itemView) {
            super(itemView);
            ivLeft = (ImageView) itemView.findViewById(R.id.iv_left);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_8_title);
            mRecycleView = (AiyaRecyclerView) itemView.findViewById(R.id.rv_list);
            mRecycleView.setAlign(AiyaRecyclerView.ALIGN_AUTO_TWO);
            ivRight = (ImageView) itemView.findViewById(R.id.iv_right);
            ivLeft.setFocusable(false);
            ivRight.setFocusable(false);
            mRecycleView.setFocusable(false);
            mAdapter = new DetailsHorizontalAdapter(context, mSpringInterpolator, mRecycleView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false);
            mRecycleView.setLayoutManager(linearLayoutManager);
            mRecycleView.setAdapter(mAdapter);
            mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener
                    () {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        int lastPosition = ((LinearLayoutManager) recyclerView
                                .getLayoutManager()).findLastVisibleItemPosition();
                        int firstVisibleItemPosition = ((LinearLayoutManager)
                                recyclerView.getLayoutManager())
                                .findFirstVisibleItemPosition();
                        if (firstVisibleItemPosition == 0) {
                            ivLeft.setVisibility(View.INVISIBLE);
                        } else {
                            ivLeft.setVisibility(View.VISIBLE);
                        }
                        if (lastPosition == mList.get(getAdapterPosition()).getData().size() - 1) {

                            ivRight.setVisibility(View.INVISIBLE);

                        } else {
                            ivRight.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(v);
            } else {
                onItemLoseFocus(v);
            }
        }
    }

    class PlayListViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener, View.OnKeyListener {
        private ImageView titleIconIv;
        private TextView titleTv;
        private RecyclerView mBtnEpisodeView;
        private FrameLayout mModuleView1, mModuleView2, mModuleView3, mModuleView4, mModuleView5,
                mModuleView6, mModuleView7, mModuleView8;
        private List<FrameLayout> viewList;
        private PageHelper<ProgramSeriesInfo.ProgramsInfo> mPageDaoImpl;
        private PlayerSelectPageAdapter mAdapter;
        private List<ProgramSeriesInfo.ProgramsInfo> dataList;
        private ImageView mRightIv;
        private ImageView mLeftIv;
        private LinearLayoutManager pageLayoutMananger;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_8_title);
            mModuleView1 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view1);
            mModuleView2 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view2);
            mModuleView3 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view3);
            mModuleView4 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view4);
            mModuleView5 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view5);
            mModuleView6 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view6);
            mModuleView7 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view7);
            mModuleView8 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view8);
            mBtnEpisodeView = (RecyclerView) itemView.findViewById(R.id.rv_episode_view);
            mRightIv = (ImageView) itemView.findViewById(R.id.iv_right);
            mLeftIv = (ImageView) itemView.findViewById(R.id.iv_left);
            viewList = new ArrayList<>();
            dataList = new ArrayList<>();
            pageLayoutMananger = new LinearLayoutManager(context, LinearLayoutManager
                    .HORIZONTAL, false);
            mBtnEpisodeView.setLayoutManager(pageLayoutMananger);
//            mBtnEpisodeView.setAlign(AiyaRecyclerView.ALIGN_CENTER);
            viewList.add(mModuleView1);
            viewList.add(mModuleView2);
            viewList.add(mModuleView3);
            viewList.add(mModuleView4);
            viewList.add(mModuleView5);
            viewList.add(mModuleView6);
            viewList.add(mModuleView7);
            viewList.add(mModuleView8);
            mModuleView1.setOnFocusChangeListener(this);
            mModuleView2.setOnFocusChangeListener(this);
            mModuleView3.setOnFocusChangeListener(this);
            mModuleView4.setOnFocusChangeListener(this);
            mModuleView5.setOnFocusChangeListener(this);
            mModuleView6.setOnFocusChangeListener(this);
            mModuleView7.setOnFocusChangeListener(this);
            mModuleView8.setOnFocusChangeListener(this);
            mModuleView1.setOnKeyListener(this);
            mModuleView2.setOnKeyListener(this);
            mModuleView3.setOnKeyListener(this);
            mModuleView4.setOnKeyListener(this);
            mModuleView5.setOnKeyListener(this);
            mModuleView6.setOnKeyListener(this);
            mModuleView7.setOnKeyListener(this);
            mModuleView8.setOnKeyListener(this);
        }

        private void scroolPageToPosition(int position) {
            View view = null;
            int first = pageLayoutMananger.findFirstVisibleItemPosition();
            int last = pageLayoutMananger.findLastVisibleItemPosition();
            if (position == first) {
                view = mBtnEpisodeView.getChildAt(0);
            } else if (position > first && position <= last) {
                int index = position - first;
                view = mBtnEpisodeView.getChildAt(index);
            } else {
                mBtnEpisodeView.scrollToPosition(position);
                view = mBtnEpisodeView.getChildAt(0);
            }

            if (view != null) {
//                ((PlayerSelectPageAdapter) mBtnEpisodeView.getAdapter()).setSelectIndex
//                        (position);
//                mBtnEpisodeView.setFocusView(view);
            }

        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return false;
            }

            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (v.getId() == R.id.id_module_8_view1) {
                    defultFocusPosition = 3;
                } else if (v.getId() == R.id.id_module_8_view5) {
                    defultFocusPosition = 7;
                } else {
                    return false;
                }
                if (mPageDaoImpl.hasPrePage()) {
                    mPageDaoImpl.prePage();
                    dataList.clear();
                    dataList.addAll(mPageDaoImpl.currentList());
                    notifyDataSetChanged();
                    scroolPageToPosition(mPageDaoImpl.getCurrentPage() - 1);
                }
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (v.getId() == R.id.id_module_8_view4) {
                    defultFocusPosition = 0;
                } else if (v.getId() == R.id.id_module_8_view8) {
                    defultFocusPosition = 4;
                } else {
                    if (v.getId() == viewList.get(mPageDaoImpl.currentList().size() - 1).getId()) {
                        return true;
                    }
                    return false;
                }
                if (mPageDaoImpl.hasNextPage()) {
                    mPageDaoImpl.nextPage();
                    dataList.clear();
                    dataList.addAll(mPageDaoImpl.currentList());
                    notifyDataSetChanged();
                    scroolPageToPosition(mPageDaoImpl.getCurrentPage() - 1);
                }
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                int position;
                if (v.getId() == R.id.id_module_8_view1) {
                    position = 0;
                } else if (v.getId() == R.id.id_module_8_view2) {
                    position = 1;
                } else if (v.getId() == R.id.id_module_8_view3) {
                    position = 2;
                } else if (v.getId() == R.id.id_module_8_view4) {
                    position = 3;
                } else if (v.getId() == R.id.id_module_8_view5) {
                    position = 4;
                } else if (v.getId() == R.id.id_module_8_view6) {
                    position = 5;
                } else if (v.getId() == R.id.id_module_8_view7) {
                    position = 6;
                } else if (v.getId() == R.id.id_module_8_view8) {
                    position = 7;
                } else {
                    return false;
                }

                ProgramSeriesInfo.ProgramsInfo programInfo = mPageDaoImpl
                        .currentList().get(position);
                currentPlayUUID = programInfo.getContentUUID();
                defultFocusPosition = position;
                int currentPosition = mPageDaoImpl.getCurrentPosition(position);

                if (TextUtils.isEmpty(dataList.get(0).getLiveUrl())) {
                    if (listener != null && listener.get() != null)
                        listener.get().onItemClick(v, currentPosition, programInfo);
                } else {
                    if (listener != null && listener.get() != null)
                        listener.get().onItemClick(v, currentPosition - 1, programInfo);
                }

                mPageDaoImpl.setCurrentPage(mPageDaoImpl.getCurrentPage(currentPosition));
                mAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (itemView.findFocus().getId() == R.id.id_module_8_view5
                        || itemView.findFocus().getId() == R.id.id_module_8_view6
                        || itemView.findFocus().getId() == R.id.id_module_8_view7
                        || itemView.findFocus().getId() == R.id.id_module_8_view8) {
//                    mBtnEpisodeView.getDefaultFocusView().requestFocus();
                    return true;
                }
            }


            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            View view = v.findViewWithTag("tag_poster_title");

            if (view!=null){
                view.setSelected(hasFocus);
            }

            if (hasFocus) {
                onItemGetFocus(v);
            } else {
                onItemLoseFocus(v);
            }
            if (mPageDaoImpl.hasNextPage()) {
                mRightIv.setVisibility(View.VISIBLE);
            } else {
                mRightIv.setVisibility(View.GONE);
            }
            if (mPageDaoImpl.hasPrePage()) {
                mLeftIv.setVisibility(View.VISIBLE);
            } else {
                mLeftIv.setVisibility(View.GONE);
            }
        }
    }
}
