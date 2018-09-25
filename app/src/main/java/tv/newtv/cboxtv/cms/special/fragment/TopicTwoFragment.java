package tv.newtv.cboxtv.cms.special.fragment;

import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.cms.special.ScrollTextView;
import tv.newtv.cboxtv.player.popupMenuWidget;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.PlayInfoUtil;

public class TopicTwoFragment extends BaseSpecialContentFragment implements PlayerCallback {
    private ModuleInfoResult moduleInfoResult;
    private ProgramSeriesInfo mProgramSeriesInfo;
    private int videoIndex = 0;
    private int playIndex = 0;
    private AiyaRecyclerView news_recycle;
    private TextView title;
    private TextView title_direction;
    private FrameLayout video_player_rl;
    private FrameLayout frame_container;
    private View focusView;
    private TextView videoTitle;
    private ImageView full_screen;
    private popupMenuWidget mPopupMenuWidget;
    private int widgetId = 0;
    private String defaultFocusId;
    private int defaultFocusIndex = -1;
    private int isFirstEnter = 0;
    private boolean hasDefaultFocus;


    @Override
    public void onResume() {
        super.onResume();

        /* 界面还原回来时候，重新注册控件 */
        if (videoPlayerView != null && mPopupMenuWidget != null) {
            widgetId = videoPlayerView.registerWidget(widgetId, mPopupMenuWidget);
        }
    }

    @Override
    public void onStop() {
        /* 销毁播放器的时候将注册的控件消除 */
        if (videoPlayerView != null) {
            videoPlayerView.unregisterWidget(widgetId);
        }
        super.onStop();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.topic_two_layout;
    }

    @Override
    protected void setUpUI(final View view) {
        news_recycle = view.findViewById(R.id.news_recycle);
        frame_container = view.findViewById(R.id.frame_container);
        title = view.findViewById(R.id.title);
        title_direction = view.findViewById(R.id.title_direction);
        videoPlayerView = view.findViewById(R.id.video_player);
        mPopupMenuWidget = new popupMenuWidget(getContext().getApplicationContext(), news_recycle);
        widgetId = videoPlayerView.registerWidget(widgetId, mPopupMenuWidget);
        video_player_rl = view.findViewById(R.id.video_player_rl);
        videoTitle = view.findViewById(R.id.videoTitle);
        full_screen = view.findViewById(R.id.full_screen);
        news_recycle.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen.width_54px);
        news_recycle.setSpace(space, 0);
        final NewsAdapter adapter = new NewsAdapter();
        news_recycle.setItemAnimator(null);
        news_recycle.setAlign(AiyaRecyclerView.ALIGN_START);
        news_recycle.setAdapter(adapter);


