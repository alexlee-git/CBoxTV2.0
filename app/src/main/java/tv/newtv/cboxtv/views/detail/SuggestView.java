package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.ProgramsInfo;
import tv.newtv.cboxtv.cms.listPage.model.ScreenInfo;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         17:10
 * 创建人:           weihaichao
 * 创建日期:          2018/5/5
 */
public class SuggestView extends RelativeLayout implements IEpisode {

    private static final String INFO_TEXT_TAG = "info_text";
    private String contentUUID;
    private int mType;
    private View currentFocus;
    private String mVideoType;
    private View controlView;
    private String leftUUID;
    private String rightUUID;
    private Disposable mDisposable;

    public SuggestView(Context context) {
        super(context);
        initalize();
    }

    public SuggestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalize();
    }

    public SuggestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize();
    }

    @Override
    public void destroy() {
        if(mDisposable != null){
            mDisposable.dispose();
            mDisposable = null;
        }
        controlView = null;
        currentFocus = null;
        removeAllViews();
    }

    private void ShowInfoTextView(String text) {
        TextView infoText = findViewWithTag(INFO_TEXT_TAG);
        if (infoText == null) {
            infoText = new TextView(getContext());
            infoText.setTag(INFO_TEXT_TAG);
            infoText.setTextAppearance(getContext(), R.style.ModuleTitleStyle);
            infoText.setText(text);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                    .WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            infoText.setLayoutParams(layoutParams);
            addView(infoText, layoutParams);
        } else {
            infoText.setText(text);
        }
    }

    private void initalize() {
//        ShowInfoTextView("正在加载...");
    }

    public View getDefaultFocus() {
        if (getChildCount() <= 0) return this;
        if (getChildAt(0) instanceof AiyaRecyclerView) {
            return ((AiyaRecyclerView) getChildAt(0)).getDefaultFocusView();
        } else {
            return currentFocus;
        }
    }

    public void setContentUUID(int type, String uuid, String channelId, String videoType, View
            controlLayout) {
        mType = type;
        contentUUID = uuid;
        mVideoType = videoType;
        controlView = controlLayout;

        if (contentUUID == null) {
            return;
        }
        if (!TextUtils.isEmpty(uuid) && uuid.length() > 2) {
            leftUUID = contentUUID.substring(0, 2);
            rightUUID = contentUUID.substring(contentUUID.length() - 2, contentUUID.length());
        } else if (uuid.length() == 2) {
            leftUUID = uuid;
            rightUUID = uuid;
        }
        Observable<ResponseBody> observable = null;
        switch (mType) {
            case EpisodeHelper.TYPE_PROGRAME_SAMETYPE:
                observable = EpisodeHelper.GetInterface(mType, channelId);
                break;

            case EpisodeHelper.TYPE_SEARCH:
                observable = EpisodeHelper.GetInterface(mType, mVideoType);
                break;
            default:
                observable = EpisodeHelper.GetInterface(mType, leftUUID,
                        rightUUID, contentUUID);
                break;

        }
        if (observable != null) {
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            mDisposable = d;
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                if (mType != EpisodeHelper.TYPE_SEARCH) {
                                    parseResult(responseBody.string());
                                } else {
                                    searchResult(responseBody.string());
                                }

                            } catch (Exception e) {
                                LogUtils.e(e.toString());
                                onLoadError(e.getMessage());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            onLoadError(e.getMessage());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    public void onActivityDestory() {
        unSubscribe();
    }

    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    private void searchResult(String string) {
        if (TextUtils.isEmpty(string)) {
            onLoadError("获取结果为空");
            return;
        }
        Gson mGson = new Gson();
        ScreenInfo mScreenInfo = mGson.fromJson(string, ScreenInfo.class);
        List<ScreenInfo.ResultListBean> ListBeans = mScreenInfo.getResultList();
        if (ListBeans != null && ListBeans.size() > 0) {
            ProgramSeriesInfo infoRecommdend = new ProgramSeriesInfo();
            List<ProgramsInfo> list = new ArrayList<>();
            int size = ListBeans.size();
            setVisibility(View.VISIBLE);
            if (controlView != null) {
                controlView.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < size; i++) {
                ScreenInfo.ResultListBean entity = ListBeans.get(i);
                if (entity != null) {
                    list.add(new ProgramsInfo(entity.getUUID(), entity.getName(),
                            entity.getContentType(), entity.getHpicurl(), entity.getHpicurl(), "",
                            "", "", "", "", "", "", "", "", entity.getDesc()));
                }
            }
            infoRecommdend.setData(list);
            buildUI(infoRecommdend);
        }
    }

    private void parseResult(String result) {
        if (TextUtils.isEmpty(result)) {
            onLoadError("获取结果为空");
            return;
        }
        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt("errorCode") == 0) {
                JSONObject obj = object.getJSONObject("data");
                Gson gson = new Gson();
                ProgramSeriesInfo info = gson.fromJson(obj.toString(), ProgramSeriesInfo.class);
                if (info != null && info.getData() != null && info.getData().size() != 0) {
                    buildUI(info);
                }
            } else {
                onLoadError(object.getString("errorMessage"));
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
            onLoadError(e.getMessage());
        }
    }

    private void buildUI(ProgramSeriesInfo info) {

        switch (mType) {
            case EpisodeHelper.TYPE_PROGRAME_XG:
                buildRecycleView(info);
                break;
            case EpisodeHelper.TYPE_SEARCH:
            case EpisodeHelper.TYPE_PROGRAME_STAR:
            case EpisodeHelper.TYPE_PROGRAME_SAMETYPE:
                buildListView(info);
                break;
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (controlView != null) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            controlView
                            .getLayoutParams();
                    layoutParams.height = (int) getResources().getDimension(R.dimen.height_83px);
                    controlView.setLayoutParams(layoutParams);
                    controlView.setVisibility(View.VISIBLE);
                }
                setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    private ProgramsInfo getItem(List<ProgramsInfo> infos, int
            index) {
        if (infos == null || infos.size() <= index) {
            return null;
        }
        return infos.get(index);
    }

    private void buildListView(ProgramSeriesInfo info) {
        if (info.getData().size() > 0) {
            removeAllViews();

            View view = LayoutInflater.from(getContext()).inflate(R.layout
                    .episode_six_content, this, false);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                    .WRAP_CONTENT);
            view.findViewWithTag("module_008_title_icon").setVisibility(View.VISIBLE);
            TextView titleview = view.findViewWithTag("module_008_title");
            if (titleview != null) {
                titleview.setVisibility(View.VISIBLE);
                titleview.setText(EpisodeHelper.getTitleByType(mType));
            }
            view.setLayoutParams(layoutParams);
            addView(view, layoutParams);

            List<ProgramsInfo> infos = info.getData();
            for (int index = 0; index < 8; index++) {
                final View target = view.findViewWithTag("cell_008_" + (index + 1));
                ProgramsInfo itemInfo = getItem(infos, index);
                SuggestViewHolder viewHolder = new SuggestViewHolder(target, index, itemInfo);
            }
        } else {
            onLoadError("暂时没有数据");
        }
    }


    private void buildRecycleView(ProgramSeriesInfo info) {
        if (info.getData().size() > 0) {
            removeAllViews();

            AiyaRecyclerView aiyaRecyclerView = new AiyaRecyclerView(getContext());
            aiyaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            aiyaRecyclerView.setAlign(AiyaRecyclerView.ALIGN_AUTO);
            int margin = getContext().getResources().getDimensionPixelOffset(R.dimen.width_78px);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                    .WRAP_CONTENT);
            layoutParams.leftMargin = margin;
            layoutParams.rightMargin = margin;
            aiyaRecyclerView.setLayoutParams(layoutParams);
            addView(aiyaRecyclerView, layoutParams);

            AiyaAdapter aiyaAdapter = new AiyaAdapter(info.getData());
            aiyaRecyclerView.setAdapter(aiyaAdapter);
        } else {
            onLoadError("暂时没有数据");
        }
    }

    private void onLoadError(String message) {
//        ShowInfoTextView(message);

        if (getParent() != null) {
            ViewGroup parentView = (ViewGroup) getParent();
            parentView.removeView(this);
            if (controlView != null) {
                parentView.removeView(controlView);
            }
        }
    }

    @Override
    public String getContentUUID() {
        return contentUUID;
    }

    @Override
    public boolean interuptKeyEvent(KeyEvent event) {
        if (getChildAt(0) == null
                || !(getChildAt(0) instanceof ViewGroup) || ((ViewGroup) getChildAt
                (0)).getChildCount() == 0) {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_DOWN) {
                if (!getChildAt(0).hasFocus()) {
                    getDefaultFocus().requestFocus();
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                View leftView = FocusFinder.getInstance().findNextFocus(this, findFocus(), View
                        .FOCUS_LEFT);
                if (leftView != null) {
                    leftView.requestFocus();
                }
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                View rightView = FocusFinder.getInstance().findNextFocus(this, findFocus(), View
                        .FOCUS_RIGHT);
                if (rightView != null) {
                    rightView.requestFocus();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        if (child != null) {
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int height = child.getMeasuredHeight();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private static class AiyaAdapter extends RecyclerView.Adapter<AiyaViewHolder> {
        private List<ProgramsInfo> mData;

        private AiyaAdapter(List<ProgramsInfo> data) {
            mData = data;
        }

        @Override
        public AiyaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .item_details_horizontal_layout, parent,
                    false);
            return new AiyaViewHolder(view);
        }

        private ProgramsInfo getItem(int postion) {
            if (mData == null) return null;
            if (mData.size() < postion) return null;
            if (postion < 0) return null;
            return mData.get(postion);
        }

        @Override
        public void onBindViewHolder(AiyaViewHolder holder, int position) {
            if (holder != null) {
                ProgramsInfo data = getItem(position);
                holder.setData(data);
            }
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }
    }

    private static class AiyaViewHolder extends RecyclerView.ViewHolder {

        ProgramsInfo infoData;
        private ImageView Poster;
        private TextView Title;
        private View FocusView;

        AiyaViewHolder(View itemView) {
            super(itemView);
            Poster = itemView.findViewWithTag("tag_poster_image");
            Title = itemView.findViewWithTag("tag_poster_title");
            FocusView = itemView.findViewWithTag("tag_img_focus");

            DisplayUtils.adjustView(itemView.getContext().getApplicationContext(), Poster,
                    FocusView, R.dimen.width_16dp, R.dimen.width_16dp);//view适配
        }

        public void setData(ProgramsInfo data) {
            if (data == null) return;
            infoData = data;
            Picasso.get()
                    .load(data.gethImage())
                    .resize(390, 214)
                    .into(Poster);

            Title.setText(data.getTitle());
            itemView.findViewById(R.id.id_module_view).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO 界面跳转
                    JumpUtil.detailsJumpActivity(view.getContext(),
                            infoData.getContentType(), infoData.getContentUUID());
                }
            });
            itemView.findViewById(R.id.id_module_view).setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    View viewMove = view.findViewWithTag("tag_poster_title");
                    if (viewMove != null) {
                        viewMove.setSelected(b);
                    }
                    FocusView.setVisibility(b ? View.VISIBLE : View.GONE);
                    if (b) {
                        ScaleUtils.getInstance().onItemGetFocus(itemView.findViewById(R.id
                                .id_module_view));
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(itemView.findViewById(R.id
                                .id_module_view));
                    }
                }
            });
        }
    }

    private class SuggestViewHolder {
        ProgramsInfo itemInfo;

        SuggestViewHolder(final View target, int index, ProgramsInfo info) {
            itemInfo = info;
            if (target != null) {
                if (itemInfo != null) {
                    target.setVisibility(View.VISIBLE);
                    ImageView poster = target.findViewWithTag("tag_poster_image");
                    final View focusView = target.findViewWithTag("tag_img_focus");
                    TextView title = target.findViewWithTag("tag_poster_title");

                    //适配
                    DisplayUtils.adjustView(getContext(), poster, focusView, R.dimen.width_16dp,
                            R.dimen.width_16dp);//适配

                    if (!TextUtils.isEmpty(itemInfo.getvImage())) {
                        poster.setScaleType(ImageView.ScaleType.FIT_XY);
                        RequestCreator picasso = Picasso.get()
                                .load(itemInfo.getvImage())
                                .transform(new PosterCircleTransform(target.getContext(), 4))
                                .priority(Picasso.Priority.HIGH)
                                .stableKey(itemInfo.getvImage())
                                .resize(240, 360)
                                .config(Bitmap.Config.RGB_565);
                        picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                                .focus_240_360);
                        picasso.into(poster);
                    } else {
                        poster.setScaleType(ImageView.ScaleType.FIT_XY);
                        RequestCreator picasso = Picasso.get()
                                .load(R.drawable.focus_240_360)
                                .priority(Picasso.Priority.HIGH)
                                .transform(new PosterCircleTransform(target.getContext(), 4))
                                .resize(240, 360)
                                .config(Bitmap.Config.RGB_565);
                        picasso = picasso.placeholder(R.drawable.focus_240_360).error(R.drawable
                                .focus_240_360);
                        picasso.into(poster);
                    }

//                    Picasso.with(target.getContext())
//                            .load(itemInfo.getvImage())
//                            .resize(240, 360)
//                            .into(poster);

                    title.setText(itemInfo.getTitle());

                    target.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO 界面跳转
                            LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL_SUGGESt, itemInfo
                                    .getContentUUID());
                            JumpUtil.detailsJumpActivity(view.getContext(),
                                    itemInfo.getContentType(), itemInfo.getContentUUID());
                        }
                    });

                    target.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean b) {

                            View viewMove = view.findViewWithTag("tag_poster_title");
                            if (viewMove != null) {
                                viewMove.setSelected(b);
                            }

                            focusView.setVisibility(b ? View.VISIBLE : View.GONE);
                            if (b) {
                                ScaleUtils.getInstance().onItemGetFocus(target);
                            } else {
                                ScaleUtils.getInstance().onItemLoseFocus(target);
                            }
                        }
                    });

                    if (currentFocus == null && index == 0) {
                        currentFocus = target;
                    }
                } else {
                    target.setVisibility(View.GONE);
                }
            }
        }
    }


}

