package tv.newtv.cboxtv.uc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.R;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:24
 * 创建人:           weihaichao
 * 创建日期:          2018/4/13
 */
public class BackgroundTipView extends FrameLayout {

    private Rect mRect = new Rect();
    private Rect visibleRect;
    private Rect DrawRect;
    private Paint mPaint;
    private Paint mLinePaint;

    private Bitmap mBitmap;

    public BackgroundTipView(Context context) {
        super(context);
        init(context);
    }

    public BackgroundTipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        //绘制中间透明区域矩形边界的Paint
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);

        //绘制四周阴影区域
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.parseColor("#80000000"));
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {
            getGlobalVisibleRect(mRect);
        }
    }

    public void setVisibleRect(Bitmap bitmap, Rect rect) {
        mBitmap = bitmap;
        visibleRect = rect;
//        int space = (int) (getContext().getResources().getDimensionPixelSize(R.dimen
//                .height_426px) * 1.1);
//        DrawRect = new Rect(rect.left, rect.top, rect.right, rect.top + space);
//        visibleRect = new Rect(0, 0, rect.width(), space);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        canvas.drawBitmap(mBitmap, visibleRect.left, visibleRect.top, mLinePaint);
//        canvas.drawBitmap(mBitmap, visibleRect, DrawRect, mLinePaint);
        super.onDraw(canvas);
    }
}
