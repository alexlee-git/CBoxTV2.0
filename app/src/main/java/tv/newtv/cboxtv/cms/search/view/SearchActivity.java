package tv.newtv.cboxtv.cms.search.view;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.bean.SearchHotInfo;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.search.custom.NewTVSearchHotRecommend;
import tv.newtv.cboxtv.cms.search.custom.NewTVSearchResult;
import tv.newtv.cboxtv.cms.search.custom.SearchViewKeyboard;
import tv.newtv.cboxtv.cms.search.listener.INotifySearchHotRecommendData;
import tv.newtv.cboxtv.cms.search.listener.INotifySearchResultData;
import tv.newtv.cboxtv.cms.search.listener.OnGetKeyListener;
import tv.newtv.cboxtv.cms.search.listener.OnGetSearchHotRecommendFocus;
import tv.newtv.cboxtv.cms.search.listener.OnGetSearchResultFocus;
import tv.newtv.cboxtv.cms.search.listener.OnReturnInputString;
import tv.newtv.cboxtv.cms.search.presenter.SearchPagePresenter;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.utils.DeviceUtil;


/**
 * 类描述：搜索页面
 * 创建人：wqs
 * 创建时间： 2018/3/6 0006 15:24
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchActivity extends FragmentActivity implements ISearchPageView {
    private final String TAG = this.getClass().getSimpleName();
    private SearchViewKeyboard mSearchViewKeyboard;
    private SearchPagePresenter mSearchPagePresenter;
    private NewTVSearchResult mSearchResult;
    private NewTVSearchHotRecommend mHotRecommend;
    private View mSearchLine1;
    private RelativeLayout mRelativeLayout;
    public boolean isFirstFocus = true;
    private Intent mIntent;
    private boolean mExternalStatus = false;
    private boolean mExternalSlide = false;
    private float SearchViewKeyboardWidth = 655;
    private String mInputString = "";//输入框的值
    private String mSearchType;
    private String type, year, area, classType;
    private StringBuilder mScreenDataBuff;
    private static boolean eatKeyEvent = false;
    private boolean mRightKey = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        init();
        whetherExternalParams();
    }


    //判断是否有外部跳转参数传值
    private void whetherExternalParams() {
        try {
            mScreenDataBuff = new StringBuilder(Constant.BUFFER_SIZE_32);
            mIntent = getIntent();
            Bundle bundle = mIntent.getBundleExtra("person");
            if (bundle != null) {
                mSearchType = bundle.getString("SearchType");
                if (TextUtils.isEmpty(mSearchType)) {
                    mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);
                } else {
                    if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("SearchListByKeyword")) {
                        mInputString = bundle.getString("keyword");
                    }
                    mExternalStatus = true;
                    isFirstFocus = false;
                    mExternalSlide = true;
                    mSearchViewKeyboard.setVisibility(View.GONE);
                    mSearchLine1.setVisibility(View.GONE);
                    mHotRecommend.setVisibility(View.GONE);
                    mSearchResult.setVisibility(View.VISIBLE);
                    mSearchResult.setExternalParams(bundle);
                }
            } else {
                mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);
            }

//            Bundle bundle = mIntent.getBundleExtra("bundle");
//            if (bundle != null) {
//                mSearchType = bundle.getString("SearchType");
//                if (TextUtils.isEmpty(mSearchType)) {
//                    mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);
//                } else {
//                    if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("SearchListByKeyword")) {
//                        mInputString = bundle.getString("keyword");
//                    } else if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("RetrievalProgramSerialList")) {
//                        type = bundle.getString("type");
//                        if (TextUtils.isEmpty(type)) {
//                            type = "-1";
//                        }
//                        year = bundle.getString("year");
//                        if (TextUtils.isEmpty(year)) {
//                            year = "-1";
//                        }
//                        area = bundle.getString("area");
//                        if (TextUtils.isEmpty(area)) {
//                            area = "-1";
//                        }
//                        classType = bundle.getString("classType");
//                        if (TextUtils.isEmpty(classType)) {
//                            classType = "-1";
//                        }
//                        mScreenDataBuff.append(type + ",")
//                                .append(classType + ",")
//                                .append(year + ",").append(area + ",").append("")
//                                .trimToSize();
//                    }
//                    mExternalStatus = true;
//                    isFirstFocus = false;
//                    mExternalSlide = true;
//                    mSearchViewKeyboard.setVisibility(View.GONE);
//                    mSearchLine1.setVisibility(View.GONE);
//                    mHotRecommend.setVisibility(View.GONE);
//                    mSearchResult.setVisibility(View.VISIBLE);
//                    mSearchResult.setExternalParams(bundle);
//                }
//            } else {
//                mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);
//            }
        } catch (Exception e) {
            Log.e(TAG, "---whetherExternalParams:Exception--" + e.toString());
        }
    }


    //对象的初始化
    private void init() {
        SearchViewKeyboardWidth = DisplayUtils.translate((int) SearchViewKeyboardWidth, 0);
        mSearchPagePresenter = new SearchPagePresenter(this, this);
        mSearchViewKeyboard.setOnReturnInputString(onReturnInputString);
        mSearchResult.setOnGetSearchResultFocus(mOnGetSearchResultFocus);
        mHotRecommend.setINotifySearchHotRecommendData(mINotifySearchHotRecommendData);
        mHotRecommend.setOnGetSearchHotRecommendFocus(mOnGetSearchHotRecommendFocus);
        setOnKeyListener(onGetKeyListener);

    }

    //控件初始化
    private void initView() {
        mSearchViewKeyboard = (SearchViewKeyboard) findViewById(R.id.search_view_keyboard);
        mSearchLine1 = findViewById(R.id.search_line1);
        mSearchResult = (NewTVSearchResult) findViewById(R.id.search_result);
        mHotRecommend = (NewTVSearchHotRecommend) findViewById(R.id.search_hot_recommend);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.RelativeLayout);
    }

    private void preventBtnRightMove() {
        mSearchViewKeyboard.getDelBtn().setNextFocusRightId(R.id.frameLayout_btn_11);
        mSearchViewKeyboard.getThreeBtn().setNextFocusRightId(R.id.frameLayout_btn_3);
        mSearchViewKeyboard.getSixBtn().setNextFocusRightId(R.id.frameLayout_btn_6);
        mSearchViewKeyboard.getNineButton().setNextFocusRightId(R.id.frameLayout_btn_9);
    }

    private void notPreventBtnRightMoveoOfResultList(int id) {
        mSearchViewKeyboard.getDelBtn().setNextFocusRightId(id);
        mSearchViewKeyboard.getThreeBtn().setNextFocusRightId(id);
        mSearchViewKeyboard.getSixBtn().setNextFocusRightId(id);
        mSearchViewKeyboard.getNineButton().setNextFocusRightId(id);
    }

    private OnReturnInputString onReturnInputString = new OnReturnInputString() {
        @Override
        public void onReturnInputString(String inputStr) {
            if (onGetKeyListener != null) {
                onGetKeyListener.notifyKeywords(inputStr);
            }

        }
    };
    //监听输入框值变化
    private OnGetKeyListener onGetKeyListener = new OnGetKeyListener() {
        @Override
        public void notifyKeywords(String key) {
            if (TextUtils.isEmpty(key)){

            }
            try {
                Log.e(TAG, "---搜索关键字变化：" + key);
                mExternalStatus = false;//令外部跳转状态为false
                mExternalSlide = false;//令外部跳转移动状态为false
                mSearchType = "";
                mInputString = key;
                LogUploadUtils.uploadLog(Constant.LOG_NODE_SEARCH, key);
                mSearchResultItemView = null;
                if (!TextUtils.isEmpty(key)) {
                    mSearchResultDataStatus = false;
                    mSearchResult.setKey(key);
                    mSearchResult.setVisibility(View.VISIBLE);
                    mHotRecommend.setVisibility(View.GONE);
                } else {

                    if (!mSearchHotRecommendDataStatus) {
                        mSearchPagePresenter.requestPageRecommendData(Constant.APP_KEY, Constant.CHANNEL_ID);
                    }
                    mSearchResult.setEmptyViewVisiable(View.GONE);
                    mSearchResult.setKey(key);
                    mSearchResult.setVisibility(View.GONE);
                    mHotRecommend.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                LogUtils.e(e.toString());
                Log.e(TAG, "---notifyKeywords:Exception--" + e.toString());
            }
        }
    };
    private int mSearchResultPosition;//搜索结果item焦点索引值
    private View mSearchResultItemView;//搜索结果最后一个获得焦点的item
    private boolean mSearchResultFocusStatus = false;
    //监听搜索结果页焦点变化
    private OnGetSearchResultFocus mOnGetSearchResultFocus = new OnGetSearchResultFocus() {
        @Override
        public void notifySearchResultFocus(boolean focus, int position, View view) {
            try {
                mSearchResultPosition = position;
                if (view != null) {
                    mSearchResultItemView = view;
                }
                mSearchResultFocusStatus = focus;
                if (focus) {
                    mSearchResult.setResultFocus(true);
                    if (mSearchViewKeyboard.getLastFocusView() != null) {
                        mSearchResult.setKeyboardLastFocusView(mSearchViewKeyboard.getLastFocusView());
                    } else {
                        mSearchResult.setKeyboardLastFocusView(mSearchViewKeyboard.getDelBtn());
                    }                    if (isFirstFocus) {
                        slideView(mRelativeLayout, 0, -SearchViewKeyboardWidth);
                    }
                    isFirstFocus = false;
                } else {
                    mSearchResult.setResultFocus(false);
                    if (view == null) {
                        if (mExternalSlide) {
                            mSearchViewKeyboard.setVisibility(View.VISIBLE);
                        }
                        slideView(mRelativeLayout, -SearchViewKeyboardWidth, 0);
                        isFirstFocus = true;
                        mExternalSlide = false;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "---notifySearchResultFocus:Exception--" + e.toString());
            }
        }
    };
    private boolean mSearchResultDataStatus = false;
    private INotifySearchResultData mINotifySearchResultData = new INotifySearchResultData() {
        @Override
        public void INotifySearchResultData(boolean searchResultDataStatus) {
            mSearchResultDataStatus = searchResultDataStatus;
        }
    };
    private int mSearchHotRecommendPosition;//热搜item焦点索引值
    private View mSearchHotRecommendItemView;//热搜最后一个获得焦点的item
    private boolean mSearchHotRecommendFocusStatus = false;
    //监听热搜页焦点变化
    private OnGetSearchHotRecommendFocus mOnGetSearchHotRecommendFocus = new OnGetSearchHotRecommendFocus() {
        @Override
        public void notifySearchHotRecommendFocus(boolean focus, int position, View view) {
            try {
                mSearchHotRecommendPosition = position;
                if (view != null) {
                    mSearchHotRecommendItemView = view;
                }
                mSearchHotRecommendFocusStatus = focus;
                if (focus) {
                    if (isFirstFocus) {
                        slideView(mRelativeLayout, 0, -SearchViewKeyboardWidth);
                    }
                    isFirstFocus = false;
                    mHotRecommend.setKeyboardLastFocusView(mSearchViewKeyboard.getLastFocusView());
                } else {
                    if (view == null) {
                        slideView(mRelativeLayout, -SearchViewKeyboardWidth, 0);
                        isFirstFocus = true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "---notifySearchHotRecommendFocus:Exception--" + e.toString());
            }
        }
    };
    private boolean mSearchHotRecommendDataStatus = false;
    private INotifySearchHotRecommendData mINotifySearchHotRecommendData = new INotifySearchHotRecommendData() {
        @Override
        public void INotifySearchHotRecommendData(boolean searchHotRecommendDataStatus) {
            mSearchHotRecommendDataStatus = searchHotRecommendDataStatus;
        }
    };

    public void setOnKeyListener(OnGetKeyListener onGetKeyListener) {
        this.onGetKeyListener = onGetKeyListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return true;
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent
                .KEYCODE_DPAD_CENTER) {
            return super.dispatchKeyEvent(event);
        }
        if (event.getRepeatCount() % 8 == 0) {
            eatKeyEvent = false;
        }
        if (eatKeyEvent) {
            return true;
        }
        if (event.getRepeatCount() % 4 == 0) {
            eatKeyEvent = true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (TextUtils.isEmpty(mInputString)) {
                    if (!mSearchHotRecommendFocusStatus) {
                        if (mSearchViewKeyboard.getBtnTag() == 11 || mSearchViewKeyboard.getBtnTag() == 3 || mSearchViewKeyboard.getBtnTag() == 6 || mSearchViewKeyboard.getBtnTag() == 9) {
                            if (mSearchHotRecommendDataStatus) {
                                if (mSearchHotRecommendItemView != null) {
                                    mSearchHotRecommendItemView.requestFocus();
                                    return true;
                                } else {
                                    if (mHotRecommend.getRecyclerView().getChildAt(0) != null) {
                                        mHotRecommend.getRecyclerView().getChildAt(0).requestFocus();
                                        return true;
                                    }
                                }
                            } else if (mExternalStatus) {
                                if (!mSearchResultFocusStatus) {
                                    if (mSearchViewKeyboard.getBtnTag() == 11 || mSearchViewKeyboard.getBtnTag() == 3 || mSearchViewKeyboard.getBtnTag() == 6 || mSearchViewKeyboard.getBtnTag() == 9) {
                                        if (mSearchResultItemView != null) {
                                            mSearchResultItemView.requestFocus();
                                            return true;
                                        } else {
                                            int id = mSearchResult.getNextFocusRightId();
                                            if (id == 0) {
                                                return true;
                                            } else {
                                                notPreventBtnRightMoveoOfResultList(id);
                                            }
                                        }
                                    }
                                }
                            } else {
                                preventBtnRightMove();
                            }
                        }
                    }

                } else {
                    if (!mSearchResultFocusStatus) {
                        if (mSearchViewKeyboard.getBtnTag() == 11 || mSearchViewKeyboard.getBtnTag() == 3 || mSearchViewKeyboard.getBtnTag() == 6 || mSearchViewKeyboard.getBtnTag() == 9) {
                            mSearchResult.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    mRightKey = true;
                                }
                            });
                            mSearchResult.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                                @Override
                                public void onDraw() {
                                    mRightKey = true;
                                }
                            });
                            if (mRightKey){
                                mRightKey = false;
                                return true;
                            }

                            if (mSearchResultItemView != null) {
                                mSearchResultItemView.requestFocus();
                                return true;
                            } else {
                                int id = mSearchResult.getNextFocusRightId();
                                if (id == 0) {
                                    return true;
                                } else {
                                    notPreventBtnRightMoveoOfResultList(id);
                                }
                            }
                        }

                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
        }
        return super.onKeyDown(keyCode, event);

    }


    private SearchResultInfos mSearchResultInfos;
    private SearchResultInfos.ResultListBean mResultListBeanInfo;
    private List<SearchResultInfos.ResultListBean> mResultListBeanList;

    //加载热搜数据，进行分类填充
    @Override
    public void inflatePageRecommendData(SearchHotInfo searchHotInfo) {
        try {
            mSearchResultInfos = new SearchResultInfos();
            mResultListBeanList = new ArrayList<>();
            List<SearchHotInfo.DataBean.ProgramsBean> moduleItemList = new ArrayList<>();
            SearchHotInfo.DataBean.ProgramsBean programInfo;
            if (searchHotInfo.getData() != null && searchHotInfo.getData().size() > 0) {
                for (int i = 0; i < searchHotInfo.getData().size(); i++) {
                    if (searchHotInfo.getData().get(i).getBlockType().equals("recommendOnCell")) {
                        if (searchHotInfo.getData().get(i).getPrograms() != null && searchHotInfo.getData().get(i).getPrograms().size() > 0) {
                            for (int j = 0; j < searchHotInfo.getData().get(i).getPrograms().size(); j++) {
                                programInfo = searchHotInfo.getData().get(i).getPrograms().get(j);
                                moduleItemList.add(programInfo);
                            }
                        }
                    }
                }
                if (moduleItemList != null && moduleItemList.size() > 0) {
                    for (int j = 0; j < moduleItemList.size(); j++) {
                        mResultListBeanInfo = new SearchResultInfos.ResultListBean();
                        mResultListBeanInfo.setUUID(moduleItemList.get(j).getContentUUID());
                        mResultListBeanInfo.setContentType(moduleItemList.get(j).getContentType());
                        mResultListBeanInfo.setType(moduleItemList.get(j).getActionType());
                        mResultListBeanInfo.setActionUri(moduleItemList.get(j).getActionUri() == null ? "" : moduleItemList.get(j).getActionUri().toString());
                        mResultListBeanInfo.setHpicurl(moduleItemList.get(j).getImg());
                        mResultListBeanInfo.setName(moduleItemList.get(j).getTitle());
                        mResultListBeanList.add(mResultListBeanInfo);
                    }
                    mSearchResultInfos.setTotal(mResultListBeanList.size());
                    mSearchResultInfos.setResultList(mResultListBeanList);
                }
            }
            mHotRecommend.setData(mSearchResultInfos);
        } catch (Exception e) {
            Log.e(TAG, "---inflatePageRecommendData：Exception：" + e.toString());
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mSearchType)) {
            if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("SearchListByKeyword")) {
                LogUploadUtils.uploadLog(Constant.LOG_NODE_SEARCH, mInputString);
            } else if (!TextUtils.isEmpty(mSearchType) && mSearchType.equals("RetrievalProgramSerialList")) {
                LogUploadUtils.uploadLog(Constant.LOG_NODE_SCREEN, mScreenDataBuff.toString());
            }
        } else {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_SEARCH, mInputString);
        }

    }

    //键盘移动的位移动画，动画类型为属性动画
    public void slideView(View view, final float p1, final float p2) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", p1, p2);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

}
