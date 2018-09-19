package tv.newtv.cboxtv.cms.special;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


public class ScrollTextView extends TextView implements Runnable {
    private int currentScrollX;// 当前滚动的位置
    private boolean isStop = false;
    private int textWidth;
    private boolean isMeasure = false;
    private boolean isStart = false;
    private int[] arr;

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public ScrollTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (!isMeasure) {// 文字宽度只需获取一次就可以了
            getTextWidth();
            isMeasure = true;
        }
    }

    /**
     * 获取文字宽度
     */
    private void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        textWidth = (int) paint.measureText(str);
        arr = new int[2];
        this.getLocationInWindow(arr);
        Log.d("ScrollTextView", "arr[0]:" + arr[0]);

    }

    @Override
    public void run() {
        currentScrollX-= 1;// 滚动速度
        if (!isStop){
            scrollTo(currentScrollX, 0);
        }

        if (isStop) {
            scrollTo(0,0);
            currentScrollX =0;
            return;
        }
        if (getScrollX() <= -(this.getWidth())) {
            scrollTo(textWidth, 0);
            currentScrollX = textWidth;
        }

        postDelayed(this, 100);
    }

    // 开始滚动
    public void startScroll() {
        isStop = false;
        this.removeCallbacks(this);
        post(this);
    }

    // 停止滚动
    public void stopScroll() {

        isStop = true;

    }

    // 从头开始滚动
    public void startFor0() {
        currentScrollX = 0;
        startScroll();
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        // TODO Auto-generated method stub
        super.setText(text, type);
        if (isStart()){
            startScroll();
        }

    }

    @Override
    public void destroyDrawingCache() {
        // TODO Auto-generated method stub
        super.destroyDrawingCache();

    }






}