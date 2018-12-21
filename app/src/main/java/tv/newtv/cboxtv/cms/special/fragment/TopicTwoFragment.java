package tv.newtv.cboxtv.cms.special.fragment;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.Constant;
import com.newtv.libs.util.RxBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.popupMenuWidget;

public class TopicTwoFragment extends BaseSpecialContentFragment implements PlayerCallback {
    private static final String TAG = TopicTwoFragment.class.getSimpleName();
    private ModelResult<ArrayList<Page>> moduleInfoResult;
    private Content mProgramSeriesInfo;
    private int videoIndex = 0;
    private int playIndex = 0;
    private AiyaRecyclerView news_recycle;
    private TextView subTitle;
    private TextView title_direction;
    private FrameLayout video_player_rl;
    private FrameLayout frame_container;
    private TextView videoTitle;
    private ImageView full_screen;
    private popupMenuWidget mPopupMenuWidget;
    private int widgetId = 0;
    private String defaultFocusId;
    private int defaultFocusIndex = -1;
    private int isFirstEnter = 0;
    private boolean hasDefaultFocus;
    private List<Program> datas;
    private ImageView down_arrow, up_arrow;
    private int firstplayIndex = -1;

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;
    private Observable<Boolean> isHaveAdObservable;
    private boolean isHaveAD = false;
    private int focusPosition;
    private View focusView;

    public static boolean isBottom(AiyaRecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int state = recyclerView.getScrollState();
        View childAt = recyclerView.getChildAt(lastVisibleItemPosition - firstVisibleItemPosition);
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && childAt !=
                null && childAt.hasFocus()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        /* 界面还原回来时候，重新注册控件 */
//        if (videoPlayerView != null && mPopupMenuWidget != null) {
//            widgetId = videoPlayerView.registerWidget(widgetId, mPopupMenuWidget);
//        }
    }

    @Override
    public void onStop() {
        /* 销毁播放器的时候将注册的控件消除 */
//        if (videoPlayerView != null) {
//            videoPlayerView.unregisterWidget(widgetId);
//        }
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        RxBus.get().unregister(Constant.IS_HAVE_AD, isHaveAdObservable);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.topic_two_layout;
    }

    @Override
    protected void onItemContentResult(String uuid, Content info, int playIndex) {
        if (info != null) {
            mProgramSeriesInfo = info;
            Log.e("info", info.toString());
            if (videoPlayerView != null) {
                videoPlayerView.setSeriesInfo(info);
                videoPlayerView.playSingleOrSeries(playIndex, 0);
            }
        } else {
            if (videoPlayerView != null) {
                videoPlayerView.showProgramError();
            }
        }
    }

    @Override
    protected void setUpUI(final View view) {
        news_recycle = view.findViewById(R.id.news_recycle);
        frame_container = view.findViewById(R.id.frame_container);
        subTitle = view.findViewById(R.id.title);
        title_direction = view.findViewById(R.id.title_direction);
        videoPlayerView = view.findViewById(R.id.video_player);
        videoPlayerView.outerControl();
        setVideoPlayerVisibility();
//        mPopupMenuWidget = new popupMenuWidget(view.getContext().getApplicationContext(),
//                news_recycle, Gravity.LEFT, new popupMenuWidget.IPopupWidget() {
//            @Override
//            public KeyAction[] getRegisterKeyActions() {
//                return new KeyAction[]{
//                        new KeyAction(KeyEvent.KEYCODE_MENU, KeyEvent.ACTION_DOWN),
//                        new KeyAction(KeyEvent.KEYCODE_DPAD_UP, KeyEvent.ACTION_DOWN),
//                        new KeyAction(KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.ACTION_DOWN),
//                };
//            }
//        });
//        widgetId = videoPlayerView.registerWidget(widgetId, mPopupMenuWidget);
        video_player_rl = view.findViewById(R.id.video_player_rl);
        videoTitle = view.findViewById(R.id.videoTitle);
        full_screen = view.findViewById(R.id.full_screen);
        down_arrow = view.findViewById(R.id.down_arrow);
        up_arrow = view.findViewById(R.id.up_arrow);
        news_recycle.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        news_recycle.setDirIndicator(up_arrow, down_arrow);
        int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen.height_24px);
        news_recycle.setSpace(space, 0);
        final NewsAdapter adapter = new NewsAdapter();
        news_recycle.setItemAnimator(null);
        news_recycle.setAlign(AiyaRecyclerView.ALIGN_START);
        news_recycle.setAdapter(adapter);

