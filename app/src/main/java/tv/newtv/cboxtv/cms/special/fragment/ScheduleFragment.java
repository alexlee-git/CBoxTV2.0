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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import tv.newtv.cboxtv.player.util.PlayInfoUtil;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.custom.CurrentPlayImageViewWorldCup;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         09:45
 * 创建人:           weihaichao
 * 创建日期:          2018/4/26
 */
public class ScheduleFragment extends BaseSpecialContentFragment implements PlayerCallback {

    private AiyaRecyclerView recyclerView;
    private ModelResult<ArrayList<Page>> mModuleInfoResult;
    private List<Program> pageInfos;
    private FrameLayout viewStubCompat;
    private String currentUUID;
    private CurrentPlayImageViewWorldCup currentView;
    private TextView hintText;
    private String defaultUUID;
    private String defaultPlayUUID;
    private String playIndexUUID;
    private int videoIndex = 0;
    private boolean Isfirst = true;
    private String defaultFrame;

    private PlayInfo playInfo;
    private Content mProgramSeriesInfo;


    private Runnable selectRunnable = new Runnable() {
        @Override
        public void run() {
            if (getContentView() != null) {
                CurrentPlayImageViewWorldCup frameLayout = getContentView().findViewWithTag
                        (TextUtils.isEmpty
                                (defaultFrame) ? "frame_0" : defaultFrame);
                if (frameLayout != null) {
                    if (currentView != null) return;
                    frameLayout.performClick();
                }
            }

        }
    };
    private boolean isPause;
    private String focusRight;

    @Override
    protected int getVideoPlayIndex() {
        return videoIndex;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        videoPlayerView = null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.schedule_fragment_layout;
    }

    @Override
    protected void onItemContentResult(String uuid, Content content) {

    }

    private HashMap<String,ModelResult<ArrayList<Page>>> cacheData;

    private void getPageData(final String pageUUID) {
        if (TextUtils.equals(currentUUID, pageUUID)) return;
        if (TextUtils.isEmpty(pageUUID)) return;
        if (getContentView() != null) {
            getContentView().removeCallbacks(selectRunnable);
        }
        currentUUID = pageUUID;
        if(cacheData != null && cacheData.containsKey(pageUUID)){
            LoadPageDataComplete(cacheData.get(pageUUID),pageUUID);
            return;
        }
        if (playInfo == null) {
            playInfo = new PlayInfo();
        }
        playInfo.PageUUID = pageUUID;
        viewStubCompat.removeAllViews();
        currentView = null;


        //TODO 从服务端去数据




//        NetClient.INSTANCE.getSpecialApi().getPageData(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID,
//                pageUUID)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<ResponseBody>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                    }
//
//                    @Override
//                    public void onNext(ResponseBody value) {
//                        String result = null;
//                        try {
//                            result = value.string();
//                            ModelResult<List<Page>> moduleData = ModuleUtils.getInstance()
//                                    .parseJsonForModuleInfo(result);
//                            if(cacheData == null){
//                                cacheData = new HashMap<>();
//                            }
//                            cacheData.put(pageUUID,moduleData);
//                            LoadPageDataComplete(moduleData, pageUUID);
//                        } catch (IOException e) {
//                            LogUtils.e(e.toString());
//                            LoadPageDataFailed(e.getMessage());
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        LogUtils.e(e.toString());
//                        LoadPageDataFailed(e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//                });
    }

