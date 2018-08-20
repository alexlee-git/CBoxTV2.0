package tv.newtv.cboxtv.cms;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.views.RecycleImageView;

/**
 * Created by lin on 2018/1/30.
 */

public class PosterViewGroup extends FrameLayout{
    private List<Integer> mChildWidthSet = new ArrayList<>();
    private List<Integer> mChildHeightSet = new ArrayList<>();

    //整体宽高使用getWidth和getHeight获取

    //海报图宽高
    private int mPosterWidth;
    private int mPosterHeight;

    //外发光尺寸使用getPadding获取

    //Title的宽高
    private int mTitleWidth;
    private int mTitleHeight;

    //Title的可见性
    private int mTitleVisible;

    //Title的文字居中方式
    private int mTitleGravity;

    //海报图和Title的间距
    private int mPosterTitleSpaceing;

    //角标和海报图的间距
    private int mAnglePadding;

    //左上角标宽高
    private int mAngleLeftTopWidth;
    private int mAngleLeftTopHeight;

    //右上角标宽高
    private int mAngleRightTopWidth;
    private int mAngleRightTopHeight;

    //主标题的宽高
    private int mAngleTitleWidth;
    private int mAngleTitleHeight;

    //副标题的宽高
    private int mAngleSubTitleWidth;
    private int mAngleSubTitleHeight;

    //右下角标的宽高
    private int mAngleRightBottomWidth;
    private int mAngleRightBottomHeight;

//    private int mPosterPadding;
//    private int mPosterAnglePadding;
//    private int mPosterTitleHeight;
//    private int mPosterImageTitleSpaceing;

    private int mPosterTitleBottomSpaceing = (int) getResources().getDimension(R.dimen.poster_title_bottom_spaceing);

    private TextView mPosterTitle;
    private RecycleImageView mPosterImage;
    private ImageView mPosterLeftTopAngle;
    private ImageView mPosterRightTopAngle;
    private TextView mPosterLeftBottomAngleMainTitle;
    private TextView mPosterLeftBottomAngleSubTitle;
    private ImageView mPosterRightBottomAngle;

    public PosterViewGroup(@NonNull Context context) {
        super(context);
    }

