package tv.newtv.cboxtv.cms.details.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.util.LogUtils;

import tv.newtv.MultipleClickListener;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;

/**
 * Created by gaoleichao on 2018/4/13.
 */
public class ProgrameSeriesAdapter extends BaseRecyclerAdapter<SubContent,
        ProgrameSeriesAdapter.ProgrameSeriesViewHolder> {

    private Context context;
    private OnRecycleItemClickListener<SubContent> listener;
    private int mCurrenPage;
    private int playerPosition;

    private boolean isFocus = false;

    public ProgrameSeriesAdapter(Context context, OnRecycleItemClickListener<SubContent> listener) {
        this.context = context;
        this.listener = listener;
    }

    public void destroy() {
        listener = null;
        context = null;
    }

    public void setPlayerPosition(int playerPosition, boolean isFocus) {
        this.playerPosition = playerPosition;
        this.isFocus = isFocus;
//        notifyDataSetChanged();
    }

    public void setCurrenPage(int mCurrenPage) {
        this.mCurrenPage = mCurrenPage;

    }

    @Override
    public void onBindViewHolder(ProgrameSeriesViewHolder holder, int position) {
        int i = position + (mCurrenPage * 30);
        SubContent entity = mList.get(position);
        holder.nameTv.setText(entity.getPeriods());
        if (i == playerPosition) {
            LogUtils.i("ProgrameSeriesAdapter", "playerPosition=" + playerPosition);
            LogUtils.i("ProgrameSeriesAdapter", "i=" + i);
            holder.nameTv.setTextColor(ContextCompat.getColor(context, R.color.color_62c0eb));
//            if (isFocus) {
//                holder.focusRl.requestFocus();
//                isFocus = false;
//            }
        } else {
            holder.nameTv.setTextColor(ContextCompat.getColor(context, R.color.detail_tvcolor));
        }
    }

    @Override
    public ProgrameSeriesAdapter.ProgrameSeriesViewHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        ProgrameSeriesViewHolder viewHolder = new ProgrameSeriesViewHolder(LayoutInflater.from
                (context).inflate(R.layout.layout_programe_series, null));
        return viewHolder;
    }

    class ProgrameSeriesViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout focusRl;
        private TextView nameTv;

        public ProgrameSeriesViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.tv_series);
            focusRl = (RelativeLayout) itemView.findViewById(R.id.rl_focus);
            focusRl.setOnClickListener(new MultipleClickListener() {
                @Override
                protected void onMultipleClick(View view) {
                    listener.onItemClick(view, getAdapterPosition(), getItem(getAdapterPosition()));
                }
            });
            focusRl.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            if (getAdapterPosition() == (getItemCount() - 1)) {
                                return true;
                            }
                        }
                        return false;
                    }

                    return false;
                }
            });
            focusRl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        focusRl.setBackgroundResource(R.drawable.icon_details_series_focus);
                    } else {
                        focusRl.setBackgroundResource(R.color.color_transparent);
                    }
                }
            });
        }
    }

}
