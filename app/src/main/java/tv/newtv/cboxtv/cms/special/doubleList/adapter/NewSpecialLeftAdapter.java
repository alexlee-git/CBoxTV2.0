package tv.newtv.cboxtv.cms.special.doubleList.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;

public class NewSpecialLeftAdapter extends RecyclerView.Adapter<NewSpecialLeftAdapter.LeftHolder> {
    private static final String TAG = NewSpecialLeftAdapter.class.getSimpleName();
    private static final String SPECIAL_ONE = "special_one";
    private static final String SPECIAL_TWO = "special_two";
    protected final Context context;
    private OnFoucesDataChangeListener mOnFoucesDataChangeListener;
    private LeftHolder mLeftHolder;
    private List<ProgramInfo> mNewSpecialDatas;
    private String mDefaultFocus;
    private int mDefaultFocusP;

    public NewSpecialLeftAdapter(Context context) {
        this.context = context;
    }

    public NewSpecialLeftAdapter(Context context, List<ProgramInfo> objectList) {
        this.mNewSpecialDatas = objectList;
        this.context = context;
    }

    public NewSpecialLeftAdapter(Context context, List<ProgramInfo> objectList, int mDefaultP) {
        this.mNewSpecialDatas = objectList;
        this.context = context;
        this.mDefaultFocusP = mDefaultP;
    }

    @Override
    public LeftHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeftHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_left_special, parent, false));
    }

    @Override
    public void onBindViewHolder(final LeftHolder holder, final int position) {
        mLeftHolder = holder;
        holder.topicItem.setText(position+ "  --  "+mNewSpecialDatas.get(position).getSubTitle());
        holder.topicContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.topicItem.setSelected(true);
                    holder.topicItem.setBackgroundResource(R.drawable.special_list_focus);
                    if (null != mOnFoucesDataChangeListener) {
                        mOnFoucesDataChangeListener.onFoucesDataChangeListener(mNewSpecialDatas.get(position).getContentUUID(), position);
                    }
                } else {
                    holder.topicItem.setSelected(false);
                    holder.topicItem.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewSpecialDatas.size();
    }

    public void refreshData(List<ProgramInfo> value, String defaultFocus) {
        mNewSpecialDatas = value;
        mDefaultFocus = defaultFocus;
        notifyDataSetChanged();
    }

    class LeftHolder extends RecyclerView.ViewHolder {
        public TextView topicItem;
        public View topicContainer;

        public LeftHolder(View itemView) {
            super(itemView);
            topicContainer = itemView.findViewById(R.id.topic_container);
            topicItem = itemView.findViewById(R.id.topic_item);
        }
    }

    public void setOnFoucesDataChangeListener(OnFoucesDataChangeListener onFoucesDataChangeListener) {
        this.mOnFoucesDataChangeListener = onFoucesDataChangeListener;
    }

    public interface OnFoucesDataChangeListener {
        void onFoucesDataChangeListener(String contentId, int position);
    }

}
