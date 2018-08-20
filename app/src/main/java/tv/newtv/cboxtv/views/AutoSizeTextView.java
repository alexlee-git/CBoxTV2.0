package tv.newtv.cboxtv.views;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.DisplayUtils;

/**
 * <pre>
 *   name:Wei JiaQi
 *   time:2018/5/20
 *   desc:
 *   version:1.0
 * </pre>
 */


public class AutoSizeTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = AutoSizeTextView.class.getSimpleName();

    private int space;


    public AutoSizeTextView(Context context) {
        this(context,null);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AutoSizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }
    
    private void initialize(){
        space = getResources().getDimensionPixelOffset(R.dimen.width_66px);
        setPadding(DisplayUtils.translate(12, DisplayUtils
                .SCALE_TYPE_WIDTH), 0, 0, 0);
        setSingleLine();
        setLines(1);
        setTextColor(Color.parseColor("#ededed"));
        setTextSize(DisplayUtils.translate(12, DisplayUtils
                .SCALE_TYPE_HEIGHT));
        setMarqueeRepeatLimit(-1);
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setIncludeFontPadding(false);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ViewGroup view = (ViewGroup) getParent();
        if(view != null){
            RecycleImageView recycleImageView = getRecycleImageView(view);
            if(recycleImageView != null){
                Log.d(TAG,"RecycleImageView width="+recycleImageView.getWidth()+" height="+recycleImageView.getHeight());
                setMeasuredDimension(recycleImageView.getWidth(),getResources().getDimensionPixelSize(R.dimen.height_40px));
                requestLayout();
                return;
            }
            setMeasuredDimension(view.getMeasuredWidth(),getResources().getDimensionPixelSize(R.dimen.height_40px));
            requestLayout();
            return;
        }
        setMeasuredDimension(0,0);
    }

    private RecycleImageView getRecycleImageView(ViewGroup view){
        int size = view.getChildCount();
        for(int index = 0;index<size;index++){
            View targetView = view.getChildAt(index);
            if(targetView instanceof RecycleImageView){
                return (RecycleImageView) targetView;
            }else if(targetView instanceof ViewGroup){
                RecycleImageView resultView = getRecycleImageView((ViewGroup) targetView);
                if(resultView != null){
                    return resultView;
                }
            }
        }
        return null;
    }
}
