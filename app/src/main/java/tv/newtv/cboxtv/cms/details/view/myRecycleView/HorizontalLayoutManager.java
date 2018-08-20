package tv.newtv.cboxtv.cms.details.view.myRecycleView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Juwan on 16/11/24.
 */

public class HorizontalLayoutManager extends LinearLayoutManager {

    private final static String TAG = HorizontalLayoutManager.class.getSimpleName();
    private final static boolean DEBUG = true;

    public HorizontalLayoutManager(Context context) {
        super(context);
    }

    public HorizontalLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HorizontalLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }


    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    @Override
    public View onFocusSearchFailed(View focused, int focusDirection,
                                    RecyclerView.Recycler recycler, RecyclerView.State state) {
        // Need to be called in order to layout new row/column
        View nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state);

        if (nextFocus == null) {
            return focused;
        }


        return nextFocus;
    }
}
