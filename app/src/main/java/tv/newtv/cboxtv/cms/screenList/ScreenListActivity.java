package tv.newtv.cboxtv.cms.screenList;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.cms.bean.FilterItem;
import com.newtv.cms.bean.FilterValue;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalLayoutManager;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.cms.screenList.adapter.FirstLabelAdapter;
import tv.newtv.cboxtv.cms.screenList.adapter.LabelDataAdapter;
import tv.newtv.cboxtv.cms.screenList.adapter.secondLabelAdapter;
import tv.newtv.cboxtv.cms.screenList.bean.FocusBean;
import tv.newtv.cboxtv.cms.screenList.presenter.LabelPresenterImpl;
import tv.newtv.cboxtv.cms.screenList.tablayout.TabLayout;
import tv.newtv.cboxtv.cms.screenList.tablayout.TvTabLayout;
import tv.newtv.cboxtv.cms.screenList.view.LabelView;
import tv.newtv.cboxtv.cms.screenList.views.FocusRecyclerView;
import tv.newtv.cboxtv.cms.util.JumpUtil;


/**
 * Created by 冯凯 on 2018/9/28.
 */

public class ScreenListActivity extends BaseActivity implements LabelView {

    private LabelPresenterImpl presenter;
    HorizontalRecyclerView labelRecyclerView;
    private FocusRecyclerView tvRecyclerView;
    private List<CategoryTreeNode> childData = new ArrayList();
    private FirstLabelAdapter adapter;
    private List<CategoryTreeNode> data;
    private LabelDataAdapter labelDataAdapter;
    private LinearLayout container;
    private Map<String, Object> map;
    private String key;
    private String categoryId = "";
    private String type_key;
    private String year_key;
    private String place_key;
    private String sour_key;
    private TextView title_label;
    private TextView type_text;
    private TextView year_text;
    private TextView place_text;
    private TextView sour_text;
    private TextView result_total;
    private boolean loadMore;
    private int pageNum = 1;
    private int moveFlag = 0;
    private List<SubContent> list;
    private TvTabLayout tab;
    private long mTimeDelay;
    private long mTimeLast;
    private long mTimeSpace;
    private Observable<CategoryTreeNode> childBeanObservable;
    private Observable<FilterItem> dataBeanObservable;
    private Observable<FilterValue> filterValueBeanObservable;
    private View labelRecordView;
    private Observable<View> RecordViewObservable;
    private Observable<View> menuRecordViewObservable;
    private View first_Record_View;
    private View second_Record_View;
    private View third_Record_View;
    private FocusBean focusBean;
    private int defaultFocusTab = -1;
    private int defaultFocusLab = -1;
    private int default_record_position = -1;
    private int default_record_position_second = -1;
    private Observable<Boolean> defaultFocusLabObservable;
    private Observable<Boolean> observableOne;
    private Observable<Boolean> observableTwo;
    private boolean hasDefaultFocus = true;
    private boolean hasDefaultFocusSecond = true;
    List<FilterItem> dataBeans;
    private String recordId = "";
    private View four_record_view;
    private int currentPos = -1;
    private int totalSize;
    private String videoType;
    private String videoClassType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_list);
        initFocus();
        initPresenter(this);
        initView();
        initEvent();

    }

    private void initFocus() {
        String s = getIntent().getStringExtra(Constant.DEFAULT_UUID);
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(s)) {
            focusBean = gson.fromJson(s, FocusBean.class);
        }
    }

    private void initEvent() {

        observableTwo = RxBus.get().register("record_position_second");
        observableTwo.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            View view = container.getChildAt(0);
                            if (view != null) {
                                view.setVisibility(View.GONE);
                            }
                            moveFlag = 3;
                        }

                    }
                });


        observableOne = RxBus.get().register("record_position_first");
        observableOne.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            labelRecyclerView.setVisibility(View.GONE);
                            moveFlag = 2;
                        }

                    }
                });

        defaultFocusLabObservable = RxBus.get().register("defaultFocusLab");

        defaultFocusLabObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            moveFlag = 1;
                        }

                    }
                });

        childBeanObservable = RxBus.get().register("labelId");
        childBeanObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CategoryTreeNode>() {
                               @Override
                               public void accept(CategoryTreeNode categoryTreeNode) throws Exception {
                                   if (categoryTreeNode != null) {
                                       videoClassType = categoryTreeNode.getTitle();
                                       categoryId = categoryTreeNode.getId();
                                       presenter.getSecondLabel();
                                       map.put("categoryId", categoryTreeNode.getId());
                                       presenter.getLabelData();
                                       title_label.setText(categoryTreeNode.getTitle());
                                       LogUploadUtils.uploadLog(Constant.LOG_NODE_FILTER, "0," + videoType+","+videoClassType+","+" "+","+" "+","+" ");
                                       title_label.setVisibility(View.VISIBLE);
                                   }

                               }
                           }
                );

        dataBeanObservable = RxBus.get().register("labelKey");
        dataBeanObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FilterItem>() {
                    @Override
                    public void accept(FilterItem filterItem) throws Exception {
                        if (filterItem != null) {
                            key = filterItem.getFilterKey();
                        }
                    }
                });
        filterValueBeanObservable = RxBus.get().register("labelValue");
        filterValueBeanObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FilterValue>() {

                    @Override
                    public void accept(FilterValue filterValue) throws Exception {
                        if (filterValue != null) {
                            map.put(key, filterValue.getKey());
                        }
                        int childCount = container.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            if (container.getChildAt(i).hasFocus()) {
                                if (i == 0) {
                                    type_key = key;
                                    if (filterValue != null) {
                                        type_text.setText(filterValue.getTitle());
                                        type_text.setVisibility(View.VISIBLE);
                                    }

                                } else if (i == 1) {
                                    year_key = key;
                                    if (filterValue != null) {
                                        year_text.setText(filterValue.getTitle());
                                        year_text.setVisibility(View.VISIBLE);
                                    }

                                } else if (i == 2) {
                                    place_key = key;
                                    if (filterValue != null) {
                                        place_text.setText(filterValue.getTitle());
                                        place_text.setVisibility(View.VISIBLE);
                                    }

                                }else if (i==3){
                                    sour_key = key;
                                    if (filterValue!=null){
                                        sour_text.setText(filterValue.getTitle());
                                        sour_text.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        presenter.getLabelData();

                    }
                });
        RecordViewObservable = RxBus.get().register("labelRecordView");
        RecordViewObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<View>() {
                    @Override
                    public void accept(View view) throws Exception {
                        labelRecordView = view;
                    }
                });
        menuRecordViewObservable = RxBus.get().register("menuRecordView");
        menuRecordViewObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<View>() {
                    @Override
                    public void accept(View view) throws Exception {
                        int childCount = container.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            if (container.getChildAt(i).hasFocus()) {
                                if (i == 0) {
                                    first_Record_View = view;
                                } else if (i == 1) {
                                    second_Record_View = view;
                                } else if (i == 2) {
                                    third_Record_View = view;

                                }else if (i==3){
                                    four_record_view = view;
                                }
                            }
                        }
                    }
                });


    }

    private void initView() {
        labelRecyclerView = findViewById(R.id.labelRecyclerView);
        tvRecyclerView = findViewById(R.id.tvRecyclerView);
        tab = findViewById(R.id.tab);
        container = findViewById(R.id.container);
        title_label = findViewById(R.id.title);
        type_text = findViewById(R.id.type_text);
        year_text = findViewById(R.id.year_text);
        result_total = findViewById(R.id.number);
        place_text = findViewById(R.id.place_text);
        sour_text = findViewById(R.id.sour_text);

//        tvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (recyclerView.getFocusedChild() != null) {
//                    if (recyclerView.getFocusedChild().getTop()<(int) getResources().getDimension(R.dimen.width_480px)) {
//                        recyclerView.smoothScrollBy(0, recyclerView.getFocusedChild().getTop() - (int) getResources().getDimension(R.dimen.width_25px));
//                        recyclerView.getFocusedChild().requestFocus();
//                    }
//                }
//            }
//
//        });

        tab.setScaleValue(1.2f);
        tab.setTabTextColors(Color.parseColor("#80ffffff"), Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        adapter = new FirstLabelAdapter(this, childData);

        labelRecyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(this, 6);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        labelDataAdapter = new LabelDataAdapter(this, list);
        labelDataAdapter.setHasStableIds(true);
        tvRecyclerView.setLayoutManager(manager);
        tvRecyclerView.setAdapter(labelDataAdapter);
        tvRecyclerView.setFocusFrontAble(true);
        tvRecyclerView.setFocusOutAble(true);
        labelDataAdapter.setOnItemClickListener(new LabelDataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SubContent subContent = list.get(position);
                JumpUtil.detailsJumpActivity(ScreenListActivity.this, subContent.getContentType(), subContent.getContentID());
            }
        });

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                title_label.setVisibility(View.GONE);
                type_text.setVisibility(View.GONE);
                year_text.setVisibility(View.GONE);
                place_text.setVisibility(View.GONE);
                sour_text.setVisibility(View.GONE);

                int position = tab.getPosition();
                categoryId = data.get(position).getId();
                videoType = data.get(position).getTitle();
                recordId = categoryId;

                presenter.getSecondLabel();
                map.put("categoryId", categoryId);
                presenter.getLabelData();
                moveFlag = 0;
                childData.clear();
                childData.addAll(data.get(position).getChild());
                if (labelRecyclerView.getVisibility()==View.GONE) {
                    labelRecyclerView.setVisibility(View.VISIBLE);
                    labelRecyclerView.requestFocus();
                }
                adapter.notifyDataSetChanged();

                for (int i = 0; i < childData.size(); i++) {
                    if (focusBean != null && !TextUtils.isEmpty(focusBean.getCateLv2())) {
                        if (focusBean.getCateLv2().equals(childData.get(i).getId())) {
                            defaultFocusLab = i;
                        }
                    }
                }
                if (hasDefaultFocus) {
                    boolean shouldScroll = requestFocus(labelRecyclerView, defaultFocusLab);
                    if (shouldScroll) {
                        labelRecyclerView.scrollToPosition(defaultFocusLab);
                    }
                    adapter.setdefaultFocus(defaultFocusLab);

                    hasDefaultFocus = false;
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


    }



    private boolean requestFocus(RecyclerView mRecyclerView, final int position) {
        HorizontalLayoutManager layoutManager = (HorizontalLayoutManager) mRecyclerView
                .getLayoutManager();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (firstVisibleItemPosition <= position && position <= lastVisibleItemPosition) {
            return false;
        } else {
            return true;
        }
    }

    private void initPresenter(Context context) {
        map = new HashMap<>();
        list = new ArrayList<>();
        presenter = new LabelPresenterImpl(context);
        presenter.attachView(this);
        presenter.getFirstLabel();
//        presenter.getSecondLabel();
//        presenter.getLabelData();

    }

    @Override
    public void showFirstMenuData(ModelResult<List<CategoryTreeNode>> modelResult) {

        if (modelResult != null && modelResult.getData() != null) {
            data = modelResult.getData();
            for (int i = 0; i < data.size(); i++) {
                tab.addTab(tab.newTab().setText(data.get(i).getTitle()));
                if (focusBean != null && !TextUtils.isEmpty(focusBean.getCateLv1()) && !TextUtils.isEmpty(data.get(i).getId())) {
                    if (focusBean.getCateLv1().equals(data.get(i).getId())) {
                        defaultFocusTab = i;
                    }
                }
            }
            if (defaultFocusTab != -1) {
                tab.selectTab(defaultFocusTab);
            }


        }

    }

    @Override
    public void showSecondMenuData(ModelResult<List<FilterItem>> modelResult) {
        if (modelResult != null) {
            container.removeAllViews();
            dataBeans = modelResult.getData();
            if(dataBeans != null) {

                List list = new ArrayList<FilterValue>();
                FilterValue value = new FilterValue("1", "最新发布");
                FilterValue value1 = new FilterValue("2","热门排行");
                list.add(value);
                list.add(value1);
                FilterItem item = new FilterItem("orderby", "排序方式", list);
                dataBeans.add(item);
            }
            if(dataBeans != null && dataBeans.size() > 0){
                for (int i = 0; i < dataBeans.size(); i++) {
                    HorizontalRecyclerView horizontalRecyclerView = new HorizontalRecyclerView(this);
                    FilterItem dataBean = dataBeans.get(i);
                    List<FilterValue> filterValue = dataBean.getFilterValue();
                    secondLabelAdapter secondMenuAdapter = new secondLabelAdapter(filterValue, this, dataBean);
                    for (int j = 0; j < filterValue.size(); j++) {
                        if (focusBean != null && !TextUtils.isEmpty(focusBean.getClassTypes())) {
                            if (focusBean.getClassTypes().equals(filterValue.get(j).getTitle())) {
                                default_record_position = j;
                            }
                        }
                        if (focusBean != null && !TextUtils.isEmpty(focusBean.getYears())) {
                            if (focusBean.getYears().equals(filterValue.get(j).getTitle())) {
                                default_record_position_second = j;
                            }
                        }
                    }
                    horizontalRecyclerView.setAdapter(secondMenuAdapter);
                    if (hasDefaultFocusSecond) {
                        if (i == 0) {
                            secondMenuAdapter.setDefaultFocusFirst(default_record_position);
                        } else if (i == 1) {
                            secondMenuAdapter.setDefaultFocusSecond(default_record_position_second);
                        }
                    }

                    int height = DisplayUtils.translate(50, 1);
                    int topMargin = DisplayUtils.translate(36, 1);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                    params.topMargin = topMargin;
                    horizontalRecyclerView.setLayoutParams(params);
                    container.addView(horizontalRecyclerView);
                }
            }
        }

    }

    @Override
    public Map<String, Object> getMap() {

        map.put("page", String.valueOf(pageNum));
        map.put("rows", "48");

        Log.d("MainActivityMap", map.toString());
        return map;
    }

    @Override
    public String getCategoryId() {
        return categoryId;
    }

    @Override
    public void showData(ArrayList<SubContent> contents, int total) {
        totalSize = total;
        result_total.setText(total + "个结果");
        if (!loadMore) {
            list.clear();
            list.addAll(contents);
            labelDataAdapter.notifyDataSetChanged();
        } else {
//            int index = list.size();
            list.addAll(contents);
            labelDataAdapter.notifyDataSetChanged();

//            int index = list.size();
//            list.addAll(contents);
//            int end = list.size();
//
//            labelDataAdapter.notifyItemRangeInserted(index,end);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        RxBus.get().unregister("labelId", childBeanObservable);
        RxBus.get().unregister("labelKey", dataBeanObservable);
        RxBus.get().unregister("labelValue", filterValueBeanObservable);
        RxBus.get().unregister("labelRecordView", RecordViewObservable);
        RxBus.get().unregister("menuRecordView", menuRecordViewObservable);
        RxBus.get().unregister("defaultFocusLab", defaultFocusLabObservable);
        RxBus.get().unregister("record_position_first", observableOne);
        RxBus.get().unregister("record_position_second", observableTwo);


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (labelRecyclerView.hasFocus() && !tab.hasFocus()){
                    LogUploadUtils.uploadLog(Constant.LOG_NODE_FILTER, "0," + videoType+","+" "+","+" "+","+" "+","+" ");
                }
                hasDefaultFocusSecond = false;
                pageNum = 1;
                loadMore = false;
                tvRecyclerView.smoothScrollToPosition(0);
                if (moveFlag == 1) {
                    title_label.setVisibility(View.GONE);
                    labelRecyclerView.smoothScrollToPosition(0);
                    tab.setFocusable(true);
                    tab.requestFocus();
                    moveFlag = 0;
                    categoryId = recordId;
                    presenter.getSecondLabel();
                    map.put("categoryId", categoryId);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 2) {
                    type_text.setVisibility(View.GONE);
                    labelRecyclerView.setVisibility(View.VISIBLE);
                    if (labelRecordView != null) {
                        labelRecordView.requestFocus();
                    }

                    moveFlag--;
                    map.remove(type_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 3) {
                    year_text.setVisibility(View.GONE);
                    View view = container.getChildAt(0);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        if (first_Record_View != null) {
                            first_Record_View.requestFocus();
                        }
                    } else {
                        labelRecyclerView.setVisibility(View.VISIBLE);
                        if (labelRecordView != null) {
                            labelRecordView.requestFocus();
                            moveFlag = 2;
                            map.remove(type_key);
                        }
                    }
                    moveFlag--;
                    map.remove(year_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 4) {
                    place_text.setVisibility(View.GONE);
                    View view1 = container.getChildAt(1);
                    View view = container.getChildAt(0);
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                        if (second_Record_View != null) {
                            second_Record_View.requestFocus();
                        }

                    } else {
                        if (view != null) {
                            view.setVisibility(View.VISIBLE);
                            if (first_Record_View != null) {
                                first_Record_View.requestFocus();
                                moveFlag = 3;
                                map.remove(year_key);
                            }
                        } else {
                            if (labelRecordView != null) {
                                labelRecyclerView.setVisibility(View.VISIBLE);

                                labelRecyclerView.requestFocus();
                                moveFlag = 2;
                                map.remove(type_key);
                            }
                        }
                    }
                    moveFlag--;
                    map.remove(place_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 5) {
                    sour_text.setVisibility(View.GONE);
                    View view3 = container.getChildAt(2);
                    View view2 = container.getChildAt(1);
                    View view1 = container.getChildAt(0);
                    if (view3 != null) {
                        view3.setVisibility(View.VISIBLE);
                        if (third_Record_View != null) {
                            third_Record_View.requestFocus();
                        }
                    } else {
                        if (view2 != null) {
                            view2.setVisibility(View.VISIBLE);
                            if (second_Record_View != null) {
                                second_Record_View.requestFocus();
                                moveFlag = 4;
                                map.remove(place_key);
                            }
                        } else {
                            if (view1 != null) {
                                view1.setVisibility(View.VISIBLE);
                                if (first_Record_View != null) {
                                    first_Record_View.requestFocus();
                                    moveFlag = 3;
                                    map.remove(year_key);
                                }
                            } else {
                                if (labelRecordView != null) {
                                    labelRecyclerView.setVisibility(View.VISIBLE);
                                    labelRecyclerView.requestFocus();
                                    moveFlag = 2;
                                    map.remove(type_key);
                                }
                            }
                        }

                    }
                    moveFlag--;
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag==6){
                    View view4 = container.getChildAt(3);
                    View view3 = container.getChildAt(2);
                    View view2 = container.getChildAt(1);
                    View view1 = container.getChildAt(0);
                    if (view4 != null) {
                        view4.setVisibility(View.VISIBLE);
                        if (four_record_view != null) {
                            four_record_view.requestFocus();
                        }
                    } else {
                        if (view3 != null) {
                            view3.setVisibility(View.VISIBLE);
                            if (third_Record_View != null) {
                                third_Record_View.requestFocus();
                                moveFlag = 5;
                                map.remove(sour_key);
                            }
                        } else {
                            if (view2 != null) {
                                view2.setVisibility(View.VISIBLE);
                                if (second_Record_View != null) {
                                    second_Record_View.requestFocus();
                                    moveFlag = 4;
                                    map.remove(place_key);
                                }
                            } else {
                                if (view1 != null) {
                                    view1.setVisibility(View.VISIBLE);
                                    if (first_Record_View != null) {
                                        first_Record_View.requestFocus();
                                        moveFlag = 3;
                                        map.remove(year_key);
                                    }
                                }else {
                                    if (labelRecordView != null) {
                                        labelRecyclerView.setVisibility(View.VISIBLE);
                                        labelRecyclerView.requestFocus();
                                        moveFlag = 2;
                                        map.remove(type_key);
                                    }
                                }
                            }
                        }

                    }
                    moveFlag--;
                    presenter.getLabelData();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:



                if (!labelRecyclerView.hasFocus() && tab.hasFocus()){
                    LogUploadUtils.uploadLog(Constant.LOG_NODE_FILTER, "0," + videoType+","+videoClassType+","+" "+","+" "+","+" ");
                }
                Log.e("yml", "onKeyDown: "+moveFlag );



                if (!tvRecyclerView.hasFocus()) {
                    moveFlag++;
                    if (tab.hasFocus()) {
                        labelRecordView = null;
                        if (labelRecyclerView.getChildAt(0) != null) {
                            labelRecyclerView.getChildAt(0).requestFocus();
                        }
                        return true;
                    }
                    if (moveFlag == 2 && labelRecyclerView.hasFocus()) {
                        first_Record_View = null;
                        labelRecyclerView.setVisibility(View.GONE);
                        if (container.getChildAt(0) != null) {
                            container.getChildAt(0).setFocusable(true);
                            container.getChildAt(0).requestFocus();
                        } else {
                            if (container.getChildAt(1) != null) {
                                container.getChildAt(1).requestFocus();
                                moveFlag = 3;
                            } else {
                                if (container.getChildAt(2) != null) {
                                    container.getChildAt(2).requestFocus();
                                    moveFlag = 4;
                                } else {
                                    if (container.getChildAt(3)!=null){
                                        container.getChildAt(3).requestFocus();
                                        moveFlag = 5;
                                    }else {
                                        if (tvRecyclerView.getChildAt(0) != null) {
                                            tvRecyclerView.getChildAt(0).requestFocus();
                                            moveFlag = 6;
                                        }else{
                                            tab.setFocusable(false);
                                        }
                                    }
                                }
                            }
                        }

                        return true;
                    }
                    if (moveFlag == 3 && container.getChildAt(0) != null && container.getChildAt(0).hasFocus()) {
                        second_Record_View = null;
                        container.getChildAt(0).setVisibility(View.GONE);
                        if (container.getChildAt(1) != null) {
                            container.getChildAt(1).setFocusable(true);
                            container.getChildAt(1).requestFocus();
                        } else {
                            if (container.getChildAt(2) != null) {
                                container.getChildAt(2).requestFocus();
                                moveFlag = 4;
                            } else {
                                if (container.getChildAt(3)!=null){
                                    container.getChildAt(3).requestFocus();
                                    moveFlag = 5;
                                }else {
                                    if (tvRecyclerView.getChildAt(0) != null) {
                                        tvRecyclerView.requestFocus();
                                        moveFlag = 6;
                                    }else {
                                        tab.setFocusable(false);
                                    }
                                }
                            }
                        }

                        return true;
                    }
                    if (moveFlag == 4 && container.getChildAt(1) != null && container.getChildAt(1).hasFocus()) {
                        third_Record_View = null;
                        container.getChildAt(1).setVisibility(View.GONE);
                        if (container.getChildAt(2) != null) {
                            container.getChildAt(2).setFocusable(true);
                            container.getChildAt(2).requestFocus();
                        } else {
                            if (container.getChildAt(3)!=null){
                                container.getChildAt(3).setFocusable(true);
                                container.getChildAt(3).requestFocus();
                                moveFlag = 5;
                            }else {
                                if (tvRecyclerView.getChildAt(0) != null) {
                                    tvRecyclerView.getChildAt(0).requestFocus();
                                    moveFlag = 6;
                                }else {
                                    tab.setFocusable(false);
                                }
                            }
                        }
                        return true;
                    }
                    if (moveFlag == 5 && container.getChildAt(2) != null && container.getChildAt(2).hasFocus()) {
                        container.getChildAt(2).setVisibility(View.GONE);
                        if (container.getChildAt(3)!=null){
                            container.getChildAt(3).requestFocus();

                        }else {
                            if (tvRecyclerView.getChildAt(0) != null) {
                                tvRecyclerView.getChildAt(0).requestFocus();
                                moveFlag = 6;
                            }else {
                                tab.setFocusable(false);
                            }
                        }
                        return true;
                    }
                    if (moveFlag==6&&container.getChildAt(3)!=null&&container.getChildAt(3).hasFocus()){
                        container.getChildAt(3).setVisibility(View.GONE);
                        if (tvRecyclerView.getChildAt(0) != null) {
                            tvRecyclerView.getChildAt(0).requestFocus();
                        }else {
                            tab.setFocusable(false);
                        }
                        return true;
                    }

                }
                if (isBottom(tvRecyclerView)) {
                    moveFlag = 6;
                    pageNum++;
                    loadMore = true;
                    presenter.getLabelData();
                    return true;
                }

                break;
            case KeyEvent.KEYCODE_BACK:
                hasDefaultFocusSecond = false;
                pageNum = 1;
                loadMore = false;
                tvRecyclerView.smoothScrollToPosition(0);
                if (moveFlag == 1) {
                    title_label.setVisibility(View.GONE);
                    labelRecyclerView.smoothScrollToPosition(0);
                    tab.setFocusable(true);
                    tab.requestFocus();
                    moveFlag = 0;
                    categoryId = recordId;
                    presenter.getSecondLabel();
                    map.put("categoryId", categoryId);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 2) {
                    type_text.setVisibility(View.GONE);
                    labelRecyclerView.setVisibility(View.VISIBLE);
                    if (labelRecordView != null) {
                        labelRecordView.requestFocus();
                    }

                    moveFlag--;
                    map.remove(type_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 3) {
                    year_text.setVisibility(View.GONE);
                    View view = container.getChildAt(0);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        if (first_Record_View != null) {
                            first_Record_View.requestFocus();
                        }
                    } else {
                        labelRecyclerView.setVisibility(View.VISIBLE);
                        if (labelRecordView != null) {
                            labelRecordView.requestFocus();
                            moveFlag = 2;
                            map.remove(type_key);
                        }
                    }
                    moveFlag--;
                    map.remove(year_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 4) {
                    place_text.setVisibility(View.GONE);
                    View view1 = container.getChildAt(1);
                    View view = container.getChildAt(0);
                    if (view1 != null) {
                        view1.setVisibility(View.VISIBLE);
                        if (second_Record_View != null) {
                            second_Record_View.requestFocus();
                        }

                    } else {
                        if (view != null) {
                            view.setVisibility(View.VISIBLE);
                            if (first_Record_View != null) {
                                first_Record_View.requestFocus();
                                moveFlag = 3;
                                map.remove(year_key);
                            }
                        } else {
                            if (labelRecordView != null) {
                                labelRecyclerView.setVisibility(View.VISIBLE);

                                labelRecyclerView.requestFocus();
                                moveFlag = 2;
                                map.remove(type_key);
                            }
                        }
                    }
                    moveFlag--;
                    map.remove(place_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 5) {
                    sour_text.setVisibility(View.GONE);
                    View view3 = container.getChildAt(2);
                    View view2 = container.getChildAt(1);
                    View view1 = container.getChildAt(0);
                    if (view3 != null) {
                        view3.setVisibility(View.VISIBLE);
                        if (third_Record_View != null) {
                            third_Record_View.requestFocus();
                        }
                    } else {
                        if (view2 != null) {
                            view2.setVisibility(View.VISIBLE);
                            if (second_Record_View != null) {
                                second_Record_View.requestFocus();
                                moveFlag = 4;
                                map.remove(place_key);
                            }
                        } else {
                            if (view1 != null) {
                                view1.setVisibility(View.VISIBLE);
                                if (first_Record_View != null) {
                                    first_Record_View.requestFocus();
                                    moveFlag = 3;
                                    map.remove(year_key);
                                }
                            } else {
                                if (labelRecordView != null) {
                                    labelRecyclerView.setVisibility(View.VISIBLE);
                                    labelRecyclerView.requestFocus();
                                    moveFlag = 2;
                                    map.remove(type_key);
                                }
                            }
                        }

                    }
                    moveFlag--;
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag==6){
                    View view4 = container.getChildAt(3);
                    View view3 = container.getChildAt(2);
                    View view2 = container.getChildAt(1);
                    View view1 = container.getChildAt(0);
                    if (view4 != null) {
                        view4.setVisibility(View.VISIBLE);
                        if (four_record_view != null) {
                            four_record_view.requestFocus();
                        }
                    } else {
                        if (view3 != null) {
                            view3.setVisibility(View.VISIBLE);
                            if (third_Record_View != null) {
                                third_Record_View.requestFocus();
                                moveFlag = 5;
                                map.remove(sour_key);
                            }
                        } else {
                            if (view2 != null) {
                                view2.setVisibility(View.VISIBLE);
                                if (second_Record_View != null) {
                                    second_Record_View.requestFocus();
                                    moveFlag = 4;
                                    map.remove(place_key);
                                }
                            } else {
                                if (view1 != null) {
                                    view1.setVisibility(View.VISIBLE);
                                    if (first_Record_View != null) {
                                        first_Record_View.requestFocus();
                                        moveFlag = 3;
                                        map.remove(year_key);
                                    }
                                }else {
                                    if (labelRecordView != null) {
                                        labelRecyclerView.setVisibility(View.VISIBLE);
                                        labelRecyclerView.requestFocus();
                                        moveFlag = 2;
                                        map.remove(type_key);
                                    }
                                }
                            }
                        }

                    }
                    moveFlag--;
                    presenter.getLabelData();
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public static boolean isBottom(FocusRecyclerView recyclerView) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1 && state == recyclerView.SCROLL_STATE_IDLE) {
            return true;
        } else {
            return false;
        }
    }


    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0) {
            long nowTime = SystemClock.elapsedRealtime();
            this.mTimeDelay = nowTime - this.mTimeLast;
            this.mTimeLast = nowTime;
            if (this.mTimeSpace <= 100L &&
                    this.mTimeDelay <= 100L) {
                this.mTimeSpace += this.mTimeDelay;
                return true;
            }
            this.mTimeSpace = 0L;
        }
        return super.dispatchKeyEvent(event);
    }


}