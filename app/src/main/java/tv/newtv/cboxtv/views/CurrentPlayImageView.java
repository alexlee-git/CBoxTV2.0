package tv.newtv.cboxtv.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.utils.BitmapUtil;
import tv.newtv.cboxtv.utils.UsefulBitmapFactory;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.viewsPlay
 * 创建事件:         19:53
 * 创建人:           weihaichao
 * 创建日期:          2018/4/27
 */
@SuppressLint("AppCompatCustomView")
public class CurrentPlayImageView extends RecycleImageView {

    private boolean isPlay = false;
    private Bitmap bitmap;
    private Paint mPaint;
    private boolean isChanged = false;
    private  PaintFlagsDrawFilter mPaintFilter;

    public CurrentPlayImageView(Context context) {
        this(context,null);
    }

    public CurrentPlayImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CurrentPlayImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int width = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen.width_60px) * 1.2);
        int height = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                .width_60px) * 1.2);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        bitmap = BitmapUtil.zoomImg(UsefulBitmapFactory.findBitmap(getContext(), R.drawable
                .playing_icon2), width, height);

        mPaintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint
                .FILTER_BITMAP_FLAG);
    }

    public void setIsPlaying(boolean value) {
        if(isPlay == value) return;
        isPlay = value;
        isChanged = true;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isChanged) {
            if (isPlay && bitmap != null) {
                canvas.setDrawFilter(mPaintFilter);
                canvas.drawBitmap(bitmap, (getWidth() - bitmap.getWidth()) / 2, (getHeight
                        () - bitmap.getHeight()) / 2, mPaint);
            }
        }
    }
}