    public PosterViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initView(context);
    }

    public PosterViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        mPosterTitle = new TextView(context);
        LayoutParams titleParams = new LayoutParams(mTitleWidth, mTitleHeight);
        mPosterTitle.setLayoutParams(titleParams);
        mPosterTitle.setVisibility(mTitleVisible);
        mPosterTitle.setGravity(mTitleGravity);
        mPosterTitle.setTag(getTag() + "_title");
        mPosterTitle.setText(R.string.app_name);
        addView(mPosterTitle);

        mPosterImage = new RecycleImageView(context);
        LayoutParams posterParams = new LayoutParams(mPosterWidth,mPosterHeight);
        mPosterImage.setLayoutParams(posterParams);
        mPosterImage.setTag(getTag() + "_poster");
        mPosterImage.setImageResource(android.R.color.holo_blue_light);
        addView(mPosterImage);

        mPosterLeftTopAngle = new ImageView(context);
        LayoutParams leftTopAngleParams = new LayoutParams(mAngleLeftTopWidth,mAngleLeftTopHeight);
        mPosterLeftTopAngle.setLayoutParams(leftTopAngleParams);
        mPosterLeftTopAngle.setTag(getTag() + "_angle_left_top");
        mPosterLeftTopAngle.setImageResource(android.R.color.holo_green_light);
        addView(mPosterLeftTopAngle);

        mPosterRightTopAngle = new ImageView(context);
        LayoutParams rightTopAngleParams = new LayoutParams(mAngleRightTopWidth,mAngleRightTopHeight);
        mPosterRightTopAngle.setLayoutParams(rightTopAngleParams);
        mPosterRightTopAngle.setTag(getTag() + "_angle_right_top");
        mPosterRightTopAngle.setImageResource(android.R.color.holo_green_light);
        addView(mPosterRightTopAngle);

        mPosterLeftBottomAngleMainTitle = new TextView(context);
        LayoutParams leftBottomAngleMainTitleParams = new LayoutParams(mAngleTitleWidth,mAngleTitleHeight);
        mPosterLeftBottomAngleMainTitle.setLayoutParams(leftBottomAngleMainTitleParams);
        mPosterLeftBottomAngleMainTitle.setTag(getTag() + "_angle_title");
        mPosterLeftBottomAngleMainTitle.setText(R.string.app_name);
        addView(mPosterLeftBottomAngleMainTitle);

        mPosterLeftBottomAngleSubTitle = new TextView(context);
        LayoutParams leftBottomAngleSubTitleParams = new LayoutParams(mAngleSubTitleWidth,mAngleSubTitleHeight);
        mPosterLeftBottomAngleSubTitle.setLayoutParams(leftBottomAngleSubTitleParams);
        mPosterLeftBottomAngleSubTitle.setTag(getTag() + "_angle_sub_title");
        mPosterLeftBottomAngleSubTitle.setText(R.string.app_name);
        addView(mPosterLeftBottomAngleSubTitle);

        mPosterRightBottomAngle = new ImageView(context);
        LayoutParams rightBottomAngleParams = new LayoutParams(mAngleRightBottomWidth,mAngleRightBottomHeight);
        mPosterRightBottomAngle.setLayoutParams(rightBottomAngleParams);
        mPosterRightBottomAngle.setTag(getTag() + "_angle_right_bottom");
        mPosterRightBottomAngle.setImageResource(android.R.color.holo_green_light);
        addView(mPosterRightBottomAngle);

    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PosterViewGroup);

        mPosterWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_poster_width,0);
        mPosterHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_poster_height, 0);

        mTitleWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_title_width, 0);
        mTitleHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_title_height,0);

        mTitleVisible = typedArray.getInt(R.styleable.PosterViewGroup_title_visible, View.VISIBLE);

        mTitleGravity = typedArray.getInt(R.styleable.PosterViewGroup_title_gravity, Gravity.CENTER);

        mPosterTitleSpaceing = (int) typedArray.getDimension(R.styleable.PosterViewGroup_poster_title_spaceing, 0);

        mAnglePadding = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_padding,0);

        mAngleLeftTopWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_left_top_width,0);
        mAngleLeftTopHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_left_top_height,0);

        mAngleRightTopWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_right_top_width, 0);
        mAngleRightTopHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_right_top_height, 0);

        mAngleTitleWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_title_width,0);
        mAngleTitleHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_title_height,0);

        mAngleSubTitleWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_sub_title_width,0);
        mAngleSubTitleHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_sub_title_height,0);

        mAngleRightBottomWidth = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_right_bottom_width,0);
        mAngleRightBottomHeight = (int) typedArray.getDimension(R.styleable.PosterViewGroup_angle_right_bottom_height,0);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得父布局建议的宽和高以及计算模式
        int parentSuggestWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentSuggestHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int parentSuggestWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentSuggestHeight = MeasureSpec.getSize(heightMeasureSpec);

        //执行计算所有childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //用于记录当当前的ViewGroup设置了wrap_content属性时的大小
        int wrapContentWidth = 0;
        int wrapContentHeight = 0;

        //获得子childView的数量
//        int childViewCount = getChildCount();
//
//        int childViewWidth = 0;
//        int childViewHeight = 0;
//        LayoutParams childViewParams;



//        for (int childViewPosition = 0 ; childViewPosition < childViewCount ; childViewPosition++) {
//            View childView = getChildAt(childViewPosition);
//            childViewWidth = childView.getMeasuredWidth();
//            childViewHeight = childView.getMeasuredHeight();
//            childViewParams = (LayoutParams) childView.getLayoutParams();
//
//            mChildWidthSet.add(childViewWidth);
//            mChildHeightSet.add(childViewHeight);
//        }

//        int maxWidth = Collections.max(mChildWidthSet);
//        int maxHeight = Collections.max(mChildHeightSet);

