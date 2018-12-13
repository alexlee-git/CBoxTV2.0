package tv.newtv.cboxtv.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import tv.newtv.cboxtv.menu.model.Node;
import tv.newtv.player.R;

/**
 * Created by TCP on 2018/4/19.
 */

public class MenuRecyclerAdapter extends BaseMenuRecyclerAdapter{
    private List<Node> data;
    private Node playNode;

    public MenuRecyclerAdapter(Context context, List<Node> data, Node node) {
        super(context);
        this.data = data;
        this.playNode = node;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
        holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Node node = data.get(position);
        final Holder h = (Holder) holder;
        h.itemView.setBackgroundResource(R.color.color_transparent);
        h.tv.setText(node.getTitle());
        if (isCurrentPlay(node)) {
            h.itemView.setBackgroundResource(R.drawable.xuanhong);
            selectView = h.itemView;
            pathView = h.itemView;
        }
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.one_focus);
                    h.tv.setSelected(true);
                    if(TextUtils.equals(MenuGroupPresenter2.LB_ID_COLLECT,node.getId())
                            && node.getChild().size() == 0){
                        Toast.makeText(context,"暂无内容,欢迎您去收藏.",Toast.LENGTH_SHORT).show();
                    }
                } else if (isCurrentPlay(node)) {
                    v.setBackgroundResource(R.drawable.xuanhong);
                    h.tv.setSelected(false);
                } else {
                    v.setBackgroundResource(R.color.color_transparent);
                    h.tv.setSelected(false);
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

    public void setData(List<Node> data, Node node) {
        this.data = data;
        this.playNode = node;
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

    public void setPlayNode(Node node){
        this.playNode = node;
        notifyDataSetChanged();
    }


    public int calculatePlayIdPosition(int defValue) {
        int result = defValue;
        for (int i = 0; i < data.size(); i++) {
            if (isCurrentPlay(data.get(i))) {
                result = i;
                break;
            }
        }
        return result;
    }

    public boolean isCurrentPlay(Node node){
        if(node == playNode){
            return true;
        }
        return false;
    }
}
