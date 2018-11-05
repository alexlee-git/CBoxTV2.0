package tv.newtv.cboxtv.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.newtv.cms.Request;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.GsonUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScreenUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.menu.model.CategoryContent;
import tv.newtv.cboxtv.menu.model.LastMenuBean;
import tv.newtv.cboxtv.menu.model.Node;
import tv.newtv.cboxtv.menu.model.Program;
import tv.newtv.cboxtv.menu.model.SeriesContent;
import tv.newtv.player.R;
import tv.icntv.icntvplayersdk.Constants;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;

/**
 * Created by TCP on 2018/4/17.
 */

public class MenuGroup extends LinearLayout implements MenuRecyclerView.OnKeyEvent {
    private static final String IGNORE_TYPE = "OPEN_PAGE";
    private static final int REQUEST_MENU_FOCUS = 1;
    private static final int REQUEST_MENU_FIRST_FOCUS = 2;
    private static final int REQUEST_MENU_PATH_VIEW_FOCUS = 3;
    private static final int PADDING_TOP = ScreenUtils.dp2px(20);
    /**
     * 无法测量到recyclerView宽度的时候，临时使用这个值为recyclerView的宽度
     */
    private int DEFAULT_WIDTH =0;
    //显示几列
    private static final int VISIBLE_COLUMN = 2;
    private static String TAG = "MenuGroup";
    private String detailcontentUUID;
    /**
     * 树中所有节点集合
     */
    private List<Node> allNodes = new ArrayList<>();
    /**
     * 树中所有根节点集合
     */
    private List<Node> rootNodes = new ArrayList<>();
    /**
     * 除了最后一级的所有RecyclerView集合
     */
    private List<MenuRecyclerView> listViews = new ArrayList<>();
    /**
     * 最后一级的RcyclerView
     */
    private MenuRecyclerView lastListView;
    /**
     * 最后一级RecyclerView中的数据
     */
    private List<Program> lastProgram;
    /**
     * 当前正在播放视频对应的实体类
     */
    private Program playProgram;

    private Node currentNode;
    private int recyclerViewWidth;
    //动画队列
    private List<MenuGroup.AnimEntity> animList = new ArrayList<>();
    private boolean isFinshAnim = true;
    private float currentX;
    private float MIN_X;
    private MyHandler focusHandler = new MyHandler(this);

    private List<OnSelectListener> onSelectListenerList = new ArrayList<>();

    private ArrowHeadInterface arrowHead;

    private ImageView iBigArrowHead;
    private ImageView iSmallArrowHead;

    /**
     * 是否添加了gone动画
     * 如果添加了，那么在动画执行结束之前不可以响应任何事件，否则会导致位移错误
     */
    private boolean addGoneAnimator = false;
    /**
     * 存储所有细线的list
     */
    private List<ImageView> lineList = new ArrayList<>();

    /**
     * AllNode初始化是否完成
     */
    private boolean allNodeInit = false;
    private Context mcontext;

    public MenuGroup(Context context) {
        this(context, null);
        mcontext =context;
    }

