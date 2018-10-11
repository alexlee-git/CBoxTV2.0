package tv.newtv.cboxtv.player.ad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.adplayer
 * 创建事件:         13:45
 * 创建人:           weihaichao
 * 创建日期:          2018/9/17
 */
public class ADPlayerView extends FrameLayout implements IADPlayer, SurfaceHolder.Callback,
        MediaPlayer.OnInfoListener
        , MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer
                .OnErrorListener, MediaPlayer.OnPreparedListener, Runnable {

    private static final String TAG = ADPlayerView.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private Object mDataSource;
    private boolean isPrepared = false;
    private boolean isPaused = false;
    private ADPlayerCallback mCallback;
    private SurfaceHolder mSurfaceHolder;
    private boolean isReleased = false;
    private Map<String, MediaPlayer> cachePlayer;
    private List<Object> cacheUrl;

    public ADPlayerView(Context context) {
        this(context, null);
    }

    public ADPlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ADPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        cachePlayer = new HashMap<>();
    }

    @Override
    public void setPlaceHolder(int res) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), res);
        setPlaceHolder(drawable);
    }

    public void setCallback(ADPlayerCallback callback) {
        mCallback = callback;
    }

    @Override
    public void setPlaceHolder(Drawable drawable) {
        setBackground(drawable);
        invalidate();
    }

    @Override
    public void setPlaceHolder(Bitmap bitmap) {
        setPlaceHolder(new BitmapDrawable(bitmap));
    }

    @Override
    public void setDataSource(Object source) {
        if (isPlaying()) {
            if (cacheUrl == null) {
                cacheUrl = new ArrayList<>();
            }
            cacheUrl.add(source);
            return;
        }
        mDataSource = source;
    }

    @Override
    public void play() {
        if (isPlaying()) {
            return;
        }
        if (mDataSource == null) return;
        boolean isException = false;
        MediaPlayer mediaPlayer = null;
        mSurfaceHolder = null;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            if (mDataSource instanceof String) {
                mediaPlayer.setDataSource((String) mDataSource);
            }
            SurfaceView surfaceView = findViewWithTag("SURFACE");
            if (findViewWithTag("SURFACE") != null) {
                Log.e(TAG, "play: reset old surface tag to old_surface");
                surfaceView.setTag("OLD_SURFACE");
                cachePlayer.put("OLD_SURFACE", mMediaPlayer);
                Log.e(TAG, "play: cache player=" + mMediaPlayer);
            }

            SurfaceView second = new SurfaceView(getContext());
            SurfaceHolder surfaceHolder = second.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            second.setTag("SURFACE");
            addView(second);
            isPrepared = false;
            mSurfaceHolder = null;
            Log.e(TAG, "play: source = " + mDataSource);
            cachePlayer.put("SURFACE", mediaPlayer);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            isException = true;
        } finally {
            if (isException) {
                release(mediaPlayer);
            }
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPaused = true;
        }
    }

    @Override
    public void change(View view) {
    }

    @Override
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    @Override
    public void resume() {

    }

    private void release(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            Log.e(TAG, "release: " + mediaPlayer);
            mediaPlayer.release();
            mediaPlayer.setOnInfoListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnBufferingUpdateListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.setOnPreparedListener(null);
        }
    }

    @Override
    public void release() {
        isReleased = true;
        release(mMediaPlayer);
        removeOldSurface("OLD_SURFACE");
        removeOldSurface("SURFACE");
        removeAllViews();
        mSurfaceHolder = null;
        mCallback = null;
        cachePlayer = null;
        mMediaPlayer = null;
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return true;
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated: ");
        mSurfaceHolder = holder;
        if (mMediaPlayer != null && isPrepared) {
            mMediaPlayer.setDisplay(holder);
            if (isPaused) {
                mMediaPlayer.start();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged: ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed: holder=" + holder);
    }

    private void removeOldSurface(String tag) {
        View old = findViewWithTag(tag);
        if (old != null) {
            Log.e(TAG, "onInfo: remove old_surface " + old);
            removeView(old);
            release(cachePlayer.get(tag));
            cachePlayer.remove(tag);
            Log.e(TAG, "onInfo: cache player size=" + cachePlayer.size());
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onInfo: what" + what);
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END || what == MediaPlayer
                .MEDIA_INFO_VIDEO_RENDERING_START) {
            removeOldSurface("OLD_SURFACE");
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e(TAG, "onCompletion: ");
        if (mCallback != null)
            mCallback.onComplete();

        if (cacheUrl != null && cacheUrl.size() > 0) {
            setDataSource(cacheUrl.remove(0));
            play();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.e(TAG, "onBufferingUpdate: ");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: what=" + what + " extra=" + extra);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(TAG, "onPrepared: player=" + mp + " holder=" + mSurfaceHolder);
        mMediaPlayer = mp;
        cachePlayer.remove("SURFACE");
        isPrepared = true;
        if (mSurfaceHolder != null) {
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.start();
        }
    }

    @Override
    public void run() {
    }

    public interface ADPlayerCallback {
        void onComplete();

        void onTime(int current, int total);
    }
}
