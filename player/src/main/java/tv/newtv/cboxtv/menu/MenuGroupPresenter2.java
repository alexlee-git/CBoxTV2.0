package tv.newtv.cboxtv.menu;

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
import com.newtv.cms.Request;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.GsonUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.menu.model.CategoryContent;
import tv.newtv.cboxtv.menu.model.CategoryTree;
import tv.newtv.cboxtv.menu.model.Content;
import tv.newtv.cboxtv.menu.model.DBProgram;
import tv.newtv.cboxtv.menu.model.LastMenuBean;
import tv.newtv.cboxtv.menu.model.LocalNode;
import tv.newtv.cboxtv.menu.model.Node;
import tv.newtv.cboxtv.menu.model.Program;
import tv.newtv.cboxtv.menu.model.SeriesContent;
import tv.newtv.cboxtv.player.IPlayProgramsCallBackEvent;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.player.BuildConfig;
import tv.newtv.player.R;

/**
 * Created by TCP on 2018/5/11.
 */

public class MenuGroupPresenter2 implements ArrowHeadInterface, IMenuGroupPresenter, ScreenInterface {
    private static final String TAG = "MenuGroupPresenter2";
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
    private String categoryId;
    private List<Node> rootNode;

    private boolean menuGroupIsInit = false;
    /**
     * 当前正在播放的节目
     */
    private Program playProgram;
    private SeriesContent seriesContent;


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
//                    setLastData();
                    break;
                case WAIT_AD_END:
                    checkShowHinter();
                    break;
            }
        }
    }

    public MenuGroupPresenter2(Context context) {
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
                com.newtv.cms.bean.Content content = program.getParent().getContent();
                if(content != null){
                    int index = program.getParent().getPrograms().indexOf(program);
                    NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(context,content,false,index,0);
                    menuGroup.gone();
                    setPlayerInfo(program);
                }else {
                    getSeries(program);
                }
            }
        });

        NewTVLauncherPlayerViewManager.getInstance().addListener(new IPlayProgramsCallBackEvent() {

            @Override
            public void onNext(SubContent info, int index, boolean isNext) {
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
     * @return
     */
    private boolean getProgramSeriesAndContentUUID() {
        com.newtv.cms.bean.Content programSeriesInfo = NewTVLauncherPlayerViewManager.getInstance().getProgramSeriesInfo();
        int typeIndex = NewTVLauncherPlayerViewManager.getInstance().getTypeIndex();
        int index = NewTVLauncherPlayerViewManager.getInstance().getIndex();

        if (programSeriesInfo == null) {
            Log.e(TAG, "programSeriesInfo == null");
            return false;
        }

        switch (programSeriesInfo.getContentType()){
            case Constant.CONTENTTYPE_PG:
                Log.i(TAG, "单节目不显示栏目树");
                break;
            case Constant.CONTENTTYPE_CP:
                programSeries = mySplit(getStringByPriority(programSeriesInfo.getTvContentIDs(), programSeriesInfo.getCsContentIDs(),programSeriesInfo.getCgContentIDs()));
                contentUUID = programSeriesInfo.getContentID();
                categoryId = mySplit(programSeriesInfo.getCategoryIDs());
                break;
//            case Constant.CONTENTTYPE_PS:
//            case Constant.CONTENTTYPE_CG:
            default:
                if(index != -1 && programSeriesInfo.getData() != null && index < programSeriesInfo.getData().size()){
                    programSeries = programSeriesInfo.getContentID();
                    contentUUID = programSeriesInfo.getData().get(index).getContentID();
                    categoryId = mySplit(programSeriesInfo.getCategoryIDs());
                }
                break;
        }

        if (TextUtils.isEmpty(programSeries) || TextUtils.isEmpty(contentUUID)) {
            Log.e(TAG, "programSeries or contentUUID can not empty,programSeries=" + programSeries + ",contentUUID=" + contentUUID
                    + ",typeIndex=" + typeIndex+","+programSeriesInfo);
            return false;
        }

        Log.i(TAG, "detailColumnUUID=" + programSeries);
        Log.i(TAG, "contentUUID=" + contentUUID);
        Log.i(TAG, "categoryId=" + categoryId);
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

        if(TextUtils.isEmpty(categoryId)){
            getCategoryId();
        }else {
            getCategoryTree();
        }
        searchDataInDB();
    }

    private void getCategoryId(){
        String leftString = contentUUID.substring(0, 2);
        String rightString = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        Request.INSTANCE.getContent()
                .getInfo(Libs.get().getAppKey(),Libs.get().getChannelId(),leftString,rightString,contentUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Log.i(TAG, "content: ");
                        Content content = GsonUtil.fromjson(responseBody.string(),Content.class);
                        if(content != null && content.data != null){
                            categoryId = mySplit(content.data.categoryIDs);
                            getCategoryTree();
                        }
                    }
                });
    }

    private void getCategoryTree() {
        Request.INSTANCE.getCategory().getCategoryTree(Libs.get().getAppKey(),Libs.get().getChannelId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Log.i(TAG, "CategoryTree: ");
                        CategoryTree categoryTree = GsonUtil.fromjson(responseBody.string(),CategoryTree.class);
                        if(categoryTree != null && categoryTree.data != null && categoryTree.data.size() > 0){
                            rootNode = categoryTree.data;
                            for(Node node : rootNode){
                                node.initParent();
                            }
                            getCategoryContent();
                        }
                    }
                });
    }

    private void getCategoryContent() {
        String contentUUID = categoryId;
        String leftString = contentUUID.substring(0, 2);
        String rightString = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        Request.INSTANCE.getCategory()
                .getCategoryContent(Libs.get().getAppKey(),Libs.get().getChannelId(),leftString,rightString,contentUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        Log.i(TAG, "CategoryContent: ");
                        CategoryContent categoryContent = GsonUtil.fromjson(responseBody.string(),CategoryContent.class);
                        if(categoryContent != null && categoryContent.data != null && categoryContent.data.size() > 0){
                            Node node = searchNodeById(categoryId);
                            node.addChild(categoryContent.data);
                            getSeriesContent();
                        }
                    }
                });
    }

    private void getSeriesContent(){
        String leftString = programSeries.substring(0, 2);
        String rightString = programSeries.substring(programSeries.length() - 2, programSeries.length());
        Request.INSTANCE.getContent()
                .getSubInfo(Libs.get().getAppKey(),Libs.get().getChannelId(),leftString,rightString,programSeries)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result = responseBody.string();
                        Log.i(TAG, "seriesContent: "+result);
                        SeriesContent seriesContent = GsonUtil.fromjson(result, SeriesContent.class);
                        if(seriesContent != null && seriesContent.data != null && seriesContent.data.size() > 0){
                            MenuGroupPresenter2.this.seriesContent = seriesContent;
                            menuGroup.addRootNodes(rootNode);
                            menuGroupIsInit = menuGroup.setLastProgram(seriesContent.data, programSeries, contentUUID);
                            playProgram = menuGroup.getPlayProgram();
                            Log.i(TAG, "accept: "+playProgram);
                            checkShowHinter();
                        }
                    }
                });
    }

    private Node searchNodeById(String id){
        for(Node node : rootNode){
            Node result = node.searchNode(id);
            if(result != null){
                return result;
            }
        }
        return null;
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
        collect.setRequest(true);
        collect.setParent(root);

        Node history = new Node();
        history.setPid("root");
        history.setTitle("我的观看记录");
        history.setId("attention");
        history.setRequest(true);
        history.setParent(root);

        Node subscribe = new Node();
        subscribe.setPid("root");
        subscribe.setTitle("我的订阅");
        subscribe.setId("subscribe");
        subscribe.setRequest(true);
        subscribe.setParent(root);

        List<Node> list = new ArrayList<>();
        list.add(collect);
        list.add(history);
        list.add(subscribe);
        root.setChild(list);

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

                Node node = new LocalNode();
                node.setId(program._contentuuid);
                node.setPid(parent.getId());
                node.setTitle(program._title_name);
                node.setActionUri(program._contentuuid);
                node.setContentType(program._contenttype);

                node.setParent(parent);
                parent.getChild().add(node);
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
            updatePlayProgram();

//            if(!updatePlayProgram(lastMenuBean)){
//                /**
//                 *  更新playProgram失败，说明当前播放的视频不在lastMenuBean中，需要重新请求数据
//                 * 复用栏目树前N级，重新请求最后一级列表逻辑
//                 */
//                menuGroupIsInit = false;
//                setHintGone();
//                menuGroup.requestLastDataById(programSeries, new MenuGroup.RecreateListener() {
//                    @Override
//                    public void success(LastMenuBean lastMenuBean) {
//                        if(updatePlayProgram(lastMenuBean)){
//                            menuGroupIsInit = true;
//                            menuGroup.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    checkShowHinter();
//                                }
//                            },100);
//                        }
//                    }
//                });
//            }
        }
    }

    @Override
    public void exitFullScreen() {
        setHintGone();
    }

    public boolean updatePlayProgram(){
        if(seriesContent != null && seriesContent.data != null
                && seriesContent.data.size() > 0){
            for(Program program : seriesContent.data){
                if(contentUUID.equals(program.getContentID())){
                    playProgram = program;
                    return true;
                }
            }
        }
        return false;
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

    private void getSeries(final Program program){
        String contentId = program.getParent().getId();
        String leftString = contentId.substring(0, 2);
        String rightString = contentId.substring(contentId.length() - 2, contentId.length());
        Request.INSTANCE.getContent()
                .getInfo(Libs.get().getAppKey(),Libs.get().getChannelId(),leftString,rightString,contentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String result = responseBody.string();
                        Log.i(TAG, "accept: "+result);
                        JSONObject jsonObject = new JSONObject(result);
                        String data = jsonObject.getString("data");
                        com.newtv.cms.bean.Content content = GsonUtil.fromjson(data, com.newtv.cms.bean.Content.class);
                        program.getParent().setContent(content);
                        List<Program> programs = program.getParent().getPrograms();
                        List<SubContent> subContents = new ArrayList<>();
                        for(Program p : programs){
                            subContents.add(p.convertProgramsInfo());
                        }
                        content.setData(subContents);

                        int index = programs.indexOf(program);
                        NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(context,content,false,index,0);
                        menuGroup.gone();
                        setPlayerInfo(program);
                    }
                });
    }

    private String mySplit(String str){
        if(TextUtils.isEmpty(str)){
           return "";
        }
        String[] split = str.split("\\|");
        return split[0];
    }

    private String getStringByPriority(String... str){
        for(String s : str){
            if(!TextUtils.isEmpty(s)){
                return s;
            }
        }
        return "";
    }
}
