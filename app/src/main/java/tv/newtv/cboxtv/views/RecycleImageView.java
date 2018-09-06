package tv.newtv.cboxtv.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.utils.BitmapUtil;
import tv.newtv.cboxtv.utils.PicassoBuilder;
import tv.newtv.cboxtv.utils.UsefulBitmapFactory;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views
 * 创建事件:         14:40
 * 创建人:           weihaichao
 * 创建日期:          2018/4/11
 */
public class RecycleImageView extends AppCompatImageView {
    private Object imageUrl;
    private String mTag;
    private Callback mCallback;
    private int mPlaceHolder;
    private int error;

    private boolean isCorner = true;
    private boolean useVH = true;
    private boolean useResize = true;

    private boolean noStore = false;
    private boolean isPlay = false;
    private Paint mPaint;
    private Bitmap bitmap;
    private PaintFlagsDrawFilter mPaintFilter;
    private boolean isChanged;

    public RecycleImageView(Context context) {
        super(context);
    }

    public RecycleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecycleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void disposs() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        mPaintFilter = null;
        mPaint = null;

//        BitmapDrawable bitmapDrawable = (BitmapDrawable) getDrawable();
//        if (bitmapDrawable != null) {
//            Bitmap bitmap = bitmapDrawable.getBitmap();
//            if (bitmap != null && !bitmap.isRecycled()) {
//                bitmap.recycle();
//            }
//            bitmap = null;
//        }

    }

    public void recycle() {
        disposs();
        if (imageUrl instanceof String) {
            PicassoBuilder.getBuilder().clearImageMemory((String) imageUrl);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //setImageDrawable(null);
    }

    public RecycleImageView withCallback(Callback callback) {
        this.mCallback = callback;
        return this;
    }

    public RecycleImageView NoStore(boolean store) {
        this.noStore = store;
        return this;
    }

    public RecycleImageView placeHolder(int place) {
        this.mPlaceHolder = place;
        return this;
    }

    public RecycleImageView errorHolder(int place) {
        this.error = place;
        return this;
    }

    public RecycleImageView useResize(boolean use) {
        this.useResize = use;
        return this;
    }

    public RecycleImageView hasCorner(boolean corner) {
        this.isCorner = corner;
        return this;
    }

    public RecycleImageView Tag(String tag) {
        this.mTag = tag;
        return this;
    }

    public void load(Object url) {
        imageUrl = url;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isAttachedToWindow()) {
                loadImage();
            }
        } else {
            loadImage();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        loadImage();
    }

    private void init() {

        int width = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen.width_90px));
        int height = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                .height_30px));
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        bitmap = BitmapUtil.zoomImg(UsefulBitmapFactory.findBitmap(getContext(), R.drawable
                .playing), width, height);

        mPaintFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG);
    }

    public void setIsPlaying(boolean value) {
        if (value) {
            init();
            isPlay = true;
            isChanged = true;
            postInvalidate();
        } else {
            disposs();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isChanged) {
            if (isPlay && bitmap != null && !bitmap.isRecycled()) {
                canvas.setDrawFilter(mPaintFilter);
                int left = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                        .width_10px));
                int top = (int) (getContext().getResources().getDimensionPixelOffset(R.dimen
                        .height_10px));
                canvas.drawBitmap(bitmap, left, top, mPaint);
            }
        }
    }

    private void loadImage() {
        RequestCreator requestCreator = null;

        if (imageUrl instanceof String) {
            String url = (String) imageUrl;
            if (TextUtils.isEmpty(url)) {
                return;
            }

            if (url.startsWith("file:")) {
                requestCreator = Picasso.get().load(Uri.parse(url));
            } else if (url.startsWith("http")) {
                requestCreator = Picasso.get().load(url);
                requestCreator.stableKey(url);
            }
        } else if (imageUrl instanceof Integer) {
            int url = (int) imageUrl;
            setImageResource(url);
            return;
        }
        if (requestCreator != null) {
            requestCreator = requestCreator
                    .priority(Picasso.Priority.HIGH)
                    .config(Bitmap.Config.RGB_565);
            if (isCorner) {
                requestCreator = requestCreator.transform(new PosterCircleTransform(getContext(),
                        4));
            }
            if (noStore) {
                requestCreator = requestCreator.memoryPolicy(MemoryPolicy.NO_STORE);
            }

            if (mPlaceHolder != -1 && mPlaceHolder != 0) {
                requestCreator = requestCreator.placeholder(mPlaceHolder);
            }
            if (error != -1 && error != 0) {
                requestCreator = requestCreator.error(error);
            } else {
                requestCreator = requestCreator.error((mPlaceHolder != -1 && mPlaceHolder != 0) ?
                        mPlaceHolder : R
                        .drawable.focus_1392_162);
            }

            if (!TextUtils.isEmpty(mTag)) {
                requestCreator = requestCreator.tag(!TextUtils.isEmpty(mTag) ? mTag : "");
            }

            if (useResize) {
                requestCreator = requestCreator.fit();
            }

//            if (useResize && getLayoutParams() != null && getLayoutParams().width > 0 &&
//                    getLayoutParams().height > 0) {
//                requestCreator = requestCreator.resize(getLayoutParams().width, getLayoutParams()
//                        .height).centerCrop();
//            }
            if (mCallback != null) {
                requestCreator.into(this, mCallback);
            } else {
                requestCreator.into(this);
            }
        }
    }
}
