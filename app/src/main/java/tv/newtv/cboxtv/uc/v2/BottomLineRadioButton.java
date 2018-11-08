package tv.newtv.cboxtv.uc.v2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RadioButton;

import tv.newtv.cboxtv.R;


/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         17:31
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
@SuppressLint("AppCompatCustomView")
public class BottomLineRadioButton extends RadioButton {

    private Rect mRect;
    private Paint mPaint;
    private int height;
    private boolean updateShow = false;

    public BottomLineRadioButton(Context context) {
        this(context, null);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setChecked(focused);
    }

    public BottomLineRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public BottomLineRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        setBackgroundColor(Color.TRANSPARENT);
        setFocusable(true);
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);

        height = getResources().getDimensionPixelOffset(R.dimen.height_4px);

        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#FF4A90E2"));
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        updateShow = checked;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mRect == null) {
            mRect = new Rect(0, getHeight() - height, getWidth(), getHeight());
        } else {
            mRect.right = getMeasuredWidth();
            mRect.top = getMeasuredHeight() - height;
            mRect.bottom = getMeasuredHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (updateShow) {
            canvas.drawRect(mRect, mPaint);
        }
    }
}
