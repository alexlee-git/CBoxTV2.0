package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.Row;
import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.NetworkManager;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.GlideUtil;
import com.newtv.libs.util.ImageUtils;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.LogUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import tv.newtv.cboxtv.MultipleClickListener;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.superscript.model.SuperscriptInfo;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.ModuleLayoutManager;
import tv.newtv.cboxtv.views.custom.AutoSizeTextView;
import tv.newtv.cboxtv.views.custom.LivePlayView;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.viewholder
 * 创建事件:         18:12
 * 创建人:           weihaichao
 * 创建日期:          2018/10/18
 */
public class BlockBuilder {

    public static final String TAG = BlockBuilder.class.getSimpleName();
    private final String SHOW_BLOCK_TITLE = "1";
    private final String DO_NOT_SHOW_BLOCK_TITLE = "0";
    private Context mContext;
    private StringBuilder idBuffer;
    private Interpolator mSpringInterpolator;
    private String PicassoTag = "";
    private String PlayerUUID = "";
    private boolean showFirstTitle = false;
    private AdContract.Presenter mAdPresenter;

    public BlockBuilder(Context context) {
        mContext = context;

        mSpringInterpolator = new OvershootInterpolator(2.2f);
        mAdPresenter = new AdContract.AdPresenter(context, null);
    }

    public void destroy() {
        if (mAdPresenter != null) {
            mAdPresenter.destroy();
            mAdPresenter = null;
        }
        mContext = null;
    }