//        wrapContentWidth = maxWidth + mPosterPadding * 2;
//        wrapContentHeight = maxHeight + mPosterPadding + mPosterImageTitleSpaceing + mPosterTitleBottomSpaceing;

        wrapContentWidth = mPosterWidth + getPaddingLeft() + getPaddingRight();

        if (mTitleVisible == View.VISIBLE) {
            wrapContentHeight = getPaddingTop() + mPosterHeight + mPosterTitleSpaceing + mTitleHeight + mPosterTitleBottomSpaceing;
        } else {
            wrapContentHeight = getPaddingTop() + mPosterHeight + getPaddingBottom();
        }


        setMeasuredDimension((parentSuggestWidthMode == MeasureSpec.EXACTLY) ? parentSuggestWidth : wrapContentWidth, (parentSuggestHeightMode == MeasureSpec.EXACTLY) ? parentSuggestHeight : wrapContentHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childViewCount = getChildCount();
        int childViewWidth;
        int childViewHeight;
        LayoutParams childViewParams;

        if (mTitleVisible == VISIBLE) {
            setPadding(getPaddingLeft(),getPaddingTop(),getPaddingRight(),0);
        }

        for (int viewPosition = 0 ; viewPosition < childViewCount ; viewPosition++) {
            View childView = getChildAt(viewPosition);
            childViewWidth = childView.getMeasuredWidth();
            childViewHeight = childView.getMeasuredHeight();
            childViewParams = (LayoutParams)childView.getLayoutParams();

            int childViewleft = 0, childViewTop = 0, childViewRight = 0, childViewBottom = 0;

            switch (viewPosition) {
                case 0:
                    childViewleft = childViewParams.leftMargin + getPaddingLeft() - childViewParams.rightMargin;
                    childViewTop = childViewParams.topMargin + getHeight() - mPosterTitleBottomSpaceing - mTitleHeight - childViewParams.bottomMargin;
                    break;
                case 1:
                    childViewleft = childViewParams.leftMargin + getPaddingLeft() - childViewParams.rightMargin;
                    childViewTop = childViewParams.topMargin + getPaddingTop() - childViewParams.bottomMargin;
                    break;
                case 2:
                    childViewleft = childViewParams.leftMargin + getPaddingLeft() + mAnglePadding - childViewParams.rightMargin;
                    childViewTop = childViewParams.topMargin + getPaddingTop() + mAnglePadding - childViewParams.bottomMargin;
                    break;
                case 3:
                    childViewleft = childViewParams.leftMargin + getWidth() - getPaddingLeft() - mAnglePadding - childViewWidth - childViewParams.rightMargin;
                    childViewTop = childViewParams.topMargin + getPaddingTop() + mAnglePadding - childViewParams.bottomMargin;
                    break;
                case 4:
                    childViewleft = childViewParams.leftMargin + getPaddingLeft() + mAnglePadding - childViewParams.rightMargin;

                    if (mTitleVisible == GONE) {
                        childViewTop = childViewParams.topMargin + getHeight() - mAngleTitleHeight - mAnglePadding - getPaddingBottom() - childViewParams.bottomMargin;
                    } else {
                        childViewTop = childViewParams.topMargin + getHeight() - mAngleTitleHeight - mAnglePadding - mPosterTitleSpaceing - mTitleHeight - mPosterTitleBottomSpaceing - childViewParams.bottomMargin;
                    }

                    Log.d("PLPLPL", "childViewParams.topMargin = " + childViewParams.topMargin + " getHeight() = " + getHeight() + " mAngleTitleHeight = " + mAngleTitleHeight + " mAnglePadding = " + mAnglePadding + " mPosterTitleSpaceing = " + mPosterTitleSpaceing
                        + " mTitleHeight = " + mTitleHeight + " mPosterTitleBottomSpaceing = " + mPosterTitleBottomSpaceing + " childViewParams.bottomMargin = " + childViewParams.bottomMargin);
                    break;
                case 5:
                    childViewleft = childViewParams.leftMargin + getPaddingLeft() + mAnglePadding - childViewParams.rightMargin;

                    View nextChildView = getChildAt(4);

                    if (mTitleVisible == GONE) {
                        childViewTop = childViewParams.topMargin + getHeight() - childViewHeight - nextChildView.getHeight() - mAnglePadding - getPaddingBottom() - childViewParams.bottomMargin;
                    } else {
                        childViewTop = childViewParams.topMargin + getHeight() - childViewHeight - nextChildView.getHeight() - mAnglePadding - mPosterTitleSpaceing - mTitleHeight - mPosterTitleBottomSpaceing - childViewParams.bottomMargin;
                    }

                    break;
                case 6:
                    childViewleft = childViewParams.leftMargin + getWidth() - getPaddingLeft() - mAnglePadding - childViewWidth - childViewParams.rightMargin;

                    if (mTitleVisible == GONE) {
                        childViewTop = childViewParams.topMargin + getHeight() - childViewHeight - mAnglePadding- getPaddingBottom() - childViewParams.bottomMargin;
                    } else {
                        childViewTop = childViewParams.topMargin + getHeight() - childViewHeight - mAnglePadding - mPosterTitleSpaceing - mTitleHeight - mPosterTitleBottomSpaceing - childViewParams.bottomMargin;
                    }

                    break;
                default:
                    break;
            }

            childViewRight = childViewleft + childViewWidth;
            childViewBottom = childViewTop + childViewHeight;

            childView.layout(childViewleft, childViewTop, childViewRight, childViewBottom);
        }
    }
}
