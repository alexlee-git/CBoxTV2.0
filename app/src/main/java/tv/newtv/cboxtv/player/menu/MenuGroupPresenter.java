package tv.newtv.cboxtv.player.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.menu.model.DBProgram;
import tv.newtv.cboxtv.player.menu.model.HeadMenuBean;
import tv.newtv.cboxtv.player.menu.model.LastMenuBean;
import tv.newtv.cboxtv.player.menu.model.Node;
import tv.newtv.cboxtv.player.menu.model.Program;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.uc.db.DBCallback;
import tv.newtv.cboxtv.uc.db.DBConfig;
import tv.newtv.cboxtv.uc.db.DataSupport;
import tv.newtv.cboxtv.utils.DeviceUtil;

/**
 * Created by TCP on 2018/5/11.
 */

public class MenuGroupPresenter implements ArrowHeadInterface, IMenuGroupPresenter, ScreenInterface {
    private static final String TAG = "MenuGroupPresenter";
    private static final String COLLECT = "我的收藏";
    private static final String HISTORY = "我的观看记录";
    private static final String SUBSCRIBE = "我的订阅";
    public static final long GONE_TIME = 5 * 1000L;
    private static final int MESSAGE_GONE = 1;
    /**
     * 获取节目集ID和节目ID 重试标识
     */
    private static final int RETRY_DATA = 2;
    /**
     * 设置栏目树最后一级数据的标识
     */
    private static final int SET_LAST_DATA = 3;
    /**
     * 等待广告播放完毕
     */
    private static final int WAIT_AD_END = 4;
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY = 10;
    private Context context;

    private View rootView;
    private ImageView hintArrowheadBigLeft;
    private ImageView hintArrowheadSmallLeft;
    private ImageView hintArrowheadBigRight;
    private ImageView hintArrowheadSmallRight;
    private ImageView takePlace1;
    private ImageView takePlace2;
    private View hintView;
    private MenuGroup menuGroup;

    private String programSeries = "";
    private String contentUUID = "";
    private String actionType = "";

    private boolean menuGroupIsInit = false;
    /**
     * 当前正在播放的节目
     */
    private Program playProgram;

    private LastMenuBean lastMenuBean;

    private Handler handler = new MyHandler();

    /**
     * 当前获取节目集ID和节目ID重试次数
     */
    private int retry = 0;

