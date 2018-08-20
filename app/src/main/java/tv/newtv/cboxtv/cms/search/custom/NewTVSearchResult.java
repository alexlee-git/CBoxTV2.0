package tv.newtv.cboxtv.cms.search.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.NewTVScroller;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.search.bean.SearchResultInfos;
import tv.newtv.cboxtv.cms.search.fragment.BaseFragment;
import tv.newtv.cboxtv.cms.search.fragment.ColumnFragment;
import tv.newtv.cboxtv.cms.search.fragment.DramaFragment;
import tv.newtv.cboxtv.cms.search.fragment.PersonFragment;
import tv.newtv.cboxtv.cms.search.listener.INotifySearchResultData;
import tv.newtv.cboxtv.cms.search.listener.OnGetSearchResultFocus;
import tv.newtv.cboxtv.cms.util.DisplayUtils;
import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * 项目名称： NewTVLauncher
 * 类描述：搜索结果页
 * 创建人：wqs
 * 创建时间： 2018/3/9 0009 12:57
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class NewTVSearchResult extends RelativeLayout {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private OnGetSearchResultFocus mOnGetSearchResultFocus;
    private FrameLayout mDramaFrameLayout, mPersonFrameLayout, mColumnFrameLayout;
    private SearchViewPager mViewpager;
    private List<BaseFragment> mFragments;
    private SearchViewPagerAdapter mViewPagerAdapter;
    private DramaFragment mDramaFragment;
    private PersonFragment mPersonFragment;
    private ColumnFragment mColumnFragment;
    private TextView mDramaTitle, mPersonTitle, mColumnTitle;
    private ImageView mPersonFocusImageView, mDramaFocusImageView, mColumnFocusImageView;
    private ImageView mLeftArrow;
    private TextView mSearchResultEmpty;
    private float SearchViewResultWidth = 409;
    private Gson mGson;
    private List<BaseFragment> mCacheFragment;

    public NewTVSearchResult(Context context) {
        super(context);
        initLayout(context);
    }

    public NewTVSearchResult(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public NewTVSearchResult(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    //填充布局
    private void initLayout(Context context) {
        mContext = context;
        View view = View.inflate(mContext, R.layout.newtv_search_result_page_list_result, this);
        initView(view);
    }


    private void initView(View view) {
        mDramaFrameLayout = (FrameLayout) view.findViewById(R.id.id_frameLayout_result_label_drama);
        mPersonFrameLayout = (FrameLayout) view.findViewById(R.id.id_frameLayout_result_label_person);
        mColumnFrameLayout = (FrameLayout) view.findViewById(R.id.id_frameLayout_result_label_column);
        mDramaTitle = (TextView) view.findViewById(R.id.id_result_label_drama_title);
        mPersonTitle = (TextView) view.findViewById(R.id.id_result_label_person_title);
        mColumnTitle = (TextView) view.findViewById(R.id.id_result_label_column_title);
        mSearchResultEmpty = (TextView) view.findViewById(R.id.id_search_result_empty);
        mPersonFocusImageView = (ImageView) view.findViewById(R.id.id_result_label_person_focus_bottom);
        mDramaFocusImageView = (ImageView) view.findViewById(R.id.id_result_label_drama_focus_bottom);
        mColumnFocusImageView = (ImageView) view.findViewById(R.id.id_result_label_column_focus_bottom);
        mViewpager = (SearchViewPager) view.findViewById(R.id.id_search_result_viewpager);
        mLeftArrow = (ImageView) view.findViewById(R.id.id_result_left_arrow);
        init();
    }

    private void init() {
        mViewpager.setScrollable(true);
        SearchViewResultWidth = DisplayUtils.translate((int) SearchViewResultWidth, 0);
        mViewpager.setCustomScroller(new NewTVScroller(mContext, new LinearInterpolator(), 250));
        mViewpager.setOffscreenPageLimit(2);
        mFragments = new ArrayList<>();
        mCacheFragment = new ArrayList<>();
        mGson = new Gson();
        final Bundle mDramaBundle = new Bundle();
        mDramaFragment = DramaFragment.newInstance(mDramaBundle);
        mDramaFragment.setLabelView(mDramaFrameLayout);
        Bundle mPersonBundle = new Bundle();
        mPersonFragment = PersonFragment.newInstance(mPersonBundle);
        mPersonFragment.setLabelView(mPersonFrameLayout);
        Bundle mColumnBundle = new Bundle();
        mColumnFragment = ColumnFragment.newInstance(mColumnBundle);
        mColumnFragment.setLabelView(mColumnFrameLayout);
        mFragments.add(mColumnFragment);
        mFragments.add(mPersonFragment);
        mFragments.add(mDramaFragment);
        mDramaFragment.setDramaLabelTitle(mDramaTitle);
        mPersonFragment.setPersonLabelTitle(mPersonTitle);
        mColumnFragment.setColumnLabelTitle(mColumnTitle);
        mDramaFragment.setLabelFocusView(mDramaFocusImageView);
        mPersonFragment.setLabelFocusView(mPersonFocusImageView);
        mColumnFragment.setLabelFocusView(mColumnFocusImageView);
        mViewPagerAdapter = new SearchViewPagerAdapter(((FragmentActivity) mContext).getSupportFragmentManager(), mFragments);
        mViewpager.setAdapter(mViewPagerAdapter);
        mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.e(TAG, "---mViewpager:position:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mDramaFrameLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mDramaTitle.setTextColor(Color.parseColor("#13d6f9"));
                    mDramaFocusImageView.setVisibility(View.VISIBLE);
                    mOnGetSearchResultFocus.notifySearchResultFocus(true, -1, view);
                    mDramaFragment.setDramaLabelTitle(mDramaTitle);
                    for (int i = 0; i < mFragments.size(); i++) {
                        if (mFragments.get(i) == mDramaFragment) {
                            showIndexPage(i);
                        }
                    }
                } else {
                    if (!mDramaFragment.mFocusStatus) {
                        mDramaTitle.setTextColor(Color.parseColor("#ededed"));
                        mDramaFocusImageView.setVisibility(View.INVISIBLE);
                    }
                    mOnGetSearchResultFocus.notifySearchResultFocus(false, -1, view);
                }
            }
        });
        mPersonFrameLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mOnGetSearchResultFocus.notifySearchResultFocus(true, -2, view);
                    mPersonTitle.setTextColor(Color.parseColor("#13d6f9"));
                    mPersonFocusImageView.setVisibility(View.VISIBLE);
                    mPersonFragment.setPersonLabelTitle(mPersonTitle);
                    for (int i = 0; i < mFragments.size(); i++) {
                        if (mFragments.get(i) == mPersonFragment) {
                            showIndexPage(i);
                        }
                    }
                } else {
                    mOnGetSearchResultFocus.notifySearchResultFocus(false, -2, view);
                    if (!mPersonFragment.mFocusStatus) {
                        mPersonTitle.setTextColor(Color.parseColor("#ededed"));
                        mPersonFocusImageView.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        mColumnFrameLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mOnGetSearchResultFocus.notifySearchResultFocus(true, -2, view);
                    mColumnTitle.setTextColor(Color.parseColor("#13d6f9"));
                    if (mColumnFragment.getEmptyView() != null) {
                        slideView(mColumnFragment.getEmptyView(), 0, SearchViewResultWidth);
                    }
                    mColumnFocusImageView.setVisibility(View.VISIBLE);
                    mColumnFragment.setColumnLabelTitle(mColumnTitle);
                    for (int i = 0; i < mFragments.size(); i++) {
                        if (mFragments.get(i) == mColumnFragment) {
                            showIndexPage(i);
                        }
                    }
                } else {
                    mOnGetSearchResultFocus.notifySearchResultFocus(false, -2, view);
                    if (mColumnFragment.getEmptyView() != null) {
                        slideView(mColumnFragment.getEmptyView(), SearchViewResultWidth, 0);
                    }
                    if (!mColumnFragment.mFocusStatus) {
                        mColumnTitle.setTextColor(Color.parseColor("#ededed"));
                        mColumnFocusImageView.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
        mDramaFrameLayout.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPersonFrameLayout.getVisibility() == VISIBLE) {
                            mPersonFrameLayout.requestFocus();
                            return true;
                        } else if (mColumnFrameLayout.getVisibility() == VISIBLE) {
                            mColumnFrameLayout.requestFocus();
                            return true;
                        } else if (mKeyboardLastFocusView != null) {
                            mOnGetSearchResultFocus.notifySearchResultFocus(false, -1, null);
                            mKeyboardLastFocusView.requestFocus();
                            return true;
                        }
                    } else if (i == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (mDramaFragment.mDataStatus) {
                            if (mDramaFragment.mLastFocusView != null) {
                                mDramaFragment.mLastFocusView.requestFocus();
                                return true;
                            } else {
                                if (mDramaFragment.getRecyclerView().getChildAt(0) != null) {
                                    mDramaFragment.getRecyclerView().getChildAt(0).requestFocus();
                                    return true;
                                }
                            }
                        } else {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        mPersonFrameLayout.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mColumnFrameLayout.getVisibility() == VISIBLE) {
                            mColumnFrameLayout.requestFocus();
                            return true;
                        } else if (mKeyboardLastFocusView != null) {
                            mOnGetSearchResultFocus.notifySearchResultFocus(false, -1, null);
                            mKeyboardLastFocusView.requestFocus();
                            return true;
                        }

                    } else if (i == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (mPersonFragment.mDataStatus) {
                            if (mPersonFragment.mLastFocusView != null) {
                                mPersonFragment.mLastFocusView.requestFocus();
                                return true;
                            } else {
                                if (mPersonFragment.getRecyclerView().getChildAt(0) != null) {
                                    mPersonFragment.getRecyclerView().getChildAt(0).requestFocus();
                                    return true;
                                }
                            }
                        } else {
                            return true;
                        }
                    } else if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mDramaFrameLayout.getVisibility() == VISIBLE) {
                            mDramaFrameLayout.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        mColumnFrameLayout.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mKeyboardLastFocusView != null) {
                            mOnGetSearchResultFocus.notifySearchResultFocus(false, -1, null);
                            mKeyboardLastFocusView.requestFocus();

                            return true;
                        }
                    } else if (i == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (mColumnFragment.mDataStatus) {
                            if (mColumnFragment.mLastFocusView != null) {
                                mColumnFragment.mLastFocusView.requestFocus();
                                return true;
                            } else {
                                if (mColumnFragment.getRecyclerView().getChildAt(0) != null) {
                                    mColumnFragment.getRecyclerView().getChildAt(0).requestFocus();
                                    return true;
                                }
                            }
                        } else {
                            return true;
                        }
                    } else if (i == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mPersonFrameLayout.getVisibility() == VISIBLE) {
                            mPersonFrameLayout.requestFocus();
                            return true;
                        } else if (mDramaFrameLayout.getVisibility() == VISIBLE) {
                            mDramaFrameLayout.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    public void setExternalParams(Bundle bundle) {
        mDramaFragment.setExternalParams(bundle);
        mPersonFragment.setExternalParams(bundle);
        mColumnFragment.setExternalParams(bundle);
        mColumnFrameLayout.requestFocus();
    }

    public void setKey(String key) {

        try {
            Log.e(TAG, "---搜索关键字：" + key);
            mInputString = key;
            requestSearchKey(key);
        } catch (Exception e) {
            //e.printStackTrace();
            Log.e(TAG, "---setKey:Exception" + e.toString());
        }
    }

    public void setEmptyViewVisiable(int visiable) {

        if (mColumnFragment.getEmptyView() != null) {
            mColumnFragment.getEmptyView().setVisibility(visiable);
            mSearchResultEmpty.setVisibility(visiable);
        }

    }

    private void requestSearchKey(final String key) {
        mCacheFragment.clear();
        NetClient.INSTANCE.getSearchResultApi().getKeywordSearchResultResponse(Constant.APP_KEY, Constant.CHANNEL_ID, "TV", key, "name", "-1", 0, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(ResponseBody result) throws Exception {
                SearchResultInfos mSearchResultInfos = mGson.fromJson(result.string(), SearchResultInfos.class);
                if (mSearchResultInfos.getResultList() != null && mSearchResultInfos.getResultList().size() > 0) {
                    mColumnFrameLayout.setVisibility(VISIBLE);
                    if (!mCacheFragment.contains(mColumnFragment)) {
                        mCacheFragment.add(mColumnFragment);
                    }
                } else {
                    mColumnFrameLayout.setVisibility(GONE);
                }

                return NetClient.INSTANCE.getSearchResultApi().getKeywordSearchResultResponse(Constant.APP_KEY, Constant.CHANNEL_ID, "FG", key, "name", "-1", 0, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        }).flatMap(new Function<ResponseBody, ObservableSource<ResponseBody>>() {
            @Override
            public ObservableSource<ResponseBody> apply(ResponseBody result) throws Exception {
                SearchResultInfos mSearchResultInfos = mGson.fromJson(result.string(), SearchResultInfos.class);
                if (mSearchResultInfos.getResultList() != null && mSearchResultInfos.getResultList().size() > 0) {
                    mPersonFrameLayout.setVisibility(VISIBLE);
                    if (!mCacheFragment.contains(mPersonFragment)) {
                        mCacheFragment.add(mPersonFragment);
                    }
                } else {
                    mPersonFrameLayout.setVisibility(GONE);
                }
                return NetClient.INSTANCE.getSearchResultApi().getKeywordSearchResultResponse(Constant.APP_KEY, Constant.CHANNEL_ID, "PS;CS;CG;PG", key, "name", "-1", 0, 1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        }).subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ResponseBody result) {
                try {
                    SearchResultInfos mSearchResultInfos = mGson.fromJson(result.string(), SearchResultInfos.class);
                    if (mSearchResultInfos.getResultList() != null && mSearchResultInfos.getResultList().size() > 0) {
                        mDramaFrameLayout.setVisibility(VISIBLE);
                        if (!mCacheFragment.contains(mDramaFragment)) {
                            mCacheFragment.add(mDramaFragment);
                        }
                    } else {
                        mDramaFrameLayout.setVisibility(GONE);
                    }
                    if (mCacheFragment.size() > 0) {
                        mSearchResultEmpty.setVisibility(GONE);
                        mFragments.clear();
                        mFragments.addAll(mCacheFragment);
                        mViewPagerAdapter.notifyDataSetChanged();
                        mDramaFragment.setKey(key);
                        mPersonFragment.setKey(key);
                        mColumnFragment.setKey(key);
                        showIndexPage(0);
                    } else {
                        mFragments.clear();
                        mViewPagerAdapter.notifyDataSetChanged();
                        if(!TextUtils.isEmpty(key)){
                            mSearchResultEmpty.setText(getResources().getString(R.string.search_enter_failure));
                            mSearchResultEmpty.setVisibility(VISIBLE);
                        }

                    }
                } catch (IOException e) {
                    LogUtils.e(e);
                }

            }

            @Override
            public void onError(Throwable e) {
                mFragments.clear();
                mViewPagerAdapter.notifyDataSetChanged();
                mSearchResultEmpty.setText(getResources().getString(R.string.search_fail_agin));
                mSearchResultEmpty.setVisibility(VISIBLE);
            }

            @Override
            public void onComplete() {
                LogUtils.i("onComplete");
            }
        });


    }

    public int getNextFocusRightId() {
        if (mColumnFrameLayout.getVisibility() == VISIBLE) {
            return mColumnFrameLayout.getId();
        } else if (mPersonFrameLayout.getVisibility() == VISIBLE) {
            return mPersonFrameLayout.getId();
        } else if (mDramaFrameLayout.getVisibility() == VISIBLE) {
            return mDramaFocusImageView.getId();
        } else {
            return 0;
        }
    }

    /**
     * 显示指定页面
     *
     * @param index 选择指定页面
     */
    private void showIndexPage(int index) {
        //先强制设定跳转到指定页面
        try {
            Field field = mViewpager.getClass().getDeclaredField("mCurItem");// 拿到mCurItem域
            field.setAccessible(true);
            field.setInt(mViewpager, index);
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }

//        //然后调用下面的函数刷新数据
//        SearchViewPagerAdapter adapter = (SearchViewPagerAdapter) mViewpager.getAdapter();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }

        //再调用setCurrentItem()函数设置一次
        mViewpager.setCurrentItem(index);
    }

    public void setResultFocus(boolean focusStatus) {
        if (focusStatus) {
            mLeftArrow.setVisibility(View.VISIBLE);
        } else {
            mLeftArrow.setVisibility(View.INVISIBLE);
        }
    }

    //获得键盘最后一个获得焦点的view
    private View mKeyboardLastFocusView;

    public void setKeyboardLastFocusView(View view) {
        mKeyboardLastFocusView = view;
        mDramaFragment.setKeyboardLastFocusView(view);
        mPersonFragment.setKeyboardLastFocusView(view);
        mColumnFragment.setKeyboardLastFocusView(view);
    }

    private String mInputString;//监控输入框值的变化

    public void setOnGetSearchResultFocus(OnGetSearchResultFocus onGetSearchResultFocus) {
        mOnGetSearchResultFocus = onGetSearchResultFocus;
        mDramaFragment.setOnGetSearchResultFocus(onGetSearchResultFocus);
        mPersonFragment.setOnGetSearchResultFocus(onGetSearchResultFocus);
        mColumnFragment.setOnGetSearchResultFocus(onGetSearchResultFocus);
    }

    //view的位移动画，动画类型为属性动画
    public void slideView(View view, final float p1, final float p2) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", p1, p2);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }
}
