package tv.newtv.cboxtv.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.widget.ImageView;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.utils.BitmapUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.viewsPlay
 * 创建事件:         19:53
 * 创建人:           weihaichao
 * 创建日期:          2018/4/27
 */
@SuppressLint("AppCompatCustomView")
public class CurrentPlayImageViewWorldCup extends ImageView {

    private boolean isPlay = false;
    private boolean isHeight = false;
    private Bitmap bitmap;
    private Paint mPaint;
    private boolean isChanged = false;
    private  PaintFlagsDrawFilter mPaintFilter;
    public void recycle(){

    }

    public CurrentPlayImageViewWorldCup(Context context) {
        super(context);
        init();
    }

    public CurrentPlayImageViewWorldCup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurrentPlayImageViewWorldCup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        int width = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen.width_60px) * 1.2);
        int height = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                .width_60px) * 1.2);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        bitmap = BitmapUtil.zoomImg(BitmapFactory.decodeResource(getContext().getResources(), R
                .drawable.playing_icon2), width, height);
        mPaintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint
                .FILTER_BITMAP_FLAG);
    }

    public void setIsPlaying(boolean value) {
        isPlay = value;
        isChanged = true;
        postInvalidate();
    }
    public void setIsPlaying(boolean value, boolean isHeightValue) {
        isPlay = value;
        isHeight = isHeightValue;
        isChanged = true;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isChanged) {
            if (isPlay && !isHeight) {
                int height = getContext().getResources().getDimensionPixelOffset(R.dimen.height_1px);
                canvas.setDrawFilter(mPaintFilter);
                canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight
                        () - bitmap.getHeight()) / 2, mPaint);            }
           else if (isHeight) {
                int height = getContext().getResources().getDimensionPixelOffset(R.dimen.height_36px);
                canvas.setDrawFilter(mPaintFilter);
                canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight
                        () - bitmap.getHeight()) / 2, mPaint);            }
        }
    }
}