    @Override
    public void release() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (menuGroup != null) {
            menuGroup.release();
            menuGroup = null;
        }

    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_GONE:
                    gone();
                    break;
                case RETRY_DATA:
                    initData();
                    break;
                case SET_LAST_DATA:
                    setLastData();
                    break;
                case WAIT_AD_END:
                    checkShowHinter();
                    break;
            }
        }
    }

    public MenuGroupPresenter(Context context) {
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.menu_group_presenter, null);
        hintArrowheadBigLeft = rootView.findViewById(R.id.hint_arrowhead_big_left);
        hintArrowheadSmallLeft = rootView.findViewById(R.id.hint_arrowhead_small_left);
        hintArrowheadBigRight = rootView.findViewById(R.id.hint_arrowhead_big_right);
        hintArrowheadSmallRight = rootView.findViewById(R.id.hint_arrowhead_small_right);
        takePlace1 = rootView.findViewById(R.id.take_place1);
        takePlace2 = rootView.findViewById(R.id.take_place2);
        hintView = rootView.findViewById(R.id.hint_text);
        menuGroup = rootView.findViewById(R.id.menu_group);
        init();
    }

    public void init() {
        initData();
        initListener();
    }

    private void initListener() {
        menuGroup.setArrowHead(this);
        menuGroup.addOnSelectListener(new MenuGroup.OnSelectListener() {
            @Override
            public void select(Program program) {
                playProgram = program;
                LastMenuBean.DataBean data = program.getParent().getLastMenuBean().getData();
                int index = data.getPrograms().indexOf(program);
                ProgramSeriesInfo programSeriesInfo = program.getParent().getLastMenuBean().getData().convertProgramSeriesInfo();
                NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(context, programSeriesInfo, false, index, 0);
                menuGroup.gone();
                setPlayerInfo(program);
//                //如果不是我的观看记录节点，就保存数据
//                if(!program.checkNode(HISTORY)){
//                    DBUtil.addHistory(programSeriesInfo,index,0, Utils.getSysTime(),null);
//                }
            }
        });

        NewTVLauncherPlayerViewManager.getInstance().addListener(new IPlayProgramsCallBackEvent() {

            @Override
            public void onNext(ProgramSeriesInfo.ProgramsInfo info, int index, boolean isNext) {
                if (isNext) {
                    if (playProgram == null) {
                        return;
                    }
                    List<Program> programs = playProgram.getParent().getPrograms();
                    for (Program p : programs) {
                        if (p.getContentUUID().equals(info.getContentUUID())) {
                            playProgram = p;
                            break;
                        }
                    }
                }
            }
        });
    }

    private void setPlayerInfo(Program program) {
        if (program == null) {
            return;
        }
        Node node = program.getParent();
        int level = node.getLevel();
        for (int i = level; i >= 0; i--) {
            if (i == 1) {
                PlayerConfig.getInstance().setSecondColumnId(node.getActionUri());
            }
            if (i == 0) {
                PlayerConfig.getInstance().setColumnId(node.getActionUri());
            }
            node = node.getParent();
        }
    }

    /**
     * 从播放器中获取节目集id和节目id
     *
     * @return
     */
    private boolean getProgramSeriesAndContentUUID() {
        ProgramSeriesInfo programSeriesInfo = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
        int typeIndex = NewTVLauncherPlayerViewManager.getInstance().getTypeIndex();

        if (programSeriesInfo == null) {
            Log.e(TAG, "programSeriesInfo == null");
            return false;
        }

        if (typeIndex == -1) {
            //单节目
            programSeries = programSeriesInfo.getProgramSeriesUUIDs();
            contentUUID = programSeriesInfo.getContentUUID();
        } else {
            //节目集
            programSeries = programSeriesInfo.getContentUUID();
            int index = NewTVLauncherPlayerViewManager.getInstance().getIndex();
            if (programSeriesInfo.getData() != null && programSeriesInfo.getData().size() > index && index >= 0) {
                contentUUID = programSeriesInfo.getData().get(index).getContentUUID();
                actionType = programSeriesInfo.getData().get(index).getActionType();
                if(TextUtils.isEmpty(programSeries)){
                    programSeries = programSeriesInfo.getData().get(index).getSeriesSubUUID();
                }
            }
        }

        if (TextUtils.isEmpty(programSeries) || TextUtils.isEmpty(contentUUID)) {
            Log.e(TAG, "programSeries or contentUUID can not empty,programSeries=" + programSeries + ",contentUUID=" + contentUUID
                    + ",typeIndex=" + typeIndex);
            return false;
        }

        Log.i(TAG, "detailColumnUUID=" + programSeries);
        Log.i(TAG, "contentUUID=" + contentUUID);
        return true;
    }

    @SuppressLint("CheckResult")
    private void initData() {
        if (!getProgramSeriesAndContentUUID()) {
            if (retry++ < MAX_RETRY) {
                handler.sendEmptyMessageDelayed(RETRY_DATA, 1000);
            }
            return;
        }

        NetClient.INSTANCE.getMenuApi()
                .getCategoryTree(Constant.APP_KEY, Constant.CHANNEL_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HeadMenuBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(HeadMenuBean headMenuBean) {
                        if (headMenuBean != null && menuGroup != null) {
                            if ("0".equals(headMenuBean.getErrorCode())) {
                                menuGroup.setAllNodes(headMenuBean.getData());
                                getLastData();
                            } else {
                                LogUtils.e(TAG, "errorCode:" + headMenuBean.getErrorCode());
                            }
                        } else {
                            LogUtils.e("headMenuBean == null or menuGroup == null");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        searchDataInDB();
    }

    private void searchDataInDB() {
        Node root = new Node();
        root.setPid("");
        root.setTitle("我的");
        root.setId("root");

        final Node collect = new Node();
        collect.setPid("root");
        collect.setTitle("我的收藏");
        collect.setId("collect");
        collect.setParent(root);

        Node history = new Node();
        history.setPid("root");
        history.setTitle("我的观看记录");
        history.setId("attention");
        history.setParent(root);

        Node subscribe = new Node();
        subscribe.setPid("root");
        subscribe.setTitle("我的订阅");
        subscribe.setId("subscribe");
        subscribe.setParent(root);

        List<Node> list = new ArrayList<>();
        list.add(collect);
        list.add(history);
        list.add(subscribe);
        root.setChildrens(list);

        searchInDB(DBConfig.COLLECT_TABLE_NAME, collect);
        searchInDB(DBConfig.SUBSCRIBE_TABLE_NAME, subscribe);
        searchInDB(DBConfig.HISTORY_TABLE_NAME, history);

        menuGroup.addNodeToRoot(root);
    }

    private void searchInDB(String titleName, final Node node) {
        DataSupport.search(titleName)
                .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        Log.i(TAG, "onResult: " + result);
                        List<DBProgram> list = dataDispose(result);
                        switch (node.getTitle()) {
                            case COLLECT:
                            case SUBSCRIBE:
                                setNode(list, node);
                                break;
                            case HISTORY:
                                if (list != null) {
                                    setProgram(node, DBProgram.convertProgram(list));
                                } else {
                                    setProgram(node, null);
                                }
                                break;
                        }
                    }
                }).excute();
    }

    private List<DBProgram> dataDispose(String result) {
        if (!TextUtils.isEmpty(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<DBProgram>>() {
            }.getType();
            List<DBProgram> list = gson.fromJson(result, type);
            return list;
        }
        return null;
    }

    private void setNode(List<DBProgram> list, Node parent) {
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                DBProgram program = list.get(i);
                if (TextUtils.isEmpty(program._title_name) || TextUtils.isEmpty(program._contentuuid))
                    continue;

                Node node = new Node();
                node.setId(program._contentuuid);
                node.setPid(parent.getId());
                node.setTitle(program._title_name);
                node.setActionUri(program._contentuuid);
                node.setContentType(program._contenttype);

                node.setParent(parent);

                parent.getChildrens().add(node);
            }
        }
    }

    private void setProgram(Node node, List<Program> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        LastMenuBean lastMenuBean = new LastMenuBean();
        lastMenuBean.setData(new LastMenuBean.DataBean());
        lastMenuBean.getData().setPrograms(list);

        node.setLastMenuBean(lastMenuBean);
        node.setPrograms(list);
        node.setRequest(true);
        for (Program p : list) {
            p.setParent(node);
        }
    }

    private void getLastData() {
        RequestMenuGroupData.getLastData(programSeries, new RequestMenuGroupData.DataListener() {
            @Override
            public void success(LastMenuBean lastBean) {
                if (menuGroup == null) return;
                MenuGroupPresenter.this.lastMenuBean = lastBean;
                if (lastBean == null || lastBean.getData() == null || lastBean.getData().getPrograms() == null) {
                    return;
                }
                setLastData();
            }
        });
    }

    private void setLastData() {
        if (!menuGroup.isAllNodeInit()) {
            handler.sendEmptyMessageDelayed(SET_LAST_DATA, 200);
            return;
        }

        try {
            LastMenuBean lastBean = lastMenuBean;
            String pid = lastBean.getData().getContentUUID();

            menuGroupIsInit = menuGroup.setLastProgram(lastBean, pid, contentUUID);
            menuGroup.setAppKeyAndChanelId(Constant.APP_KEY, Constant.CHANNEL_ID);
            playProgram = menuGroup.getPlayProgram();
            checkShowHinter();
        } catch (Exception e) {
            LogUtils.e(TAG, "exception:" + e.toString());
        }
    }

    /**
     * 栏目树初始化完成后,如果在播放广告，等待广告播放完毕在显示提示view
     * 如果未播广告并且是全屏就直接显示
     * 如果已经退出全屏就不需要显示
     */
    private void checkShowHinter() {
        if (NewTVLauncherPlayerViewManager.getInstance().isADPlaying()) {
            handler.sendEmptyMessageDelayed(WAIT_AD_END, 500);
        } else {
            if (NewTVLauncherPlayerViewManager.getInstance().isFullScreen()) {
                showHinter();
            }
        }
    }

    public void showHinter() {
        if (menuGroupIsInit) {
            Log.i(TAG, "showHinter: ");
            hintView.setVisibility(View.VISIBLE);
            hintArrowheadBigRight.setVisibility(View.VISIBLE);
            hintArrowheadSmallRight.setVisibility(View.VISIBLE);
            hintAnimator(hintArrowheadBigRight);
            hintAnimator(hintArrowheadSmallRight);
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e(TAG, "action:" + event.getAction() + ",keyCode=" + event.getKeyCode());

        if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (menuGroupIsInit && menuGroup.getVisibility() == View.VISIBLE) {
                        gone();
                        return true;
                    }
                    break;
            }

            /**
             * 适配讯码盒子
             * 正常盒子按返回键返回KeyEvent.KEYCODE_BACK
             * 讯码盒子非长按返回KeyEvent.KEYCODE_ESCAPE  长按返回KeyEvent.KEYCODE_ESCAPE KeyEvent.KEYCODE_BACK
             */
            if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA)) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_ESCAPE:
                        if (menuGroupIsInit && menuGroup.getVisibility() == View.VISIBLE) {
                            menuGroup.gone();
                            return true;
                        }
                        break;
                }
            }
        }
        /**
         * 如果正在播放广告,就不让点击栏目树
         */
        if (NewTVLauncherPlayerViewManager.getInstance().isADPlaying()) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MENU:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (menuGroupIsInit && menuGroup.getVisibility() == View.GONE) {
                        if (playProgram != null) {
                            menuGroup.show(playProgram);
                        } else {
                            menuGroup.show();
                        }
                        NewTVLauncherPlayerViewManager.getInstance().setShowingView
                                (NewTVLauncherPlayerView.SHOWING_PROGRAM_TREE);
                        setHintGone();
                        return true;
                    }
                    break;
            }
        }


        if (menuGroupIsInit && menuGroup.getVisibility() == View.VISIBLE) {
            send();
            menuGroup.dispatchKeyEvent(event);
            return true;
        } else {
            return false;
        }
    }

    private void send() {
        handler.removeMessages(MESSAGE_GONE);
        handler.sendEmptyMessageDelayed(MESSAGE_GONE, GONE_TIME);
    }

    @Override
    public void setLeftArrowHeadVisible(int visible) {
        hintArrowheadSmallLeft.setVisibility(visible);
        hintArrowheadBigLeft.setVisibility(visible);
        takePlace1.setVisibility(visible);
        takePlace2.setVisibility(visible);
    }

    @Override
    public void setRightArrowHeadVisible(int visible) {

    }

    public void setHintGone() {
        hintView.setVisibility(View.GONE);
        hintArrowheadBigRight.setVisibility(View.GONE);
        hintArrowheadSmallRight.setVisibility(View.GONE);
    }

    public View getRootView() {
        return rootView;
    }

    public boolean isShow() {
        return menuGroupIsInit && menuGroup.getVisibility() == View.VISIBLE;
    }

    public void gone() {
        NewTVLauncherPlayerViewManager.getInstance().setShowingView
                (NewTVLauncherPlayerView.SHOWING_NO_VIEW);
        handler.removeMessages(MESSAGE_GONE);
        menuGroup.gone();
    }

    private void hintAnimator(final View view) {
        ObjectAnimator translationX = new ObjectAnimator().ofFloat(view, "alpha", 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0);
        translationX.setDuration(5000);
        translationX.start();
        translationX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                hintView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
    }

    public boolean isCanShowMenuGroup() {
        if (menuGroupIsInit && menuGroup.getVisibility() == View.GONE) {
            return true;
        }
        return false;
    }

    public void addSelectListener(MenuGroup.OnSelectListener listener) {
        if (menuGroup != null) {
            menuGroup.addOnSelectListener(listener);
        }
    }


    @Override
    public void enterFullScreen() {
        if (menuGroupIsInit) {
            getProgramSeriesAndContentUUID();

            if(!updatePlayProgram(lastMenuBean)){
                /**
                 *  更新playProgram失败，说明当前播放的视频不在lastMenuBean中，需要重新请求数据
                 * 复用栏目树前N级，重新请求最后一级列表逻辑
                 */
                menuGroupIsInit = false;
                setHintGone();
                menuGroup.requestLastDataById(programSeries, new MenuGroup.RecreateListener() {
                    @Override
                    public void success(LastMenuBean lastMenuBean) {
                        if(updatePlayProgram(lastMenuBean)){
                            menuGroupIsInit = true;
                            menuGroup.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    checkShowHinter();
                                }
                            },100);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void exitFullScreen() {
        setHintGone();
    }

    /**
     * 根据lastMenuBean更新playProgram,从小屏切换大屏的时候需要更新，否则会显示错误
     * @param lastMenuBean
     * @return
     */
    public boolean updatePlayProgram(LastMenuBean lastMenuBean){
        List<Program> programs = lastMenuBean.getData().getPrograms();
        for (Program program : programs) {
            if (contentUUID.equals(program.getContentUUID())) {
                playProgram = program;
                return true;
            }
        }
        return false;
    }
}
