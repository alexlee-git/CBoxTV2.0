package tv.newtv.cboxtv.cms.special.doubleList.fragment;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.special.doubleList.adapter.NewSpecialCenterAdapter;
import tv.newtv.cboxtv.cms.special.doubleList.adapter.NewSpecialLeftAdapter;
import tv.newtv.cboxtv.cms.special.doubleList.bean.SpecialBean;
import tv.newtv.cboxtv.cms.special.doubleList.view.FocusRecyclerView;
import tv.newtv.cboxtv.cms.special.fragment.BaseSpecialContentFragment;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.cms.util.ModuleUtils;
import tv.newtv.cboxtv.player.videoview.PlayerCallback;
import tv.newtv.cboxtv.player.videoview.VideoExitFullScreenCallBack;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.utils.PlayInfoUtil;

/**
 * 双列表专题界面
 */

public class NewSpecialFragment extends BaseSpecialContentFragment implements PlayerCallback {

    private static final String TAG = NewSpecialFragment.class.getSimpleName();
    private static final String PAGEUUID = "3a6cc222-bb3f-11e8-8f40-c7d8a7a18cc4";
    private static final int LEFT_TO_CENTER_POSITION = 0X001;
    private static final int VIDEO_TO_CENTER_POSITION = 0X002;
    private static final int CENTER_REFRESH_DATA = 0X003;
    private static final int SELECT_DEFAULT_ITEM = 0X004;
    private static final int VIDEO_PLAY = 0X005;
    private static final int VIDEO_NEXT_PLAY = 0X006;
    private FocusRecyclerView mLeftMenu, mCenterMenu;
    private NewSpecialLeftAdapter mNewSpecialLeftAdapter;
    private NewSpecialCenterAdapter mNewSpecialCenterAdapter;
    private LinearLayoutManager mLeftManager, mCenterManager;
    private TextView mSpecialTopicName, mSpecialTopicTitle;
    private LinearLayout mVideoLinear;
    private FrameLayout mFocusViewVideo;
    private TextView mVideoPlayerTitle;
    private ImageView mFullScreenImage;
    private List<ProgramInfo> mLeftData = new ArrayList<>();
    private List<ProgramInfo> mLeftFocusedData = new ArrayList<>();
    private List<SpecialBean.DataBean.ProgramsBean> mCenterData = new ArrayList<>();
    private List<SpecialBean.DataBean.ProgramsBean> mCenterFocusedData = new ArrayList<>();
    private boolean isFristPlay = true, isRightToLeft = false;
    private int leftPosition = 0, oldLeftPosition = -1, centerPosition = 0;
    private View focusView;
    //数据
    private int currentIndex = 0;
    private ModuleInfoResult mModuleInfoResult;
    private String mLeftContentId;
    private SpecialBean mSpecialBean = null;
    private ProgramSeriesInfo mProgramSeriesInfo;
    private SpecialHandler mSpecialHandler = new SpecialHandler(NewSpecialFragment.this);

    class SpecialHandler extends Handler {
        WeakReference<NewSpecialFragment> mWeakHandler;