        adapter.setOnItemAction(new OnItemAction<ProgramInfo>() {
            @Override
            public void onItemFocus(View item) {


            }

            @Override
            public void onItemClick(ProgramInfo item, int index) {
                videoIndex = index;
                if (defaultFocusId != null && defaultFocusIndex != -1) {
                    smoothMoveToPosition(news_recycle, defaultFocusIndex);
                    firstPlay(defaultFocusId, defaultFocusIndex);

                } else {
                    if (isFirstEnter == 1) {
                        NewsViewHolder viewHolder = (NewsViewHolder) news_recycle.findViewHolderForAdapterPosition(defaultFocusIndex);
                        viewHolder.isPlaying.setVisibility(View.GONE);
                        viewHolder.relative_container.setBackgroundResource(0);
                        defaultFocusIndex = -1;
                        isFirstEnter = 0;
                    }
                    onItemClickAction(item);
                }
                adapter.setThisPosition(index);
                videoPlayerView.setisPlayingView(adapter.getImageView());

            }

            @Override
            public void onItemChange(final int before, final int current) {
                if (news_recycle != null) {
                    MainLooper.get().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            news_recycle.getAdapter().notifyItemChanged(before);
                            news_recycle.getAdapter().notifyItemChanged(current);
                        }
                    }, 300);
                }
            }
        });

        videoPlayerView.setPlayerCallback(this);
        videoPlayerView.setFocusView(view.findViewById(R.id.video_player_rl), true);
        if (moduleInfoResult != null) {
            if (moduleInfoResult.getDescription().length() > 30) {
                title_direction.setText(moduleInfoResult.getDescription().substring(0, 30));
            } else {
                title_direction.setText(moduleInfoResult.getDescription());
            }

            adapter.refreshData(moduleInfoResult.getDatas().get(0).getDatas())
                    .notifyDataSetChanged();
        }

        news_recycle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) news_recycle.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (defaultFocusIndex <= lastVisibleItemPosition) {
                    news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition).setFocusable(true);
                    news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition).requestFocus();
                    defaultFocusId = null;
                    isFirstEnter = 1;

                } else {
                    if (hasDefaultFocus) {
                        news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition).setFocusable(true);
                        news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition).requestFocus();
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    news_recycle.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        news_recycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(recyclerView, mToPosition);
                    hasDefaultFocus = true;
                }
            }
        });


    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }

        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    @Override
    public void setModuleInfo(ModuleInfoResult infoResult) {
        if (infoResult.getPageBackground()==null){
            frame_container.setBackgroundResource(R.drawable.bg);
        }

        moduleInfoResult = infoResult;
        Log.e("TopicTwoFragmentaaaa", infoResult.getPageBackground());

        List<ProgramInfo> datas = moduleInfoResult.getDatas().get(0).getDatas();

        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getDefaultFocus() == 1) {
                defaultFocusId = datas.get(i).getContentUUID();
                defaultFocusIndex = i;
            }
        }
        Log.d("TopicTwoFragment", "defaultFocusIndex:" + defaultFocusIndex);

        if (news_recycle != null && news_recycle.getAdapter() != null) {
            ((NewsAdapter) news_recycle.getAdapter()).refreshData(infoResult.getDatas().get(0)
                    .getDatas()).notifyDataSetChanged();


        }
    }

    private void getDrawable(String url) {
//        NetClient.INSTANCE.getDownLoadImageApi()


    }

    private void onItemClickAction(ProgramInfo programInfo) {

        videoPlayerView.beginChange();

        PlayInfoUtil.getPlayInfo(programInfo.getContentUUID(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(ProgramSeriesInfo info) {
                if (info != null) {
                    mProgramSeriesInfo = info;
                    Log.e("info", info.toString());
                    if (videoPlayerView != null) {
                        videoPlayerView.setSeriesInfo(info);
                        videoPlayerView.playSingleOrSeries(0, 0);
                    }
                } else {
                    if (videoPlayerView != null) {
                        videoPlayerView.showProgramError();
                    }
                }
            }
        });
    }

    private void firstPlay(String id, final int index) {

        videoPlayerView.beginChange();

        PlayInfoUtil.getPlayInfo(id, new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(ProgramSeriesInfo info) {
                if (info != null) {
                    mProgramSeriesInfo = info;
                    Log.e("info", info.toString());
                    if (videoPlayerView != null) {
                        videoPlayerView.setSeriesInfo(info);
                        videoPlayerView.playSingleOrSeries(index, 0);
                    }
                } else {
                    if (videoPlayerView != null) {
                        videoPlayerView.showProgramError();
                    }
                }
            }
        });

    }

    @Override
    public void onEpisodeChange(int index, int position) {
        playIndex = index;
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoTitle.setVisibility(View.GONE);
        full_screen.setVisibility(View.GONE);
        videoPlayerView.EnterFullScreen(getActivity(), false);
        videoPlayerView.setView(videoTitle,full_screen);

    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
        if (news_recycle.getAdapter() != null) {
            NewsAdapter adapter = (NewsAdapter) news_recycle.getAdapter();
            LinearLayoutManager layoutManager = (LinearLayoutManager) news_recycle
                    .getLayoutManager();
            videoIndex++;
            if (adapter.getItemCount() - 1 < videoIndex) {
                return;
            }
            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();
            int postion = 0;
            if (videoIndex == first) {
                postion = 0;
            } else if (videoIndex > first && videoIndex <= last) {
                postion = videoIndex - first;
            }
            View view = news_recycle.getChildAt(postion);
            NewsViewHolder viewHolder = (NewsViewHolder) news_recycle.getChildViewHolder
                    (view);
            if (viewHolder != null) {
                viewHolder.dispatchSelect();
                viewHolder.isPlaying.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public void ProgramChange() {
        videoPlayerView.setSeriesInfo(mProgramSeriesInfo);
        videoPlayerView.playSingleOrSeries(playIndex, 0);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getContentView() != null) {
                focusView = getContentView().findFocus();
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (focusView instanceof VideoPlayerView) {
                    return true;
                }
                if (videoPlayerView != null) {
                    videoPlayerView.requestFocus();
                    videoTitle.setVisibility(View.VISIBLE);
                    full_screen.setVisibility(View.VISIBLE);

                }
                return true;
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (focusView instanceof VideoPlayerView) {
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (focusView instanceof VideoPlayerView) {
                    news_recycle.getDefaultFocusView().requestFocus();
                    videoTitle.setVisibility(View.GONE);
                    full_screen.setVisibility(View.GONE);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public ScrollTextView news_title;
        public ImageView isPlaying;
        public RelativeLayout relative_fou;
        public RelativeLayout relative_container;


        public NewsViewHolder(View itemView) {
            super(itemView);
            news_title = itemView.findViewById(R.id.news_title);
            isPlaying = itemView.findViewById(R.id.isPlaying);
            relative_fou = itemView.findViewById(R.id.relative_fou);
            relative_container = itemView.findViewById(R.id.relative_container);
        }

        public void dispatchSelect() {
            itemView.requestFocus();
            itemView.performClick();
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

        private List<ProgramInfo> ModuleItems;
        private OnItemAction<ProgramInfo> onItemAction;
        private String currentUUID;
        private int currentIndex = 0;
        private int thisPosition;
        private ImageView imageView;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public int getthisPosition() {
            return thisPosition;
        }

        public void setThisPosition(int thisPosition) {
            this.thisPosition = thisPosition;
        }

        NewsAdapter refreshData(List<ProgramInfo> datas) {
            ModuleItems = datas;
            return this;
        }

        public void setOnItemAction(OnItemAction<ProgramInfo> programInfoOnItemAction) {
            onItemAction = programInfoOnItemAction;
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .news_item_layout, parent, false);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view
                    .getLayoutParams();
            int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen
                    .height_18px) * -1;
            layoutParams.topMargin = space;
            view.setLayoutParams(layoutParams);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final NewsViewHolder holder, final int position) {


            ProgramInfo moduleItem = getItem(position);
            setImageView(holder.isPlaying);

            if (position == getthisPosition()) {

                holder.relative_container.setBackgroundResource(R.drawable.topic_foucs);
            } else {
                holder.relative_container.setBackgroundResource(0);
            }

            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {

                    if (hasFocus) {
                        if (holder.news_title.getText().length() > 10) {
                            holder.news_title.startFor0();
                        }


                        holder.relative_fou.setBackgroundResource(R.drawable.topic_foucs);
                        if (position == getthisPosition()) {
                            holder.relative_container.setBackgroundResource(0);
                        }
                    } else {
                        if (position == getthisPosition()) {
                            holder.relative_container.setBackgroundResource(R.drawable.topic_background);
                        }
                        holder.relative_fou.setBackgroundResource(0);
                        holder.news_title.stopScroll();

                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (defaultFocusId != null && defaultFocusIndex != -1) {
                        ProgramInfo programInfo = getItem(defaultFocusIndex);
                        if (programInfo != null) {
                            if (programInfo.getSubTitle().length() > 15) {
                                title.setText(programInfo.getSubTitle().substring(0, 15));
                            } else {
                                title.setText(programInfo.getSubTitle());
                            }
                            if (programInfo.getSubTitle().length() > 30) {
                                videoTitle.setText(programInfo.getSubTitle().substring(0, 30));
                            } else {
                                videoTitle.setText(programInfo.getSubTitle());
                            }
                            currentUUID = programInfo.getContentUUID();
                            onItemAction.onItemChange(currentIndex, holder.getAdapterPosition());
                            currentIndex = holder.getAdapterPosition();
                            onItemAction.onItemClick(programInfo, defaultFocusIndex);
                            holder.isPlaying.setVisibility(View.VISIBLE);
                        }

                    } else {
                        if (position != defaultFocusIndex) {
                            holder.isPlaying.setVisibility(View.GONE);
                            holder.relative_container.setBackgroundResource(0);
                            holder.relative_fou.setBackgroundResource(0);

                        }
                        final ProgramInfo moduleItem = getItem(holder.getAdapterPosition());
                        if (moduleItem != null) {
                            if (moduleItem.getSubTitle().length() > 15) {
                                title.setText(moduleItem.getSubTitle().substring(0, 15));
                            } else {
                                title.setText(moduleItem.getSubTitle());
                            }
                            if (moduleItem.getSubTitle().length() > 30) {
                                videoTitle.setText(moduleItem.getSubTitle().substring(0, 30));
                            } else {
                                videoTitle.setText(moduleItem.getSubTitle());
                            }
                            currentUUID = moduleItem.getContentUUID();
                            onItemAction.onItemChange(currentIndex, holder.getAdapterPosition());
                            currentIndex = holder.getAdapterPosition();
                            onItemAction.onItemClick(moduleItem, holder.getAdapterPosition());
                            holder.isPlaying.setVisibility(View.VISIBLE);
                        }
                    }

                }
            });
            if (moduleItem != null) {
                if (moduleItem.getSubTitle().length() > 15) {
                    String s = moduleItem.getSubTitle().substring(0, 15);
                    holder.news_title.setText(s);
                } else {
                    holder.news_title.setText(moduleItem.getSubTitle());
                }
                if (moduleItem.getContentUUID().equals(currentUUID)) {
                    holder.isPlaying.setVisibility(View.VISIBLE);
                } else {
                    holder.isPlaying.setVisibility(View.GONE);
                }

                if (TextUtils.isEmpty(currentUUID)) {
                    if (position == 0) {
                        holder.dispatchSelect();
                    }
                }
            }
        }

        private ProgramInfo getItem(int position) {
            if (ModuleItems == null || position < 0 || ModuleItems.size() <= position) {
                return null;
            }
            return ModuleItems.get(position);
        }

        @Override
        public int getItemCount() {
            return ModuleItems != null ? ModuleItems.size() : 0;
        }
    }


}
