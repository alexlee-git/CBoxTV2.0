package tv.newtv.cboxtv.menu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.util.PlayerTimeUtils;

import java.util.List;

import tv.newtv.cboxtv.menu.model.LastNode;
import tv.newtv.cboxtv.menu.model.Node;
import tv.newtv.cboxtv.menu.model.Program;
import tv.newtv.cboxtv.player.util.LbUtils;
import tv.newtv.player.R;

/**
 * Created by TCP on 2018/4/19.
 */

public class MenuRecyclerAdapter extends BaseMenuRecyclerAdapter {
    private List<Node> data;
    private Node playNode;
    private MenuGroup menuGroup;

    public MenuRecyclerAdapter(Context context, List<Node> data, Node node, MenuGroup menuGroup) {
        super(context);
        this.data = data;
        this.playNode = node;
        this.menuGroup = menuGroup;
        notifyItemDecoration();
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == 0) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
            holder = new Holder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_lb_number, parent, false);
            holder = new LbHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Node node = data.get(position);
        holder.itemView.setBackgroundResource(R.color.color_transparent);
        if(position == 0){
            menuGroup.setRecyclerViewSpacesItem(node);
        }

        if (holder instanceof Holder) {
            final Holder h = (Holder) holder;
            h.tv.setText(node.getTitle());
        } else if (holder instanceof LbHolder) {
            LbHolder lbHolder = (LbHolder) holder;
            lbHolder.title.setText(node.getTitle());
            if (node instanceof LastNode) {
                LastNode lastNode = (LastNode) node;
                lbHolder.playTitle.setText("");
                lbHolder.lbNumber.setText(lastNode.alternateNumber);
                if (node.getPrograms().size() > 0) {
                    List<Program> programs = node.getPrograms();
                    lbHolder.playTitle.setText(programs.get(LbUtils.binarySearch(programs,0)).getTitle());
                } else if (!node.isRequest() || !node.isRequesting()) {
                    menuGroup.requestData(node);
                }
            }
        }
        if (isCurrentPlay(node)) {
            holder.itemView.setBackgroundResource(R.drawable.xuanhong);
            selectView = holder.itemView;
            pathView = holder.itemView;
        }
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.one_focus);
                    setSelect(holder, true);
                    if (TextUtils.equals(MenuGroupPresenter2.LB_ID_COLLECT, node.getId())
                            && node.getChild().size() == 0) {
                        Toast.makeText(context, "暂无内容,欢迎您去收藏.", Toast.LENGTH_SHORT).show();
                    }
                } else if (isCurrentPlay(node)) {
                    v.setBackgroundResource(R.drawable.xuanhong);
                    setSelect(holder, false);
                } else {
                    v.setBackgroundResource(R.color.color_transparent);
                    setSelect(holder, false);
                }
            }
        });

        if (position == 0) {
            firstPositionView = holder.itemView;
        }
    }

    private void setSelect(RecyclerView.ViewHolder holder, boolean select) {
        if (holder instanceof Holder) {
            Holder h = (Holder) holder;
            h.tv.setSelected(select);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (Constant.CONTENTTYPE_LB.equals(data.get(position).getContentType())) {
            return 1;
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).hashCode();
    }

    public Node getItem(int position) {
        return data.get(position);
    }

    public void setData(List<Node> data) {
        this.data = data;
        notifyItemDecoration();
        notifyDataSetChanged();
    }

    public void setData(List<Node> data, Node node) {
        this.data = data;
        this.playNode = node;
        this.selectView = null;
        notifyItemDecoration();
        notifyDataSetChanged();
    }

    private void notifyItemDecoration(){
        if(data != null && data.size() > 0){
            menuGroup.setRecyclerViewSpacesItem(data.get(0));
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView tv;

        public Holder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_video_name);
        }
    }

    class LbHolder extends RecyclerView.ViewHolder {
        public TextView lbNumber;
        public TextView title;
        public TextView playTitle;

        public LbHolder(View itemView) {
            super(itemView);
            lbNumber = itemView.findViewById(R.id.tv_lb_number);
            title = itemView.findViewById(R.id.tv_video_name);
            playTitle = itemView.findViewById(R.id.tv_play_title);
        }
    }

    public void setPlayNode(Node node) {
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

    public boolean isCurrentPlay(Node node) {
        if (node == playNode) {
            return true;
        }
        return false;
    }
}
