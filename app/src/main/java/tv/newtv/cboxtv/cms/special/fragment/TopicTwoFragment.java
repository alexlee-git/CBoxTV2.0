package tv.newtv.cboxtv.cms.special.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;

import tv.newtv.cboxtv.player.util.PlayInfoUtil;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.player.view.popupMenuWidget;

public class TopicTwoFragment extends BaseSpecialContentFragment implements PlayerCallback {
    private ModuleInfoResult moduleInfoResult;
    private Content mProgramSeriesInfo;
    private int videoIndex = 0;
    private int playIndex = 0;
    private AiyaRecyclerView news_recycle;
    private TextView title;
    private TextView title_direction;
    private FrameLayout video_player_rl;
    private View focusView;
    private TextView videoTitle;
    private ImageView full_screen;
    private popupMenuWidget mPopupMenuWidget;
    private int widgetId = 0;


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
        if(videoPlayerView != null){
            videoPlayerView.unregisterWidget(widgetId);
        }
        super.onStop();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.topic_two_layout;
    }

    @Override
    protected void setUpUI(View view) {
        news_recycle = view.findViewById(R.id.news_recycle);
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
        adapter.setOnItemAction(new OnItemAction<SubContent>() {
            @Override
            public void onItemFocus(View item) {

            }

            @Override
            public void onItemClick( SubContent item, int index) {
                videoIndex = index;
                onItemClickAction(item);
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
            title_direction.setText(moduleInfoResult.getDescription());
            adapter.refreshData(moduleInfoResult.getDatas().get(0).getDatas())
                    .notifyDataSetChanged();
        }
    }

    @Override
    public void setModuleInfo(ModuleInfoResult infoResult) {


        moduleInfoResult = infoResult;
        Log.d("TopicTwoFragment", moduleInfoResult.toString());
        if (news_recycle != null && news_recycle.getAdapter() != null) {
            ((NewsAdapter) news_recycle.getAdapter()).refreshData(infoResult.getDatas().get(0)
                    .getDatas()).notifyDataSetChanged();

        }
    }

    private void onItemClickAction(SubContent programInfo) {

        videoPlayerView.beginChange();
        PlayInfoUtil.getPlayInfo(programInfo.getContentUUID(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(Content info) {
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

    @Override
    public void onEpisodeChange(int index, int position) {
        playIndex = index;
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoPlayerView.EnterFullScreen(getActivity(), false);
        videoTitle.setVisibility(View.GONE);
        full_screen.setVisibility(View.GONE);

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
        public TextView news_title;
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

        private List<SubContent> ModuleItems;
        private OnItemAction<SubContent> onItemAction;
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


        NewsAdapter refreshData(List<SubContent> datas) {
            ModuleItems = datas;
            return this;
        }

        public void setOnItemAction(OnItemAction<SubContent> programInfoOnItemAction) {
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
        public void onBindViewHolder(final NewsViewHolder holder,final int position) {
            SubContent moduleItem = getItem(position);
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
                        holder.relative_fou.setBackgroundResource(R.drawable.topic_foucs);
                        if (position == getthisPosition()) {

                            holder.relative_container.setBackgroundResource(0);
                        }
                    } else {
                        if (position == getthisPosition()) {

                            holder.relative_container.setBackgroundResource(R.drawable.topic_background);
                        }

                        holder.relative_fou.setBackgroundResource(0);

                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final SubContent moduleItem = getItem(holder.getAdapterPosition());
                    if (moduleItem != null) {
                        title.setText(moduleItem.getSubTitle());
                        videoTitle.setText(moduleItem.getSubTitle());
                        currentUUID = moduleItem.getContentUUID();
                        onItemAction.onItemChange(currentIndex, holder.getAdapterPosition());

                        currentIndex = holder.getAdapterPosition();
                        onItemAction.onItemClick(moduleItem, holder.getAdapterPosition());

                        holder.isPlaying.setVisibility(View.VISIBLE);

                    }
                }
            });
            if (moduleItem != null) {
                holder.news_title.setText(moduleItem.getSubTitle());
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

        private SubContent getItem(int position) {
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