    private void LoadPageDataComplete(final ModelResult<ArrayList<Page>> infoResult, String uuid) {
        try {
            if (!TextUtils.equals(currentUUID,uuid)) {
                return;
            }
            if (infoResult == null || !"0".equals(infoResult.getErrorCode())) {
                return;
            }
            List<Page> moduleItems = infoResult.getData();
            if (moduleItems == null || moduleItems.size() == 0) {
                return;
            }
            final List<Program> programInfos = moduleItems.get(0).getPrograms();
            if (programInfos == null || programInfos.size() == 0) {
                return;
            }

            viewStubCompat.removeAllViews();

            pageInfos = programInfos;

            int size = programInfos.size();

            View targetView = null;

            if (size <= 0) return;

            if (size > 1 && size % 2 != 0) {
                size = size - 1;
            }

            if (size == 6) {
                targetView = getLayoutInflater().inflate(R.layout.six_frame_layout, null, false);
            } else if (size == 4) {
                targetView = getLayoutInflater().inflate(R.layout.four_frame_layout, null, false);
            } else if (size == 2) {
                targetView = getLayoutInflater().inflate(R.layout.two_frame_layout, null, false);
            } else if (size == 8) {
                targetView = getLayoutInflater().inflate(R.layout.eight_frame_layout, null, false);
            }
            if (targetView != null) {
                viewStubCompat.addView(targetView);//
                for (int index = 0; index < size; index++) {
                    Program programInfo = programInfos.get(index);
                    String frameId = "frame_" + index;
                    if (TextUtils.equals(programInfo.getContentId(),defaultPlayUUID)) {
                        defaultFrame = frameId;
                    }
                    CurrentPlayImageViewWorldCup currentPlayImageView = targetView.findViewWithTag
                            (frameId);
                    currentPlayImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (currentView != null) {
                                currentView.setIsPlaying(false, false);
                            }

                            currentView = (CurrentPlayImageViewWorldCup) view;
                            currentView.setIsPlaying(true, true);

                            String tag = view.getTag().toString();
                            int index = Integer.parseInt(tag.substring(tag.length() - 1));

                            play(index);
                        }
                    });
                    currentPlayImageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {
                            if (b) {
                                ScaleUtils.getInstance().onItemGetFocus(view,1.06f);
                            } else {
                                ScaleUtils.getInstance().onItemLoseFocus(view,1.06f);
                            }
                        }
                    });

                    currentPlayImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    if (playInfo != null && TextUtils.equals(programInfo.getContentId(),playInfo
                            .ContentUUID) &&
                    TextUtils.equals(currentUUID,playInfo.ContentUUID)) {
                        currentView = currentPlayImageView;
                        currentPlayImageView.setIsPlaying(true, true);
                    }
                    Picasso.get()
                            .load(programInfo.getImg())
                            .into(currentPlayImageView);
                }


                if (Isfirst) {
                    getContentView().postDelayed(selectRunnable, 3000);
                    Isfirst = false;
                } else {
                    getContentView().postDelayed(selectRunnable, 10000);
                }
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    private void play(final int index) {
        Program programInfo = pageInfos.get(index);
        PlayInfoUtil.getPlayInfo(programInfo.getContentId(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(Content info) {
                hintText.setVisibility(View.GONE);
                mProgramSeriesInfo = info;
                createPlayerView();
                if (videoPlayerView == null) {
                    return;
                } else {
                    Log.e("ScheduleFragment", "videoPlayerView is null");
                }
                if (info != null) {
                    Log.e("scheduleInfo", info.toString());
                    int index = 0;
                        if (info.getData() != null) {
                            for (int i = 0; i < info.getData().size(); i++) {
                                if (TextUtils.equals(info.getData().get(i).getContentUUID(),
                                playIndexUUID)) {
                                    index = i;
                                    break;
                                }
                            }
                        }

                    videoPlayerView.setSeriesInfo(info);
                    videoPlayerView.playSingleOrSeries(0, index);
                    if (playInfo == null) {
                        playInfo = new PlayInfo();
                    }
                    playInfo.ContentUUID = info.getContentID();
                } else {
                    videoPlayerView.showProgramError();
                }
            }
        });
    }

    private void LoadPageDataFailed(String desc) {
        Log.e("ScheduleFragment", "failed=" + desc);
    }

    private void createPlayerView() {
        if (videoPlayerView == null) {
            if (contentView == null) return;
            videoPlayerView = new VideoPlayerView(getContext());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout
                    .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            videoPlayerView.setLayoutParams(layoutParams);
            ViewGroup container = contentView.findViewById(R.id.video_container);
            container.addView(videoPlayerView, 0, layoutParams);

            videoPlayerView.setFocusView(contentView.findViewById(R.id.video_player_focus), true);
            videoPlayerView.setPlayerCallback(this);
        }
    }

    @Override
    protected void setUpUI(View view) {
        videoPlayerView = view.findViewById(R.id.video_player);

        viewStubCompat = view.findViewById(R.id.view_stub);
        recyclerView = view.findViewById(R.id.recyle_view);
        recyclerView.setDirIndicator(view.findViewById(R.id.indicator_up), view.findViewById(R.id
                .indicator_down));
        hintText = view.findViewById(R.id.hint_text);
        int space = LauncherApplication.AppContext.getApplicationContext().getResources()
                .getDimensionPixelOffset(R.dimen.height_54px);
        recyclerView.setSpace(space, 0);
        recyclerView.setAlign(AiyaRecyclerView.ALIGN_CENTER);
        recyclerView.setLayoutManager(new LinearLayoutManager(LauncherApplication.AppContext,
                LinearLayoutManager.VERTICAL, false));
        final ScheduleAdapter adapter = new ScheduleAdapter();
        view.findViewById(R.id.video_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProgramSeriesInfo == null || videoPlayerView == null) return;
                videoPlayerView.EnterFullScreen(getActivity(), false);
//                int mPlayPosition = videoPlayerView.getCurrentPosition();
//                NewTVLauncherPlayerViewManager.getInstance().playProgramSeries
//                        (ScheduleFragment.this.getActivity(), mProgramSeriesInfo, 0,
// mPlayPosition);
            }
        });


        adapter.setOnItemClick(new OnItemClick() {
            @Override
            public void onClick(String pageUUID) {
                getPageData(pageUUID);
            }
        });
        recyclerView.setAdapter(adapter);

        if (mModuleInfoResult != null) {
            updateUI();
        }
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        mModuleInfoResult = infoResult;
        if (UiReady) {
            updateUI();
        }
    }

    // TODO updateUI
    private void updateUI() {
        int defaultIndex = 0;
        List<Program> values = mModuleInfoResult.getData().get(0).getPrograms();
        if (getArguments() != null && getArguments().containsKey(Constant.DEFAULT_UUID)) {
            String uuid = getArguments().getString(Constant.DEFAULT_UUID);
            String focusParam = getArguments().getString(Constant.FOCUSPARAM);
//            if(uuid != null && focusParam !=null){
//                String[] splitfocusParam = focusParam.split("\\|");
//                if (splitfocusParam.length >= 1) {
//                    defaultPlayUUID = splitfocusParam[0];
//                }
//            }


            if (uuid != null) {
                defaultUUID = uuid;
                if (focusParam.contains("|")) {
                    String[] ids = focusParam.split("\\|");
                    if (ids.length > 1) {
                        defaultPlayUUID = ids[0];
                        playIndexUUID = ids[1];

                    }
                } else {
                    defaultPlayUUID = focusParam;
                    playIndexUUID = null;
                }

                for (Program programInfo : values) {
                    if (TextUtils.equals(programInfo.getContentId(),defaultUUID)) {
                        defaultIndex = values.indexOf(programInfo);
                    }
                }
            }
        }
        ((ScheduleAdapter) recyclerView.getAdapter()).refresh(values, defaultUUID)
                .notifyDataSetChanged();
        recyclerView.scrollToPosition(defaultIndex);
    }

    //TODO
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("ScheduleFragment", "dispatchKeyEvent action=" + event.getAction() + " keycode=" +
                event.getKeyCode());

        if (getContentView() == null) return true;
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            View focusView = getContentView().findFocus();
            if (focusView == null) {
                return super.dispatchKeyEvent(event);
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View targetView = null;
                if (focusView.getParent().getParent() instanceof AiyaRecyclerView) {
                    getContentView().findViewById(R.id.video_container).requestFocus();
                    return true;
                } else if (focusView.getId() == R.id.video_container) {
                    targetView = viewStubCompat.findViewWithTag("frame_0");
                    if (targetView != null) {
                        targetView.requestFocus();
                        return true;
                    }
                } else {
                    targetView = FocusFinder.getInstance().findNextFocus((ViewGroup)
                            getContentView(), getContentView().findFocus(), View
                            .FOCUS_RIGHT);
                    if (targetView != null) {
                        targetView.requestFocus();
                        return true;
                    }
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                View view = FocusFinder.getInstance().findNextFocus((ViewGroup) getContentView(),
                        getContentView().findFocus(), View.FOCUS_LEFT);
                if (view != null) {
                    if (view.getParent().getParent() instanceof AiyaRecyclerView) {
                        ((ScheduleAdapter) recyclerView.getAdapter()).requestFocus();
                        return true;
                    }
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (focusView.getId() == R.id.video_container) {
                    return true;
                }
                if (focusView.getParent().getParent() instanceof AiyaRecyclerView) {
                    return super.dispatchKeyEvent(event);
                }
                View view = FocusFinder.getInstance().findNextFocus((ViewGroup) focusView
                                .getParent(),
                        focusView, View.FOCUS_UP);
                if (view != null) {
                    view.requestFocus();
                    return true;
                } else {
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (focusView.getId() == R.id.video_container) {
                    return true;
                }
                if (focusView.getParent().getParent() instanceof AiyaRecyclerView) {
                    return super.dispatchKeyEvent(event);
                }
                View view = FocusFinder.getInstance().findNextFocus((ViewGroup) focusView
                                .getParent(),
                        focusView, View.FOCUS_DOWN);
                if (view != null) {
                    view.requestFocus();
                    return true;
                } else {
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onEpisodeChange(int index, int position) {
        videoIndex = index;
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        videoPlayerView.EnterFullScreen(getActivity(), true);
    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {

    }

    @Override
    public void ProgramChange() {
        if (videoPlayerView != null) {
            videoPlayerView.setSeriesInfo(mProgramSeriesInfo);
            videoPlayerView.playSingleOrSeries(videoIndex, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPause) {
            updateUI();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isPause = true;
    }

    private interface OnItemClick {
        void onClick(String pageUUID);
    }

    private static class ScheduleAdapter extends RecyclerView.Adapter<ScheduleViewHolder> {
        public String currentUUID;
        private OnItemClick clickListen;
        private List<Program> programInfos;
        private ScheduleViewHolder currentViewHolder;
        private String mDefaultFocus;

        ScheduleAdapter refresh(List<Program> value, String defaultFocus) {
            programInfos = value;
            mDefaultFocus = defaultFocus;
            return this;
        }

        public void requestFocus() {
            if (currentViewHolder != null) {
                currentViewHolder.title.requestFocus();
            }
        }

        public void onGetFocus(ScheduleViewHolder holder, String uuid) {
            if (currentViewHolder != null && currentViewHolder != holder) {
                currentViewHolder.title.setSelected(false);
            }
            currentUUID = uuid;
            currentViewHolder = holder;
        }

        public void onLostFocus(ScheduleViewHolder holder) {
            if (currentViewHolder == holder) {
                currentViewHolder.title.setSelected(true);
            }
        }

        public void setOnItemClick(OnItemClick onItemClick) {
            clickListen = onItemClick;
        }

        private Program getItem(int pos) {
            if (programInfos != null && pos >= 0 && programInfos.size() > pos) {
                return programInfos.get(pos);
            }
            return null;
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .schedule_fragment_item_layout, parent, false);
            return new ScheduleViewHolder(view, this);
        }

        @Override
        public void onBindViewHolder(final ScheduleViewHolder holder, int position) {
            Program programInfo = getItem(position);
            if (programInfo != null) {
                holder.setOnClickListen(new OnItemClick() {
                    @Override
                    public void onClick(String pageUUID) {
                        clickListen.onClick(pageUUID);
                    }
                });
                holder.setData(programInfo);
                holder.title.setSelected(TextUtils.equals(programInfo.getContentId(),currentUUID));
                Log.e("Fragment", "select uuid=" + currentUUID + " postion=" + position + " sel="
                        + holder
                        .title.isSelected());
                if (holder.title.isSelected()) {
                    currentViewHolder = holder;
                    holder.title.postInvalidate();
                }

                if (TextUtils.isEmpty(currentUUID)) {
                    if (!TextUtils.isEmpty(mDefaultFocus)) {
                        if (TextUtils.equals(programInfo.getContentId(),mDefaultFocus)) {
                            holder.itemView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    holder.performClick();
                                }
                            }, 200);

                        }
                    } else {
                        if (position == 0) {
                            holder.performClick();
                        }
                    }
                }
            }


        }

        @Override
        public int getItemCount() {
            return programInfos != null ? programInfos.size() : 0;
        }
    }

    private static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private Program mProgramInfo;
        private ScheduleAdapter mAdapter;

        ScheduleViewHolder(View itemView, ScheduleAdapter adapter) {
            super(itemView);
            title = itemView.findViewById(R.id.schedule_title);
            mAdapter = adapter;
        }

        public void performClick() {
            title.requestFocus();
        }

        public void setOnClickListen(final OnItemClick listen) {
            title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        mAdapter.onGetFocus(ScheduleViewHolder.this, mProgramInfo.getContentId());
                        title.setSelected(true);
                        listen.onClick(mProgramInfo.getContentId());
                    } else {
                        mAdapter.onLostFocus(ScheduleViewHolder.this);
                    }
                }
            });
        }

        public void setData(Program programInfo) {
            mProgramInfo = programInfo;
            title.setText(programInfo.getTitle());
            if (TextUtils.equals(programInfo.getContentId(),mAdapter.currentUUID)) {
                mAdapter.onGetFocus(ScheduleViewHolder.this, mProgramInfo.getContentId());
            }
        }
    }

    private class PlayInfo {
        String PageUUID;
        String ContentUUID;
    }
}
