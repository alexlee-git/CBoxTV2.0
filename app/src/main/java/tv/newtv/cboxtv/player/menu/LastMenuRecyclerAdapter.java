package tv.newtv.cboxtv.player.menu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.menu.model.Program;

/**
 * Created by TCP on 2018/4/19.
 */

public class LastMenuRecyclerAdapter extends BaseMenuRecyclerAdapter<RecyclerView.ViewHolder> {

    private List<Program> data;
    /**
     * RecyclerView显示时当前正在播放的视频对应条目需要获取焦点
     * 该变量控制与playId对应的条目是否可以获得焦点，获得焦点后自动设置为true
     */
    private boolean init = false;
    /**
     * 当前正在播放的节目标题
     */
    private String title = "";

    private Handler handler = new MyHandler(this);

    private static class MyHandler extends android.os.Handler{

        private final WeakReference<LastMenuRecyclerAdapter> mAdapter;

        public MyHandler(LastMenuRecyclerAdapter mAdapter) {
            this.mAdapter = new WeakReference<LastMenuRecyclerAdapter>(mAdapter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            View view = (View) msg.obj;
            view.requestFocus();
            view.setBackgroundResource(R.drawable.one_focus);
        }
    }

    public LastMenuRecyclerAdapter(Context context, List<Program> data, String playId) {
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
        final Program program = data.get(position);
        Holder h = (Holder) holder;
        h.itemView.setBackgroundResource(R.color.color_transparent);
        h.playing.setVisibility(View.GONE);
        h.tv.setText(program.getTitle());

        if (isCurrentPlay(program)) {
            holder.itemView.setBackgroundResource(R.drawable.xuanhong);
            h.playing.setVisibility(View.VISIBLE);
            selectView = h.itemView;
            pathView = h.itemView;
            if(!init){
                Message msg = Message.obtain();
                msg.obj = holder.itemView;
                handler.sendMessageDelayed(msg, 50);
                init = true;
            }
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.one_focus);
                } else if (isCurrentPlay(program)) {
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

    public void setData(List<Program> data){
        this.data = data;
        this.selectView = null;
        notifyDataSetChanged();
    }

    public void setData(List<Program> data,Program program){
        this.data = data;
        this.selectView = null;
        setPlayId(program);
    }

    public void setPlayId(Program program){
        if(program != null){
            this.playId = program.getContentUUID();
            this.title = program.getTitle();
            this.init = false;
            notifyDataSetChanged();
        }
    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ImageView playing;

        public Holder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_video_name);
            playing = itemView.findViewById(R.id.playing);
        }
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

    public boolean isInit() {
        return init;
    }

    /**
     * 正在播放的内容是否是当前item
     * @param program 当前item对应的数据
     * @return
     */
    private boolean isCurrentPlay(Program program){
        if(program != null &&program.getContentUUID()!= null && program.getContentUUID().equals(playId)
                && program.getTitle() != null && program.getTitle().equals(title)){
            return true;
        }
        return false;
    }
}
