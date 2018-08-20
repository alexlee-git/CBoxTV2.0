package tv.newtv.cboxtv.cms.listPage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.CircleTransform;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.cms.listPage.adapter.UniversalAdapter;
import tv.newtv.cboxtv.cms.listPage.adapter.UniversalViewHolder;
import tv.newtv.cboxtv.cms.listPage.model.ListPageInfo;
import tv.newtv.cboxtv.cms.listPage.model.MarkInfo;
import tv.newtv.cboxtv.cms.listPage.model.MarkListener;
import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;
import tv.newtv.cboxtv.cms.listPage.model.ScreenInfo;
import tv.newtv.cboxtv.cms.listPage.presenter.IListPagePresenter;
import tv.newtv.cboxtv.cms.listPage.presenter.ListPagePresenter;
import tv.newtv.cboxtv.cms.listPage.view.ListMenuView;
import tv.newtv.cboxtv.cms.listPage.view.ListPageView;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.AnimationBuilder;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;

//import tv.newtv.cboxtv.cms.net.ApiUtil;

/**
 * Created by caolonghe on 2018/2/14 0014.
 */

public class ListPageActivity extends BaseActivity implements ListPageView, MarkListener {

    private View parent;
    private RelativeLayout rel_screen;//筛选
    private TextView tv_name;
    private ListMenuView mListMenuView;
    private UniversalAdapter<NavListPageInfoResult.NavInfo> mMenuAdapter;
    private RelativeLayout mRelativeLayout_error, mRelativeLayout_success, listpage_rel_loading;
    private RecyclerView mRecyclerView;
    private RecyclerView mRecyclerView_MarkData;
    private RecycleMarkDataAdapter markDataAdapter;
    private RecycleItemAdapter mAdapter;
    private ListHandler mHandler = new ListHandler();
    private IListPagePresenter mIListPagePresenter;
    private NavListPageInfoResult mNavListPageInfoResult;
    private List<NavListPageInfoResult.NavInfo> mNavInfos;
    private int MenuPostion = 0;
    private int MenuFousedPostion = 0;
    private TextView oldView;
    private TextView newView;
    private int mMenuSize = 0;
    private long mLastKeyDownTime = 0;
    //    private String mMarkUrl = "http://search.cloud.ottcn
    // .com:8080/newtv-solr-search/pps/getRetrievalKeywords.json";
    // private String mMarkDataUrl = "http://111.32.138.56/icms_api/api/listPage/newtv/0/3ac2503b
    // -21aa-11e8-ae54-c7d8a7a18cc4.json";
    private String mMarkDataUrl_pageUUid;
    private MarkInfo mMarkInfo;//筛选条件
    private List<MarkInfo.Mark> mMarks;//筛选条件
    private MarkManager markManager;
    private TextView tv_type;
    private ImageView img_error;
    private TextView tv_screen;
    private ImageView img_screen;
    private HorizontalRecyclerView mMarkRecycle;   //筛选pop
    private LinearLayout mLinearLayout;
    private boolean isMark = false;
    private TextView tv_screen_type, tv_screen_total;
    private List<String> mListTypes = new ArrayList<>();
    private PopupWindow popWindow;
    private ScreenInfo mScreenInfo;
    private List<ScreenInfo.ResultListBean> mResultData;
    private int MarkPosition = 0;
    private ListPageInfo mListPageInfo;
    private List<ListPageInfo.DataBean> dataBeans;
    private List<ListPageInfo.DataBean.ProgramsBean> mProgramsBeanList;
    private int startSum = 0;
    private int size = 50;
    private int TotalSum = 0;
    private int page = 1;
    private int lastPosition;
    private String mPageUUID;
    private String mActionType;
    private String mActionUri;
    private String nav_id;//导航id
    private int defaultMenuSelectIndex;
    private boolean isScreenLoad = false;
    private String mBlockTitle;
    private int markPos = 0;
    private ScreenInfo mBlockTypeScreen;
    private View lastFoucsScreenDataView;
    private long mLastKeyTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_listpage);
        parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        mPageUUID = getIntent().getStringExtra("page_uuid");
        mActionType = getIntent().getStringExtra("action_type");
        mActionUri = getIntent().getStringExtra("action_uri");
        nav_id = getIntent().getStringExtra(Constant.DEFAULT_UUID);//导航id
        Log.e("list---pageuuid", mPageUUID + "-----------");
        if (mPageUUID == null) {
            Toast.makeText(ListPageActivity.this, getResources().getString(R.string
                    .list_no_pageuuid), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        init();
        mIListPagePresenter = new ListPagePresenter(this, LauncherApplication.AppContext);
        //mIListPagePresenter.requestListPageNav( .LISTPAGE_MARK + "/listPage/" + Constant
        // .APP_KEY + "/" +
        //        Constant.CHANNEL_ID + "/" + mPageUUID + ".json");
        mIListPagePresenter.requestListPageNav(mPageUUID);
        showPopwindow();
        uploadEnterLog();
    }

    private void inflateView() {
        mMenuSize = mNavInfos != null ? mNavInfos.size() : 0;

        for (int i = 0; i < mMenuSize; i++) {
            if (!TextUtils.isEmpty(nav_id)) {

                if (mNavInfos != null && mNavInfos.get(i) != null) {
                    NavListPageInfoResult.NavInfo navInfo = mNavInfos.get(i);
                    String uuid = null;
                    if (Constant.OPEN_CHANNEL.equals(navInfo.getActionType())) {
                        uuid = navInfo.getActionURI();
                    } else {
                        uuid = navInfo.getContentID();
                    }
                    if (nav_id.equals(uuid)) {
                        MenuPostion = i;
                        requestMarkData(uuid);
                        break;
                    }
                }
            }

            Log.e("MM", "menuPositon=" + MenuPostion + "///////////");
        }
        mMenuAdapter = new UniversalAdapter<NavListPageInfoResult.NavInfo>(ListPageActivity.this,
                R.layout.listpage_item_left, mNavInfos, null) {
            @Override
            public void convert(UniversalViewHolder holder, NavListPageInfoResult.NavInfo info,
                                int position) {
                TextView textView = (TextView) holder.getViewByKey(R.id.listpage_item_left_tv);
                if (textView != null) {
                    textView.setText(info.getTitle());
                    if (MenuPostion == position) {
                        textView.setSelected(true);
                        textView.setPressed(true);
                        textView.setTextColor(Color.parseColor("#00d3fe"));
                    } else {
                        textView.setSelected(false);
                        textView.setPressed(false);
                        textView.setTextColor(Color.parseColor("#e0e0e0"));
                    }
                    if (MenuFousedPostion == position) {
                        textView.setTextColor(Color.parseColor("#ffffff"));
                    }
                }
            }
        };
        mListMenuView.setFocusableInTouchMode(false);
        mListMenuView.setAdapter(mMenuAdapter);
        mListMenuView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("onItemSelected------", i + "--" + view + "----onItemSelected--------");
                newView = (TextView) view;

                Log.i("onItemSelected------", i + "--" + newView + "----newview--------");
                Log.i("onItemSelected------", i + "--" + oldView + "----oldview--------");
                MenuPostion = i;
                int textSize = DisplayUtils.translate(36, 1);


                if (oldView != null) {
                    //oldView.setTextSize(textSize);

                    TextPaint paint = oldView.getPaint();
                    paint.setFakeBoldText(false);//字体
                    oldView.setTextColor(Color.parseColor("#ededed"));
                    oldView.postInvalidate();


                }

                if (newView != null) {
                    TextPaint paint = newView.getPaint();
                    paint.setFakeBoldText(true);//字体加粗
                    newView.setTextColor(Color.parseColor("#FFFFFF"));
                    //newView.setTextSize(textSize);
                    newView.postInvalidate();
                    oldView = newView;
                }


                MarkPosition = 0;
                mProgramsBeanList.clear();
                markDataAdapter.notifyDataSetChanged();
                if (Constant.OPEN_CHANNEL.equals(mNavInfos.get(i).getActionType())) {
                    mMarkDataUrl_pageUUid = mNavInfos.get(i).getActionURI();
                } else {
                    mMarkDataUrl_pageUUid = mNavInfos.get(i).getContentID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i("onItemSelected------", adapterView + "-----onNothingSelected-------");
            }
        });
        mListMenuView.setOnKeyListener(new ListView.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            Log.i("onKey-------", "KEYCODE_DPAD_UP");
                            if (MenuPostion == 0) {
                                rel_screen.setFocusable(true);
                                rel_screen.requestFocus();
                                oldView.setTextColor(Color.parseColor("#ededed"));
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            Log.i("onKey-------", "KEYCODE_DPAD_DOWN");
                            if (mMenuSize - 1 == MenuPostion) {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            Log.i("onKey-------", "KEYCODE_DPAD_RIGHT");
                            if (mRecyclerView.getVisibility() == View.GONE &&
                                    mRecyclerView_MarkData.getVisibility() == View.GONE) {
                                return true;
                            }
                            if (mRecyclerView.getVisibility() == View.GONE) {
                                Log.i("mRecyclerView", "-GONE----");
                                View view = mRecyclerView_MarkData.getChildAt(MarkPosition);
//                                if (view != null) {
                                mRecyclerView_MarkData.requestFocus();
//                                }
                                if (lastFoucsScreenDataView != null) {
                                    lastFoucsScreenDataView.requestFocus();
                                }

                                return true;

                            }
                            if (mRecyclerView_MarkData.getVisibility() == View.GONE) {
                                Log.i("mRecyclerView_MarkData", "-GONE----");

                                View view = mRecyclerView.getChildAt(0);
                                if (view != null) {
                                    view.requestFocus();
                                }
                                return true;

                            }
                            break;
                    }
                }
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                        case KeyEvent.KEYCODE_DPAD_UP:
                            int selectedItemPosition = mListMenuView.getSelectedItemPosition();
                            Log.e("gao", "selectedItemPosition:" + selectedItemPosition);
                            if (Constant.OPEN_CHANNEL.equals(mNavInfos.get(selectedItemPosition)
                                    .getActionType())) {
                                mMarkDataUrl_pageUUid = mNavInfos.get(selectedItemPosition)
                                        .getActionURI();
                            } else {
                                mMarkDataUrl_pageUUid = mNavInfos.get(selectedItemPosition)
                                        .getContentID();
                            }
                            Log.e("gao", "mMarkDataUrl_pageUUid:" + mMarkDataUrl_pageUUid);
                            mHandler.postDelayed(new Runnable() {
                                public void run() {
                                    requestMarkData(mMarkDataUrl_pageUUid);
                                }
                            }, 200);
                            break;
                    }
                }
                return false;
            }
        });

        mListMenuView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.i("onFocusChange-------", view + "------------");
