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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    public static final String NO_CONTENTS = "noContents";
    private static final String TAG = "LastMenuRecyclerAdapter";
    private MenuGroup menuGroup;

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

    private Program playProgram;
    private SimpleDateFormat format;
    private SimpleDateFormat targetFormat;
    private Handler handler = new MyHandler(this);

    private static class MyHandler extends Handler {

        private final WeakReference<LastMenuRecyclerAdapter> mAdapter;

        public MyHandler(LastMenuRecyclerAdapter mAdapter) {
            this.mAdapter = new WeakReference<LastMenuRecyclerAdapter>(mAdapter);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MessageObj messageObj = (MessageObj) msg.obj;
            messageObj.view.requestFocus();
            messageObj.view.setBackgroundResource(messageObj.resId);
        }
    }

    public LastMenuRecyclerAdapter(Context context, List<Program> data, String playId,MenuGroup menuGroup) {
        super(context);
        this.menuGroup = menuGroup;
        Program program = null;
        for (Program p : data) {
            if (TextUtils.equals(p.getContentUUID(), playId)) {
                program = p;
            }
        }
        setData(data, program);
        setHasStableIds(true);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (0 == viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu, null);
            holder = new Holder(view);
        } else if (1 == viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_collect, null);
            holder = new CollectHolder(view);
        } else if(2 == viewType){
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_no_contents,null);
            holder = new NoContentsHolder(view);
        } else if(3 == viewType){
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_lb,parent,false);
            holder = new LbHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Program program = data.get(position);
        holder.itemView.setBackgroundResource(R.color.color_transparent);

        if (holder instanceof Holder) {
            Holder h = (Holder) holder;
            h.playing.setVisibility(View.GONE);
            h.tv.setText(program.getTitle());

            if (isCurrentPlay(program)) {
                h.playing.setVisibility(View.VISIBLE);
            }

        } else if (holder instanceof CollectHolder) {
            CollectHolder collectHolder = (CollectHolder) holder;
            if (program.isCollect()) {
                collectHolder.collect.setImageResource(R.drawable.menu_group_collect_hasfocus);
            } else {
                collectHolder.collect.setImageResource(R.drawable.menu_group_collect_unfocus);
            }
        } else if(holder instanceof NoContentsHolder){
//            NoContentsHolder noContentsHolder = (NoContentsHolder) holder;
            return;
        } else if(holder instanceof LbHolder){
            LbHolder lbHolder = (LbHolder) holder;
            lbHolder.title.setText(program.getTitle());
            lbHolder.time.setText(getTime(program.getStartTime(),program.getDuration()));
            if(isCurrentPlay(program)){
                lbHolder.playing.setVisibility(View.VISIBLE);
            } else {
                lbHolder.playing.setVisibility(View.GONE);
            }
        }

        if (isCurrentPlay(program)) {
            holder.itemView.setBackgroundResource(R.drawable.xuanhong);
            selectView = holder.itemView;
            pathView = holder.itemView;
            if (!init) {
                MessageObj messageObj = new MessageObj();
                messageObj.view = holder.itemView;
                if (isCollect(program) || holder instanceof LbHolder) {
                    messageObj.resId = R.drawable.menu_group_item_focus;
                } else {
                    messageObj.resId = R.drawable.one_focus;
                }
                Message msg = Message.obtain();
                msg.obj = messageObj;
                handler.sendMessageDelayed(msg, 50);
                init = true;
            }
        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isCollect(program) || holder instanceof LbHolder) {
                        v.setBackgroundResource(R.drawable.menu_group_item_focus);
                    } else {
                        v.setBackgroundResource(R.drawable.one_focus);
                        setSelect(holder, true);
                    }
                } else if (isCurrentPlay(program)) {
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
        if (COLLECT_ID.equals(data.get(position).getContentUUID())) {
            return 1;
        } else if(NO_CONTENTS.equals(data.get(position).getContentUUID())){
            return 2;
        } else if(Constant.CONTENTTYPE_LB.equals(data.get(position).getParent().getContentType())){
            return 3;
        }
        return super.getItemViewType(position);
    }

    public void setData(List<Program> data) {
        setData(data, null);
    }

    public void setData(List<Program> data, Program program) {
        if (data != null && data.size() > 0 && (Constant.CONTENTTYPE_LB.equals(data.get(0).getParent().getContentType())
                || Constant.CONTENTTYPE_LV.equals(data.get(0).getParent().getContentType()))
                && data.get(0).getParent().searchNodeInParent(MenuGroupPresenter2.LB_ID_COLLECT) == null
                && !data.get(0).getParent().isForbidAddCollect()) {
            Node node = data.get(0).getParent();
            addCollectDataToList(data, node);
            this.contentType = node.getContentType();
        } else {
            this.contentType = "";
        }

        if(data != null && data.size() > 0
                && Constant.CONTENTTYPE_LB.equals(data.get(0).getParent().getContentType())){
            menuGroup.addLastAdapterSpacesItem();
        }else {
            menuGroup.removeLastAdapterSpacesItem();
        }

        this.data = data;
        this.selectView = null;
        if (program != null) {
            setPlayId(program);
        } else {
            notifyDataSetChanged();
        }
    }

    private void addCollectDataToList(List<Program> data, Node node) {
        if (data == null || !(node instanceof LastNode)) {
            return;
        }

        if (COLLECT.equals(data.get(0).getTitle())) {
            return;
        }
        final Program program = new Program();
        program.setTitle("收藏");
        program.setContentUUID(COLLECT_ID);
        program.setParent(node);
        data.add(0, program);

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

    @Override
    public long getItemId(int position) {
        return data.get(position).hashCode();
    }

    public void setPlayId(Program program) {
        if (program != null) {
            playProgram = program;
            this.title = program.getTitle();
            this.init = false;
            notifyDataSetChanged();
        }
    }

    class CollectHolder extends RecyclerView.ViewHolder {
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

    class NoContentsHolder extends RecyclerView.ViewHolder {
        public ImageView playing;

        public NoContentsHolder(View itemView) {
            super(itemView);
            playing = itemView.findViewById(R.id.playing);
        }
    }

    class LbHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView time;
        public ImageView playing;
        public ImageView vip;

        public LbHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_video_name);
            time = itemView.findViewById(R.id.tv_time);
            playing = itemView.findViewById(R.id.iv_playing);
            vip = itemView.findViewById(R.id.iv_vip);
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
    private boolean isCurrentPlay(Program program) {
//        if(program != null &&program.getContentUUID()!= null && program.getContentUUID().equals(playId)
//                && program.getTitle() != null && program.getTitle().equals(title)){
//            return true;
//        }
        if (playProgram == program) {
            return true;
        }
        return false;
    }

    private class MessageObj {
        View view;
        int resId;
    }

    private boolean isCollect(Program program) {
        if ((Constant.CONTENTTYPE_LB.equals(contentType) || Constant.CONTENTTYPE_LV.equals(contentType))
                && COLLECT_ID.equals(program.getContentUUID())) {
            return true;
        }
        return false;
    }

    private String getTime(String startTime,String duration){
        StringBuilder sb = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        Calendar after = (Calendar) calendar.clone();
        if(format == null){
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        }
        if(targetFormat == null){
            targetFormat = new SimpleDateFormat("HH:mm");
        }
        try {
            Date parse = format.parse(startTime);
            calendar.setTime(parse);
            after.add(Calendar.SECOND,Integer.parseInt(duration));

            sb.append(targetFormat.format(calendar.getTime()));
            sb.append("-");
            sb.append(targetFormat.format(after.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
