package tv.newtv.cboxtv.player.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wangkun on 2018/1/19.
 */

public class PlayerRecylerViewHolder<T> extends RecyclerView.ViewHolder {
    private static final String TAG = "PlayerRecylerViewHolder";
    public PlayerRecylerViewHolder(View itemView) {
        super(itemView);

    }

    public View getViewById(int id){
        return this.itemView.findViewById(id);
    }
}