        public SpecialHandler(NewSpecialFragment activity) {
            mWeakHandler = new WeakReference<NewSpecialFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            NewSpecialFragment mNewSpecialFragment = mWeakHandler.get();
            if (mNewSpecialFragment != null) {
                switch (msg.what) {
                    case LEFT_TO_CENTER_POSITION:
                        if (mCenterMenu.getChildAt(msg.arg1) != null) {
                            mCenterMenu.getChildAt(msg.arg1).requestFocus();
                        } else {
                            printLogAndToast("Handler", "ywy left to center is null", false);
                        }
                        break;
                    case VIDEO_TO_CENTER_POSITION:
                        if (mCenterMenu.getChildAt(centerPosition) != null) {
                            mCenterMenu.getChildAt(centerPosition).requestFocus();
                        } else {
                            printLogAndToast("Handler", "ywy initVideo key left is null", false);
                        }
                        break;
                    case CENTER_REFRESH_DATA:
                        printLogAndToast("Handler", "case CENTER_REFRESH_DATA", false);
                        mSpecialBean = (SpecialBean) msg.obj;
                        if(null != mSpecialBean){
                            printLogAndToast("Handler", "case msg.obj Center_Refresh_data : " + mSpecialBean.getData().toString(), false);
                            printLogAndToast("Handler", "case msg.obj Center_Refresh_data : " + mSpecialBean.getData().getPrograms(), false);
                        }else{
                            printLogAndToast("Handler", "case msg.obj Center_Refresh_data is null ", false);
                        }
                        if (mSpecialBean.getData() != null) {
                            if (mSpecialBean.getData().getPrograms() != null) {
                                mCenterData = mSpecialBean.getData().getPrograms();
                                mNewSpecialCenterAdapter.refreshData(msg.arg1, mCenterData);
                                if (isFristPlay) {
                                    mLeftFocusedData = mLeftData;
                                    mCenterFocusedData = mCenterData;
                                    mSpecialHandler.sendEmptyMessageDelayed(VIDEO_PLAY, 400);
                                    isFristPlay = false;
                                }
                            } else {
                                printLogAndToast("Handler", "ywy center_refresh_data Programs is null", false);
                            }
                        } else {
                            printLogAndToast("Handler", "ywy center_refresh_data is null", false);
                        }
                        break;
                    case VIDEO_PLAY:
                        if (mCenterData.size() > 0) {
                            setVideoFocusedPlay(mCenterData.get(0));
                            sendEmptyMessageDelayed(SELECT_DEFAULT_ITEM, 400);
                        } else {
                            printLogAndToast("Handler", "ywy video playurl is null", false);
                        }
                        break;
                    case SELECT_DEFAULT_ITEM:
                        setSelectBg(0, false);
                        break;
                    case VIDEO_NEXT_PLAY:
                        if (mLeftFocusedData != null && mLeftFocusedData.size() > 0) {
                            mSpecialTopicName.setText(mLeftFocusedData.get(leftPosition).getTitle());
                            mSpecialTopicTitle.setText(mLeftFocusedData.get(leftPosition).getSubTitle());
                        } else {
                            if (mLeftData != null) {
                                printLogAndToast("Handler", "video next play data: " + mLeftData.toString(), false);
                            } else {
                                printLogAndToast("Handler", "video next play data is null ", false);
                            }
                        }
                        if (mCenterFocusedData != null && mCenterFocusedData.size() > 0) {
                            mVideoPlayerTitle.setText(mCenterFocusedData.get(centerPosition).getTitle());
                        }
                        setVideoFocusedPlay(mCenterFocusedData.get(centerPosition));
                        mNewSpecialCenterAdapter.setSelected(centerPosition);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_newspecial_layout;
    }

    @Override
    protected void setUpUI(View view) {
        initTitle(view);
        initLeftList(view);
        initCenterList(view);
        initVideo(view);
    }

    @Override
    public void setModuleInfo(ModuleInfoResult infoResult) {
        mModuleInfoResult = infoResult;
        String uuid = getArguments().getString(Constant.DEFAULT_UUID);
        if (null != mModuleInfoResult) {
            printLogAndToast("setModuleInfo", "ywy Modulenfo 1 : " + mModuleInfoResult.toString() + "  uuid : " + uuid, false);
        } else {
            printLogAndToast("setModuleInfo", "ywy Modulenfo 1 is null :   uuid : " + uuid, false);
        }
        Log.d(TAG, "ywy Modulenfo 1 : " + mModuleInfoResult.toString() + "  uuid : " + uuid);
        mLeftData = mModuleInfoResult.getDatas().get(0).getDatas();
        if (null != mLeftData) {
            printLogAndToast("setModuleInfo", "ywy Modulenfo 2 : " + mLeftData.toString(), false);
        } else {
            printLogAndToast("setModuleInfo", "ywy Modulenfo 2 : is null", false);
        }
    }

    @Override
    public void onEpisodeChange(int index, int position) {
        printLogAndToast("onEpisodeChange", "ywy index : " + index + " position : " + position, false);
        currentIndex = index;
    }

    @Override
    public void onPlayerClick(VideoPlayerView videoPlayerView) {
        printLogAndToast("onPlayerClick", "EnterFullScreen", false);
        videoPlayerView.EnterFullScreen(getActivity(), false);
        mVideoPlayerTitle.setVisibility(View.GONE);
        mFullScreenImage.setVisibility(View.GONE);
    }

    @Override
    public void AllPlayComplete(boolean isError, String info, VideoPlayerView videoPlayerView) {
        printLogAndToast("AllPalyComplete", "isError : " + isError + " info : " + info, false);
        if (mCenterFocusedData.size() - 1 < centerPosition) {
            return;
        } else {
            centerPosition++;
            mCenterMenu.smoothScrollToPosition(centerPosition);
            mSpecialHandler.sendEmptyMessageDelayed(VIDEO_NEXT_PLAY, 400);
        }
    }

    @Override
    public void ProgramChange() {
        printLogAndToast("ProgramChange", "ProgramChange", false);
        videoPlayerView.setSeriesInfo(mProgramSeriesInfo);
        videoPlayerView.playSingleOrSeries(currentIndex, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (leftPosition > 0) {
            mLeftMenu.getChildAt(leftPosition).requestFocus();
        }
        if (centerPosition > 0) {
            mCenterMenu.getChildAt(centerPosition).requestFocus();
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        printLogAndToast("onStop", "onStop", false);
        oldLeftPosition = 0;
        leftPosition = -1;
        centerPosition = 0;
        mLeftData.clear();
        mLeftFocusedData.clear();
        mCenterData.clear();
        mCenterFocusedData.clear();
        mNewSpecialCenterAdapter.clearList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isFristPlay = true;
        printLogAndToast("onDetach", "onDetach", false);
        videoPlayerView = null;
        mSpecialHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getContentView() != null) {
                focusView = getContentView().findFocus();
            }
            printLogAndToast("dispatchKeyEvent", "isInstanceof : " + (focusView instanceof VideoPlayerView) + " code : " + event.getKeyCode(), false);
            if (focusView instanceof VideoPlayerView) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    printLogAndToast("initVideo", "onKeyListener left", false);
                    mVideoPlayerTitle.setVisibility(View.GONE);
                    mFullScreenImage.setVisibility(View.GONE);
                    setCenterRecyclerFocused(true);
                    setLeftRecyclerFocused(false);
                    mCenterMenu.smoothScrollToPosition(centerPosition);
                    mSpecialHandler.sendEmptyMessageDelayed(VIDEO_TO_CENTER_POSITION, 300);
                    setVideoFocus(false);
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initTitle(View view) {
        mSpecialTopicName = view.findViewById(R.id.fragment_newspecial_video_name);
        mSpecialTopicTitle = view.findViewById(R.id.fragment_newspecial_video_title);
        if (mLeftData != null && mLeftData.size() > 0) {
            mSpecialTopicName.setText(mLeftData.get(0).getTitle());
            mSpecialTopicTitle.setText(mLeftData.get(0).getSubTitle());
        }
    }

    private void initLeftList(View view) {
        Log.d(TAG, "ywy : initLeftList");
        mLeftMenu = view.findViewById(R.id.fragment_newspecial_left_list);
        mLeftManager = new LinearLayoutManager(getContext());
        mLeftManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLeftRecyclerFocused(true);
        mLeftMenu.setLayoutManager(mLeftManager);
        mNewSpecialLeftAdapter = new NewSpecialLeftAdapter(getContext(), mLeftData);
        mNewSpecialLeftAdapter.setOnFoucesDataChangeListener(new NewSpecialLeftAdapter.OnFoucesDataChangeListener() {
            @Override
            public void onFoucesDataChangeListener(String contentId, int position) {
                mLeftContentId = contentId;
                printLogAndToast("initLeftList", "position : " + position, false);
                if (!isRightToLeft) {
                    getCenterData(position, mLeftContentId);
                } else {
                    isRightToLeft = false;
                }
            }
        });
        mLeftMenu.setAdapter(mNewSpecialLeftAdapter);
        mLeftMenu.setOnGetPositionListener(new FocusRecyclerView.OnGetPositionListener() {
            @Override
            public void GetRightPositionListener(int position) {
                printLogAndToast("initLeftList", "ywy left position right : " + position + " centerPosition : " + centerPosition, false);
                leftPosition = position;
                int firstVP = mCenterManager.findFirstVisibleItemPosition();
                int lastVP = mCenterManager.findLastVisibleItemPosition();
                setLeftRecyclerFocused(false);
                setCenterRecyclerFocused(true);
                Message msg = Message.obtain();
                msg.what = LEFT_TO_CENTER_POSITION;
                if (centerPosition > 0) {
                    msg.arg1 = centerPosition;
                    if (firstVP > centerPosition || lastVP < centerPosition) {
                        mCenterMenu.smoothScrollToPosition(centerPosition);
                    }
                } else {
                    msg.arg1 = 0;
                    mCenterMenu.smoothScrollToPosition(0);
                }
                mSpecialHandler.sendMessageDelayed(msg, 400);
            }

            @Override
            public void GetLeftPositionListener(int position) {
                Log.d(TAG, "ywy left position left : " + position);
            }
        });
    }

    private void initCenterList(View view) {
        Log.d(TAG, "ywy : initCenterList");
        mCenterMenu = view.findViewById(R.id.fragment_newspecial_center_list);
        setCenterRecyclerFocused(false);
        mCenterManager = new LinearLayoutManager(LauncherApplication.AppContext);
        mCenterManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCenterMenu.setLayoutManager(mCenterManager);
        mNewSpecialCenterAdapter = new NewSpecialCenterAdapter(LauncherApplication.AppContext, mCenterData);
        mCenterMenu.setAdapter(mNewSpecialCenterAdapter);
        mCenterMenu.setOnGetPositionListener(new FocusRecyclerView.OnGetPositionListener() {
            @Override
            public void GetRightPositionListener(int position) {
                //centerPosition = position;
                Log.d(TAG, "center get   right");
                setLeftRecyclerFocused(false);
                setCenterRecyclerFocused(false);
                setVideoFocus(true);
                if (centerPosition == -1) {
                    mVideoPlayerTitle.setText(mCenterFocusedData.get(0).getTitle());
                } else {
                    mVideoPlayerTitle.setText(mCenterFocusedData.get(centerPosition).getTitle());
                }
            }

            @Override
            public void GetLeftPositionListener(int position) {
                Log.d(TAG, "center get   left");
                isRightToLeft = true;
                setLeftRecyclerFocused(true);
                setCenterRecyclerFocused(false);
                setVideoFocus(false);
                Log.d(TAG, "ywy first : " + (mCenterManager.findFirstVisibleItemPosition() > leftPosition) + "  last " + (mCenterManager.findLastVisibleItemPosition() < leftPosition));
                //mLeftMenu.smoothScrollToPosition(leftPosition);
                if (mLeftMenu.getChildAt(leftPosition) != null) {
                    mLeftMenu.getChildAt(leftPosition).requestFocus();
                } else {
                    Log.d(TAG, "ywy GetLeftPositionListener is null");
                }
            }
        });
        mNewSpecialCenterAdapter.setOnFocusedVideoChangeListener(new NewSpecialCenterAdapter.OnFocusedVideoChangeListener() {
            @Override
            public void onFocusedVideoChangeListener(String title, int position) {
                centerPosition = position;
                mLeftFocusedData = mLeftData;
                mCenterFocusedData = mCenterData;
                if (mCenterData.get(position) != null) {
                    mSpecialTopicName.setText(mLeftFocusedData.get(leftPosition).getTitle());
                    mSpecialTopicTitle.setText(mLeftFocusedData.get(leftPosition).getSubTitle());
                    mVideoPlayerTitle.setText(mCenterFocusedData.get(centerPosition).getTitle());
                } else {
                    printLogAndToast("initCenterList", "mCenter position view is null", false);
                }
                setVideoFocus(true);
                setSelectBg(position, true);
                setVideoFocusedPlay(mCenterFocusedData.get(position));
                setLeftRecyclerFocused(false);
                setCenterRecyclerFocused(false);
            }
        });
    }

    private void initVideo(View view) {
        mVideoLinear = view.findViewById(R.id.video_linear);
        mFocusViewVideo = view.findViewById(R.id.fragment_newspecial_video_focus);
        videoPlayerView = view.findViewById(R.id.fragment_newspecial_video_player);
        videoPlayerView.setPlayerCallback(this);
        videoPlayerView.setFocusView(mFocusViewVideo, true);
        mVideoPlayerTitle = view.findViewById(R.id.fragment_newspecial_video_player_title);
        mFullScreenImage = view.findViewById(R.id.fragment_newspecial_video_player_full_tip);
        setVideoFocus(false);
        videoPlayerView.setVideoExitCallback(new VideoExitFullScreenCallBack() {
            @Override
            public void videoEitFullScreen() {
                if (mVideoPlayerTitle != null && mFullScreenImage != null) {
                    mVideoPlayerTitle.setVisibility(View.VISIBLE);
                    mFullScreenImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setSelectBg(int position, boolean isClick) {
        mNewSpecialCenterAdapter.reFreshSecleted(position, isClick);
        //mNewSpecialCenterAdapter.refreshData(leftPosition,mCenterData);
        mNewSpecialCenterAdapter.notifyDataSetChanged();
        if (oldLeftPosition != -1 && mLeftMenu.getChildAt(oldLeftPosition) != null) {
            mLeftMenu.getChildAt(oldLeftPosition).setBackgroundColor(Color.parseColor("#00000000"));
        }
        if (mLeftMenu.getChildAt(leftPosition) != null) {
            mLeftMenu.getChildAt(leftPosition).setBackgroundResource(R.drawable.xuanhong);
        } else {
            printLogAndToast("setSelectBg", "ywy setSelectBg is null", false);
        }
        oldLeftPosition = leftPosition;
    }

    private void setVideoFocusedPlay(SpecialBean.DataBean.ProgramsBean mProgramsBean) {
        if (videoPlayerView != null) {
            videoPlayerView.beginChange();
        }
        PlayInfoUtil.getPlayInfo(mProgramsBean.getContentUUID(), new PlayInfoUtil
                .ProgramSeriesInfoCallback() {
            @Override
            public void onResult(ProgramSeriesInfo info) {
                if (info != null) {
                    mProgramSeriesInfo = info;
                    printLogAndToast("setVideoFocusedPlay", "info :" + info, false);
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

    private void getCenterData(final int position, String contentId) {
        if (contentId.length() > 2) {
            printLogAndToast("getCenterData", "left : " + contentId.substring(0, 2) + "  right : " + contentId.substring(contentId.length() - 2, contentId.length()) + "  all : " + contentId, false);
        } else {
            printLogAndToast("getCenterData", "contentId length < 2 ", false);
        }
        // 从服务端去数据
        NetClient.INSTANCE.getSpecialApi().getDoublePageData(Constant.APP_KEY, Constant.CHANNEL_ID, contentId.substring(0, 2), contentId.substring(contentId.length() - 2, contentId.length()),
                contentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            mSpecialBean = ModuleUtils.getInstance()
                                    .parseJsonForNewSpecialSecondData(value.string());
                            printLogAndToast("getCenterData", "bean : " + mSpecialBean.toString() + " value : " + value.string(), false);
                            Message msg = Message.obtain();
                            msg.what = CENTER_REFRESH_DATA;
                            msg.arg1 = position;
                            msg.obj = mSpecialBean;
                            mSpecialHandler.sendMessage(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(e.toString());
                        printLogAndToast("getCenterData onError", "ywy Throwable: " + e.toString(), false);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void setLeftRecyclerFocused(boolean isFocus) {
        mLeftMenu.setFocusable(isFocus);
        if (isFocus) {
            mLeftMenu.requestFocus();
        }
    }

    private void setCenterRecyclerFocused(boolean isFocus) {
        mCenterMenu.setFocusable(isFocus);
        if (isFocus) {
            mCenterMenu.requestFocus();
        }
    }

    private void setVideoFocus(boolean isFocus) {
        if (mVideoLinear != null) {
            mVideoLinear.setFocusable(isFocus);
        }
        if (videoPlayerView != null) {
            videoPlayerView.setFocusable(isFocus);
        }
        if (isFocus) {
            mFocusViewVideo.requestFocus();
            mVideoPlayerTitle.setVisibility(View.VISIBLE);
            mFullScreenImage.setVisibility(View.VISIBLE);
            //videoPlayerView.setFocusView(mFocusViewVideo, true);
        } else {
            mVideoPlayerTitle.setVisibility(View.GONE);
            mFullScreenImage.setVisibility(View.GONE);
        }
    }

    private void printLogAndToast(String method, String content, boolean showToast) {
        if (showToast) {
            Toast.makeText(LauncherApplication.AppContext, method + " ywy " + content, Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, method + " ywy " + content);
    }

}
