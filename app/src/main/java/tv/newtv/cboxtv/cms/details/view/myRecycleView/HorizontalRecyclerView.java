package tv.newtv.cboxtv.cms.details.view.myRecycleView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Juwan on 16/11/24.
 */

public class HorizontalRecyclerView extends RecyclerView {
    private final static String TAG = HorizontalRecyclerView.class.getSimpleName();
    private final static boolean DEBUG = true;

    private HorizontalLayoutManager mLayoutManager;

    public HorizontalRecyclerView(Context context) {
        super(context);

        initialize(context);
    }

    private void initialize(Context context) {
        mLayoutManager =  new HorizontalLayoutManager(context, HorizontalRecyclerView.HORIZONTAL, false);
//        mLayoutManager = new HorizontalLayoutManager(context, 1, HorizontalLayoutManager.HORIZONTAL, false);
        setLayoutManager(mLayoutManager);
    }

    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    public HorizontalRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize(context);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(DEBUG) {
//            Log.d(TAG, "computeScroll");
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View focusedView = null;
        int  focusedPosition;

        focusedPosition = mLayoutManager.getPosition(focused);

        if (DEBUG) {
            Log.d(TAG, "focused: " + focused + ", direction: " + direction);
            Log.d(TAG, "focusedPosition = " + focusedPosition);
            Log.d(TAG, "item count = " + getAdapter().getItemCount());
        }

        if (direction == View.FOCUS_LEFT && focusedPosition == 0) {
            return getChildAt(focusedPosition - 1);
        } else if (direction == View.FOCUS_RIGHT && focusedPosition == getAdapter().getItemCount() - 1) {
            return getChildAt(focusedPosition + 1);
        } else {
            focusedView = super.focusSearch(focused, direction);
        }

        return focusedView;
    }
}
