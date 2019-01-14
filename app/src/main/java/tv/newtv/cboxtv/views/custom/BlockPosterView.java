package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Program;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.DisplayUtils;
import com.newtv.libs.util.GlideUtil;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.ScreenUtils;
import com.newtv.libs.util.UsefulBitmapFactory;

import java.util.Locale;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.listener.ScreenListener;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.custom
 * 创建事件:         17:12
 * 创建人:           weihaichao
 * 创建日期:         2018/11/16
 */
public class BlockPosterView extends ViewGroup implements View.OnClickListener, View
        .OnFocusChangeListener, ScreenListener {
    private static final String TAG = BlockPosterView.class.getSimpleName();
    private static final int BLOCK_TYPE_IMAGE = 0;                  //推荐位状态为图片模式
    private static final int BLOCK_TYPE_VIDEO = 1;                  //推荐位状态为视频模式

    private static final int BLOCK_IMAGE_TYPE_NORMAL = 0;
    private static final int BLOCK_IMAGE_TYPE_PLAY = 1;


    private View focusBackground;                                   //焦点框背景图
    private RecycleImageView mPosterImage;                          //海报图片控件
    private FrameLayout mPoster;                                    //海报容器 （为了加入角标控制在海报范围内，加入该容器限制）
    private TextView mPosterTitle;                                  //推荐位标题
    private int marginSpace = 0;                                    //海报焦点框间隔
    private int titleHeight = 0;                                    //标题高度
    private int poster_width = 0;                                   //海报宽度
    private int poster_height = 0;                                  //海报高度
    private boolean auto_location = true;
    private int poster_resource_holder = 0;                         //海报占位图
    private boolean show_title = false;                             //是否显示标题
    private int block_type = BLOCK_TYPE_IMAGE;
    private String block_tag = "block";                             //海报标识

    private boolean enterFullScreen = false;                        //是否进入全屏

    private boolean isLast = false;                                 //是否为最后的一个海报（最后一个不设置右margin）
    private Bitmap bitmap;//
    private Paint mPaint;

    private String mPageUUID;

    private LivePlayView mLivePlayView;//
    private OnClickListener mOnClickListener;


    private String mPosterTag = null;
    private String mPosterTitleTag = null;

    private boolean specialLayout = false;

    private boolean isVideoMode = false;//
    private int mPosterType = BLOCK_IMAGE_TYPE_NORMAL;
    private Object mProgram;

    public BlockPosterView(Context context) {
        this(context, null);
    }

    public BlockPosterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlockPosterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        if (mLivePlayView != null) {
            mLivePlayView.dispatchWindowVisibilityChanged(visibility);
        }
    }

    public RecycleImageView getPosterImageView() {
        return mPosterImage;
    }

    /**
     * 是否圆角
     *
     * @return
     */
    public boolean hasCorner() {
        if (block_type == BLOCK_TYPE_VIDEO && mLivePlayView != null) {
            return !mLivePlayView.isVideoType();
        }
        return true;
    }

    public void loadPoster(RecycleImageView imageView, String url, boolean isCorner) {
        GlideUtil.loadImage(imageView.getContext(), imageView,
                url, poster_resource_holder, poster_resource_holder, isCorner);
    }

    /**
     * 设置推荐位UUID
     *
     * @param uuid
     */
    public void setPageUUID(String uuid) {
        mPageUUID = uuid;
        if (mLivePlayView != null) {
            mLivePlayView.setPageUUID(uuid);
        }
    }

    /**
     * 设置是否显示聚焦框
     *
     * @param useable 是否启用
     */
    public void setUseable(boolean useable) {
        if (focusBackground != null) {
            focusBackground.setFocusable(true);
            focusBackground.setClickable(false);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        refreshLayout();
    }


    /**
     * 设置推荐位数据
     *
     * @param program 推荐位数据
     */
    public void setData(Object program) {

        mProgram = program;

        if (mProgram != null) {
            if (mLivePlayView != null && mProgram instanceof Program) {
                mLivePlayView.setProgramInfo((Program) mProgram);
            }
        } else {
            if (mPosterImage != null) {
                mPosterImage.setImageResource(poster_resource_holder);
            }
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mOnClickListener = l;
    }

    /**
     * 获取推荐位宽度
     *
     * @return
     */
    private int getBlockWidth() {
        if (enterFullScreen) {
            return ScreenUtils.getScreenW() + marginSpace * 2;
        }
        return poster_width + marginSpace * 2;
    }

    /**
     * 获取推荐位高度
     *
     * @return
     */
    private int getBlockHeight() {
        if (enterFullScreen) {
            return ScreenUtils.getScreenH() + marginSpace * 2 + titleHeight;
        }
        return poster_height + marginSpace * 2 + titleHeight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        refreshLayout();
    }


    private void refreshLayout() {
        if (focusBackground != null) {
            int mWidth = enterFullScreen ? ScreenUtils.getScreenW() : poster_width;
            int mHeight = enterFullScreen ? ScreenUtils.getScreenH() : poster_height;

            ViewGroup.LayoutParams layoutParams = getLayoutParams();

            if (!specialLayout) {
                int space = focusBackground.getPaddingLeft();

                if (space != marginSpace) {
                    if (mLivePlayView != null)
                        LogUtils.d(TAG, "space=" + space + " marginSpace=" + marginSpace);
                    marginSpace = space;
                }
            }

            layoutParams.width = getBlockWidth();
            layoutParams.height = getBlockHeight();

            //横向间隔
            int h_margin = getResources().getDimensionPixelSize(R.dimen.width_48px);
            //纵向间隔
            int v_margin = getResources().getDimensionPixelSize(R.dimen.height_48px);

            if (layoutParams instanceof MarginLayoutParams) {
                if (!isLast) {
                    ((MarginLayoutParams) layoutParams).rightMargin = h_margin - marginSpace
                            * 2;
                }
                ((MarginLayoutParams) layoutParams).bottomMargin = v_margin - marginSpace * 2;
            }

//            setLayoutParams(layoutParams);

            focusBackground.layout(0, 0, getBlockWidth(), getBlockHeight() - titleHeight);

            if (mPoster != null) {
                mPoster.layout(marginSpace, marginSpace, mWidth + marginSpace, mHeight +
                        marginSpace);
                View background = (View) mPoster.getTag(R.id.tag_title_background);
                if (background != null) {
                    int backHeight = getResources().getDimensionPixelSize(R.dimen.height_70px);
                    background.layout(0, mHeight - backHeight, mWidth, mHeight);
                }
                int padding = getResources().getDimensionPixelSize(R.dimen.width_12px);
                View titleView = (View) mPoster.getTag(R.id.tag_title);
                if (titleView != null) {
                    FrameLayout.LayoutParams titleLayoutParam = (FrameLayout.LayoutParams)
                            titleView.getLayoutParams();
                    titleLayoutParam.width = mWidth - padding;
                }
                View subTitleView = (View) mPoster.getTag(R.id.tag_sub_title);
                if (subTitleView != null) {
                    FrameLayout.LayoutParams marginLayoutParams = (FrameLayout.LayoutParams)
                            subTitleView.getLayoutParams();
                    marginLayoutParams.width = mWidth - padding;
                    if (titleView == null) {
                        //不包含主标题
                        marginLayoutParams.bottomMargin = padding;
                    }
                }

            }

            if (mLivePlayView != null) {
                LogUtils.d(TAG, String.format(Locale.getDefault(), "space = %d width=%d " +
                                "height=%d",
                        marginSpace, mWidth, mHeight));
                mLivePlayView.layout(0, 0, mWidth, mHeight);
            }

            if (mPosterImage != null) {
                mPosterImage.layout(0, 0, mWidth, mHeight);
            }

            if (mPosterTitle != null) {
                mPosterTitle.layout(specialLayout ? marginSpace * 2 : marginSpace, getBlockHeight
                                () - titleHeight,
                        mWidth + marginSpace,
                        getBlockHeight());
            }

        }

    }

    public void showCorner(View view, FrameLayout.LayoutParams layoutParams) {
        if (mPoster != null) {
            mPoster.addView(view, layoutParams);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((block_type == BLOCK_TYPE_VIDEO)
                && isInEditMode()) {
            canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight
                    () - bitmap.getHeight()) / 2, mPaint);
        }
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LogUtils.d(TAG, "initialize");

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BlockPosterView);
        int focusResource = R.drawable.selector_pos_background_27px;
        boolean include = true;
        if (typedArray != null) {
            poster_width = typedArray.getDimensionPixelSize(
                    R.styleable.BlockPosterView_block_poster_width, 0);
            poster_height = typedArray.getDimensionPixelSize(
                    R.styleable.BlockPosterView_block_poster_height, 0);
            show_title = typedArray.getBoolean(
                    R.styleable.BlockPosterView_block_show_title, false);
            poster_resource_holder = typedArray.getResourceId(
                    R.styleable.BlockPosterView_block_poster_holder, 0);
            int block = typedArray.getInteger(
                    R.styleable.BlockPosterView_block_poster_type, BLOCK_TYPE_IMAGE);
            if (block > 0) {
                block_type = BLOCK_TYPE_VIDEO;
            } else {
                block_type = BLOCK_TYPE_IMAGE;
            }
            focusResource = typedArray.getResourceId(R.styleable
                    .BlockPosterView_block_poster_focus, R.drawable.selector_pos_background_27px);

            specialLayout = focusResource != R.drawable.selector_pos_background_27px;

            include = typedArray.getBoolean(R.styleable
                    .BlockPosterView_block_poster_include_padding, true);

            block_tag = typedArray.getString(R.styleable.BlockPosterView_block_tag);

            isLast = typedArray.getBoolean(R.styleable.BlockPosterView_block_last, false);

            mPosterTag = typedArray.getString(R.styleable.BlockPosterView_block_image_tag);
            mPosterTitleTag = typedArray.getString(R.styleable.BlockPosterView_block_title_tag);

            auto_location = typedArray.getBoolean(R.styleable
                    .BlockPosterView_block_auto_location, true);

            mPosterType = typedArray.getInteger(R.styleable.BlockPosterView_image_type,
                    BLOCK_IMAGE_TYPE_NORMAL);

            typedArray.recycle();
        }


        if (block_type > BLOCK_TYPE_IMAGE && isInEditMode()) {
            mPaint = new Paint();

            int width = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                    .width_60px) * 1.2);
            int height = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                    .width_60px) * 1.2);

            bitmap = BitmapUtil.zoomImg(UsefulBitmapFactory.findBitmap(getContext(), R.drawable
                    .playing_icon2), width, height);
        }

        setTag(block_tag);

        setClipChildren(false);
        setClipToPadding(false);

        marginSpace = context.getResources().getDimensionPixelSize(specialLayout ? R.dimen
                .height_22px : R.dimen.width_27px);
        titleHeight = context.getResources().getDimensionPixelSize(R.dimen.height_65px);

        focusBackground = new View(context);
        focusBackground.setBackgroundResource(focusResource);
        focusBackground.setFocusable(true);
        focusBackground.setClickable(true);
        focusBackground.setOnFocusChangeListener(this);
        focusBackground.setOnClickListener(this);
        addView(focusBackground, 0);

        mPoster = new FrameLayout(getContext());
        LayoutParams poster_layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        mPoster.setLayoutParams(poster_layoutParams);
        if (mPosterType == BLOCK_IMAGE_TYPE_NORMAL) {
            mPosterImage = new RecycleImageView(context);
        } else {
            mPosterImage = new CurrentPlayImageView(context);
        }
        mPosterImage.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams layoutParams = new LayoutParams(poster_width, poster_height);
        mPosterImage.setLayoutParams(layoutParams);
        if (!TextUtils.isEmpty(mPosterTag)) {
            mPosterImage.setTag(mPosterTag);
        } else {
            mPosterImage.setTag(String.format("%s_%s", block_tag, "poster"));
        }
        if (!include) {
            mPosterImage.setPadding(marginSpace, marginSpace, marginSpace, marginSpace);
        }
        mPosterImage.setImageResource(poster_resource_holder);
        mPoster.addView(mPosterImage);
        mPoster.setTag(block_tag);
        addView(mPoster, 1, poster_layoutParams);

        addOtherWidget(layoutParams);

        if (show_title) {
            mPosterTitle = new TextView(context);
            mPosterTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mPosterTitle.setMarqueeRepeatLimit(1);
            mPosterTitle.setSingleLine();
            mPosterTitle.setMaxLines(1);
            mPosterTitle.setIncludeFontPadding(false);
            mPosterTitle.setHorizontallyScrolling(true);
            if (specialLayout) {
                mPosterTitle.setGravity(Gravity.CENTER);
            }
            mPosterTitle.setTextAppearance(context, R.style.ModulePosterBottomTitleStyle);
            DisplayUtils.adjustTextSize(getContext(), mPosterTitle, 28);
            LayoutParams titleLayoutParam = new LayoutParams(poster_width, titleHeight);
//            titleLayoutParam.topMargin = getBlockHeight() - titleHeight;
//            titleLayoutParam.leftMargin = marginSpace;
//            titleLayoutParam.rightMargin = marginSpace;
            mPosterTitle.setLayoutParams(titleLayoutParam);
            if (isInEditMode()) {
                mPosterTitle.setText("央视影音测试标题");
            }
            if (!TextUtils.isEmpty(mPosterTitleTag)) {
                mPosterTitle.setTag(mPosterTitleTag);
            } else {
                mPosterTitle.setTag(String.format("%s_%s", block_tag, "title"));
            }
            addView(mPosterTitle, titleLayoutParam);
        } else {
            titleHeight = 0;
        }


        if (mProgram != null) {
            setData(mProgram);
        }
    }

    private void addOtherWidget(LayoutParams layoutParams) {
        switch (block_type) {
            case BLOCK_TYPE_VIDEO:
                isVideoMode = true;
                if (mLivePlayView == null) {
                    mLivePlayView = new LivePlayView(getContext());
                    mLivePlayView.setLayoutParams(layoutParams);
                    mLivePlayView.attachScreenListener(this);
                    mLivePlayView.setPageUUID(mPageUUID);
                    mLivePlayView.setTag(String.format("%s_%s", block_tag, "player"));
                    mPoster.addView(mLivePlayView, layoutParams);
                }
                break;
            default:

                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onClick(View view) {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(mLivePlayView != null ? mLivePlayView : this);
        }
    }

    @Override
    public void onFocusChange(View view, boolean gainFocus) {
        if (!isVideoMode || (mLivePlayView != null && !mLivePlayView.isVideoType())) {
            if (gainFocus) {
                ScaleUtils.getInstance().onItemGetFocus(this);
            } else {
                ScaleUtils.getInstance().onItemLoseFocus(this);
            }
        }
        if (show_title) {
            if (mPosterTitle != null) {
                mPosterTitle.setSelected(gainFocus);
            }
            if (mPosterImage != null) {
                mPosterImage.setActivated(gainFocus);
            }
        } else {
            if (mPoster != null) {
                TextView title = (TextView) mPoster.getTag(R.id.tag_title);
                if (title != null) {
                    title.setSelected(gainFocus);
                }
            }
        }

    }

    @Override
    public void enterFullScreen() {
        enterFullScreen = true;
    }

    @Override
    public void exitFullScreen() {
        enterFullScreen = false;
    }
}
