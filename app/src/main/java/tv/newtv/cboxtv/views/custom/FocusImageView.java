package tv.newtv.cboxtv.views.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.newtv.libs.util.ScaleUtils;

import tv.newtv.cboxtv.R;

/**
 * Created by renpengfei
 * Created by Date 2019/1/10 13:41
 */

public class FocusImageView extends AppCompatImageView implements View.OnFocusChangeListener{
    private static final int DEFAULT_BACKGROUND_RESOURCE_ID = R.drawable.pos_zui_27px;
    private Paint mPaint;

    public FocusImageView(Context context) {
        this(context,null);
    }

    public FocusImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FocusImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusImageView, defStyleAttr, 0);
        setBackgroundResource(typedArray.getResourceId(R.styleable.FocusImageView_focus_background, DEFAULT_BACKGROUND_RESOURCE_ID));
        typedArray.recycle();
        setFocusable(true);
        setClickable(true);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(this);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(this);
        }
    }
}