        adapter.setOnItemAction(new OnItemAction<Program>() {
            @Override
            public void onItemFocus(View item) {

            }

            @Override
            public void onItemClick(Program item, int index) {
                videoIndex = index;
                if (index + 1 == datas.size()) {
                    videoPlayerView.setisEnd(true);
                }
                if (defaultFocusId != null && defaultFocusIndex != -1) {
                    smoothMoveToPosition(news_recycle, defaultFocusIndex);
                    firstPlay(defaultFocusId, defaultFocusIndex);
                } else {
                    if (isFirstEnter == 1) {
                        NewsViewHolder viewHolder = (NewsViewHolder) news_recycle
                                .findViewHolderForAdapterPosition(defaultFocusIndex);
                        if (viewHolder != null) {
                            viewHolder.isPlaying.setVisibility(View.GONE);
                            viewHolder.relative_container.setBackgroundResource(0);
                        }

                        defaultFocusIndex = -1;
                        isFirstEnter = 0;
                    }
                    onItemClickAction(item);
                }
                adapter.setThisPosition(index);
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
            if (!TextUtils.isEmpty(moduleInfoResult.getDescription())) {
                if (moduleInfoResult.getDescription().length() >= 30) {
                    title_direction.setText(moduleInfoResult.getDescription().substring(0, 30));
                } else {
                    title_direction.setText(moduleInfoResult.getDescription());
                }
            }

            if (!TextUtils.isEmpty(moduleInfoResult.getSubTitle())) {
                if (moduleInfoResult.getSubTitle().length() >= 30) {
                    subTitle.setText(moduleInfoResult.getSubTitle().substring(0, 30));
                } else {
                    subTitle.setText(moduleInfoResult.getSubTitle());
                }
            }
            adapter.refreshData(moduleInfoResult.getData().get(0).getPrograms())
                    .notifyDataSetChanged();
        }

        news_recycle.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) news_recycle
                        .getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (defaultFocusIndex >= firstVisibleItemPosition && defaultFocusIndex <=
                        lastVisibleItemPosition) {
                    if (news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition) !=
                            null) {
                        news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition)
                                .setFocusable(true);
                        news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition)
                                .requestFocus();
                    }

                    defaultFocusId = null;
                    isFirstEnter = 1;

                } else {
                    if (hasDefaultFocus) {
                        if (news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition)
                                != null) {
                            news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition)
                                    .setFocusable(true);
                            news_recycle.getChildAt(defaultFocusIndex - firstVisibleItemPosition)
                                    .requestFocus();
                        }

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

    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt
                (mRecyclerView.getChildCount() - 1));
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

    private void onItemClickAction(Program programInfo) {

        videoPlayerView.beginChange();
        getContent(programInfo.getL_id(), programInfo);
    }

    private void firstPlay(String id, final int index) {
        videoPlayerView.beginChange();
        firstplayIndex = index;
        getContent(id);
    }

    @Override
    public void onEpisodeChange(int index, int position) {
        playIndex = index;

//
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoTitle.setVisibility(View.GONE);
        full_screen.setVisibility(View.GONE);
        videoPlayerView.EnterFullScreen(getActivity(), false);
        videoPlayerView.setView(videoTitle, full_screen);


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
            View focusView = null;
            if (getContentView() != null) {
                focusView = getContentView().findFocus();
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (focusView instanceof VideoPlayerView) {
                    return true;
                }
                if (videoPlayerView != null) {
                    videoPlayerView.requestFocus();
                    setVideoTitleVisibility();
//                    full_screen.setVisibility(View.VISIBLE);
                    full_screen.setVisibility(View.GONE);

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
                    if (isPositionShow(news_recycle, focusPosition)) {
                        news_recycle.getDefaultFocusView().requestFocus();
                    } else {
                        news_recycle.scrollToPosition(focusPosition);
                        MainLooper.get().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getFocusView().requestFocus();
                            }
                        }, 100);
                    }
                    videoTitle.setVisibility(View.GONE);
                    full_screen.setVisibility(View.GONE);
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public View getFocusView() {
        return focusView;
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        Log.d("TopicTwoFragment", infoResult.getData().toString());
        if (infoResult.getBackground() == null) {
            frame_container.setBackgroundResource(R.drawable.bg);
        }
        moduleInfoResult = infoResult;


        datas = moduleInfoResult.getData().get(0).getPrograms();
        Log.d("TopicTwoFragment", "datas.size():" + datas.size());

        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).getDefaultFocus() == 1) {
                defaultFocusId = datas.get(i).getContentId();
                defaultFocusIndex = i;
            }
        }
        if (news_recycle != null && news_recycle.getAdapter() != null) {
            ((NewsAdapter) news_recycle.getAdapter()).refreshData(infoResult.getData().get(0)
                    .getPrograms()).notifyDataSetChanged();


        }
    }

    @SuppressLint("CheckResult")
    private void setVideoPlayerVisibility() {
        if (null != videoPlayerView) {
            isHaveAdObservable = RxBus.get().register(Constant.IS_HAVE_AD);
            isHaveAdObservable.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isHaveAd) {
                            Log.d(TAG, "setVideoPlayerVisibility isHaveAd : " + isHaveAd);
                            isHaveAD = isHaveAd;
                            setVideoTitleVisibility();
                        }
                    });
        }
    }

    private void setVideoTitleVisibility() {
        if (videoPlayerView != null && videoTitle != null) {
            if (isHaveAD) {
                videoTitle.setVisibility(View.GONE);
            } else {
                videoTitle.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isPositionShow(RecyclerView recyclerView, int position) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

            if (firstVisibleItemPosition <= position && position <= lastVisibleItemPosition) {
                return true;
            }
        }
        return false;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView news_title;
        public ImageView isPlaying;
        public RelativeLayout relative_fou;
        public RelativeLayout relative_container;


        NewsViewHolder(View itemView) {
            super(itemView);
            news_title = itemView.findViewById(R.id.news_title);
            isPlaying = itemView.findViewById(R.id.isPlaying);
            relative_fou = itemView.findViewById(R.id.relative_fou);
            relative_container = itemView.findViewById(R.id.relative_container);
        }

        void dispatchSelect() {
            itemView.requestFocus();
            itemView.performClick();
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

        private List<Program> ModuleItems;
        private OnItemAction<Program> onItemAction;
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

        NewsAdapter refreshData(List<Program> datas) {
            Log.d("NewsAdapter", "datas.size():" + datas.size());
            ModuleItems = datas;
            return this;
        }

        public void setOnItemAction(OnItemAction<Program> programInfoOnItemAction) {
            onItemAction = programInfoOnItemAction;
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .news_item_layout, parent, false);

            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final NewsViewHolder holder, final int position) {
            if (focusPosition == position) {
                focusView = holder.itemView;
            }

            final Program moduleItem = getItem(position);
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
                        TopicTwoFragment.this.focusPosition = position;

//                        if (isBottom(news_recycle)) {
//                            down_arrow.setVisibility(View.VISIBLE);
//                        } else {
//                            down_arrow.setVisibility(View.INVISIBLE);
//                        }
                        if (moduleItem != null && !TextUtils.isEmpty(moduleItem.getSubTitle()) &&
                                moduleItem.getSubTitle().length() > 10) {

                            holder.news_title.setSingleLine(true);
                            holder.news_title.setText(moduleItem.getSubTitle());
                            holder.news_title.setSelected(true);
                        }
                        holder.relative_fou.setBackgroundResource(R.drawable.topic_foucs);
                        if (position == getthisPosition()) {
                            holder.relative_container.setBackgroundResource(0);
                        }
                    } else {
                        if (position == getthisPosition()) {
                            holder.relative_container.setBackgroundResource(R.drawable
                                    .topic_background);
                        }
                        holder.relative_fou.setBackgroundResource(0);
                        holder.news_title.setSelected(false);
                        if (moduleItem != null) {


                            if (!TextUtils.isEmpty(moduleItem.getSubTitle())) {
                                if (moduleItem.getSubTitle().length() > 30) {
                                    holder.news_title.setText(moduleItem.getSubTitle().substring(0,
                                            30));
                                    holder.news_title.setSingleLine(false);
                                    holder.news_title.setMaxLines(2);
                                } else {
                                    holder.news_title.setText(moduleItem.getSubTitle());
                                }
                            }

                        }


                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (defaultFocusId != null && defaultFocusIndex != -1) {
                        Program programInfo = getItem(defaultFocusIndex);
                        if (programInfo != null) {
                            if (!TextUtils.isEmpty(programInfo.getSubTitle())) {
//                                if (programInfo.getSubTitle().length() > 15) {
//                                    title.setText(programInfo.getSubTitle().substring(0, 15));
//                                    title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                                    title.getPaint().setFakeBoldText(true);
//                                } else {
//                                    title.setText(programInfo.getSubTitle());
//                                    title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                                    title.getPaint().setFakeBoldText(true);
//                                }
                                if (programInfo.getSubTitle().length() > 30) {
                                    videoTitle.setText(programInfo.getSubTitle().substring(0, 30));
                                } else {
                                    videoTitle.setText(programInfo.getSubTitle());
                                }
                            }

                            currentUUID = programInfo.getContentId();
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
                        final Program moduleItem = getItem(holder.getAdapterPosition());
                        if (moduleItem != null) {
                            if (!TextUtils.isEmpty(moduleItem.getSubTitle())) {
//                                if (moduleItem.getSubTitle().length() > 15) {
//                                    title.setText(moduleItem.getSubTitle().substring(0, 15));
//                                    title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                                    title.getPaint().setFakeBoldText(true);
//                                } else {
//                                    title.setText(moduleItem.getSubTitle());
//                                    title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//                                    title.getPaint().setFakeBoldText(true);
//                                }
                                if (moduleItem.getSubTitle().length() > 30) {
                                    videoTitle.setText(moduleItem.getSubTitle().substring(0, 30));
                                } else {
                                    videoTitle.setText(moduleItem.getSubTitle());
                                }
                            }

                            currentUUID = moduleItem.getL_id();
                            onItemAction.onItemChange(currentIndex, holder.getAdapterPosition());
                            currentIndex = holder.getAdapterPosition();
                            onItemAction.onItemClick(moduleItem, holder.getAdapterPosition());
                            holder.isPlaying.setVisibility(View.VISIBLE);
                        }
                    }

                }
            });
            if (moduleItem != null) {
                if (holder.itemView.hasFocus() && !TextUtils.isEmpty(moduleItem.getSubTitle())) {
                    holder.news_title.setSingleLine(true);
                    holder.news_title.setText(moduleItem.getSubTitle());
                } else {
                    if (!TextUtils.isEmpty(moduleItem.getSubTitle())) {
                        if (moduleItem.getSubTitle().length() > 30) {
                            holder.news_title.setSingleLine(false);
                            holder.news_title.setMaxLines(2);
                            holder.news_title.setText(moduleItem.getSubTitle().substring(0, 30));
                        } else {
                            holder.news_title.setText(moduleItem.getSubTitle());
                        }
                    }


                }

                if (TextUtils.equals(moduleItem.getL_id(), currentUUID)) {
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

        private Program getItem(int position) {
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
