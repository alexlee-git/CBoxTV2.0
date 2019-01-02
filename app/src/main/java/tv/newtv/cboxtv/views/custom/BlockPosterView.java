package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Program;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.GlideUtil;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.ScaleUtils;
import com.newtv.libs.util.ScreenUtils;
import com.newtv.libs.util.UsefulBitmapFactory;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.player.listener.ScreenListener;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.custom
 * 创建事件:         17:12
 * 创建人:           weihaichao
 * 创建日期:          2018/11/16
 */
public class BlockPosterView extends FrameLayout implements View.OnClickListener, View
        .OnFocusChangeListener, ScreenListener {
    private static final String TAG = BlockPosterView.class.getSimpleName();
    private static final int BLOCK_TYPE_IMAGE = 0;
    private static final int BLOCK_TYPE_VIDEO = 1;
    private View focusBackground;
    private RecycleImageView mPosterImage;
    private FrameLayout mPoster;
    private TextView mPosterTitle;
    private int marginSpace = 0;
    private int titleHeight = 0;
    private int poster_width = 0;
    private int poster_height = 0;
    private boolean auto_location = true;
    private int poster_resource_holder = 0;
    private boolean show_title = false;
    private int block_type = BLOCK_TYPE_IMAGE;
    private String block_tag = "block";

    private boolean enterFullScreen = false;

    private boolean isLast = false;
    private Bitmap bitmap;
    private Paint mPaint;

    private String mPageUUID;

    private LivePlayView mLivePlayView;

    private boolean isVideoMode = false;
    private Program mProgram;

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

    public boolean hasCorner() {
        if (block_type == BLOCK_TYPE_VIDEO && mLivePlayView != null) {
            return !mLivePlayView.isVideoType();
        }
        return true;
    }

    public void setPageUUID(String uuid) {
        mPageUUID = uuid;
        if (mLivePlayView != null) {
            mLivePlayView.setPageUUID(uuid);
        }
    }

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

    public void setData(Program program) {

        mProgram = program;

        if (mLivePlayView != null) {
            mLivePlayView.setProgramInfo(mProgram);
        }

        GlideUtil.loadImage(getContext(), mPosterImage, program.getImg(), poster_resource_holder,
                poster_resource_holder, true);
    }

    private int getBlockWidth() {
        if (enterFullScreen) {
            return ScreenUtils.getScreenW() + marginSpace * 2;
        }
        return poster_width + marginSpace * 2;
    }

    private int getBlockHeight() {
        if (enterFullScreen) {
            return ScreenUtils.getScreenH() + marginSpace * 2 + titleHeight;
        }
        return poster_height + marginSpace * 2 + titleHeight;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = getBlockWidth();
        params.height = getBlockHeight();
        LogUtils.d(TAG, "width=" + params.width + " height=" + params.height);
        super.setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        refreshLayout();
    }

    private void refreshLayout() {
        if (focusBackground != null) {

            int mWidth = enterFullScreen ? ScreenUtils.getScreenW() : poster_width;
            int mHeight = enterFullScreen ? ScreenUtils.getScreenH() : poster_height;

            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = mWidth + marginSpace;
            layoutParams.height = mHeight + marginSpace;

            int hmargin = getResources().getDimensionPixelSize(R.dimen.width_48px);
            int vmargin = getResources().getDimensionPixelSize(R.dimen.height_48px);

            marginSpace = focusBackground.getPaddingLeft();
            if (marginSpace == 0) return;

            if (layoutParams instanceof MarginLayoutParams) {
                if (!isLast) {
                    ((MarginLayoutParams) layoutParams).rightMargin = hmargin - marginSpace * 2;
                }
                ((MarginLayoutParams) layoutParams).bottomMargin = vmargin - marginSpace * 2;
            }

            setLayoutParams(layoutParams);

            focusBackground.layout(0, 0, mWidth + marginSpace * 2, mHeight +
                    marginSpace * 2);

            if (mLivePlayView != null) {
                mLivePlayView.layout(0, 0, mWidth, mHeight);
            }
            if (mPoster != null) {
                mPoster.layout(marginSpace, marginSpace, mWidth+marginSpace, mHeight+marginSpace);
            }

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
            include = typedArray.getBoolean(R.styleable
                    .BlockPosterView_block_poster_include_padding, true);

            block_tag = typedArray.getString(R.styleable.BlockPosterView_block_tag);

            isLast = typedArray.getBoolean(R.styleable.BlockPosterView_block_last, false);

            auto_location = typedArray.getBoolean(R.styleable
                    .BlockPosterView_block_auto_location, true);

            typedArray.recycle();
        }


        if (block_type > BLOCK_TYPE_IMAGE) {
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

        marginSpace = context.getResources().getDimensionPixelSize(R.dimen.width_27px);
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
        mPosterImage = new RecycleImageView(context);
        mPosterImage.setTag("POSTER");
        mPosterImage.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams layoutParams = new LayoutParams(poster_width, poster_height);
        mPosterImage.setLayoutParams(layoutParams);
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
            mPosterTitle.setHorizontallyScrolling(true);
            mPosterTitle.setTextAppearance(context, R.style.ModulePosterBottomTitleStyle);
            LayoutParams titleLayoutParam = new LayoutParams(poster_width, titleHeight);
            titleLayoutParam.topMargin = getBlockHeight() - titleHeight;
            titleLayoutParam.leftMargin = marginSpace;
            titleLayoutParam.rightMargin = marginSpace;
            mPosterTitle.setLayoutParams(titleLayoutParam);
            if(isInEditMode()) {
                mPosterTitle.setText("央视影音测试标题");
            }
            mPosterTitle.setTag(String.format("%s_%s", block_tag, "title"));
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
                    mLivePlayView.setTag(String.format("%s_%s", block_tag, "poster"));
                    mPoster.addView(mLivePlayView, layoutParams);
                }
                break;
            default:
                mPosterImage.setTag(String.format("%s_%s", block_tag, "poster"));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (mLivePlayView != null) {
            if (mLivePlayView.isVideoType()) {
                mLivePlayView.dispatchClick();
                return;
            }
        }
        if (mProgram != null) {
            JumpUtil.activityJump(getContext(), mProgram);
        }

    }

    @Override
    public void onFocusChange(View view, boolean gainFocus) {
        if (show_title) {
            if (mPosterTitle != null) {
                mPosterTitle.setSelected(gainFocus);
            }

            if (mPosterImage != null) {
                mPosterImage.setActivated(gainFocus);
            }
        }
        if (!isVideoMode || (mLivePlayView != null && !mLivePlayView.isVideoType())) {
            if (gainFocus) {
                ScaleUtils.getInstance().onItemGetFocus(this);
            } else {
                ScaleUtils.getInstance().onItemLoseFocus(this);
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
