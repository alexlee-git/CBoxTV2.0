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

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
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
public class QXDFFragment extends BaseSpecialContentFragment implements
        OnItemAction<Program>, PlayerCallback {
    private AiyaRecyclerView recyclerView;
    private ModelResult<ArrayList<Page>> moduleInfoResult;
    private View downView;
    private int videoIndex = 0;
    private Content mProgramSeriesInfo;

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainLooper.get().clear();
    }

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
    protected void onItemContentResult(String uuid, Content info) {
        mProgramSeriesInfo = info;
        if (info != null) {
            Log.e("info", info.toString());
            videoPlayerView.setSeriesInfo(info);
            videoPlayerView.playSingleOrSeries(0, 0);
        } else {
            videoPlayerView.showProgramError();
        }
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
            adapter.refreshData(moduleInfoResult.getData().get(0).getPrograms())
                    .notifyDataSetChanged();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (getContentView() == null) return true;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            View focusView = getContentView().findFocus();
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (recyclerView.getDefaultFocusView() != null) {
                    if (recyclerView.getDefaultFocusView().hasFocus()) {
                        View view = FocusFinder.getInstance().findNextFocus(recyclerView,
                                recyclerView
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
            }else if (event.getKeyCode()==KeyEvent.KEYCODE_DPAD_DOWN){
                if (recyclerView!=null){
                    recyclerView.requestFocus();
                }
            }
        }
        return super.dispatchKeyEvent(event);
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
    public void onItemFocus(View item) {

    }

    @Override
    public void onItemClick(Program item, final int index) {
//        videoPlayerView.beginChange();

        getContent(item.getL_id(),item.getL_contentType());
    }

    @Override
    public void onItemChange(final int before, final int current) {
        MainLooper.get().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (recyclerView != null && recyclerView.getAdapter() != null) {
                    recyclerView.getAdapter().notifyItemChanged(before);
                    recyclerView.getAdapter().notifyItemChanged(current);
                }
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
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
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
        if (mProgramSeriesInfo != null && videoPlayerView != null) {
            videoPlayerView.setSeriesInfo(mProgramSeriesInfo);
            videoPlayerView.playSingleOrSeries(videoIndex, 0);
        }
    }

    private static class ShooterAdapter extends RecyclerView.Adapter<ShooterViewHolder> {

        private List<Program> ModuleItems;
        private String currentUUID;
        private OnItemAction<Program> mOnItemAction;
        private int currentIndex = 0;
        private boolean isStart = true;

        ShooterAdapter refreshData(List<Program> datas) {
            ModuleItems = datas;
            return this;
        }

        void setOnItemAction(OnItemAction<Program> onItemAction) {
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
            Program moduleItem = getItem(position);
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
                    Program moduleItem = getItem(holder.getAdapterPosition());
                    if (moduleItem != null) {
                        currentUUID = moduleItem.getContentId();
                        mOnItemAction.onItemChange(currentIndex, holder.getAdapterPosition());
                        currentIndex = holder.getAdapterPosition();
                        mOnItemAction.onItemClick(moduleItem, holder.getAdapterPosition());
                    }
                }
            });

            if (moduleItem != null) {

                int radius = holder.itemView.getContext().getResources().getDimensionPixelOffset
                        (R.dimen.width_4px);


                Picasso.get()
                        .load(moduleItem.getImg())
                        .transform(new PosterCircleTransform(holder.itemView.getContext(),
                                radius))
                        .into(holder.poster);
                holder.poster.setTag(R.id.tag_imageview, moduleItem.getImg());

                if (TextUtils.equals(moduleItem.getContentId(), currentUUID) && position ==
                        currentIndex) {
                    holder.poster.setIsPlaying(true, false);
                } else {
                    holder.poster.setIsPlaying(false, false);
                }

                if (!TextUtils.isEmpty(moduleItem.getContentId())) {
                    holder.poster.setIsPlaying(TextUtils.equals(moduleItem.getContentId(),
                            currentUUID), false);
                }
                if (TextUtils.isEmpty(currentUUID) && position == 0 && isStart) {
                    holder.dispatchSelect();
                    isStart = false;
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
        CurrentPlayImageViewWorldCup poster;
        ImageView poster_focus;

        ShooterViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.shooter_poster);
            poster_focus = itemView.findViewById(R.id.shooter_poster_focus);

            DisplayUtils.adjustView(itemView.getContext(), poster, poster_focus, R.dimen
                    .width_17dp, R.dimen.width_17dp);
        }

        void dispatchSelect() {
            itemView.requestFocus();
            itemView.performClick();
        }
    }

}
