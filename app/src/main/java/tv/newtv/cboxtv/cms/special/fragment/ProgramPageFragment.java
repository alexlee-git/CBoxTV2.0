package tv.newtv.cboxtv.cms.special.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.util.DisplayUtils;

import tv.newtv.cboxtv.player.util.PlayInfoUtil;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.custom.CurrentPlayImageViewWorldCup;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         13:21
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class ProgramPageFragment extends BaseSpecialContentFragment implements PlayerCallback {
    private AiyaRecyclerView recyclerView;
    private ModelResult<ArrayList<Page>> moduleInfoResult;
    private int currentIndex = 0;
    private TextView tvProgramaTitle;

    @Override
    protected int getVideoPlayIndex() {
        return currentIndex;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_programpage_layout;
    }

    @Override
    protected void onItemContentResult(Content content) {

    }

    @Override
    protected void setUpUI(View view) {
        videoPlayerView = view.findViewById(R.id.video_player);
        videoPlayerView.setPlayerCallback(this);
        videoPlayerView.setFocusView(view.findViewById(R.id.video_player_focus), true);

        recyclerView = view.findViewById(R.id.shooter_recycle);
        tvProgramaTitle = view.findViewById(R.id.tv_programa_title);
        recyclerView.setAlign(AiyaRecyclerView.ALIGN_AUTO);
        recyclerView.setDirIndicator(null, view.findViewById(R.id.right_direction));
        int space = getResources().getDimensionPixelOffset(R.dimen._height_18px);
        recyclerView.setSpace(0, space);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        ShooterAdapter adapter = new ShooterAdapter();
        adapter.setOnItemAction(new OnItemAction<Program>() {
            @Override
            public void onItemFocus(View item) {

            }

            @Override
            public void onItemClick(Program item, int index) {
                playVideo(item);
                currentIndex = index;
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
        });
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);


        if (moduleInfoResult != null) {
            adapter.refreshData(moduleInfoResult.getData().get(0).getPrograms())
                    .notifyDataSetChanged();
            tvProgramaTitle.setText(moduleInfoResult.getSubTitle());

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && getContentView() != null) {
            View focusView = getContentView().findFocus();
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (focusView instanceof VideoPlayerView) {
                    return true;
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (focusView instanceof VideoPlayerView) {
                    if (recyclerView != null && recyclerView.getDefaultFocusView() != null) {
                        recyclerView.getDefaultFocusView().requestFocus();
                        return true;
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void playVideo(Program programInfo) {
        if (programInfo == null) return;
        PlayInfoUtil.getPlayInfo(programInfo.getContentId(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {

            @Override
            public void onResult(Content info) {
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
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        moduleInfoResult = infoResult;
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            ((ShooterAdapter) recyclerView.getAdapter()).refreshData(infoResult.getData().get(0)
                    .getPrograms()).notifyDataSetChanged();
        }
    }

    @Override
    public void onEpisodeChange(int index, int position) {
        currentIndex = index;
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoPlayerView.EnterFullScreen(getActivity(), false);
    }


    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
        if (recyclerView.getAdapter() != null) {
            ShooterAdapter adapter = (ShooterAdapter) recyclerView.getAdapter();
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            currentIndex++;
            if (adapter.getItemCount() - 1 < currentIndex) {
                return;
            }
            int first = layoutManager.findFirstVisibleItemPosition();
            int last = layoutManager.findLastVisibleItemPosition();
            int postion = 0;
            if (currentIndex == first) {
                postion = 0;
            } else if (currentIndex > first && currentIndex <= last) {
                postion = currentIndex - first;
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

    }

    private static class ShooterAdapter extends RecyclerView.Adapter<ShooterViewHolder> {

        private List<Program> ModuleItems;
        private OnItemAction<Program> onItemAction;
        private String currentID;
        private int currentIndex = 0;

        ShooterAdapter refreshData(List<Program> datas) {
            ModuleItems = datas;
            return this;
        }

        public void setOnItemAction(OnItemAction<Program> programInfoOnItemAction) {
            onItemAction = programInfoOnItemAction;
        }

        @Override
        public ShooterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .fragment_program_item_layout, parent, false);
            return new ShooterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ShooterViewHolder holder, int position) {
            Program moduleItem = getItem(position);
            holder.container.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        ScaleUtils.getInstance().onItemGetFocus(view, holder.poster_focus);
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(view, holder.poster_focus);
                    }
                }
            });

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Program programInfo = getItem(holder.getAdapterPosition());
                    if (programInfo != null) {
                        currentID = programInfo.getContentId();
                        onItemAction.onItemChange(currentIndex, holder.getAdapterPosition());

                        currentIndex = holder.getAdapterPosition();
                        onItemAction.onItemClick(programInfo, holder.getAdapterPosition());
                    }
                }
            });

            if (moduleItem != null) {
                holder.title.setText(moduleItem.getTitle());
                int radius = holder.itemView.getContext().getResources().getDimensionPixelOffset(R.dimen.width_4px);

                Picasso.get()
                        .load(moduleItem.getImg())
                        .transform(new PosterCircleTransform(holder.itemView.getContext(), radius))
                        .into(holder.poster);

                if (TextUtils.equals(moduleItem.getContentId(),currentID) && position == currentIndex) {
                    holder.poster.setIsPlaying(true, false);
                } else {
                    holder.poster.setIsPlaying(false, false);
                }
                if (TextUtils.isEmpty(currentID)) {
                    if (position == 0) {
                        holder.container.requestFocus();
                        holder.container.performClick();
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

    private static class ShooterViewHolder extends RecyclerView.ViewHolder {
        public CurrentPlayImageViewWorldCup poster;
        public ImageView poster_focus;
        public TextView title;
        public RelativeLayout container;

        public ShooterViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.id_container);
            poster = itemView.findViewById(R.id.id_poster);
            poster_focus = itemView.findViewById(R.id.id_poster_focus);
            title = itemView.findViewById(R.id.id_title);
            DisplayUtils.adjustView(itemView.getContext(), poster, poster_focus, R.dimen.width_17dp, R.dimen
                    .width_17dp);
        }

        public void dispatchSelect() {
            container.requestFocus();
            container.performClick();
        }
    }
}