    public UniversalViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        UniversalViewHolder holder = null;
        if (viewType > 0) {
            // 根据viewType获取相应的布局文件
            int layoutResId = ModuleLayoutManager.getInstance().getLayoutResFileByViewType
                    (viewType);
            holder = new UniversalViewHolder(LayoutInflater.from(parent
                    .getContext()).inflate
                    (layoutResId, parent, false));
        } else if (viewType == 0) {
            holder = new UniversalViewHolder(new AutoBlockType(parent.getContext()));
        }
        return holder;
    }

    int parseItemViewType(Page item) {
        String layoutCode = item.getLayoutCode();
        if (!TextUtils.isEmpty(layoutCode)) {
            return Integer.parseInt(layoutCode.substring(layoutCode.indexOf("_") + 1));
        }
        return -1;
    }

    int getItemViewType(int position, Page item) {
        try {
            if (item != null) {
                if (!"6".equals(item.getBlockType())) {
                    String layoutCode = item.getLayoutCode();
                    if (!TextUtils.isEmpty(layoutCode)) {
                        return Integer.parseInt(layoutCode.substring(layoutCode.indexOf("_")
                                + 1));
                    }
                } else {
                    return 0;
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return -1;
    }

    void showFirstLineTitle(boolean value) {
        showFirstTitle = value;
    }

    void setPlayerUUID(String uuid) {
        PlayerUUID = uuid;
    }

    void setPicassoTag(String tag) {
        PicassoTag = tag;
    }

    public void build(final Page moduleItem, View itemView, int position) {
        if (moduleItem == null) {
            return;
        }

        if (TextUtils.equals("0", moduleItem.getBlockType())) {
            //TODO 默认区块类型

            final String layoutCode = moduleItem.getLayoutCode(); // 形如"layout_002"
            final String layoutId = layoutCode.substring(layoutCode.indexOf("_") + 1); //
            // 形如"002"

            // 处理组件标题与icon
            if (TextUtils.equals(SHOW_BLOCK_TITLE, moduleItem.getHaveBlockTitle())) {
                String moduleTitleText = moduleItem.getBlockTitle();
                if (!TextUtils.isEmpty(moduleTitleText) && (position != 0 || showFirstTitle)) {
                    // 填充组件标题
                    String titleId = "module_" + layoutId + "_title";
                    TextView moduleTitleTextView = ((TextView) itemView.findViewWithTag(titleId));
                    if (moduleTitleTextView != null) {

                        TextPaint paint = moduleTitleTextView.getPaint();
                        paint.setFakeBoldText(true);//字体加粗

                        moduleTitleTextView.setText(moduleItem.getBlockTitle());
                        moduleTitleTextView.setVisibility(View.VISIBLE);
                    }

                    String titleIconId = "module_" + layoutId + "_title_icon";
                    ImageView titleIcon = (ImageView) itemView.findViewWithTag(titleIconId);
                    String iconUrl = moduleItem.getBlockImg();
                    if (titleIcon != null) {
                        titleIcon.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(iconUrl) && !iconUrl.equals(titleIcon.getTag()
                        )) {
                            titleIcon.setTag(iconUrl);
                            Picasso.get().load(iconUrl).into(titleIcon);
                        }
                    }
                }
            }

            LogUtils.i(TAG, layoutCode);
            int posSize = ModuleLayoutManager.getInstance().getSubWidgetSizeById(layoutCode);
            Log.i(TAG, "layoutCode=" + layoutCode);
            for (int i = 0; i < posSize; ++i) {
                if (moduleItem.getPrograms().size() - 1 < i) break;
                final Program info = moduleItem.getPrograms().get(i);

                // 拿1号组件的1号推荐位为例, 其子海报控件的id为 : cell_001_1_poster
                final String cellCode = info.getCellCode();
                if (TextUtils.isEmpty(cellCode)) {
                    continue;
                }

                final String viewId = cellCode.substring(cellCode.lastIndexOf("_") + 1);//
                // viewId是形如 "1", "2"这样的字符串

                // 拿1号组件的1号推荐位为例, 其海报控件的id为 : cell_001_1_poster
                String posterWidgetId = generateViewId(layoutId, viewId, "cell", "poster", "_");
                // 拿8号组件的1号推荐位为例, 其海报控件的id为 : cell_008_1_focus
                String focusWidgetId = generateViewId(layoutId, viewId, "cell", "focus", "_");
                // 拿1号组件的1号推荐位为例, 其推荐位标题控件的id为 : cell_001_1_title
                String titleWidgetId = generateViewId(layoutId, viewId, "cell", "title", "_");

                // 拿1号组件的1号推荐位为例, 其推荐位的根控件id为 : cell_001_1
                final String frameLayoutId = posterWidgetId.substring(0, posterWidgetId.indexOf
                        ("poster") - 1);

                // 给推荐位设置监听器
                final FrameLayout frameLayout = (FrameLayout) itemView.findViewWithTag
                        (frameLayoutId);
                if (frameLayout != null) {
                    //屏幕适配
                    if (!"005".equals(layoutId) && !"008".equals(layoutId)) {
                        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        frameLayout.setLayoutParams(params);
                    }

                    // 按需添加角标控件
                    processSuperscript(layoutCode, info, frameLayout);

                    // 按需添加标题控件
                    processTitle(layoutCode, info.getTitle(), info.getSubTitle(), frameLayout);

                    // onFocusChangeListener
                    frameLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (hasFocus) {
                                onItemGetFocus(layoutId, view);
//                                StatusBarManager.getInstance().setFocusable(true);
                                frameLayout.setTag(cellCode);

                                TextView title = (TextView) frameLayout.getTag(R.id.tag_title);
                                if (title != null) {
                                    title.setVisibility(View.VISIBLE);
                                    title.setSelected(true);
                                }
                            } else {
                                onItemLoseFocus(layoutId, view);
                                TextView title = (TextView) frameLayout.getTag(R.id.tag_title);
                                if (title != null) {
                                    title.setSelected(false);
                                }
                            }
                        }
                    });

                    // onClickListener
                    frameLayout.setOnClickListener(new MultipleClickListener() {
                        @Override
                        protected void onMultipleClick(View view) {
                            processOpenCell(view, info, moduleItem.getBlockId(), layoutCode);
                        }
                    });

                    // 如果是第5和第8号组件,则设置推荐位标题.因为按照约定只有第5和第8套组件留有推荐位标题控件
                    if (TextUtils.equals("005", layoutId) || TextUtils.equals("008",
                            layoutId)) {
                        TextView textView = (TextView) itemView.findViewWithTag(titleWidgetId);
                        View focusView = (View) itemView.findViewWithTag(focusWidgetId);

                        if (textView != null && !TextUtils.isEmpty(info.getTitle())) {
                            textView.setText(info.getTitle());
                        }
                        frameLayout.setTag(R.id.tag_textview, textView);
                        frameLayout.setTag(R.id.tag_imageview, focusView);
                    }

//                    processFirstRowRecommendUpKeyEvent(frameLayout, position, layoutCode, info);

                    // 让默认获取焦点的推荐位获取焦点
                    /*
                    if (Constant.isInitStatus && position == 0 && cellCode.substring(cellCode
                            .lastIndexOf("_") + 1).equals("1")) {
                        frameLayout.setFocusableInTouchMode(true);
                        frameLayout.requestFocus();
                        Constant.isInitStatus = false;
                    }
                    */

//                    CmsLiveUtil.isInPlay(info.getLiveLoopType(), info.getLiveParam(), info
//                            .getPlayStartTime(), info.getPlayEndTime(), frameLayout);

                }

                // 是否圆角
                boolean hasCorner = hasCorner4Cup(layoutId, viewId);
                // 加载海报图
                final View posterView = itemView.findViewWithTag(posterWidgetId);
                RecycleImageView recycleImageView = null;
                if (posterView instanceof RecycleImageView) {
                    recycleImageView = (RecycleImageView) posterView;
                } else if (posterView instanceof LivePlayView) {
                    ((LivePlayView) posterView).setProgramInfo(info);
                    ((LivePlayView) posterView).setUUID(PlayerUUID);
                    recycleImageView = ((LivePlayView) posterView).getPosterImageView();
                }

                if (recycleImageView != null) {
                    if (info.isAd() != 1) {
                        showPosterByCMS(posterWidgetId, recycleImageView, info.getImg(),
                                hasCorner);
                    } else {
                        Log.e(Constant.TAG, "block id : " + moduleItem.getBlockId() + ", " +
                                "cellcode" +
                                " : "
                                + info.getCellCode() + ", isAd : " + info.isAd());
                        showPosterByAD(posterWidgetId, moduleItem, recycleImageView, info,
                                hasCorner);
                    }
                }
            }
        } else if (TextUtils.equals("6", moduleItem.getBlockType())) {
            //TODO 自动区块类型
            ((AutoBlockType) itemView).build(moduleItem);
        }
    }

    /**
     * 31号模版专门给世界杯使用，过滤31号模版中的3、4、5、6view不需要圆角
     * 005号模版是人物头像，也不需要圆角
     *
     * @param viewId
     * @return
     */
    boolean hasCorner4Cup(String module, String viewId) {
        boolean hasCorner = true;
        if ("005".equals(module)) {
            hasCorner = false;
        } else if (module.equals("031")) {
            if ("3".equals(viewId) || "4".equals(viewId) || "5".equals(viewId) || "6".equals
                    (viewId)) {
                hasCorner = false;
            }
        }
        return hasCorner;
    }

    /**
     * 推荐位显示广告
     */
    private void showPosterByAD(final String imageId, final Page moduleItem, final RecycleImageView
            imageView, final Program info, final boolean hasCorner) {

        if (imageView == null) {
            return;
        }

        if (info == null) {
            return;
        }

        if (moduleItem == null) {
            return;
        }

        mAdPresenter.getAdByType(Constant.AD_DESK, moduleItem.getBlockId() + "_" + info
                        .getCellCode(),
                "", null, new AdContract.Callback() {
                    @Override
                    public void showAd(@Nullable String type, @Nullable String url, @Nullable
                            HashMap<?, ?> hashMap) {
                        LogUtils.e(TAG, "load " + imageId + " AD image path=" + url);
                        if (TextUtils.isEmpty(url)) {
                            showPosterByCMS(imageId, imageView, info.getImg(), hasCorner);
                        } else {
                            int width = imageView.getLayoutParams().width;
                            int height = imageView.getLayoutParams().height;
                            int placeHolderResId = ImageUtils.getProperPlaceHolderResId(mContext,
                                    width, height);
                            if (placeHolderResId != 0) {
                                imageView.Tag(PicassoTag).placeHolder(placeHolderResId)
                                        .hasCorner(hasCorner).load(url);
                            } else {
                                imageView.Tag(PicassoTag).hasCorner(hasCorner).load(url);
                            }
                        }
                    }
                });
    }

    /**
     * 推荐位显示海报
     */
    void showPosterByCMS(String imageId, final RecycleImageView imageView, final String
            imgUrl, boolean isCorner) {
        LogUtils.e(TAG, "load " + imageId + " cms image path=" + imgUrl);
        if (imageView != null) {
            int width = imageView.getLayoutParams().width;
            int height = imageView.getLayoutParams().height;

            int placeHolderResId = ImageUtils.getProperPlaceHolderResId(imageView.getContext(),
                    width, height);
            GlideUtil.loadImage(imageView.getContext(), imageView,
                    imgUrl, placeHolderResId, placeHolderResId, isCorner);
        } else {
            Log.e(Constant.TAG, "未找到的控件地址 : ");
        }
    }

    /**
     * @param layoutId
     * @param viewId
     * @param widgetIdPrefix
     * @param widgetId
     * @param delimiter
     * @return
     */
    String generateViewId(String layoutId, String viewId, String widgetIdPrefix, String
            widgetId, String delimiter) {
        if (idBuffer == null) {
            idBuffer = new StringBuilder(Constant.BUFFER_SIZE_16);
        }

        idBuffer.delete(0, idBuffer.length());
        idBuffer.append(widgetIdPrefix)
                .append(delimiter)
                .append(layoutId)
                .append(delimiter)
                .append(viewId)
                .append(delimiter)
                .append(widgetId)
                .trimToSize(); // "cell_001_1_poster"

        return idBuffer.toString();
    }

    protected void onItemGetFocus(String layoutId, final View view) {
        if (TextUtils.equals("005", layoutId) || TextUtils.equals("008", layoutId)) {
            View focusView = (View) view.getTag(R.id.tag_imageview);
            TextView focusTextView = (TextView) view.getTag(R.id.tag_textview);
            focusTextView.setSelected(true);

            if ("005".equals(layoutId)) {
                focusView.setVisibility(View.VISIBLE);
            } else {

//                focusView.requestFocus();
                focusView.setBackgroundResource(R.drawable.pos_zui_27px);
            }
        }

        LivePlayView livePlayView = getLivePlayView(view);
        if (livePlayView != null && livePlayView.isVideoType()) {
            return;
        }

        String tag = (String) view.getTag();
        if (tag.equals("cell_017_1") || tag.equals("cell_019_1") || tag.equals("cell_019_2")) {
            //直接放大view
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.07f, 1.0f, 1.07f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(500);
            sa.setInterpolator(mSpringInterpolator);
            view.bringToFront();
            view.startAnimation(sa);
        } else {
            //直接放大view
            ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.bringToFront();
            view.startAnimation(sa);

        }
    }

    protected void onItemLoseFocus(String layoutId, View view) {
        if (TextUtils.equals("005", layoutId) || TextUtils.equals("008", layoutId)) {
            View focusView = (View) view.getTag(R.id.tag_imageview);
            TextView focusTextView = (TextView) view.getTag(R.id.tag_textview);
            focusTextView.setSelected(false);

            if ("005".equals(layoutId)) {
                focusView.setVisibility(View.INVISIBLE);
            } else {

//                focusView.clearFocus();
                focusView.setBackgroundResource(0);
//                focusView.setBackgroundColor(mContext.getResources().getColor(R.color.color) );
            }
        }

        LivePlayView livePlayView = getLivePlayView(view);
        if (livePlayView != null && livePlayView.isVideoType()) {
            return;
        }

        String tag = (String) view.getTag();
        if (tag.equals("cell_017_1") || tag.equals("cell_019_1") || tag.equals("cell_019_2")) {
            ScaleAnimation sa = new ScaleAnimation(1.07f, 1.0f, 1.07f, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(500);
            sa.setInterpolator(mSpringInterpolator);
            view.startAnimation(sa);
        } else {
            // 直接缩小view
            ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                    .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            sa.setFillAfter(true);
            sa.setDuration(400);
            sa.setInterpolator(mSpringInterpolator);
            view.startAnimation(sa);
        }
    }

    void processOpenCell(View view, Object info, String blockId, String layoutCode) {
        if (!NetworkManager.getInstance().isConnected()) {
            Toast.makeText(mContext, R.string.net_error, Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder logBuff = new StringBuilder(Constant.BUFFER_SIZE_16);
        //进入推荐位
        if (info instanceof Program) {
            logBuff.append(0)
                    .append(",")
                    .append(blockId)
                    .append("+")
                    .append(layoutCode)
                    .append("+")
                    .append(((Program) info).getCellCode())
                    .append(",")
                    .append(((Program) info).getContentId())
                    .append(",")
                    .append(((Program) info).getContentType())
                    .append(",")
                    .append(((Program) info).getL_actionType())
                    .append(",")
                    .append(((Program) info).getL_actionUri())
                    .trimToSize();
        }
        LogUploadUtils.uploadLog(Constant.LOG_NODE_RECOMMEND, logBuff
                .toString());//由首页进入下个推荐位

        LivePlayView livePlayView = getLivePlayView(view);
        if (livePlayView != null && livePlayView.isVideoType()) {
            livePlayView.enterFullScreen();
            return;
        }
        if (info instanceof Program) {
            Program pInfo = (Program) info;
//            JumpUtil.activityJump(mContext,pInfo.getL_actionType(),pInfo.getContentType(),pInfo
//                    .getL_id(),pInfo.getL_actionUri());
            JumpUtil.activityJump(mContext, (Program) info);
        } else if (info instanceof Row) {
            Row row = (Row) info;
            JumpUtil.detailsJumpActivity(mContext, row.getContentType(), row.getContentId());
        }

    }

    private void processSuperscript(String layoutCode, Program info, ViewGroup parent) {
        if (info == null || parent == null) {
            return;
        }

        String leftTopUrl = info.getLSuperScript();
        if (!TextUtils.isEmpty(leftTopUrl)) {
            addLeftTopSuperscript(leftTopUrl, parent);
        }

        String rightTopUrl = info.getRSuperScript();
        if (!TextUtils.isEmpty(rightTopUrl)) {
            addRightTopSuperscript(rightTopUrl, parent);
        }

        String leftBottomUrl = info.getLSubScript();
        if (!TextUtils.isEmpty(leftBottomUrl)) {
            addLeftBottomSuperscript(layoutCode, leftBottomUrl, parent);
        }

        String rightBottomUrl = info.getRSubScript();
        if (!TextUtils.isEmpty(rightBottomUrl)) {
            addRightBottomSuperscript(layoutCode, rightBottomUrl, parent);
        }
    }

    private void addLeftTopSuperscript(String superId, ViewGroup parent) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
        lp.topMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
        ;

        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(lp);
        parent.addView(imageView);

        loadSuperscript(imageView, superId);
    }

    private void addLeftBottomSuperscript(String layoutCode, String superId, ViewGroup parent) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
        if (TextUtils.equals(layoutCode, "layout_008") || TextUtils.equals(layoutCode,
                "layout_005")) {
            lp.bottomMargin = DisplayUtils.translate(101, DisplayUtils.SCALE_TYPE_HEIGHT);
        } else {
            lp.bottomMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
        }

        lp.gravity = Gravity.BOTTOM;
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(lp);
        parent.addView(imageView);

        loadSuperscript(imageView, superId);
    }

    private void addRightTopSuperscript(String superId, ViewGroup parent) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
        lp.topMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
        lp.gravity = Gravity.RIGHT | Gravity.END;
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(lp);
        parent.addView(imageView);
        loadSuperscript(imageView, superId);
    }

    private void addRightBottomSuperscript(String layoutCode, String superId, ViewGroup parent) {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                .WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (TextUtils.equals(layoutCode, "layout_005") || TextUtils.equals(layoutCode,
                "layout_008")) {
            lp.bottomMargin = DisplayUtils.translate(101, DisplayUtils.SCALE_TYPE_HEIGHT);
        } else {
            lp.bottomMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
        }
        lp.rightMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
        lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(lp);
        parent.addView(imageView);

        // 加载角标
        loadSuperscript(imageView, superId);
    }

    private void loadSuperscript(ImageView target, String superscriptId) {
        SuperscriptInfo info = SuperScriptManager.getInstance().getSuperscriptInfoById
                (superscriptId);
        if (info != null) {
            String superType = info.getCornerType();
            if ("IMG".equals(superType)) {
                String superUrl = info.getCornerImg();
                if (superUrl != null) {
                    Picasso.get().load(superUrl).tag(PicassoTag).into(target);
                }
            }
        }
    }

    /**
     * 按需添加标题控件
     * 1.如果是5和8号组件, 只在海报上面添加一个标题, 展示subtitle
     * 2.对于其余组件, 在海报上添加两个TextView,负责展示Title和SubTitles
     */
    void processTitle(String layoutCode, String title, String subTitle, ViewGroup
            framelayout) {
        if (TextUtils.isEmpty(layoutCode)) {
            return;
        }

        ImageView background = null;
        //高
        int pxheight_1px = mContext.getResources().getDimensionPixelSize(R.dimen._height_1px);
        int pxSize = mContext.getResources().getDimensionPixelSize(R.dimen.width_70px);
        int pxSize2 = mContext.getResources().getDimensionPixelSize(R.dimen.height_40px);

        if (!TextUtils.isEmpty(title) && !TextUtils.equals(title, "null")) {
            if (!TextUtils.equals(layoutCode, "layout_005") && !TextUtils.equals(layoutCode,
                    "layout_008")) {
                AutoSizeTextView titleWidget = (AutoSizeTextView) framelayout.getTag(R.id
                        .tag_title);
                if (titleWidget == null) {
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup
                            .LayoutParams.MATCH_PARENT, DisplayUtils.translate(pxSize, DisplayUtils
                            .SCALE_TYPE_HEIGHT));
                    layoutParams.gravity = Gravity.BOTTOM;
                    layoutParams.bottomMargin = pxheight_1px;
                    background = new ImageView(mContext);
                    background.setBackgroundResource(R.drawable.gradient_white_to_black);
                    background.setLayoutParams(layoutParams);
                    framelayout.addView(background);

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout
                            .LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    lp.gravity = Gravity.BOTTOM;
                    lp.bottomMargin = 0;
                    lp.leftMargin = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        lp.setMarginStart(0);
                    }
                    titleWidget = new AutoSizeTextView(mContext);
                    titleWidget.setLayoutParams(lp);
                    framelayout.setTag(R.id.tag_title, titleWidget);
                    framelayout.addView(titleWidget, lp);
                }
                titleWidget.setText(title);
            }
        }

        if (!TextUtils.isEmpty(subTitle) && !TextUtils.equals(subTitle, "null")) {
            TextView subTitleWidget = (TextView) framelayout.getTag(R.id.tag_sub_title);
            if (TextUtils.equals(layoutCode, "layout_005") || TextUtils.equals(layoutCode,
                    "layout_008")) {
                if (subTitleWidget == null) {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(DisplayUtils
                            .translate
                                    (225, DisplayUtils.SCALE_TYPE_WIDTH),
                            DisplayUtils.translate(33, DisplayUtils.SCALE_TYPE_HEIGHT));
                    lp.gravity = Gravity.BOTTOM;
                    lp.leftMargin = DisplayUtils.translate(33, DisplayUtils.SCALE_TYPE_WIDTH);
                    ;
                    lp.bottomMargin = DisplayUtils.translate(96, DisplayUtils.SCALE_TYPE_HEIGHT);
                    ;
                    subTitleWidget = new TextView(mContext);
                    subTitleWidget.setLayoutParams(lp);
                    subTitleWidget.setSingleLine();
                    subTitleWidget.setGravity(Gravity.BOTTOM);
                    subTitleWidget.setIncludeFontPadding(false);
                    subTitleWidget.setTextColor(Color.parseColor("#c1c1c1"));
                    subTitleWidget.setTextSize(DisplayUtils.translate(10, DisplayUtils
                            .SCALE_TYPE_HEIGHT));
                    subTitleWidget.setPadding(DisplayUtils.translate(12, DisplayUtils
                            .SCALE_TYPE_WIDTH), 0, 0, 0);
                    framelayout.setTag(R.id.tag_sub_title, subTitleWidget);
                    framelayout.addView(subTitleWidget, lp);
                }
            } else {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, DisplayUtils.translate(33, DisplayUtils
                        .SCALE_TYPE_HEIGHT));
                lp.gravity = Gravity.BOTTOM;
                lp.bottomMargin = DisplayUtils.translate(34, DisplayUtils.SCALE_TYPE_HEIGHT);
                ;
                subTitleWidget = new TextView(mContext);
                subTitleWidget.setLayoutParams(lp);
                subTitleWidget.setPadding(DisplayUtils.translate(12, DisplayUtils
                        .SCALE_TYPE_WIDTH), 0, 0, 0);
                subTitleWidget.setSingleLine();
                subTitleWidget.setTextColor(Color.parseColor("#c1c1c1"));
                subTitleWidget.setTextSize(DisplayUtils.translate(10, DisplayUtils
                        .SCALE_TYPE_HEIGHT));
                subTitleWidget.setIncludeFontPadding(false);
                subTitleWidget.setGravity(Gravity.BOTTOM);
                framelayout.setTag(R.id.tag_sub_title, subTitleWidget);
                framelayout.addView(subTitleWidget, lp);
            }

            subTitleWidget.setText(subTitle);
        }
    }

    private LivePlayView getLivePlayView(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }

        ViewGroup viewGroup = (ViewGroup) view;
        LivePlayView livePlayView = null;
        int viewCount = viewGroup.getChildCount();
        for (int i = 0; i < viewCount; i++) {
            if (viewGroup.getChildAt(i) instanceof LivePlayView) {
                livePlayView = (LivePlayView) viewGroup.getChildAt(i);
                break;
            }
        }
        return livePlayView;
    }
}
