package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import tv.newtv.cboxtv.R;
import com.newtv.libs.util.BitmapUtil;
import com.newtv.libs.util.UsefulBitmapFactory;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         13:34
 * 创建人:           weihaichao
 * 创建日期:          2018/5/7
 */
public class FocusToggleView extends View implements FocusToggleSelect {
    private Bitmap NormalDrawable;
    private Bitmap SelectDrawable;
    private int FocusDrawable;
    private Paint mPaint;
    private int space = 0, mWidth, mHeight;
    private boolean isSelect = false;
    private  Context mContext;

    public FocusToggleView(Context context) {
        this(context, null);
        mContext = context;
    }

    public FocusToggleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;

    }

    public FocusToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initalize(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (!isInEditMode()) {
            if (params != null) {
                space = getResources().getDimensionPixelOffset(R.dimen.width_17dp);
//                space = DisplayUtils.getPxByDensity(mContext,space);
                Log.e("MM","space="+space);

                if (params instanceof RelativeLayout.LayoutParams) {
                    ((RelativeLayout.LayoutParams) params).leftMargin -= space;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        ((RelativeLayout.LayoutParams) params).setMarginStart(((RelativeLayout
                                .LayoutParams) params).leftMargin);
                    }
                    ((RelativeLayout.LayoutParams) params).topMargin -= space;
                    ((RelativeLayout.LayoutParams) params).bottomMargin -= space;
                    ((RelativeLayout.LayoutParams) params).rightMargin -= space;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        ((RelativeLayout.LayoutParams) params).setMarginEnd(((RelativeLayout
                                .LayoutParams) params).rightMargin);
                    }
                }
            }
        }

        super.setLayoutParams(params);
    }

    private void initalize(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusToggleView);
        if (typedArray != null) {
            int normal = typedArray.getResourceId(R.styleable.FocusToggleView_normal_drawable, 0);
            int select = typedArray.getResourceId(R.styleable.FocusToggleView_selected_drawable, 0);
            FocusDrawable = typedArray.getResourceId(R.styleable.FocusToggleView_focus_drawable, 0);
            mWidth = typedArray.getDimensionPixelOffset(R.styleable
                    .FocusToggleView_drawable_width, 0);
            mHeight = typedArray.getDimensionPixelOffset(R.styleable
                    .FocusToggleView_drawable_height, 0);


            setToggleDrawable(normal, select);

            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = mWidth + space * 2;
        int height = mHeight + space * 2;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.UNSPECIFIED);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean value) {
        isSelect = value;
        postInvalidate();
    }

    public void setToggleDrawable(int normal, int selected) {
        NormalDrawable = UsefulBitmapFactory.findBitmap(getContext(), normal);
        SelectDrawable = UsefulBitmapFactory.findBitmap(getContext(), selected);
        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            if (NormalDrawable != null && !NormalDrawable.isRecycled()) {
                canvas.drawBitmap(BitmapUtil.zoomImg(NormalDrawable, mWidth,
                        mHeight), 0, 0, mPaint);
            }
            return;
        }


        if (hasFocus()) {
            setBackgroundResource(FocusDrawable);
        } else {
            setBackgroundColor(Color.TRANSPARENT);
        }


        if (isSelect) {
            if (SelectDrawable != null&&!SelectDrawable.isRecycled()) {
                canvas.drawBitmap(BitmapUtil.zoomImg(SelectDrawable, mWidth,
                        mHeight),
                        space, space, mPaint);
            }
        } else {
            if (NormalDrawable != null && !NormalDrawable.isRecycled()) {
                canvas.drawBitmap(BitmapUtil.zoomImg(NormalDrawable, mWidth,
                        mHeight), space, space, mPaint);
            }
        }


    }
}
