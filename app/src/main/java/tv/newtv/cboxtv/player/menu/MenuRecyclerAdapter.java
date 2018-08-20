package tv.newtv.cboxtv.player.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.menu.model.Node;

/**
 * Created by TCP on 2018/4/19.
 */

public class MenuRecyclerAdapter extends BaseMenuRecyclerAdapter{
    private List<Node> data;

    public MenuRecyclerAdapter(Context context, List<Node> data, String playId) {
        super(context,playId);
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
        holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Node node = data.get(position);
        Holder h = (Holder) holder;
        h.itemView.setBackgroundResource(R.color.color_transparent);
        h.tv.setText(node.getTitle());
        if (node.getId().equals(playId)) {
            h.itemView.setBackgroundResource(R.drawable.xuanhong);
            selectView = h.itemView;
            pathView = h.itemView;
        }
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.one_focus);
                } else if (node.getId().equals(playId)) {
                    v.setBackgroundResource(R.drawable.xuanhong);
                } else {
                    v.setBackgroundResource(R.color.color_transparent);
                }
            }
        });

        if(position == 0){
            firstPositionView = h.itemView;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Node getItem(int position) {
        return data.get(position);
    }

    public void setData(List<Node> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setData(List<Node> data, String playId) {
        this.data = data;
        this.playId = playId;
        this.selectView = null;
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView tv;

        public Holder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_video_name);
        }
    }


    public void setPlayId(String playId) {
        this.playId = playId;
        notifyDataSetChanged();
    }


    public int calculatePlayIdPosition(int defValue) {
        int result = defValue;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getId().equals(playId)) {
                result = i;
                break;
            }
        }
        return result;
    }

}
