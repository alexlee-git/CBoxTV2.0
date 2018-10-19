package tv.newtv.cboxtv.cms.screenList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.screenList.adapter.FirstLabelAdapter;
import tv.newtv.cboxtv.cms.screenList.adapter.LabelDataAdapter;
import tv.newtv.cboxtv.cms.screenList.adapter.secondLabelAdapter;
import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;
import tv.newtv.cboxtv.cms.screenList.bean.LabelDataBean;
import tv.newtv.cboxtv.cms.screenList.bean.TabBean;
import tv.newtv.cboxtv.cms.screenList.presenter.LabelPresenterImpl;
import tv.newtv.cboxtv.cms.screenList.tablayout.TabLayout;
import tv.newtv.cboxtv.cms.screenList.tablayout.TvTabLayout;
import tv.newtv.cboxtv.cms.screenList.view.LabelView;
import tv.newtv.cboxtv.cms.screenList.views.FocusRecyclerView;
import tv.newtv.cboxtv.cms.util.DisplayUtils;

/**
 * Created by 冯凯 on 2018/9/28.
 */

public class ScreenListActivity extends AppCompatActivity implements LabelView {

    private LabelPresenterImpl presenter;
    private HorizontalGridView labelRecyclerView;
    private FocusRecyclerView tvRecyclerView;
    private List<TabBean.DataBean.ChildBean> childData = new ArrayList<>();
    private FirstLabelAdapter adapter;
    private List<TabBean.DataBean> data;
    private LabelDataAdapter labelDataAdapter;
    private LinearLayout container;
    private Map<String, Object> map;
    private String key;
    private String categoryId;
    private String type_key;
    private String year_key;
    private String place_key;
    private TextView title_label;
    private TextView type_text;
    private TextView year_text;
    private TextView place_text;
    private boolean loadMore;
    private int num = 1;
    private int moveFlag = 0;
    private List<LabelDataBean.DataBean> list;
    private TvTabLayout tab;
    private boolean mShouldScroll;
    private int mToPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_list);
        initPresenter();
        initView();
        EventBus.getDefault().register(this);


    }

    private void initView() {
        labelRecyclerView = findViewById(R.id.labelRecyclerView);
        tvRecyclerView = findViewById(R.id.tvRecyclerView);
        tab = findViewById(R.id.tab);
        container = findViewById(R.id.container);
        title_label = findViewById(R.id.title);
        type_text = findViewById(R.id.type_text);
        year_text = findViewById(R.id.year_text);
        place_text = findViewById(R.id.place_text);
        labelRecyclerView.setNumRows(1);
        tab.setScaleValue(1.2f);
        tab.setTabTextColors(Color.parseColor("#ffffff"), Color.BLUE, Color.parseColor("#ff00b7fd"));
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


        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                categoryId = data.get(position).getId();
                map.put("categoryId", categoryId);
                presenter.getLabelData();
                moveFlag = 0;
                childData.clear();
                childData.addAll(data.get(position).getChild());
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tvRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {//
                    mShouldScroll = false;
                    smoothMoveToPosition(tvRecyclerView, mToPosition);
                }
            }
        });
    }

    private void initPresenter() {
        list = new ArrayList<>();
        map = new HashMap<>();
        presenter = new LabelPresenterImpl();
        presenter.attachView(this);
        presenter.getFirstLabel();
        presenter.getSecondLabel();
        presenter.getLabelData();

    }

    @Override
    public void showFirstMenuData(TabBean bean) {
        if (bean != null) {
            data = bean.getData();
            for (int i = 0; i < data.size(); i++) {
                tab.addTab(tab.newTab().setText(data.get(i).getTitle()));
            }
        }

    }

    @Override
    public void showSecondMenuData(LabelBean bean) {
        if (bean != null) {
            List<LabelBean.DataBean> dataBeans = bean.getData();
            for (int i = 0; i < dataBeans.size(); i++) {
                HorizontalGridView horizontalGridView = new HorizontalGridView(this);
                LabelBean.DataBean dataBean = dataBeans.get(i);
                List<LabelBean.DataBean.FilterValueBean> filterValue = dataBean.getFilterValue();
                secondLabelAdapter secondMenuAdapter = new secondLabelAdapter(filterValue, this, dataBean);
                horizontalGridView.setAdapter(secondMenuAdapter);
                int height = DisplayUtils.translate(50, 1);
                int topMargin = DisplayUtils.translate(36, 1);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                params.topMargin = topMargin;
                horizontalGridView.setLayoutParams(params);
                container.addView(horizontalGridView);
            }
        }

    }

    @Override
    public Map<String, Object> getMap() {

        map.put("page", String.valueOf(num));
        map.put("rows", "50");
        Log.d("MainActivityMap", map.toString());
        return map;
    }

    @Override
    public void showData(LabelDataBean dataBean) {


        if (!loadMore) {
            list.clear();
            if (dataBean != null) {
                list.addAll(dataBean.getData());
                labelDataAdapter.notifyDataSetChanged();
            }

        } else {
            if (dataBean != null) {
                list.addAll(dataBean.getData());
                labelDataAdapter.notifyDataSetChanged();
            }

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        EventBus.getDefault().unregister(this);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getFirstLabel(TabBean.DataBean.ChildBean childBean) {

        if (childBean != null) {
            map.put("categoryId", childBean.getId());
            presenter.getLabelData();

            title_label.setText(childBean.getTitle());
            title_label.setVisibility(View.VISIBLE);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getgetFilterKey(LabelBean.DataBean dataBean) {
        if (dataBean != null) {
            key = dataBean.getFilterKey();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getgetFilterTitle(LabelBean.DataBean.FilterValueBean valueBean) {

        if (valueBean != null) {
            map.put(key, URLEncoder.encode(valueBean.getTitle()));
        }
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (container.getChildAt(i).hasFocus()) {
                if (i == 0) {
                    type_key = key;
                    if (valueBean != null) {
                        type_text.setText(valueBean.getTitle());
                        type_text.setVisibility(View.VISIBLE);
                    }

                } else if (i == 1) {
                    year_key = key;
                    if (valueBean != null) {
                        year_text.setText(valueBean.getTitle());
                        year_text.setVisibility(View.VISIBLE);
                    }

                } else if (i == 2) {
                    place_key = key;
                    if (valueBean != null) {
                        place_text.setText(valueBean.getTitle());
                        place_text.setVisibility(View.VISIBLE);
                    }

                }
            }
        }
        presenter.getLabelData();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                num = 1;
                loadMore = false;
                if (moveFlag == 1) {
                    title_label.setVisibility(View.GONE);
                    tab.setFocusable(true);
                    tab.requestFocus();
                    moveFlag = 0;
                    map.put("categoryId", categoryId);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 2) {
                    type_text.setVisibility(View.GONE);
                    labelRecyclerView.setVisibility(View.VISIBLE);
                    labelRecyclerView.setFocusable(true);
                    labelRecyclerView.requestFocus();
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
                        view.setFocusable(true);
                        view.requestFocus();
                    }
                    moveFlag--;
                    map.remove(year_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 4) {
                    place_text.setVisibility(View.GONE);
                    View view = container.getChildAt(1);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        view.setFocusable(true);
                        view.requestFocus();
                    }
                    moveFlag--;
                    map.remove(place_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 5) {
                    View view = container.getChildAt(2);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        view.requestFocus();
                    }
                    moveFlag--;
                    presenter.getLabelData();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (!tvRecyclerView.hasFocus()) {
                    moveFlag++;
                    if (tab.hasFocus()) {
                        tab.setFocusable(false);
                        labelRecyclerView.setFocusable(true);
                        labelRecyclerView.requestFocus();
                        return true;
                    }
                    if (moveFlag == 2 && labelRecyclerView.hasFocus()) {
                        labelRecyclerView.setVisibility(View.GONE);
                        if (container.getChildAt(0) != null) {
                            container.getChildAt(0).setFocusable(true);
                            container.getChildAt(0).requestFocus();
                        }
                        return true;
                    }
                    if (moveFlag == 3) {
                        if (container.getChildAt(0) != null && container.getChildAt(1) != null) {
                            container.getChildAt(0).setVisibility(View.GONE);
                            container.getChildAt(1).setFocusable(true);
                            container.getChildAt(1).requestFocus();
                        }

                        return true;
                    }
                    if (moveFlag == 4) {
                        if (container.getChildAt(1) != null && container.getChildAt(2) != null) {
                            container.getChildAt(1).setVisibility(View.GONE);
                            container.getChildAt(2).setFocusable(true);
                            container.getChildAt(2).requestFocus();
                        }
                        return true;
                    }
                    if (moveFlag == 5) {
                        if (container.getChildAt(2) != null) {
                            container.getChildAt(2).setVisibility(View.GONE);
                        }
                        if (tvRecyclerView.getChildAt(0) != null) {
                            tvRecyclerView.getChildAt(0).requestFocus();
                        }
                        return true;
                    }
                }
                if (isVisBottom(tvRecyclerView)) {
                    moveFlag = 5;
                    num++;
                    loadMore = true;
                    presenter.getLabelData();
                    return true;
                }

                break;
            case KeyEvent.KEYCODE_BACK:
                num = 1;
                loadMore = false;
                smoothMoveToPosition(tvRecyclerView, 0);
                if (moveFlag == 1) {
                    title_label.setVisibility(View.GONE);
                    tab.setFocusable(true);
                    tab.requestFocus();
                    moveFlag = 0;
                    map.put("categoryId", categoryId);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 2) {
                    type_text.setVisibility(View.GONE);
                    labelRecyclerView.setVisibility(View.VISIBLE);
                    labelRecyclerView.setFocusable(true);
                    labelRecyclerView.requestFocus();
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
                        view.setFocusable(true);
                        view.requestFocus();
                    }
                    moveFlag--;
                    map.remove(year_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 4) {
                    place_text.setVisibility(View.GONE);
                    View view = container.getChildAt(1);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        view.setFocusable(true);
                        view.requestFocus();
                    }
                    moveFlag--;
                    map.remove(place_key);
                    presenter.getLabelData();
                    return true;
                }
                if (moveFlag == 5) {
                    View view = container.getChildAt(2);
                    if (view != null) {
                        view.setVisibility(View.VISIBLE);
                        view.requestFocus();
                    }
                    moveFlag--;
                    presenter.getLabelData();
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public static boolean isVisBottom(FocusRecyclerView recyclerView) {
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


    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        int firstItemPosition = -1;
        int lastItemPosition = -1;

        // 第一个可见位置
        firstItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        lastItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItemPosition) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItemPosition) {
            // 第二种可能:跳转位置在第一个可见位置之后,在最后一个可见项之前
            int movePosition = position - firstItemPosition;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);//dx>0===>向左  dy>0====>向上
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

}
