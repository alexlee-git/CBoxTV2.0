package tv.newtv.cboxtv.cms.special.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.PlayInfoUtil;
import tv.newtv.cboxtv.utils.ScaleUtils;
import tv.newtv.cboxtv.views.CurrentPlayImageViewWorldCup;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class QXDFFragment extends BaseSpecialContentFragment implements
        OnItemAction<ProgramInfo>, PlayerCallback {
    private AiyaRecyclerView recyclerView;
    private ModuleInfoResult moduleInfoResult;
    private View downView;
    private int videoIndex = 0;
    private ProgramSeriesInfo mProgramSeriesInfo;

    @Override
    protected int getVideoPlayIndex() {
        return videoIndex;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        downView = null;
        recyclerView = null;
        moduleInfoResult = null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_qxdf_layout;
    }

    @Override
    protected void setUpUI(View view) {
        recyclerView = view.findViewById(R.id.shooter_recycle);
        downView = view.findViewById(R.id.shooter_down);
        downView.setVisibility(View.GONE);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAlign(AiyaRecyclerView.ALIGN_CENTER);
        recyclerView.setDirIndicator(null, downView);
        int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen.width_10px);
        recyclerView.setSpace(0, space * -1);
        ShooterAdapter adapter = new ShooterAdapter();
        adapter.setOnItemAction(this);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);

        videoPlayerView = view.findViewById(R.id.video_player);
        videoPlayerView.setFocusView(view.findViewById(R.id.video_player_focus), true);
        videoPlayerView.setPlayerCallback(this);

        if (moduleInfoResult != null) {
            adapter.refreshData(moduleInfoResult.getDatas().get(0).getDatas())
                    .notifyDataSetChanged();
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(getContentView() == null) return true;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            View focusView = getContentView().findFocus();
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (recyclerView.getDefaultFocusView() != null) {
                    if (recyclerView.getDefaultFocusView().hasFocus()) {
                        View view = FocusFinder.getInstance().findNextFocus(recyclerView, recyclerView
                                .getDefaultFocusView(), View.FOCUS_LEFT);
                        if (view == null) {
                            videoPlayerView.requestFocus();
                            return true;
                        }
                    }
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (focusView != null && focusView.getId() == R.id.video_player) {
                    if (recyclerView.getDefaultFocusView() != null) {
                        recyclerView.getDefaultFocusView().requestFocus();
                        return true;
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setModuleInfo(ModuleInfoResult infoResult) {
        moduleInfoResult = infoResult;
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            ((ShooterAdapter) recyclerView.getAdapter()).refreshData(infoResult.getDatas().get(0)
                    .getDatas()).notifyDataSetChanged();


        }
    }

    @Override
    public void onItemFocus(View item) {

    }

    @Override
    public void onItemClick(ProgramInfo item, final int index) {
        videoPlayerView.beginChange();
        PlayInfoUtil.getPlayInfo(item.getContentUUID(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(ProgramSeriesInfo info) {
                mProgramSeriesInfo = info;
                if (info != null) {
                    Log.e("info", info.toString());
                    videoPlayerView.setSeriesInfo(info);
                    videoPlayerView.playSingleOrSeries(0, 0);
                } else {
                    videoPlayerView.showProgramError();
                }
            }
        });
    }

    @Override
    public void onItemChange(final int before, final int current) {
        MainLooper.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.getAdapter().notifyItemChanged(before);
                recyclerView.getAdapter().notifyItemChanged(current);
            }
        }, 300);
    }

    @Override
    public void onEpisodeChange(int index, int position) {
        videoIndex = index;
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoPlayerView.EnterFullScreen(getActivity(), false);
    }

    @Override
    public void AllPalyComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
        if (recyclerView.getAdapter() != null) {
            ShooterAdapter adapter = (ShooterAdapter) recyclerView.getAdapter();
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
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
            View view = recyclerView.getChildAt(postion);
            ShooterViewHolder viewHolder = (ShooterViewHolder) recyclerView.getChildViewHolder
                    (view);
            if (viewHolder != null) {
                viewHolder.dispatchSelect();
            }
        }
    }

    @Override
    public void ProgramChange() {
        if(mProgramSeriesInfo != null && videoPlayerView != null){
            videoPlayerView.setSeriesInfo(mProgramSeriesInfo);
            videoPlayerView.playSingleOrSeries(videoIndex,0);
        }
    }

    private static class ShooterAdapter extends RecyclerView.Adapter<ShooterViewHolder> {

        private List<ProgramInfo> ModuleItems;
        private String currentUUID;
        private OnItemAction<ProgramInfo> mOnItemAction;
        private int currentIndex = 0;

        ShooterAdapter refreshData(List<ProgramInfo> datas) {
            ModuleItems = datas;
            return this;
        }

        void setOnItemAction(OnItemAction<ProgramInfo> onItemAction) {
            mOnItemAction = onItemAction;
        }

        @Override
        public ShooterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .fragment_qxdf_item_layout, parent, false);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view
                    .getLayoutParams();
            int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen
                    .width_18px) * -1;
            layoutParams.leftMargin = space;
            view.setLayoutParams(layoutParams);
            return new ShooterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ShooterViewHolder holder, int position) {
            ProgramInfo moduleItem = getItem(position);
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        if (mOnItemAction != null) mOnItemAction.onItemFocus(view);
                        ScaleUtils.getInstance().onItemGetFocus(view, holder.poster_focus);
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(view, holder.poster_focus);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgramInfo moduleItem = getItem(holder.getAdapterPosition());
                    if (moduleItem != null) {
                        currentUUID = moduleItem.getContentUUID();
                        mOnItemAction.onItemChange(currentIndex, holder.getAdapterPosition());
                        currentIndex = holder.getAdapterPosition();
                        mOnItemAction.onItemClick(moduleItem, holder.getAdapterPosition());
                    }
                }
            });


            if (moduleItem != null) {
                holder.poster.setIsPlaying(moduleItem.getContentUUID().equals(currentUUID), false);
//                holder.poster.setLoadImageUrl(moduleItem.getImg());
                int radius = holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.width_4px);
                Picasso.with(holder.itemView.getContext())
                        .load(moduleItem.getImg())
                        .transform(new PosterCircleTransform(holder.itemView.getContext(), radius))
                        .into(holder.poster);
                if (moduleItem.getContentUUID().equals(currentUUID) && position == currentIndex) {
                    holder.poster.setIsPlaying(true, false);
                } else {
                    holder.poster.setIsPlaying(false, false);
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

    private static class ShooterViewHolder extends RecyclerView.ViewHolder {
        public CurrentPlayImageViewWorldCup poster;
        public ImageView poster_focus;

        ShooterViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.shooter_poster);
            poster_focus = itemView.findViewById(R.id.shooter_poster_focus);

            DisplayUtils.adjustView(itemView.getContext(),poster,poster_focus,R.dimen.width_17dp,R.dimen.width_17dp);
        }

        public void dispatchSelect() {
            itemView.requestFocus();
            itemView.performClick();
        }
    }

}
