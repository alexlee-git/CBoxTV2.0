package tv.newtv.cboxtv.player.adplayer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.adplayer
 * 创建事件:         13:45
 * 创建人:           weihaichao
 * 创建日期:          2018/9/17
 */
public class ADSurfaceView extends SurfaceView implements Runnable {

    private SurfaceHolder mSurfaceHolder;
    private View cacheView;
    private boolean run = false;

    public ADSurfaceView(Context context) {
        this(context, null);
    }

    public ADSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ADSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSurfaceHolder = getHolder();
        new Thread(this).start();
    }

    public void getCurrentFrame(final View view) {
        cacheView = view;
        synchronized (this) {
            notify();
        }
    }

    @Override
    public void run() {
        while (true){
            if(cacheView == null){
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(mSurfaceHolder.getSurface().isValid()) {
                Canvas mCanvas = mSurfaceHolder.lockCanvas();

                if (mCanvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
                cacheView = null;
            }
        }
    }


    interface FrameCallback {
        void onCanvas(Canvas canvas);
    }
}
