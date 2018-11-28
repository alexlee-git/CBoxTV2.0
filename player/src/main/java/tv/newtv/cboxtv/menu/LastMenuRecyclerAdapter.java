package tv.newtv.cboxtv.menu;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;

import java.lang.ref.WeakReference;
import java.util.List;

import tv.newtv.cboxtv.menu.model.LastNode;
import tv.newtv.cboxtv.menu.model.Node;
import tv.newtv.cboxtv.menu.model.Program;
import tv.newtv.player.R;

/**
 * Created by TCP on 2018/4/19.
 */

public class LastMenuRecyclerAdapter extends BaseMenuRecyclerAdapter<RecyclerView.ViewHolder> {
    private static final String COLLECT = "收藏";
    public static final String COLLECT_ID = "collect";
    private static final String TAG = "LastMenuRecyclerAdapter";

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

    private String contentType;

    private Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler{

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
        setData(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if(0 == viewType){
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
            holder = new Holder(view);
        }else if(1 == viewType){
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_collect,null);
            holder = new CollectHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Program program = data.get(position);
        holder.itemView.setBackgroundResource(R.color.color_transparent);

        if(holder instanceof Holder){
            Holder h = (Holder) holder;
            h.playing.setVisibility(View.GONE);
            h.tv.setText(program.getTitle());

            if(isCurrentPlay(program)){
                h.playing.setVisibility(View.VISIBLE);
            }

        }else if(holder instanceof CollectHolder){
            CollectHolder collectHolder = (CollectHolder) holder;
            if(program.isCollect()){
                collectHolder.collect.setImageResource(R.drawable.menu_group_collect_hasfocus);
            }else {
                collectHolder.collect.setImageResource(R.drawable.menu_group_collect_unfocus);
            }
        }

        if (isCurrentPlay(program)) {
            holder.itemView.setBackgroundResource(R.drawable.xuanhong);
            selectView = holder.itemView;
            pathView = holder.itemView;
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
                    if(Constant.CONTENTTYPE_LB.equals(contentType) && isCurrentPlay(program)){
                        v.setBackgroundResource(R.drawable.menu_group_item_focus);
                    }else {
                        v.setBackgroundResource(R.drawable.one_focus);
                    }
                } else if (isCurrentPlay(program)) {
                    v.setBackgroundResource(R.drawable.xuanhong);
                } else {
                    v.setBackgroundResource(R.color.color_transparent);
                }
            }
        });

        if(position == 0){
            firstPositionView = holder.itemView;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(COLLECT.equals(data.get(position).getTitle())){
            return super.getItemViewType(position) + 1;
        }
        return super.getItemViewType(position);
    }

    public void setData(List<Program> data){
        setData(data,null);
    }

    public void setData(List<Program> data,Program program){
        if(data != null && data.size() > 0 && Constant.CONTENTTYPE_LB.equals(data.get(0).getParent().getContentType())){
            Node node = data.get(0).getParent();
            addCollectDataToList(data,node);
            this.contentType = node.getContentType();
        }else {
            this.contentType = "";
        }
        this.data = data;
        this.selectView = null;
        if(program != null){
            setPlayId(program);
        }else {
            notifyDataSetChanged();
        }
    }

    private void addCollectDataToList(List<Program> data,Node node){
        if(data == null || !(node instanceof LastNode)){
            return;
        }

        if(COLLECT.equals(data.get(0).getTitle())){
            return;
        }
        final Program program = new Program();
        program.setTitle("收藏");
        program.setContentUUID(COLLECT_ID);
        program.setParent(node);
        data.add(0,program);

        LastNode lastNode = (LastNode) node;

        DataSupport.search(DBConfig.LB_COLLECT_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, lastNode.contentUUID)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(new DBCallback<String>() {
                    @Override
                    public void onResult(int code, String result) {
                        if (code == 0) {
                            if (!TextUtils.isEmpty(result)) {
                                program.setCollect(true);
                            } else {
                                program.setCollect(false);
                            }
                            notifyDataSetChanged();
                        }
                    }
                }).excute();
    }

    public void setPlayId(Program program){
        if(program != null){
            this.playId = program.getContentUUID();
            this.title = program.getTitle();
            this.init = false;
            notifyDataSetChanged();
        }
    }

    class CollectHolder extends RecyclerView.ViewHolder{
        public TextView tv;
        public ImageView collect;

        public CollectHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_video_name);
            collect = itemView.findViewById(R.id.iv_collect);
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
