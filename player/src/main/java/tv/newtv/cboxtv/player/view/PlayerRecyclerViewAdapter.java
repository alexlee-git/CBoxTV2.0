package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by wangkun on 2018/1/19.
 */

public abstract class PlayerRecyclerViewAdapter<T> extends RecyclerView.Adapter<PlayerRecylerViewHolder> {
    private static final String TAG = "PlayerRecyclerViewAdapter";
    private int mLayoutId;
    private List<T> mDatas;
    private LayoutInflater mLayoutInflater;
    public PlayerRecyclerViewAdapter(Context context, int layoutId, List<T> datas) {
        mLayoutId = layoutId;
        mDatas = datas;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public PlayerRecylerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(mLayoutId,parent,false);
        return new PlayerRecylerViewHolder(view);
    }

    @Override
    public abstract void onBindViewHolder(PlayerRecylerViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mDatas==null?0:mDatas.size();
    }
}