    public MenuGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        mcontext =context;
    }

    @SuppressLint("ResourceAsColor")
    public MenuGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mcontext =context;
        setOrientation(HORIZONTAL);
        recyclerViewWidth = DEFAULT_WIDTH = getResources().getDimensionPixelOffset(R.dimen.width_430px);
    }

    public void release() {
        removeAllViews();
        if(allNodes != null){
            allNodes.clear();
            allNodes = null;
        }
        if(rootNodes != null){
            rootNodes.clear();
            rootNodes = null;
        }
        if (listViews != null) {
            listViews.clear();
            listViews = null;
        }
        lastListView = null;
        if (lastProgram != null) {
            lastProgram.clear();
            lastProgram = null;
        }
        currentNode = null;
        playProgram = null;
        if (animList != null) {
            animList.clear();
            animList = null;
        }
        arrowHead = null;
        if (focusHandler != null) {
            focusHandler.removeCallbacksAndMessages(null);
            focusHandler = null;
        }
        if (lineList != null) {
            lineList.clear();
            lineList = null;
        }
        if (onSelectListenerList != null) {
            onSelectListenerList.clear();
            onSelectListenerList = null;
        }
    }

    public void addOnSelectListener(OnSelectListener onSelectListener) {
        onSelectListenerList.add(onSelectListener);
    }

    public void setArrowHead(ArrowHeadInterface arrowHead) {
        this.arrowHead = arrowHead;
    }

    public void setAllNodes(final List<Node> aNodes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Node node : aNodes) {
                        if (node.getActionType().equals(IGNORE_TYPE)) {
                            continue;
                        }
                        MenuGroup.this.allNodes.add(node);
                    }

                    for (Node node : allNodes) {
                        //获取root节点
                        if (TextUtils.isEmpty(node.getPid()) && !"我的".equals(node.getTitle())) {
                            rootNodes.add(node);
                        }
                    }

                    for (int i = 0; i < allNodes.size(); i++) {
                        Node n = allNodes.get(i);
                        for (int j = i + 1; j < allNodes.size(); j++) {
                            Node m = allNodes.get(j);
                            if (n.getId().equals(m.getPid())) {
                                n.getChild().add(m);
                                m.setParent(n);
                            } else if (m.getId().equals(n.getPid())) {
                                m.getChild().add(n);
                                n.setParent(m);
                            }
                        }
                    }
                    allNodeInit = true;
                } catch (Exception e) {
                    LogUtils.e(TAG, e.toString());
                }
            }
        }).start();
    }

    public void addRootNodes(List<Node> nodes){
        this.rootNodes.addAll(nodes);
        for(Node node : nodes){
            allNodes.addAll(node.getNodes());
        }
    }

    public void addNodeToRoot(Node node) {
        for (int i=0;i<rootNodes.size();i++){
            if (rootNodes.get(i).getTitle().equals(node.getTitle()) && node.getTitle().equals("我的")){
                rootNodes.remove(rootNodes.get(i));
            }
        }
        if (node.getTitle().equals("我的")){
            rootNodes.add(0,node);
        }
    }

    public boolean setLastProgram(LastMenuBean lastMenuBean, String pId, String detailContentUUID) {
        if (lastMenuBean == null || lastMenuBean.getData() == null || lastMenuBean.getData()
                .getPrograms() == null) {
            Log.e(TAG, "lastMenuBean or lastMenuBean.getData() or lastMenuBean.getData()" +
                    ".getPrograms() is null , MenuGroup initView fail");
            return false;
        }

        this.lastProgram = lastMenuBean.getData().getPrograms();
        this.detailcontentUUID = detailContentUUID;
        for (Node node : allNodes) {
            if (node.getActionUri().equals(pId)) {
                node.setPrograms(lastProgram);
                node.setLastMenuBean(lastMenuBean);
                currentNode = node;
                for (Program p : lastProgram) {
                    p.setParent(node);
                    if (p.getContentID().equals(detailContentUUID)) {
                        playProgram = p;
                    }
                }
                break;
            }
        }
        if (currentNode == null) {
            Log.e(TAG, "currentNode can not null");
            return false;
        }
        initView();
        return true;
    }

    public boolean setLastProgram(List<Program> lastProgram, String pId, String detailContentUUID) {
        this.lastProgram = lastProgram;
        this.detailcontentUUID = detailContentUUID;
        for (Node node : allNodes) {
            if (TextUtils.equals(node.getId(),pId)) {
                node.setPrograms(lastProgram);
                currentNode = node;
                for (Program p : lastProgram) {
                    p.setParent(node);
                    if (p.getContentID().equals(detailContentUUID)) {
                        playProgram = p;
                    }
                }
                break;
            }
        }
        if (currentNode == null) {
            Log.e(TAG, "currentNode can not null");
            return false;
        }
        initView();
        return true;
    }

    private void initView() {
        int level = currentNode.getLevel();
        Node currentNode = this.currentNode;
        for (int i = 0; i <= level; i++) {
            MenuRecyclerView lv = new MenuRecyclerView(getContext());
            MenuRecyclerAdapter adapter;
            if (i == level) {
                adapter = new MenuRecyclerAdapter(getContext(), rootNodes, currentNode.getId());
            } else {
                adapter = new MenuRecyclerAdapter(getContext(), currentNode.getParent()
                        .getChild(), currentNode.getId());
            }
            lv.setAdapter(adapter);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lv.setLayoutParams(layoutParams);
            lv.setLayoutManager(new LinearLayoutManager(getContext()));
            lv.setPadding(0, PADDING_TOP, 0, 0);
            lv.setLevel(currentNode.getLevel());
            lv.setKeyEvent(this);

            listViews.add(0, lv);
            currentNode = currentNode.getParent();
        }

        lastListView = new MenuRecyclerView(getContext());
        LastMenuRecyclerAdapter adapter = new LastMenuRecyclerAdapter(getContext(), lastProgram,
                detailcontentUUID);
        lastListView.setAdapter(adapter);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lastListView.setLayoutParams(layoutParams);
        lastListView.setLayoutManager(new LinearLayoutManager(getContext()));
        lastListView.setPadding(0, PADDING_TOP, 0, 0);
        lastListView.setLevel(MenuRecyclerView.MAX_LEVEL);
        lastListView.setKeyEvent(this);

        for (int i = 0; i < listViews.size(); i++) {
            addView(listViews.get(i));
            ImageView imageView = createSplitLine();
            lineList.add(imageView);
            imageView.setTag(i);
            addView(imageView);
        }

        addView(lastListView);
        iSmallArrowHead = createImageView(R.drawable.hint_arrowhead_small_right);
        iBigArrowHead = createImageView(R.drawable.hint_arrowhead_big_right);
        addView(iSmallArrowHead);
        addView(iBigArrowHead);
        measureViewWidth(lastListView);
        checkFocus();

        if (arrowHead != null) {
            arrowHead.setRightArrowHeadVisible(View.VISIBLE);
        }
    }


    @Override
    public void keyEvent(int level, int keyCode, int position, View focusView) {

        if (addGoneAnimator || position == -1 || !isFinshAnim) {
            return;
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                //1.获取上一级的recyclerView和adapter
                //如果当前level不为MAX_LEVEL，重新设置lastRecyclerView
                int previous = -1;
                if (level == MenuRecyclerView.MAX_LEVEL) {
                    previous = lastProgram.get(0).getParent().getLevel();
                } else {
                    resetLastRecyclerView();
                    previous = level - 1;
                }
                if (previous >= listViews.size() || previous < 0) {
                    break;
                }
                MenuRecyclerView menuRecyclerView = listViews.get(previous);
                MenuRecyclerAdapter menuRecyclerViewAdapter = (MenuRecyclerAdapter)
                        menuRecyclerView.getAdapter();

                //2.让上一级的selectView获取焦点
                if (menuRecyclerViewAdapter.getSelectView() != null) {
                    menuRecyclerViewAdapter.getSelectView().requestFocus();
                } else {
                    menuRecyclerView.scrollToPosition(menuRecyclerViewAdapter
                            .calculatePlayIdPosition(0));
                    Message msg = Message.obtain();
                    msg.obj = menuRecyclerViewAdapter;
                    msg.what = REQUEST_MENU_FOCUS;
                    focusHandler.sendMessageDelayed(msg, 60);
                }

                //3.设置相应recyclerView为GONE
                setRecyclerViewsGoneByLevel(level);
                //计算是否需要滚动
                if (currentX < -MIN_X && level != MenuRecyclerView.MAX_LEVEL) {
                    float current = currentX;
                    currentX = currentX + recyclerViewWidth;
                    startAnim(new AnimEntity(current, currentX));
                }
                checkHeadArrow();
                checkLine();
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                keyRightSelectPlay(level, keyCode, position, focusView);
//                keyRightSelectFirst(level,keyCode,position,focusView);
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                //更新下一个MenuRecyclerView中的数据
                MenuRecyclerView menuRecyclerViewDown = getMenuRecyclerViewByLevel(level);
                MenuRecyclerAdapter adapterDown = (MenuRecyclerAdapter) menuRecyclerViewDown
                        .getAdapter();
                Node itemDown = adapterDown.getItem(position + 1);
                refreshRecyclerViewDataBylevel(itemDown, level + 1);
                checkHeadArrow();
                checkLine();
                break;

            case KeyEvent.KEYCODE_DPAD_UP:
                MenuRecyclerView menuRecyclerViewUp = getMenuRecyclerViewByLevel(level);
                MenuRecyclerAdapter adapterUp = (MenuRecyclerAdapter) menuRecyclerViewUp
                        .getAdapter();
                Node itemUp = adapterUp.getItem(position - 1);
                refreshRecyclerViewDataBylevel(itemUp, level + 1);
                checkHeadArrow();
                checkLine();
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Program program = lastProgram.get(position);
                playProgram = program;
                setPlayId(program);
                if (onSelectListenerList.size() > 0) {
                    for (OnSelectListener l : onSelectListenerList) {
                        l.select(program);
                    }
                }
                break;
        }
    }

    /**
     * 按右鍵时 如果当前显示数据包含正在播放的路径,那么选中该条数据
     * 如果不包含，那么选中第一条
     *
     * @param level
     * @param keyCode
     * @param position
     * @param focusView
     */
    private void keyRightSelectPlay(int level, int keyCode, int position, View focusView) {
        //选中下一个MenuRecyclerView的play条目或者第一条,并更新下下个MenuRecyclerView中的数据
        MenuRecyclerView menuRecyclerViewRight = getMenuRecyclerViewByLevel(level);
        MenuRecyclerAdapter adapter = (MenuRecyclerAdapter) menuRecyclerViewRight.getAdapter();

        //根据数据判断下一个recyclerView是不是最后一个
        Node item = adapter.getItem(position);
        int playIdPosition = 0;
        if (!item.isLeaf()) {
            if(item.getChild().size() == 0){
                return;
            }
            adapter.setSelectView(focusView);
            MenuRecyclerView menuRecyclerViewByLevel = getMenuRecyclerViewByLevel(level + 1);

            playIdPosition = ((MenuRecyclerAdapter) menuRecyclerViewByLevel.getAdapter())
                    .calculatePlayIdPosition(-1);

            if (playIdPosition != -1 && playIdPosition < item.getChild().size()) {
                if (!isPositionShow(menuRecyclerViewByLevel, playIdPosition)) {
                    menuRecyclerViewByLevel.scrollToPosition(playIdPosition);
                }
                sendDelayToFocusHandler(menuRecyclerViewByLevel.getAdapter(), REQUEST_MENU_PATH_VIEW_FOCUS);
                refreshRecyclerViewDataBylevel(item.getChild().get(playIdPosition), level + 2);
            } else {
                menuRecyclerViewByLevel.scrollToPosition(0);
                sendDelayToFocusHandler(menuRecyclerViewByLevel.getAdapter(),
                        REQUEST_MENU_FIRST_FOCUS);
                refreshRecyclerViewDataBylevel(item.getChild().get(0), level + 2);
            }


            float current = currentX;
            currentX = currentX - recyclerViewWidth;
            startAnim(new AnimEntity(current, currentX));

        } else if (item.getPrograms() != null && item.getPrograms().size() > 0) {
            //是最后一级并且有program
            adapter.setSelectView(focusView);

            playIdPosition = ((LastMenuRecyclerAdapter) lastListView.getAdapter())
                    .calculatePlayIdPosition(-1);
            if (playIdPosition != -1) {
                if (!isPositionShow(lastListView, playIdPosition)) {
                    lastListView.scrollToPosition(playIdPosition);
                }
                sendDelayToFocusHandler(lastListView.getAdapter(), REQUEST_MENU_PATH_VIEW_FOCUS);
            } else {
                lastListView.scrollToPosition(0);
                sendDelayToFocusHandler(lastListView.getAdapter(), REQUEST_MENU_FIRST_FOCUS);
            }

            lastListView.setVisibility(View.VISIBLE);
        } else {
            //是最后一级但是没有program
            lastListView.setVisibility(View.VISIBLE);
            focusView.requestFocus();
        }
        checkHeadArrow();
        checkLine();
    }

    /**
     * 选中右边列表第一条数据逻辑
     *
     * @param level
     * @param keyCode
     * @param position
     * @param focusView
     */
    private void keyRightSelectFirst(int level, int keyCode, int position, View focusView) {
        //选中下一个MenuRecyclerView的第一条,并更新下下个MenuRecyclerView中的数据
        MenuRecyclerView menuRecyclerViewRight = getMenuRecyclerViewByLevel(level);
        MenuRecyclerAdapter adapter = (MenuRecyclerAdapter) menuRecyclerViewRight.getAdapter();

        //根据数据判断下一个recyclerView是不是最后一个
        Node item = adapter.getItem(position);
        if (!item.isLeaf()) {
            adapter.setSelectView(focusView);
            MenuRecyclerView menuRecyclerViewByLevel = getMenuRecyclerViewByLevel(level + 1);
            menuRecyclerViewByLevel.scrollToPosition(0);

            Message msg = Message.obtain();
            msg.obj = menuRecyclerViewByLevel.getAdapter();
            msg.what = REQUEST_MENU_FIRST_FOCUS;
            focusHandler.sendMessageDelayed(msg, 60);

            refreshRecyclerViewDataBylevel(item.getChild().get(0), level + 2);

            float current = currentX;
            currentX = currentX - recyclerViewWidth;
            startAnim(new AnimEntity(current, currentX));

        } else if (item.getPrograms() != null && item.getPrograms().size() > 0) {
            //是最后一级并且有program
            adapter.setSelectView(focusView);
            lastListView.setVisibility(View.VISIBLE);
            lastListView.scrollToPosition(0);

            Message msg = Message.obtain();
            msg.obj = lastListView.getAdapter();
            msg.what = REQUEST_MENU_FIRST_FOCUS;
            focusHandler.sendMessageDelayed(msg, 60);
        } else {
            //是最后一级但是没有program
            lastListView.setVisibility(View.VISIBLE);
            focusView.requestFocus();
        }
        checkHeadArrow();
        checkLine();
    }

    private void sendDelayToFocusHandler(Object adapter, int what) {
        Message msg = Message.obtain();
        msg.obj = adapter;
        msg.what = what;
        focusHandler.sendMessageDelayed(msg, 60);
    }

    private void refreshRecyclerViewDataBylevel(Node node, int level) {
        //刷新数据分四种情况
        //1.不是叶子节点
        //2.是叶子节点并且programs != null
        //3.是叶子节点并且去网络请求数据了，但是获取到的programs为空
        //4.是叶子节点还未去网络请求数据
        if (!node.isLeaf()) {
            MenuRecyclerView menuRecyclerViewByLevel = getMenuRecyclerViewByLevel(level);
            MenuRecyclerAdapter nextAdapter = (MenuRecyclerAdapter) menuRecyclerViewByLevel
                    .getAdapter();
            nextAdapter.setData(node.getChild());
            if(node.getChild().size() == 0 && !node.isRequest()){
                //请求数据
                getNodeData(node,menuRecyclerViewByLevel);
            }else {
                menuRecyclerViewByLevel.scrollToPosition(0);
                resetLastRecyclerView();
            }

        } else if (node.getPrograms() != null && node.getPrograms().size() > 0) {
            lastListView.setVisibility(View.VISIBLE);
            lastListView.setTag(node.getId());
            LastMenuRecyclerAdapter nextAdapter = (LastMenuRecyclerAdapter) lastListView
                    .getAdapter();
            nextAdapter.setData(node.getPrograms());
            lastListView.scrollToPosition(0);
            lastProgram = node.getPrograms();
            setRecyclerViewsGoneByLevel(level - 1);

        } else if (node.isRequest()) {
            //已经请求过数据，但是数据为空
            lastListView.setVisibility(View.VISIBLE);
            lastListView.setTag(node.getId());
            resetLastRecyclerViewData();
            setRecyclerViewsGoneByLevel(level - 1);
        } else {
            lastListView.setVisibility(View.VISIBLE);
            lastListView.setTag(node.getId());
            resetLastRecyclerViewData();
            getLastData(node);
            setRecyclerViewsGoneByLevel(level - 1);
        }
    }

    private void getNodeData(final Node node, final MenuRecyclerView recyclerView) {
        recyclerView.setTag(node.getId());

        String contentUUID = node.getId();
        String leftString = contentUUID.substring(0, 2);
        String rightString = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        Request.INSTANCE.getCategory()
                .getCategoryContent(Libs.get().getAppKey(), Libs.get().getChannelId(),leftString,rightString,contentUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        CategoryContent categoryContent = GsonUtil.fromjson(responseBody.string(),CategoryContent.class);
                        if(categoryContent != null && categoryContent.data != null && categoryContent.data.size() > 0){
                            node.addChild(categoryContent.data);
                            node.setRequest(true);
                            node.initParent();
                            if(recyclerView.getTag().equals(node.getId()) && View.VISIBLE == recyclerView.getVisibility()){
                                MenuRecyclerAdapter nextAdapter = (MenuRecyclerAdapter) recyclerView.getAdapter();
                                nextAdapter.setData(node.getChild());
                                recyclerView.scrollToPosition(0);
                                resetLastRecyclerView();
                            }
                        }
                    }
                });
    }

    private void resetLastRecyclerView() {
        lastListView.setVisibility(View.GONE);
    }

    private void resetLastRecyclerViewData() {
        LastMenuRecyclerAdapter adapter = (LastMenuRecyclerAdapter) lastListView.getAdapter();
        adapter.setData(new ArrayList<Program>());
    }

    /**
     * 获取前N级的recyclerView 不包括最后一级
     *
     * @param level
     * @return
     */
    public MenuRecyclerView getMenuRecyclerViewByLevel(int level) {
        MenuRecyclerView result = null;
        int size = listViews.size();
        if (level < size) {
            result = listViews.get(level);
        } else {
            result = createRecyclerView(level);
        }
        result.setVisibility(View.VISIBLE);
        return result;
    }

    public MenuRecyclerView createRecyclerView(int level) {
        MenuRecyclerView lv = new MenuRecyclerView(getContext());
        MenuRecyclerAdapter adapter = new MenuRecyclerAdapter(getContext(), new ArrayList<Node>()
                , "");
        lv.setAdapter(adapter);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lv.setLayoutParams(layoutParams);
        lv.setLayoutManager(new LinearLayoutManager(getContext()));
        lv.setPadding(0, PADDING_TOP, 0, 0);
        lv.setLevel(level);
        lv.setKeyEvent(this);

        listViews.add(lv);
        addView(lv, level * 2);
        ImageView line = createSplitLine();
        line.setTag(level);
        lineList.add(line);
        addView(line, level * 2 + 1);
        return lv;
    }

    private void getLastData(Node node){
        getLastData2(node,null);
    }

    private void getLastData2(final Node node,final RecreateListener l){
        lastListView.setTag(node.getId());
        final String programSeries = node.getId();
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
                            node.setPrograms(seriesContent.data);
                            for(Program p : seriesContent.data){
                                p.setParent(node);
                            }

                            if (lastListView!=null&&lastListView.getTag().equals(node.getId())) {
                                lastProgram = seriesContent.data;
                                LastMenuRecyclerAdapter adapter =
                                        (LastMenuRecyclerAdapter) lastListView.getAdapter();
                                adapter.setData(lastProgram);
                            }
                            node.setRequest(true);

                            if(l != null){
                                l.success(null);
                            }

                        }
                    }
                });
    }


    private void getLastData(final Node node, final RecreateListener l){
        lastListView.setTag(node.getId());

        String actionUri = node.getActionUri();
        if (TextUtils.isEmpty(actionUri))
            return;
        RequestMenuGroupData.getLastData(actionUri, new RequestMenuGroupData.DataListener() {
            @Override
            public void success(LastMenuBean lastMenuBean) {
                if (lastMenuBean != null && lastMenuBean.getData() != null &&
                        lastMenuBean.getData().getPrograms() != null) {
                    List<Program> programs = lastMenuBean.getData().getPrograms();
                    node.setLastMenuBean(lastMenuBean);

                    node.setPrograms(programs);
                    for (Program p : programs) {
                        p.setParent(node);
                    }
                    if (lastListView!=null&&lastListView.getTag().equals(node.getId())) {
                        lastProgram = programs;
                        LastMenuRecyclerAdapter adapter =
                                (LastMenuRecyclerAdapter) lastListView.getAdapter();
                        adapter.setData(lastProgram);
                    }
                    node.setRequest(true);

                    if(l != null){
                        l.success(lastMenuBean);
                    }
                }
            }
        });
    }

    /**
     * 测试使用的
     *
     * @param node
     */
    private void randomData(Node node) {
        List<Program> programs = new ArrayList<>();
        int rand = new Random().nextInt(100);
        for (int i = 0; i < rand; i++) {
            Program p = new Program();
            p.setParent(node);
            p.setTitle(node.getTitle() + i * 3);
            p.setContentUUID("");
            programs.add(p);
        }
        node.setPrograms(programs);
        lastProgram = programs;

        LastMenuRecyclerAdapter adapter = (LastMenuRecyclerAdapter) lastListView.getAdapter();
        lastListView.scrollToPosition(0);
        adapter.setData(lastProgram);
    }

    /**
     * 设置level之后的recyclerView为GONE,不包含当前level
     *
     * @param level
     */
    private void setRecyclerViewsGoneByLevel(int level) {
        if (level == MenuRecyclerView.MAX_LEVEL)
            return;
        int size = listViews.size();
        for (int i = level + 1; i < size; i++) {
            MenuRecyclerView menuRecyclerView = listViews.get(i);
            menuRecyclerView.setVisibility(View.GONE);
        }
    }

    private void measureViewWidth(final View view) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerViewWidth = view.getWidth();
                if (recyclerViewWidth == 0) {
                    recyclerViewWidth = DEFAULT_WIDTH;
                }
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void checkFocus() {
        final ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                LastMenuRecyclerAdapter lastAdapter = getLastAdapter();
                if (!lastAdapter.isInit()) {
                    lastListView.scrollToPosition(lastAdapter.calculatePlayIdPosition(0));
                }

                viewTreeObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void startAnim(MenuGroup.AnimEntity entity) {
        animList.add(entity);
        startAnim();
    }

    private void startAnim() {
        if (isFinshAnim) {
            if (animList != null && animList.size() > 0) {
                isFinshAnim = false;
                startAnimator(animList.get(0));
            }
        }
    }

    private void startAnimator(final MenuGroup.AnimEntity values) {
        ObjectAnimator translationX = new ObjectAnimator().ofFloat(this, "translationX", values
                .x, values.toX);
        translationX.setDuration(400);
        translationX.start();
        translationX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isFinshAnim = true;
                if (animList != null && animList.size() > 0) {
                    animList.remove(0);
                }
                if (values.gone) {
                    MenuGroup.this.setVisibility(View.GONE);
                    if (arrowHead != null) {
                        arrowHead.setRightArrowHeadVisible(View.VISIBLE);
                        arrowHead.setLeftArrowHeadVisible(View.GONE);
                    }
                    addGoneAnimator = false;
                }
                startAnim();

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
    }

    public void show() {
        show(playProgram);
    }

    public void show(Program playProgram) {
        if (playProgram != null && playProgram.getParent() != null) {
            this.currentNode = playProgram.getParent();

            int size = currentNode.getLevel() + 2;
            currentX = -(size * recyclerViewWidth);
            showView(playProgram);
            checkFocus();
        }
    }

    private void showView(Program playProgram) {
        lastProgram = currentNode.getPrograms();
        LastMenuRecyclerAdapter lastAdapter = (LastMenuRecyclerAdapter) lastListView.getAdapter();
        lastAdapter.setData(lastProgram, playProgram);

        int level = currentNode.getLevel();
        for (int i = level; i > 0; i--) {
            MenuRecyclerView menuRecyclerView = getMenuRecyclerViewByLevel(i);
            MenuRecyclerAdapter adapter = (MenuRecyclerAdapter) menuRecyclerView.getAdapter();
            adapter.setData(currentNode.getParent().getChild(), currentNode.getId());
            currentNode = currentNode.getParent();
        }
        MenuRecyclerView firstMenu = getMenuRecyclerViewByLevel(0);
        MenuRecyclerAdapter adapter = (MenuRecyclerAdapter) firstMenu.getAdapter();
        adapter.setData(rootNodes, currentNode.getId());

        lastListView.setVisibility(View.VISIBLE);
        setRecyclerViewsGoneByLevel(level);

        setVisibility(View.VISIBLE);
        visibleAnimator();
        if (arrowHead != null) {
            arrowHead.setRightArrowHeadVisible(View.GONE);
        }
        checkHeadArrow();
    }

    public void gone() {
        if (addGoneAnimator) {
            return;
        }
        addGoneAnimator = true;
        //播放消失动画
        goneAnimator();


    String    duration = mcontext.getSharedPreferences("durationConfig", Context.MODE_PRIVATE).getString("duration", "");
        if (duration !=null)
            LogUploadUtils.uploadLog(Constant.FLOATING_LAYER, "15,"+playProgram.getSeriesSubUUID()+","+playProgram.getContentUUID()+",0,0,"+   Integer.parseInt(duration)*60*1000+","+NewTVLauncherPlayerViewManager.getInstance().getCurrentPosition()+","+Constants.vodPlayId);

    }

    private int getVisibleNumber() {
        int result = 0;
        int size = listViews.size();
        for (int i = 0; i < size; i++) {
            MenuRecyclerView menuRecyclerView = listViews.get(i);
            result = menuRecyclerView.getVisibility() == View.VISIBLE ? ++result : result;
        }
        result = lastListView.getVisibility() == View.VISIBLE ? ++result : result;
        return result;
    }

    private void visibleAnimator() {
        float current = currentX;
        currentX = currentX + recyclerViewWidth * VISIBLE_COLUMN;
        startAnim(new AnimEntity(current, currentX));
    }

    private void goneAnimator() {
        float current = currentX;
        currentX = currentX - recyclerViewWidth * VISIBLE_COLUMN;
        startAnim(new AnimEntity(current, currentX, true));
    }

    private void setPlayId(Program program) {
        getLastAdapter().setPlayId(program);
        Node currentNode = program.getParent();
        int level = currentNode.getLevel();
        for (int i = level; i >= 0; i--) {
            MenuRecyclerView menuRecyclerView = getMenuRecyclerViewByLevel(i);
            MenuRecyclerAdapter adapter = (MenuRecyclerAdapter) menuRecyclerView.getAdapter();
            adapter.setPlayId(currentNode.getId());
            currentNode = currentNode.getParent();
        }

        int size = listViews.size();
        if(level + 1 < size){
            for(int i=level+1;i<size;i++){
                MenuRecyclerView menuRecyclerView = listViews.get(i);
                MenuRecyclerAdapter adapter = (MenuRecyclerAdapter) menuRecyclerView.getAdapter();
                adapter.setPlayId("");

            }
        }
    }

    private LastMenuRecyclerAdapter getLastAdapter() {
        LastMenuRecyclerAdapter lastAdapter = (LastMenuRecyclerAdapter) lastListView.getAdapter();
        return lastAdapter;
    }

    private ImageView createSplitLine() {
        ImageView image = new ImageView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(1, ViewGroup
                .LayoutParams.MATCH_PARENT);
        image.setLayoutParams(layoutParams);
        image.setBackgroundResource(R.drawable.split_line);
        return image;
    }

    private ImageView createImageView(int resId) {
        ImageView image = new ImageView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        image.setLayoutParams(layoutParams);
        image.setBackgroundResource(resId);
        return image;
    }

    private void setHeadArrowVisible(int visible) {
        iSmallArrowHead.setVisibility(visible);
        iBigArrowHead.setVisibility(visible);
    }

    private void checkHeadArrow() {
        if (arrowHead == null)
            return;
        if (lastListView.getVisibility() == VISIBLE) {
            setHeadArrowVisible(View.GONE);
        } else {
            setHeadArrowVisible(View.VISIBLE);
        }

        int number = getVisibleNumber();
        if (number > VISIBLE_COLUMN) {
            arrowHead.setLeftArrowHeadVisible(View.VISIBLE);
        } else {
            arrowHead.setLeftArrowHeadVisible(View.GONE);
        }
    }

    private void checkLine() {
        //N个RecyclerView只显示N-1个线
        int number = getVisibleNumber() - 1;
        int size = lineList.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = lineList.get(i);
            if (i < number) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
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

    public Program getPlayProgram() {
        return playProgram;
    }

    public boolean isAllNodeInit() {
        return allNodeInit;
    }

    public boolean isFinshAnim() {
        return isFinshAnim;
    }

    /**
     * 根据id查找node
     * @param id
     * @return
     */
    private Node getNodeById(String id){
        Node result = null;
        for(Node node : allNodes){
            if(node.getActionUri().equals(id)){
                result = node;
                break;
            }
        }
        return result;
    }

    public void requestLastDataById(String id,RecreateListener l){
        Node node = getNodeById(id);
        if(node != null && node.getLastMenuBean() == null){
            getLastData(node,l);
        } else if(node != null){
            if(l != null){
                l.success(node.getLastMenuBean());
            }
        }
    }

    public interface RecreateListener{

        void success(LastMenuBean lastMenuBean);

    }

    public interface OnSelectListener {

        void select(Program program);
    }

    private static class MyHandler extends android.os.Handler {

        private final WeakReference<MenuGroup> mAdapter;

        MyHandler(MenuGroup menuGroup) {
            this.mAdapter = new WeakReference<MenuGroup>(menuGroup);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REQUEST_MENU_FOCUS:
                    BaseMenuRecyclerAdapter adapter = (BaseMenuRecyclerAdapter) msg.obj;
                    if (adapter != null && adapter.getSelectView() != null) {
                        adapter.getSelectView().requestFocus();
                    }
                    break;
                case REQUEST_MENU_FIRST_FOCUS:
                    BaseMenuRecyclerAdapter adapter1 = (BaseMenuRecyclerAdapter) msg.obj;
                    if (adapter1 != null && adapter1.getFirstPositionView() != null) {
                        adapter1.getFirstPositionView().requestFocus();
                    }
                    break;
                case REQUEST_MENU_PATH_VIEW_FOCUS:
                    BaseMenuRecyclerAdapter adapter3 = (BaseMenuRecyclerAdapter) msg.obj;
                    if (adapter3 != null && adapter3.getPathView() != null) {
                        adapter3.getPathView().requestFocus();
                    }
                    break;
            }
        }
    }

    static class AnimEntity {
        float x;
        float toX;
        boolean gone;

        AnimEntity(float x, float toX) {
            this(x, toX, false);
        }

        AnimEntity(float x, float toX, boolean gone) {
            this.x = x;
            this.toX = toX;
            this.gone = gone;
        }
    }
}
