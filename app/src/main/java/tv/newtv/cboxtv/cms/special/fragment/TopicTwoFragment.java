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

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.special.OnItemAction;
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
    private View focusView;
    private TextView videoTitle;
    private ImageView full_screen;

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
        video_player_rl = view.findViewById(R.id.video_player_rl);
        videoTitle =view.findViewById(R.id.videoTitle);
        full_screen =view.findViewById(R.id.full_screen);

        news_recycle.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.VERTICAL, false));
        int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen.width_54px);
        news_recycle.setSpace(space, 0);
        NewsAdapter adapter = new NewsAdapter();
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
                onItemClickAction(item);

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
            }
        }
    }




    @Override
    public void ProgramChange() {
        videoPlayerView.setSeriesInfo(mProgramSeriesInfo);
        videoPlayerView.playSingleOrSeries(playIndex, 0);
    }


    private class NewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

        private List<ProgramInfo> ModuleItems;
        private OnItemAction<ProgramInfo> onItemAction;
        private String currentUUID;
        private int currentIndex = 0;


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
        public void onBindViewHolder(final NewsViewHolder holder, int position) {
            ProgramInfo moduleItem = getItem(position);


            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {

                    if (hasFocus) {

                        holder.layout.setBackgroundResource(R.drawable.news_foucs);

                    } else {

                        holder.layout.setBackgroundResource(0);

                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgramInfo moduleItem = getItem(holder.getAdapterPosition());
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

    private static class NewsViewHolder extends RecyclerView.ViewHolder {
        public TextView news_title;
        public ImageView isPlaying;
        public RelativeLayout layout;


        public NewsViewHolder(View itemView) {
            super(itemView);
            news_title = itemView.findViewById(R.id.news_title);
            isPlaying = itemView.findViewById(R.id.isPlaying);
            layout = itemView.findViewById(R.id.liner_fou);
        }

        public void dispatchSelect() {
            itemView.requestFocus();
            itemView.performClick();
        }
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
}