//                View viewFocus = view.findFocus().;

//                Log.i("onFocusChange-------", viewFocus + "################");
                if (b) {
                    mListMenuView.setSelector(R.drawable.menufouse);
                    if (oldView != null) {
                        oldView.setTextColor(Color.parseColor("#ededed"));
                        TextPaint paint = oldView.getPaint();
                        paint.setFakeBoldText(true);//字体加粗
                    }
                } else {
                    mListMenuView.setSelector(R.drawable.menunormal);
                    if (oldView != null) {
                        oldView.setTextColor(Color.parseColor("#3f7afd"));
                        TextPaint paint = oldView.getPaint();
                        paint.setFakeBoldText(false);//字体加粗
                    }
                }

            }
        });

        mListMenuView.setSelection(MenuPostion);
        mListMenuView.requestFocus();
        markManager = MarkManager.getInstance(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            Log.i("onKeyDown-------", "KEYCODE_DPAD_RIGHT");
            if (rel_screen.isFocused()) {
                Log.i("onKeyDown-------", "rel_screen");
                if (mRecyclerView.getVisibility() == View.GONE &&
                        mRecyclerView_MarkData.getVisibility() == View.GONE) {
                    return true;
                }
                if (mRecyclerView.getVisibility() == View.GONE) {
                    View view = mRecyclerView_MarkData.getChildAt(0);

                    if (view != null) {
                        view.requestFocus();
                    } else {
                        return true;
                    }
                }
                if (mRecyclerView_MarkData.getVisibility() == View.GONE) {
                    View view = mRecyclerView.getChildAt(0);
                    if (view != null) {
                        view.requestFocus();
                    } else {
                        return true;
                    }
                }
                rel_screen.setFocusable(false);
                mListMenuView.setFocusable(false);
                return true;
            }

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            Log.i("onKeyDown-------", "KEYCODE_DPAD_DOWN");
            if (rel_screen.isFocused()) {
                Log.i("onKeyDown-------", "rel_screen");
                tv_screen_type.setVisibility(View.INVISIBLE);
                tv_screen_total.setVisibility(View.INVISIBLE);
                img_screen.setImageResource(R.drawable.screen);
                tv_screen.setTextColor(Color.parseColor("#ededed"));
                mRecyclerView_MarkData.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                rel_screen.setFocusable(false);
                mListMenuView.setFocusable(true);
                mListMenuView.requestFocus();

                listpage_rel_loading.setVisibility(View.GONE);
                if (mProgramsBeanList == null || mProgramsBeanList.size() <= 0) {
                    mRelativeLayout_error.setVisibility(View.VISIBLE);
                    mRelativeLayout_success.setVisibility(View.GONE);
                } else {
                    mRelativeLayout_error.setVisibility(View.GONE);
                    mRelativeLayout_success.setVisibility(View.VISIBLE);
                }
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        mResultData = new ArrayList<>();
        mProgramsBeanList = new ArrayList<>();
        tv_name = (TextView) findViewById(R.id.listpage_tv_type);
        mRelativeLayout_error = (RelativeLayout) findViewById(R.id.listpage_rel_error);
        mRelativeLayout_success = (RelativeLayout) findViewById(R.id.listpage_rel_success);
        listpage_rel_loading = (RelativeLayout) findViewById(R.id.listpage_rel_loading);
        tv_screen_type = (TextView) findViewById(R.id.listpage_tv_total);
        tv_screen_total = (TextView) findViewById(R.id.listpage_tv_totalpage);
        tv_screen = (TextView) findViewById(R.id.listpage_rel_screen_tv);
        img_screen = (ImageView) findViewById(R.id.listpage_img_rearch);
        rel_screen = (RelativeLayout) findViewById(R.id.listpage_rel_screen);
        mListMenuView = (ListMenuView) findViewById(R.id.listpage_recycle_left);
        mRecyclerView = (RecyclerView) findViewById(R.id.listpage_recycle);
        mRecyclerView_MarkData = (RecyclerView) findViewById(R.id.listpage_recycle_page);
        rel_screen.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    img_screen.setImageResource(R.drawable.screen_fouse);
                    tv_screen.setTextColor(Color.parseColor("#3f7afd"));
                }
            }
        });
        rel_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView_MarkData.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                tv_screen_type.setVisibility(View.VISIBLE);
                if (mResultData == null || mResultData.size() <= 0) {
                    for (int m = 0; m < 3; m++) {
                        mListTypes.add("全部");
                    }
                    page = 1;
                    startSum = 0;
                    uploadScreenLog("-1", "-1", "-1");
                    getScreenData(mBlockTitle, "", "", "", startSum + "", size + "");
                }
                popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });

        mAdapter = new RecycleItemAdapter(ListPageActivity.this);
        GridLayoutManager layoutManager1 = new GridLayoutManager(ListPageActivity.this, 5);
        mRecyclerView.setLayoutManager(layoutManager1);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVisibility(View.GONE);

        markDataAdapter = new RecycleMarkDataAdapter(ListPageActivity.this);
        GridLayoutManager layoutManager2 = new GridLayoutManager(ListPageActivity.this, 5);
        mRecyclerView_MarkData.setLayoutManager(layoutManager2);
        mRecyclerView_MarkData.setAdapter(markDataAdapter);
    }

    private void setData(int page) {
        if (page == 1) {
            mResultData.clear();
            mAdapter.notifyDataSetChanged();
        }
        isScreenLoad = true;
        mResultData.addAll(mScreenInfo.getResultList());
        if (mResultData.size() <= 0) {
            Log.i("--mDatas---", "---null---");
            mRelativeLayout_error.setVisibility(View.VISIBLE);
            mRelativeLayout_success.setVisibility(View.GONE);
        } else {
            Log.i("--mDatas---", mResultData.size() + "---!null---");
            mRelativeLayout_error.setVisibility(View.GONE);
            mRelativeLayout_success.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyItemRangeInserted((page - 1) * size, size);
    }

    @Override
    public void showMarkdata(String type, String name) {
        Log.i("----mark--", type + "---" + name);
        if (name == null) {
            return;
        }

        if (name.equals(getResources().getString(R.string.list_year))) {
            mListTypes.set(0, type);
        } else if (name.equals(getResources().getString(R.string.list_area))) {
            mListTypes.set(1, type);
        } else if (name.equals(getResources().getString(R.string.list_classtypes))) {
            mListTypes.set(2, type);
        }
        StringBuilder types = new StringBuilder();
        for (int i = 0; i < mListTypes.size(); i++) {
            types.append(getType(mListTypes.get(i)));
        }
        tv_screen_type.setText(getSplit(types.toString()));
        page = 1;
        startSum = 0;
        mResultData.clear();
        mAdapter.notifyDataSetChanged();
        getScreenData(mBlockTitle, mListTypes.get(0), mListTypes.get(1), mListTypes.get(2),
                startSum + "", size + "");
        uploadScreenLog(getScreenType(mListTypes.get(0)), getScreenType(mListTypes.get(1)),
                getScreenType(mListTypes.get(2)));
    }

    private String getType(String type) {
        String mType = "";
        if (type.equals("全部")) {
            mType = "";
        } else {
            mType = type + "/";
        }
        return mType;
    }

    private String getSplit(String content) {
        String str = "";
        if (content == null || content.length() <= 0) {
            return "";
        }
        if (content.substring(content.length() - 1, content.length()).equalsIgnoreCase("/")) {
            str = content.substring(0, content.length() - 1);
        } else {
            str = content;
        }
        return str;
    }

    //左侧列表数据加载
    @Override
    public void inflateListPage(NavListPageInfoResult value, String from) {
        try {
            mNavListPageInfoResult = value;
            if (mNavListPageInfoResult != null) {
                mNavInfos = mNavListPageInfoResult.getData();
                mBlockTitle = mNavListPageInfoResult.getBlockTitle();
                if (mBlockTitle == null) {
                    mBlockTitle = getResources().getString(R.string.detail_film);
                }
                tv_name.setText(mBlockTitle);
                //Collections.sort(mNavInfos);

                if ("server".equals(from)) {

                    inflateView();
                }
            }
            isMark = false;
//        if (mNavInfos != null && mNavInfos.size() > 0) {
//            requestMarkData(mNavInfos.get(0).getContentID());
//        }

            requestMark();
        } catch (Exception e) {
            LogUtils.e(e.toString());
            Toast.makeText(ListPageActivity.this, getResources().getString(R.string
                    .list_no_pageuuid), Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onFailed(String desc) {

    }

    private void setMarkPageData(int i) {

        List<ListPageInfo.DataBean.ProgramsBean> programs = dataBeans.get(i).getPrograms();
        if (programs != null && programs.size() > 0) {
            mProgramsBeanList.addAll(programs);
        }

        if (mProgramsBeanList == null || mProgramsBeanList.size() <= 0) {
            mRelativeLayout_success.setVisibility(View.GONE);
        } else {
//            mRelativeLayout_error.setVisibility(View.GONE);
            mRelativeLayout_success.setVisibility(View.VISIBLE);
        }
        markDataAdapter.notifyDataSetChanged();
    }

    private void setListIntent(String mActionType, String mContentType, String mContentUUID,
                               String mActionUri) {
//        StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_16);
//        logBuff.append(0 + ",")
//                .append("" + "+")
//                .append("" + "+")
//                .append("" + ",")
//                .append(mContentUUID + ",")
//                .append(mContentType + ",")
//                .append(mActionType + ",")
//                .append("")
//                .trimToSize();
//
//        LogUploadUtils.uploadLog(Constant.LOG_NODE_HOME_PAGE, logBuff
//                .toString());//由首页进入下个推荐位

        JumpUtil.activityJump(this, mActionType, mContentType, mContentUUID, mActionUri);
    }

    private void zoomByFactor(View view, float factor, int duration) {
        if (!view.isFocusable()) {
            return;
        }
        ScaleAnimation animation = AnimationBuilder.getInstance()
                .getScaleAnimation(1.0f, factor, 1.0f, factor,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, duration);
        TextView name = (TextView) view.findViewWithTag("");
        if (name != null) {
            name.setVisibility(View.VISIBLE);
            // name.setSelected(true);
        }
        if (animation != null) {
//                     view.bringToFront();
            view.startAnimation(animation);
        }
    }

    private void scaleToOriginalDimension(View view, float factor, int duration) {
        ScaleAnimation animation = AnimationBuilder.getInstance()
                .getScaleAnimation(factor, 1.0f, factor, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f, duration);
        TextView name = (TextView) view.findViewWithTag("");
        if (name != null) {
            name.setSelected(false);
        }
        if (animation != null) {
            view.startAnimation(animation);
        }
    }

    public void getScreenData(String type, String year, String area, String classType, String
            startnum, String size) {
        requsetScreenData(type, year, area, classType, startnum, size);
    }

    private void showPopwindow() {
        View popView = View.inflate(this, R.layout.listpage_layout_dialog, null);

        mLinearLayout = (LinearLayout) popView.findViewById(R.id.listpage_dailog_rel);
        img_error = (ImageView) popView.findViewById(R.id.listpage_dailog_error);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.loading);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        img_error.startAnimation(operatingAnim);
        popWindow = new PopupWindow(popView, width, height);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(false);// 设置同意在外点击消失

        if (isMark) {
            setMark();
        } else {
            mHandler.sendEmptyMessage(3);
            img_error.setVisibility(View.VISIBLE);
        }
        ColorDrawable dw = new ColorDrawable(0x30000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.setAnimationStyle(R.style.take_photo_anim);
    }

    private void setMark() {

        for (int i = 0; i < mMarks.size(); i++) {
            if (mBlockTitle.equals(mMarks.get(i).getType())) {
                markPos = i;
                Log.e("--listpage---", markPos + "---mark--");
                break;
            }
        }
        List<String> types = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (int m = 0; m < 3; m++) {
            mListTypes.add("全部");
        }
        if (mMarks.get(markPos).getYears() != null) {
            types.add(mMarks.get(markPos).getYears());
            names.add(getResources().getString(R.string.list_year));
        }
        if (mMarkInfo.getData().get(markPos).getAreas() != null) {
            types.add(mMarkInfo.getData().get(markPos).getAreas());
            names.add(getResources().getString(R.string.list_area));
        }
        if (mMarkInfo.getData().get(markPos).getClassTypes() != null) {
            types.add(mMarkInfo.getData().get(markPos).getClassTypes());
            names.add(getResources().getString(R.string.list_classtypes));
        }
        Log.e("listpage--tyes--", types.size() + "---");

        img_error.clearAnimation();
        img_error.setVisibility(View.GONE);
        for (int i = 0; i < types.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.listpage_item_dialog, null);
            tv_type = (TextView) view.findViewById(R.id.Listpage_pop_tv);
            tv_type.setText(names.get(i));
            mMarkRecycle = (HorizontalRecyclerView) view.findViewById(R.id.Listpage_pop_recycle);
            if (markManager != null) {
                markManager.setMark(ListPageActivity.this, mMarkRecycle, types.get(i), names.get
                        (i));
            }
            mLinearLayout.addView(view);
        }
        int height = DisplayUtils.translate(types.size() * 80 + 115, 1);
        popWindow.setHeight(height);
//        mLinearLayout.requestFocus();
    }

    private void requsetScreenData(String type, String year, String area, String classType,
                                   String startnum, String size) {
        mScreenInfo = null;
        listpage_rel_loading.setVisibility(View.VISIBLE);
        mRelativeLayout_success.setVisibility(View.GONE);
        mRelativeLayout_error.setVisibility(View.GONE);

        try {
            NetClient.INSTANCE.getListPageApi()
                    .getScreenResult(type, Constant.APP_KEY, Constant.CHANNEL_ID, "PS;CG",
                            getScreenType(year), getScreenType(area), getScreenType(classType),
                            startnum, size)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                Log.i("ScrenResult", data + "----");
                                Gson mGson = new Gson();
                                mScreenInfo = mGson.fromJson(data, ScreenInfo.class);
                                mHandler.sendEmptyMessage(4);
                            } catch (Exception e) {
                                LogUtils.e(e);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mScreenInfo = null;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(4);
                            }
                            Log.e("ScrenResult---onError", e + "----");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.i("Listpage----获取筛选数据异常");
        }
    }

    private String getScreenType(String str) {
        String mType = "";
        if (str.equals("全部")) {
            mType = "-1";
        } else {
            mType = str;
        }
        return mType;
    }

    //筛选条件的查询
    private void requestMark() {

        try {
            NetClient.INSTANCE.getListPageApi()
                    .getMarkData()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                Gson mGson = new Gson();
                                mMarkInfo = mGson.fromJson(data, MarkInfo.class);
                                mHandler.sendEmptyMessage(2);

                            } catch (Exception e) {
                                LogUtils.e(e);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("标签---onError", e + "----");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.i("Listpage----获取标签页异常");
        }
    }

    private void requestMarkData(String contentUUid) {
        try {
            NetClient.INSTANCE.getListPageApi()
                    .getMarkDataResult(Constant.BASE_URL_CMS + Constant.CMS_URL + Constant
                            .APP_KEY + "/" + Constant.CHANNEL_ID + "/page/" + contentUUid + ".json")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                Gson mGson = new Gson();
                                mListPageInfo = mGson.fromJson(data, ListPageInfo.class);
                                mHandler.sendEmptyMessage(1);

                            } catch (Exception e) {
                                LogUtils.e(e);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListPageInfo = null;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(1);
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            LogUtils.e(e);
            LogUtils.i("Listpage----获取页面数据异常");
        }
    }

    private void uploadEnterLog() {
        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
        dataBuff.append(0 + ",")
                .append(mPageUUID + ",")
                .append(mActionType + ",")
                .append(mActionUri)
                .trimToSize();

        LogUploadUtils.uploadLog(Constant.LOG_NODE_PAGE, dataBuff.toString());
    }

    private void uploadExitLog() {
        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
        dataBuff.append(1 + ",")
                .append(mPageUUID + ",")
                .append(mActionType + ",")
                .append(mActionUri)
                .trimToSize();

        LogUploadUtils.uploadLog(Constant.LOG_NODE_PAGE, dataBuff.toString());
    }

    private void uploadScreenLog(String mVideoClassType, String year, String area) {
        StringBuilder dataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
        dataBuff.append(Constant.LOG_NODE_SCREEN + ",")
                .append(mBlockTitle + ",")
                .append(mVideoClassType + ",")
                .append(year + ",")
                .append(area + ",")
                .append("")
                .trimToSize();

        LogUploadUtils.uploadLog(Constant.LOG_NODE_PAGE, dataBuff.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MarkManager.getInstance(this).release();
        uploadExitLog();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    private void getCatagoryData(String firstCatagory) {

        listpage_rel_loading.setVisibility(View.VISIBLE);
        mRelativeLayout_success.setVisibility(View.GONE);
        mRelativeLayout_error.setVisibility(View.GONE);
        try {
            NetClient.INSTANCE.getListPageApi()
                    .getSearchCategoryData("PS;CG", firstCatagory, Constant.APP_KEY, Constant
                            .CHANNEL_ID, "0", "30")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                Gson mGson = new Gson();
                                mBlockTypeScreen = mGson.fromJson(data, ScreenInfo.class);
                                Log.e("mBlockTypeScreen---", mBlockTypeScreen + "");
                                mHandler.sendEmptyMessage(5);

                            } catch (Exception e) {
                                LogUtils.e(e.toString());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i("Listpage----", "Catagory---onError");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            LogUtils.i("Listpage----", "Catagory---" + e.toString());
        }
    }

    private void getSearchTagsData(String type, String year, String area, String classType) {
        listpage_rel_loading.setVisibility(View.VISIBLE);
        mRelativeLayout_success.setVisibility(View.GONE);
        mRelativeLayout_error.setVisibility(View.GONE);
        mScreenInfo = null;
        try {
            NetClient.INSTANCE.getListPageApi()
                    .getScreenResult(type, Constant.APP_KEY, Constant.CHANNEL_ID, "PS;CG", year,
                            area, classType, "0", "30")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody value) {
                            try {
                                String data = value.string();
                                Gson mGson = new Gson();
                                mBlockTypeScreen = mGson.fromJson(data, ScreenInfo.class);
                                LogUtils.i("mBlockTypeScreen---type", mBlockTypeScreen + "-----");
                                mHandler.sendEmptyMessage(5);
                            } catch (Exception e) {
                                LogUtils.e(e.toString());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            LogUtils.i(e.toString());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    @SuppressLint("HandlerLeak")
    class ListHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            listpage_rel_loading.setVisibility(View.GONE);
            switch (msg.what) {
                case 1:
                    mProgramsBeanList.clear();
                    if (mListPageInfo != null) {
                        dataBeans = mListPageInfo.getData();
                        if (dataBeans != null && dataBeans.size() > 0) {
                            for (int i = 0; i < dataBeans.size(); i++) {
                                String blockType = dataBeans.get(i).getBlockType();
                                List<ListPageInfo.DataBean.SearchConditionsBean>
                                        mSearchConditions = dataBeans.get(i).getSearchConditions();
                                if (blockType.equals("recommendOnOrder")) {
                                    setMarkPageData(i);
                                    continue;
                                } else if (blockType.equals("searchTags")) {
                                    Log.e("---searchTags", i + "---searchTags");
                                    for (int k = 0; k < mSearchConditions.size(); k++) {
                                        Log.e("---searchTags", mSearchConditions.size() + "--" + k);
                                        getSearchTagsData(mSearchConditions.get(k).getType(),
                                                mSearchConditions.get(k).getYear(),
                                                mSearchConditions.get(k).getArea(),
                                                mSearchConditions.get(k).getClassType());
                                    }

                                } else if (blockType.equals("searchCategory")) {
                                    Log.e("---searchCategory", i + "---searchCategory");
                                    for (int k = 0; k < mSearchConditions.size(); k++) {
                                        Log.e("---searchCategory", mSearchConditions.size() +
                                                "--" + k);
                                        getCatagoryData(mSearchConditions.get(k).getCategory());
                                    }

                                } else {
                                    setMarkPageData(i);
                                    continue;
                                }
                            }
                        } else {
                            listpage_rel_loading.setVisibility(View.GONE);
                            mRelativeLayout_error.setVisibility(View.VISIBLE);
                            mRelativeLayout_success.setVisibility(View.GONE);
                        }
                    } else {
                        listpage_rel_loading.setVisibility(View.GONE);
                        mRelativeLayout_error.setVisibility(View.VISIBLE);
                        mRelativeLayout_success.setVisibility(View.GONE);
                    }

                    break;
                case 2:
                    if (mMarkInfo != null) {
                        Log.i("mark-----", mMarkInfo.getData().get(0).getKey() + "");
                        mMarks = mMarkInfo.getData();
                        if (mMarks != null && mMarks.size() > 0) {
                            isMark = true;
                        } else {
                            isMark = false;
                            removeMessages(3);
                        }
                    } else {
                        isMark = false;
                    }
                    break;
                case 3:
                    if (isMark) {
                        setMark();
                    } else {
                        sendEmptyMessageDelayed(3, 200);
                    }
                    break;
                case 4:
                    listpage_rel_loading.setVisibility(View.GONE);
                    if (mScreenInfo != null) {
                        String mTotal = mScreenInfo.getTotal();
                        if (mTotal != null) {
                            try {
                                TotalSum = Integer.parseInt(mTotal);
                                tv_screen_total.setText(TotalSum + "部");
                            } catch (Exception e) {
                                tv_screen_total.setVisibility(View.GONE);
                                TotalSum = 0;
                            }
                        }
                    }
                    setData(page);
                    break;
                case 5:
                    listpage_rel_loading.setVisibility(View.GONE);
                    if (mBlockTypeScreen != null) {
                        if (mBlockTypeScreen.getResultList() != null && mBlockTypeScreen
                                .getResultList().size() > 0) {

                            mRelativeLayout_error.setVisibility(View.GONE);
                            mRelativeLayout_success.setVisibility(View.VISIBLE);

                            List<ListPageInfo.DataBean.ProgramsBean> programs = new ArrayList<>();
                            for (int i = 0; i < mBlockTypeScreen.getResultList().size(); i++) {
                                ListPageInfo.DataBean.ProgramsBean programsBean = new
                                        ListPageInfo.DataBean.ProgramsBean();

                                programsBean.setContentType(mBlockTypeScreen.getResultList().get
                                        (i).getContentType());
                                programsBean.setContentUUID(mBlockTypeScreen.getResultList().get
                                        (i).getUUID());
                                programsBean.setImg(mBlockTypeScreen.getResultList().get(i)
                                        .getPicurl());
                                programsBean.setTitle(mBlockTypeScreen.getResultList().get(i)
                                        .getName());
                                programs.add(programsBean);
                            }
                            int total = mProgramsBeanList.size();
                            mProgramsBeanList.addAll(programs);
                            markDataAdapter.notifyItemRangeChanged(total - 1, mBlockTypeScreen
                                    .getResultList().size());
                        } else {

                            mRelativeLayout_error.setVisibility(View.VISIBLE);
                            mRelativeLayout_success.setVisibility(View.GONE);
                            Log.e("---ResultList", mBlockTypeScreen.getResultList() + "--");
                        }
                    } else {
                        mRelativeLayout_error.setVisibility(View.VISIBLE);
                        mRelativeLayout_success.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    }

    class RecycleItemAdapter extends RecyclerView.Adapter<RecycleItemAdapter.MyHolder> {


        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public RecycleItemAdapter(Context mContext) {
            this.mContext = mContext;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.listpage_item, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {

            if (mResultData == null || mResultData.size() <= 0) {
                return;
            }
            final ScreenInfo.ResultListBean mResultListBean = mResultData.get(position);
            holder.tvname.setText(mResultListBean.getName());
            String url = mResultListBean.getPicurl();
            if (url == null) {
                Picasso.with(mContext).load(R.drawable.focus_240_360).into(holder.img);
            } else {
                Picasso.with(mContext)    //context
                        .load(url)     //图片加载地址
                        .placeholder(R.drawable.focus_240_360)
                        .error(R.drawable.focus_240_360)   //图片记载失败时显示的页面
                        .noFade()       //设置淡入淡出效果
//                        .resize(240, 360)
//                        .centerInside()
                        .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                        .transform(new CircleTransform(mContext))
                        .into(holder.img);
            }

            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        zoomByFactor(v, 1.1f, 200);
                        holder.img_fouse.setVisibility(View.VISIBLE);
                        holder.tvname.setSelected(true);
                        if (position >= (mResultData.size() - 6) && TotalSum > mResultData.size()) {
                            if (isScreenLoad) {
                                page++;
                                getScreenData(mBlockTitle, "", "", "", (page - 1) * size + "",
                                        size + "");
                                isScreenLoad = false;
                            }
                        }
                    } else {
                        holder.tvname.setSelected(false);
                        scaleToOriginalDimension(v, 1.1f, 200);
                        holder.img_fouse.setVisibility(View.INVISIBLE);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ScreenInfo.ResultListBean mResultListBean = mResultData.get(position);
                    setListIntent("OPEN_DETAILS", mResultListBean.getContentType(),
                            mResultListBean.getUUID(), "");
//                    processOpenCellScreen(mResultData.get(position));
                }
            });

            holder.itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    Log.i("onKey-------", i + "KEYCODE");
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                        if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (position % 5 == 0) {
                                rel_screen.setFocusable(true);
                                rel_screen.requestFocus();
                                return true;
                            }
                            return false;
                        } else if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            return false;
                        } else if (i == KeyEvent.KEYCODE_BACK) {
                            ListPageActivity.this.finish();
                            return true;
                        } else {
                            long current = System.currentTimeMillis();
                            if (current - mLastKeyTime < 400) {
                                return true;
                            } else {
                                mLastKeyTime = current;
                            }
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mResultData != null ? mResultData.size() : 0;
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            private ImageView img;
            private ImageView img_fouse;
            private TextView tvcontent;
            private TextView tvname;

            public MyHolder(View itemView) {
                super(itemView);
                img = (ImageView) itemView.findViewById(R.id.listpage_item_img);
                img_fouse = (ImageView) itemView.findViewById(R.id.listpage_item_img_foused);
                tvname = (TextView) itemView.findViewById(R.id.listpage_item_tv_name);
                tvcontent = (TextView) itemView.findViewById(R.id.listpage_item_tv_content);
                DisplayUtils.adjustView(getBaseContext(), img, img_fouse, R.dimen.width_17dp, R
                        .dimen.height_16dp);
            }
        }
    }

    class RecycleMarkDataAdapter extends RecyclerView.Adapter<RecycleMarkDataAdapter.MyHolder> {
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public RecycleMarkDataAdapter(Context mContext) {
            this.mContext = mContext;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.listpage_item, null);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {

            if (dataBeans == null || dataBeans.size() <= 0) {
                return;
            }
            final ListPageInfo.DataBean.ProgramsBean mProgramsBean = mProgramsBeanList.get
                    (position);
            holder.tvname.setText(mProgramsBean.getTitle());
            String url = mProgramsBean.getImg();
            if (url == null) {
                Picasso.with(mContext).load(R.drawable.deful_user).into(holder.img);
            } else {
                Picasso.with(mContext)    //context
                        .load(url)     //图片加载地址
                        .placeholder(R.drawable.focus_240_360)
                        .error(R.drawable.deful_user)   //图片记载失败时显示的页面
                        .noFade()       //设置淡入淡出效果
//                        .resize(240, 360)
//                        .centerInside()
                        .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                        .transform(new CircleTransform(mContext))
                        .into(holder.img);
            }

            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        Log.i("-----------", MarkPosition + "------------" + position);
                        MarkPosition = position;
                        holder.tvname.setSelected(true);
                        zoomByFactor(v, 1.1f, 200);
                        holder.img_fouse.setVisibility(View.VISIBLE);
                        lastFoucsScreenDataView = v;
                    } else {
                        holder.tvname.setSelected(false);
                        scaleToOriginalDimension(v, 1.1f, 200);
                        holder.img_fouse.setVisibility(View.INVISIBLE);
                    }
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ListPageInfo.DataBean.ProgramsBean programsBean = mProgramsBeanList.get
                            (position);
                    Log.e("----", programsBean.getContentType() + "--");
                    setListIntent("OPEN_DETAILS", programsBean.getContentType(),
                            programsBean.getContentUUID(), "");
//                    processOpenCell(mProgramsBeanList.get(position));
                }
            });

            holder.itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {

                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                        if (i == KeyEvent.KEYCODE_BACK) {
                            ListPageActivity.this.finish();
                            return true;
                        } else if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (position % 5 == 0) {
                                mListMenuView.setFocusable(true);
                                mListMenuView.requestFocus();
                                tv_screen_type.setVisibility(View.INVISIBLE);
                                img_screen.setImageResource(R.drawable.screen);
                                tv_screen.setTextColor(Color.parseColor("#ededed"));
                                return true;
                            }
                            return false;
                        } else if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            return false;
                        } else {

                            long current = System.currentTimeMillis();
                            if (current - mLastKeyDownTime < 400) {
                                return true;
                            } else {
                                mLastKeyDownTime = current;
                            }
                        }
                    }

                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mProgramsBeanList != null ? mProgramsBeanList.size() : 0;
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            private ImageView img;
            private ImageView img_fouse;
            private TextView tvcontent;
            private TextView tvname;

            public MyHolder(View itemView) {
                super(itemView);
                img = (ImageView) itemView.findViewById(R.id.listpage_item_img);
                img_fouse = (ImageView) itemView.findViewById(R.id.listpage_item_img_foused);
                tvname = (TextView) itemView.findViewById(R.id.listpage_item_tv_name);
                tvcontent = (TextView) itemView.findViewById(R.id.listpage_item_tv_content);
                DisplayUtils.adjustView(getApplicationContext(), img, img_fouse, R.dimen
                        .width_17dp, R.dimen.height_16dp);
            }
        }
    }
}
