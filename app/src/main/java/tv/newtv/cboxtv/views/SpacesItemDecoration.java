package tv.newtv.cboxtv.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2018/4/19.
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration{

    private int space;
    private int wtop;

    public SpacesItemDecoration(int space) {
        this.space = space;
//            this.wtop = wtop;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = space;
//            outRect.top = space;
    }

}
